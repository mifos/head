/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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
package org.mifos.customers.util.helpers;

import java.util.List;

import org.mifos.framework.business.service.DataTransferObject;
import org.mifos.framework.util.helpers.Money;

/**
 *
 */
public class ClientPerformanceHistoryDto implements DataTransferObject {

    private final Integer loanCycleNumber;
    private final Money lastLoanAmount;
    private final Integer noOfActiveLoans;
    private final String delinquentPortfolioAmount;
    private final Money totalSavingsAmount;
    private final Integer meetingsAttended;
    private final Integer meetingsMissed;
    private final List<LoanCycleCounter> loanCycleCounters;

    public ClientPerformanceHistoryDto(final Integer loanCycleNumber, final Money lastLoanAmount,
            final Integer noOfActiveLoans, final String delinquentPortfolioAmount, final Money totalSavingsAmount,
            final Integer meetingsAttended, final Integer meetingsMissed, final List<LoanCycleCounter> loanCycleCounters) {

        this.loanCycleNumber = loanCycleNumber;
        this.lastLoanAmount = lastLoanAmount;
        this.noOfActiveLoans = noOfActiveLoans;
        this.delinquentPortfolioAmount = delinquentPortfolioAmount;
        this.totalSavingsAmount = totalSavingsAmount;
        this.meetingsAttended = meetingsAttended;
        this.meetingsMissed = meetingsMissed;
        this.loanCycleCounters = loanCycleCounters;
    }

    public Integer getLoanCycleNumber() {
        return this.loanCycleNumber;
    }

    public Money getLastLoanAmount() {
        return this.lastLoanAmount;
    }

    public Integer getNoOfActiveLoans() {
        return this.noOfActiveLoans;
    }

    public String getDelinquentPortfolioAmount() {
        return this.delinquentPortfolioAmount;
    }

    public Money getTotalSavingsAmount() {
        return this.totalSavingsAmount;
    }

    public Integer getMeetingsAttended() {
        return this.meetingsAttended;
    }

    public Integer getMeetingsMissed() {
        return this.meetingsMissed;
    }

    public List<LoanCycleCounter> getLoanCycleCounters() {
        return this.loanCycleCounters;
    }

}
