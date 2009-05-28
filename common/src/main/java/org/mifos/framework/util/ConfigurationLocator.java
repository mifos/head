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
 
package org.mifos.framework.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.ClassPathResource;

/**
 * Encapsulates logic for determining which directory to look in for
 * configuration files.
 * <p>
 * Does not use MifosLogger since this requires prior initialization, but this
 * particular class is used to find the file used to configure the logger.
 */
public class ConfigurationLocator {
    private static final String CURRENT_WORKING_DIRECTORY_PATH = "";
    private static final String LOCATOR_SYSTEM_PROPERTY_NAME = "mifos.conf";
    private static final String LOCATOR_ENVIRONMENT_PROPERTY_NAME = "MIFOS_CONF";
    private static final String HOME_PROPERTY_NAME = "user.home";
    private static final String MIFOS_USER_CONFIG_DIRECTORY_NAME = ".mifos";
    private static final String DEFAULT_CONFIGURATION_PATH = "org/mifos/config/resources/";

    @SuppressWarnings("PMD.ImmutableField")
    private ConfigurationLocatorHelper configurationLocatorHelper;

    public ConfigurationLocator() {
        super();
       configurationLocatorHelper = new ConfigurationLocatorHelper();
    }

    /**
     * Will not throw an exception if the file is not found. This method may be
     * used to find files in cases where we don't care if the file cannot be
     * found.
     */
    @SuppressWarnings({"PMD.EmptyCatchBlock", // see comment in empty catch block, below
                      "PMD.AvoidThrowingRawExceptionTypes"}) // being lazy; RuntimeException should be rare
    public String getSpringFilePath(String filename) {
        String returnValue = null;
        try {
            returnValue = "file:" + getFilePath(filename);
        } catch (FileNotFoundException e) {
            /*
             * Ignore so we can allow Spring to refer to "optional" files. This
             * may not be the correct approach--we may want to instead allow
             * this behavior (ignoring the exception) to be configurable.
             */
            returnValue = "";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return returnValue;
    }

    public String getFilePath(String filename) throws IOException {
        return getFile(filename).getAbsolutePath();
    }

    private String[] getDirectoriesToSearch() {
        String systemPropertyDirectory = System.getProperty(LOCATOR_SYSTEM_PROPERTY_NAME);
        String envPropertyDirectory = System.getenv(LOCATOR_ENVIRONMENT_PROPERTY_NAME);
        String homeDirectory = getHomeProperty();
        String userConfigDirectory = homeDirectory + '/' + MIFOS_USER_CONFIG_DIRECTORY_NAME;
        
        return new String[] { systemPropertyDirectory, envPropertyDirectory, userConfigDirectory };
    }

    @SuppressWarnings({"PMD.SystemPrintln", "PMD.OnlyOneReturn"}) // just until we get proper logging. See issue 2388.
    public String getConfigurationDirectory() throws IOException {
        for (String directoryPath : getDirectoriesToSearch()) {
            if (directoryExists(directoryPath)) {
                System.out.println("ConfigurationLocator found configuration directory: " + directoryPath);
                return directoryPath;
            }
        }
        return CURRENT_WORKING_DIRECTORY_PATH;
    }
    
    @SuppressWarnings({"PMD.SystemPrintln", "PMD.AvoidInstantiatingObjectsInLoops", "PMD.OnlyOneReturn"}) // just until we get proper logging. See issue 2388.
    private File getConfigurationFile(String filename) throws IOException {
        for (String directoryPath : getDirectoriesToSearch()) {
            if (StringUtils.isNotBlank(directoryPath)) {
                File file = new File(directoryPath, filename);
                if (file.exists()) {
                    return file;
                }
            }
        }
        return new ClassPathResource(DEFAULT_CONFIGURATION_PATH + filename).getFile();
    }

    private String getHomeProperty() {
        return configurationLocatorHelper.getHomeProperty(HOME_PROPERTY_NAME);
    }
    
    private boolean directoryExists(String directory) throws IOException {
        return StringUtils.isNotBlank(directory) && (getFileObject(directory)).exists();
    }

    private File getFileObject(String directory) throws IOException {
        return configurationLocatorHelper.getFile(directory);
    }

    @SuppressWarnings({"PMD.SystemPrintln"}) // just until we get proper logging. See issue 2388.
    public File getFile(String filename) throws IOException {
        File fileToReturn = getConfigurationFile(filename);
        System.out.println("ConfigurationLocator found configuration file: " + fileToReturn);
        return fileToReturn;
    }

    public void setConfigurationLocatorHelper(ConfigurationLocatorHelper fileFactory) {
        this.configurationLocatorHelper = fileFactory;
    }

}
