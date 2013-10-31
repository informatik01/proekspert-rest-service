package ee.proekspert.webapp.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import ee.proekspert.webapp.model.Person;
import ee.proekspert.webapp.model.ServiceMessage;
import ee.proekspert.webapp.util.PropertiesUtil;

public class DataService {
	
	/*
	 * IMPLEMENTATION NOTE
	 * Using DEBUG level to log network request not to pollute file system
	 * with excessive data (service makes a request every 5 seconds).
	 * It is known in advance that some legacy interface are unavailable.
	 */
	
	private static final Logger logger = Logger.getLogger(DataService.class);
	
	private static final String DATE_FORMAT = "yyyyMMdd";
	private static final String TIME_FORMAT = "HHmm";
	private static final String DATE_TIME_FORMAT = "yyyyMMddHHmm";
	
	private static final String CONFIG_FILE = "./config.properties";
	
	private static final int NTHREADS = 10;
	
	private static final String[] RESOURCES;
	private static final int RESOURCES_COUNT;
	static {
		PropertiesUtil propertiesUtil = new PropertiesUtil(CONFIG_FILE);
		RESOURCES = propertiesUtil.getProperties("legacy.interface.resources");
		RESOURCES_COUNT = RESOURCES.length;
	}
	
	private static final ReentrantReadWriteLock counterLock = new ReentrantReadWriteLock();
	private static final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	
	private static ServiceMessage seviceMessage;
	
	private ExecutorService threadPool = Executors.newFixedThreadPool(NTHREADS);
	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private int PERIOD = 5;
	private ScheduledFuture<?> future;
	
	private AtomicInteger counter = new AtomicInteger();
	
	private boolean started;

	public DataService(boolean start) {
		if (start) {
			startup();
			this.started = true;
		}
	}
	
	/**
	 * Starts up the data service sequentially.
	 * @see java.util.concurrent.ScheduledExecutorService#scheduleAtFixedRate()
	 */
	public void startup() {
		if (started) {
			return;
		}
		
		Runnable task = new Runnable() {
			@Override
			public void run() {
				processData();
			}
		};
		this.future = scheduler.scheduleAtFixedRate(task, 0, PERIOD, TimeUnit.SECONDS);
		started = true;
		logger.info("Data Service started successfully in sequential mode.");
	}
	
	/**
	 * Starts up the data service in a concurrent way. It is useful when scheduled tasks
	 * may overlap due to the possible network delays.
	 * <p>Each thread executing the task can run concurrently with other threads,
	 * executing the same task. This is different from just scheduling task execution
	 * using {java.util.concurrent.ScheduledExecutorService#scheduleAtFixedRate()},
	 * which according to the API documentation does not provide concurrent execution of tasks.
	 */
	public void startupConcurrently() {
		if (started) {
			return;
		}
		
		final Runnable serviceReader = new Runnable() {
			@Override
			public void run() {
				threadPool.execute(new Runnable() {
					@Override
					public void run() {
						processData();
					}
				});
			}
		};
		future = scheduler.scheduleAtFixedRate(serviceReader, 0, PERIOD, TimeUnit.SECONDS);
		started = true;
		logger.info("Data Service started successfully in concurrent mode.");
	}
	
	/**
	 * Cancels the scheduled tasks.
	 */
	public void stop() {
		future.cancel(true);
		started = false;
		logger.info("Data Service was stopped successfully.");
	}
	
	/**
	 * Cancels the scheduled tasks and shuts down the related scheduler.
	 */
	public void shutdown() {
		stop();
		scheduler.shutdown();
		try {
			if (!scheduler.awaitTermination(15, TimeUnit.SECONDS)) {
				scheduler.shutdownNow();
			}
		} catch (InterruptedException e) {
			scheduler.shutdownNow();
			Thread.currentThread().interrupt();
		}
		started = false;
		logger.info("Data Service was shut down successfully.");
	}
	
	/**
	 * Read-only static method for getting service messages.
	 * @return	latest service message. If service was not started returns <tt>null</tt>.
	 */
	public static ServiceMessage getSeviceMessage() {
		readWriteLock.readLock().lock();
		ServiceMessage latestServiceMessage = null;
		try {
			latestServiceMessage = seviceMessage;
		} finally {
			readWriteLock.readLock().unlock();
		}
		
		return latestServiceMessage;
	}

	private void setServiceMessage(ServiceMessage seviceMessage) {
		readWriteLock.writeLock().lock();
		try {
			DataService.seviceMessage = seviceMessage;
		} finally {
			readWriteLock.writeLock().unlock();
		}
	}
	
	private void resetCounter() {
		counterLock.writeLock().lock();
		try {
			if (counter.get() > RESOURCES_COUNT) {
				counter.set(0);
			}
		} finally {
			counterLock.writeLock().unlock();
		}
	}
	
	/*
	 * Main helper method that organizes legacy service consumption
	 * and storage of the fetched data. According to the current
	 * specification no buffering of previously fetched data is required.
	 */
	private void processData() {
		int index = RESOURCES_COUNT;
		while ((index = counter.getAndIncrement()) >= RESOURCES_COUNT) {
			resetCounter();
		}
		
		String rawServiceData = "";
		try {
			rawServiceData = fetchRawServiceData(RESOURCES[index]);
		} catch (IOException e) {
			// Could not fetch service data, log and return
			logger.debug(e);
			return;
		}
		ServiceMessage parsedData = parseRawServiceData(rawServiceData);
		if (parsedData != null) {
			parsedData.setId(index);
			setServiceMessage(parsedData);
		}
	}
	
	private Date parseDate(String dateString, String dateFormat) {
		DateFormat inputDateFormat = new SimpleDateFormat(dateFormat);
		inputDateFormat.setLenient(false);	// enabling strict parsing
		
		Date date = null;
		try {
			date = inputDateFormat.parse(dateString);
		} catch (ParseException e) {
			logger.error("Error parsing date string: " + dateString, e);
		}
		
		return date;
	}
	
	
	private LocalTime parseTime(String timeString, String timeFormat) {
		DateTimeFormatter dtf = DateTimeFormat.forPattern(TIME_FORMAT);
		
		LocalTime time = null;
		try {
			time = dtf.parseLocalTime(timeString);
		} catch (UnsupportedOperationException | IllegalArgumentException e) {
			logger.error("Error parsing time string: " + timeString, e);
		}
		
		return time;
	}
	
	/*
	 * Helper method that accesses legacy network service
	 * and retrieves data, if available.
	 */
	
	private String fetchRawServiceData(String resource) throws IOException {
		BufferedReader reader = null;
		String serviceLine = "";
		try {
			URL url = new URL(resource);
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			
			// According to the legacy service specification, it MUST be one line
			serviceLine = reader.readLine();
		} catch (IOException e) {
			/* 
			 * Could not read from the resource:
			 *     either the resource is not available,
			 *     or URL is invalid,
			 *     or something else.
			 * Continue with the next resource.
			 */
			logger.debug("Could not read service data.");
			throw e;	// let the caller handle the exception
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		
		return serviceLine;
	}
	
	/*
	 * Helper method that parses raw service data according to the specification.
	 */
	
	private ServiceMessage parseRawServiceData(String data) {
		if (data == null) {
			throw new IllegalArgumentException("Null argument passed to parseMessageString()");
		}
		
		ServiceMessage serviceMessage = new ServiceMessage();
		int messageLength = data.length();
		
		boolean active = false;							// 1 symbol ('A' active / 'P' passive)
		String servicePhoneNumber = "";					// 20 symbols
		boolean xlService = false;						// 1 symbol ('J' yes / 'E' no)
		String serviceLanguage = "";					// 1 symbol (E = Estonian, I = English)
		String xlServiceLanguage = "";					// 1 symbol (E = Estonian, I = English)
		Date serviceEndDate = null;						// 8 symbols, format YYYYMMDD; includes service end time: 4 symbols, format ttmm
		LocalTime xlServiceActivationTime = null;		// 4 symbols, format ttmm
		LocalTime xlServiceEndTime = null;;				// 4 symbols, format ttmm
		boolean overrideListInUse = false;      		// 1 symbol ('K' in use / 'E' not in use)
		List<Person> persons = new ArrayList<>();		// phone numbers (15 symbols * 8 = 120 symbols),
														// names (20 symbols * 8 = 160 symbols)
		
		// Extracting service activity data (1 symbol; index 0)
		if (messageLength > 0) {
			char activitySymbol = data.charAt(0);
			if (activitySymbol == 'A') {
				active = true;
			} else if (activitySymbol == 'P') {
				active = false;
			}
			serviceMessage.setActive(active);
		}
		
		// Extracting phone number (20 symbols; index range 1-20)
		if (20 < messageLength) {
			servicePhoneNumber = data.substring(1, 21);	// last index is exclusive
			serviceMessage.setServicePhoneNumber(servicePhoneNumber);
		}
		
		// Extracting XL-Additional service activity data (1 symbol; index 21)
		if (21 < messageLength) {
			char xlServiceSymbol = data.charAt(21);
			if (xlServiceSymbol == 'J') {
				xlService = true;
			} else if (xlServiceSymbol == 'E') {
				xlService = false;
			}
			
			serviceMessage.setXlService(xlService);
		}
		
		// Extracting service language (1 symbol; index 22)
		if (22 < messageLength) {
			char serviceLanguageSymbol = data.charAt(22);
			if (serviceLanguageSymbol == 'E') {
				serviceLanguage = "Estonian";
			} else if (serviceLanguageSymbol == 'I') {
				serviceLanguage = "English";
			} else {
				serviceLanguage = "unspecified";
			}
			
			serviceMessage.setServiceLanguage(serviceLanguage);
		}
		
		// Extracting XL-Additional service language (1 symbol; index 23)
		if (23 < messageLength) {
			char xlServiceLanguageSymbol = data.charAt(23);
			if (xlServiceLanguageSymbol == 'E') {
				xlServiceLanguage = "Estonian";
			} else if (xlServiceLanguageSymbol == 'I') {
				xlServiceLanguage = "English";
			} else {
				xlServiceLanguage = "unspecified";
			}
			
			serviceMessage.setXlServiceLanguage(xlServiceLanguage);
		}
		
		// Extracting service end date (8 symbols; index range 24-31) and time (4 symbols; index range 32-35)
		if (27 < messageLength) {		// at least service end date is available
			int endIndex;
			String dateFormat;
			if (31 < messageLength) {	// both service end date and time available
				endIndex = 36;
				dateFormat = DATE_TIME_FORMAT;
			} else {					// only service end date available
				endIndex = 32;
				dateFormat = DATE_FORMAT;
			}
			
			String serviceEndDateString = data.substring(24, endIndex).trim(); // trimming result to avoid invalid Date and Tine format errors
			if (!serviceEndDateString.isEmpty()) {
				serviceEndDate = parseDate(serviceEndDateString, dateFormat);
				serviceMessage.setServiceEndDate(serviceEndDate);
			}
		}
		
		// Extracting XL-additional service activation time (4 symbols; index range 36-39)
		if (39 < messageLength) {
			String xlServiceActivationTimeString = data.substring(36, 40).trim();
			if (!xlServiceActivationTimeString.isEmpty()) {
				xlServiceActivationTime = parseTime(xlServiceActivationTimeString, TIME_FORMAT);
				serviceMessage.setXlServiceActivationTime(xlServiceActivationTime);
			}
		}
		
		// Extracting XL-additional service end time (4 symbols; index range 40-43)
		if (43 < messageLength) {
			String xlServiceEndTimeString = data.substring(40, 44).trim();
			if (!xlServiceEndTimeString.isEmpty()) {
				xlServiceEndTime = parseTime(xlServiceEndTimeString, TIME_FORMAT);
				serviceMessage.setXlServiceEndTime(xlServiceEndTime);
			}
		}
		
		// Extracting override list in use data (1 symbol; index 44)
		if (44 < messageLength) {
			char overrideListInUseSymbol = data.charAt(44);
			if (overrideListInUseSymbol == 'K') {
				overrideListInUse = true;
			} else if (overrideListInUseSymbol == 'E') {
				overrideListInUse = false;
			}
			
			serviceMessage.setOverrideListInUse(overrideListInUse);
		}
		
		// Extracting list of personal data.
		// Not all personal information might be available (e.g. it can be only name, only a phone number etc)
		if (overrideListInUse) {	// if true it means there is a list with personal data
			
			/*
			 * Extracting list of phone numbers if available (15 symbols * 8 = 120 symbols; index range 45-164)
			 * Extracting list of names if available (20 symbols * 8 = 160 symbols; index range 165-324)
			 *
			 * IMPLEMENTATION NOTE:
			 * Although now, according to the service convention, there MUST be at least 325 symbols,
			 * it is better to play safe and avoid IndexOutOfBoundsException. If after the confirmation,
			 * that override list is in use there are still less than 325 symbols, consider the service invalid.
			 */
			if (324 < messageLength) {
				int phoneStartIndex = 45;
				int phoneNumberLength = 15;
				String phoneNumber = "";
				
				int nameStartIndex = 165;
				int nameLength = 20;
				String name = "";
				
				for (int i = 0; i < 8; i++, phoneStartIndex += phoneNumberLength, nameStartIndex += nameLength) {
					phoneNumber = data.substring(phoneStartIndex, (phoneStartIndex + phoneNumberLength)).trim();
					name = data.substring(nameStartIndex, (nameStartIndex + nameLength)).trim();
					if (!phoneNumber.isEmpty() || !name.isEmpty()) {
						Person person = new Person(name, phoneNumber);
						persons.add(person);
					}
				}
				
				serviceMessage.setPersons(persons);
			}
		}
		
		return serviceMessage;
	}
	
}

