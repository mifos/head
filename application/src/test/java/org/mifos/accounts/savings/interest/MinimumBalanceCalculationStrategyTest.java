/*
 * Copyright Grameen Foundation USA
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

public class MinimumBalanceCalculationStrategyTest {

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
        oldRoundingMode = (String) MifosConfigurationManager.getInstance().getProperty(
                AccountingRulesConstants.CURRENCY_ROUNDING_MODE);
        MifosConfigurationManager.getInstance().setProperty(AccountingRulesConstants.CURRENCY_ROUNDING_MODE,
                RoundingMode.HALF_UP.toString());
    }

    @AfterClass
    public static void resetCurrency() {
        MifosConfigurationManager.getInstance()
                .setProperty(AccountingRulesConstants.CURRENCY_ROUNDING_MODE, oldRoundingMode);
        Money.setDefaultCurrency(oldCurrency);
    }

    private InterestCalculationPeriodBuilder withBalanceOnAug31stToSeptember30thCalculationPeriod() {
        return new InterestCalculationPeriodBuilder().from(august31st).to(september30th).withStartingBalance("1000");
    }

    @Before
    public void setup() {
        calculationStrategy = new MinimumBalanceCalculationStrategy();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWithNull() {

        // exercise test
        calculationStrategy.calculatePrincipal(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWithNullBalanceBeforeInterval() {

        interestCalculationPeriodDetail = withBalanceOnAug31stToSeptember30thCalculationPeriod()
                .withBalanceBeforeInterval(null).build();

        // exercise test
        calculationStrategy.calculatePrincipal(interestCalculationPeriodDetail);
    }

    @Test
    public void noDailyDetailsWithExistingBalanceBeforeInterval() {

        interestCalculationPeriodDetail = withBalanceOnAug31stToSeptember30thCalculationPeriod().build();

        // exercise test
        Money minimumBalancePrincipal = calculationStrategy.calculatePrincipal(interestCalculationPeriodDetail);

        assertThat(minimumBalancePrincipal, is(TestUtils.createMoney("1000")));
    }

    @Test
    public void oneDepositWithExistingBalanceBeforeInterval() {

        EndOfDayDetail september6thDetails = new EndOfDayBuilder().on(september6th).withDespoitsOf("500").build();

        interestCalculationPeriodDetail = withBalanceOnAug31stToSeptember30thCalculationPeriod().containing(september6thDetails).build();

        // exercise test
        Money minimumBalancePrincipal = calculationStrategy.calculatePrincipal(interestCalculationPeriodDetail);

        assertThat(minimumBalancePrincipal, is(TestUtils.createMoney("1000")));
    }

    @Test
    public void oneWithdrawalWithExistingBalanceBeforeInterval() {

        EndOfDayDetail september6thDetails = new EndOfDayBuilder().on(september6th).withWithdrawalsOf("500").build();

        interestCalculationPeriodDetail = withBalanceOnAug31stToSeptember30thCalculationPeriod().containing(september6thDetails).build();

        // exercise test
        Money minimumBalancePrincipal = calculationStrategy.calculatePrincipal(interestCalculationPeriodDetail);

        assertThat(minimumBalancePrincipal, is(TestUtils.createMoney("500")));
    }

    @Test
    public void oneWithdrawalThenOneDepositWithExistingBalanceBeforeInterval() {

        EndOfDayDetail september6thDetails = new EndOfDayBuilder().on(september6th).withWithdrawalsOf("500").build();
        EndOfDayDetail september13thDetails = new EndOfDayBuilder().on(september13th).withDespoitsOf("550").build();

        interestCalculationPeriodDetail = withBalanceOnAug31stToSeptember30thCalculationPeriod()
                .containing(september6thDetails, september13thDetails).build();

        // exercise test
        Money minimumBalancePrincipal = calculationStrategy.calculatePrincipal(interestCalculationPeriodDetail);

        assertThat(minimumBalancePrincipal, is(TestUtils.createMoney("500")));
    }

    @Test
    public void oneDepositThenOneWithdrawalWithExistingBalanceBeforeInterval() {

        EndOfDayDetail september6thDetails = new EndOfDayBuilder().on(september6th).withDespoitsOf("550").build();
        EndOfDayDetail september13thDetails = new EndOfDayBuilder().on(september13th).withWithdrawalsOf("500").build();

        interestCalculationPeriodDetail = withBalanceOnAug31stToSeptember30thCalculationPeriod()
                .containing(september6thDetails, september13thDetails).build();

        // exercise test
        Money minimumBalancePrincipal = calculationStrategy.calculatePrincipal(interestCalculationPeriodDetail);

        assertThat(minimumBalancePrincipal, is(TestUtils.createMoney("1000")));
    }

    @Test
    public void twoWithdrawalThenTwoDepositWithExistingBalanceBeforeInterval() {

        EndOfDayDetail september1stDetails = new EndOfDayBuilder().on(september1st).withWithdrawalsOf("100").build();
        EndOfDayDetail september6thDetails = new EndOfDayBuilder().on(september6th).withWithdrawalsOf("100").build();
        EndOfDayDetail september13thDetails = new EndOfDayBuilder().on(september13th).withDespoitsOf("100").build();
        EndOfDayDetail september20thDetails = new EndOfDayBuilder().on(september20th).withDespoitsOf("1000").build();

        interestCalculationPeriodDetail = withBalanceOnAug31stToSeptember30thCalculationPeriod()
                .containing(september1stDetails, september6thDetails, september13thDetails, september20thDetails).build();

        // exercise test
        Money minimumBalancePrincipal = calculationStrategy.calculatePrincipal(interestCalculationPeriodDetail);

        assertThat(minimumBalancePrincipal, is(TestUtils.createMoney("800")));
    }


    @Test
    public void twoDepositThenTwoWithdrawalWithExistingBalanceBeforeInterval() {

        EndOfDayDetail september1stDetails = new EndOfDayBuilder().on(september1st).withDespoitsOf("100").build();
        EndOfDayDetail september6thDetails = new EndOfDayBuilder().on(september6th).withDespoitsOf("100").build();
        EndOfDayDetail september13thDetails = new EndOfDayBuilder().on(september13th).withWithdrawalsOf("100").build();
        EndOfDayDetail september20thDetails = new EndOfDayBuilder().on(september20th).withWithdrawalsOf("1000").build();

        interestCalculationPeriodDetail = withBalanceOnAug31stToSeptember30thCalculationPeriod()
                .containing(september1stDetails, september6thDetails, september13thDetails, september20thDetails).build();

        // exercise test
        Money minimumBalancePrincipal = calculationStrategy.calculatePrincipal(interestCalculationPeriodDetail);

        assertThat(minimumBalancePrincipal, is(TestUtils.createMoney("100")));
    }

    @Test
    public void fourEntriesWithExistingBalanceBeforeInterval() {

        EndOfDayDetail september1stDetails = new EndOfDayBuilder().on(september1st).withDespoitsOf("1100").build(); // Balance 2100
        EndOfDayDetail september6thDetails = new EndOfDayBuilder().on(september6th).withWithdrawalsOf("900").build(); // Balance 1200
        EndOfDayDetail september13thDetails = new EndOfDayBuilder().on(september13th).withWithdrawalsOf("1000").build(); // Balance 200 * Minimum
        EndOfDayDetail september20thDetails = new EndOfDayBuilder().on(september20th).withDespoitsOf("2000").build(); // Balance 2200

        interestCalculationPeriodDetail = withBalanceOnAug31stToSeptember30thCalculationPeriod()
                .containing(september1stDetails, september6thDetails, september13thDetails, september20thDetails).build();

        // exercise test
        Money minimumBalancePrincipal = calculationStrategy.calculatePrincipal(interestCalculationPeriodDetail);

        assertThat(minimumBalancePrincipal, is(TestUtils.createMoney("200")));
    }

    @Test // MIFOSTEST-140
    public void twoDepositWithFirstActivityWithinInterval() {

        EndOfDayDetail september13thDetails = new EndOfDayBuilder().on(september13th).withDespoitsOf("12000").build(); // Balance 12000 * Minimum
        EndOfDayDetail september20thDetails = new EndOfDayBuilder().on(september20th).withDespoitsOf("15000").build(); // Balance 15000

        interestCalculationPeriodDetail = new InterestCalculationPeriodBuilder().from(august31st).to(september30th).isFirstActivity().withStartingBalance("0")
                .containing(september13thDetails, september20thDetails).build();

        // exercise test
        Money minimumBalancePrincipal = calculationStrategy.calculatePrincipal(interestCalculationPeriodDetail);

        assertThat(minimumBalancePrincipal, is(TestUtils.createMoney("12000")));
    }

}