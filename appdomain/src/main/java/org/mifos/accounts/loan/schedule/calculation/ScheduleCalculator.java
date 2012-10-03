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

import java.math.BigDecimal;
import java.util.Date;

import org.mifos.accounts.loan.business.RepaymentResultsHolder;
import org.mifos.accounts.loan.schedule.domain.Schedule;
import org.mifos.config.AccountingRules;

public class ScheduleCalculator {
    public void applyPayment(Schedule schedule, BigDecimal amount, Date transactionDate) {
        schedule.resetCurrentPayment();
        computeExtraInterest(schedule, transactionDate);
        if(AccountingRules.isOverdueInterestPaidFirst()){
            amount = schedule.payOverDueInstallments(transactionDate, amount);
        }
        BigDecimal balance = schedule.payDueInstallments(transactionDate, amount);
        schedule.adjustFutureInstallments(balance, transactionDate);
    }

    public void computeExtraInterest(Schedule schedule, Date transactionDate) {
        schedule.computeExtraInterest(transactionDate);
    }
    
    public BigDecimal getExtraInterest(Schedule schedule, Date transactionDate) {
        return schedule.getExtraInterest(transactionDate);
    }

    public RepaymentResultsHolder computeRepaymentAmount(Schedule schedule, Date asOfDate) {
        computeExtraInterest(schedule, asOfDate);
        BigDecimal payableAmountForDueInstallments = schedule.computePayableAmountForDueInstallments(asOfDate);
        RepaymentResultsHolder repaymentResultsHolder = schedule.computePayableAmountForFutureInstallments(asOfDate);
        BigDecimal totalRepaymentAmount = payableAmountForDueInstallments.add(repaymentResultsHolder.getTotalRepaymentAmount());
        repaymentResultsHolder.setTotalRepaymentAmount(totalRepaymentAmount);
        return repaymentResultsHolder;
    }
}
