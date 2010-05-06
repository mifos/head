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

import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldDto;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.LookUpEntity;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.servicefacade.CenterUpdate;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.customers.business.CustomerPositionDto;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.domain.builders.PersonnelBuilder;
import org.mifos.framework.TestUtils;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.util.UserContext;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * I test implementation of {@link CustomerService} for update of {@link CenterBO}'s.
 */
@RunWith(MockitoJUnitRunner.class)
public class CenterUpdateTest {

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
        customerService = new CustomerServiceImpl(customerDao, personnelDao, officeDao, holidayDao);
    }

    @Test
    public void cannotUpdateCenterWithDifferentVersion() {

        // setup
        int versionNum = 1;
        int differentVersionNum = -1;
        CenterBO existingCenter = new CenterBuilder().withLoanOfficer(PersonnelBuilder.anyLoanOfficer()).withVersion(versionNum).build();

        int customerId = -1;
        Short loanOfficerId = existingCenter.getPersonnel().getPersonnelId();
        String externalId = existingCenter.getExternalId();
        String mfiJoiningDate = new SimpleDateFormat("dd/MM/yyyy").format(existingCenter.getMfiJoiningDate());
        Address address = existingCenter.getAddress();
        List<CustomFieldDto> customFields = new ArrayList<CustomFieldDto>();
        List<CustomerPositionDto> customerPositions = new ArrayList<CustomerPositionDto>();

        CenterUpdate centerUpdate = new CenterUpdate(customerId, differentVersionNum, loanOfficerId, externalId, mfiJoiningDate, address, customFields, customerPositions);

        UserContext userContext = TestUtils.makeUser();

        // stub
        when(customerDao.findCustomerById(customerId)).thenReturn(existingCenter);

        // exercise test
        try {
            customerService.updateCenter(userContext, centerUpdate);
            fail("cannotUpdateCenterWithDifferentVersion");
        } catch (ApplicationException e) {
            assertThat(e.getKey(), is(Constants.ERROR_VERSION_MISMATCH));
        }
    }

    @Test
    public void cannotUpdateCenterWithNoLoanOfficerWhenCenterIsActive() {

        // setup
        int versionNum = 1;
        CenterBO existingCenter = new CenterBuilder().withLoanOfficer(PersonnelBuilder.anyLoanOfficer()).withVersion(versionNum).build();

        int customerId = -1;
        Short loanOfficerId = null;
        String externalId = existingCenter.getExternalId();
        String mfiJoiningDate = new SimpleDateFormat("dd/MM/yyyy").format(existingCenter.getMfiJoiningDate());
        Address address = existingCenter.getAddress();
        List<CustomFieldDto> customFields = new ArrayList<CustomFieldDto>();
        List<CustomerPositionDto> customerPositions = new ArrayList<CustomerPositionDto>();

        CenterUpdate centerUpdate = new CenterUpdate(customerId, versionNum, loanOfficerId, externalId, mfiJoiningDate, address, customFields, customerPositions);

        UserContext userContext = TestUtils.makeUser();

        // stub
        when(customerDao.findCustomerById(customerId)).thenReturn(existingCenter);

        // exercise test
        try {
            customerService.updateCenter(userContext, centerUpdate);
            fail("cannotUpdateCenterWithNoLoanOfficerWhenCenterIsActive");
        } catch (ApplicationException e) {
            assertThat(e.getKey(), is(CustomerConstants.INVALID_LOAN_OFFICER));
        }
    }


    @Test
    public void cannotUpdateCenterWithInvalidMfiJoiningDate() {

        // setup
        int versionNum = 1;
        PersonnelBO existingLoanOfficer = PersonnelBuilder.anyLoanOfficer();
        CenterBO existingCenter = new CenterBuilder().withLoanOfficer(existingLoanOfficer).withVersion(versionNum).build();

        int customerId = -1;
        Short loanOfficerId = -1;
        String externalId = existingCenter.getExternalId();
        String invalidMfiJoiningDate = "23";
        Address address = existingCenter.getAddress();
        List<CustomFieldDto> customFields = new ArrayList<CustomFieldDto>();
        List<CustomerPositionDto> customerPositions = new ArrayList<CustomerPositionDto>();

        CenterUpdate centerUpdate = new CenterUpdate(customerId, versionNum, loanOfficerId, externalId, invalidMfiJoiningDate, address, customFields, customerPositions);

        UserContext userContext = TestUtils.makeUser();

        // stub
        when(customerDao.findCustomerById(customerId)).thenReturn(existingCenter);
        when(personnelDao.findPersonnelById(anyShort())).thenReturn(existingLoanOfficer);

        // exercise test
        try {
            customerService.updateCenter(userContext, centerUpdate);
            fail("cannotUpdateCenterWithInvalidMfiJoiningDate");
        } catch (ApplicationException e) {
            assertThat(e.getKey(), is(CustomerConstants.MFI_JOINING_DATE_MANDATORY));
        }
    }

    @Test
    public void cannotUpdateCenterWithMandatoryAdditionalFieldsNotPopulated() {

        // setup
        int versionNum = 1;
        PersonnelBO existingLoanOfficer = PersonnelBuilder.anyLoanOfficer();
        CenterBO existingCenter = new CenterBuilder().withLoanOfficer(existingLoanOfficer).withVersion(versionNum).build();

        int customerId = -1;
        Short loanOfficerId = -1;
        String externalId = existingCenter.getExternalId();
        String mfiJoiningDate = "23/01/2010";
        Address address = existingCenter.getAddress();
        List<CustomFieldDto> customFields = new ArrayList<CustomFieldDto>();
        List<CustomerPositionDto> customerPositions = new ArrayList<CustomerPositionDto>();

        CenterUpdate centerUpdate = new CenterUpdate(customerId, versionNum, loanOfficerId, externalId, mfiJoiningDate, address, customFields, customerPositions);

        UserContext userContext = TestUtils.makeUser();

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
        when(customerDao.findCustomerById(customerId)).thenReturn(existingCenter);
        when(personnelDao.findPersonnelById(anyShort())).thenReturn(existingLoanOfficer);
        when(customerDao.retrieveCustomFieldEntitiesForCenter()).thenReturn(mandatoryCustomFieldDefinitions);

        // exercise test
        try {
            customerService.updateCenter(userContext, centerUpdate);
            fail("cannotUpdateCenterWithMandatoryAdditionalFieldsNotPopulated");
        } catch (ApplicationException e) {
            assertThat(e.getKey(), is(CustomerConstants.ERRORS_SPECIFY_CUSTOM_FIELD_VALUE));
        }
    }
}