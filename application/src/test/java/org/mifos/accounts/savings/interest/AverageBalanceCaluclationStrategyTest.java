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

import java.util.ArrayList;
import java.util.Arrays;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.Money;

public class AverageBalanceCaluclationStrategyTest {

    private PrincipalCalculationStrategy calculationStrategy;

    private InterestCalculationPeriodDetail interestCalculationPeriodDetail;
    private InterestCalculationRange interestCalculationRange;

    private LocalDate september1st = new LocalDate(new DateTime().withDate(2010, 9, 1));
    private LocalDate september6th = new LocalDate(new DateTime().withDate(2010, 9, 6));
    private LocalDate september13th = new LocalDate(new DateTime().withDate(2010, 9, 13));
    private LocalDate september20th = new LocalDate(new DateTime().withDate(2010, 9, 20));
    private LocalDate october1st = new LocalDate(new DateTime().withDate(2010, 10, 1));

    private Money money = TestUtils.createMoney("0");
    private static MifosCurrency oldCurrency;

    @BeforeClass
    public static void setCurrency() {
        oldCurrency = Money.getDefaultCurrency();
        Money.setDefaultCurrency(TestUtils.RUPEE);
    }

    @AfterClass
    public static void resetCurrency() {
        Money.setDefaultCurrency(oldCurrency);
    }

    @Before
    public void setup() {
        calculationStrategy = new AverageBalanceCaluclationStrategy();
        interestCalculationRange = new InterestCalculationRange(september1st, october1st);
    }

    @Test
    public void shouldRecieveZeroBalanceWithNoDailyRecords() {

        interestCalculationPeriodDetail = new InterestCalculationPeriodDetail(interestCalculationRange, new ArrayList<EndOfDayDetail>(), money, money, money.getCurrency(), 10.0);

        // exercise test
        Money averageBalancePrincipal = calculationStrategy.calculatePrincipal(interestCalculationPeriodDetail);

        assertThat(averageBalancePrincipal, is(TestUtils.createMoney("0")));
    }

    @Test
    public void shouldCalculateAverageBalanceGivenOnlyOneDailyBalanceExistsWithinRange() {

        Money deposit1 = TestUtils.createMoney("1000");
        Money withdrawal1 = TestUtils.createMoney("0");
        Money interest1 = TestUtils.createMoney("0");
        EndOfDayDetail endOfDayDetail = new EndOfDayDetail(september6th, deposit1, withdrawal1, interest1);

        interestCalculationPeriodDetail = new InterestCalculationPeriodDetail(interestCalculationRange, Arrays.asList(endOfDayDetail), money, money, money.getCurrency(), 10.0);

        // exercise test
        Money averageBalancePrincipal = calculationStrategy.calculatePrincipal(interestCalculationPeriodDetail);

        assertThat(averageBalancePrincipal, is(TestUtils.createMoney("1000")));
    }

    @Test
    public void shouldCalculateAverageBalanceGivenTwoDailyBalancesExistWithinRange() {

        Money deposit1 = TestUtils.createMoney("1000");
        Money withdrawal1 = TestUtils.createMoney("0");
        Money interest1 = TestUtils.createMoney("0");
        EndOfDayDetail endOfDayDetail = new EndOfDayDetail(september6th, deposit1, withdrawal1, interest1);

        Money deposit2 = TestUtils.createMoney("1000");
        Money withdrawal2 = TestUtils.createMoney("0");
        Money interest2 = TestUtils.createMoney("0");
        EndOfDayDetail endOfDayDetail2 = new EndOfDayDetail(september13th, deposit2, withdrawal2, interest2);

        interestCalculationPeriodDetail = new InterestCalculationPeriodDetail(interestCalculationRange, Arrays.asList(endOfDayDetail, endOfDayDetail2), money, money, money.getCurrency(), 10.0);

        // exercise test
        Money averageBalancePrincipal = calculationStrategy.calculatePrincipal(interestCalculationPeriodDetail);

        // verification
        assertThat(averageBalancePrincipal, is(TestUtils.createMoney("1720")));
    }

    @Test
    public void shouldCalculateAverageBalanceGivenThreeDailyBalancesExistWithinRange() {

        Money deposit1 = TestUtils.createMoney("1000");
        Money withdrawal1 = TestUtils.createMoney("0");
        Money interest1 = TestUtils.createMoney("0");
        EndOfDayDetail endOfDayDetail = new EndOfDayDetail(september6th, deposit1, withdrawal1, interest1);

        Money deposit2 = TestUtils.createMoney("1000");
        Money withdrawal2 = TestUtils.createMoney("0");
        Money interest2 = TestUtils.createMoney("0");
        EndOfDayDetail endOfDayDetail2 = new EndOfDayDetail(september13th, deposit2, withdrawal2, interest2);

        Money deposit3 = TestUtils.createMoney("500");
        Money withdrawal3 = TestUtils.createMoney("0");
        Money interest3 = TestUtils.createMoney("0");
        EndOfDayDetail endOfDayDetail3 = new EndOfDayDetail(september20th, deposit3, withdrawal3, interest3);

        interestCalculationPeriodDetail = new InterestCalculationPeriodDetail(interestCalculationRange, Arrays.asList(endOfDayDetail, endOfDayDetail2, endOfDayDetail3), money, money, money.getCurrency(), 10.0);

        // exercise test
        Money averageBalancePrincipal = calculationStrategy.calculatePrincipal(interestCalculationPeriodDetail);

        // verification
        assertThat(averageBalancePrincipal, is(TestUtils.createMoney(("1940"))));
    }
}