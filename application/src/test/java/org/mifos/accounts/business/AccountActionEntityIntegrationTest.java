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

package org.mifos.accounts.business;

import java.util.Set;

import junit.framework.Assert;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.LookUpEntity;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

public class AccountActionEntityIntegrationTest extends MifosIntegrationTestCase {

    private Session session;
    private AccountActionEntity accountActionEntity;

    @Before
    public void setUp() throws Exception {
        //super.setUp();
        session = StaticHibernateUtil.getSessionTL();
    }

    @After
    public void tearDown() throws Exception {
        StaticHibernateUtil.flushSession();
        session = null;
        //super.tearDown();
    }

    @Test
    public void testGetAccountAction() {
        Short id = 1;
        accountActionEntity = getAccountActionEntityObject(id);
       Assert.assertEquals("Loan Repayment", accountActionEntity.getName());
    }

    @Test
    public void testBasics() throws Exception {
        AccountActionEntity action = (AccountActionEntity) session.get(AccountActionEntity.class,
                AccountActionTypes.PAYMENT.getValue());

        LookUpValueEntity lookUpValue = action.getLookUpValue();
       Assert.assertEquals("AccountAction-Payment", lookUpValue.getLookUpName());
       Assert.assertEquals(new Integer(191), lookUpValue.getLookUpId());

        LookUpEntity lookUpEntity = lookUpValue.getLookUpEntity();
       Assert.assertEquals(LookUpEntity.ACCOUNT_ACTION, lookUpEntity.getEntityId().shortValue());
       Assert.assertEquals("AccountAction", lookUpEntity.getEntityType());

        Set<LookUpValueLocaleEntity> valueLocales = lookUpValue.getLookUpValueLocales();
       Assert.assertEquals(1, valueLocales.size());
        LookUpValueLocaleEntity valueLocale = valueLocales.iterator().next();
       Assert.assertEquals(1, (int) valueLocale.getLocaleId());
       Assert.assertEquals("Payment", ApplicationContextProvider.getBean(MessageLookup.class).lookup(lookUpValue));

       Assert.assertEquals("Payment", action.getName());
    }

    @Test
    public void testEnum() throws Exception {
        AccountActionTypes myEnum = AccountActionTypes.FEE_REPAYMENT;
        AccountActionEntity entity = new AccountActionEntity(myEnum);
       Assert.assertEquals(myEnum.getValue(), entity.getId());

        AccountActionTypes out = entity.asEnum();
       Assert.assertEquals(myEnum, out);
    }

    @Test
    public void testFromBadInt() throws Exception {
        try {
            AccountActionTypes.fromInt(9999);
            Assert.fail();
        } catch (RuntimeException e) {
           Assert.assertEquals("no account action 9999", e.getMessage());
        }
    }

    private AccountActionEntity getAccountActionEntityObject(Short id) {
        return (AccountActionEntity) session.get(AccountActionEntity.class, id);
    }

}
