/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

import org.apache.commons.lang.StringUtils;
import org.mifos.framework.components.logger.LoggerConstants;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Encapsulates logic for determining which directory to look in for
 * configuration files.
 * <p>
 * Does not use MifosLogger since this requires prior initialization, but this
 * particular class is used to find the file used to configure the logger.
 */
public class ConfigurationLocator {
    private static final String LOCATOR_ENVIRONMENT_PROPERTY_NAME = "MIFOS_CONF";
    private static final String CURRENT_WORKING_DIRECTORY_PATH = "";
    private static final String LOCATOR_SYSTEM_PROPERTY_NAME = "mifos.conf";
    private static final String HOME_PROPERTY_NAME = "user.home";
    private static final String MIFOS_USER_CONFIG_DIRECTORY_NAME = ".mifos";
    private static final String DEFAULT_CONFIGURATION_PATH = "org/mifos/config/resources/";
    private static final Logger LOG = Logger.getLogger(LoggerConstants.FRAMEWORKLOGGER);

    @SuppressWarnings("PMD.ImmutableField")
    private ConfigurationLocatorHelper configurationLocatorHelper;
    private String defaultConfigPath;
    private static final Pattern propertyPattern = Pattern.compile("\\$\\{([^\\$\\s/\\{\\}]+)\\}");
    private static final Pattern envVarPattern = Pattern.compile("\\$([^\\$\\W\\{\\}/]+)");

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
        String envPropertyDirectory = configurationLocatorHelper.getEnvironmentProperty(LOCATOR_ENVIRONMENT_PROPERTY_NAME);
        String homeDirectory = getHomeProperty();
        String userConfigDirectory = homeDirectory + '/' + MIFOS_USER_CONFIG_DIRECTORY_NAME;

        return new String[] { systemPropertyDirectory, envPropertyDirectory, userConfigDirectory };
    }

    @SuppressWarnings({"PMD.OnlyOneReturn"})
    public String getConfigurationDirectory() {
        for (String directoryPath : getDirectoriesToSearch()) {
            if (directoryExists(directoryPath)) {
                LOG.info("ConfigurationLocator found configuration directory: " + directoryPath);
                return directoryPath;
            }
        }
        return CURRENT_WORKING_DIRECTORY_PATH;
    }

    @SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops", "PMD.OnlyOneReturn"})
    private File getConfigurationFile(String filename) throws IOException {
        for (String directoryPath : getDirectoriesToSearch()) {
            if (StringUtils.isNotBlank(directoryPath)) {
                File file = new File(directoryPath, filename);
                if (file.exists()) {
                    return file;
                }
            }
        }
        return new ClassPathResource(getDefaultConfigPath() + filename).getFile();
    }

    private String getHomeProperty() {
        return configurationLocatorHelper.getHomeProperty(HOME_PROPERTY_NAME);
    }

    private boolean directoryExists(String directory) {
        return StringUtils.isNotBlank(directory) && (getFileObject(directory)).exists();
    }

    private File getFileObject(String directory) {
        return configurationLocatorHelper.getFile(directory);
    }

    public File getFile(String filename) throws IOException {
        File fileToReturn = getConfigurationFile(filename);
        LOG.info("ConfigurationLocator found configuration file: " + fileToReturn);
        return fileToReturn;
    }

    public void setConfigurationLocatorHelper(ConfigurationLocatorHelper fileFactory) {
        this.configurationLocatorHelper = fileFactory;
    }

    public void setDefaultConfigPath(String path) {
        this.defaultConfigPath = path;
    }

    private String getDefaultConfigPath() {
        return StringUtils.isBlank(this.defaultConfigPath) ? DEFAULT_CONFIGURATION_PATH : this.defaultConfigPath; 
    }

    public String resolvePath(String fileName) {
        StringBuilder fileBuffer = new StringBuilder(fileName);
        resolveHomeProperties(fileName, fileBuffer);
        resolveEnvironmentProperties(fileName, fileBuffer);
        return fileBuffer.toString();
    }

    private void resolveEnvironmentProperties(String fileName, StringBuilder fileBuffer) {
        Matcher envVarMatcher = envVarPattern.matcher(fileName);
        while (envVarMatcher.find()) {
            String envVar = envVarMatcher.group();
            String environmentProperty = configurationLocatorHelper.getEnvironmentProperty(envVar.substring(1));
            if (environmentProperty != null) {
                fileBuffer.replace(envVarMatcher.start(), envVarMatcher.end(), environmentProperty);
            }
        }
    }

    private void resolveHomeProperties(String fileName, StringBuilder fileBuffer) {
        Matcher matcher = propertyPattern.matcher(fileName);
        while (matcher.find()) {
            String property = matcher.group();
            String homeProperty = configurationLocatorHelper.getHomeProperty(property.substring(2, property.length() - 1));
            if (homeProperty != null) {
                fileBuffer.replace(matcher.start(), matcher.end(), homeProperty);
            }
        }
    }
}