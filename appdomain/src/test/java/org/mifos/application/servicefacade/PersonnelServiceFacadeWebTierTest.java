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

package org.mifos.application.servicefacade;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.admin.servicefacade.PersonnelServiceFacade;
import org.mifos.config.persistence.ApplicationConfigurationDao;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.domain.builders.OfficeBuilder;
import org.mifos.dto.screen.DefinePersonnelDto;
import org.mifos.security.rolesandpermission.persistence.LegacyRolesPermissionsDao;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PersonnelServiceFacadeWebTierTest {

    private PersonnelServiceFacade personnelServiceFacade;

    @Mock private OfficeDao officeDao;
    @Mock private CustomerDao customerDao;
    @Mock private PersonnelDao personnelDao;
    @Mock private ApplicationConfigurationDao applicationConfigurationDao;
    @Mock private LegacyRolesPermissionsDao rolesPermissionsPersistence;

    @Before
    public void setup() {
        personnelServiceFacade = new PersonnelServiceFacadeWebTier(officeDao, customerDao, personnelDao, applicationConfigurationDao, rolesPermissionsPersistence);
    }

    @Test
    public void shouldRetrieveOfficeName() {

        // setup
        Short officeId = Short.valueOf("3");
        OfficeBO office = new OfficeBuilder().withName("officeTestName").build();
        when(officeDao.findOfficeById(officeId)).thenReturn(office);

        // exercise test
        DefinePersonnelDto personnelDto = this.personnelServiceFacade.retrieveInfoForNewUserDefinition(officeId);

        // verification
        assertThat(personnelDto.getOfficeName(), is("officeTestName"));
    }

}
