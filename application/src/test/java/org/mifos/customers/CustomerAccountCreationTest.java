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

import static org.junit.matchers.JUnitMatchers.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.application.collectionsheet.persistence.FeeBuilder;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.calendar.CalendarEvent;
import org.mifos.customers.business.CustomerAccountBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.domain.builders.CalendarEventBuilder;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.Money;

/**
 * I test {@link CustomerAccountBO#createNew(CustomerBO, List, MeetingBO, CalendarEvent)}
 */
public class CustomerAccountCreationTest {

    // class under test
    private CustomerAccountBO customerAccount;

    // collabrators
    private CustomerBO customer;
    private MeetingBO customerMeeting;
    private List<AccountFeesEntity> accountFees;
    private CalendarEvent applicableCalendarEvents;

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

    @Test
    public void customerAccountIsAlwaysCreatedInActiveState() throws Exception {

        // setup
        customer = new CenterBuilder().inActive().build();
        accountFees = new ArrayList<AccountFeesEntity>();

        // exercise test
        customerAccount = CustomerAccountBO.createNew(customer, accountFees, customerMeeting, applicableCalendarEvents);

        // verification
        assertThat(customerAccount.isActive(), is(true));
    }

    @Test
    public void customerAccountCanBeCreatedWithNoAccountFees() throws Exception {

        // setup
        accountFees = new ArrayList<AccountFeesEntity>();
        customer = new CenterBuilder().inActive().build();

        // exercise test
        customerAccount = CustomerAccountBO.createNew(customer, accountFees, customerMeeting, applicableCalendarEvents);

        // verification
        assertThat(customerAccount.getAccountFees().isEmpty(), is(true));
    }

    @Test
    public void customerAccountCanBeCreatedWithAccountFees() throws Exception {

        // setup
        FeeBO fee = new FeeBuilder().appliesToAllCustomers().periodic().with(new MeetingBuilder().periodicFeeMeeting().weekly().every(1)).build();
        AccountFeesEntity accountFee = new AccountFeesEntity(null, fee, Double.valueOf("25.0"));
        accountFees = new ArrayList<AccountFeesEntity>();
        accountFees.add(accountFee);

        customer = new CenterBuilder().inActive().build();

        // exercise test
        customerAccount = CustomerAccountBO.createNew(customer, accountFees, customerMeeting, applicableCalendarEvents);

        // verification
        assertThat(customerAccount.getAccountFees(), hasItem(accountFee));
        assertThat(accountFee.getAccount(), is((AccountBO)customerAccount));
    }

    @Test
    public void customerAccountIsNotCreatedWithCustomerSchedulesWhenAssociatedCustomerIsNotActive() throws Exception {

        // setup
        accountFees = new ArrayList<AccountFeesEntity>();
        customer = new CenterBuilder().inActive().build();

        // exercise test
        customerAccount = CustomerAccountBO.createNew(customer, accountFees, customerMeeting, applicableCalendarEvents);

        // verification
        assertThat(customerAccount.getAccountActionDates().isEmpty(), is(true));
    }

    @Test
    public void customerAccountIsCreatedWithCustomerSchedulesWhenAssociatedCustomerIsActive() throws Exception {

        // setup
        applicableCalendarEvents = new CalendarEventBuilder().build();
        customerMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).build();
        accountFees = new ArrayList<AccountFeesEntity>();

        customer = new CenterBuilder().active().build();

        // exercise test
        customerAccount = CustomerAccountBO.createNew(customer, accountFees, customerMeeting, applicableCalendarEvents);

        // verification
        assertThat(customerAccount.getAccountActionDates().isEmpty(), is(false));
    }

    @Test
    public void customerSchedulesAreCreatedFromCustomersActivationDate() throws Exception {

        // setup
        applicableCalendarEvents = new CalendarEventBuilder().build();

        DateTime twoWeeksAgo = new DateTime().minusWeeks(2);
        customerMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).startingFrom(twoWeeksAgo.toDate()).build();
        accountFees = new ArrayList<AccountFeesEntity>();

        customer = new CenterBuilder().active().withActivationDate(new DateMidnight().toDateTime()).build();

        // exercise test
        customerAccount = CustomerAccountBO.createNew(customer, accountFees, customerMeeting, applicableCalendarEvents);

        // verification
        assertThat(customerAccount.getAccountActionDates().isEmpty(), is(false));

        List<AccountActionDateEntity> customerSchedules = new ArrayList<AccountActionDateEntity>(customerAccount.getAccountActionDates());
        assertThat(new LocalDate(customerSchedules.get(0).getActionDate()), is(new LocalDate()));
    }
}