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
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.ClassPathResource;

/**
 * Encapsulates logic for determining which directory 
 * to look in for configuration files.
 */
public class ConfigurationLocator {
    private static final String LOCATOR_SYSTEM_PROPERTY_NAME = "mifos.conf";
    private static final String LOCATOR_ENVIRONMENT_PROPERTY_NAME = "MIFOS_CONF";
    private static final String HOME_PROPERTY_NAME = "user.home";
    private static final String MIFOS_USER_CONFIG_DIRECTORY_NAME = ".mifos";
    private static final String DEFAULT_CONFIGURATION_PATH = "org/mifos/config/resources/";
    
    private boolean lastFileHandleFromClassPath = true;
    
    public String getSpringFilePath(String filename) throws IOException {
        return "file:" + getFileHandle(filename).getAbsolutePath();
    }
    
    public String getFilePath(String filename) throws IOException {
        return getFileHandle(filename).getAbsolutePath();
    }
    
    public File getFileHandle(String filename) throws IOException {
        String systemPropertyDirectory = System.getProperty(LOCATOR_SYSTEM_PROPERTY_NAME);
        String envPropertyDirectory = System.getenv(LOCATOR_ENVIRONMENT_PROPERTY_NAME);
        String homeDirectory = System.getProperty(HOME_PROPERTY_NAME);

        File fileToReturn = null;
        lastFileHandleFromClassPath = false;
        
        if (StringUtils.isNotBlank(systemPropertyDirectory)) {
            File file = new File(systemPropertyDirectory,filename);
            System.out.println("Checking system property path: " + file.getAbsolutePath());
            if (file.exists()) {
                System.out.println("Found system property path: " + file.getAbsolutePath());
                fileToReturn = file;
            }
        } else if (StringUtils.isNotBlank(envPropertyDirectory)) {
            File file = new File(envPropertyDirectory,filename);
            System.out.println("Checking environment path: " + file.getAbsolutePath());
            if (file.exists()) {
                fileToReturn = file;
            }
        } else if (new File(homeDirectory,MIFOS_USER_CONFIG_DIRECTORY_NAME).exists()) {
            File file = new File(homeDirectory + "/" + MIFOS_USER_CONFIG_DIRECTORY_NAME,filename);
            System.out.println("Checking home directory path: " + file.getAbsolutePath());
            if (file.exists()) {
                fileToReturn = file;
            }
        } 
        
        if (fileToReturn == null) {
            filename = DEFAULT_CONFIGURATION_PATH + filename;
            System.out.println("Using classpath resource: " + new ClassPathResource(filename).getPath());
            fileToReturn = new ClassPathResource(filename).getFile();
            lastFileHandleFromClassPath = true;
        }
        
        return fileToReturn;
    }

    public boolean isLastFileHandleFromClassPath() {
        return lastFileHandleFromClassPath;
    }
    
    
}
