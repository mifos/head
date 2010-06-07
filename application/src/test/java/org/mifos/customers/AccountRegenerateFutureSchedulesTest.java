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
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.calendar.CalendarEvent;
import org.mifos.customers.business.CustomerAccountBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.domain.builders.CalendarEventBuilder;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.schedule.ScheduledDateGeneration;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * I test {@link CustomerAccountBO#createNew(CustomerBO, List, MeetingBO, CalendarEvent)}
 */
@RunWith(MockitoJUnitRunner.class)
public class AccountRegenerateFutureSchedulesTest {

    // class under test
    private CustomerAccountBO customerAccount;

    // collabrators
    private CustomerBO customer;
    private MeetingBO customerMeeting;
    private List<AccountFeesEntity> accountFees;
    private CalendarEvent applicableCalendarEvents;

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
//john w - get back to this later
    @Ignore
    @Test
    public void regeneratingDatesFromFirstInstallmentWithNoNewHolidaysDoesNothing() throws Exception {

        // setup
        customer = new CenterBuilder().inActive().build();
        accountFees = new ArrayList<AccountFeesEntity>();
        applicableCalendarEvents = new CalendarEventBuilder().build();
        customerAccount = CustomerAccountBO.createNew(customer, accountFees, customerMeeting, applicableCalendarEvents);
        List<Holiday> unappliedHolidays = new ArrayList<Holiday>();

        // exercise test
        // john w get back to this later customerAccount.rescheduleDatesForNewHolidays(scheduledDateGeneration, unappliedHolidays);

        // verification
        assertThat(customerAccount.getAccountActionDates().isEmpty(), is(true));
    }
}