/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 *  explanation of the license and how it is applied.
 */
package org.mifos.accounts.loan.schedule.calculation;

import org.joda.time.DateMidnight;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mifos.accounts.loan.schedule.domain.Installment;
import org.mifos.accounts.loan.schedule.domain.Schedule;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mifos.framework.TestUtils.getDate;

public class ScheduleCalculatorTest {
    private ScheduleCalculator scheduleCalculator;
    private Schedule schedule;

    @Before
    public void setup() {
        scheduleCalculator = new ScheduleCalculator();
    }

    @Test
    public void withOneInstallmentAdjusted() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        Installment installment3 = getInstallment(3, getDate(25, 11, 2010), 252.22, 10.40, 0);
        Installment installment4 = getInstallment(4, getDate(25, 12, 2010), 257.87, 5.09, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2, installment3, installment4));
        scheduleCalculator.applyPayment(schedule, BigDecimal.valueOf(280d), getDate(25, 9, 2010));

        assertInstallmentPrincipals(installment1, 242.24, 0, 242.24);
        assertInstallmentPrincipals(installment2, 247.67, 230.31, 17.36);
        assertInstallmentPrincipals(installment3, 252.22, 252.22, 0);
        assertInstallmentPrincipals(installment4, 257.87, 257.87, 0);

        assertInstallmentInterests(installment1, 20.40, 0, 20.40);
        assertInstallmentInterests(installment2, 14.62, 14.62, 0);
        assertInstallmentInterests(installment3, 10.40, 10.40, 0);
        assertInstallmentInterests(installment4, 5.09, 5.09, 0);
    }

    @Test
    public void withExactPaymentOnTime() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        Installment installment3 = getInstallment(3, getDate(25, 11, 2010), 252.22, 10.40, 0);
        Installment installment4 = getInstallment(4, getDate(25, 12, 2010), 257.87, 5.09, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2, installment3, installment4));
        scheduleCalculator.applyPayment(schedule, BigDecimal.valueOf(262.64d), getDate(25, 9, 2010));

        assertInstallmentPrincipals(installment1, 242.24, 0, 242.24);
        assertInstallmentPrincipals(installment2, 247.67, 247.67, 0);
        assertInstallmentPrincipals(installment3, 252.22, 252.22, 0);
        assertInstallmentPrincipals(installment4, 257.87, 257.87, 0);

        assertInstallmentInterests(installment1, 20.40, 0, 20.40);
        assertInstallmentInterests(installment2, 14.96, 14.96, 0);
        assertInstallmentInterests(installment3, 10.40, 10.40, 0);
        assertInstallmentInterests(installment4, 5.09, 5.09, 0);
    }

    @Test
    public void withShortPaymentOnTime() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        Installment installment3 = getInstallment(3, getDate(25, 11, 2010), 252.22, 10.40, 0);
        Installment installment4 = getInstallment(4, getDate(25, 12, 2010), 257.87, 5.09, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2, installment3, installment4));
        scheduleCalculator.applyPayment(schedule, BigDecimal.valueOf(240d), getDate(25, 9, 2010));

        assertInstallmentPrincipals(installment1, 242.24, 22.64, 219.6);
        assertInstallmentPrincipals(installment2, 247.67, 247.67, 0);
        assertInstallmentPrincipals(installment3, 252.22, 252.22, 0);
        assertInstallmentPrincipals(installment4, 257.87, 257.87, 0);

        assertInstallmentInterests(installment1, 20.40, 0, 20.40);
        assertInstallmentInterests(installment2, 14.96, 14.96, 0);
        assertInstallmentInterests(installment3, 10.40, 10.40, 0);
        assertInstallmentInterests(installment4, 5.09, 5.09, 0);
    }

    @Test
    public void withTwoInstallmentsAdjusted() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        Installment installment3 = getInstallment(3, getDate(25, 11, 2010), 252.22, 10.40, 0);
        Installment installment4 = getInstallment(4, getDate(25, 12, 2010), 257.87, 5.09, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2, installment3, installment4));
        scheduleCalculator.applyPayment(schedule, BigDecimal.valueOf(550d), getDate(25, 9, 2010));

        assertInstallmentPrincipals(installment1, 242.24, 0, 242.24);
        assertInstallmentPrincipals(installment2, 247.67, 0, 247.67);
        assertInstallmentPrincipals(installment3, 252.22, 212.53, 39.69);
        assertInstallmentPrincipals(installment4, 257.87, 257.87, 0);

        assertInstallmentInterests(installment1, 20.40, 0, 20.40);
        assertInstallmentInterests(installment2, 9.29, 9.29, 0);
        assertInstallmentInterests(installment3, 18.88, 18.88, 0);
        assertInstallmentInterests(installment4, 5.09, 5.09, 0);
    }

    @Test
    public void withOverDueInterests() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        Installment installment3 = getInstallment(3, getDate(25, 11, 2010), 252.22, 10.40, 0);
        Installment installment4 = getInstallment(4, getDate(25, 12, 2010), 257.87, 5.09, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2, installment3, installment4));
        scheduleCalculator.applyPayment(schedule, BigDecimal.valueOf(550d), getDate(30, 9, 2010));

        assertInstallmentPrincipals(installment1, 242.24, 0, 242.24);
        assertInstallmentPrincipals(installment2, 247.67, 0, 247.67);
        assertInstallmentPrincipals(installment3, 252.22, 215.82, 36.4);
        assertInstallmentPrincipals(installment4, 257.87, 257.87, 0);

        assertInstallmentInterests(installment1, 20.40, 0, 20.40);
        assertInstallmentInterests(installment2, 7.79, 7.79, 2.49);
        assertInstallmentInterests(installment3, 17.45, 17.45, 0);
        assertInstallmentInterests(installment4, 5.09, 5.09, 0);
    }

    @Test
    public void withShortPaymentBeforeDueDate() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        Installment installment3 = getInstallment(3, getDate(25, 11, 2010), 252.22, 10.40, 0);
        Installment installment4 = getInstallment(4, getDate(25, 12, 2010), 257.87, 5.09, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2, installment3, installment4));
        scheduleCalculator.applyPayment(schedule, BigDecimal.valueOf(240d), getDate(23, 9, 2010));

        assertInstallmentPrincipals(installment1, 242.24, 21.32, 220.92);
        assertInstallmentPrincipals(installment2, 247.67, 247.67, 0);
        assertInstallmentPrincipals(installment3, 252.22, 252.22, 0);
        assertInstallmentPrincipals(installment4, 257.87, 257.87, 0);

        assertInstallmentInterests(installment1, 1.03, 1.03, 19.08);
        assertInstallmentInterests(installment2, 14.96, 14.96, 0);
        assertInstallmentInterests(installment3, 10.40, 10.40, 0);
        assertInstallmentInterests(installment4, 5.09, 5.09, 0);
    }

    @Test
    public void withShortPaymentAfterDueDate() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        Installment installment3 = getInstallment(3, getDate(25, 11, 2010), 252.22, 10.40, 0);
        Installment installment4 = getInstallment(4, getDate(25, 12, 2010), 257.87, 5.09, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2, installment3, installment4));
        scheduleCalculator.applyPayment(schedule, BigDecimal.valueOf(240d), getDate(29, 9, 2010));

        assertInstallmentPrincipals(installment1, 242.24, 22.64, 219.6);
        assertInstallmentPrincipals(installment2, 247.67, 247.67, 0);
        assertInstallmentPrincipals(installment3, 252.22, 252.22, 0);
        assertInstallmentPrincipals(installment4, 257.87, 257.87, 0);

        assertInstallmentInterests(installment1, 20.40, 0, 20.40);
        assertInstallmentInterests(installment2, 14.96, 14.96, 0);
        assertInstallmentInterests(installment3, 10.40, 10.40, 0);
        assertInstallmentInterests(installment4, 5.09, 5.09, 0);

        assertInstallmentForOverdueInterests(installment1, 0, 0, 0);
        assertInstallmentForOverdueInterests(installment2, 0.64, 0.64, 0);
        assertInstallmentForOverdueInterests(installment3, 0, 0, 0);
        assertInstallmentForOverdueInterests(installment4, 0, 0, 0);
    }

    @Test
    public void withExcessPaymentAfterDueDate() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        Installment installment3 = getInstallment(3, getDate(25, 11, 2010), 252.22, 10.40, 0);
        Installment installment4 = getInstallment(4, getDate(25, 12, 2010), 257.87, 5.09, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2, installment3, installment4));
        scheduleCalculator.applyPayment(schedule, BigDecimal.valueOf(280d), getDate(29, 9, 2010));

        assertInstallmentPrincipals(installment1, 242.24, 0, 242.24);
        assertInstallmentPrincipals(installment2, 247.67, 232.94, 14.73);
        assertInstallmentPrincipals(installment3, 252.22, 252.22, 0);
        assertInstallmentPrincipals(installment4, 257.87, 257.87, 0);

        assertInstallmentInterests(installment1, 20.40, 0, 20.40);
        assertInstallmentInterests(installment2, 12.71, 12.71, 1.99);
        assertInstallmentInterests(installment3, 10.40, 10.40, 0);
        assertInstallmentInterests(installment4, 5.09, 5.09, 0);

        assertInstallmentForOverdueInterests(installment1, 0, 0, 0);
        assertInstallmentForOverdueInterests(installment2, 0.64, 0, 0.64);
        assertInstallmentForOverdueInterests(installment3, 0, 0, 0);
        assertInstallmentForOverdueInterests(installment4, 0, 0, 0);
    }

    @Test
    public void withExactPaymentAfterDueDate() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        Installment installment3 = getInstallment(3, getDate(25, 11, 2010), 252.22, 10.40, 0);
        Installment installment4 = getInstallment(4, getDate(25, 12, 2010), 257.87, 5.09, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2, installment3, installment4));
        scheduleCalculator.applyPayment(schedule, BigDecimal.valueOf(262.64d), getDate(29, 9, 2010));

        assertInstallmentPrincipals(installment1, 242.24, 0, 242.24);
        assertInstallmentPrincipals(installment2, 247.67, 247.67, 0);
        assertInstallmentPrincipals(installment3, 252.22, 252.22, 0);
        assertInstallmentPrincipals(installment4, 257.87, 257.87, 0);

        assertInstallmentInterests(installment1, 20.40, 0, 20.40);
        assertInstallmentInterests(installment2, 14.96, 14.96, 0);
        assertInstallmentInterests(installment3, 10.40, 10.40, 0);
        assertInstallmentInterests(installment4, 5.09, 5.09, 0);

        assertInstallmentForOverdueInterests(installment1, 0, 0, 0);
        assertInstallmentForOverdueInterests(installment2, 0.64, 0.64, 0);
        assertInstallmentForOverdueInterests(installment3, 0, 0, 0);
        assertInstallmentForOverdueInterests(installment4, 0, 0, 0);
    }

    @Test
    public void withNoPaymentPaymentAfterDueDate() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        Installment installment3 = getInstallment(3, getDate(25, 11, 2010), 252.22, 10.40, 0);
        Installment installment4 = getInstallment(4, getDate(25, 12, 2010), 257.87, 5.09, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2, installment3, installment4));
        scheduleCalculator.computeOverdueInterest(schedule, getDate(26, 12, 2010));

        assertInstallmentPrincipals(installment1, 242.24, 242.24, 0);
        assertInstallmentPrincipals(installment2, 247.67, 247.67, 0);
        assertInstallmentPrincipals(installment3, 252.22, 252.22, 0);
        assertInstallmentPrincipals(installment4, 257.87, 257.87, 0);

        assertInstallmentInterests(installment1, 20.40, 20.40, 0);
        assertInstallmentInterests(installment2, 14.96, 14.96, 0);
        assertInstallmentInterests(installment3, 10.40, 10.40, 0);
        assertInstallmentInterests(installment4, 5.09, 5.09, 0);

        assertInstallmentForOverdueInterests(installment1, 0, 0, 0);
        assertInstallmentForOverdueInterests(installment2, 14.66, 14.66, 0);
        assertInstallmentForOverdueInterests(installment3, 10.1, 10.1, 0);
        assertInstallmentForOverdueInterests(installment4, 5.31, 5.31, 0);
    }


    @Test
    public void withExcessPaymentBeforeDueDate() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        Installment installment3 = getInstallment(3, getDate(25, 11, 2010), 252.22, 10.40, 0);
        Installment installment4 = getInstallment(4, getDate(25, 12, 2010), 257.87, 5.09, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2, installment3, installment4));
        scheduleCalculator.applyPayment(schedule, BigDecimal.valueOf(280d), getDate(23, 9, 2010));

        assertInstallmentPrincipals(installment1, 242.24, 0, 242.24);
        assertInstallmentPrincipals(installment2, 247.67, 228.99, 18.68);
        assertInstallmentPrincipals(installment3, 252.22, 252.22, 0);
        assertInstallmentPrincipals(installment4, 257.87, 257.87, 0);

        assertInstallmentInterests(installment1, 0.97, 0.97, 19.08);
        assertInstallmentInterests(installment2, 15.56, 15.56, 0);
        assertInstallmentInterests(installment3, 10.40, 10.40, 0);
        assertInstallmentInterests(installment4, 5.09, 5.09, 0);
    }

    @Test
    public void withExactPaymentBeforeDueDate() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        Installment installment3 = getInstallment(3, getDate(25, 11, 2010), 252.22, 10.40, 0);
        Installment installment4 = getInstallment(4, getDate(25, 12, 2010), 257.87, 5.09, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2, installment3, installment4));
        scheduleCalculator.applyPayment(schedule, BigDecimal.valueOf(261.32d), getDate(23, 9, 2010));

        assertInstallmentPrincipals(installment1, 242.24, 0, 242.24);
        assertInstallmentPrincipals(installment2, 247.67, 247.67, 0);
        assertInstallmentPrincipals(installment3, 252.22, 252.22, 0);
        assertInstallmentPrincipals(installment4, 257.87, 257.87, 0);

        assertInstallmentInterests(installment1, 1, 1, 19.08);
        assertInstallmentInterests(installment2, 15.96, 15.96, 0);
        assertInstallmentInterests(installment3, 10.40, 10.40, 0);
        assertInstallmentInterests(installment4, 5.09, 5.09, 0);
    }

    @Test
    public void withInitialInstallmentAmountBeforeDueDate() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        Installment installment3 = getInstallment(3, getDate(25, 11, 2010), 252.22, 10.40, 0);
        Installment installment4 = getInstallment(4, getDate(25, 12, 2010), 257.87, 5.09, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2, installment3, installment4));
        scheduleCalculator.applyPayment(schedule, BigDecimal.valueOf(262.64d), getDate(23, 9, 2010));

        assertInstallmentPrincipals(installment1, 242.24, 0, 242.24);
        assertInstallmentPrincipals(installment2, 247.67, 246.35, 1.32);
        assertInstallmentPrincipals(installment3, 252.22, 252.22, 0);
        assertInstallmentPrincipals(installment4, 257.87, 257.87, 0);

        assertInstallmentInterests(installment1, 1, 1, 19.08);
        assertInstallmentInterests(installment2, 15.93, 15.93, 0);
        assertInstallmentInterests(installment3, 10.40, 10.40, 0);
        assertInstallmentInterests(installment4, 5.09, 5.09, 0);
    }

    @Test
    public void shouldComputeOverdueInterestBeforeDisbursement() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2));
        scheduleCalculator.computeOverdueInterest(schedule, getDate(20, 8, 2010));
        assertThat(installment1.getOverdueInterest().doubleValue(), is(0.0));
        assertThat(installment2.getOverdueInterest().doubleValue(), is(0.0));
    }

    @Test
    public void shouldComputeOverdueInterestOnDisbursement() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2));
        scheduleCalculator.computeOverdueInterest(schedule, getDate(25, 8, 2010));
        assertThat(installment1.getOverdueInterest().doubleValue(), is(0.0));
        assertThat(installment2.getOverdueInterest().doubleValue(), is(0.0));
    }

    @Test
    public void shouldComputeOverdueInterestOnDueDate() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2));
        scheduleCalculator.computeOverdueInterest(schedule, getDate(25, 9, 2010));
        assertThat(installment1.getOverdueInterest().doubleValue(), is(0.0));
        assertThat(installment2.getOverdueInterest().doubleValue(), is(0.0));
    }

    @Test
    public void shouldComputeOneDayOverdueInterest() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2));
        scheduleCalculator.computeOverdueInterest(schedule, getDate(26, 9, 2010));
        assertThat(installment1.getOverdueInterest().doubleValue(), is(0.0));
        assertThat(installment2.getOverdueInterest().doubleValue(), is(0.16));
    }

    @Test
    public void shouldComputeTwoDayOverdueInterest() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2));
        scheduleCalculator.computeOverdueInterest(schedule, getDate(27, 9, 2010));
        assertThat(installment1.getOverdueInterest().doubleValue(), is(0.0));
        assertThat(installment2.getOverdueInterest().doubleValue(), is(0.32));
    }

    @Test
    public void shouldComputeOverdueInterestAfterPartPay() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2));
        scheduleCalculator.computeOverdueInterest(schedule, getDate(27, 9, 2010));
        assertThat(installment1.getOverdueInterest().doubleValue(), is(0.0));
        assertThat(installment2.getOverdueInterest().doubleValue(), is(0.32));
        installment1.pay(BigDecimal.valueOf(100), getDate(27, 9, 2010));
        scheduleCalculator.computeOverdueInterest(schedule, getDate(28, 9, 2010));
        assertThat(installment1.getOverdueInterest().doubleValue(), is(0.0));
        assertThat(installment2.getOverdueInterest().doubleValue(), is(0.43));
    }

    @Test @Ignore("Uncomment and verify the algorithm during payment")
    public void shouldComputeOverdueInterestAfterMultiplePaymentsOnSameDay() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2));

        scheduleCalculator.applyPayment(schedule, new BigDecimal(100), getDate(27, 9, 2010));
        assertThat(installment2.getOverdueInterestPaid().doubleValue(), is(0.0));
        assertThat(installment1.getOverdueInterest().doubleValue(), is(0.0));
        assertThat(installment2.getOverdueInterest().doubleValue(), is(0.43));

        scheduleCalculator.applyPayment(schedule, new BigDecimal(200), getDate(27, 9, 2010));
        // overdue computed on at time of payment remains same
        // irrespective of paying off the installment through multiple payments
        assertThat(installment2.getOverdueInterestPaid().doubleValue(), is(0.43));
        assertThat(installment2.getOverdueInterest().doubleValue(), is(0.43));
    }

    @Test
    public void shouldComputeOverdueFullPayment() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2));
        scheduleCalculator.applyPayment(schedule, new BigDecimal(262.64), getDate(25, 9, 2010));
        scheduleCalculator.applyPayment(schedule, new BigDecimal(262.63), getDate(25, 10, 2010));

        scheduleCalculator.computeOverdueInterest(schedule, getDate(30, 12, 2010));
        assertThat(installment1.getOverdueInterest().doubleValue(), is(0.0));
        assertThat(installment2.getOverdueInterest().doubleValue(), is(0.0));
    }

    @Test
    public void overdueOnLastInstallmentIsRecoveredInLastInstallment() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2));
        scheduleCalculator.computeOverdueInterest(schedule, getDate(20, 11, 2010));
        assertThat(installment1.getOverdueInterest().doubleValue(), is(0.0));
        assertThat(installment2.getOverdueInterest().doubleValue(), is(13.17));
    }

    private void assertInstallmentPrincipals(Installment installment, double principal, double principalDue, double principalPaid) {
        assertThat(installment.getPrincipal().doubleValue(), is(principal));
        assertThat(installment.getPrincipalDue().doubleValue(), is(principalDue));
        assertThat(installment.getPrincipalPaid().doubleValue(), is(principalPaid));
    }

    private void assertInstallmentInterests(Installment installment, double interest, double interestDue, double interestPaid) {
        assertThat(installment.getApplicableInterest().doubleValue(), is(interest));
        assertThat(installment.getInterestDue().doubleValue(), is(interestDue));
        assertThat(installment.getInterestPaid().doubleValue(), is(interestPaid));
    }

    private void assertInstallmentForOverdueInterests(Installment installment, double overdueInterest, double overdueInterestDue, double overdueInterestPaid) {
        assertThat(installment.getOverdueInterest().doubleValue(), is(overdueInterest));
        assertThat(installment.getOverdueInterestDue().doubleValue(), is(overdueInterestDue));
        assertThat(installment.getOverdueInterestPaid().doubleValue(), is(overdueInterestPaid));
    }

    private Installment getInstallment(int id, Date dueDate, double principal, double interest, double overdueInterest) {
        Installment installment = new Installment(id, dueDate, BigDecimal.valueOf(principal), BigDecimal.valueOf(interest), BigDecimal.ZERO);
        installment.setOverdueInterest(BigDecimal.valueOf(overdueInterest));
        return installment;
    }
}
