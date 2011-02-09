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

package org.mifos.customers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mifos.framework.TestUtils.assertThatAllCustomerSchedulesOccuringAfterCurrentInstallmentPeriodFallOnDayOfWeek;
import static org.mifos.framework.TestUtils.assertThatAllCustomerSchedulesOccuringBeforeOrOnCurrentInstallmentPeriodRemainUnchanged;
import static org.mockito.Matchers.anyShort;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.joda.time.DateTime;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.calendar.DayOfWeek;
import org.mifos.customers.business.service.CustomerAccountFactory;
import org.mifos.customers.business.service.CustomerService;
import org.mifos.customers.business.service.CustomerServiceImpl;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.domain.builders.CalendarEventBuilder;
import org.mifos.domain.builders.CenterBuilder;
import org.mifos.domain.builders.CustomerAccountBuilder;
import org.mifos.domain.builders.MeetingBuilder;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * I test implementation of {@link CustomerService} for creation of {@link CenterBO}'s.
 */
@RunWith(MockitoJUnitRunner.class)
public class UpdateCustomerMeetingScheduleTest {

    // class under test
    private CustomerService customerService;

    // collaborators (behaviour)
    @Mock
    private CustomerDao customerDao;

    @Mock
    private PersonnelDao personnelDao;

    @Mock
    private OfficeDao officeDao;

    @Mock
    private HolidayDao holidayDao;

    @Mock
    private HibernateTransactionHelper hibernateTransaction;

    @Mock
    private CustomerAccountFactory customerAccountFactory;

    // stubbed data
    @Mock
    private CenterBO mockedCenter;

    private static MifosCurrency oldDefaultCurrency;

    @BeforeClass
    public static void initialiseHibernateUtil() {

        oldDefaultCurrency = Money.getDefaultCurrency();
        Money.setDefaultCurrency(TestUtils.RUPEE);
    }

    @AfterClass
    public static void resetCurrency() {
        Money.setDefaultCurrency(oldDefaultCurrency);
    }


    @Before
    public void setupAndInjectDependencies() {
        customerService = new CustomerServiceImpl(customerDao, personnelDao, officeDao, holidayDao, hibernateTransaction);
        ((CustomerServiceImpl)customerService).setCustomerAccountFactory(customerAccountFactory);
    }

    @Test(expected = BusinessRuleException.class)
    public void throwsCheckedExceptionWhenCustomerIsNotTopOfCustomerHierarchy() throws Exception {

        // setup
        MeetingBO newMeeting = new MeetingBuilder().build();

        // stubbing
        doThrow(new BusinessRuleException(CustomerConstants.INVALID_MEETING)).when(mockedCenter).validateIsTopOfHierarchy();

        // exercise test
        customerService.updateCustomerMeetingSchedule(newMeeting, mockedCenter);

        // verification
        verify(mockedCenter).validateIsTopOfHierarchy();
    }

    @Test(expected = BusinessRuleException.class)
    public void throwsCheckedExceptionWhenUserDoesNotHavePermissionToEditMeetingSchedule() throws Exception {

        // setup
        UserContext userContext = TestUtils.makeUser();
        MeetingBO newMeeting = new MeetingBuilder().build();
        newMeeting.updateDetails(userContext);

        // stubbing
        when(mockedCenter.getUserContext()).thenReturn(TestUtils.makeUser());
        doThrow(new BusinessRuleException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED)).when(customerDao).checkPermissionForEditMeetingSchedule(userContext, mockedCenter);

        // exercise test
        customerService.updateCustomerMeetingSchedule(newMeeting, mockedCenter);

        // verification
        verify(customerDao).checkPermissionForEditMeetingSchedule(userContext, mockedCenter);
    }

    @Test
    public void givenDayOfWeekRemainsUnchangedShouldNotRegenerateSchedules() throws Exception {

        // setup
        UserContext userContext = TestUtils.makeUser();
        MeetingBO weeklyMondayMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).occuringOnA(WeekDay.MONDAY).build();
        weeklyMondayMeeting.updateDetails(userContext);

        CustomerAccountBuilder accountBuilder = new CustomerAccountBuilder();
        CenterBO center = new CenterBuilder().active().with(weeklyMondayMeeting).withAccount(accountBuilder).build();

        // pre - verification
        assertThatAllCustomerSchedulesOccuringBeforeOrOnCurrentInstallmentPeriodRemainUnchanged(center, WeekDay.MONDAY);
        assertThatAllCustomerSchedulesOccuringAfterCurrentInstallmentPeriodFallOnDayOfWeek(center, WeekDay.MONDAY);

        // exercise test
        customerService.updateCustomerMeetingSchedule(weeklyMondayMeeting, mockedCenter);

        // verification
        assertThatAllCustomerSchedulesOccuringBeforeOrOnCurrentInstallmentPeriodRemainUnchanged(center, WeekDay.MONDAY);
        assertThatAllCustomerSchedulesOccuringAfterCurrentInstallmentPeriodFallOnDayOfWeek(center, WeekDay.MONDAY);
    }

    @Test
    public void givenDayOfWeekIsDifferentShouldRegenerateSchedules() throws Exception {

        // setup
        UserContext userContext = TestUtils.makeUser();
        DateTime mondayTwoWeeksAgo = new DateTime().withDayOfWeek(DayOfWeek.monday()).minusWeeks(2);
        MeetingBO weeklyMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).withStartDate(mondayTwoWeeksAgo).build();
        weeklyMeeting.updateDetails(userContext);

        CustomerAccountBuilder accountBuilder = new CustomerAccountBuilder();
        CenterBO center = new CenterBuilder().active().with(weeklyMeeting).withAccount(accountBuilder).build();

        MeetingBO weeklyWednesdayMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).occuringOnA(WeekDay.WEDNESDAY).build();
        weeklyWednesdayMeeting.updateDetails(userContext);

        // stubbing
        when(holidayDao.findCalendarEventsForThisYearAndNext(anyShort())).thenReturn(new CalendarEventBuilder().build());

        // pre - verification
        assertThatAllCustomerSchedulesOccuringAfterCurrentInstallmentPeriodFallOnDayOfWeek(center, WeekDay.MONDAY);

        // exercise test
        customerService.updateCustomerMeetingSchedule(weeklyWednesdayMeeting, center);

        // verification
        assertThatAllCustomerSchedulesOccuringBeforeOrOnCurrentInstallmentPeriodRemainUnchanged(center, WeekDay.MONDAY);
        assertThatAllCustomerSchedulesOccuringAfterCurrentInstallmentPeriodFallOnDayOfWeek(center, WeekDay.WEDNESDAY);
    }

    @Test
    public void givenCustomerHasNoExistingMeetingShouldCreateMeetingOnCustomer() throws Exception {

        // setup
        UserContext userContext = TestUtils.makeUser();
        CustomerAccountBuilder accountBuilder = new CustomerAccountBuilder();

        CenterBO center = new CenterBuilder().active().withAccount(accountBuilder).build();
        center.setCustomerMeeting(null);

        MeetingBO weeklyWednesdayMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).occuringOnA(WeekDay.WEDNESDAY).build();
        weeklyWednesdayMeeting.updateDetails(userContext);

        // stubbing
        when(holidayDao.findCalendarEventsForThisYearAndNext(anyShort())).thenReturn(new CalendarEventBuilder().build());

        // exercise test
        customerService.updateCustomerMeetingSchedule(weeklyWednesdayMeeting, center);

        // verification
        assertThat(center.getCustomerMeeting(), is(notNullValue()));
        assertThat(center.getCustomerMeetingValue(), is(notNullValue()));
    }
}