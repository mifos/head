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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.RoundingMode;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.config.AccountingRulesConstants;
import org.mifos.config.business.MifosConfigurationManager;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.Money;

public class AverageBalanceCalculationStrategyTest {

    private PrincipalCalculationStrategy calculationStrategy;

    private InterestCalculationPeriodDetail interestCalculationPeriodDetail;

    private LocalDate august31st = new LocalDate(new DateTime().withDate(2010, 8, 31));
    private LocalDate september1st = new LocalDate(new DateTime().withDate(2010, 9, 1));
    private LocalDate september6th = new LocalDate(new DateTime().withDate(2010, 9, 6));
    private LocalDate september13th = new LocalDate(new DateTime().withDate(2010, 9, 13));
    private LocalDate september20th = new LocalDate(new DateTime().withDate(2010, 9, 20));
    private LocalDate september30th = new LocalDate(new DateTime().withDate(2010, 9, 30));

    private static MifosCurrency oldCurrency;
    private static String oldRoundingMode;

    @BeforeClass
    public static void setCurrency() {
        oldCurrency = Money.getDefaultCurrency();
        Money.setDefaultCurrency(TestUtils.RUPEE);
        oldRoundingMode =  (String) MifosConfigurationManager.getInstance().getProperty(AccountingRulesConstants.CURRENCY_ROUNDING_MODE);
        MifosConfigurationManager.getInstance().setProperty(AccountingRulesConstants.CURRENCY_ROUNDING_MODE, RoundingMode.HALF_UP.toString());
    }

    @AfterClass
    public static void resetCurrency() {
        MifosConfigurationManager.getInstance().setProperty(AccountingRulesConstants.CURRENCY_ROUNDING_MODE, oldRoundingMode);
        Money.setDefaultCurrency(oldCurrency);
    }

    private InterestCalculationPeriodBuilder zeroBalanceAug31stToSeptember30thCalculationPeriod() {
        return new InterestCalculationPeriodBuilder().from(august31st).to(september30th).withStartingBalance("0");
    }

    @Before
    public void setup() {
        calculationStrategy = new AverageBalanceCalculationStrategy();
    }

    @Test
    public void shouldRecieveZeroBalanceWithNoDailyRecords() {

        interestCalculationPeriodDetail = zeroBalanceAug31stToSeptember30thCalculationPeriod().build();

        // exercise test
        Money averageBalancePrincipal = calculationStrategy.calculatePrincipal(interestCalculationPeriodDetail);

        assertThat(averageBalancePrincipal, is(TestUtils.createMoney("0")));
    }

    @Test
    public void shouldCalculateAverageBalanceGivenOnlyOneDailyBalanceExistsWithinRange() {

        EndOfDayDetail endOfDayDetail = new EndOfDayBuilder().on(september6th).withDespoitsOf("1000").build();

        interestCalculationPeriodDetail = zeroBalanceAug31stToSeptember30thCalculationPeriod().from(september6th)
                                                                                              .containing(endOfDayDetail)
                                                                                              .isFirstActivity()
                                                                                              .build();

        // exercise test
        Money averageBalancePrincipal = calculationStrategy.calculatePrincipal(interestCalculationPeriodDetail);

        assertThat(averageBalancePrincipal, is(TestUtils.createMoney("1000")));
    }

    @Test
    public void shouldCalculateAverageBalanceGivenTwoDailyBalancesExistWithinRange() {

        EndOfDayDetail september6thDetails = new EndOfDayBuilder().on(september6th).withDespoitsOf("1000").build();
        EndOfDayDetail september13thDetails = new EndOfDayBuilder().on(september13th).withDespoitsOf("1000").build();

        interestCalculationPeriodDetail = zeroBalanceAug31stToSeptember30thCalculationPeriod()
                                                                                              .containing(september6thDetails, september13thDetails)
                                                                                              .from(september6th)
                                                                                              .isFirstActivity()
                                                                                              .build();

        // exercise test
        Money averageBalancePrincipal = calculationStrategy.calculatePrincipal(interestCalculationPeriodDetail);

        // NOT (1000 x 7 + 2000 x 17)/24 = 1708.333333..
        // But (1000 x 6 + 2000 x 17)/23 = 1708.333333.. due to first activity on 6th of september
        // verification
        assertThat(averageBalancePrincipal, is(TestUtils.createMoney("1739.1")));
    }

    @Test
    public void shouldCalculateAverageBalanceGivenOneDepositExistBeforeRange() {

        EndOfDayDetail september1stDetails = new EndOfDayBuilder().on(september1st).build();
        EndOfDayDetail september6thDetails = new EndOfDayBuilder().on(september6th).withDespoitsOf("1000").build();
        EndOfDayDetail september13thDetails = new EndOfDayBuilder().on(september13th).withDespoitsOf("1000").build();
        EndOfDayDetail september20thDetails = new EndOfDayBuilder().on(september20th).withDespoitsOf("500").build();

        interestCalculationPeriodDetail = zeroBalanceAug31stToSeptember30thCalculationPeriod()
                                                                                              .withStartingBalance("1000")
                                                                                              .isFirstActivity()
                                                .containing(september1stDetails, september6thDetails, september13thDetails, september20thDetails)
                                                .build();

        // exercise test
        Money averageBalancePrincipal = calculationStrategy.calculatePrincipal(interestCalculationPeriodDetail);

        // verification
        assertThat(averageBalancePrincipal, is(TestUtils.createMoney(("2586.2"))));
    }
}