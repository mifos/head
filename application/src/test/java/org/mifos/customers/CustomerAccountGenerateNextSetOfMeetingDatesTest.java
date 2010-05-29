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

import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.calendar.CalendarEvent;
import org.mifos.customers.business.CustomerAccountBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.domain.builders.CalendarEventBuilder;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.schedule.ScheduledDateGeneration;
import org.mifos.schedule.ScheduledEvent;
import org.mifos.schedule.ScheduledEventFactory;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * I test {@link CustomerAccountBO#generateNextSetOfMeetingDates(ScheduledDateGeneration)}
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomerAccountGenerateNextSetOfMeetingDatesTest {

    // class under test
    private CustomerAccountBO customerAccount;

    // collabrators
    private CustomerBO customer;
    private List<AccountFeesEntity> accountFees;
    private CalendarEvent calendarEvent;

    @Mock
    private ScheduledDateGeneration scheduledDateGeneration;

    private static MifosCurrency oldDefaultCurrency;

    @BeforeClass
    public static void setupDefaultCurrency() {
        oldDefaultCurrency = Money.getDefaultCurrency();
        Money.setDefaultCurrency(TestUtils.RUPEE);
    }

    @AfterClass
    public static void replaceOldDefaultCurrency() {
        Money.setDefaultCurrency(oldDefaultCurrency);
    }

    @Before
    public void setupAndInjectDependencies() {
        calendarEvent = new CalendarEventBuilder().build();
        accountFees = new ArrayList<AccountFeesEntity>();
    }


    @Test
    public void canGenerateTenSchedulesMatchingMeetingRecurrenceWhenNoPreviousSchedulesExisted() throws Exception {

        // use default setup
        customer = new CenterBuilder().inActive().build();
        customerAccount = CustomerAccountBO.createNew(customer, accountFees, customer.getCustomerMeetingValue(), calendarEvent);

        DateTime lastScheduledDate = new DateTime().toDateMidnight().toDateTime();
        ScheduledEvent scheduledEvent = ScheduledEventFactory.createScheduledEventFrom(customer.getCustomerMeetingValue());

        // pre verification
        assertThat(customerAccount.getAccountActionDates().isEmpty(), is(true));

        // stubbing
        List<DateTime> generatedDates = elevenDatesStartingFromAndIncluding(lastScheduledDate, scheduledEvent);
        when(scheduledDateGeneration.generateScheduledDates(anyInt(), (DateTime)anyObject(), (ScheduledEvent)anyObject())).thenReturn(generatedDates);

        // exercise test
        customerAccount.generateNextSetOfMeetingDates(scheduledDateGeneration);

        // verification
        assertThat(customerAccount.getAccountActionDates().isEmpty(), is(false));
        assertThat(customerAccount.getAccountActionDates().size(), is(10));
    }

    @Test
    public void canGenerateNextSetOfMeetingDatesWithFreshlyGeneratedSchedule() {

        // use default setup
        MeetingBO customerMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).build();
        customer = new CenterBuilder().active().with(customerMeeting).build();
        customerAccount = CustomerAccountBO.createNew(customer, accountFees, customerMeeting, calendarEvent);

        // pre verification
        assertThat(customerAccount.getAccountActionDates().size(), is(10));

        // setup
        List<AccountActionDateEntity> schedules = new ArrayList<AccountActionDateEntity>(customerAccount.getAccountActionDates());
        DateTime lastScheduledDate = new DateTime(schedules.get(9).getActionDate());
        ScheduledEvent scheduledEvent = ScheduledEventFactory.createScheduledEventFrom(customer.getCustomerMeetingValue());

        // stubbing
        List<DateTime> generatedDates = elevenDatesStartingFromAndIncluding(lastScheduledDate, scheduledEvent);
        when(scheduledDateGeneration.generateScheduledDates(anyInt(), (DateTime)anyObject(), (ScheduledEvent)anyObject())).thenReturn(generatedDates);

        // exercise test
        customerAccount.generateNextSetOfMeetingDates(scheduledDateGeneration);

        //verification
        assertThat(customerAccount.getAccountActionDates().size(), is(20));
    }

    private List<DateTime> elevenDatesStartingFromAndIncluding(DateTime lastScheduledDate, ScheduledEvent scheduledEvent) {

        List<DateTime> dates = new ArrayList<DateTime>();
        dates.add(lastScheduledDate);

        DateTime lastestDate = lastScheduledDate;
        for (int i = 0; i < 10; i++) {
            lastestDate = scheduledEvent.nextEventDateAfter(lastestDate);
            dates.add(lastestDate);
        }

        return dates;
    }
}