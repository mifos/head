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

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.collectionsheet.persistence.OfficeBuilder;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.servicefacade.GroupUpdate;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.business.service.CustomerAccountFactory;
import org.mifos.customers.business.service.CustomerService;
import org.mifos.customers.business.service.CustomerServiceImpl;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.domain.builders.GroupUpdateBuilder;
import org.mifos.domain.builders.PersonnelBuilder;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.security.util.UserContext;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GroupUpdateTest {

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

    @Mock
    private CustomerAccountFactory customerAccountFactory;

    // stubbed data
    @Mock
    private GroupBO mockedGroup;

    @Before
    public void setupDependencies() {
        customerService = new CustomerServiceImpl(customerDao, personnelDao, officeDao, holidayDao, hibernateTransactionHelper);
        ((CustomerServiceImpl)customerService).setCustomerAccountFactory(customerAccountFactory);
    }

    @Test(expected = CustomerException.class)
    public void throwsCheckedExceptionWhenVersionOfGroupForUpdateIsDifferentToPersistedGroupVersion() throws Exception {

        // setup
        UserContext userContext = TestUtils.makeUser();
        GroupUpdate groupUpdate = new GroupUpdateBuilder().build();

        // stubbing
        when(customerDao.findGroupBySystemId(groupUpdate.getGlobalCustNum())).thenReturn(mockedGroup);
        doThrow(new CustomerException(Constants.ERROR_VERSION_MISMATCH)).when(mockedGroup).validateVersion(groupUpdate.getVersionNo());

        // exercise test
        customerService.updateGroup(userContext, groupUpdate);
    }

    @Test
    public void userContextIsSetBeforeBeginningAuditLogging() throws Exception {

        // setup
        UserContext userContext = TestUtils.makeUser();
        GroupUpdate groupUpdate = new GroupUpdateBuilder().build();

        // stubbing
        when(customerDao.findGroupBySystemId(groupUpdate.getGlobalCustNum())).thenReturn(mockedGroup);

        // exercise test
        customerService.updateGroup(userContext, groupUpdate);

        // verification
        InOrder inOrder = inOrder(hibernateTransactionHelper, mockedGroup);
        inOrder.verify(mockedGroup).setUserContext(userContext);
        inOrder.verify(hibernateTransactionHelper).beginAuditLoggingFor(mockedGroup);
    }

    @Test
    public void trainedDetailsAreUpdated() throws Exception {

        // setup
        UserContext userContext = TestUtils.makeUser();
        GroupUpdate groupUpdate = new GroupUpdateBuilder().build();

        // stubbing
        when(customerDao.findGroupBySystemId(groupUpdate.getGlobalCustNum())).thenReturn(mockedGroup);

        // exercise test
        customerService.updateGroup(userContext, groupUpdate);

        // verification
        verify(mockedGroup).updateTrainedDetails(groupUpdate);
    }

    @Test
    public void addressAndExternalIdAreUpdated() throws Exception {

        // setup
        UserContext userContext = TestUtils.makeUser();
        GroupUpdate groupUpdate = new GroupUpdateBuilder().build();

        // stubbing
        when(customerDao.findGroupBySystemId(groupUpdate.getGlobalCustNum())).thenReturn(mockedGroup);

        // exercise test
        customerService.updateGroup(userContext, groupUpdate);

        // verification
        verify(mockedGroup).setExternalId(groupUpdate.getExternalId());
        verify(mockedGroup).updateAddress(groupUpdate.getAddress());
    }

    @Test
    public void validatesNameOfGroupIsNotTakenWhenNameIsUpdatedToDifferentValue() throws Exception {

        // setup
        UserContext userContext = TestUtils.makeUser();
        GroupUpdate groupUpdate = new GroupUpdateBuilder().build();

        // stubbing
        when(customerDao.findGroupBySystemId(groupUpdate.getGlobalCustNum())).thenReturn(mockedGroup);
        when(mockedGroup.isNameDifferent(groupUpdate.getDisplayName())).thenReturn(true);
        when(mockedGroup.getOffice()).thenReturn(new OfficeBuilder().build());

        // exercise test
        customerService.updateGroup(userContext, groupUpdate);

        // verification
        verify(mockedGroup).setDisplayName(groupUpdate.getDisplayName());
        verify(customerDao).validateGroupNameIsNotTakenForOffice(anyString(), anyShort());
    }

    @Test
    public void updatesLoanOfficerForAllChildrenAndThierAccountsWhenLoanOfficerIsUpdated() throws Exception {

        // setup
        UserContext userContext = TestUtils.makeUser();
        GroupUpdate groupUpdate = new GroupUpdateBuilder().build();
        PersonnelBO newLoanOfficer = new PersonnelBuilder().anyLoanOfficer();

        // stubbing
        when(customerDao.findGroupBySystemId(groupUpdate.getGlobalCustNum())).thenReturn(mockedGroup);
        when(mockedGroup.isNameDifferent(groupUpdate.getDisplayName())).thenReturn(false);
        when(personnelDao.findPersonnelById(groupUpdate.getLoanOfficerId())).thenReturn(newLoanOfficer);
        when(mockedGroup.isLoanOfficerChanged(newLoanOfficer)).thenReturn(true);
        when(mockedGroup.getOffice()).thenReturn(new OfficeBuilder().build());

        // exercise test
        customerService.updateGroup(userContext, groupUpdate);

        // verification
        verify(mockedGroup).setLoanOfficer(newLoanOfficer);
        verify(mockedGroup).validate();
        verify(customerDao).updateLoanOfficersForAllChildrenAndAccounts(anyShort(), anyString(), anyShort());
    }

    @Test(expected = MifosRuntimeException.class)
    public void rollsbackTransactionClosesSessionAndThrowsRuntimeExceptionWhenExceptionOccurs() throws Exception {

        // setup
        UserContext userContext = TestUtils.makeUser();
        GroupUpdate groupUpdate = new GroupUpdateBuilder().build();
        PersonnelBO newLoanOfficer = new PersonnelBuilder().anyLoanOfficer();

        // stubbing
        when(customerDao.findGroupBySystemId(groupUpdate.getGlobalCustNum())).thenReturn(mockedGroup);
        when(mockedGroup.isNameDifferent(groupUpdate.getDisplayName())).thenReturn(false);
        when(personnelDao.findPersonnelById(groupUpdate.getLoanOfficerId())).thenReturn(newLoanOfficer);
        when(mockedGroup.isLoanOfficerChanged(newLoanOfficer)).thenReturn(false);
        when(mockedGroup.getOffice()).thenReturn(new OfficeBuilder().build());

        // exercise test
        customerService.updateGroup(userContext, groupUpdate);

        // stub
        doThrow(new RuntimeException()).when(customerDao).save(mockedGroup);

        // exercise test
        customerService.updateGroup(userContext, groupUpdate);

        // verification
        verify(hibernateTransactionHelper).rollbackTransaction();
        verify(hibernateTransactionHelper).closeSession();
    }
}