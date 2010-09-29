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

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.business.AccountActionEntity;
import org.mifos.accounts.savings.business.SavingsActivityEntity;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.framework.TestUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class InterestCalculatorTest {

    private InterestCalculator interestCalculator;

    @Mock
    private PersonnelBO personnel;

    @Mock
    private SavingsBO account;

    private MathContext mathContext;

    private InterestCalculationRange interestCalculationRange;

    private int totalDaysInRange = 0;
    private int totalDaysFromFirstDeposit = 0;
    private LocalDate sept1st = new LocalDate(new DateTime().withDate(2010, 9, 1));
    private LocalDate sept6th = new LocalDate(new DateTime().withDate(2010, 9, 6));
    private LocalDate september13th = new LocalDate(new DateTime().withDate(2010, 9, 13));
    private LocalDate september20th = new LocalDate(new DateTime().withDate(2010, 9, 20));
    private LocalDate october1st = new LocalDate(new DateTime().withDate(2010, 10, 1));

    @Before
    public void setup() {
        interestCalculator = new AverageBalanceInterestCalculator();
        int internalPrecision = 5;
        RoundingMode internalRoundingMode = RoundingMode.HALF_UP;
        mathContext = new MathContext(internalPrecision, internalRoundingMode);

        interestCalculationRange = new InterestCalculationRange(sept1st, october1st);

        totalDaysInRange = Days.daysBetween(interestCalculationRange.getLowerDate(), interestCalculationRange.getUpperDate()).getDays();
        totalDaysFromFirstDeposit = Days.daysBetween(sept6th, interestCalculationRange.getUpperDate()).getDays();
    }

    @Test
    public void shouldCalculateInterestForSimpleDeposit() {

        BigDecimal deposit1 = BigDecimal.valueOf(Double.valueOf("1000.0"));

        EndOfDayDetail endOfDayDetail = new EndOfDayDetail(sept6th, deposit1);

        BigDecimal interest = interestCalculator.calcInterest(interestCalculationRange, endOfDayDetail);

        BigDecimal expectedInterest = new BigDecimal(Double.valueOf("100.00011123"), mathContext);
        assertThat(interest, is(expectedInterest));
    }

    @Test
    public void shouldCalculateInterestForTwoSimpleDeposits() {

        BigDecimal deposit1 = BigDecimal.valueOf(Double.valueOf("1000.0"));
        EndOfDayDetail endOfDayDetail = new EndOfDayDetail(sept6th, deposit1);

        BigDecimal deposit2 = BigDecimal.valueOf(Double.valueOf("1000.0"));
        EndOfDayDetail endOfDayDetail2 = new EndOfDayDetail(september13th, deposit2);

        // exercise test
        BigDecimal interest = interestCalculator.calcInterest(interestCalculationRange, endOfDayDetail, endOfDayDetail2);

        // verification

//      avgbal = (1000 x7) + (2000 x 18)/ 25  =
        // 7 days from 6 september to 13th of september
        // 18 days from 13 september to 1st of october
        BigDecimal expectedInterest = new BigDecimal(Double.valueOf("172.00011123"), mathContext);
        assertThat(interest, is(expectedInterest));
    }

    @Test
    public void shouldCalculateInterestForThreeSimpleDeposits() {

        BigDecimal deposit1 = BigDecimal.valueOf(Double.valueOf("1000.0"));
        EndOfDayDetail endOfDayDetail = new EndOfDayDetail(sept6th, deposit1);

        BigDecimal deposit2 = BigDecimal.valueOf(Double.valueOf("1000.0"));
        EndOfDayDetail endOfDayDetail2 = new EndOfDayDetail(september13th, deposit2);

        BigDecimal deposit3 = BigDecimal.valueOf(Double.valueOf("500.0"));
        EndOfDayDetail endOfDayDetail3 = new EndOfDayDetail(september20th, deposit3);

        // exercise test
        BigDecimal interest = interestCalculator.calcInterest(interestCalculationRange, endOfDayDetail, endOfDayDetail2, endOfDayDetail3);

        // verification

//      avgbal = (1000 x7) + (2000 x 7) + (2500x11) / 25  = 48500 /25
        // 7 days from 6 september to 13th of september
        // 7 days from 13 september to 20th of september
        // 11 days from 20 september to 1st of october
        BigDecimal expectedInterest = new BigDecimal(Double.valueOf("194.00011123"), mathContext);
        assertThat(interest, is(expectedInterest));
    }


    @Test @Ignore
    public void testGetEffectiveBalanceRecordsForIntervalWhenNoActivitiesBeforeOrOnCalculationStartDate() {
        Set<SavingsActivityEntity> activities = new LinkedHashSet<SavingsActivityEntity>();
        activities.add(createActivity("1000", "1000", new LocalDate(2010, 9, 15), deposit()));
        activities.add(createActivity("1000", "2000", new LocalDate(2010, 9, 15), deposit()));
        activities.add(createActivity("2000", "4000", new LocalDate(2010, 9, 15), deposit()));
        activities.add(createActivity("1000", "3000", new LocalDate(2010, 9, 25), withdrawal()));
        activities.add(createActivity("1000", "4000", new LocalDate(2010, 9, 25), deposit()));
        activities.add(createActivity("1000", "5000", new LocalDate(2010, 9, 25), deposit()));

        LocalDate fromDate = new LocalDate(2010, 9, 1);
        LocalDate toDate = new LocalDate(2010, 9, 30);

        MinimumBalanceInterestCalculator ic = new MinimumBalanceInterestCalculator();

        List<EndOfDayBalance> balanceRecords = null;// ic.getEffectiveBalanceRecordsForInterval(fromDate, toDate, activities);

        Assert.assertEquals(2, balanceRecords.size());
        Assert.assertEquals(TestUtils.createMoney("4000"), balanceRecords.get(0).getBalance());
        Assert.assertEquals(new LocalDate(2010, 9, 15), balanceRecords.get(0).getDate());
        Assert.assertEquals(TestUtils.createMoney("5000"), balanceRecords.get(1).getBalance());
        Assert.assertEquals(new LocalDate(2010, 9, 25), balanceRecords.get(1).getDate());
    }

    @Test @Ignore
    public void testGetEffectiveBalanceRecordsForIntervalWhenActivitiesBeforeOrOnCalculationStartDate() {
        Set<SavingsActivityEntity> activities = new LinkedHashSet<SavingsActivityEntity>();
        activities.add(createActivity("1000", "1000", new LocalDate(2010, 8, 5), deposit()));
        activities.add(createActivity("1000", "0", new LocalDate(2010, 8, 5), withdrawal()));
        activities.add(createActivity("1000", "1000", new LocalDate(2010, 8, 5), deposit()));

        activities.add(createActivity("1000", "2000", new LocalDate(2010, 9, 15), deposit()));
        activities.add(createActivity("2000", "4000", new LocalDate(2010, 9, 15), deposit()));

        activities.add(createActivity("1000", "3000", new LocalDate(2010, 9, 25), withdrawal()));
        activities.add(createActivity("1000", "4000", new LocalDate(2010, 9, 25), deposit()));
        activities.add(createActivity("1000", "5000", new LocalDate(2010, 9, 25), deposit()));

        activities.add(createActivity("1000", "4000", new LocalDate(2010, 10, 8), withdrawal()));
        activities.add(createActivity("1000", "5000", new LocalDate(2010, 10, 8), deposit()));
        activities.add(createActivity("1000", "6000", new LocalDate(2010, 10, 8), deposit()));

        LocalDate fromDate = new LocalDate(2010, 9, 1);
        LocalDate toDate = new LocalDate(2010, 9, 30);

        MinimumBalanceInterestCalculator ic = new MinimumBalanceInterestCalculator();

        List<EndOfDayBalance> balanceRecords = null;// ic.getEffectiveBalanceRecordsForInterval(fromDate, toDate, activities);

        Assert.assertEquals(3, balanceRecords.size());
        Assert.assertEquals(TestUtils.createMoney("1000"), balanceRecords.get(0).getBalance());
        Assert.assertEquals(new LocalDate(2010, 9, 1), balanceRecords.get(0).getDate());
        Assert.assertEquals(TestUtils.createMoney("4000"), balanceRecords.get(1).getBalance());
        Assert.assertEquals(new LocalDate(2010, 9, 15), balanceRecords.get(1).getDate());
        Assert.assertEquals(TestUtils.createMoney("5000"), balanceRecords.get(2).getBalance());
        Assert.assertEquals(new LocalDate(2010, 9, 25), balanceRecords.get(2).getDate());
    }

    SavingsActivityEntity createActivity(String amount, String balance, LocalDate date, AccountActionTypes activity) {
        AccountActionEntity action = new AccountActionEntity(activity);
        Date trxnDate = date.toDateMidnight().toDate();
        return new SavingsActivityEntity(personnel, action, TestUtils.createMoney(amount),
                TestUtils.createMoney(balance), trxnDate, account);

    }

    AccountActionTypes deposit() {
        return AccountActionTypes.SAVINGS_DEPOSIT;
    }

    AccountActionTypes withdrawal() {
        return AccountActionTypes.SAVINGS_WITHDRAWAL;
    }

    AccountActionTypes adjustment() {
        return AccountActionTypes.SAVINGS_WITHDRAWAL;
    }

}
