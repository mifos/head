/*
 * Copyright Grameen Foundation USA
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
package org.mifos.customers.personnel.persistence;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.domain.builders.PersonnelBuilder;
import org.mifos.dto.domain.UserSearchDto;
import org.mifos.dto.screen.SystemUserSearchResultsDto;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.StandardTestingService;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.MifosUser;
import org.mifos.service.test.TestMode;
import org.mifos.test.framework.util.DatabaseCleaner;
import org.springframework.beans.factory.annotation.Autowired;

public class PersonnelDaoHibernateIntegrationTest extends MifosIntegrationTestCase {

    // class under test
    @Autowired
    private PersonnelDao personnelDao;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    private static MifosCurrency oldDefaultCurrency;

    @BeforeClass
    public static void initialiseHibernateUtil() {

        oldDefaultCurrency = Money.getDefaultCurrency();
        Money.setDefaultCurrency(TestUtils.RUPEE);
        new StandardTestingService().setTestMode(TestMode.INTEGRATION);
    }

    @AfterClass
    public static void resetCurrency() {
        Money.setDefaultCurrency(oldDefaultCurrency);
    }

    @After
    public void cleanDatabaseTablesAfterTest() {
        // NOTE: - only added to stop older integration tests failing due to brittleness
        databaseCleaner.clean();
    }

    @Before
    public void setUp() throws Exception {
        databaseCleaner.clean();
    }

    @Test
    public void shouldFindPersonellByUsername() {

        // exercise test
        PersonnelBO user = personnelDao.findPersonnelByUsername("mifos");

        // verification
        assertNotNull(user);
    }

    @Test
    public void shouldFindAppUserByUsernameGivenCustomerExists() {

        // exercise test
        MifosUser user = personnelDao.findAuthenticatedUserByUsername("mifos");

        // verification
        assertNotNull(user);
        assertThat(user.getUsername(), is("mifos"));
        assertThat(user.getAuthorities().isEmpty(), is(false));
    }

    @Test
    public void shouldFindAppUserWithNoRolesByUsername() {

        OfficeBO office = IntegrationTestObjectMother.sampleBranchOffice();
        PersonnelBO userWithNoRoles = new PersonnelBuilder().withUsername("noRoles")
                                                            .asLoanOfficer()
                                                            .with(office)
                                                            .build();
        IntegrationTestObjectMother.createPersonnel(userWithNoRoles);

        // exercise test
        MifosUser user = personnelDao.findAuthenticatedUserByUsername("noRoles");

        // verification
        assertNotNull(user);
    }


    @Test
    public void shouldFindUsersByNameWhenSearching() {

        // setup
        MifosUser user = personnelDao.findAuthenticatedUserByUsername("mifos");

        // exercise test
        String searchString = "mifos";
        Integer page = 1;
        Integer pageSize = 10;

        UserSearchDto searchDto = new UserSearchDto(searchString, page, pageSize);

        SystemUserSearchResultsDto searchResults = personnelDao.search(searchDto, user);

        // verification
        assertNotNull(searchResults);
//        assertThat(searchResults.size(), is(1));
    }

}