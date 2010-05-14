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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.application.collectionsheet.persistence.OfficeBuilder;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.servicefacade.CustomerStatusUpdate;
import org.mifos.customers.business.CustomerStatusEntity;
import org.mifos.customers.business.service.CustomerAccountFactory;
import org.mifos.customers.business.service.CustomerService;
import org.mifos.customers.business.service.CustomerServiceImpl;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.domain.builders.CustomerStatusUpdateBuilder;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * I test implementation of {@link CustomerService} for update of status of {@link CenterBO}'s.
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomerStatusUpdateTest {

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

    @Mock
    private CustomerAccountFactory customerAccountFactory;

    @Mock
    private CenterBO mockedCenter;

    @Before
    public void setupAndInjectDependencies() {
        customerService = new CustomerServiceImpl(customerDao, personnelDao, officeDao, holidayDao, hibernateTransaction);
        ((CustomerServiceImpl)customerService).setCustomerAccountFactory(customerAccountFactory);
    }

    @Test(expected=CustomerException.class)
    public void throwsCheckedExceptionWhenVersionOfCustomerForUpdateIsDifferentToPersistedCustomerVersion() throws Exception {

        // setup
        UserContext userContext = TestUtils.makeUser();
        CustomerStatusUpdate customerStatusUpdate = new CustomerStatusUpdateBuilder().build();

        // stubbing
        when(customerDao.findCustomerById(customerStatusUpdate.getCustomerId())).thenReturn(mockedCenter);
        doThrow(new CustomerException(Constants.ERROR_VERSION_MISMATCH)).when(mockedCenter).validateVersion(anyInt());

        // exercise test
        customerService.updateCustomerStatus(userContext, customerStatusUpdate);
    }

    @Test
    public void updateDetailsAreSetWhenUpdatingCustomerStatus() throws Exception {

        // setup
        OfficeBO office = new OfficeBuilder().withGlobalOfficeNum("xxx-9999").build();
        UserContext userContext = TestUtils.makeUser();
        CustomerStatusUpdate customerStatusUpdate = new CustomerStatusUpdateBuilder().build();

        // stubbing
        when(customerDao.findCustomerById(customerStatusUpdate.getCustomerId())).thenReturn(mockedCenter);
        when(mockedCenter.getOffice()).thenReturn(office);
        when(mockedCenter.getCustomerStatus()).thenReturn(new CustomerStatusEntity(CustomerStatus.CENTER_ACTIVE));

        // exercise test
        customerService.updateCustomerStatus(userContext, customerStatusUpdate);

        // verification
        verify(mockedCenter).updateDetails(userContext);
    }

    @Test(expected=CustomerException.class)
    public void throwsCheckedExceptionWhenUserDoesNotHavePermissionToUpdateCustomerStatus() throws Exception {

        // setup
        UserContext userContext = TestUtils.makeUser();
        CustomerStatusUpdate customerStatusUpdate = new CustomerStatusUpdateBuilder().build();
        CenterBO existingCenter = new CenterBuilder().withVersion(customerStatusUpdate.getVersionNum()).build();

        // stubbing
        when(customerDao.findCustomerById(customerStatusUpdate.getCustomerId())).thenReturn(existingCenter);
        doThrow(new CustomerException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED)).when(customerDao).checkPermissionForStatusChange(anyShort(), eq(userContext), anyShort(), anyShort(), anyShort());

        // exercise test
        customerService.updateCustomerStatus(userContext, customerStatusUpdate);

        // verification
        verify(customerDao).checkPermissionForStatusChange(customerStatusUpdate.getNewStatus().getValue(), userContext, customerStatusUpdate.getCustomerStatusFlag().getValue(), existingCenter.getOffice().getOfficeId(), existingCenter.getPersonnel().getPersonnelId());
    }
}