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

package org.mifos.application.servicefacade;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.productdefinition.business.service.ProductService;
import org.mifos.accounts.productdefinition.persistence.LoanProductDao;
import org.mifos.accounts.productdefinition.persistence.SavingsProductDao;
import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mifos.config.persistence.ApplicationConfigurationDao;
import org.mifos.customers.office.business.service.OfficeHierarchyService;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AdminServiceFacadeWebTierTest {

    // system under test (SUT)
    private AdminServiceFacade adminServiceFacade;

    @Mock
    private ProductService productService;

    @Mock
    private OfficeHierarchyService officeHierarchyService;

    @Mock
    private LoanProductDao loanProductDao;

    @Mock
    private SavingsProductDao savingsProductDao;

    @Mock
    private OfficeDao officeDao;

    @Mock
    private ApplicationConfigurationDao applicationConfigurationDao;

    @Before
    public void setupSUTAndInjectMocksAsDependencies() {

        adminServiceFacade = new AdminServiceFacadeWebTier(productService, officeHierarchyService, loanProductDao, savingsProductDao,
                                                           officeDao, applicationConfigurationDao);
    }

    @Test
    public void shouldDo() {

    }
}
