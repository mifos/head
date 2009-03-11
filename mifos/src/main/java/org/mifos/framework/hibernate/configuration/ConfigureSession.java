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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.mifos.core.ClasspathResource;
import org.mifos.framework.exceptions.HibernateStartUpException;
import org.mifos.framework.hibernate.factory.HibernateSessionFactory;
import org.mifos.framework.util.ConfigurationLocator;
import org.mifos.framework.util.TestingService;
import org.mifos.framework.util.helpers.FilePaths;

/**
 * Create the hibernate configuration object from the defined hibernate mapping
 * and properties files.
 */
public class ConfigureSession {
    private static Configuration config = null;

    /**
     * This method returns the hibernate configuration object configured during
     * system start up
     */
    public static Configuration getConfiguration() throws HibernateStartUpException {
        return config;
    }

    /**
     * On System start up this method creates the hibernate configuration
     * object, which is configured with the hibernate configuration files
     * containing the mapping information and also the database connection
     * related parameters required for hibernate for database access.
     */
    public static void configure() throws HibernateStartUpException {
        config = new Configuration();

        try {
            config.configure(ClasspathResource.getURI(FilePaths.HIBERNATECFGFILE).toURL());
        } catch (HibernateException e) {
            throw new HibernateStartUpException(e);
        } catch (MalformedURLException e) {
            throw new HibernateStartUpException(e);
        } catch (URISyntaxException e) {
            throw new HibernateStartUpException(e);
        }

        try {
            config.setProperties(new TestingService().getDatabaseConnectionSettings());
        } catch (IOException e) {
            throw new HibernateStartUpException(e);
        }

        HibernateSessionFactory.setConfiguration(ConfigureSession.getConfiguration());
    }

}
