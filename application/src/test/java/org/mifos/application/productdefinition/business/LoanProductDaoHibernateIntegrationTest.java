/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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
package org.mifos.application.productdefinition.business;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;

import java.util.List;

import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.accounts.savings.persistence.GenericDaoHibernate;
import org.mifos.application.customer.business.CustomerLevelEntity;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.master.business.ValueListElement;
import org.mifos.application.productdefinition.persistence.LoanProductDao;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;

/**
 *
 */
public class LoanProductDaoHibernateIntegrationTest extends MifosIntegrationTestCase {

    public LoanProductDaoHibernateIntegrationTest() throws Exception {
        super();
    }

    // class under test
    private LoanProductDao loanProductDao;

    // collaborators
    private final GenericDao genericDao = new GenericDaoHibernate();

    // test data
    private LoanOfferingBO activeLoanProduct;
    private LoanOfferingBO inActiveLoanProduct;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        TestDatabase.resetMySQLDatabase();

        activeLoanProduct = new LoanProductBuilder().active().appliesToGroupsOnly().withGlobalProductNumber("AAA-111")
                .buildForIntegrationTests();
        inActiveLoanProduct = new LoanProductBuilder().inActive().appliesToGroupsOnly().withGlobalProductNumber(
                "AAA-112").buildForIntegrationTests();
        IntegrationTestObjectMother.saveLoanProducts(activeLoanProduct, inActiveLoanProduct);

        loanProductDao = new LoanProductDaoHibernate(genericDao);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testShouldFindOnlyActiveLoanProductsApplicableToCustomerLevel() {

        CustomerLevelEntity groupLevel = new CustomerLevelEntity(CustomerLevel.GROUP);

        // exercise test
        List<LoanOfferingBO> activeLoanProductsApplicableToGroup = loanProductDao
                .findActiveLoanProductsApplicableToCustomerLevel(groupLevel);

        assertThat("Only active products applicable to groups should be returned", activeLoanProductsApplicableToGroup
                .size(), is(1));
        assertThat(activeLoanProductsApplicableToGroup, hasItem(activeLoanProduct));
    }

    public void testShouldFindAllLoanPurposeSeedData() {

        // exercise test
        List<ValueListElement> allLoanPruposes = loanProductDao.findAllLoanPurposes();

        assertThat(
                "number of loan purposes has changed: this could be due to change in seed data in lookup_value table",
                allLoanPruposes.size(), is(131));
    }
}
