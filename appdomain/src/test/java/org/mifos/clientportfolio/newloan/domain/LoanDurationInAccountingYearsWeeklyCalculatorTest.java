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

package org.mifos.clientportfolio.newloan.domain;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.math.MathContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.service.BusinessRuleException;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LoanDurationInAccountingYearsWeeklyCalculatorTest {

    private LoanDurationInAccountingYearsCalculatorFactoryForWeeklyRecurrence weeklyCalculator;

    @Before
    public void setup() {
        weeklyCalculator = new LoanDurationInAccountingYearsCalculatorFactoryForWeeklyRecurrence();
    }

    @Test(expected=BusinessRuleException.class)
    public void shouldThrowRuntimeException() {

        Integer recurringEvery = Integer.valueOf(1);
        Integer numberOfInstallments = Integer.valueOf(12);
        Integer interestDays = Integer.valueOf(1);

        // exercise test
        weeklyCalculator.calculate(recurringEvery, numberOfInstallments, interestDays);
    }

    @Test
    public void shouldCalculateDurationInYears() {

        Integer recurringEvery = Integer.valueOf(1);
        Integer numberOfInstallments = Integer.valueOf(12);
        Integer interestDays = Integer.valueOf(360);

        // exercise test
        Double durationInYears = weeklyCalculator.calculate(recurringEvery, numberOfInstallments, interestDays);

        // verification
        assertThat(doubleOf(durationInYears), is(doubleOf(Double.valueOf("0.2333"))));
    }

    private Double doubleOf(Double durationInYears) {
        MathContext mc = new MathContext(4);
        return BigDecimal.valueOf(durationInYears).round(mc).doubleValue();
    }
}