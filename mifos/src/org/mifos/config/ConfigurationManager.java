package org.mifos.config;

import java.net.URL;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationFactory;

/**
 * This is a quick initial sketch of a class for managing configuration
 * values that come from various sources.  The intent is to use file
 * based configuration values for some configuration data and 
 * database based configuration for other configuration data.  
 * 
 * The general idea is that configuration that does not change often
 * (or should not be changed often) would go into configuration files
 * while more frequently changed configuration would be stored in the
 * database and exposed via the UI.  In particular, configuration 
 * values that should be set once at install time or not be changed 
 * after being set are likely to go into configuration files.
 * 
 * The file org/mifos/config/resources/configurationFactory.xml 
 * contains the names of the configuration files currently being
 * read.
 * 
 * This class is currently under active development, so it is likely
 * to be changed significantly as iterative development proceeds.
 */
public class ConfigurationManager {
	private static ConfigurationManager configurationManagerInstance = new ConfigurationManager();
		
	private Configuration configuration;
	
	public static final String TestKey = "test.value"; 
	
	public static final ConfigurationManager getInstance() {
		return configurationManagerInstance;
	}

	public ConfigurationManager() {
		ConfigurationFactory factory = new ConfigurationFactory();
		URL configURL = getClass().getResource("resources/configurationFactory.xml");
		factory.setConfigurationURL(configURL);
		
		try {
			configuration = factory.getConfiguration();
		}
		catch (ConfigurationException e) {
			throw new RuntimeException(e);
		}	
	}

	public Configuration getConfiguration() {
		return configuration;
	}
	
}
