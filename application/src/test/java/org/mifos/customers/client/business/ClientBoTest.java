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

package org.mifos.customers.client.business;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import junit.framework.Assert;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.LoanProductBuilder;
import org.mifos.application.collectionsheet.persistence.ClientBuilder;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.Money;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * I test {@link ClientBO}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ClientBoTest {

    // class under test
    private ClientBO client;

    @Mock
    private LoanBO loanAccount;

    @Before
    public void setup() throws Exception {
        Money.setDefaultCurrency(TestUtils.RUPEE);
        client = new ClientBuilder().buildForUnitTests();
    }

    @Test
    public void testHasActiveLoanAccountsForProductReturnsTrueIfCustomerHasSuchAccounts() throws Exception {

        // setup
        client = new ClientBuilder().active().buildForUnitTests();
        LoanOfferingBO loanProduct = new LoanProductBuilder().active().buildForUnitTests();
        client.addAccount(loanAccount);

        // stubbing
        when(loanAccount.isActiveLoanAccount()).thenReturn(true);
        when(loanAccount.getLoanOffering()).thenReturn(loanProduct);

        // exercise
        boolean hasActiveAccounts = client.hasActiveLoanAccountsForProduct(loanProduct);

        assertThat(hasActiveAccounts, is(true));
    }

    @Test
    public void testHasActiveLoanAccountsForProductReturnsFalseIfCustomerHasNoSuchAccountsForThatProduct() throws Exception {

        // setup
        client = new ClientBuilder().active().buildForUnitTests();
        LoanOfferingBO loanProduct1 = new LoanProductBuilder().withGlobalProductNumber("xxxx-111").buildForUnitTests();
        LoanOfferingBO loanProduct2 = new LoanProductBuilder().withGlobalProductNumber("xxxx-222").buildForUnitTests();
        client.addAccount(loanAccount);

        // stubbing
        when(loanAccount.isActiveLoanAccount()).thenReturn(true);
        when(loanAccount.getLoanOffering()).thenReturn(loanProduct2);

        // exercise
        boolean hasActiveAccounts = client.hasActiveLoanAccountsForProduct(loanProduct1);

        assertThat(hasActiveAccounts, is(false));
    }

    @Test
    public void testHasActiveLoanAccountsForProductDoesNotFetchLoanOfferingIfNoActiveLoanAccounts() throws Exception {

        // setup
        client = new ClientBuilder().active().buildForUnitTests();
        LoanOfferingBO loanProduct1 = new LoanProductBuilder().withGlobalProductNumber("xxxx-111").buildForUnitTests();
        client.addAccount(loanAccount);

        // stubbing
        when(loanAccount.isActiveLoanAccount()).thenReturn(false);

        // exercise
        boolean hasActiveAccounts = client.hasActiveLoanAccountsForProduct(loanProduct1);

        assertThat(hasActiveAccounts, is(false));
    }

    @Test
    public void testAddClientAttendance() {
        Assert.assertEquals("Expecting no attendance entries on a new object", 0, client.getClientAttendances().size());
        DateTime meetingDate = new DateTimeService().getCurrentDateTime();
        client.addClientAttendance(buildClientAttendance(meetingDate.toDate()));
        Assert.assertEquals("Expecting no attendance entries on a new object", 1, client.getClientAttendances().size());
    }

    @Test
    public void testGetClientAttendanceForMeeting() {
        DateTime meetingDate = new DateTimeService().getCurrentDateTime();
        client.addClientAttendance(buildClientAttendance(meetingDate.toDate()));
        Assert.assertEquals("The value of customer attendance for the meeting : ", AttendanceType.PRESENT, client
                .getClientAttendanceForMeeting(meetingDate.toDate()).getAttendanceAsEnum());
    }

    @Test
    public void testHandleAttendance() throws Exception {

        DateTime meetingDate = new DateTimeService().getCurrentDateTime();
        client.handleAttendance(meetingDate.toDate(), AttendanceType.PRESENT.getValue(), false);

        Assert.assertEquals("The size of customer attendance is : ", client.getClientAttendances().size(), 1);
        Assert.assertEquals("The value of customer attendance for the meeting : ", AttendanceType.PRESENT, client
                .getClientAttendanceForMeeting(meetingDate.toDate()).getAttendanceAsEnum());

        client.handleAttendance(meetingDate.toDate(), AttendanceType.ABSENT.getValue(), false);
        Assert.assertEquals("The size of customer attendance is : ", 1, client.getClientAttendances().size());
        Assert.assertEquals("The value of customer attendance for the meeting : ", AttendanceType.ABSENT, client
                .getClientAttendanceForMeeting(meetingDate.toDate()).getAttendanceAsEnum());
    }

    @Test
    public void testHandleAttendanceForDifferentDates() throws Exception {

        DateTime meetingDate = new DateTimeService().getCurrentDateTime();
        client.handleAttendance(meetingDate.toDate(), AttendanceType.PRESENT.getValue(), false);

        Assert.assertEquals("The size of customer attendance is : ", client.getClientAttendances().size(), 1);
        Assert.assertEquals("The value of customer attendance for the meeting : ", AttendanceType.PRESENT, client
                .getClientAttendanceForMeeting(meetingDate.toDate()).getAttendanceAsEnum());

        DateMidnight offSetDate = getDateOffset(1);
        client.handleAttendance(new java.sql.Date(offSetDate.getMillis()), AttendanceType.ABSENT.getValue(), false);

        Assert.assertEquals("The size of customer attendance is : ", client.getClientAttendances().size(), 2);
        Assert.assertEquals("The value of customer attendance for the meeting : ", AttendanceType.PRESENT, client
                .getClientAttendanceForMeeting(meetingDate.toDate()).getAttendanceAsEnum());
        Assert.assertEquals("The value of customer attendance for the meeting : ", AttendanceType.ABSENT, client
                .getClientAttendanceForMeeting(offSetDate.toDate()).getAttendanceAsEnum());
    }

    private DateMidnight getDateOffset(final int numberOfDays) {
        DateMidnight currentDate = new DateTimeService().getCurrentDateMidnight();
        currentDate = currentDate.minusDays(numberOfDays);
        return currentDate;
    }

    private ClientAttendanceBO buildClientAttendance(final java.util.Date meetingDate) {
        ClientAttendanceBO clientAttendance = new ClientAttendanceBO();
        clientAttendance.setAttendance(AttendanceType.PRESENT);
        clientAttendance.setMeetingDate(meetingDate);
        return clientAttendance;
    }
}