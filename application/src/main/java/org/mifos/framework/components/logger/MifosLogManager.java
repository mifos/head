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

package org.mifos.framework.components.logger;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.apache.log4j.varia.NullAppender;
import org.mifos.config.Localization;
import org.mifos.framework.exceptions.LoggerConfigurationException;
import org.mifos.framework.exceptions.ResourceBundleNotFoundException;
import org.mifos.framework.util.ConfigurationLocator;
import org.mifos.framework.util.helpers.FilePaths;

/**
 * A class with static methods to obtain instances of the logger. It also keeps
 * a HashMap of the actual logger instances per module
 */
public class MifosLogManager {

    /**
     * Contains all the loggers that have been created. Key is the name of the
     * logger and the value is logger instance
     */
    private static HashMap<String, MifosLogger> loggerRepository;

    public static boolean isConfigured() {
        return loggerRepository != null;
    }

    /**
     * Method to initialize the loggerRepository and configure the root logger
     * from the loggerconfiguration.xml A root logger instance is also created
     * and the resource bundle for the locale of the MFI is associated with the
     * logger
     *
     * @param fileName
     *            The loggerconfiguration xml file
     * @throws LoggerConfigurationException
     */
    public static void configure(String fileName) throws LoggerConfigurationException {
        // Initialises a logger with the name com.mifos which acts as the
        // ancestor for all the other loggers
        try {
            readConfiguration(new ConfigurationLocator().getFilePath(fileName));
            MifosLogger logger = new Log4jLogger(LoggerConstants.ROOTLOGGER,
                    getResourceBundle(LoggerConstants.LOGGERRESOURCEBUNDLE));
            loggerRepository = new HashMap<String, MifosLogger>(20);
            loggerRepository.put(LoggerConstants.ROOTLOGGER, logger);

            // For Hibernate, and anything else outside org.mifos.
            // TODO: Figure out how to make this more specific to
            // org.hibernate, and/or set the level to WARN, and/or
            // send the output somewhere. The way it is now makes
            // it too easy to lose messages, perhaps.
            Logger.getRootLogger().addAppender(new NullAppender());
        } catch (ResourceBundleNotFoundException e) {
            throw new LoggerConfigurationException(e);
        } catch (MalformedURLException e) {
            throw new LoggerConfigurationException(e);
        } catch (IOException e) {
            throw new LoggerConfigurationException(e);
        }
    }

    /**
     * Function to obtain an instance of the mifos logger.
     *
     * @param name
     *            The name the logger will be associated with
     */
    public static MifosLogger getLogger(String name) {
        return getLogger(name, null);
    }

    /**
     * Function to obtain an instance of the mifos logger. This calls a helper
     * method to create the logger A resource bundle can aslo be associated with
     * this logger
     *
     * @param name
     *            The name the logger will be associated with
     * @param resourceBundleName
     *            The name of the resource bundle from where the logger takes
     *            its log statements
     * @return An instance of the MifosLogger
     */
    public static MifosLogger getLogger(String name, String resourceBundleName) {
        return getLoggerHelper(name, resourceBundleName);
    }

    /**
     * Function to obtain an instance of the mifos logger. If it is already
     * present it is retrieved from the logger repository, else it is created
     * and added to the repository. A resource bundle can also be associated
     * with this logger
     *
     * @param name
     *            The name the logger will be associated with
     * @param resourceBundleName
     *            The name of the resource bundle from where the logger takes
     *            its log statements. If it is null then the logger is created
     *            using the name
     * @return An instance of the MifosLogger
     */
    public static MifosLogger getLoggerHelper(String name, String resourceBundleName) {

        MifosLogger logger;
        //initialize loggerRepositor if not initialized
        configureLogging();

        // checks to see if the logger repository already contains an instance
        // of the logger.
        // If it does that instance is returned
        if (loggerRepository.containsKey(name)) {
            logger = loggerRepository.get(name);
        }
        // Since the logger repository doesnt contain an instance, a new
        // instance is created and put into the logger repository
        else {
            // taking care of the scenario: two users creating the same instance
            // of a logger and trying to push into
            // the repository. To avoid this the repository is synchronised
            // allowing only one person to push the
            // instance in. For the second user a check on the availability is
            // done again
            synchronized (loggerRepository) {
                if (loggerRepository.containsKey(name)) {
                    logger = loggerRepository.get(name);
                } else {
                    if (resourceBundleName != null) {
                        try {
                            logger = new Log4jLogger(name, getResourceBundle(resourceBundleName));
                        } catch (ResourceBundleNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        logger = new Log4jLogger(name);
                    }
                    loggerRepository.put(name, logger);
                }
            }
        }

        return logger;
    }

    /**
     * Obtains the Resource bundle for a particular MFI Locale
     */
    protected static ResourceBundle getResourceBundle(String resourceBundleName) throws ResourceBundleNotFoundException {
        Locale mfiLocale = getMFILocale();
        ResourceBundleFactory resourceBundleFactory = ResourceBundleFactory.getInstance();
        return resourceBundleFactory.getResourceBundle(resourceBundleName, mfiLocale);
    }

    /**
     * Configures the root logger from the loggerconfiguration.xml A root logger
     * instance is also created and the resource bundle for the locale of the
     * MFI is associated with the logger
     */
    private static void readConfiguration(String loggerConfigurationAbsolutePath) {
        File configFile = new File(loggerConfigurationAbsolutePath);

        MifosDOMConfigurator.configureAndWatch(configFile.getAbsolutePath(), LoggerConstants.DELAY);
    }

    private static Locale getMFILocale() {
        // return ApplicationConfig.getMFILocale();
        return Localization.getInstance().getMainLocale();
    }

    /**
     * Set up log4j (this is required for hibernate, as well as perhaps other
     * parts of MIFOS).
     */
    private static void configureLogging() {
        if (!isConfigured()) {
            configure(FilePaths.LOG_CONFIGURATION_FILE);
        }
    }

}
