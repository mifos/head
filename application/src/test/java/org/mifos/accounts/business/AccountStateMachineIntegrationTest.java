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

package org.mifos.accounts.business;

import java.util.List;

import junit.framework.Assert;

import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountStateFlag;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ServiceUnavailableException;
import org.mifos.framework.exceptions.StatesInitializationException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class AccountStateMachineIntegrationTest extends MifosIntegrationTestCase {

    public AccountStateMachineIntegrationTest() throws Exception {
        super();
    }

    private AccountBusinessService service;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        service = new AccountBusinessService();
    }

    @Override
    protected void tearDown() throws Exception {
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testGetStatusList() throws Exception {
        AccountStateMachines.getInstance().initialize((short) 1, (short) 1, AccountTypes.LOAN_ACCOUNT, null);
        List<AccountStateEntity> stateList = service.getStatusList(new AccountStateEntity(
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING), AccountTypes.LOAN_ACCOUNT, Short.valueOf("1"));
       Assert.assertEquals(2, stateList.size());
    }

    public void testGetStatusName() throws Exception {
        AccountStateMachines.getInstance().initialize((short) 1, (short) 1, AccountTypes.LOAN_ACCOUNT, null);
        Assert.assertNotNull(service.getStatusName((short) 1, AccountState.LOAN_CLOSED_RESCHEDULED, AccountTypes.LOAN_ACCOUNT));
    }

    public void testGetFlagName() throws Exception {
        AccountStateMachines.getInstance().initialize((short) 1, (short) 1, AccountTypes.LOAN_ACCOUNT, null);
        Assert.assertNotNull(service.getFlagName((short) 1, AccountStateFlag.LOAN_WITHDRAW, AccountTypes.LOAN_ACCOUNT));
    }

    public void testStatesInitializationException() throws Exception {
        TestObjectFactory.simulateInvalidConnection();
        try {
            AccountStateMachines.getInstance().initialize((short) 1, (short) 1, AccountTypes.LOAN_ACCOUNT, null);
            Assert.fail();
        } catch (StatesInitializationException sie) {
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    public void testServiceUnavailableException() throws Exception {
        try {
            service = (AccountBusinessService) ServiceFactory.getInstance().getBusinessService(null);
            Assert.fail();
        } catch (ServiceUnavailableException sue) {
        }
    }

    public void testFlagForLoanCancelState() throws Exception {
        AccountStateMachines.getInstance().initialize((short) 1, (short) 1, AccountTypes.LOAN_ACCOUNT, null);
        StaticHibernateUtil.closeSession();
        List<AccountStateEntity> stateList = service.getStatusList(new AccountStateEntity(
                AccountState.LOAN_PARTIAL_APPLICATION), AccountTypes.LOAN_ACCOUNT, Short.valueOf("1"));
        for (AccountStateEntity accountState : stateList) {
            if (accountState.getId().equals(AccountState.LOAN_CANCELLED.getValue())) {
               Assert.assertEquals(3, accountState.getFlagSet().size());
                for (AccountStateFlagEntity accountStateFlag : accountState.getFlagSet()) {
                    if (accountStateFlag.getId().equals(AccountStateFlag.LOAN_REVERSAL.getValue())) {
                        Assert.fail();
                    }
                }
            }
        }
    }
}
