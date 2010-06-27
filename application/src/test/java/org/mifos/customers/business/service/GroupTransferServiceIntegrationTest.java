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

package org.mifos.customers.business.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMotherBuilderDsl.aWeeklyMeeting;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMotherBuilderDsl.anActiveCenter;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMotherBuilderDsl.anExistingActiveCenter;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMotherBuilderDsl.anExistingBranchOffice;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMotherBuilderDsl.anExistingGroupUnderCenterInDifferentBranchAs;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMotherBuilderDsl.anExistingLoanOfficer;

import java.util.Locale;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.application.collectionsheet.persistence.ClientBuilder;
import org.mifos.application.collectionsheet.persistence.OfficeBuilder;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.config.Localization;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.audit.util.helpers.AuditConfigurtion;
import org.mifos.framework.util.StandardTestingService;
import org.mifos.framework.util.helpers.DatabaseSetup;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.Money;
import org.mifos.service.test.TestMode;
import org.mifos.test.framework.util.DatabaseCleaner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Most {@link CustomerService#transferGroupTo(GroupBO, CenterBO)} functionality is covered in unit tests but
 * added these high value tests for now to verify that 'schedule regeneration works correctly' when transferring groups.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/integration-test-context.xml",
                                    "/org/mifos/config/resources/hibernate-daos.xml",
                                    "/org/mifos/config/resources/services.xml" })
public class GroupTransferServiceIntegrationTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    private static MifosCurrency oldDefaultCurrency;

    @BeforeClass
    public static void initialiseHibernateUtil() {

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

        Locale locale = Localization.getInstance().getMainLocale();
        AuditConfigurtion.init(locale);
    }

    @Test
    public void transferingGroupToCenterWithDifferentMeetingDayOfWeekCausesFutureSchedulesToBeRegeneratedForGroup() throws Exception {

        // setup
        OfficeBO branchOffice = anExistingBranchOffice(new OfficeBuilder().withGlobalOfficeNum("xxxx-003").withName("builder-branch-office1").withShortName("bf1").withSearchId("1.1.1.1."));

        OfficeBO branchOffice2 = new OfficeBuilder().withParentOffice(branchOffice.getParentOffice()).branchOffice().withGlobalOfficeNum("xxxx-004").withName("builder-branch-office2").withShortName("bf1").withSearchId("1.1.1.2.").build();
        IntegrationTestObjectMother.createOffice(branchOffice2);

        DateTime todaySixWeeksAgo = new DateMidnight().toDateTime().minusWeeks(6);
        MeetingBO weeklyMeeting = aWeeklyMeeting().withLocation("weeklyMeeting-sixWeeksAgo").startingFrom(todaySixWeeksAgo.toDate()).build();
        IntegrationTestObjectMother.saveMeeting(weeklyMeeting);

        CenterBuilder centerWithWeeklyMeeting = anActiveCenter().withName("center-with-no-children")
                                                                .with(weeklyMeeting)
                                                                .with(branchOffice)
                                                                .withActivationDate(todaySixWeeksAgo)
                                                                .withLoanOfficer(anExistingLoanOfficer());

        CenterBO centerWithNoChildren = anExistingActiveCenter(centerWithWeeklyMeeting);


        DateTime tomorrowTwoWeeksAgo = new DateMidnight().toDateTime().minusWeeks(2).plusDays(1);
        MeetingBO differentWeeklyMeeting = aWeeklyMeeting().withLocation("weeklyMeeting-tomorrow-twoWeeksAgo").withStartDate(tomorrowTwoWeeksAgo.minusWeeks(1)).build();
        IntegrationTestObjectMother.saveMeeting(differentWeeklyMeeting);

        CenterBuilder centerInDifferentBranchWithDifferentMeeting = centerWithWeeklyMeeting.withName("center-with-group")
                                                                                           .with(branchOffice2)
                                                                                           .with(differentWeeklyMeeting)
                                                                                           .withActivationDate(tomorrowTwoWeeksAgo);
        GroupBO groupForTransfer = anExistingGroupUnderCenterInDifferentBranchAs(centerInDifferentBranchWithDifferentMeeting);

        // pre-verification
        assertThat(groupForTransfer.getStatus().isGroupActive(), is(true));
        TestUtils.assertThatAllCustomerSchedulesOccuringAfterCurrentInstallmentPeriodFallOnDayOfWeek(groupForTransfer, WeekDay.getJodaWeekDay(tomorrowTwoWeeksAgo.getDayOfWeek()));

        // exercise test
        customerService.transferGroupTo(groupForTransfer, centerWithNoChildren);

        // verification
        groupForTransfer = customerDao.findGroupBySystemId(groupForTransfer.getGlobalCustNum());

        assertThat(groupForTransfer.getStatus().isGroupActive(), is(false));
        TestUtils.assertThatAllCustomerSchedulesOccuringBeforeOrOnCurrentInstallmentPeriodRemainUnchanged(groupForTransfer, WeekDay.getJodaWeekDay(tomorrowTwoWeeksAgo.getDayOfWeek()));
        TestUtils.assertThatAllCustomerSchedulesOccuringAfterCurrentInstallmentPeriodFallOnDayOfWeek(groupForTransfer, WeekDay.getJodaWeekDay(todaySixWeeksAgo.getDayOfWeek()));
    }

    @Test
    public void transferingGroupToCenterWithDifferentMeetingDayOfWeekCausesFutureSchedulesToBeRegeneratedForGroupsChildren() throws Exception {

        // setup
        OfficeBO branchOffice = anExistingBranchOffice(new OfficeBuilder().withGlobalOfficeNum("xxxx-003").withName("builder-branch-office1").withShortName("bf1").withSearchId("1.1.1.1."));

        OfficeBO branchOffice2 = new OfficeBuilder().withParentOffice(branchOffice.getParentOffice()).branchOffice().withGlobalOfficeNum("xxxx-004").withName("builder-branch-office2").withShortName("bf1").withSearchId("1.1.1.2.").build();
        IntegrationTestObjectMother.createOffice(branchOffice2);

        DateTime todaySixWeeksAgo = new DateMidnight().toDateTime().minusWeeks(6);
        MeetingBO weeklyMeeting = aWeeklyMeeting().withLocation("weeklyMeeting-sixWeeksAgo").startingFrom(todaySixWeeksAgo.toDate()).build();
        IntegrationTestObjectMother.saveMeeting(weeklyMeeting);

        CenterBuilder centerWithWeeklyMeeting = anActiveCenter().withName("center-with-no-children")
                                                                .with(weeklyMeeting)
                                                                .with(branchOffice)
                                                                .withActivationDate(todaySixWeeksAgo)
                                                                .withLoanOfficer(anExistingLoanOfficer());

        CenterBO centerWithNoChildren = anExistingActiveCenter(centerWithWeeklyMeeting);


        DateTime tomorrowTwoWeeksAgo = new DateMidnight().toDateTime().minusWeeks(2).plusDays(1);
        MeetingBO differentWeeklyMeeting = aWeeklyMeeting().withLocation("weeklyMeeting-tomorrow-twoWeeksAgo").withStartDate(tomorrowTwoWeeksAgo.minusWeeks(1)).build();
        IntegrationTestObjectMother.saveMeeting(differentWeeklyMeeting);

        CenterBuilder centerInDifferentBranchWithDifferentMeeting = centerWithWeeklyMeeting.withName("center-with-group")
                                                                                           .with(branchOffice2)
                                                                                           .with(differentWeeklyMeeting)
                                                                                           .withActivationDate(tomorrowTwoWeeksAgo);
        GroupBO groupForTransfer = anExistingGroupUnderCenterInDifferentBranchAs(centerInDifferentBranchWithDifferentMeeting);

        ClientBO child1 = new ClientBuilder().active().withName("client1").withParentCustomer(groupForTransfer).buildForIntegrationTests();
        IntegrationTestObjectMother.createClient(child1, child1.getCustomerMeetingValue());

        // pre-verification
        ClientBO client = customerDao.findClientBySystemId(child1.getGlobalCustNum());
        TestUtils.assertThatAllCustomerSchedulesOccuringAfterCurrentInstallmentPeriodFallOnDayOfWeek(client, WeekDay.getJodaWeekDay(tomorrowTwoWeeksAgo.getDayOfWeek()));

        // exercise test
        groupForTransfer = customerDao.findGroupBySystemId(groupForTransfer.getGlobalCustNum());
        groupForTransfer.setUserContext(TestUtils.makeUser());
        customerService.transferGroupTo(groupForTransfer, centerWithNoChildren);

        // verification
        client = customerDao.findClientBySystemId(child1.getGlobalCustNum());

        TestUtils.assertThatAllCustomerSchedulesOccuringBeforeOrOnCurrentInstallmentPeriodRemainUnchanged(client, WeekDay.getJodaWeekDay(tomorrowTwoWeeksAgo.getDayOfWeek()));
        TestUtils.assertThatAllCustomerSchedulesOccuringAfterCurrentInstallmentPeriodFallOnDayOfWeek(client, WeekDay.getJodaWeekDay(todaySixWeeksAgo.getDayOfWeek()));
    }
}