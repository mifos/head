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

import org.hibernate.Session;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.persistence.Upgrade;

public class AddAccountStateFlagIntegrationTest extends MifosIntegrationTestCase {

    public AddAccountStateFlagIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    private static final short FLAG_FEET_TOO_BIG = 12;

    // private TestDatabase database;

    /*
     * @Before public void setUp() throws SystemException, ApplicationException
     * {
     * 
     * database = TestDatabase.makeStandard(); database.installInThreadLocal();
     * 
     * }
     */

    public void testStartFromStandardStore() throws Exception {
        TestDatabase database = TestDatabase.makeStandard();
        String start = database.dumpForComparison();

        Upgrade upgrade = new AddAccountStateFlag(72, FLAG_FEET_TOO_BIG, "Feet too big", TEST_LOCALE,
                "Rejected because feet are too big");

        upgradeAndCheck(database, upgrade);
    }

    private void upgradeAndCheck(TestDatabase database, Upgrade upgrade) throws Exception {
        upgrade.upgrade(database.openConnection());
        /*
         * Below is a workaround to make this test case work The upgrade is
         * being done in a Mayfly database. After the upgrade, we set up the
         * Mayfly session to be used by the next call to
         * HibernateUtils.getSessionTL() Then call init() on MifosConfiguration
         * to refresh the values from the Mayfly database, so that the call to
         * flag.getName() below can find the new value.
         */
        database.installInThreadLocal();
        MifosConfiguration.getInstance().init();

        Session session = database.openSession();
        AccountStateFlagEntity flag = (AccountStateFlagEntity) session.get(AccountStateFlagEntity.class,
                FLAG_FEET_TOO_BIG);
        flag.setLocaleId(TEST_LOCALE);

        assertEquals((Object) FLAG_FEET_TOO_BIG, (Object) flag.getId());
        assertEquals(10, (short) flag.getStatusId());
        assertEquals(false, flag.isFlagRetained());
        assertEquals("Feet too big", flag.getFlagDescription());

        assertEquals("Rejected because feet are too big", flag.getName());
    }

    public void testValidateLookupValueKeyTest() throws Exception {
        String validKey = "AccountFlags-Withdraw";
        String format = "AccountFlags-";
        assertTrue(AddAccountStateFlag.validateLookupValueKey(format, validKey));
        String invalidKey = "Withdraw";
        assertFalse(AddAccountStateFlag.validateLookupValueKey(format, invalidKey));
    }

    public void testConstructor() throws Exception {
        TestDatabase database = TestDatabase.makeStandard();
        short newId = 31500;
        AddAccountStateFlag upgrade = null;
        try {
            // use deprecated construtor
            upgrade = new AddAccountStateFlag(DatabaseVersionPersistence.APPLICATION_VERSION + 1, newId,
                    "NewAccountStateFlag", TEST_LOCALE, "NewAccountStateFlag");
        } catch (Exception e) {
            assertEquals(e.getMessage(), AddAccountStateFlag.wrongConstructor);
        }
        String invalidKey = "NewAccountStateFlag";

        try {
            // use invalid lookup key format
            upgrade = new AddAccountStateFlag(DatabaseVersionPersistence.APPLICATION_VERSION + 1, newId, invalidKey,
                    invalidKey);
        } catch (Exception e) {
            assertEquals(e.getMessage(), AddAccountStateFlag.wrongLookupValueKeyFormat);
        }
        String goodKey = "AccountFlags-NewAccountStateFlag";
        // use valid construtor and valid key
        upgrade = new AddAccountStateFlag(DatabaseVersionPersistence.APPLICATION_VERSION + 1, newId, goodKey, goodKey);
        upgrade.upgrade(database.openConnection());
        Session session = database.openSession();
        AccountStateFlagEntity flag = (AccountStateFlagEntity) session.get(AccountStateFlagEntity.class, newId);
        assertEquals(goodKey, flag.getLookUpValue().getLookUpName());
        MifosConfiguration.getInstance().init();

    }

}
