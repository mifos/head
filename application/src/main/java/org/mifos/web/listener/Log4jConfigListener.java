/*
 * Copyright Grameen Foundation USA
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

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.mifos.framework.util.ConfigurationLocator;
import org.mifos.framework.util.helpers.FilePaths;
import org.springframework.util.Log4jConfigurer;
import org.springframework.web.util.Log4jWebConfigurer;

/**
 * Bootstrap listener for custom log4j initialization in a web environment.
 *
 * <p>
 * This listener should be registered before ContextLoaderListener in <code>web.xml</code> when using custom log4j
 * initialization.
 *
 */
public class Log4jConfigListener implements ServletContextListener {

    // check if configuration file has changed/min
    final long refreshInterval = 60000;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        try {
            String location = new ConfigurationLocator().getFilePath(FilePaths.LOG_CONFIGURATION_FILE);
            Log4jConfigurer.initLogging(location, refreshInterval);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Unable to start logging " + e.getMessage());
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to start logging " + e.getMessage());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        Log4jWebConfigurer.shutdownLogging(event.getServletContext());
    }

}
