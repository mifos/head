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

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.application.collectionsheet.persistence.ClientBuilder;
import org.mifos.application.collectionsheet.persistence.GroupBuilder;
import org.mifos.application.collectionsheet.persistence.LoanAccountBuilder;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.collectionsheet.persistence.OfficeBuilder;
import org.mifos.application.collectionsheet.persistence.SavingsAccountBuilder;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.domain.builders.PersonnelBuilder;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.Money;

public class GroupValidationTest {

    // class under test
    private GroupBO group;

    // collaborators
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
    public void nameCannotBeBlank() {
        group = new GroupBuilder().withName(null).withParentCustomer(center).build();

        try {
            group.validate();
            fail("should throw customer exception as name must be set on group.");
        } catch (CustomerException e) {
            assertThat(e.getKey(), is(CustomerConstants.ERRORS_SPECIFY_NAME));
        }
    }

    @Test
    public void formedByPersonnelMustBeAssigned() {

        group = new GroupBuilder().withName("group-On-center").withParentCustomer(center).formedBy(null).build();

        try {
            group.validate();
            fail("should throw customer exception as personnel that fomed group must exist when creating group.");
        } catch (CustomerException e) {
            assertThat(e.getKey(), is(CustomerConstants.INVALID_FORMED_BY));
        }
    }

    @Test
    public void loanOfficerMustBeAssigned() {

        OfficeBO office = new OfficeBuilder().build();
        group = new GroupBuilder().withName("group-On-branch").withOffice(office).withLoanOfficer(null).buildAsTopOfHierarchy();

        try {
            group.validate();
            fail("should throw customer exception as loan officer must exist when creating group under a branch.");
        } catch (CustomerException e) {
            assertThat(e.getKey(), is(CustomerConstants.ERRORS_SELECT_LOAN_OFFICER));
        }
    }

    @Test
    public void officeMustBeAssigned() {
        group = new GroupBuilder().withName("group-On-branch").withOffice(null).buildAsTopOfHierarchy();

        try {
            group.validate();
            fail("should throw customer exception as office must exist when creating group under a branch.");
        } catch (CustomerException e) {
            assertThat(e.getKey(), is(CustomerConstants.INVALID_OFFICE));
        }
    }

    @Test
    public void givenGroupIsTrainedButTrainedDateIsNotSetThenShouldThrowCustomerException() {

        PersonnelBO formedBy = new PersonnelBuilder().build();
        group = new GroupBuilder().withName("group-On-center").withParentCustomer(center).formedBy(formedBy).isTrained().trainedOn(null).build();

        try {
            group.validate();
            fail("should throw customer exception as trained date must be provided if in trained state when creating group.");
        } catch (CustomerException e) {
            assertThat(e.getKey(), is(CustomerConstants.INVALID_TRAINED_OR_TRAINEDDATE));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenGroupWithNullMeetingShouldThrowIllegalArgumentException() {
        OfficeBO office = new OfficeBuilder().build();
        PersonnelBO loanOfficer = new PersonnelBuilder().asLoanOfficer().build();
        group = new GroupBuilder().withName("group-On-branch").withOffice(office).withLoanOfficer(loanOfficer).withMeeting(null).buildAsTopOfHierarchy();
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenGroupWithNullParentShouldThrowIllegalArgumentException() {
        OfficeBO office = new OfficeBuilder().build();
        PersonnelBO loanOfficer = new PersonnelBuilder().asLoanOfficer().build();
        MeetingBO meeting = new MeetingBuilder().customerMeeting().build();
        group = new GroupBuilder().withName("group-On-center").withOffice(office).withLoanOfficer(loanOfficer).withMeeting(meeting).withParentCustomer(null).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenGroupWithNullCustomerStatusShouldThrowIllegalArgumentException() {
        group = new GroupBuilder().withName("group1").withStatus(null).withParentCustomer(center).build();
    }

    @Test
    public void givenReceivingCenterIsNullShouldFailValidation() {

        PersonnelBO formedBy = new PersonnelBuilder().build();
        group = new GroupBuilder().withName("group-On-center").withParentCustomer(center).formedBy(formedBy).build();

        CenterBO toCenter = null;

        try {
            group.validateReceivingCenter(toCenter);
            fail("givenReceivingCenterIsNullShouldThrowCustomerException");
        } catch (CustomerException e) {
            assertThat(e.getKey(), is(CustomerConstants.INVALID_PARENT));
        }
    }

    @Test
    public void givenCenterIsSameAsGroupsParentGroupTransferToCenterShouldFailValidation() {
        // setup
        CenterBO center = new CenterBuilder().build();
        GroupBO group = new GroupBuilder().withParentCustomer(center).build();

        // exercise test
        try {
            group.validateReceivingCenter(center);
            fail("should fail validation");
        } catch (CustomerException e) {
            assertThat(e.getKey(), is(CustomerConstants.ERRORS_SAME_PARENT_TRANSFER));
        }
    }

    @Test
    public void givenCenterIsInActiveGroupTransferToCenterShouldFailValidation() {
        // setup
        CenterBO center = new CenterBuilder().withName("newCenter").inActive().build();
        GroupBO group = new GroupBuilder().build();

        // exercise test
        try {
            group.validateReceivingCenter(center);
            fail("should fail validation");
        } catch (CustomerException e) {
            assertThat(e.getKey(), is(CustomerConstants.ERRORS_INTRANSFER_PARENT_INACTIVE));
        }
    }

    @Test
    public void givenCenterHasMeetingWithDifferentPeriodicityGroupTransferToCenterShouldFailValidation() {
        // setup
        MeetingBO weeklyMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).build();
        CenterBO toCenter = new CenterBuilder().withName("receivingCenter").with(weeklyMeeting).build();

        MeetingBO monthlyMeeting = new MeetingBuilder().customerMeeting().monthly().every(1).onDayOfMonth(1).build();
        CenterBO oldCenter = new CenterBuilder().withName("oldCenter").with(monthlyMeeting).build();
        GroupBO group = new GroupBuilder().withParentCustomer(oldCenter).build();

        // exercise test
        try {
            group.validateReceivingCenter(toCenter);
            fail("should fail validation");
        } catch (CustomerException e) {
            assertThat(e.getKey(), is(CustomerConstants.ERRORS_MEETING_FREQUENCY_MISMATCH));
        }
    }

    @Test
    public void givenAnActiveLoanAccountExistsGroupTransferToCenterShouldFailValidation() {
        // setup
        CenterBO center = new CenterBuilder().build();
        GroupBO group = new GroupBuilder().withParentCustomer(center).build();

        LoanBO loan = new LoanAccountBuilder().activeInGoodStanding().build();
        group.addAccount(loan);

        // exercise test
        try {
            group.validateNoActiveAccountsExist();
            fail("should fail validation");
        } catch (CustomerException e) {
            assertThat(e.getKey(), is(CustomerConstants.ERRORS_HAS_ACTIVE_ACCOUNT));
        }
    }

    @Test
    public void givenAnActiveSavingsAccountExistsGroupTransferToCenterShouldFailValidation() {
        // setup
        CenterBO center = new CenterBuilder().build();
        GroupBO group = new GroupBuilder().withParentCustomer(center).build();
        SavingsBO savings = new SavingsAccountBuilder().active().withCustomer(group).build();
        group.addAccount(savings);

        // exercise test
        try {
            group.validateNoActiveAccountsExist();
            fail("should fail validation");
        } catch (CustomerException e) {
            assertThat(e.getKey(), is(CustomerConstants.ERRORS_HAS_ACTIVE_ACCOUNT));
        }
    }

    @Test
    public void givenAnActiveLoanAccountExistsOnClientOfGroupGroupTransferToCenterShouldFailValidation() {
        // setup
        CenterBO center = new CenterBuilder().build();
        GroupBO group = new GroupBuilder().withParentCustomer(center).build();
        ClientBO client = new ClientBuilder().withParentCustomer(group).active().buildForUnitTests();
        group.addChild(client);

        LoanBO loan = new LoanAccountBuilder().activeInGoodStanding().build();
        client.addAccount(loan);

        // exercise test
        try {
            group.validateNoActiveAccountsExist();
            fail("should fail validation");
        } catch (CustomerException e) {
            assertThat(e.getKey(), is(CustomerConstants.ERRORS_CHILDREN_HAS_ACTIVE_ACCOUNT));
        }
    }

    @Test
    public void givenAnActiveSavingsAccountExistsOnClientOfGroupGroupTransferToCenterShouldFailValidation() {
        // setup
        CenterBO center = new CenterBuilder().build();
        GroupBO group = new GroupBuilder().withParentCustomer(center).build();
        ClientBO client = new ClientBuilder().withParentCustomer(group).active().buildForUnitTests();
        group.addChild(client);

        SavingsBO savings = new SavingsAccountBuilder().active().withCustomer(group).build();
        client.addAccount(savings);

        // exercise test
        try {
            group.validateNoActiveAccountsExist();
            fail("should fail validation");
        } catch (CustomerException e) {
            assertThat(e.getKey(), is(CustomerConstants.ERRORS_CHILDREN_HAS_ACTIVE_ACCOUNT));
        }
    }

}