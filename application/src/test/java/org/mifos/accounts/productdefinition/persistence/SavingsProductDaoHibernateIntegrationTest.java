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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mifos.accounts.productdefinition.business.ProductTypeEntity;
import org.mifos.accounts.productdefinition.business.RecommendedAmntUnitEntity;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.business.SavingsProductBuilder;
import org.mifos.accounts.productdefinition.util.helpers.ProductType;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.StandardTestingService;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.Money;
import org.mifos.service.test.TestMode;
import org.mifos.test.framework.util.DatabaseCleaner;
import org.springframework.beans.factory.annotation.Autowired;

public class SavingsProductDaoHibernateIntegrationTest extends MifosIntegrationTestCase {

    // class under test
    @Autowired
    private SavingsProductDao savingsProductDao;

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

    @Before
    public void setUp() throws Exception {

        databaseCleaner.clean();
    }

    @After
    public void cleanDatabaseTablesAfterTest() {
        // NOTE: - only added to stop older integration tests failing due to brittleness
        databaseCleaner.clean();
    }

    @Test
    public void testShouldFindSavingsProductConfiguration() {

        // exercise test
       ProductTypeEntity productType = savingsProductDao.findSavingsProductConfiguration();

       assertThat(productType, is(notNullValue()));
       assertThat(productType.getType(), is(ProductType.SAVINGS));
    }

    @Test
    public void testShouldFindAllSavingsProducts() {

        // exercise test
        List<Object[]> queryResult = savingsProductDao.findAllSavingsProducts();
        assertThat(queryResult, is(notNullValue()));

    }

    @Test
    public void testShouldFindSavingsProductById() {

        // setup
        SavingsOfferingBO savingsProduct = new SavingsProductBuilder().appliesToCentersOnly()
                                                                                 .mandatory()
                                                                                 .withName("product1")
                                                                                 .withShortName("1234")
                                                                                 .withMandatoryAmount("25")
                                                                                 .buildForIntegrationTests();
        IntegrationTestObjectMother.saveSavingsProducts(savingsProduct);

        // exercise test
        SavingsOfferingBO result = savingsProductDao.findById(savingsProduct.getPrdOfferingId().intValue());
        Money recommendedAmount = TestUtils.createMoney("25");
        RecommendedAmntUnitEntity recommendedAmntUnit = null;
        Money maxAmntWithdrawl = TestUtils.createMoney("100");
        Double interestRate = Double.valueOf("12");
        Money minAmountRequiredForInterestToBeCalculated = TestUtils.createMoney("100");
        LocalDate updateDate = new LocalDate();
        result.updateSavingsDetails(recommendedAmount, recommendedAmntUnit, maxAmntWithdrawl, interestRate, minAmountRequiredForInterestToBeCalculated, updateDate);

        IntegrationTestObjectMother.saveSavingsProducts(result);

        // assertion
        assertNotNull(result);
    }

}
