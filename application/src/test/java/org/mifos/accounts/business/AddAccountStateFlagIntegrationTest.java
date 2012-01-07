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

import java.sql.Connection;
import java.sql.SQLException;

import junit.framework.Assert;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

public class AddAccountStateFlagIntegrationTest extends MifosIntegrationTestCase {

    private Session session;
    private Connection connection;

    @Before
    public void setUp() throws SQLException {
        session = StaticHibernateUtil.getSessionTL();
        connection = dataSource.getConnection();
    }

    @After
    public void tearDown() throws Exception {
        StaticHibernateUtil.flushSession();
        connection.close();
    }

    @Test
    public void testValidateLookupValueKeyTest() throws Exception {
        String validKey = "AccountFlags-Withdraw";
        String format = "AccountFlags-";
       Assert.assertTrue(AddAccountStateFlag.validateLookupValueKey(format, validKey));
        String invalidKey = "Withdraw";
        Assert.assertFalse(AddAccountStateFlag.validateLookupValueKey(format, invalidKey));
    }

    @Test
    public void testConstructor() throws Exception {
        short newId = 31500;
        AddAccountStateFlag upgrade = null;
        String invalidKey = "NewAccountStateFlag";

        try {
            // use invalid lookup key format
            upgrade = new AddAccountStateFlag(newId, invalidKey,
                    invalidKey);
        } catch (Exception e) {
           Assert.assertEquals(e.getMessage(), AddAccountStateFlag.wrongLookupValueKeyFormat);
        }
        String goodKey = "AccountFlags-NewAccountStateFlag";
        // use valid constructor and valid key
        upgrade = new AddAccountStateFlag( newId, goodKey, goodKey);

        upgrade.upgrade(connection);
        AccountStateFlagEntity flag = (AccountStateFlagEntity) session.get(AccountStateFlagEntity.class, newId);
        Assert.assertEquals(goodKey, flag.getLookUpValue().getLookUpName());
    }

}
