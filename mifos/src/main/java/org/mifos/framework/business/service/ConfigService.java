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

package org.mifos.framework.business.service;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.mifos.framework.exceptions.ConfigServiceInitializationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.CollectionUtils;
import org.springframework.core.io.Resource;

public abstract class ConfigService {
    private Properties config;
    private static final String CSV_DELIMITER = "\\s*,\\s*";

    public ConfigService(Resource configResource) {
        super();
        initConfig(configResource);
    }

    protected void initConfig(Resource configResource) {
        if (!configResource.exists())
            throw new ConfigServiceInitializationException("Failed to initialize config cervice for resource: "
                    + configResource.getFilename());
        config = new Properties();
        try {
            config.load(configResource.getInputStream());
        } catch (IOException e) {
            throw new ConfigServiceInitializationException("Failed to initialize Config service ", e);
        }
    }

    protected String getProperty(String propertyKey) throws ServiceException {
        String propertyValue = config.getProperty(propertyKey);
        if (propertyValue == null)
            throw new ServiceException("Failed to retrieve " + propertyKey + " from config resource");
        return propertyValue;
    }

    protected List<String> getPropertyValues(String key) throws ServiceException {
        String[] values = getProperty(key).split(CSV_DELIMITER);
        return CollectionUtils.asList(values);
    }

    public boolean isPropertyPresent(String key) {
        return config.containsKey(key);
    }
}
