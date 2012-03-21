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
package org.mifos.dto.screen;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID"}, justification="should disable at filter level and also for pmd - not important for us")
public class ClientPerformanceHistoryDto implements Serializable {

    private final Integer loanCycleNumber;
    private final String lastLoanAmount;
    private final Integer noOfActiveLoans;
    private final String delinquentPortfolioAmount;
    private final String totalSavingsAmount;
    private final Integer meetingsAttended;
    private final Integer meetingsMissed;
    private final List<LoanCycleCounter> loanCycleCounters;

    public ClientPerformanceHistoryDto(final Integer loanCycleNumber, final String lastLoanAmount,
            final Integer noOfActiveLoans, final String delinquentPortfolioAmount, final String totalSavingsAmount,
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

    public String getLastLoanAmount() {
        return this.lastLoanAmount;
    }

    public Integer getNoOfActiveLoans() {
        return this.noOfActiveLoans;
    }

    public String getDelinquentPortfolioAmount() {
        return this.delinquentPortfolioAmount;
    }

    public boolean isDelinquentPortfolioAmountInvalid() {
        boolean invalidAmount = false;
        try {
            Double.parseDouble(this.delinquentPortfolioAmount);
        } catch (NumberFormatException e) {
            invalidAmount = true;
        }
        return invalidAmount;
    }

    public String getTotalSavingsAmount() {
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
