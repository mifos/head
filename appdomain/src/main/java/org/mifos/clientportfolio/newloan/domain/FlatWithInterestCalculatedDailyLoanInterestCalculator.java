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

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.framework.util.helpers.Money;

/**
 * Implementation of {@link LoanInterestCalculator} for a variable installment loan product.
 * 
 * Interest is calculated based on a daily frequency (even if product is weekly or monthly).
 * 
 * see http://mifosforge.jira.com/wiki/display/MIFOS/Variable+Loan+Installments+and+Cash+Flow+Comparison+Functional+Spec
 */
public class FlatWithInterestCalculatedDailyLoanInterestCalculator implements LoanInterestCalculator {

    private InterestCalculationForumula formula = new DecliningBalanceWithInterestCalculatedDailyFormula();
    
    @Override
    public Money calculate(LoanInterestCalculationDetails loanInterestCalculationDetails) {

        Double interestRate = loanInterestCalculationDetails.getInterestRate();
        Money loanAmount = loanInterestCalculationDetails.getLoanAmount();
        Money totalInterestOwed = new Money(loanAmount.getCurrency(), BigDecimal.ZERO);

        LocalDate interestPeriodStartDate = loanInterestCalculationDetails.getDisbursementDate();
        for (DateTime installmentDueDate : loanInterestCalculationDetails.getLoanScheduleDates()) {
            Money interestForInstallment = formula.calculate(loanAmount, interestRate, interestPeriodStartDate, new LocalDate(installmentDueDate));
            totalInterestOwed = totalInterestOwed.add(interestForInstallment);
            interestPeriodStartDate = new LocalDate(installmentDueDate);
        }

        return totalInterestOwed;
    }
}