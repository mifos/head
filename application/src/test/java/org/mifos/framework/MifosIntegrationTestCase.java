/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.ComparisonFailure;

import org.dbunit.Assertion;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.filter.ExcludeTableFilter;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.hibernate.SessionFactory;
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
import org.mifos.customers.personnel.persistence.LegacyPersonnelDao;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.AuditInterceptorFactory;
import org.mifos.framework.hibernate.helper.DatabaseDependentTest;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.ConfigurationLocator;
import org.mifos.framework.util.StandardTestingService;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestCaseInitializer;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.service.test.TestMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Log4jConfigurer;

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
@ContextConfiguration(locations = { "/integration-test-context.xml",
                                    "/org/mifos/config/resources/applicationContext.xml",
                                    "/org/mifos/config/resources/apponly-services.xml",
                                    "classpath*:META-INF/spring/DbUpgradeContext.xml"})
public class MifosIntegrationTestCase {

    private static Boolean isTestingModeSet = false;

    private static IDataSet latestDataDump;

    @Autowired
    protected LegacyPersonnelDao legacyPersonnelDao;

    @Autowired
    protected SessionFactory sessionFactory;

    /**
     * This is a switch to enable verification of database (cleanup) at the end of an integration tests. i.e. if a test
     * leaves database in dirty state (not rolling back properly) then it will force it to fail. This is used for
     * figuring out which test leaving database in inconsistent state for an another test causing it to fail.
     */
    protected static boolean verifyDatabaseState;

    protected static ExcludeTableFilter excludeTables = new ExcludeTableFilter();

    private static String savedFiscalCalendarRulesWorkingDays;

    @BeforeClass
    public static void init() throws Exception {
        Log4jConfigurer.initLogging(new ConfigurationLocator().getFilePath(FilePaths.LOG_CONFIGURATION_FILE));
        verifyDatabaseState = false;
        if (!isTestingModeSet) {
            new StandardTestingService().setTestMode(TestMode.INTEGRATION);
            isTestingModeSet = true;
        }
    }

    @Before
    public void before() throws Exception {
        new TestCaseInitializer().initialize(sessionFactory);
        dbVerificationSetUp();
        DatabaseDependentTest.before(new AuditInterceptorFactory(), sessionFactory);
        Money.setDefaultCurrency(TestUtils.RUPEE);
    }

    @After
    public void after() throws Exception {
        diableCustomWorkingDays();
        TestUtils.dereferenceObjects(this);
        DatabaseDependentTest.after(new AuditInterceptorFactory(), sessionFactory);
        dbVerificationTearDown();
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
            return legacyPersonnelDao.getPersonnel(PersonnelConstants.SYSTEM_USER);
        } catch (PersistenceException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * Gets the test data user personnel_id == 3
     */
    protected PersonnelBO getTestUser() {
        try {
            return legacyPersonnelDao.getPersonnel(PersonnelConstants.TEST_USER);
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

    private void dbVerificationSetUp() throws Exception {
        if (verifyDatabaseState) {
            excludeTables.excludeTable("BATCH_JOB_EXECUTION");

            Connection connection = StaticHibernateUtil.getSessionTL().connection();
            connection.setAutoCommit(false);
            DatabaseConnection dbUnitConnection = new DatabaseConnection(connection);
            latestDataDump = new FilteredDataSet(excludeTables, dbUnitConnection.createDataSet());
            String tmpDir = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator");
            FlatXmlDataSet.write(latestDataDump, new FileOutputStream(tmpDir + "latestDataDump.xml"));
            FlatXmlDataSetBuilder fxmlBuilder = new FlatXmlDataSetBuilder();
            latestDataDump = fxmlBuilder.build(new File(tmpDir + "latestDataDump.xml"));
        }
    }

    private void dbVerificationTearDown() throws Exception,
            FileNotFoundException, MalformedURLException {
        if (verifyDatabaseState) {
            Connection connection = StaticHibernateUtil.getSessionTL().connection();
            connection.setAutoCommit(false);
            DatabaseConnection dbUnitConnection = new DatabaseConnection(connection);
            IDataSet upgradeDataDump = new FilteredDataSet(excludeTables, dbUnitConnection.createDataSet());
            String tmpDir = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator");
            FlatXmlDataSet.write(upgradeDataDump, new FileOutputStream(tmpDir + "upgradeDataDump.xml"));
            FlatXmlDataSetBuilder fxmlBuilder = new FlatXmlDataSetBuilder();
            upgradeDataDump = fxmlBuilder.build(new File(tmpDir + "upgradeDataDump.xml"));
            Assertion.assertEquals(latestDataDump, upgradeDataDump);
        }
    }

    public MifosCurrency getCurrency() {
        // TODO: will be replaced by a better way to get currency for integration tests
        // NOTE: TestObjectFactory.getCurrency also exists
        return Money.getDefaultCurrency();
    }
}