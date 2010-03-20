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
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.servlet.ServletContext;

import org.mifos.config.AccountingRules;
import org.mifos.config.ClientRules;
import org.mifos.config.ConfigLocale;
import org.mifos.config.ConfigurationManager;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.config.Localization;
import org.mifos.core.MifosException;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.components.batchjobs.MifosScheduler;
import org.mifos.framework.components.batchjobs.MifosTask;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.security.authorization.AuthorizationManager;
import org.mifos.security.authorization.HierarchyManager;
import org.mifos.service.test.TestMode;
import org.mifos.service.test.TestingService;

/**
 * Encapsulates all logic necessary to have the application behave differently
 * during acceptance and integration tests.
 */
public class StandardTestingService implements TestingService {
    private static final Logger LOG = Logger.getLogger(LoggerConstants.FRAMEWORKLOGGER);
    private final ConfigurationLocator configurationLocator;

    public StandardTestingService() {
        configurationLocator = new ConfigurationLocator();
    }

    public TestMode getTestMode() {
        String testMode = System.getProperty(TEST_MODE_SYSTEM_PROPERTY);
        if ("acceptance".equals(testMode)) {
            return TestMode.ACCEPTANCE;
        } else if ("integration".equals(testMode)) {
            return TestMode.INTEGRATION;
        } else {
            // "main" means we are *not* running in a test mode
            return TestMode.MAIN;
        }
    }

    public void setTestMode(TestMode newMode) {
        String modeString = newMode.toString().toLowerCase();
        System.setProperty(TEST_MODE_SYSTEM_PROPERTY, modeString);
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
            LOG.info("local overrides not found.");
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
            LOG.info("no local overrides in use.");
        }
        return settingsFilenames.toArray(new String[] {});
    }

    public String getDefaultSettingsFilename(TestMode testMode) {
        String defaultSettingsFilename = null;
        if (testMode == TestMode.MAIN) {
            defaultSettingsFilename = FilePaths.DATABASE_CONFIGURATION;
        } else if (testMode == TestMode.ACCEPTANCE) {
            defaultSettingsFilename = FilePaths.ACCEPTANCE_DATABASE_CONFIGURATION;
        } else if (testMode == TestMode.INTEGRATION) {
            defaultSettingsFilename = FilePaths.INTEGRATION_DATABASE_CONFIGURATION;
        } else {
            throw new RuntimeException("illegal mifos.test.mode");
        }
        return defaultSettingsFilename;
    }

    Properties translateToHibernate(Properties mifosSpecific, TestMode testModeEnum) {
        Properties hibernateSpecific = new Properties();
        String testMode = testModeEnum.toString().toLowerCase();

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
        hibernateToMifos.put("hibernate.cache.use_query_cache", testMode + ".database.hibernate.cache.use_query_cache");
        hibernateToMifos.put("hibernate.cache.use_second_level_cache", testMode + ".database.hibernate.cache.use_second_level_cache");
        hibernateToMifos.put("hibernate.connection.isolation", testMode + ".database.hibernate.connection.isolation");
        hibernateToMifos.put("hibernate.connection.release_mode", testMode + ".database.hibernate.connection.release_mode");
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

    /**
     * Provides for re-initialization of certain custom Mifos caches. Necessary
     * since acceptance tests may truncate and reload data directly in a Mifos
     * database without the application's knowledge, causing the application to
     * behave unexpectedly. Only allowed during acceptance tests.
     */
    @Override
    public void reinitializeCaches() {
        try {
            HierarchyManager.getInstance().init();
            AuthorizationManager.getInstance().init();
            AccountingRules.init();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setLocale(String languageCode, String countryCode) throws MifosException {
        try {
            ConfigLocale configLocale = new ConfigLocale();
            configLocale.setLanguageCode(languageCode);
            configLocale.setCountryCode(countryCode);
            Localization localization = Localization.getInstance();
            localization.setConfigLocale(configLocale);
            localization.refresh();
            ConfigurationManager configMgr = ConfigurationManager.getInstance();
            configMgr.setProperty("Localization.LanguageCode", languageCode);
            configMgr.setProperty("Localization.CountryCode", countryCode);
        } catch (MifosRuntimeException e) {
            throw new MifosException("The locale " + languageCode + "_" + countryCode + " is not supported by Mifos.");
        }
    }

    @Override
    public void setAccountingRules(String accountingRulesParamName, String accountingRulesParamValue) throws MifosException {
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        if(accountingRulesParamValue == null || accountingRulesParamValue.equals("")) {
            configMgr.clearProperty(accountingRulesParamName);
            return;
        }
        configMgr.setProperty(accountingRulesParamName, accountingRulesParamValue);

    }

    @Override
    public void setFiscalCalendarRules(String workingDays, String scheduleTypeForMeetingOnHoliday)
            throws MifosException {
        if (isSet(workingDays)) {
            new FiscalCalendarRules().setWorkingDays(workingDays);
        }
        if (isSet(scheduleTypeForMeetingOnHoliday)) {
            new FiscalCalendarRules().setScheduleTypeForMeetingOnHoliday(scheduleTypeForMeetingOnHoliday);
        }
    }

    private boolean isSet(Object value) {
        return value != null;
    }

    @Override
    public void setMaximumAgeForNewClient(int age) {
        ClientRules.setMaximumAgeForNewClient(age);
    }

    @Override
    public void setMinimumAgeForNewClient(int age) {
        ClientRules.setMinimumAgeForNewClient(age);
    }

    @Override
    public void setAreFamilyDetailsRequired(boolean flag) {
        ClientRules.setFamilyDetailsRequired(flag);
    }

    @Override
    public void setMaximumNumberOfFamilyMembers(int number) {
        ClientRules.setMaximumNumberOfFamilyMembers(number);
    }

    @Override
    public void runAllBatchJobs(final ServletContext ctx) {
        LOG.info("running all batch jobs");
        MifosScheduler mifosScheduler = (MifosScheduler) ctx.getAttribute(MifosScheduler.class.getName());
        mifosScheduler.runAllTasks();
    }

    @Override
    public void runIndividualBatchJob(final String requestedJob, final ServletContext ctx) {
        LOG.info("running batch job with name like: " + requestedJob + "*");
        boolean jobFound = false;
        final MifosScheduler mifosScheduler = (MifosScheduler) ctx.getAttribute(MifosScheduler.class.getName());
        OUTER:
        for (String taskName : mifosScheduler.getTaskNames()) {
            if (taskName.startsWith(requestedJob)) {
                final List<MifosTask> tasks = mifosScheduler.getTasks();
                for (MifosTask task : tasks) {
                    if (taskName.equals(task.name)) {
                        jobFound = true;
                        task.run();
                        break OUTER;
                    }
                }
                throw new MifosRuntimeException("task names and active tasks do not match!");
            }
        }
        if (!jobFound) {
            throw new IllegalArgumentException(requestedJob + " is unknown and will not be executed.");
        }
    }

}
