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

package org.mifos.accounts.savings.interest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.Money;

public class MinimumBalanceInterestCalculationRuleTest {

    private InterestCalucationRule minimumBalanceRule;

    private Money minimumBalanceRequired = TestUtils.createMoney("10");

    @Before
    public void setup() {
        minimumBalanceRule = new MinimumBalanceInterestCalculationRule(minimumBalanceRequired);
    }

    @Test
    public void calculationShouldBeAllowedWhenPrincipalIsEqualToMinBalanceRequired() {

        Money principal = TestUtils.createMoney("10");

        // exercise test
        boolean result = minimumBalanceRule.isCalculationAllowed(principal);

        assertTrue("calculation should be allowed as principal is greater than or equal to min balance required.", result);
    }

    @Test
    public void calculationShouldBeAllowedWhenPrincipalIsGreaterThanMinBalanceRequired() {

        Money principal = TestUtils.createMoney("50");

        // exercise test
        boolean result = minimumBalanceRule.isCalculationAllowed(principal);

        assertTrue("calculation should be allowed as principal is greater than or equal to min balance required.", result);
    }

    @Test
    public void calculationShouldNotBeAllowedWhenPrincipalIsLessThanMinBalanceRequired() {

        Money principal = TestUtils.createMoney("9.99");

        // exercise test
        boolean result = minimumBalanceRule.isCalculationAllowed(principal);

        assertFalse("calculation should NOT be allowed as principal is less than min balance required.", result);
    }
}