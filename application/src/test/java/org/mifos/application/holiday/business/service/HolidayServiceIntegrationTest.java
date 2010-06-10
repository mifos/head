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

package org.mifos.application.holiday.business.service;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.collectionsheet.persistence.OfficeBuilder;
import org.mifos.application.holiday.persistence.HolidayDetails;
import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.config.Localization;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.audit.util.helpers.AuditConfigurtion;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ValidationException;
import org.mifos.framework.util.StandardTestingService;
import org.mifos.framework.util.helpers.DatabaseSetup;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.Money;
import org.mifos.service.test.TestMode;
import org.mifos.test.framework.util.DatabaseCleaner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/integration-test-context.xml",
                                    "/org/mifos/config/resources/hibernate-daos.xml",
                                    "/org/mifos/config/resources/services.xml" })
public class HolidayServiceIntegrationTest {

    @Autowired
    private HolidayService holidayService;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    private OfficeBO headOffice;
    private OfficeBO regionalOffice;
    private OfficeBO areaOffice;
    private OfficeBO branch1;
    private OfficeBO branch2;
    private OfficeBO branch3;

    private static MifosCurrency oldDefaultCurrency;

    @BeforeClass
    public static void initialiseHibernateUtil() {

        Locale locale = Localization.getInstance().getMainLocale();
        AuditConfigurtion.init(locale);

        oldDefaultCurrency = Money.getDefaultCurrency();
        Money.setDefaultCurrency(TestUtils.RUPEE);
        new StandardTestingService().setTestMode(TestMode.INTEGRATION);
        DatabaseSetup.initializeHibernate();
    }

    @AfterClass
    public static void resetCurrency() {
        Money.setDefaultCurrency(oldDefaultCurrency);
    }

    @After
    public void cleanDatabaseTablesAfterTest() {
        // NOTE: - only added to stop older integration tests failing due to brittleness
        databaseCleaner.clean();
    }

    @Before
    public void cleanDatabaseTables() {
        databaseCleaner.clean();

        createOfficeHierarchy();
    }

    @Test(expected = ValidationException.class)
    public void shouldFailToCreateHolidayAgainstOfficesOfDifferentLevelThatExistInSameOfficeHierarchySubTree() throws ApplicationException {

        // setup
        HolidayDetails holidayDetails = new HolidayDetails("test", new DateTime().plusDays(1).toDate(), new DateTime().plusDays(1).toDate(), RepaymentRuleTypes.NEXT_MEETING_OR_REPAYMENT);
        List<Short> officeIds = Arrays.asList(headOffice.getOfficeId(), regionalOffice.getOfficeId());

        // exercise test
        holidayService.create(holidayDetails, officeIds);
    }

    @Test
    public void shouldCreateHolidayAgainstOfficesOfDifferentLevelThatDoNotExistInSameOfficeHierarchySubTree() throws ApplicationException {

        // setup
        HolidayDetails holidayDetails = new HolidayDetails("test", new DateTime().plusDays(1).toDate(), new DateTime().plusDays(1).toDate(), RepaymentRuleTypes.NEXT_MEETING_OR_REPAYMENT);
        List<Short> officeIds = Arrays.asList(areaOffice.getOfficeId(), branch3.getOfficeId());

        // exercise test
        holidayService.create(holidayDetails, officeIds);
    }

    @Test
    public void shouldCreateHolidayAgainstOfficesOfDifferentLevelThatDoNotExistInSameOfficeHierarchySubTree_Allbranches() throws ApplicationException {

        // setup
        HolidayDetails holidayDetails = new HolidayDetails("test", new DateTime().plusDays(1).toDate(), new DateTime().plusDays(1).toDate(), RepaymentRuleTypes.NEXT_MEETING_OR_REPAYMENT);
        List<Short> officeIds = Arrays.asList(branch1.getOfficeId(), branch2.getOfficeId(), branch3.getOfficeId());

        // exercise test
        holidayService.create(holidayDetails, officeIds);
    }

    public void createOfficeHierarchy() {
        headOffice = new OfficeBuilder().withGlobalOfficeNum("001").withName("headOffice").headOffice().withParentOffice(null).build();
        IntegrationTestObjectMother.createOffice(headOffice);

        regionalOffice = new OfficeBuilder().withGlobalOfficeNum("002").withName("region1").regionalOffice().withParentOffice(headOffice).build();
        IntegrationTestObjectMother.createOffice(regionalOffice);

        OfficeBO subRegionalOffice = new OfficeBuilder().withGlobalOfficeNum("003").withName("sub1-of-region1").subRegionalOffice().withParentOffice(regionalOffice).build();
        IntegrationTestObjectMother.createOffice(subRegionalOffice);

        areaOffice = new OfficeBuilder().withGlobalOfficeNum("004").withName("area-of-sub1-regional").areaOffice().withParentOffice(subRegionalOffice).build();
        IntegrationTestObjectMother.createOffice(areaOffice);

        branch1 = new OfficeBuilder().withGlobalOfficeNum("005").withName("branch1-of-area").branchOffice().withParentOffice(areaOffice).build();
        IntegrationTestObjectMother.createOffice(branch1);

        branch2 = new OfficeBuilder().withGlobalOfficeNum("006").withName("branch2-of-area").branchOffice().withParentOffice(areaOffice).build();
        IntegrationTestObjectMother.createOffice(branch2);

        branch3 = new OfficeBuilder().withGlobalOfficeNum("007").withName("branch1-of-regional").branchOffice().withParentOffice(regionalOffice).build();
        IntegrationTestObjectMother.createOffice(branch3);
    }
}