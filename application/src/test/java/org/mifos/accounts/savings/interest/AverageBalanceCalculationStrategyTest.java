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

public class AverageBalanceCalculationStrategyTest {

    private PrincipalCalculationStrategy calculationStrategy;

    private InterestCalculationPeriodDetail interestCalculationPeriodDetail;

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
        return new InterestCalculationPeriodBuilder().from(september1st).to(september30th).withStartingBalance("0");
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

        interestCalculationPeriodDetail = zeroBalanceAug31stToSeptember30thCalculationPeriod().from(september6th.plusDays(1))
                                                                                              .containing(endOfDayDetail)
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
                                                                                              .from(september6th.plusDays(1))
                                                                                              .build();

        // exercise test
        Money averageBalancePrincipal = calculationStrategy.calculatePrincipal(interestCalculationPeriodDetail);

        // (1000 x 7 + 2000 x 17)/24 = 1708.333333..
        // verification
        assertThat(averageBalancePrincipal, is(TestUtils.createMoney("1708.3")));
    }

    @Test
    public void shouldCalculateAverageBalanceGivenOneDepositExistBeforeRange() {

        EndOfDayDetail september6thDetails = new EndOfDayBuilder().on(september6th).withDespoitsOf("1000").build();
        EndOfDayDetail september13thDetails = new EndOfDayBuilder().on(september13th).withDespoitsOf("1000").build();
        EndOfDayDetail september20thDetails = new EndOfDayBuilder().on(september20th).withDespoitsOf("500").build();

        interestCalculationPeriodDetail = zeroBalanceAug31stToSeptember30thCalculationPeriod().withStartingBalance("1000")
                                                .containing(september6thDetails, september13thDetails, september20thDetails)
                                                .build();

        // exercise test
        Money averageBalancePrincipal = calculationStrategy.calculatePrincipal(interestCalculationPeriodDetail);

        // verification
        // (1000 * 6 + 2000 * 7 + 3000 * 7 + 3500 * 10)/30
        assertThat(averageBalancePrincipal, is(TestUtils.createMoney(("2533.3"))));
    }

    @Test
    public void shouldCalculateAverageBalanceSECDEPDataTest() {

        LocalDate apr1st =new LocalDate(new DateTime().withDate(2010, 4, 1));
        LocalDate apr5 =new LocalDate(new DateTime().withDate(2010, 4, 5));
        LocalDate apr12 =new LocalDate(new DateTime().withDate(2010, 4, 12));
        LocalDate apr19 =new LocalDate(new DateTime().withDate(2010, 4, 19));
        LocalDate apr26 =new LocalDate(new DateTime().withDate(2010, 4, 26));

        LocalDate may3 =new LocalDate(new DateTime().withDate(2010, 5, 3));
        LocalDate may10 =new LocalDate(new DateTime().withDate(2010, 5, 10));
        LocalDate may17 =new LocalDate(new DateTime().withDate(2010, 5, 17));
        LocalDate may24 =new LocalDate(new DateTime().withDate(2010, 5, 24));
        LocalDate may31 =new LocalDate(new DateTime().withDate(2010, 5, 31));

        LocalDate jun7 =new LocalDate(new DateTime().withDate(2010, 6, 7));
        LocalDate jun14 =new LocalDate(new DateTime().withDate(2010, 6, 14));
        LocalDate jun21 =new LocalDate(new DateTime().withDate(2010, 6, 21));
        LocalDate jun28 =new LocalDate(new DateTime().withDate(2010, 6, 28));
        LocalDate jun30 =new LocalDate(new DateTime().withDate(2010, 6, 30));

        EndOfDayDetail apr5Details = new EndOfDayBuilder().on(apr5).withDespoitsOf("20").build();
        EndOfDayDetail apr12Details = new EndOfDayBuilder().on(apr12).withDespoitsOf("20").build();
        EndOfDayDetail apr19Details = new EndOfDayBuilder().on(apr19).withDespoitsOf("20").build();
        EndOfDayDetail apr26Details = new EndOfDayBuilder().on(apr26).withDespoitsOf("20").build();

        EndOfDayDetail may3Details = new EndOfDayBuilder().on(may3).withDespoitsOf("20").build();
        EndOfDayDetail may10Details = new EndOfDayBuilder().on(may10).withDespoitsOf("20").build();
        EndOfDayDetail may17Details = new EndOfDayBuilder().on(may17).withDespoitsOf("20").build();
        EndOfDayDetail may24Details = new EndOfDayBuilder().on(may24).withDespoitsOf("20").build();
        EndOfDayDetail may31Details = new EndOfDayBuilder().on(may31).withDespoitsOf("20").build();

        EndOfDayDetail jun7Details = new EndOfDayBuilder().on(jun7).withDespoitsOf("20").build();
        EndOfDayDetail jun14Details = new EndOfDayBuilder().on(jun14).withDespoitsOf("20").build();
        EndOfDayDetail jun21Details = new EndOfDayBuilder().on(jun21).withDespoitsOf("20").build();
        EndOfDayDetail jun28Details = new EndOfDayBuilder().on(jun28).withDespoitsOf("520").build();

        interestCalculationPeriodDetail = new InterestCalculationPeriodBuilder().from(apr1st).to(jun30)
                                                                                                .withStartingBalance("1765")
                                                                                                .containing(apr5Details,
                                                                                                        apr12Details,
                                                                                                        apr19Details,
                                                                                                        apr26Details,
                                                                                                        may3Details,
                                                                                                        may10Details,
                                                                                                        may17Details,
                                                                                                        may24Details,
                                                                                                        may31Details,
                                                                                                        jun7Details,
                                                                                                        jun14Details,
                                                                                                        jun21Details,
                                                                                                        jun28Details)
                                                                                                .build();

        // exercise test
        Money averageBalancePrincipal = calculationStrategy.calculatePrincipal(interestCalculationPeriodDetail);

        // verification
        assertThat(averageBalancePrincipal, is(TestUtils.createMoney(("1901.7"))));
    }
}