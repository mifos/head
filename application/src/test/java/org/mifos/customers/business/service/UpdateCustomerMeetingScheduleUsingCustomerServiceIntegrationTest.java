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

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.application.collectionsheet.persistence.GroupBuilder;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.servicefacade.MeetingUpdateRequest;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerScheduleEntity;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.domain.builders.MeetingUpdateRequestBuilder;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.StandardTestingService;
import org.mifos.framework.util.helpers.DatabaseSetup;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.authorization.AuthorizationManager;
import org.mifos.security.util.UserContext;
import org.mifos.service.test.TestMode;
import org.mifos.test.framework.util.DatabaseCleaner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/integration-test-context.xml",
                                    "/org/mifos/config/resources/hibernate-daos.xml",
                                    "/org/mifos/config/resources/services.xml" })
public class UpdateCustomerMeetingScheduleUsingCustomerServiceIntegrationTest {

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
    public void cleanDatabaseTables() throws Exception {
        databaseCleaner.clean();

        AuthorizationManager.getInstance().init();
    }

    @Ignore
    @Test
    public void shouldRescheduleSchedulesThatFallOnOrAfterDateMeetingIsRescheduled() throws Exception {

        // setup
        PersonnelBO existingLoanOfficer = IntegrationTestObjectMother.testUser();
        OfficeBO existingOffice = IntegrationTestObjectMother.sampleBranchOffice();


        DateTime fiveWeeksAgo = new DateTime().minusWeeks(5);
        MeetingBO existingMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).startingFrom(fiveWeeksAgo.toDate()).build();
        IntegrationTestObjectMother.saveMeeting(existingMeeting);

        CenterBO existingCenter = new CenterBuilder().withName("Center-IntegrationTest")
                                                     .with(existingMeeting)
                                                     .with(existingOffice)
                                                     .withLoanOfficer(existingLoanOfficer)
                                                     .withUserContext()
                                                     .build();
        IntegrationTestObjectMother.createCenter(existingCenter, existingMeeting);
        MeetingUpdateRequest meetingUpdateRequest = new MeetingUpdateRequestBuilder().withCustomerId(existingCenter.getCustomerId())
                                                                                     .with(WeekDay.WEDNESDAY)
                                                                                     .withMeetingPlace("town hall")
                                                                                     .build();

        UserContext userContext = TestUtils.makeUser();
        userContext.setBranchId(existingOffice.getOfficeId());

        // pre-verification
        CenterBO createdCenter = customerDao.findCenterBySystemId(existingCenter.getGlobalCustNum());
        assertThatAllCustomerSchedulesOccuringOnOrAfterTodayFallOnDayOfWeek(createdCenter, WeekDay.MONDAY);

        // exercise test
        customerService.updateCustomerMeetingSchedule(meetingUpdateRequest, userContext);

        // verification
        CenterBO modifiedCenter = customerDao.findCenterBySystemId(existingCenter.getGlobalCustNum());
        assertThatAllCustomerSchedulesOccuringOnOrAfterTodayFallOnDayOfWeek(modifiedCenter, WeekDay.WEDNESDAY);
    }

    @Ignore
    @Test
    public void givenCenterGroupHierarchyShouldRescheduleSchedulesThatFallOnOrAfterDateMeetingIsRescheduled() throws Exception {

        // setup
        PersonnelBO existingLoanOfficer = IntegrationTestObjectMother.testUser();
        OfficeBO existingOffice = IntegrationTestObjectMother.sampleBranchOffice();


        DateTime fiveWeeksAgo = new DateTime().minusWeeks(5);
        MeetingBO existingMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).startingFrom(fiveWeeksAgo.toDate()).build();
        IntegrationTestObjectMother.saveMeeting(existingMeeting);

        CenterBO existingCenter = new CenterBuilder().withName("Center-top-hierarchy")
                                                     .with(existingMeeting)
                                                     .with(existingOffice)
                                                     .withLoanOfficer(existingLoanOfficer)
                                                     .withUserContext()
                                                     .build();
        IntegrationTestObjectMother.createCenter(existingCenter, existingMeeting);

        GroupBO existingGroup = new GroupBuilder().withName("group1")
                                                  .withParentCustomer(existingCenter)
                                                  .formedBy(existingLoanOfficer)
                                                  .build();
        IntegrationTestObjectMother.createGroup(existingGroup, existingMeeting);

        MeetingUpdateRequest meetingUpdateRequest = new MeetingUpdateRequestBuilder().withCustomerId(existingCenter.getCustomerId())
                                                                                     .with(WeekDay.WEDNESDAY)
                                                                                     .withMeetingPlace("town hall")
                                                                                     .build();

        UserContext userContext = TestUtils.makeUser();
        userContext.setBranchId(existingOffice.getOfficeId());

        // pre-verification
        GroupBO createdGroup = customerDao.findGroupBySystemId(existingGroup.getGlobalCustNum());
        assertThatAllCustomerSchedulesOccuringOnOrAfterTodayFallOnDayOfWeek(createdGroup, WeekDay.MONDAY);

        // exercise test
        customerService.updateCustomerMeetingSchedule(meetingUpdateRequest, userContext);

        // verification
        GroupBO modifiedGroup = customerDao.findGroupBySystemId(existingGroup.getGlobalCustNum());
        assertThatAllCustomerSchedulesOccuringOnOrAfterTodayFallOnDayOfWeek(modifiedGroup, WeekDay.WEDNESDAY);
    }


    private void assertThatAllCustomerSchedulesOccuringOnOrAfterTodayFallOnDayOfWeek(CustomerBO customer, WeekDay expectedDayOfWeek) {
        Set<AccountActionDateEntity> customerSchedules = customer.getCustomerAccount().getAccountActionDates();
        for (AccountActionDateEntity accountActionDateEntity : customerSchedules) {
            CustomerScheduleEntity customerSchedule = (CustomerScheduleEntity) accountActionDateEntity;

            LocalDate scheduledDate = new LocalDate(customerSchedule.getActionDate());
            LocalDate now = new LocalDate();

            if (scheduledDate.isEqual(now) || scheduledDate.isAfter(now)) {
                assertThat(scheduledDate.dayOfWeek().get(), is(WeekDay.getJodaDayOfWeekThatMatchesMifosWeekDay(expectedDayOfWeek.getValue())));
            }
        }
    }
}