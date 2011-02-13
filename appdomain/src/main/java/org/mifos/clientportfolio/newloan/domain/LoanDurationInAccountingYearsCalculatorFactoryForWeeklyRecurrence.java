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

import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.service.BusinessRuleException;

public class LoanDurationInAccountingYearsCalculatorFactoryForWeeklyRecurrence implements
        LoanDurationInAccountingYearsCalculator {

    @Override
    public Double calculate(Integer recurringEvery, Integer numberOfInstallments, Integer interestDays) {

        if (interestDays != AccountConstants.INTEREST_DAYS_360 && interestDays != AccountConstants.INTEREST_DAYS_365) {
            throw new BusinessRuleException(AccountConstants.NOT_SUPPORTED_INTEREST_DAYS);
        }

        int daysInWeek = 7;
        int duration = numberOfInstallments * recurringEvery;

        double totalWeekDays = duration * daysInWeek;
        return totalWeekDays / interestDays;
    }

}
