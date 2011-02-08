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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mifos.config.AccountingRules;
import org.mifos.config.ClientRules;
import org.mifos.config.ConfigLocale;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.config.Localization;
import org.mifos.config.business.MifosConfigurationManager;
import org.mifos.core.MifosException;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.components.batchjobs.MifosScheduler;
import org.mifos.framework.components.batchjobs.exceptions.TaskSystemException;
import org.mifos.framework.components.mifosmenu.MenuBuilder;
import org.mifos.framework.components.mifosmenu.MenuRepository;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.security.authorization.AuthorizationManager;
import org.mifos.security.authorization.HierarchyManager;
import org.mifos.service.test.TestMode;
import org.mifos.service.test.TestingService;
import org.apache.commons.lang.StringUtils;

/**
 * Encapsulates all logic necessary to have the application behave differently during acceptance and integration tests.
 */
public class StandardTestingService implements TestingService {
    private static final Logger logger = LoggerFactory.getLogger(StandardTestingService.class);
    private final ConfigurationLocator configurationLocator;

    public StandardTestingService() {
        configurationLocator = new ConfigurationLocator();
    }

    @Override
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

    public String[] getAllSettingsFilenames() throws IOException {
        ArrayList<String> settingsFilenames = new ArrayList<String>();
        settingsFilenames.add(configurationLocator.getFilePath(getDefaultSettingsFilename(getTestMode())));
        try {
            String optionalOverrides = configurationLocator.getFilePath(FilePaths.LOCAL_CONFIGURATION_OVERRIDES);
            settingsFilenames.add(optionalOverrides);
        } catch (FileNotFoundException e) {
            // basically ignore; no matter if they don't have local overrides
            logger.info("no local overrides in use.");
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
            throw new RuntimeException("illegal mifos.mode");
        }
        return defaultSettingsFilename;
    }

    /**
     * Provides for re-initialization of certain custom Mifos caches. Necessary since acceptance tests may truncate and
     * reload data directly in a Mifos database without the application's knowledge, causing the application to behave
     * unexpectedly. Only allowed during acceptance tests.
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
            MifosConfigurationManager configMgr = MifosConfigurationManager.getInstance();
            configMgr.setProperty("Localization.LanguageCode", languageCode);
            configMgr.setProperty("Localization.CountryCode", countryCode);
        } catch (MifosRuntimeException e) {
            throw new MifosException("The locale " + languageCode + "_" + countryCode + " is not supported by Mifos.");
        }
    }

    @Override
    public void setAccountingRules(String accountingRulesParamName, String accountingRulesParamValue)
            throws MifosException {
        MifosConfigurationManager configMgr = MifosConfigurationManager.getInstance();
        if (accountingRulesParamValue == null || accountingRulesParamValue.equals("")) {
            configMgr.clearProperty(accountingRulesParamName);
            return;
        }
        configMgr.setProperty(accountingRulesParamName, accountingRulesParamValue);

    }

    @Override
    public void setFiscalCalendarRules(String workingDays, String ScheduleMeetingIfNonWorkingDay)
            throws MifosException {
        if (isSet(workingDays)) {
            new FiscalCalendarRules().setWorkingDays(workingDays);
        }
        if (isSet(ScheduleMeetingIfNonWorkingDay)) {
            new FiscalCalendarRules().setScheduleTypeForMeetingIfNonWorkingDay(ScheduleMeetingIfNonWorkingDay);
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
    public void setCenterHierarchyExists(boolean flag) {
        ClientRules.setCenterHierarchyExists(flag);
        MifosConfigurationManager.getInstance().setProperty(ClientRules.ClientRulesCenterHierarchyExists, flag);
        // we need to rebuild menus after changing this setting
        MenuRepository.getInstance().removeMenuForAllLocale();
        MenuRepository.getInstance().setCrudeMenu(null);
    }

    @Override
    public void setGroupCanApplyLoans(boolean flag) {
        ClientRules.setGroupCanApplyLoans(flag);
        MifosConfigurationManager.getInstance().setProperty(ClientRules.ClientRulesGroupCanApplyLoans, flag);
    }

    @Override
    public void setClientCanExistOutsideGroup(boolean flag){
        ClientRules.setClientCanExistOutsideGroup(flag);
        MifosConfigurationManager.getInstance().setProperty(ClientRules.ClientRulesClientCanExistOutsideGroup, flag);
    }

    @Override
    public void runAllBatchJobs(final ServletContext ctx) {
        logger.info("running all batch jobs");
        MifosScheduler mifosScheduler = (MifosScheduler) ctx.getAttribute(MifosScheduler.class.getName());
        try {
            mifosScheduler.runAllTasks();
        } catch (TaskSystemException se) {
            throw new MifosRuntimeException("Scheduler's inner exception while running all batch jobs!", se);
        }
    }

    @Override
    public void runIndividualBatchJob(final String requestedJob, final ServletContext ctx) throws MifosException {
        logger.info("running batch job with name: " + requestedJob);
        boolean jobFound = false;
        String jobToRun = null;
        final MifosScheduler mifosScheduler = (MifosScheduler) ctx.getAttribute(MifosScheduler.class.getName());
        try {
            for(String taskName : mifosScheduler.getTaskNames()) {
                if(taskName.equals(requestedJob)) {
                    jobFound = true;
                    jobToRun = taskName;
                    break;
                }
            }
            if (!jobFound) {
                throw new IllegalArgumentException(requestedJob + " is unknown and will not be executed.");
            }
            mifosScheduler.runIndividualTask(jobToRun);
        } catch(TaskSystemException se) {
            throw new MifosException("Scheduler's inner exception while running individual batch job!", se);
        }
    }

    @Override
    public void setImport(String importParamName, String importParamValue) throws MifosException {
        MifosConfigurationManager configMgr = MifosConfigurationManager.getInstance();
        if (importParamValue == null || importParamValue.equals("")) {
            configMgr.clearProperty(importParamName);
            return;
        }
        configMgr.setProperty(importParamName, importParamValue);
    }

    @Override
    public void setProcessFlow(String processFlowParamName, String processFlowParamValue)
            throws MifosException {
        MifosConfigurationManager configMgr = MifosConfigurationManager.getInstance();
        if (processFlowParamValue == null || processFlowParamValue.equals("")) {
            configMgr.clearProperty(processFlowParamName);
            return;
        }
        configMgr.setProperty(processFlowParamName, processFlowParamValue);

    }

    @Override
    public void setBackDatedTransactionsAllowed(boolean flag) {
        MifosConfigurationManager.getInstance().setProperty("BackDatedTransactionsAllowed", flag);
    }

    @Override
    public void setClientNameSequence(String[] nameSequence) {
        ClientRules.setNameSequence(nameSequence);
    }
}
