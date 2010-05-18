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

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.servicefacade.ClientMfiInfoUpdate;
import org.mifos.application.servicefacade.ClientPersonalInfoUpdate;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.business.service.CustomerService;
import org.mifos.customers.business.service.CustomerServiceImpl;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.client.util.helpers.ClientConstants;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.domain.builders.ClientMfiInfoUpdateBuilder;
import org.mifos.domain.builders.ClientPersonalInfoUpdateBuilder;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.security.util.UserContext;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ClientUpdateTest {

    // class under test
    private CustomerService customerService;

    // collaborators (behaviour mocks)
    @Mock
    private CustomerDao customerDao;

    @Mock
    private PersonnelDao personnelDao;

    @Mock
    private OfficeDao officeDao;

    @Mock
    private HolidayDao holidayDao;

    @Mock
    private HibernateTransactionHelper hibernateTransactionHelper;

    // stubbed data
    @Mock
    private ClientBO mockedClient;

    @Mock
    private PersonnelBO personnel;

    @Before
    public void setupDependencies() {
        customerService = new CustomerServiceImpl(customerDao, personnelDao, officeDao, holidayDao, hibernateTransactionHelper);
    }

    @Test(expected = CustomerException.class)
    public void throwsCheckedExceptionWhenVersionOfClientForUpdateIsDifferentToPersistedClientVersion() throws Exception {

        // setup
        UserContext userContext = TestUtils.makeUser();
        ClientMfiInfoUpdate clientMfiInfoUpdate = new ClientMfiInfoUpdateBuilder().build();

        // stubbing
        when(customerDao.findCustomerById(clientMfiInfoUpdate.getClientId())).thenReturn(mockedClient);
        doThrow(new CustomerException(Constants.ERROR_VERSION_MISMATCH)).when(mockedClient).validateVersion(clientMfiInfoUpdate.getOrginalClientVersionNumber());

        // exercise test
        customerService.updateClientMfiInfo(userContext, clientMfiInfoUpdate);

        // verify
        verify(mockedClient).validateVersion(clientMfiInfoUpdate.getOrginalClientVersionNumber());
    }

    @Test
    public void userContextAndUpdateDetailsAreSetBeforeBeginningAuditLogging() throws Exception {

        // setup
        UserContext userContext = TestUtils.makeUser();
        ClientMfiInfoUpdate clientMfiInfoUpdate = new ClientMfiInfoUpdateBuilder().build();

        // stubbing
        when(customerDao.findCustomerById(clientMfiInfoUpdate.getClientId())).thenReturn(mockedClient);

        // exercise test
        customerService.updateClientMfiInfo(userContext, clientMfiInfoUpdate);

        // verification
        InOrder inOrder = inOrder(hibernateTransactionHelper, mockedClient);
        inOrder.verify(mockedClient).updateDetails(userContext);
        inOrder.verify(hibernateTransactionHelper).beginAuditLoggingFor(mockedClient);
    }

    @Test
    public void clientMfiDetailsAreUpdated() throws Exception {

        // setup
        UserContext userContext = TestUtils.makeUser();
        ClientMfiInfoUpdate clientMfiInfoUpdate = new ClientMfiInfoUpdateBuilder().build();

        // stubbing
        when(customerDao.findCustomerById(clientMfiInfoUpdate.getClientId())).thenReturn(mockedClient);
        when(personnelDao.findPersonnelById(clientMfiInfoUpdate.getPersonnelId())).thenReturn(personnel);

        // exercise test
        customerService.updateClientMfiInfo(userContext, clientMfiInfoUpdate);

        // verification
        verify(mockedClient).updateMfiInfo(personnel, clientMfiInfoUpdate);
    }

    @Test(expected = MifosRuntimeException.class)
    public void rollsbackTransactionClosesSessionAndThrowsRuntimeExceptionWhenExceptionOccurs() throws Exception {

        // setup
        UserContext userContext = TestUtils.makeUser();
        ClientMfiInfoUpdate clientMfiInfoUpdate = new ClientMfiInfoUpdateBuilder().build();

        // stubbing
        when(customerDao.findCustomerById(clientMfiInfoUpdate.getClientId())).thenReturn(mockedClient);
        when(personnelDao.findPersonnelById(clientMfiInfoUpdate.getPersonnelId())).thenReturn(personnel);
        doThrow(new RuntimeException()).when(customerDao).save(mockedClient);

        // exercise test
        customerService.updateClientMfiInfo(userContext, clientMfiInfoUpdate);

        // verification
        verify(hibernateTransactionHelper).rollbackTransaction();
        verify(hibernateTransactionHelper).closeSession();
    }

    @Test(expected = CustomerException.class)
    public void rollsbackTransactionClosesSessionAndReThrowsApplicationException() throws Exception {

        // setup
        UserContext userContext = TestUtils.makeUser();
        ClientMfiInfoUpdate clientMfiInfoUpdate = new ClientMfiInfoUpdateBuilder().build();

        // stubbing
        when(customerDao.findCustomerById(clientMfiInfoUpdate.getClientId())).thenReturn(mockedClient);
        when(personnelDao.findPersonnelById(clientMfiInfoUpdate.getPersonnelId())).thenReturn(personnel);
        doThrow(new CustomerException(CustomerConstants.ERRORS_DUPLICATE_CUSTOMER)).when(mockedClient).updateMfiInfo(personnel, clientMfiInfoUpdate);

        // exercise test
        customerService.updateClientMfiInfo(userContext, clientMfiInfoUpdate);

        // verification
        verify(hibernateTransactionHelper).rollbackTransaction();
        verify(hibernateTransactionHelper).closeSession();
    }

    @Test(expected = CustomerException.class)
    public void throwsCheckedExceptionWhenVersionOfClientForUpdateIsDifferentToPersistedClient() throws Exception {

        // setup
        UserContext userContext = TestUtils.makeUser();
        ClientPersonalInfoUpdate clientPersonalInfoUpdate = new ClientPersonalInfoUpdateBuilder().build();

        // stubbing
        when(customerDao.findCustomerById(clientPersonalInfoUpdate.getCustomerId())).thenReturn(mockedClient);
        doThrow(new CustomerException(Constants.ERROR_VERSION_MISMATCH)).when(mockedClient).validateVersion(clientPersonalInfoUpdate.getOriginalClientVersionNumber());

        // exercise test
        customerService.updateClientPersonalInfo(userContext, clientPersonalInfoUpdate);

        // verify
        verify(mockedClient).validateVersion(clientPersonalInfoUpdate.getOriginalClientVersionNumber());
    }

    @Test
    public void userContextAndUpdateDetailsAreSetBeforeBeginningAuditLoggingForPersonalInfo() throws Exception {

        // setup
        UserContext userContext = TestUtils.makeUser();
        ClientPersonalInfoUpdate clientPersonalInfoUpdate = new ClientPersonalInfoUpdateBuilder().build();

        // stubbing
        when(customerDao.findCustomerById(clientPersonalInfoUpdate.getCustomerId())).thenReturn(mockedClient);

        // exercise test
        customerService.updateClientPersonalInfo(userContext, clientPersonalInfoUpdate);

        // verification
        InOrder inOrder = inOrder(hibernateTransactionHelper, mockedClient);
        inOrder.verify(mockedClient).updateDetails(userContext);
        inOrder.verify(hibernateTransactionHelper).beginAuditLoggingFor(mockedClient);
    }

    @Test
    public void mandatoryAdditionalFieldsAreUpdatedForClientPersonalInfo() throws Exception {

        // setup
        UserContext userContext = TestUtils.makeUser();
        ClientPersonalInfoUpdate clientPersonalInfoUpdate = new ClientPersonalInfoUpdateBuilder().build();

        // stubbing
        when(customerDao.findCustomerById(clientPersonalInfoUpdate.getCustomerId())).thenReturn(mockedClient);
        when(customerDao.retrieveCustomFieldEntitiesForClient()).thenReturn(new ArrayList<CustomFieldDefinitionEntity>());

        // exercise test
        customerService.updateClientPersonalInfo(userContext, clientPersonalInfoUpdate);

        // verification
        verify(mockedClient).updateCustomFields(clientPersonalInfoUpdate.getClientCustomFields());
        verify(mockedClient).validateMandatoryCustomFields(new ArrayList<CustomFieldDefinitionEntity>());
    }

    @Test
    public void clientPersonalDetailsAreUpdated() throws Exception {

        // setup
        UserContext userContext = TestUtils.makeUser();
        ClientPersonalInfoUpdate clientPersonalInfoUpdate = new ClientPersonalInfoUpdateBuilder().build();

        // stubbing
        when(customerDao.findCustomerById(clientPersonalInfoUpdate.getCustomerId())).thenReturn(mockedClient);
        when(customerDao.retrieveCustomFieldEntitiesForClient()).thenReturn(new ArrayList<CustomFieldDefinitionEntity>());

        // exercise test
        customerService.updateClientPersonalInfo(userContext, clientPersonalInfoUpdate);

        // verification
        verify(mockedClient).updatePersonalInfo(clientPersonalInfoUpdate);
    }

    @Test(expected = MifosRuntimeException.class)
    public void rollsbackTransactionClosesSessionAndThrowsRuntimeExceptionWhenExceptionOccurs2() throws Exception {

        // setup
        UserContext userContext = TestUtils.makeUser();
        ClientPersonalInfoUpdate clientPersonalInfoUpdate = new ClientPersonalInfoUpdateBuilder().build();

        // stubbing
        when(customerDao.findCustomerById(clientPersonalInfoUpdate.getCustomerId())).thenReturn(mockedClient);
        when(customerDao.retrieveCustomFieldEntitiesForClient()).thenReturn(new ArrayList<CustomFieldDefinitionEntity>());
        doThrow(new RuntimeException()).when(customerDao).save(mockedClient);

        // exercise test
        customerService.updateClientPersonalInfo(userContext, clientPersonalInfoUpdate);

        // verification
        verify(hibernateTransactionHelper).rollbackTransaction();
        verify(hibernateTransactionHelper).closeSession();
    }

    @Test(expected = CustomerException.class)
    public void rollsbackTransactionClosesSessionAndReThrowsApplicationException2() throws Exception {

        // setup
        UserContext userContext = TestUtils.makeUser();
        ClientPersonalInfoUpdate clientPersonalInfoUpdate = new ClientPersonalInfoUpdateBuilder().build();

        // stubbing
        when(customerDao.findCustomerById(clientPersonalInfoUpdate.getCustomerId())).thenReturn(mockedClient);
        when(customerDao.retrieveCustomFieldEntitiesForClient()).thenReturn(new ArrayList<CustomFieldDefinitionEntity>());
        doThrow(new CustomerException(ClientConstants.INVALID_DOB_EXCEPTION)).when(mockedClient).updatePersonalInfo(clientPersonalInfoUpdate);

        // exercise test
        customerService.updateClientPersonalInfo(userContext, clientPersonalInfoUpdate);

        // verification
        verify(hibernateTransactionHelper).rollbackTransaction();
        verify(hibernateTransactionHelper).closeSession();
    }
}