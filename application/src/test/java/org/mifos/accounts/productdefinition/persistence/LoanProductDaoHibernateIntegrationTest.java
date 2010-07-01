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
package org.mifos.accounts.productdefinition.persistence;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.LoanProductBuilder;
import org.mifos.accounts.productdefinition.business.ProductTypeEntity;
import org.mifos.accounts.productdefinition.persistence.LoanProductDao;
import org.mifos.accounts.productdefinition.util.helpers.ProductType;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.business.ValueListElement;
import org.mifos.customers.business.CustomerLevelEntity;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.framework.TestUtils;
import org.mifos.framework.spring.SpringUtil;
import org.mifos.framework.util.StandardTestingService;
import org.mifos.framework.util.helpers.DatabaseSetup;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.Money;
import org.mifos.service.test.TestMode;
import org.mifos.test.framework.util.DatabaseCleaner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/integration-test-context.xml",
                                    "/org/mifos/config/resources/hibernate-daos.xml"})
public class LoanProductDaoHibernateIntegrationTest {

    // class under test
    @Autowired
    private LoanProductDao loanProductDao;

    // test data
    private LoanOfferingBO activeLoanProduct;
    private LoanOfferingBO inActiveLoanProduct;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    private static MifosCurrency oldDefaultCurrency;

    @BeforeClass
    public static void initialiseHibernateUtil() {

        oldDefaultCurrency = Money.getDefaultCurrency();
        Money.setDefaultCurrency(TestUtils.RUPEE);
        new StandardTestingService().setTestMode(TestMode.INTEGRATION);
        DatabaseSetup.initializeHibernate();
        SpringUtil.initializeSpring();
    }

    @AfterClass
    public static void resetCurrency() {
        Money.setDefaultCurrency(oldDefaultCurrency);
    }

    @Before
    public void setUp() throws Exception {

        databaseCleaner.clean();

        activeLoanProduct = new LoanProductBuilder().active().withName("Active Loan Product").withShortName("ALP")
                .appliesToGroupsOnly().withGlobalProductNumber("AAA-111").buildForIntegrationTests();
        inActiveLoanProduct = new LoanProductBuilder().inActive().withName("inActive Loan Product")
                .withShortName("ILP").appliesToGroupsOnly().withGlobalProductNumber("AAA-112")
                .buildForIntegrationTests();
        IntegrationTestObjectMother.saveLoanProducts(activeLoanProduct, inActiveLoanProduct);
    }

    @After
    public void cleanDatabaseTablesAfterTest() {
        // NOTE: - only added to stop older integration tests failing due to brittleness
        databaseCleaner.clean();
    }

    @Test
    public void testShouldFindOnlyActiveLoanProductsApplicableToCustomerLevel() {

        CustomerLevelEntity groupLevel = new CustomerLevelEntity(CustomerLevel.GROUP);

        // exercise test
        List<LoanOfferingBO> activeLoanProductsApplicableToGroup = loanProductDao
                .findActiveLoanProductsApplicableToCustomerLevel(groupLevel);

        assertThat("Only active products applicable to groups should be returned", activeLoanProductsApplicableToGroup
                .size(), is(1));
        assertThat(activeLoanProductsApplicableToGroup, hasItem(activeLoanProduct));
    }

    @Test
    public void testShouldFindAllLoanPurposeSeedData() {

        // exercise test
        List<ValueListElement> allLoanPruposes = loanProductDao.findAllLoanPurposes();

        assertThat(
                "number of loan purposes has changed: this could be due to change in seed data in lookup_value table",
                allLoanPruposes.size(), is(131));
    }

    @Test
    public void testShouldFindLoanProductConfiguration() {

        // exercise test
       ProductTypeEntity productType = loanProductDao.findLoanProductConfiguration();

       assertThat(productType, is(notNullValue()));
       assertThat(productType.getType(), is(ProductType.LOAN));
    }
}
