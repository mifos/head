/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
 
package org.mifos.config;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.ConfigurationLocator;
import org.mifos.framework.util.StandardTestingService;
import org.mifos.service.test.TestMode;
import org.springframework.core.io.ClassPathResource;


/**
 * This is a quick initial sketch of a class for managing configuration
 * values that come from various sources.  The intent is to use file
 * based configuration values for some configuration data and 
 * database based configuration for other configuration data.  
 * <p>
 * The general idea is that configuration that does not change often
 * (or should not be changed often) would go into configuration files
 * while more frequently changed configuration would be stored in the
 * database and exposed via the UI.  In particular, configuration 
 * values that should be set once at install time or not be changed 
 * after being set are likely to go into configuration files.
 * <p>
 * This class is currently under active development, so it is likely
 * to be changed significantly as iterative development proceeds.
 */
public class ConfigurationManager implements Configuration {

	/**
	 * Filename where default application-wide configuration values are stored.
	 * This file should never be hand-edited, edit values in the custom config
	 * file instead.
	 * 
	 * @see #CUSTOM_CONFIG_PROPS_FILENAME
	 */
	public static final String DEFAULT_CONFIG_PROPS_FILENAME = "applicationConfiguration.default.properties";

	/**
	 * Filename where custom overrides for application-wide configuration values
	 * are stored. Keys in this file must exist in the default config file. This
	 * file may be hand-edited.
	 * 
	 * @see #DEFAULT_CONFIG_PROPS_FILENAME
	 */
	public static final String CUSTOM_CONFIG_PROPS_FILENAME = "applicationConfiguration.custom.properties";
	
	/** Custom application-wide configuration file for acceptance testing only. */
	public static final String ACCEPTANCE_CONFIG_PROPS_FILENAME = "applicationConfiguration.acceptance.properties";
	
	private static ConfigurationManager configurationManagerInstance = new ConfigurationManager();
		
	private Configuration configuration;
	
	public static final ConfigurationManager getInstance() {
		return configurationManagerInstance;	
	}

	private ConfigurationManager() {
	    ConfigurationLocator configurationLocator = new ConfigurationLocator();
	    Properties props = new Properties();

		try {
		    File defaults = configurationLocator.getFile(DEFAULT_CONFIG_PROPS_FILENAME);
		    props.load(new BufferedInputStream(new FileInputStream(defaults)));
		}
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        TestMode currentTestMode = new StandardTestingService().getTestMode();
        File customConfigFile = null;
        
        try {
            if (TestMode.MAIN == currentTestMode) {
                customConfigFile = configurationLocator.getFile(CUSTOM_CONFIG_PROPS_FILENAME);
            } else if (TestMode.ACCEPTANCE == currentTestMode) {
                customConfigFile = configurationLocator.getFile(ACCEPTANCE_CONFIG_PROPS_FILENAME);
            }
        } catch (FileNotFoundException e) {
            /* Do nothing */
        } catch (IOException e) {
            throw new SystemException(e);
        }
        
        if (TestMode.INTEGRATION == currentTestMode) {
            try {
                customConfigFile = new ClassPathResource("org/mifos/config/resources/" + CUSTOM_CONFIG_PROPS_FILENAME).getFile();
                props.load(new BufferedInputStream(new FileInputStream(customConfigFile)));
            } catch (IOException e) {
                // an IOException will be thrown if the file is not found
                // since we currently depend of finding this file, fail if we don't find it
                throw new SystemException(e);
            }
        }

		configuration = ConfigurationConverter.getConfiguration(props);
	}

	public Configuration getConfiguration() {
		return configuration;
	}
	
	public Short getShort(String key, Short defaultValue)
	{
		return configuration.getShort(key, defaultValue);
	}
	
	public short getShort(String key, short defaultValue)
	{
		return configuration.getShort(key, defaultValue);
	}
	
	public  short getShort(String key)
	{
		return configuration.getShort(key);
	}
	
	public float getFloat(String key)
	{
		return configuration.getFloat(key);
	}
	
	
	public String getString(String key, String defaultValue)
	{
		return configuration.getString(key, defaultValue);
	}
	
	
	public  int getInt(String key, int defaultValue)
	{
		return configuration.getInt(key, defaultValue);
	}
	
	public int getInt(String key)
	{
		return configuration.getInt(key);
	}
	
	public double getDouble(String key, double defaultValue)
	{
		return configuration.getDouble(key, defaultValue);
	}
	
	public  double getDouble(String key)
	{
		return configuration.getDouble(key);
	}
	
	public  boolean containsKey(String key)
	{
		return configuration.containsKey(key);
	}

	public void addProperty(String propertyName, Object propertyValue) {
		configuration.addProperty(propertyName, propertyValue);
	}
	
	public void clearProperty(String propertyName) {
		configuration.clearProperty(propertyName);
	}

	public void clear() {
		configuration.clear();
	}

	public BigDecimal getBigDecimal(String key) {
		return configuration.getBigDecimal(key);
	}

	public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
		return configuration.getBigDecimal(key, defaultValue);
	}

	public BigInteger getBigInteger(String key) {
		return configuration.getBigInteger(key);
	}

	public BigInteger getBigInteger(String key, BigInteger defaultValue) {
		return configuration.getBigInteger(key, defaultValue);
	}

	public boolean getBoolean(String key) {
		return configuration.getBoolean(key);
	}

	public boolean getBoolean(String key, boolean defaultValue) {
		return configuration.getBoolean(key, defaultValue);
	}

	public Boolean getBoolean(String key, Boolean defaultValue) {
		return configuration.getBoolean(key, defaultValue);
	}

	public byte getByte(String key) {
		return configuration.getByte(key);
	}

	public byte getByte(String key, byte defaultValue) {
		return configuration.getByte(key, defaultValue);
	}

	public Byte getByte(String key, Byte defaultValue) {
		return configuration.getByte(key, defaultValue);
	}

	

	public Double getDouble(String key, Double defaultValue) {
		return configuration.getDouble(key, defaultValue);
	}

	public float getFloat(String key, float defaultValue) {
		return configuration.getFloat(key, defaultValue);
	}

	public Float getFloat(String key, Float defaultValue) {
		return configuration.getFloat(key, defaultValue);
	}


	public Integer getInteger(String key, Integer defaultValue) {
		return configuration.getInteger(key, defaultValue);
	}

	public Iterator getKeys() {
		return configuration.getKeys();
	}

	public Iterator getKeys(String key) {
		return configuration.getKeys(key);
	}

	public List getList(String key) {
		return configuration.getList(key);
	}

	public List getList(String key, List defaultValue) {
		return configuration.getList(key, defaultValue);
	}

	public long getLong(String key) {
		return configuration.getLong(key);
	}

	public long getLong(String key, long defaultValue) {
		return configuration.getLong(key, defaultValue);
	}

	public Long getLong(String key, Long defaultValue) {
		return configuration.getLong(key, defaultValue);
	}

	public Properties getProperties(String key) {
		return configuration.getProperties(key);
	}

	public Object getProperty(String key) {
		return configuration.getProperty(key);
	}


	public String getString(String key) {
		return configuration.getString(key);
	}

	public String[] getStringArray(String key) {
		return configuration.getStringArray(key);
	}

	public boolean isEmpty() {
		return configuration.isEmpty();
	}

	public void setProperty(String propertyName, Object propertyValue) {
		configuration.setProperty(propertyName, propertyValue);
	}
	
    // Return a decorator Configuration containing every key from the current Configuration 
	// that starts with the specified prefix
	public Configuration subset(String prefix) {
		return configuration.subset(prefix);
	}

}
