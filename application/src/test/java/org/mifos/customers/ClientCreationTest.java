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
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.LookUpEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.customers.business.service.CustomerAccountFactory;
import org.mifos.customers.business.service.CustomerService;
import org.mifos.customers.business.service.CustomerServiceImpl;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.client.util.helpers.ClientConstants;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ClientCreationTest {

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
    private ClientBO mockedClient;

    @Mock
    private MeetingBO meeting;

    @Before
    public void setupDependencies() {
        customerService = new CustomerServiceImpl(customerDao, personnelDao, officeDao, holidayDao, hibernateTransactionHelper);
        ((CustomerServiceImpl)customerService).setCustomerAccountFactory(customerAccountFactory);
    }

    @Test(expected = CustomerException.class)
    public void throwsCheckedExceptionWhenClientValidationFails() throws Exception {

        // setup
        List<AccountFeesEntity> accountFees = new ArrayList<AccountFeesEntity>();
        List<SavingsOfferingBO> noSavings = new ArrayList<SavingsOfferingBO>();

        // stubbing
        doThrow(new CustomerException(ClientConstants.ERRORS_GROUP_CANCELLED)).when(mockedClient).validate();

        // exercise test
        customerService.createClient(mockedClient, meeting, accountFees, noSavings);

        // verify
        verify(mockedClient).validate();
    }

    @Test(expected = CustomerException.class)
    public void throwsCheckedExceptionWhenValidationForDuplicateSavingsFails() throws Exception {

        // setup
        List<AccountFeesEntity> accountFees = new ArrayList<AccountFeesEntity>();
        List<SavingsOfferingBO> noSavings = new ArrayList<SavingsOfferingBO>();

        // stubbing
        doThrow(new CustomerException(ClientConstants.ERRORS_DUPLICATE_OFFERING_SELECTED)).when(mockedClient).validateNoDuplicateSavings(noSavings);

        // exercise test
        customerService.createClient(mockedClient, meeting, accountFees, noSavings);

        // verify
        verify(mockedClient).validateNoDuplicateSavings(noSavings);
    }

    @Test(expected = CustomerException.class)
    public void throwsCheckedExceptionWhenValidationForDuplicateNameOrGovernmentId() throws Exception {

        // setup
        List<AccountFeesEntity> accountFees = new ArrayList<AccountFeesEntity>();
        List<SavingsOfferingBO> noSavings = new ArrayList<SavingsOfferingBO>();

        // stubbing
        doThrow(new CustomerException(CustomerConstants.DUPLICATE_GOVT_ID_EXCEPTION)).when(customerDao).validateClientForDuplicateNameOrGovtId(mockedClient);

        // exercise test
        customerService.createClient(mockedClient, meeting, accountFees, noSavings);

        // verify
        verify(customerDao).validateClientForDuplicateNameOrGovtId(mockedClient);
    }

    @Test(expected = CustomerException.class)
    public void throwsCheckedExceptionWhenValidationForMandatoryAdditionalFieldsNotPopulated() throws Exception {

        // setup
        List<AccountFeesEntity> accountFees = new ArrayList<AccountFeesEntity>();
        List<SavingsOfferingBO> noSavings = new ArrayList<SavingsOfferingBO>();

        LookUpEntity name = null;
        Short fieldIndex = Short.valueOf("1");
        CustomFieldType fieldType = CustomFieldType.ALPHA_NUMERIC;
        EntityType entityType = EntityType.CENTER;
        String defaultValue = "defalutValue";
        YesNoFlag mandatory = YesNoFlag.YES;

        CustomFieldDefinitionEntity mandatoryDefinition = new CustomFieldDefinitionEntity(name, fieldIndex, fieldType, entityType, defaultValue, mandatory);
        List<CustomFieldDefinitionEntity> mandatoryCustomFieldDefinitions = new ArrayList<CustomFieldDefinitionEntity>();
        mandatoryCustomFieldDefinitions.add(mandatoryDefinition);

        // stubbing
        when(customerDao.retrieveCustomFieldEntitiesForClient()).thenReturn(mandatoryCustomFieldDefinitions);
        doThrow(new CustomerException(CustomerConstants.DUPLICATE_GOVT_ID_EXCEPTION)).when(mockedClient).validateMandatoryCustomFields(mandatoryCustomFieldDefinitions);

        // exercise test
        customerService.createClient(mockedClient, meeting, accountFees, noSavings);

        // verify
        verify(mockedClient).validateMandatoryCustomFields(mandatoryCustomFieldDefinitions);
    }
}