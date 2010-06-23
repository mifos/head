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

package org.mifos.accounts.productdefinition.business;

import static org.mifos.framework.util.helpers.TestObjectFactory.TEST_LOCALE;
import junit.framework.Assert;

import org.hibernate.Session;
import org.mifos.application.master.business.InterestTypesEntity;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.persistence.TestDatabase;

public class AddInterestCalcRuleIntegrationTest extends MifosIntegrationTestCase {

    public AddInterestCalcRuleIntegrationTest() throws Exception {
        super();
    }

    public void testValidateLookupValueKeyTest() throws Exception {
        String validKey = "InterestTypes-DecliningBalance";
        String format = "InterestTypes-";
       Assert.assertTrue(AddInterestCalcRule.validateLookupValueKey(format, validKey));
        String invalidKey = "DecliningBalance";
        Assert.assertFalse(AddInterestCalcRule.validateLookupValueKey(format, invalidKey));
    }

    public void testConstructor() throws Exception {
        short newRuleId = 2555;
        short categoryId = 1;
        String description = "DecliningBalance";
        AddInterestCalcRule upgrade = null;
        try {
            // use deprecated construtor
            upgrade = new AddInterestCalcRule(DatabaseVersionPersistence.APPLICATION_VERSION + 1, newRuleId,
                    categoryId, "DecliningBalance", description, TEST_LOCALE, "DecliningBalance");
        } catch (Exception e) {
           Assert.assertEquals(e.getMessage(), AddInterestCalcRule.WRONG_CONSTRUCTOR);
        }
        String invalidKey = "DecliningBalance";

        try {
            // use invalid lookup key format
            upgrade = new AddInterestCalcRule(DatabaseVersionPersistence.APPLICATION_VERSION + 1, newRuleId,
                    categoryId, invalidKey, description);
        } catch (Exception e) {
           Assert.assertEquals(e.getMessage(), AddInterestCalcRule.wrongLookupValueKeyFormat);
        }
        String goodKey = "InterestTypes-NewDecliningBalance";
        // use valid construtor and valid key
        upgrade = new AddInterestCalcRule(DatabaseVersionPersistence.APPLICATION_VERSION + 1, newRuleId, categoryId,
                goodKey, description);
        try {
            Session session = StaticHibernateUtil.getSessionTL();
            upgrade.upgrade(session.connection());
            InterestTypesEntity entity = (InterestTypesEntity) session.get(InterestTypesEntity.class, newRuleId);
            Assert.assertEquals(goodKey, entity.getLookUpValue().getLookUpName());
        } finally {
            TestDatabase.resetMySQLDatabase();
        }
    }

}
