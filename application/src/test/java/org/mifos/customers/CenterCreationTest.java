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
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mifos.domain.builders.PersonnelBuilder.anyLoanOfficer;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.LookUpEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.calendar.CalendarEvent;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.business.CustomerAccountBO;
import org.mifos.customers.business.service.CustomerAccountFactory;
import org.mifos.customers.business.service.CustomerService;
import org.mifos.customers.business.service.CustomerServiceImpl;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.domain.builders.CalendarEventBuilder;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * I test implementation of {@link CustomerService} for creation of {@link CenterBO}'s.
 */
@RunWith(MockitoJUnitRunner.class)
public class CenterCreationTest {

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
    private CustomerAccountFactory customerAccountFactory;

    // stubbed data
    @Mock
    private CenterBO mockedCenter;

    @Mock
    private MeetingBO meeting;

    @Mock
    private CustomerAccountBO customerAccount;

    @Before
    public void setupAndInjectDependencies() {
        customerService = new CustomerServiceImpl(customerDao, personnelDao, officeDao, holidayDao, hibernateTransaction);
        ((CustomerServiceImpl)customerService).setCustomerAccountFactory(customerAccountFactory);
    }


    @Test(expected = CustomerException.class)
    public void throwsCheckedExceptionWhenCenterValidationFails() throws Exception {

        // setup
        List<AccountFeesEntity> accountFees = new ArrayList<AccountFeesEntity>();

        // stubbing
        doThrow(new CustomerException(CustomerConstants.ERRORS_SPECIFY_NAME)).when(mockedCenter).validate();

        // exercise test
        customerService.createCenter(mockedCenter, meeting, accountFees);

        // verification
        verify(mockedCenter).validate();
    }

    @Test(expected = CustomerException.class)
    public void throwsCheckedExceptionWhenCenterMeetingAndFeesFailValidation() throws Exception {

        // setup
        List<AccountFeesEntity> accountFees = new ArrayList<AccountFeesEntity>();

        // stubbing
        doThrow(new CustomerException(CustomerConstants.ERRORS_SPECIFY_MEETING)).when(mockedCenter).validateMeetingAndFees(accountFees);

        // exercise test
        customerService.createCenter(mockedCenter, meeting, accountFees);

        // verification
        verify(mockedCenter).validateMeetingAndFees(accountFees);
    }

    @Test
    public void createsCenterWithCustomerAccount() throws Exception {

        // setup
        OfficeBO withOffice = new OfficeBO(new Short("1"), "testOffice",new Integer("1"),new Short("1"));
        CenterBO center = new CenterBuilder().withLoanOfficer(anyLoanOfficer()).with(withOffice).build();
        List<AccountFeesEntity> accountFees = new ArrayList<AccountFeesEntity>();

        // stub
        CalendarEvent upcomingCalendarEvents = new CalendarEventBuilder().build();
        when(holidayDao.findCalendarEventsForThisYearAndNext((short)1)).thenReturn(upcomingCalendarEvents);
        when(customerAccountFactory.create(center, accountFees, meeting, upcomingCalendarEvents)).thenReturn(customerAccount);
        when(customerAccount.getType()).thenReturn(AccountTypes.CUSTOMER_ACCOUNT);

        // exercise test
        customerService.createCenter(center, meeting, accountFees);

        // verification
        assertThat(center.getCustomerAccount(), is(customerAccount));
    }

    @Test(expected = MifosRuntimeException.class)
    public void rollsbackTransactionClosesSessionAndThrowsRuntimeExceptionWhenExceptionOccurs() throws Exception {

        // setup
        CenterBO center = new CenterBuilder().withLoanOfficer(anyLoanOfficer()).build();
        List<AccountFeesEntity> accountFees = new ArrayList<AccountFeesEntity>();

        // stub
        CalendarEvent upcomingCalendarEvents = new CalendarEventBuilder().build();
        when(holidayDao.findCalendarEventsForThisYearAndNext((short)1)).thenReturn(upcomingCalendarEvents);
        when(customerAccountFactory.create(center, accountFees, meeting, upcomingCalendarEvents)).thenReturn(customerAccount);
        when(customerAccount.getType()).thenReturn(AccountTypes.CUSTOMER_ACCOUNT);

        // stub
        doThrow(new RuntimeException()).when(customerDao).save(center);

        // exercise test
        customerService.createCenter(center, meeting, accountFees);

        // verification
        verify(hibernateTransaction).rollbackTransaction();
        verify(hibernateTransaction).closeSession();
    }

    @Test
    public void cannotCreateCenterWithMandatoryAdditionalFieldsNotPopulated() {

        // setup
        DateTime today = new DateTime();
        MeetingBuilder aWeeklyMeeting = new MeetingBuilder().customerMeeting().weekly();
        CenterBO centerWithNoCustomFields = new CenterBuilder().withName("center1").withLoanOfficer(anyLoanOfficer()).with(aWeeklyMeeting).withMfiJoiningDate(today).build();

        List<AccountFeesEntity> noAccountFees = new ArrayList<AccountFeesEntity>();

        LookUpEntity name = null;
        Short fieldIndex = Short.valueOf("1");
        CustomFieldType fieldType = CustomFieldType.ALPHA_NUMERIC;
        EntityType entityType = EntityType.CENTER;
        String defaultValue = "defalutValue";
        YesNoFlag mandatory = YesNoFlag.YES;

        CustomFieldDefinitionEntity mandatoryDefinition = new CustomFieldDefinitionEntity(name, fieldIndex, fieldType, entityType, defaultValue, mandatory);
        List<CustomFieldDefinitionEntity> mandatoryCustomFieldDefinitions = new ArrayList<CustomFieldDefinitionEntity>();
        mandatoryCustomFieldDefinitions.add(mandatoryDefinition);

        // stub
        when(customerDao.retrieveCustomFieldEntitiesForCenter()).thenReturn(mandatoryCustomFieldDefinitions);

        // exercise test
        try {
            customerService.createCenter(centerWithNoCustomFields, aWeeklyMeeting.build(), noAccountFees);
            fail("cannotCreateCenterWithMandatoryAdditionalFieldsNotEntered");
        } catch (ApplicationException e) {
            assertThat(e.getKey(), is(CustomerConstants.ERRORS_SPECIFY_CUSTOM_FIELD_VALUE));
        }
    }
}