package ee.proekspert.webapp.model;

import java.util.Date;
import java.util.List;

import org.joda.time.LocalTime;

/**
 * <code>ServiceMessage</code> is a JavaBean class for storing
 * legacy service related data.
 *
 */
public class ServiceMessage {
	
	private int id;
	
	private boolean active;				

	private String servicePhoneNumber;

	private boolean xlService;			

	private String serviceLanguage;

	private String xlServiceLanguage;
		
	private Date serviceEndDate;
	
	private LocalTime xlServiceActivationTime;

	private LocalTime xlServiceEndTime;		

	private boolean overrideListInUse;		
	
	private List<Person> persons;			
	
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}

	public String getServicePhoneNumber() {
		return servicePhoneNumber;
	}

	public void setServicePhoneNumber(String servicePhoneNumber) {
		this.servicePhoneNumber = servicePhoneNumber;
	}

	public boolean isXlService() {
		return xlService;
	}

	public void setXlService(boolean xlService) {
		this.xlService = xlService;
	}

	public String getServiceLanguage() {
		return serviceLanguage;
	}

	public void setServiceLanguage(String serviceLanguage) {
		this.serviceLanguage = serviceLanguage;
	}

	public String getXlServiceLanguage() {
		return xlServiceLanguage;
	}

	public void setXlServiceLanguage(String xlServiceLanguage) {
		this.xlServiceLanguage = xlServiceLanguage;
	}

	public Date getServiceEndDate() {
		return serviceEndDate;
	}

	public void setServiceEndDate(Date serviceEndDate) {
		this.serviceEndDate = serviceEndDate;
	}

	public LocalTime getXlServiceActivationTime() {
		return xlServiceActivationTime;
	}

	public void setXlServiceActivationTime(LocalTime xlServiceActivationTime) {
		this.xlServiceActivationTime = xlServiceActivationTime;
	}

	public LocalTime getXlServiceEndTime() {
		return xlServiceEndTime;
	}

	public void setXlServiceEndTime(LocalTime xlServiceEndTime) {
		this.xlServiceEndTime = xlServiceEndTime;
	}

	public boolean isOverrideListInUse() {
		return overrideListInUse;
	}

	public void setOverrideListInUse(boolean overrideListInUse) {
		this.overrideListInUse = overrideListInUse;
	}

	public List<Person> getPersons() {
		return persons;
	}

	public void setPersons(List<Person> persons) {
		this.persons = persons;
	}
        
}