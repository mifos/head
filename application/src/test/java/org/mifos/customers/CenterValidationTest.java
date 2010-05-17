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
import static org.junit.Assert.fail;
import static org.mifos.domain.builders.PersonnelBuilder.anyLoanOfficer;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.application.collectionsheet.persistence.FeeBuilder;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.collectionsheet.persistence.OfficeBuilder;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.business.service.CustomerService;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.domain.builders.PersonnelBuilder;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.util.helpers.Money;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * I test implementation of {@link CustomerService} for creation of {@link CenterBO}'s.
 */
@RunWith(MockitoJUnitRunner.class)
public class CenterValidationTest {

    // class under test
    private CenterBO center;

    private static MifosCurrency oldCurrency;

    @BeforeClass
    public static void setupDefaultCurrency() {
        oldCurrency = Money.getDefaultCurrency();
        Money.setDefaultCurrency(TestUtils.RUPEE);
    }

    @AfterClass
    public static void resetDefaultCurrency() {
        Money.setDefaultCurrency(oldCurrency);
    }

    @Before
    public void setupDependencies() {
        OfficeBO office = new OfficeBuilder().build();
        PersonnelBO loanOfficer = new PersonnelBuilder().asLoanOfficer().build();
        MeetingBO meeting = new MeetingBuilder().customerMeeting().build();

        center = new CenterBuilder().with(office).withLoanOfficer(loanOfficer).with(meeting).build();
    }

    @Test
    public void cannotCreateCenterWithBlankName() {

        // setup
        center = new CenterBuilder().withName("").build();

        // exercise test
        try {
            center.validate();
            fail("cannotCreateCenterWithBlankName");
        } catch (ApplicationException e) {
            assertThat(e.getKey(), is(CustomerConstants.ERRORS_SPECIFY_NAME));
        }
    }

    @Test
    public void cannotCreateCenterWithoutALoanOfficer() {

        // setup
        PersonnelBO noLoanOfficer = null;
        center = new CenterBuilder().withName("center1").withLoanOfficer(noLoanOfficer).build();

        // exercise test
        try {
            center.validate();
            fail("cannotCreateCenterWithoutALoanOfficer");
        } catch (ApplicationException e) {
            assertThat(e.getKey(), is(CustomerConstants.ERRORS_SELECT_LOAN_OFFICER));
        }
    }

    @Test
    public void cannotCreateCenterWithoutAMfiJoiningDate() {

        // setup
        DateTime noJoiningDate = null;
        MeetingBuilder customerMeeting = new MeetingBuilder().customerMeeting();
        center = new CenterBuilder().withName("center1").withLoanOfficer(anyLoanOfficer()).with(customerMeeting).withMfiJoiningDate(noJoiningDate).build();

        // exercise test
        try {
            center.validate();
            fail("cannotCreateCenterWithoutAMfiJoiningDate");
        } catch (ApplicationException e) {
            assertThat(e.getKey(), is(CustomerConstants.MFI_JOINING_DATE_MANDATORY));
        }
    }

    @Test
    public void cannotCreateCenterWithoutABranch() {

        // setup
        DateTime today = new DateTime();
        MeetingBuilder customerMeeting = new MeetingBuilder().customerMeeting();
        OfficeBO noBranch = null;
        CenterBO center = new CenterBuilder().withName("center1").withLoanOfficer(anyLoanOfficer()).with(customerMeeting).withMfiJoiningDate(today).with(noBranch).build();

        // exercise test
        try {
            center.validate();
            fail("cannotCreateCenterWithoutABranch");
        } catch (ApplicationException e) {
            assertThat(e.getKey(), is(CustomerConstants.INVALID_OFFICE));
        }
    }

    @Test
    public void cannotCreateCenterWithoutAMeeting() {

        // setup
        MeetingBuilder noMeetingBuilder = null;
        MeetingBO noMeeting = null;
        center = new CenterBuilder().withName("center1").withLoanOfficer(anyLoanOfficer()).with(noMeeting).with(noMeetingBuilder).build();
        List<AccountFeesEntity> noAccountFees = new ArrayList<AccountFeesEntity>();

        // exercise test
        try {
            center.validateMeetingAndFees(noAccountFees);
            fail("cannotCreateCenterWithoutAMeeting");
        } catch (ApplicationException e) {
            assertThat(e.getKey(), is(CustomerConstants.ERRORS_SPECIFY_MEETING));
        }
    }

    @Test
    public void cannotCreateCenterWithFeeThatHasDifferentPeriod() {

        // setup
        DateTime today = new DateTime();
        MeetingBuilder aWeeklyMeeting = new MeetingBuilder().customerMeeting().weekly();
        CenterBO center = new CenterBuilder().withName("center1").withLoanOfficer(anyLoanOfficer()).with(aWeeklyMeeting).withMfiJoiningDate(today).build();

        MeetingBuilder aMonthlyMeeting = new MeetingBuilder().periodicFeeMeeting().monthly();
        AmountFeeBO montlyPeriodicFee = new FeeBuilder().appliesToCenterOnly().with(aMonthlyMeeting).build();

        AccountFeesEntity accountFee = new AccountFeesEntity(null, montlyPeriodicFee, montlyPeriodicFee.getFeeAmount().getAmountDoubleValue());
        List<AccountFeesEntity> centerAccountFees = new ArrayList<AccountFeesEntity>();
        centerAccountFees.add(accountFee);

        // exercise test
        try {
            center.validateMeetingAndFees(centerAccountFees);
            fail("cannotCreateCenterWithFeeThatHasDifferentPeriod");
        } catch (ApplicationException e) {
            assertThat(e.getKey(), is(CustomerConstants.ERRORS_FEE_FREQUENCY_MISMATCH));
        }
    }

    @Test
    public void cannotCreateCenterWithMultipleInstancesOfSamePeriodFee() {

        // setup
        DateTime today = new DateTime();
        MeetingBuilder aWeeklyMeeting = new MeetingBuilder().customerMeeting().weekly();
        CenterBO center = new CenterBuilder().withName("center1").withLoanOfficer(anyLoanOfficer()).with(aWeeklyMeeting).withMfiJoiningDate(today).build();

        MeetingBuilder aWeeklyFeeMeeting = new MeetingBuilder().periodicFeeMeeting().weekly();
        AmountFeeBO montlyPeriodicFee = new FeeBuilder().appliesToCenterOnly().with(aWeeklyFeeMeeting).build();

        AccountFeesEntity accountFee = new AccountFeesEntity(null, montlyPeriodicFee, montlyPeriodicFee.getFeeAmount().getAmountDoubleValue());
        List<AccountFeesEntity> centerAccountFees = new ArrayList<AccountFeesEntity>();
        centerAccountFees.add(accountFee);
        centerAccountFees.add(accountFee);

        // exercise test
        try {
            center.validateMeetingAndFees(centerAccountFees);
            fail("cannotCreateCenterWithFeeThatHasDifferentPeriod");
        } catch (ApplicationException e) {
            assertThat(e.getKey(), is(CustomerConstants.ERRORS_DUPLICATE_PERIODIC_FEE));
        }
    }
}