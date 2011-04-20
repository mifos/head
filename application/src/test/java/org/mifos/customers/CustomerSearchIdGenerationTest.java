/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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
import static org.mockito.Matchers.anyShort;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.servicefacade.CustomerStatusUpdate;
import org.mifos.calendar.CalendarEvent;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.customers.business.CustomerNoteEntity;
import org.mifos.customers.business.service.CustomerAccountFactory;
import org.mifos.customers.business.service.CustomerService;
import org.mifos.customers.business.service.CustomerServiceImpl;
import org.mifos.customers.business.service.MessageLookupHelper;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.domain.builders.CenterBuilder;
import org.mifos.domain.builders.ClientBuilder;
import org.mifos.domain.builders.CustomerStatusUpdateBuilder;
import org.mifos.domain.builders.GroupBuilder;
import org.mifos.domain.builders.OfficeBuilder;
import org.mifos.domain.builders.PersonnelBuilder;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.util.UserContext;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * I test implementation of {@link CustomerService} for update of status of {@link CenterBO}'s.
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomerSearchIdGenerationTest {

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
    private MessageLookupHelper messageLookupHelper;

    @Mock
    private CustomerNoteEntity accountNotesEntity;

    @Mock
    private static ConfigurationPersistence configurationPersistence;

    @Mock
    CalendarEvent calendarEvent;

    private static MifosCurrency oldDefaultCurrency;

    @BeforeClass
    public static void initialiseCurrencyForMoney() {
        oldDefaultCurrency = Money.getDefaultCurrency();
        Money.setDefaultCurrency(TestUtils.RUPEE);
    }

    @AfterClass
    public static void resetCurrency() {
        Money.setDefaultCurrency(oldDefaultCurrency);
    }

    @Before
    public void setupAndInjectDependencies() {
        customerService = new CustomerServiceImpl(customerDao, personnelDao, officeDao, holidayDao, hibernateTransaction);
        ((CustomerServiceImpl)customerService).setCustomerAccountFactory(customerAccountFactory);
        ((CustomerServiceImpl)customerService).setMessageLookupHelper(messageLookupHelper);
        ((CustomerServiceImpl)customerService).setConfigurationPersistence(configurationPersistence);
    }


    @Test
    public void removeGroupMembershipTest() throws Exception {

        // setup
        short localeId = 1;
        CustomerStatusUpdate customerStatusUpdate = new CustomerStatusUpdateBuilder().with(CustomerStatus.GROUP_CANCELLED).build();

        PersonnelBO loanOfficer = PersonnelBuilder.anyLoanOfficer();
        CenterBO existingCenter = new CenterBuilder().withLoanOfficer(loanOfficer).active().withNumberOfExistingCustomersInOffice(1).build();
        GroupBO existingGroup = new GroupBuilder().pendingApproval().withParentCustomer(existingCenter).withVersion(customerStatusUpdate.getVersionNum()).build();
        ClientBO existingClient = new ClientBuilder().withParentCustomer(existingGroup).pendingApproval().buildForUnitTests();
        existingGroup.addChild(existingClient);
        UserContext userContext = TestUtils.makeUser();
        existingGroup.setUserContext(userContext);
        existingClient.setUserContext(userContext);
        existingClient.setCustomerDao(customerDao);

        // stubbing
        when(configurationPersistence.isGlimEnabled()).thenReturn(false);
        when(customerDao.retrieveLastSearchIdValueForNonParentCustomersInOffice(anyShort())).thenReturn(3);
        existingClient = spy(existingClient);
        int customer_id = 22;
        when(existingClient.getCustomerId()).thenReturn(customer_id);
        // exercise test
        assertThat(existingClient.getSearchId(), is("1.1.1.1"));
        customerService.removeGroupMembership(existingClient, loanOfficer, accountNotesEntity, localeId);

        // verification
        assertThat(existingClient.getSearchId(), is("1." + customer_id));
    }

    @Test
    public void transferGroupFromOfficeToOfficeTest() throws Exception {
        final short office1_id = 2;
        // setup
        OfficeBO office1 = new OfficeBuilder().withName("office1").branchOffice().withOfficeId(office1_id).build();
        PersonnelBO loanOfficer = PersonnelBuilder.anyLoanOfficer();
        GroupBO existingGroup = new GroupBuilder().pendingApproval().withLoanOfficer(loanOfficer).buildAsTopOfHierarchy();
        existingGroup = spy(existingGroup);
        int customer_id = 22;
        when(existingGroup.getCustomerId()).thenReturn(customer_id);
        existingGroup.generateSearchId();
        ClientBO existingClient = new ClientBuilder().pendingApproval().withParentCustomer(existingGroup).buildForUnitTests();
        existingGroup.addChild(existingClient);
        existingGroup.setCustomerDao(customerDao);
        UserContext userContext = TestUtils.makeUser();
        existingGroup.setUserContext(userContext);

        // stubbing
        when(customerDao.retrieveLastSearchIdValueForNonParentCustomersInOffice(office1_id)).thenReturn(3);


        // exercise test
        assertThat(existingGroup.getSearchId(), is("1." + customer_id));
        assertThat(existingClient.getSearchId(), is("1." + customer_id + ".1"));
        customerService.transferGroupTo(existingGroup, office1);

        // verification
        assertThat(existingGroup.getSearchId(), is("1." + customer_id));
        assertThat(existingClient.getSearchId(), is("1." + customer_id + ".1"));
    }

    @Test
    public void transferClientFromOfficeToOfficeTest() throws Exception {
        final short office1_id = 2;
        // setup
        OfficeBO office1 = new OfficeBuilder().withName("office1").branchOffice().withOfficeId(office1_id).build();
        PersonnelBO loanOfficer = PersonnelBuilder.anyLoanOfficer();
        ClientBO existingClient = new ClientBuilder().pendingApproval().withNoParent().withLoanOfficer(loanOfficer).buildForUnitTests();
        existingClient.setCustomerDao(customerDao);
        existingClient = spy(existingClient);
        int customer_id = 22;
        when(existingClient.getCustomerId()).thenReturn(customer_id);
        existingClient.generateSearchId();
        UserContext userContext = TestUtils.makeUser();
        existingClient.setUserContext(userContext);

        // stubbing
        when(customerDao.retrieveLastSearchIdValueForNonParentCustomersInOffice(office1_id)).thenReturn(3);

        // exercise test
        assertThat(existingClient.getSearchId(), is("1." + customer_id));
        customerService.transferClientTo(existingClient, office1);

        // verification
        assertThat(existingClient.getSearchId(), is("1." + customer_id));
    }

    @Test
    public void transferClientFromGroupToGroupTest() throws Exception {
        final short office1_id = 2;
        final Integer group1_id = 33;
        final Integer group2_id = 22;
        final int group2_child_count = 8;
        // setup
        UserContext userContext = TestUtils.makeUser();
        OfficeBO office1 = new OfficeBuilder().withName("office1").branchOffice().withOfficeId(office1_id).build();
        PersonnelBO loanOfficer = PersonnelBuilder.anyLoanOfficer();
        GroupBO existingGroup = new GroupBuilder().pendingApproval().withLoanOfficer(loanOfficer).
            with(userContext).buildAsTopOfHierarchy();
        existingGroup = spy(existingGroup);
        when(existingGroup.getCustomerId()).thenReturn(group1_id);
        existingGroup.generateSearchId();
        ClientBO existingClient = new ClientBuilder().pendingApproval().withParentCustomer(existingGroup).
            withVersion(1).buildForUnitTests();
        existingGroup.addChild(existingClient);
        existingGroup.setCustomerDao(customerDao);


        GroupBO existingGroup2 = new GroupBuilder().pendingApproval().withLoanOfficer(loanOfficer).
            with(userContext).withSearchId(group2_child_count).withOffice(office1).buildAsTopOfHierarchy();
        existingGroup2.setCustomerDao(customerDao);
        existingGroup2 = spy(existingGroup2);
        when(existingGroup2.getCustomerId()).thenReturn(group2_id);
        existingGroup2.generateSearchId();

        // stubbing
        when(customerDao.retrieveLastSearchIdValueForNonParentCustomersInOffice(office1_id)).thenReturn(3);
        when(customerDao.findClientBySystemId("clientid")).thenReturn(existingClient);
        when(customerDao.findCustomerById(group2_id)).thenReturn(existingGroup2);
        when(holidayDao.findCalendarEventsForThisYearAndNext(anyShort())).thenReturn(calendarEvent);

        // exercise test
        assertThat(existingGroup.getSearchId(), is("1." + group1_id));
        assertThat(existingClient.getSearchId(), is("1." + group1_id + ".1"));

        customerService.transferClientTo(userContext, group2_id, "clientid", 1);

        // verification
        assertThat(existingGroup2.getSearchId(), is("1." + group2_id));
        assertThat(existingClient.getSearchId(), is("1." + group2_id + "." + (group2_child_count + 1)));
    }

}