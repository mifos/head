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

package org.mifos.framework;

import junit.framework.ComparisonFailure;
import org.dbunit.Assertion;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.filter.ExcludeTableFilter;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.hibernate.stat.Statistics;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.DatabaseDependentTest;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.ConfigurationLocator;
import org.mifos.framework.util.StandardTestingService;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestCaseInitializer;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.service.test.TestMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Log4jConfigurer;

import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *  All classes extending this class must be names as <b>*IntegrationTest.java</b> to support maven-surefire-plugin autofind
 * feature.
 * <br />
 * <br />
 * This base class initializes the database and various other things and so any class derived from this is an
 * integration test. If a test is not an integration test and does not need the database, then it should not derive from
 * this class.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/integration-test-context.xml", "/org/mifos/config/resources/applicationContext.xml"})
public class MifosIntegrationTestCase {

    private static Boolean isTestingModeSet = false;

    private static IDataSet latestDataDump;

    protected static ExcludeTableFilter excludeTables = new ExcludeTableFilter();

    private static String savedFiscalCalendarRulesWorkingDays;

    @BeforeClass
    public static void init() throws Exception {
        Log4jConfigurer.initLogging(new ConfigurationLocator().getFilePath(FilePaths.LOG_CONFIGURATION_FILE));
        excludeTables.excludeTable("config_key_value_integer");
        excludeTables.excludeTable("personnel");
        excludeTables.excludeTable("meeting");
        excludeTables.excludeTable("recurrence_detail");
        excludeTables.excludeTable("recur_on_day");

        if (!isTestingModeSet) {
            new StandardTestingService().setTestMode(TestMode.INTEGRATION);
            isTestingModeSet = true;
        }
    }

    @Before
    public void before() throws Exception {
        new TestCaseInitializer().initialize();
        Money.setDefaultCurrency(TestUtils.RUPEE);
        DatabaseDependentTest.before();
    }

    @After
    public void after() throws Exception {
        diableCustomWorkingDays();
        TestUtils.dereferenceObjects(this);
        DatabaseDependentTest.after();
    }

    private Statistics statisticsService;

    protected void assertEquals(String s, Money one, Money two) {
        if (one.equals(two)) {
            return;
        }
        throw new ComparisonFailure(s, one.toString(), two.toString());
    }

    protected Date getDate(String date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        return format.parse(date);
    }

    protected Statistics getStatisticsService() {
        return this.statisticsService;
    }

    protected void setStatisticsService(Statistics service) {
        this.statisticsService = service;
    }

    protected void initializeStatisticsService() {
        statisticsService = StaticHibernateUtil.getSessionFactory().getStatistics();
        statisticsService.setStatisticsEnabled(true);
    }

    /*
     * Gets the test data office with office_id == 1
     */
    protected OfficeBO getHeadOffice() {
        try {
            return new OfficePersistence().getOffice(TestObjectFactory.HEAD_OFFICE);
        } catch (PersistenceException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * Gets the test data office with office_id == 3
     */
    protected OfficeBO getBranchOffice() {
        try {
            return new OfficePersistence().getOffice(TestObjectFactory.SAMPLE_BRANCH_OFFICE);
        } catch (PersistenceException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * Gets the test data user personnel_id == 1
     */
    protected PersonnelBO getSystemUser() {
        try {
            return new PersonnelPersistence().getPersonnel(PersonnelConstants.SYSTEM_USER);
        } catch (PersistenceException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * Gets the test data user personnel_id == 3
     */
    protected PersonnelBO getTestUser() {
        try {
            return new PersonnelPersistence().getPersonnel(PersonnelConstants.TEST_USER);
        } catch (PersistenceException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * see MIFOS-2659 <br><br>
     * This will be disabled automatically at the end of a test case
     *
     */
    protected static void enableCustomWorkingDays() {
        savedFiscalCalendarRulesWorkingDays = new FiscalCalendarRules().getWorkingDaysAsString();
        new FiscalCalendarRules().setWorkingDays("MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY,SUNDAY");
    }

    /**
     * see MIFOS-2659
     */
    private static void diableCustomWorkingDays() {
        if(savedFiscalCalendarRulesWorkingDays != null) {
        new FiscalCalendarRules().setWorkingDays(savedFiscalCalendarRulesWorkingDays);
        }
        savedFiscalCalendarRulesWorkingDays = null;
    }

    public MifosCurrency getCurrency() {
        // TODO: will be replaced by a better way to get currency for integration tests
        // NOTE: TestObjectFactory.getCurrency also exists
        return Money.getDefaultCurrency();
    }
}
