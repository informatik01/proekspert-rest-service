package ee.proekspert.webapp.util;

import java.io.InputStream;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * This is a utility class to ease retrieving data from the properties files.
 * 
 */
public class PropertiesUtil {
	
	/*
	 * IMPLEMENTATION NOTE.
	 * Current implementation uses Apache Commons Configuration library because of its
	 * flexibility and enhanced possibilities compared with standard Java SE properties.
	 * (e.g. variable interpolation, referencing system properties and others)
	 */
	private PropertiesConfiguration config = new PropertiesConfiguration();
	
	private String configFileName;

	public PropertiesUtil(String configFileName) throws PropertiesUtilException {
		this.configFileName = configFileName;
		
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(configFileName);
		if (inputStream == null) {
			throw new PropertiesUtilException("No properties file with the name \"" +
											configFileName + "\" found on the class path.");
		}
		
		try {
			config.load(inputStream);
		} catch (ConfigurationException e) {
			throw new PropertiesUtilException("Could not load properties file \"" + configFileName + "\".");
		}
	}
	
	public String getProperty(String key) throws PropertiesUtilException {
		String property = config.getString(key, null);
		if (property == null || property.trim().length() == 0) {
			throw new PropertiesUtilException("Property '" + key + "' is missing in configuration file '" + 
											configFileName + "'." );
		}
		
		return property;
	}
	
	public String[] getProperties(String key) throws PropertiesUtilException {
		String[] properties = config.getStringArray(key);
		if (properties == null || properties.length == 0) {
			throw new PropertiesUtilException("Property '" + key + "' is missing in configuration file '" + 
					configFileName + "'." );
		}
		
		return properties;
	}
}

