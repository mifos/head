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

package org.mifos.customers.business.service;

import static org.mifos.domain.builders.PersonnelBuilder.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.application.collectionsheet.persistence.GroupBuilder;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.calendar.CalendarEvent;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.business.CustomerAccountBO;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.domain.builders.CalendarEventBuilder;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GroupCreationTest {

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

    @Mock
    private MeetingBO meeting;

    @Mock
    private CustomerAccountBO customerAccount;

    @Before
    public void setupDependencies() {
        customerService = new CustomerServiceImpl(customerDao, personnelDao, officeDao, holidayDao, hibernateTransactionHelper);
        ((CustomerServiceImpl)customerService).setCustomerAccountFactory(customerAccountFactory);
    }

    @Test(expected = CustomerException.class)
    public void throwsCheckedExceptionWhenGroupValidationFails() throws Exception {

        // setup
        List<AccountFeesEntity> accountFees = new ArrayList<AccountFeesEntity>();

        // stubbing
        doThrow(new CustomerException(CustomerConstants.ERRORS_SPECIFY_NAME)).when(mockedGroup).validate();

        // exercise test
        customerService.createGroup(mockedGroup, meeting, accountFees);
    }

    @Test(expected = CustomerException.class)
    public void throwsCheckedExceptionWhenGroupNameIsTakenForOfficeAlready() throws Exception {

        // setup
        CenterBO parent = new CenterBuilder().withLoanOfficer(anyLoanOfficer()).build();
        GroupBO stubbedGroup = new GroupBuilder().withName("already-exists-group").withParentCustomer(parent).formedBy(anyLoanOfficer()).build();
        List<AccountFeesEntity> accountFees = new ArrayList<AccountFeesEntity>();

        // stubbing
        doThrow(new CustomerException(CustomerConstants.ERRORS_DUPLICATE_CUSTOMER)).when(customerDao).validateGroupNameIsNotTakenForOffice(anyString(), anyShort());

        // exercise test
        customerService.createGroup(stubbedGroup, meeting, accountFees);
    }

    @Test
    public void createsGroupWithCustomerAccount() throws Exception {

        // setup
        CenterBO parent = new CenterBuilder().withLoanOfficer(anyLoanOfficer()).build();
        GroupBO stubbedGroup = new GroupBuilder().withName("group").withParentCustomer(parent).formedBy(anyLoanOfficer()).build();
        List<AccountFeesEntity> accountFees = new ArrayList<AccountFeesEntity>();

        // stub
        CalendarEvent upcomingCalendarEvents = new CalendarEventBuilder().build();
        when(holidayDao.findCalendarEventsForThisYearAndNext()).thenReturn(upcomingCalendarEvents);
        when(customerAccountFactory.create(stubbedGroup, accountFees, meeting, upcomingCalendarEvents)).thenReturn(customerAccount);
        when(customerAccount.getType()).thenReturn(AccountTypes.CUSTOMER_ACCOUNT);

        // exercise test
        customerService.createGroup(stubbedGroup, meeting, accountFees);

        // verification
        assertThat(stubbedGroup.getCustomerAccount(), is(customerAccount));
    }

    @Test(expected = MifosRuntimeException.class)
    public void rollsbackTransactionClosesSessionAndThrowsRuntimeExceptionWhenExceptionOccurs() throws Exception {

        // setup
        CenterBO parent = new CenterBuilder().withLoanOfficer(anyLoanOfficer()).build();
        GroupBO stubbedGroup = new GroupBuilder().withName("group").withParentCustomer(parent).formedBy(anyLoanOfficer()).build();
        List<AccountFeesEntity> accountFees = new ArrayList<AccountFeesEntity>();

        // stub
        doThrow(new RuntimeException()).when(customerDao).save(stubbedGroup);

        // exercise test
        customerService.createGroup(stubbedGroup, meeting, accountFees);

        // verification
        verify(hibernateTransactionHelper).rollbackTransaction();
        verify(hibernateTransactionHelper).closeSession();
    }
}