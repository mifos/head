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

package org.mifos.application.accounts.business;

import static org.mifos.framework.util.helpers.TestObjectFactory.TEST_LOCALE;

import java.sql.Connection;

import junit.framework.Assert;

import org.hibernate.Session;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.persistence.Upgrade;

public class AddAccountActionIntegrationTest extends MifosIntegrationTestCase {

    public AddAccountActionIntegrationTest() throws SystemException, ApplicationException {
        super();
    }
    
    private Session session;
    
    private Connection connection;

    private static final short SEND_TO_ORPHANS = 43;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        session = StaticHibernateUtil.getSessionTL();
        connection = session.connection();
    }

    @Override
    protected void tearDown() throws Exception {
        StaticHibernateUtil.closeSession();
        connection = null;
        session = null;
        super.tearDown();
    }

    public void startFromStandardStore() throws Exception {
        Upgrade upgrade = new AddAccountAction(72, SEND_TO_ORPHANS, TEST_LOCALE, "Send money to orphans");
        upgradeAndCheck(upgrade);
    }

    private void upgradeAndCheck(Upgrade upgrade) throws Exception {
        upgrade.upgrade(connection);
        AccountActionEntity action = (AccountActionEntity) session.get(AccountActionEntity.class, SEND_TO_ORPHANS);
        action.setLocaleId(TEST_LOCALE);
       Assert.assertEquals(SEND_TO_ORPHANS, (short) action.getId());
       Assert.assertEquals(" ", action.getLookUpValue().getLookUpName());
    }

    public void testValidateLookupValueKey() throws Exception {
        String validKey = "AccountAction-LoanRepayment";
        String format = "AccountAction-";
       Assert.assertTrue(AddAccountAction.validateLookupValueKey(format, validKey));
        String invalidKey = "Action-LoanRepayment";
        Assert.assertFalse(AddAccountAction.validateLookupValueKey(format, invalidKey));
    }

    public void testConstructor() throws Exception {
        short newId = 31000;
        AddAccountAction upgrade = null;
        try {
            // use deprecated construtor
            upgrade = new AddAccountAction(DatabaseVersionPersistence.APPLICATION_VERSION + 1, newId, TEST_LOCALE,
                    "NewAccountAction");
        } catch (Exception e) {
           Assert.assertEquals(e.getMessage(), AddAccountAction.wrongConstructor);
        }
        String invalidKey = "NewAccountAction";

        try {
            // use invalid lookup key format
            upgrade = new AddAccountAction(DatabaseVersionPersistence.APPLICATION_VERSION + 1, newId, invalidKey);
        } catch (Exception e) {
           Assert.assertEquals(e.getMessage(), AddAccountAction.wrongLookupValueKeyFormat);
        }
        String goodKey = "AccountAction-NewAccountAction";
        // use valid construtor and valid key
        upgrade = new AddAccountAction(DatabaseVersionPersistence.APPLICATION_VERSION + 1, newId, goodKey);
        upgrade.upgrade(connection);
        
        AccountActionEntity action = (AccountActionEntity) session.get(AccountActionEntity.class, newId);
       Assert.assertEquals(goodKey, action.getLookUpValue().getLookUpName());
    }

}
