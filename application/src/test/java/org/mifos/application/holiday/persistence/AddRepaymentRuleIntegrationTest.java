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

package org.mifos.application.holiday.persistence;

import static org.mifos.framework.util.helpers.TestObjectFactory.TEST_LOCALE;

import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.persistence.TestDatabase;

public class AddRepaymentRuleIntegrationTest extends MifosIntegrationTestCase {

    public AddRepaymentRuleIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    public void testValidateLookupValueKey() throws Exception {
        String validKey = "RepaymentRule-NewSameDay";
        String format = "RepaymentRule-";
        assertTrue(AddRepaymentRule.validateLookupValueKey(format, validKey));
        String invalidKey = "NewSameDay";
        assertFalse(AddRepaymentRule.validateLookupValueKey(format, invalidKey));
    }

    public void testConstructor() throws Exception {
        TestDatabase database = TestDatabase.makeStandard();
        String start = database.dumpForComparison();
        AddRepaymentRule upgrade = null;
        try {
            // use deprecated construtor
            upgrade = new AddRepaymentRule(DatabaseVersionPersistence.APPLICATION_VERSION + 1,
                    RepaymentRuleTypes.SAME_DAY, TEST_LOCALE, "SameDayValue");
        } catch (Exception e) {
            assertEquals(e.getMessage(), AddRepaymentRule.wrongConstructor);
        }
        String invalidKey = "SameDay";

        try {
            // use invalid lookup key format
            upgrade = new AddRepaymentRule(DatabaseVersionPersistence.APPLICATION_VERSION + 1,
                    RepaymentRuleTypes.SAME_DAY, invalidKey);
        } catch (Exception e) {
            assertEquals(e.getMessage(), AddRepaymentRule.wrongLookupValueKeyFormat);
        }

        String goodKey = "RepaymentRule-NextWorkingDay";
        // use valid construtor and valid key
        upgrade = new AddRepaymentRule(DatabaseVersionPersistence.APPLICATION_VERSION + 1,
                RepaymentRuleTypes.NEXT_WORKING_DAY, goodKey);
        String afterUpAndDownGrade = database.dumpForComparison();
        assertEquals(start, afterUpAndDownGrade);

    }

}
