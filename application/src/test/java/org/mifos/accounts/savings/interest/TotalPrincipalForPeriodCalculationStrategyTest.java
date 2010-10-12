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
import org.mifos.config.ConfigurationManager;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.Money;

public class TotalPrincipalForPeriodCalculationStrategyTest {

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
        oldRoundingMode =  (String) ConfigurationManager.getInstance().getProperty(AccountingRulesConstants.CURRENCY_ROUNDING_MODE);
        ConfigurationManager.getInstance().setProperty(AccountingRulesConstants.CURRENCY_ROUNDING_MODE, RoundingMode.HALF_UP.toString());
    }

    @AfterClass
    public static void resetCurrency() {
        ConfigurationManager.getInstance().setProperty(AccountingRulesConstants.CURRENCY_ROUNDING_MODE, oldRoundingMode);
        Money.setDefaultCurrency(oldCurrency);
    }

    private InterestCalculationPeriodBuilder zeroBalanceAug31stToSeptember30thCalculationPeriod() {
        return new InterestCalculationPeriodBuilder().from(august31st).to(september30th).withStartingBalance("90");
    }

    @Before
    public void setup() {
        calculationStrategy = new TotalPrincipalForPeriodCalculationStrategy();
    }

    @Test
    public void shouldNotTakeBalanceAtStartOfPeriodIntoAccountWhenCalculatingTotalPrincipalForGivenPeriod() {

        interestCalculationPeriodDetail = zeroBalanceAug31stToSeptember30thCalculationPeriod().build();

        // exercise test
        Money totalBalancePrincipal = calculationStrategy.calculatePrincipal(interestCalculationPeriodDetail);

        assertThat(totalBalancePrincipal, is(TestUtils.createMoney("0")));
    }

    @Test
    public void shouldCalculateTotalBalanceGivenOnlyOneDailyBalanceExistsWithinRange() {

        Money deposit1 = TestUtils.createMoney("1000");
        Money withdrawal1 = TestUtils.createMoney("0");
        Money interest1 = TestUtils.createMoney("0");
        EndOfDayDetail endOfDayDetail = new EndOfDayDetail(september6th, deposit1, withdrawal1, interest1);

        interestCalculationPeriodDetail = zeroBalanceAug31stToSeptember30thCalculationPeriod()
                                                                                              .containing(endOfDayDetail)
                                                                                              .build();

        // exercise test
        Money averageBalancePrincipal = calculationStrategy.calculatePrincipal(interestCalculationPeriodDetail);

        assertThat(averageBalancePrincipal, is(TestUtils.createMoney("1000")));
    }

    @Test
    public void shouldCalculateTotalBalanceGivenTwoDailyBalancesExistWithinRange() {

        Money deposit1 = TestUtils.createMoney("1000");
        Money withdrawal1 = TestUtils.createMoney("0");
        Money interest1 = TestUtils.createMoney("0");
        EndOfDayDetail september6thDetails = new EndOfDayDetail(september6th, deposit1, withdrawal1, interest1);

        Money deposit2 = TestUtils.createMoney("1000");
        Money withdrawal2 = TestUtils.createMoney("0");
        Money interest2 = TestUtils.createMoney("0");
        EndOfDayDetail september13thDetails = new EndOfDayDetail(september13th, deposit2, withdrawal2, interest2);

        interestCalculationPeriodDetail = zeroBalanceAug31stToSeptember30thCalculationPeriod()
                                                                                              .containing(september6thDetails, september13thDetails)
                                                                                              .from(september6th)
                                                                                              .build();

        // exercise test
        Money averageBalancePrincipal = calculationStrategy.calculatePrincipal(interestCalculationPeriodDetail);

        // verification
        assertThat(averageBalancePrincipal, is(TestUtils.createMoney("2000")));
    }

    @Test
    public void shouldCalculateTotalBalanceGivenOneDepositExistBeforeRange() {

        Money deposit1 = TestUtils.createMoney("0");
        Money withdrawal1 = TestUtils.createMoney("0");
        Money interest1 = TestUtils.createMoney("0");
        EndOfDayDetail september1stDetails = new EndOfDayDetail(september1st, deposit1, withdrawal1, interest1);

        Money deposit2 = TestUtils.createMoney("1000");
        Money withdrawal2 = TestUtils.createMoney("0");
        Money interest2 = TestUtils.createMoney("0");
        EndOfDayDetail september6thDetails = new EndOfDayDetail(september6th, deposit2, withdrawal2, interest2);

        Money deposit3 = TestUtils.createMoney("1000");
        Money withdrawal3 = TestUtils.createMoney("0");
        Money interest3 = TestUtils.createMoney("0");
        EndOfDayDetail september13thDetails = new EndOfDayDetail(september13th, deposit3, withdrawal3, interest3);

        Money deposit4 = TestUtils.createMoney("500");
        Money withdrawal4 = TestUtils.createMoney("0");
        Money interest4 = TestUtils.createMoney("0");
        EndOfDayDetail september20thDetails = new EndOfDayDetail(september20th, deposit4, withdrawal4, interest4);

        interestCalculationPeriodDetail = zeroBalanceAug31stToSeptember30thCalculationPeriod()
                                                .containing(september1stDetails, september6thDetails, september13thDetails, september20thDetails)
                                                .build();

        // exercise testt
        Money averageBalancePrincipal = calculationStrategy.calculatePrincipal(interestCalculationPeriodDetail);

        // verification
        assertThat(averageBalancePrincipal, is(TestUtils.createMoney(("2500"))));
    }
}