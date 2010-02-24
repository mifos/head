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

import static org.mifos.framework.util.helpers.TestObjectFactory.TEST_LOCALE;
import junit.framework.Assert;

import org.hibernate.Session;
import org.mifos.config.business.MifosConfiguration;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.persistence.Upgrade;

public class AddAccountStateFlagIntegrationTest extends MifosIntegrationTestCase {

    public AddAccountStateFlagIntegrationTest() throws Exception {
        super();
    }

    private static final short FLAG_FEET_TOO_BIG = 12;

    private Session session;
    
    @Override
    public void setUp() {
        session = StaticHibernateUtil.getSessionTL();
    }
    
    @Override
    public void tearDown() throws Exception {
        TestDatabase.resetMySQLDatabase();
    }

    public void testStartFromStandardStore() throws Exception {


        Upgrade upgrade = new AddAccountStateFlag(72, FLAG_FEET_TOO_BIG, "Feet too big", TEST_LOCALE,
                "Rejected because feet are too big");

        upgradeAndCheck(upgrade);
    }

    private void upgradeAndCheck(Upgrade upgrade) throws Exception {
        upgrade.upgrade(session.connection());
        MifosConfiguration.getInstance().init();
        session = StaticHibernateUtil.getSessionTL();
        AccountStateFlagEntity flag = (AccountStateFlagEntity) session.get(AccountStateFlagEntity.class,
                FLAG_FEET_TOO_BIG);
        flag.setLocaleId(TEST_LOCALE);

       Assert.assertEquals((Object) FLAG_FEET_TOO_BIG, (Object) flag.getId());
       Assert.assertEquals(10, (short) flag.getStatusId());
       Assert.assertEquals(false, flag.isFlagRetained());
       Assert.assertEquals("Feet too big", flag.getFlagDescription());

       Assert.assertEquals("Rejected because feet are too big", flag.getName());
    }

    public void testValidateLookupValueKeyTest() throws Exception {
        String validKey = "AccountFlags-Withdraw";
        String format = "AccountFlags-";
       Assert.assertTrue(AddAccountStateFlag.validateLookupValueKey(format, validKey));
        String invalidKey = "Withdraw";
        Assert.assertFalse(AddAccountStateFlag.validateLookupValueKey(format, invalidKey));
    }

    public void testConstructor() throws Exception {
        short newId = 31500;
        AddAccountStateFlag upgrade = null;
        try {
            // use deprecated construtor
            upgrade = new AddAccountStateFlag(DatabaseVersionPersistence.APPLICATION_VERSION + 1, newId,
                    "NewAccountStateFlag", TEST_LOCALE, "NewAccountStateFlag");
        } catch (Exception e) {
           Assert.assertEquals(e.getMessage(), AddAccountStateFlag.wrongConstructor);
        }
        String invalidKey = "NewAccountStateFlag";

        try {
            // use invalid lookup key format
            upgrade = new AddAccountStateFlag(DatabaseVersionPersistence.APPLICATION_VERSION + 1, newId, invalidKey,
                    invalidKey);
        } catch (Exception e) {
           Assert.assertEquals(e.getMessage(), AddAccountStateFlag.wrongLookupValueKeyFormat);
        }
        String goodKey = "AccountFlags-NewAccountStateFlag";
        // use valid construtor and valid key
        upgrade = new AddAccountStateFlag(DatabaseVersionPersistence.APPLICATION_VERSION + 1, newId, goodKey, goodKey);
        upgrade.upgrade(session.connection());
        AccountStateFlagEntity flag = (AccountStateFlagEntity) session.get(AccountStateFlagEntity.class, newId);
        Assert.assertEquals(goodKey, flag.getLookUpValue().getLookUpName());
        MifosConfiguration.getInstance().init();

    }

}
