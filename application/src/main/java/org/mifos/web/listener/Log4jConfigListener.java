/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.web.listener;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.mifos.framework.util.ConfigurationLocator;
import org.mifos.framework.util.helpers.FilePaths;
import org.springframework.core.io.Resource;
import org.springframework.util.Log4jConfigurer;

/**
 * Bootstrap listener for custom log4j initialization in a web environment.
 * 
 * <p>
 * <b>WARNING: Assumes an expanded WAR file</b>, if loading the Mifos embedded default loggerconfiguration.xml (cannot
 * load from a JAR inside a packaged WAR).
 * 
 * <p>
 * This listener should be registered before ContextLoaderListener in <code>web.xml</code> when using custom log4j
 * initialization.
 * 
 * @see org.springframework.web.util.Log4jConfigListener which this is based on, but we are not using that as we want to
 *      control Logging configuration location via Mifos ConfigurationLocator (which checks ~/.mifos etc.)
 */
public class Log4jConfigListener implements ServletContextListener {

    private static final String NAME = FilePaths.LOG_CONFIGURATION_FILE;

    // check if configuration file has changed/min
    final long refreshInterval = 60000;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        Resource location;
        try {
            location = new ConfigurationLocator().getResource(NAME);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to start logging, because " + NAME + " look-up failed: "
                    + e.getMessage(), e);
        }

        // following code due to https://jira.springsource.org/browse/SPR-8254
        try {
            File locationFile = location.getFile();
            // can use refreshInterval here (as opposed to below)
            Log4jConfigurer.initLogging(locationFile.getAbsolutePath(), refreshInterval);
            return;
        } catch (IOException e) {
            try {
                URL locationURL = location.getURL();
                // use method w.o. refreshInterval here (@see https://jira.springsource.org/browse/SPR-707)
                Log4jConfigurer.initLogging(locationURL.toString());
            } catch (IOException e1) {
                throw new IllegalArgumentException("Unable to start logging, because non-File resource "
                        + location.getDescription() + " toURL() failed: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        Log4jConfigurer.shutdownLogging();
    }

}
