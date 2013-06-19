/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mifos.framework.TestUtils.getDate;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mifos.accounts.loan.business.RepaymentResultsHolder;
import org.mifos.accounts.loan.schedule.domain.Installment;
import org.mifos.accounts.loan.schedule.domain.Schedule;

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
        scheduleCalculator.applyPayment(schedule, BigDecimal.valueOf(280d), getDate(25, 9, 2010), false);

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
        scheduleCalculator.applyPayment(schedule, BigDecimal.valueOf(262.64d), getDate(25, 9, 2010), false);

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
    public void exactPaymentOnTimeWithFeesAndPenalty() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0, 1, 2, 3, 4);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        Installment installment3 = getInstallment(3, getDate(25, 11, 2010), 252.22, 10.40, 0);
        Installment installment4 = getInstallment(4, getDate(25, 12, 2010), 257.87, 5.09, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2, installment3, installment4));
        scheduleCalculator.applyPayment(schedule, BigDecimal.valueOf(272.64d), getDate(25, 9, 2010), false);

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
        scheduleCalculator.applyPayment(schedule, BigDecimal.valueOf(240d), getDate(25, 9, 2010), false);

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
        scheduleCalculator.applyPayment(schedule, BigDecimal.valueOf(550d), getDate(25, 9, 2010), false);

        assertInstallmentPrincipals(installment1, 242.24, 0, 242.24);
        assertInstallmentPrincipals(installment2, 247.67, 0, 247.67);
        assertInstallmentPrincipals(installment3, 252.22, 212.53, 39.69);
        assertInstallmentPrincipals(installment4, 257.87, 257.87, 0);

        assertInstallmentInterests(installment1, 20.40, 0, 20.40);
        assertInstallmentInterests(installment2, 9.29, 9.29, 0);
        assertInstallmentInterests(installment3, 9.6, 9.6, 0);
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
        scheduleCalculator.applyPayment(schedule, BigDecimal.valueOf(550d), getDate(30, 9, 2010), false);

        assertInstallmentPrincipals(installment1, 242.24, 0, 242.24);
        assertInstallmentPrincipals(installment2, 247.67, 0, 247.67);
        assertInstallmentPrincipals(installment3, 252.22, 215.82, 36.4);
        assertInstallmentPrincipals(installment4, 257.87, 257.87, 0);

        assertInstallmentInterests(installment1, 20.40, 0, 20.40);
        assertInstallmentInterests(installment2, 7.79, 7.79, 2.49);
        assertInstallmentInterests(installment3, 9.66, 9.66, 0);
        assertInstallmentInterests(installment4, 5.09, 5.09, 0);
    }

    @Test
    public void withOverDueInterestsAndFeesAndPenalty() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0, 1, 2, 3, 4);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0, 1, 2, 3, 4);
        Installment installment3 = getInstallment(3, getDate(25, 11, 2010), 252.22, 10.40, 0);
        Installment installment4 = getInstallment(4, getDate(25, 12, 2010), 257.87, 5.09, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2, installment3, installment4));
        scheduleCalculator.applyPayment(schedule, BigDecimal.valueOf(560d), getDate(30, 9, 2010), false);

        assertInstallmentPrincipals(installment1, 242.24, 0, 242.24);
        assertInstallmentPrincipals(installment2, 247.67, 0, 247.67);
        assertInstallmentPrincipals(installment3, 252.22, 215.82, 36.4);
        assertInstallmentPrincipals(installment4, 257.87, 257.87, 0);

        assertInstallmentInterests(installment1, 20.40, 0, 20.40);
        assertInstallmentInterests(installment2, 7.79, 7.79, 2.49);
        assertInstallmentInterests(installment3, 9.66, 9.66, 0);
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
        scheduleCalculator.applyPayment(schedule, BigDecimal.valueOf(240d), getDate(23, 9, 2010), false);

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
    public void shortPaymentBeforeDueDateWithFeesAndPenalty() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0, 1, 2, 3, 4);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        Installment installment3 = getInstallment(3, getDate(25, 11, 2010), 252.22, 10.40, 0);
        Installment installment4 = getInstallment(4, getDate(25, 12, 2010), 257.87, 5.09, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2, installment3, installment4));
        scheduleCalculator.applyPayment(schedule, BigDecimal.valueOf(240d), getDate(23, 9, 2010), false);

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
        scheduleCalculator.applyPayment(schedule, BigDecimal.valueOf(240d), getDate(29, 9, 2010), false);

        assertInstallmentPrincipals(installment1, 242.24, 22.64, 219.6);
        assertInstallmentPrincipals(installment2, 247.67, 247.67, 0);
        assertInstallmentPrincipals(installment3, 252.22, 252.22, 0);
        assertInstallmentPrincipals(installment4, 257.87, 257.87, 0);

        assertInstallmentInterests(installment1, 20.40, 0, 20.40);
        assertInstallmentInterests(installment2, 14.96, 14.96, 0);
        assertInstallmentInterests(installment3, 10.40, 10.40, 0);
        assertInstallmentInterests(installment4, 5.09, 5.09, 0);

        assertInstallmentForExtraInterests(installment1, 0, 0, 0);
        assertInstallmentForExtraInterests(installment2, 0.64, 0.64, 0);
        assertInstallmentForExtraInterests(installment3, 0, 0, 0);
        assertInstallmentForExtraInterests(installment4, 0, 0, 0);
    }

    @Test
    public void withExcessPaymentAfterDueDate() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        Installment installment3 = getInstallment(3, getDate(25, 11, 2010), 252.22, 10.40, 0);
        Installment installment4 = getInstallment(4, getDate(25, 12, 2010), 257.87, 5.09, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2, installment3, installment4));
        scheduleCalculator.applyPayment(schedule, BigDecimal.valueOf(280d), getDate(29, 9, 2010), false);

        assertInstallmentPrincipals(installment1, 242.24, 0, 242.24);
        assertInstallmentPrincipals(installment2, 247.67, 232.94, 14.73);
        assertInstallmentPrincipals(installment3, 252.22, 252.22, 0);
        assertInstallmentPrincipals(installment4, 257.87, 257.87, 0);

        assertInstallmentInterests(installment1, 20.40, 0, 20.40);
        assertInstallmentInterests(installment2, 12.71, 12.71, 1.99);
        assertInstallmentInterests(installment3, 10.40, 10.40, 0);
        assertInstallmentInterests(installment4, 5.09, 5.09, 0);

        assertInstallmentForExtraInterests(installment1, 0, 0, 0);
        assertInstallmentForExtraInterests(installment2, 0.64, 0, 0.64);
        assertInstallmentForExtraInterests(installment3, 0, 0, 0);
        assertInstallmentForExtraInterests(installment4, 0, 0, 0);
    }

    @Test
    public void withExactPaymentAfterDueDate() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        Installment installment3 = getInstallment(3, getDate(25, 11, 2010), 252.22, 10.40, 0);
        Installment installment4 = getInstallment(4, getDate(25, 12, 2010), 257.87, 5.09, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2, installment3, installment4));
        scheduleCalculator.applyPayment(schedule, BigDecimal.valueOf(262.64d), getDate(29, 9, 2010), false);

        assertInstallmentPrincipals(installment1, 242.24, 0, 242.24);
        assertInstallmentPrincipals(installment2, 247.67, 247.67, 0);
        assertInstallmentPrincipals(installment3, 252.22, 252.22, 0);
        assertInstallmentPrincipals(installment4, 257.87, 257.87, 0);

        assertInstallmentInterests(installment1, 20.40, 0, 20.40);
        assertInstallmentInterests(installment2, 14.96, 14.96, 0);
        assertInstallmentInterests(installment3, 10.40, 10.40, 0);
        assertInstallmentInterests(installment4, 5.09, 5.09, 0);

        assertInstallmentForExtraInterests(installment1, 0, 0, 0);
        assertInstallmentForExtraInterests(installment2, 0.64, 0.64, 0);
        assertInstallmentForExtraInterests(installment3, 0, 0, 0);
        assertInstallmentForExtraInterests(installment4, 0, 0, 0);
    }

    @Test
    public void withNoPaymentPaymentAfterDueDate() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        Installment installment3 = getInstallment(3, getDate(25, 11, 2010), 252.22, 10.40, 0);
        Installment installment4 = getInstallment(4, getDate(25, 12, 2010), 257.87, 5.09, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2, installment3, installment4));
        scheduleCalculator.computeExtraInterest(schedule, getDate(26, 12, 2010));

        assertInstallmentPrincipals(installment1, 242.24, 242.24, 0);
        assertInstallmentPrincipals(installment2, 247.67, 247.67, 0);
        assertInstallmentPrincipals(installment3, 252.22, 252.22, 0);
        assertInstallmentPrincipals(installment4, 257.87, 257.87, 0);

        assertInstallmentInterests(installment1, 20.40, 20.40, 0);
        assertInstallmentInterests(installment2, 14.96, 14.96, 0);
        assertInstallmentInterests(installment3, 10.40, 10.40, 0);
        assertInstallmentInterests(installment4, 5.09, 5.09, 0);

        assertInstallmentForExtraInterests(installment1, 0, 0, 0);
        assertInstallmentForExtraInterests(installment2, 14.66, 14.66, 0);
        assertInstallmentForExtraInterests(installment3, 10.1, 10.1, 0);
        assertInstallmentForExtraInterests(installment4, 5.31, 5.31, 0);
    }


    @Test
    public void withExcessPaymentBeforeDueDate() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        Installment installment3 = getInstallment(3, getDate(25, 11, 2010), 252.22, 10.40, 0);
        Installment installment4 = getInstallment(4, getDate(25, 12, 2010), 257.87, 5.09, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2, installment3, installment4));
        scheduleCalculator.applyPayment(schedule, BigDecimal.valueOf(280d), getDate(23, 9, 2010), false);

        assertInstallmentPrincipals(installment1, 242.24, 0, 242.24);
        assertInstallmentPrincipals(installment2, 247.67, 228.99, 18.68);
        assertInstallmentPrincipals(installment3, 252.22, 252.22, 0);
        assertInstallmentPrincipals(installment4, 257.87, 257.87, 0);

        assertInstallmentInterests(installment1, 0.97, 0.97, 19.08);
        assertInstallmentInterests(installment2, 14.59, 14.59, 0);
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
        scheduleCalculator.applyPayment(schedule, BigDecimal.valueOf(261.32d), getDate(23, 9, 2010), false);

        assertInstallmentPrincipals(installment1, 242.24, 0, 242.24);
        assertInstallmentPrincipals(installment2, 247.67, 247.67, 0);
        assertInstallmentPrincipals(installment3, 252.22, 252.22, 0);
        assertInstallmentPrincipals(installment4, 257.87, 257.87, 0);

        assertInstallmentInterests(installment1, 1, 1, 19.08);
        assertInstallmentInterests(installment2, 14.96, 14.96, 0);
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
        scheduleCalculator.applyPayment(schedule, BigDecimal.valueOf(262.64d), getDate(23, 9, 2010), false);

        assertInstallmentPrincipals(installment1, 242.24, 0, 242.24);
        assertInstallmentPrincipals(installment2, 247.67, 246.35, 1.32);
        assertInstallmentPrincipals(installment3, 252.22, 252.22, 0);
        assertInstallmentPrincipals(installment4, 257.87, 257.87, 0);

        assertInstallmentInterests(installment1, 1, 1, 19.08);
        assertInstallmentInterests(installment2, 14.93, 14.93, 0);
        assertInstallmentInterests(installment3, 10.40, 10.40, 0);
        assertInstallmentInterests(installment4, 5.09, 5.09, 0);
    }

    @Test
    public void multiplePaymentsOnDisbursementDate() {
        Installment installment1 = getInstallment(1, getDate(18, 10, 2010), 332.2, 3.8, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 333.4, 2.6, 0);
        Installment installment3 = getInstallment(3, getDate(1, 11, 2010), 334.4, 1.3, 0);
        schedule = new Schedule(getDate(11, 10, 2010), 0.000548, BigDecimal.valueOf(1000d),
                asList(installment1, installment2, installment3));
        scheduleCalculator.applyPayment(schedule, BigDecimal.valueOf(337d), getDate(11, 10, 2010), false);

        assertInstallmentPrincipals(installment1, 332.2, 0, 332.2);
        assertInstallmentPrincipals(installment2, 333.4, 328.6, 4.8);
        assertInstallmentPrincipals(installment3, 334.4, 334.4, 0);

        assertInstallmentInterests(installment1, 2.54, 2.54, 0);
        assertInstallmentInterests(installment2, 2.54, 2.54, 0);
        assertInstallmentInterests(installment3, 1.3, 1.3, 0);

        scheduleCalculator.applyPayment(schedule, BigDecimal.valueOf(10d), getDate(11, 10, 2010), false);
        assertInstallmentPrincipals(installment1, 332.2, 0, 332.2);
        assertInstallmentPrincipals(installment2, 333.4, 318.6, 14.8);
        assertInstallmentPrincipals(installment3, 334.4, 334.4, 0);

        assertInstallmentInterests(installment1, 2.54, 2.54, 0);
        assertInstallmentInterests(installment2, 2.5, 2.5, 0);
        assertInstallmentInterests(installment3, 1.3, 1.3, 0);
    }

    @Test
    public void shouldComputeExtraInterestBeforeDisbursement() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2));
        scheduleCalculator.computeExtraInterest(schedule, getDate(20, 8, 2010));
        assertThat(installment1.getExtraInterest().doubleValue(), is(0.0));
        assertThat(installment2.getExtraInterest().doubleValue(), is(0.0));
    }

    @Test
    public void shouldComputeExtraInterestOnDisbursement() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2));
        scheduleCalculator.computeExtraInterest(schedule, getDate(25, 8, 2010));
        assertThat(installment1.getExtraInterest().doubleValue(), is(0.0));
        assertThat(installment2.getExtraInterest().doubleValue(), is(0.0));
    }

    @Test
    public void shouldComputeExtraInterestOnDueDate() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2));
        scheduleCalculator.computeExtraInterest(schedule, getDate(25, 9, 2010));
        assertThat(installment1.getExtraInterest().doubleValue(), is(0.0));
        assertThat(installment2.getExtraInterest().doubleValue(), is(0.0));
    }

    @Test
    public void shouldComputeOneDayExtraInterest() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2));
        scheduleCalculator.computeExtraInterest(schedule, getDate(26, 9, 2010));
        assertThat(installment1.getExtraInterest().doubleValue(), is(0.0));
        assertThat(installment2.getExtraInterest().doubleValue(), is(0.16));
    }

    @Test
    public void shouldComputeTwoDayExtraInterest() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2));
        scheduleCalculator.computeExtraInterest(schedule, getDate(27, 9, 2010));
        assertThat(installment1.getExtraInterest().doubleValue(), is(0.0));
        assertThat(installment2.getExtraInterest().doubleValue(), is(0.32));
    }

    @Test
    public void shouldComputeExtraInterestAfterPartPay() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2));
        scheduleCalculator.computeExtraInterest(schedule, getDate(27, 9, 2010));
        assertThat(installment1.getExtraInterest().doubleValue(), is(0.0));
        assertThat(installment2.getExtraInterest().doubleValue(), is(0.32));
        installment1.pay(BigDecimal.valueOf(100), getDate(27, 9, 2010));
        scheduleCalculator.computeExtraInterest(schedule, getDate(28, 9, 2010));
        assertThat(installment1.getExtraInterest().doubleValue(), is(0.0));
        assertThat(installment2.getExtraInterest().doubleValue(), is(0.43));
    }

    @Test
    public void shouldComputeExtraInterestAfterMultiplePaymentsOnSameDay() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2));

        scheduleCalculator.applyPayment(schedule, new BigDecimal(100), getDate(27, 9, 2010), false);
        assertThat(installment2.getExtraInterestPaid().doubleValue(), is(0.0));
        assertThat(installment1.getExtraInterest().doubleValue(), is(0.0));
        assertThat(installment1.getInterest().doubleValue(), is(20.40));
        assertThat(installment1.getApplicableInterest().doubleValue(), is(20.40));
        assertThat(installment2.getExtraInterest().doubleValue(), is(0.32));

        scheduleCalculator.applyPayment(schedule, new BigDecimal(200), getDate(27, 9, 2010), false);
        assertThat(installment2.getExtraInterestPaid().doubleValue(), is(0.32));
        assertThat(installment2.getExtraInterest().doubleValue(), is(0.32));
        assertThat(installment2.getInterestPaid().doubleValue(), is(1.00));
        assertThat(installment2.getPrincipalDue().doubleValue(), is(211.63));
        assertThat(installment2.getInterest().doubleValue(), is(14.96));
        assertThat(installment2.getApplicableInterest().doubleValue(), is(13.30));
    }

    @Test
    public void shouldComputeExtraFullPayment() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2));
        scheduleCalculator.applyPayment(schedule, new BigDecimal(262.64), getDate(25, 9, 2010), false);
        scheduleCalculator.applyPayment(schedule, new BigDecimal(262.63), getDate(25, 10, 2010), false);

        scheduleCalculator.computeExtraInterest(schedule, getDate(30, 12, 2010));
        assertThat(installment1.getExtraInterest().doubleValue(), is(0.0));
        assertThat(installment2.getExtraInterest().doubleValue(), is(0.0));
    }

    @Test
    public void overdueOnLastInstallmentIsRecoveredInLastInstallment() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2));
        scheduleCalculator.computeExtraInterest(schedule, getDate(20, 11, 2010));
        assertThat(installment1.getExtraInterest().doubleValue(), is(0.0));
        assertThat(installment2.getExtraInterest().doubleValue(), is(13.17));
    }

    @Test
    public void shouldComputeRepaymentAmountWhenNoPastPaymentsMade() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        Installment installment3 = getInstallment(3, getDate(25, 11, 2010), 252.22, 10.40, 0);
        Installment installment4 = getInstallment(4, getDate(25, 12, 2010), 257.87, 5.09, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2, installment3, installment4));
        RepaymentResultsHolder repaymentResultsHolder = scheduleCalculator.computeRepaymentAmount(schedule, getDate(30, 11, 2010));
        assertThat(repaymentResultsHolder.getTotalRepaymentAmount().doubleValue(),is(1063.83));
        assertThat(repaymentResultsHolder.getWaiverAmount().doubleValue(),is(0.85));
    }

    @Test
    public void computeRepaymentAmountShouldConsiderFeesAndPenaltyOfCurrentInstallmentAndDues() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0, 1, 2, 3, 4);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0, 1, 2, 3, 4);
        Installment installment3 = getInstallment(3, getDate(25, 11, 2010), 252.22, 10.40, 0, 1, 2, 3, 4);
        Installment installment4 = getInstallment(4, getDate(25, 12, 2010), 257.87, 5.09, 0, 1, 2, 3, 4);
        Installment installment5 = getInstallment(5, getDate(25, 1, 2011), 257.87, 5.09, 0, 1, 2, 3, 4);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2, installment3, installment4, installment5));
        RepaymentResultsHolder repaymentResultsHolder = scheduleCalculator.computeRepaymentAmount(schedule, getDate(30, 11, 2010));
        assertThat(repaymentResultsHolder.getTotalRepaymentAmount().doubleValue(), is(1361.7));
        assertThat(repaymentResultsHolder.getWaiverAmount().doubleValue(), is(0.85));
    }

    @Test
    public void shouldComputeRepaymentAmountWhenLateExcessPaymentMade() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        Installment installment3 = getInstallment(3, getDate(25, 11, 2010), 252.22, 10.40, 0);
        Installment installment4 = getInstallment(4, getDate(25, 12, 2010), 257.87, 5.09, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2, installment3, installment4));
        scheduleCalculator.applyPayment(schedule, BigDecimal.valueOf(500d), getDate(30, 10, 2010), false);
        RepaymentResultsHolder repaymentResultsHolder = scheduleCalculator.computeRepaymentAmount(schedule, getDate(30, 11, 2010));
        assertThat(repaymentResultsHolder.getTotalRepaymentAmount().doubleValue(),is(554.46));
        assertThat(repaymentResultsHolder.getWaiverAmount().doubleValue(),is(0.85));
    }

    @Test
    public void shouldComputeRepaymentAmountWhenOnDateExcessPaymentMade() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        Installment installment3 = getInstallment(3, getDate(25, 11, 2010), 252.22, 10.40, 0);
        Installment installment4 = getInstallment(4, getDate(25, 12, 2010), 257.87, 5.09, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2, installment3, installment4));
        scheduleCalculator.applyPayment(schedule, BigDecimal.valueOf(500d), getDate(30, 10, 2010), false);
        RepaymentResultsHolder repaymentResultsHolder = scheduleCalculator.computeRepaymentAmount(schedule, getDate(25, 12, 2010));
        assertThat(repaymentResultsHolder.getTotalRepaymentAmount().doubleValue(),is(563.36));
        assertThat(repaymentResultsHolder.getWaiverAmount().doubleValue(),is(5.09));
    }

    @Test
    public void shouldComputeRepaymentAmountForOnTimePaymentsAndRepaymentDoneOnInstallmentDueDate() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        Installment installment3 = getInstallment(3, getDate(25, 11, 2010), 252.22, 10.40, 0);
        Installment installment4 = getInstallment(4, getDate(25, 12, 2010), 257.87, 5.09, 0);
        schedule = new Schedule(getDate(25, 8, 2010), 0.000658, BigDecimal.valueOf(1000d),
                asList(installment1, installment2, installment3, installment4));
        scheduleCalculator.applyPayment(schedule, BigDecimal.valueOf(262.64), getDate(25, 9, 2010), false);
        scheduleCalculator.applyPayment(schedule, BigDecimal.valueOf(262.63), getDate(25, 10, 2010), false);
        BigDecimal repaymentAmount = scheduleCalculator.computeRepaymentAmount(schedule, getDate(25, 11, 2010)).getTotalRepaymentAmount();
        assertThat(repaymentAmount.doubleValue(),is(520.49));
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

    private void assertInstallmentForExtraInterests(Installment installment, double ExtraInterest, double ExtraInterestDue, double ExtraInterestPaid) {
        assertThat(installment.getExtraInterest().doubleValue(), is(ExtraInterest));
        assertThat(installment.getExtraInterestDue().doubleValue(), is(ExtraInterestDue));
        assertThat(installment.getExtraInterestPaid().doubleValue(), is(ExtraInterestPaid));
    }

    private Installment getInstallment(int id, Date dueDate, double principal, double interest, double extraInterest) {
        Installment installment = new Installment(id, dueDate, BigDecimal.valueOf(principal), BigDecimal.valueOf(interest), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        installment.setExtraInterest(BigDecimal.valueOf(extraInterest));
        return installment;
    }

    private Installment getInstallment(int id, Date dueDate, double principal, double interest, double extraInterest, double fees, double miscFees, double penalty, double miscPenalty) {
        return new Installment(id, dueDate, BigDecimal.valueOf(principal), BigDecimal.valueOf(interest), BigDecimal.valueOf(extraInterest), BigDecimal.valueOf(fees), BigDecimal.valueOf(miscFees), BigDecimal.valueOf(penalty), BigDecimal.valueOf(miscPenalty));
    }
}
