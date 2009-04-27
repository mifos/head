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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.security.authorization.AuthorizationManager;
import org.mifos.framework.security.authorization.HierarchyManager;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.service.test.TestingService;

/**
 * Encapsulates all logic necessary to have the application behave differently
 * during acceptance and integration tests.
 */
public class StandardTestingService implements TestingService {
    private MifosLogger LOG = null;
    private final ConfigurationLocator configurationLocator;

    public StandardTestingService() {
        LOG = MifosLogManager.getLogger(LoggerConstants.CONFIGURATION_LOGGER);
        configurationLocator = new ConfigurationLocator();
    }

    public String getTestMode() {
        // "main" means we are *not* running in a test mode
        return System.getProperty("mifos.test.mode", "main");
    }

    public Properties getDatabaseConnectionSettings() throws IOException {
        String defaultSettingsFilename = getDefaultSettingsFilename(getTestMode());

        File defaultsFile = configurationLocator.getFile(defaultSettingsFilename);
        Properties mifosSpecific = new Properties();
        mifosSpecific.load(new FileInputStream(defaultsFile));

        Properties overrides = new Properties();

        try {
            File overridesFile = configurationLocator.getFile(FilePaths.LOCAL_CONFIGURATION_OVERRIDES);
            overrides.load(new FileInputStream(overridesFile));
        } catch (FileNotFoundException e) {
            // basically ignore; no matter if they don't have local overrides
            LOG.warn("StandardTestingService: local overrides not found.");
        }

        mifosSpecific.putAll(overrides);

        return translateToHibernate(mifosSpecific, getTestMode());
    }

    public String[] getAllSettingsFilenames() throws IOException {
        ArrayList<String> settingsFilenames = new ArrayList<String>();
        settingsFilenames.add(configurationLocator.getFilePath(getDefaultSettingsFilename(getTestMode())));
        try {
            String optionalOverrides = configurationLocator.getFilePath(FilePaths.LOCAL_CONFIGURATION_OVERRIDES);
            settingsFilenames.add(optionalOverrides);
        } catch (FileNotFoundException e) {
            // basically ignore; no matter if they don't have local overrides
            LOG.warn("StandardTestingService: no local overrides in use.");
        }
        return settingsFilenames.toArray(new String[] {});
    }

    public String getDefaultSettingsFilename() {
        return getDefaultSettingsFilename(System.getProperty("mifos.test.mode", getTestMode()));
    }

    public String getDefaultSettingsFilename(String testMode) {
        String defaultSettingsFilename = null;
        if (testMode.equals("main")) {
            defaultSettingsFilename = FilePaths.DATABASE_CONFIGURATION;
        } else if (testMode.equals("acceptance")) {
            defaultSettingsFilename = FilePaths.ACCEPTANCE_DATABASE_CONFIGURATION;
        } else if (testMode.equals("integration")) {
            defaultSettingsFilename = FilePaths.INTEGRATION_DATABASE_CONFIGURATION;
        } else {
            throw new RuntimeException("illegal mifos.test.mode");
        }
        return defaultSettingsFilename;
    }

    Properties translateToHibernate(Properties mifosSpecific, String testMode) {
        Properties hibernateSpecific = new Properties();

        String host = mifosSpecific.getProperty(testMode + ".database.host");
        String port = mifosSpecific.getProperty(testMode + ".database.port");
        String database = mifosSpecific.getProperty(testMode + ".database");
        String params = mifosSpecific.getProperty(testMode + ".database.params");
        hibernateSpecific.setProperty("hibernate.connection.url", "jdbc:mysql://" + host + ":" + port + "/" + database
                + "?" + params);

        Map<String, String> hibernateToMifos = new HashMap<String, String>();
        hibernateToMifos.put("hibernate.connection.driver_class", testMode + ".database.driver");
        hibernateToMifos.put("hibernate.connection.username", testMode + ".database.user");
        hibernateToMifos.put("hibernate.connection.password", testMode + ".database.password");
        hibernateToMifos.put("hibernate.dialect", testMode + ".database.hibernate.dialect");
        hibernateToMifos.put("hibernate.show_sql", testMode + ".database.hibernate.show_sql");
        hibernateToMifos.put("hibernate.transaction.factory_class", testMode
                + ".database.hibernate.transaction.factory_class");
        hibernateToMifos.put("hibernate.cache.provider_class", testMode + ".database.hibernate.cache.provider_class");
        hibernateToMifos.put("hibernate.connection.isolation", testMode + ".database.hibernate.connection.isolation");
        hibernateToMifos.put("hibernate.c3p0.acquire_increment", testMode
                + ".database.hibernate.c3p0.acquire_increment");
        hibernateToMifos.put("hibernate.c3p0.idle_test_period", testMode + ".database.hibernate.c3p0.idle_test_period");
        hibernateToMifos.put("hibernate.c3p0.max_size", testMode + ".database.hibernate.c3p0.max_size");
        hibernateToMifos.put("hibernate.c3p0.max_statements", testMode + ".database.hibernate.c3p0.max_statements");
        hibernateToMifos.put("hibernate.c3p0.min_size", testMode + ".database.hibernate.c3p0.min_size");
        hibernateToMifos.put("hibernate.c3p0.timeout", testMode + ".database.hibernate.c3p0.timeout");

        for (String key : hibernateToMifos.keySet()) {
            String value = mifosSpecific.getProperty(hibernateToMifos.get(key));
            if (null != value) {
                hibernateSpecific.setProperty(key, value);
            }
        }

        return hibernateSpecific;
    }

    @Override
    public void reinitializeCaches() {
        if ("main".equals(getTestMode())) {
            throw new RuntimeException("only allowed during testing");
        } else {
            try {
                HierarchyManager.getInstance().init();
                AuthorizationManager.getInstance().init();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            LOG.info("cache reinitialization complete.");
        }
    }
}
