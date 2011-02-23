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

import org.mifos.accounts.util.helpers.AccountConstants;

public class LoanDurationInAccountingYearsCalculatorFactoryForMonthlyRecurrence implements
        LoanDurationInAccountingYearsCalculator {

    @Override
    public Double calculate(Integer recurringEvery, Integer numberOfInstallments, Integer interestDays) {

        int daysInMonth = 30;
        int duration = numberOfInstallments * recurringEvery;

        double totalMonthDays = duration * daysInMonth;

        // TODO - keithw - ask why duration of loan is worked out for 360 financial year for monthly loans.
        //      - is it related to assumption that month has exactly 30days so 12 months = 360 days
        return totalMonthDays / AccountConstants.INTEREST_DAYS_360;
    }

}
