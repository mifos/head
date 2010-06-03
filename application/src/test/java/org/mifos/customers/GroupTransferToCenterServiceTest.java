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

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.customers.business.service.CustomerService;
import org.mifos.customers.business.service.CustomerServiceImpl;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class GroupTransferToCenterServiceTest {

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
    private GroupBO group;

    @Before
    public void setupAndInjectDependencies() {
        customerService = new CustomerServiceImpl(customerDao, personnelDao, officeDao, holidayDao, hibernateTransaction);
    }

    @Test(expected = CustomerException.class)
    public void validatesReceivingCenterWhenTransferingGroup() throws Exception {

        // setup
        CenterBO toCenter = new CenterBuilder().build();

        // stubbing
        doThrow(new CustomerException(CustomerConstants.INVALID_PARENT)).when(group).validateReceivingCenter(toCenter);

        // exercise test
        customerService.transferGroupTo(group, toCenter);

        // verification
        verify(group).validateReceivingCenter(toCenter);
    }


    @Test(expected = CustomerException.class)
    public void validatesNoActiveAccountExistWhenTransferingGroup() throws Exception {

        // setup
        CenterBO toCenter = new CenterBuilder().build();

        // stubbing
        doThrow(new CustomerException(CustomerConstants.ERRORS_HAS_ACTIVE_ACCOUNT)).when(group).validateNoActiveAccountsExist();

        // exercise test
        customerService.transferGroupTo(group, toCenter);

        // verification
        verify(group).validateNoActiveAccountsExist();
    }

    @Test(expected = CustomerException.class)
    public void validatesGroupNameIsNotAlreadyTakenWhenTransferingGroupToDifferentOffice() throws Exception {

        // setup
        CenterBO toCenter = new CenterBuilder().build();

        // stubbing
        when(group.isDifferentBranch(toCenter.getOffice())).thenReturn(true);
        doThrow(new CustomerException(CustomerConstants.ERRORS_DUPLICATE_CUSTOMER)).when(customerDao).validateGroupNameIsNotTakenForOffice(anyString(), eq(toCenter.getOfficeId()));

        // exercise test
        customerService.transferGroupTo(group, toCenter);

        // verification
        verify(customerDao).validateGroupNameIsNotTakenForOffice(anyString(), eq(toCenter.getOfficeId()));
    }

    @Test
    public void transfersGroupToReceivingCenter() throws Exception {

        // setup
        CenterBO toCenter = new CenterBuilder().build();

        // stubbing
        when(group.isDifferentBranch(toCenter.getOffice())).thenReturn(false);
        when(group.getUserContext()).thenReturn(TestUtils.makeUser());

        // exercise test
        customerService.transferGroupTo(group, toCenter);

        // verification
        verify(group).transferTo(toCenter);
    }

    @Test(expected=RuntimeException.class)
    public void rollsbackTransactionOnException() throws Exception {

        // setup
        CenterBO toCenter = new CenterBuilder().build();

        // stubbing
        when(group.isDifferentBranch(toCenter.getOffice())).thenReturn(false);
        when(group.getUserContext()).thenReturn(TestUtils.makeUser());
        doThrow(new RuntimeException()).when(customerDao).save(group);

        // exercise test
        customerService.transferGroupTo(group, toCenter);

        // verification
        verify(hibernateTransaction).rollbackTransaction();
    }
}