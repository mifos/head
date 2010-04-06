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

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.collectionsheet.persistence.ClientBuilder;
import org.mifos.application.collectionsheet.persistence.GroupBuilder;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.calendar.DayOfWeek;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.StandardTestingService;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DatabaseSetup;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.Money;
import org.mifos.service.test.TestMode;
import org.mifos.test.framework.util.DatabaseCleaner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/integration-test-context.xml", "/hibernate-daos.xml", "/services.xml" })
public class GroupAtTopOfCustomerHierarchyIntegrationTest {

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    private static MifosCurrency oldDefaultCurrency;

    private PersonnelBO existingUser;
    private PersonnelBO existingLoanOfficer;
    private OfficeBO existingOffice;
    private MeetingBO existingMeeting;
    private GroupBO existingGroup;
    private ClientBO existingActiveClient;
    private ClientBO existingOnHoldClient;
    private ClientBO existingCancelledClient;

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
    public void cleanDatabaseTables() throws Exception {
        databaseCleaner.clean();

        existingUser = IntegrationTestObjectMother.testUser();
        existingLoanOfficer = IntegrationTestObjectMother.testUser();
        existingOffice = IntegrationTestObjectMother.sampleBranchOffice();

        DateTime startDate = new DateTime().withYear(2010).withMonthOfYear(4).withDayOfMonth(5).withDayOfWeek(DayOfWeek.monday()).toDateMidnight().toDateTime();

        existingMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).startingFrom(startDate.toDate()).occuringOnA(WeekDay.MONDAY).build();
        IntegrationTestObjectMother.saveMeeting(existingMeeting);

        existingGroup = new GroupBuilder().withName("newGroup")
                                            .withMeeting(existingMeeting)
                                            .withOffice(existingOffice)
                                            .withLoanOfficer(existingLoanOfficer)
                                            .withStatus(CustomerStatus.GROUP_ACTIVE)
                                            .formedBy(existingUser).buildAsTopOfHierarchy();
        IntegrationTestObjectMother.createGroup(existingGroup, existingMeeting);

        existingActiveClient = new ClientBuilder().withStatus(CustomerStatus.CLIENT_ACTIVE).withParentCustomer(existingGroup).buildForIntegrationTests();
        IntegrationTestObjectMother.createClient(existingActiveClient, existingMeeting);

        existingOnHoldClient = new ClientBuilder().withStatus(CustomerStatus.CLIENT_HOLD).withParentCustomer(existingGroup).buildForIntegrationTests();
        IntegrationTestObjectMother.createClient(existingOnHoldClient, existingMeeting);

        existingCancelledClient = new ClientBuilder().withStatus(CustomerStatus.CLIENT_CANCELLED).withParentCustomer(existingGroup).buildForIntegrationTests();
        IntegrationTestObjectMother.createClient(existingCancelledClient, existingMeeting);
    }

    @Test
    public void meetingOfGroupAndAllChildrenIsUpdatedAndStoredForUpdatingLater() throws Exception {

        existingGroup = customerDao.findGroupBySystemId(existingGroup.getGlobalCustNum());
        existingActiveClient = customerDao.findClientBySystemId(existingActiveClient.getGlobalCustNum());
        existingOnHoldClient = customerDao.findClientBySystemId(existingOnHoldClient.getGlobalCustNum());
        existingCancelledClient = customerDao.findClientBySystemId(existingCancelledClient.getGlobalCustNum());

        // pre-verification
        assertThat(existingGroup.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay(), is(WeekDay.MONDAY));
        assertThat(existingGroup.getCustomerMeeting().getUpdatedFlag(), is(Constants.NO));

        assertThat(existingActiveClient.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay(), is(WeekDay.MONDAY));
        assertThat(existingActiveClient.getCustomerMeeting().getUpdatedFlag(), is(Constants.NO));

        assertThat(existingOnHoldClient.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay(), is(WeekDay.MONDAY));
        assertThat(existingOnHoldClient.getCustomerMeeting().getUpdatedFlag(), is(Constants.NO));

        assertThat(existingCancelledClient.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay(), is(WeekDay.MONDAY));
        assertThat(existingCancelledClient.getCustomerMeeting().getUpdatedFlag(), is(Constants.NO));

        // setup
        MeetingBO newMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).occuringOnA(WeekDay.FRIDAY).startingFrom(new DateTime().minusDays(3).toDate()).build();

        // exercise test
        existingGroup.updateMeeting(newMeeting);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        // verification
        assertThat(existingGroup.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails().getWeekDay(), is(WeekDay.FRIDAY));
        assertThat(existingGroup.getCustomerMeeting().getUpdatedFlag(), is(Constants.YES));
        assertThat(existingActiveClient.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails().getWeekDay(), is(WeekDay.FRIDAY));
        assertThat(existingActiveClient.getCustomerMeeting().getUpdatedFlag(), is(Constants.YES));
        assertThat(existingOnHoldClient.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails().getWeekDay(), is(WeekDay.FRIDAY));
        assertThat(existingOnHoldClient.getCustomerMeeting().getUpdatedFlag(), is(Constants.YES));
        assertThat(existingCancelledClient.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails().getWeekDay(), is(WeekDay.FRIDAY));
        assertThat(existingCancelledClient.getCustomerMeeting().getUpdatedFlag(), is(Constants.YES));
    }

    @Test
    public void meetingOfGroupAndAllChildrenIsChanged() throws Exception {

        existingGroup = customerDao.findGroupBySystemId(existingGroup.getGlobalCustNum());
        existingActiveClient = customerDao.findClientBySystemId(existingActiveClient.getGlobalCustNum());
        existingOnHoldClient = customerDao.findClientBySystemId(existingOnHoldClient.getGlobalCustNum());
        existingCancelledClient = customerDao.findClientBySystemId(existingCancelledClient.getGlobalCustNum());

        // pre-verification
        assertThat(existingGroup.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay(), is(WeekDay.MONDAY));
        assertThat(existingGroup.getCustomerMeeting().getUpdatedFlag(), is(Constants.NO));

        assertThat(existingActiveClient.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay(), is(WeekDay.MONDAY));
        assertThat(existingActiveClient.getCustomerMeeting().getUpdatedFlag(), is(Constants.NO));

        assertThat(existingOnHoldClient.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay(), is(WeekDay.MONDAY));
        assertThat(existingOnHoldClient.getCustomerMeeting().getUpdatedFlag(), is(Constants.NO));

        assertThat(existingCancelledClient.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay(), is(WeekDay.MONDAY));
        assertThat(existingCancelledClient.getCustomerMeeting().getUpdatedFlag(), is(Constants.NO));

        // setup
        MeetingBO newMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).occuringOnA(WeekDay.FRIDAY).startingFrom(new DateTime().minusDays(3).toDate()).build();

        // exercise test
        existingGroup.updateMeeting(newMeeting);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        existingGroup.changeUpdatedMeeting();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        // verification
        assertThat(existingGroup.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay(), is(WeekDay.FRIDAY));
        assertThat(existingGroup.getCustomerMeeting().getUpdatedFlag(), is(Constants.YES));
        assertThat(existingActiveClient.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay(), is(WeekDay.FRIDAY));
        assertThat(existingActiveClient.getCustomerMeeting().getUpdatedFlag(), is(Constants.YES));
        assertThat(existingOnHoldClient.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay(), is(WeekDay.FRIDAY));
        assertThat(existingOnHoldClient.getCustomerMeeting().getUpdatedFlag(), is(Constants.YES));
        assertThat(existingCancelledClient.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay(), is(WeekDay.FRIDAY));
        assertThat(existingCancelledClient.getCustomerMeeting().getUpdatedFlag(), is(Constants.YES));
    }
}