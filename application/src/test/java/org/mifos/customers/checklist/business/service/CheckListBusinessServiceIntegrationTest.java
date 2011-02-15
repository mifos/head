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

package org.mifos.customers.checklist.business.service;

import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;
import org.mifos.accounts.productdefinition.util.helpers.ProductType;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.customers.api.CustomerLevel;
import org.mifos.customers.checklist.business.AccountCheckListBO;
import org.mifos.customers.checklist.business.CustomerCheckListBO;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class CheckListBusinessServiceIntegrationTest extends MifosIntegrationTestCase {

    @After
    public void tearDown() throws Exception {
        StaticHibernateUtil.flushSession();
    }

    @Test
    public void testRetreiveAllAccountCheckLists() throws Exception {
        TestObjectFactory.createAccountChecklist(ProductType.LOAN.getValue(),
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, (short) 1);
        TestObjectFactory.createCustomerChecklist(CustomerLevel.CENTER.getValue(),
                CustomerStatus.CENTER_ACTIVE.getValue(), (short) 1);
        List<AccountCheckListBO> checkLists = new CheckListBusinessService().retreiveAllAccountCheckLists();
        Assert.assertNotNull(checkLists);
        Assert.assertEquals(1, checkLists.size());
    }

    @Test
    public void testRetreiveAllCustomerCheckLists() throws Exception {
        TestObjectFactory.createAccountChecklist(ProductType.LOAN.getValue(),
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, (short) 1);
        TestObjectFactory.createCustomerChecklist(CustomerLevel.CENTER.getValue(),
                CustomerStatus.CENTER_ACTIVE.getValue(), (short) 1);
        List<CustomerCheckListBO> checkLists = new CheckListBusinessService().retreiveAllCustomerCheckLists();
        Assert.assertNotNull(checkLists);
        Assert.assertEquals(1, checkLists.size());
    }
}
