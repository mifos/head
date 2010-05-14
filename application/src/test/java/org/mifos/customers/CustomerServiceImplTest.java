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
import static org.mockito.Matchers.anyShort;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.application.collectionsheet.persistence.GroupBuilder;
import org.mifos.application.collectionsheet.persistence.OfficeBuilder;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.business.service.CustomerService;
import org.mifos.customers.business.service.CustomerServiceImpl;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.util.helpers.Money;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CustomerServiceImplTest {

    // class under test
    private CustomerService customerService;

    // collaborators
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
    public void setupAndInjectDependencies() {
        customerService = new CustomerServiceImpl(customerDao, personnelDao, officeDao, holidayDao, hibernateTransaction);
    }

    @Test
    public void cannotCreateCenterWithBlankName() {

        // setup
        MeetingBO weeklyMeeting = null;

        List<AccountFeesEntity> noAccountFees = new ArrayList<AccountFeesEntity>();

        CenterBO center = new CenterBuilder().withName("").build();

        // exercise test
        try {
            customerService.createCenter(center, weeklyMeeting, noAccountFees);
        } catch (ApplicationException e) {
            assertThat(e.getKey(), is(CustomerConstants.ERRORS_SPECIFY_NAME));
        }
    }

    @Test
    public void givenCenterIsNullGroupTransferToCenterShouldFailValidation() {
        // setup
        GroupBO group = new GroupBuilder().build();
        CenterBO center = null;

        // exercise test
        try {
            customerService.transferGroupTo(group, center);
            fail("should fail validation");
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
            customerService.transferGroupTo(group, center);
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
            customerService.transferGroupTo(group, center);
            fail("should fail validation");
        } catch (CustomerException e) {
            assertThat(e.getKey(), is(CustomerConstants.ERRORS_INTRANSFER_PARENT_INACTIVE));
        }
    }



    @Ignore
    @Test
    public void givenFrequencyOfCenterAndGroupOrClientsMeetingsAreDifferentGroupTransferToCenterShouldFailValidation() {

        // setup

        // exercise test
//        try {
//            customerService.transferGroupTo(group, center);
//            fail("should fail validation");
//        } catch (CustomerException e) {
//            assertThat(e.getKey(), is(CustomerConstants.ERRORS_MEETING_FREQUENCY_MISMATCH));
//        }
    }

    @Test
    public void givenOfficeIsNullGroupTransferToBranchShouldFailValidation() {

        // setup
        GroupBO group = new GroupBuilder().build();
        OfficeBO office = null;

        // exercise test
        try {
            customerService.transferGroupTo(group, office);
            fail("should fail validation");
        } catch (CustomerException e) {
            assertThat(e.getKey(), is(CustomerConstants.INVALID_OFFICE));
        }
    }

    @Test
    public void givenOfficeIsInactiveGroupTransferToBranchShouldFailValidation() {

        // setup
        GroupBO group = new GroupBuilder().build();
        OfficeBO office = new OfficeBuilder().inActive().build();

        // exercise test
        try {
            customerService.transferGroupTo(group, office);
            fail("should fail validation");
        } catch (CustomerException e) {
            assertThat(e.getKey(), is(CustomerConstants.ERRORS_TRANSFER_IN_INACTIVE_OFFICE));
        }
    }

    @Test
    public void givenOfficeIsSameAsGroupsOfficeGroupTransferToBranchShouldFailValidation() {

        // setup
        OfficeBO office = new OfficeBuilder().build();
        CenterBO parent = new CenterBuilder().with(office).build();
        GroupBO group = new GroupBuilder().withParentCustomer(parent).build();

        // exercise test
        try {
            customerService.transferGroupTo(group, office);
            fail("should fail validation");
        } catch (CustomerException e) {
            assertThat(e.getKey(), is(CustomerConstants.ERRORS_SAME_BRANCH_TRANSFER));
        }
    }

    @Test
    public void givenOfficeAlreadyHasGroupWithSameNameThenGroupTransferToBranchShouldFailValidation() throws Exception {

        // setup
        GroupBO group = new GroupBuilder().withName("already-exists-group").build();
        OfficeBO office = new OfficeBuilder().inActive().build();

        // stubbing
        doThrow(new CustomerException(CustomerConstants.ERRORS_DUPLICATE_CUSTOMER)).when(customerDao).validateGroupNameIsNotTakenForOffice(anyString(), anyShort());

        // exercise test
        try {
            customerService.transferGroupTo(group, office);
            fail("should fail validation");
        } catch (CustomerException e) {
            assertThat(e.getKey(), is(CustomerConstants.ERRORS_TRANSFER_IN_INACTIVE_OFFICE));
        }
    }
}