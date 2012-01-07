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

package org.mifos.accounts.productdefinition.business;

import java.sql.Connection;

import junit.framework.Assert;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.mifos.application.master.business.InterestTypesEntity;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

public class AddInterestCalcRuleIntegrationTest extends MifosIntegrationTestCase {

    Connection connection;
    Session session;

    @Before
    public void setUp() throws Exception {
        session = StaticHibernateUtil.getSessionTL();
        connection = dataSource.getConnection();
    }

    public void tearDown() throws Exception {
        connection.close();
    }

    public void testValidateLookupValueKeyTest() throws Exception {
        String validKey = "InterestTypes-DecliningBalance";
        String format = "InterestTypes-";
       Assert.assertTrue(AddInterestCalcRule.validateLookupValueKey(format, validKey));
        String invalidKey = "DecliningBalance";
        Assert.assertFalse(AddInterestCalcRule.validateLookupValueKey(format, invalidKey));
    }

    @Test
    public void testConstructor() throws Exception {
        short newRuleId = 2555;
        short categoryId = 1;
        String description = "DecliningBalance";
        AddInterestCalcRule upgrade = null;
        String invalidKey = "DecliningBalance";

        try {
            // use invalid lookup key format
            upgrade = new AddInterestCalcRule(newRuleId,
                    categoryId, invalidKey, description);
        } catch (Exception e) {
           Assert.assertEquals(e.getMessage(), AddInterestCalcRule.wrongLookupValueKeyFormat);
        }
        String goodKey = "InterestTypes-NewDecliningBalance";
        // use valid constructor and valid key
        upgrade = new AddInterestCalcRule(newRuleId, categoryId,
                goodKey, description);
        try {
            upgrade.upgrade(connection);
            InterestTypesEntity entity = (InterestTypesEntity) session.get(InterestTypesEntity.class, newRuleId);
            Assert.assertEquals(goodKey, entity.getLookUpValue().getLookUpName());
        } finally {

        }
    }

}
