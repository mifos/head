/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.service.BusinessRuleException;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LoanDurationInAccountingYearsCalculatorFactoryTest {

    private LoanDurationInAccountingYearsCalculatorFactory loanDurationCalculatorFactory;

    @Before
    public void setup() {
        loanDurationCalculatorFactory = new LoanDurationInAccountingYearsCalculatorFactory();
    }

    @Test
    public void shouldUseWeeklyCalculator() {

        // exercise test
        LoanDurationInAccountingYearsCalculator loanDurationCalculator = loanDurationCalculatorFactory.create(RecurrenceType.WEEKLY);

        // verification
        assertThat(loanDurationCalculator, is(instanceOf(LoanDurationInAccountingYearsCalculatorFactoryForWeeklyRecurrence.class)));
    }

    @Test
    public void shouldUseMonthlyCalculator() {

        // exercise test
        LoanDurationInAccountingYearsCalculator loanDurationCalculator = loanDurationCalculatorFactory.create(RecurrenceType.MONTHLY);

        // verification
        assertThat(loanDurationCalculator, is(instanceOf(LoanDurationInAccountingYearsCalculatorFactoryForMonthlyRecurrence.class)));
    }
    
    @Test
    public void shouldUseDailyCalculator() {

        // exercise test
        LoanDurationInAccountingYearsCalculator loanDurationCalculator = loanDurationCalculatorFactory.create(RecurrenceType.DAILY);

        // verification
        assertThat(loanDurationCalculator, is(instanceOf(LoanDurationInAccountingYearsCalculatorFactoryForDailyRecurrence.class)));
    }
}