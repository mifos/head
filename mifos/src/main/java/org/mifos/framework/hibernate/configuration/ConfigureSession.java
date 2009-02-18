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

package org.mifos.framework.hibernate.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.util.Properties;

import org.hibernate.cfg.Configuration;
import org.mifos.framework.exceptions.HibernateStartUpException;
import org.mifos.framework.hibernate.helper.HibernateConstants;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.core.ClasspathResource;

/**
	Create the hibernate configuration object from the 
	defined hibernate mapping files
*/

public class ConfigureSession
{
	private static Configuration config = null;


	/**
		This method returns the hibernate configuration object configured 
		during system start up
	*/

	public static Configuration getConfiguration() throws HibernateStartUpException
	{
		return config;
	}

	/**
	 * On System start up this method creates the hibernate configuration object,
	 * which is configured with the hibernate configuration files containing the
	 * mapping information and also the database connection related parameters
	 * required for hibernate for database access.
	 */
    public static void configure(String hibernatePropertiesPath)
			throws HibernateStartUpException {
		config = new Configuration();
		try {
			config.configure(ClasspathResource.getURI(FilePaths.HIBERNATECFGFILE).toURL());
		} catch (Exception e) {
			throw new HibernateStartUpException(
					HibernateConstants.CFGFILENOTFOUND, e);
		}

		try {
			// TODO: factor this out instead of repeating it in ApplicationInitializer
			Properties hibernateProperties = new Properties();

			URI uri = ClasspathResource.getURI(hibernatePropertiesPath);
			File propertiesFile;
			if (uri == null) {
				// Look for it in the current directory.
				// This is how we currently find it when running
				// tests directly from an IDE.
				propertiesFile = new File(hibernatePropertiesPath);
			} else {
				// We found it in the classpath.  The normal case
				// for when we have run an "ant copy_files" 
				propertiesFile = new File(uri);
			}
			hibernateProperties.load(new FileInputStream(propertiesFile));
			config.setProperties(hibernateProperties);
			// Allow database to be specified dynamically for testing
			if (System.getProperty("hibernate.connection.url") != null) {
				config.setProperty("hibernate.connection.url", System.getProperty("hibernate.connection.url"));
			}
		} catch (Exception e) {
			throw new HibernateStartUpException(
					HibernateConstants.HIBERNATEPROPNOTFOUND, e);
		}
	}


}
