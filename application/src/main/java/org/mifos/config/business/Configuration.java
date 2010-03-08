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

package org.mifos.config.business;

import java.util.Hashtable;
import java.util.Map;

import org.mifos.config.cache.CacheRepository;
import org.mifos.config.util.helpers.ConfigurationInitializer;

/**
 * This class is a remnant of per-office configuration, which <a
 * href="http://article.gmane.org/gmane.comp.finance.mifos.devel/3498">is
 * deprecated and may be removed</a> (-Adam 22-JAN-2008).
 */
public class Configuration {
    /**
     * It is the map of instances of OfficeConfig stored based on officeid. It
     * is cache of OfficeConfig instances
     */
    private Map<Short, OfficeConfig> officeConfigMap;

    private SystemConfiguration systemConfig;

    private static CacheRepository cacheRepo;

    private static Configuration config;

    public synchronized static Configuration getInstance() {
        if (config == null) {
            config = new Configuration();
            config.initialize();
        }
        return config;
    }

    private void initialize() {
        synchronized (cacheRepo) {
            new ConfigurationInitializer().initialize();
            initializeSystemConfiguration();
        }
    }

    // TODO:Currently offset is being passed for TimeZone. It should be changed
    // to Timezone value picked from database
    private void initializeSystemConfiguration() {
        systemConfig = cacheRepo.getSystemConfiguration();
    }

    private Configuration() {
        cacheRepo = CacheRepository.getInstance();
        officeConfigMap = new Hashtable<Short, OfficeConfig>();
    }

    /**
     * This method will return instance of OfficeConfig based on officeId. If
     * OfficeConfig instance is available in cache it will return the same,
     * otherwise it will create and retuen a new instance and store that into
     * cache also.
     */
    public OfficeConfig getOfficeConfig(Short officeId) {
        if (officeConfigMap.containsKey(officeId)) {
            return officeConfigMap.get(officeId);
        }
        OfficeConfig officeConfig = new OfficeConfig(cacheRepo, officeId);
        officeConfigMap.put(officeId, officeConfig);
        return officeConfig;
    }

    public SystemConfiguration getSystemConfig() {
        return systemConfig;
    }

    public AccountConfig getAccountConfig(Short officeId) {
        return getOfficeConfig(officeId).getAccountConfig();
    }

    /**
     * For injecting mock configuration instance
     * @param config
     */
    public static void setConfig(Configuration config) {
        Configuration.config = config;
    }

}
