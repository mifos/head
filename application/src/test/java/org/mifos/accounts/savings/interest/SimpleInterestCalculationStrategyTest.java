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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.math.RoundingMode;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.config.AccountingRulesConstants;
import org.mifos.config.ConfigurationManager;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.Money;

public class SimpleInterestCalculationStrategyTest {

    private SimpleInterestCalculationStrategy interestCalculationStrategy;

    private Double interestRate = Double.valueOf("10");
    private int numberOfDaysInYear = 365;

    private static MifosCurrency oldCurrency;
    private static String oldRoundingMode;

    @BeforeClass
    public static void setCurrency() {
        oldCurrency = Money.getDefaultCurrency();
        Money.setDefaultCurrency(TestUtils.RUPEE);
        oldRoundingMode =  (String) ConfigurationManager.getInstance().getProperty(AccountingRulesConstants.CURRENCY_ROUNDING_MODE);
        ConfigurationManager.getInstance().setProperty(AccountingRulesConstants.CURRENCY_ROUNDING_MODE, RoundingMode.HALF_UP.toString());
    }

    @AfterClass
    public static void resetCurrency() {
        ConfigurationManager.getInstance().setProperty(AccountingRulesConstants.CURRENCY_ROUNDING_MODE, oldRoundingMode);
        Money.setDefaultCurrency(oldCurrency);
    }

    @Before
    public void setup() {
        interestCalculationStrategy = new SimpleInterestCalculationStrategy(interestRate, numberOfDaysInYear);
    }

    @Test
    public void shouldCalculateInterestAmountBasedOnPrincipalAndThirthyDayDuration() {

        Money principal = TestUtils.createMoney("1000");
        int duration = 30;

        // exercise test
        Money interest = interestCalculationStrategy.calculateInterest(principal, duration);

        assertThat(interest, is(TestUtils.createMoney("8.2")));
    }

    @Test
    public void shouldCalculateInterestAmountBasedOnPrincipalAndThirthyOneDayDuration() {

        Money principal = TestUtils.createMoney("1000");
        int duration = 31;

        // exercise test
        Money interest = interestCalculationStrategy.calculateInterest(principal, duration);

        assertThat(interest, is(TestUtils.createMoney("8.5")));
    }

}