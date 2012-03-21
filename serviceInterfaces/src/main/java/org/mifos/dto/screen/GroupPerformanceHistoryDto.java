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
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification="should disable at filter level and also for pmd - not important for us")
public class GroupPerformanceHistoryDto implements Serializable {
    private final String activeClientCount;
    private final String lastGroupLoanAmount;
    private final String avgLoanAmountForMember;
    private final String totalOutStandingLoanAmount;
    private final String portfolioAtRisk;
    private final String totalSavingsAmount;
    private final List<LoanCycleCounter> loanCycleCounters;

    public GroupPerformanceHistoryDto(final String activeClientCount, final String lastGroupLoanAmount, final String avgLoanAmountForMember,
            final String totalOutStandingLoanAmount, final String portfolioAtRisk, final String totalSavingsAmount, final List<LoanCycleCounter> loanCycleCounters) {
        this.activeClientCount = activeClientCount;
        this.lastGroupLoanAmount = lastGroupLoanAmount;
        this.avgLoanAmountForMember = avgLoanAmountForMember;
        this.totalOutStandingLoanAmount = totalOutStandingLoanAmount;
        this.portfolioAtRisk = portfolioAtRisk;
        this.totalSavingsAmount = totalSavingsAmount;
        this.loanCycleCounters = loanCycleCounters;
    }

    public String getActiveClientCount() {
        return this.activeClientCount;
    }

    public String getLastGroupLoanAmount() {
        return this.lastGroupLoanAmount;
    }

    public String getAvgLoanAmountForMember() {
        return this.avgLoanAmountForMember;
    }

    public boolean isAvgLoanAmountForMemberInvalid() {
        boolean invalidAmount = false;
        try {
            Double.parseDouble(this.avgLoanAmountForMember);
        } catch (NumberFormatException e) {
            invalidAmount = true;
        }
        return invalidAmount;
    }

    public String getTotalOutStandingLoanAmount() {
        return this.totalOutStandingLoanAmount;
    }

    public boolean isTotalOutStandingLoanAmountInvalid() {
        boolean invalidAmount = false;
        try {
            Double.parseDouble(this.totalOutStandingLoanAmount);
        } catch (NumberFormatException e) {
            invalidAmount = true;
        }
        return invalidAmount;
    }

    public String getPortfolioAtRisk() {
        return this.portfolioAtRisk;
    }

    public boolean isPortfolioAtRiskInvalid() {
        boolean invalidAmount = false;
        try {
            Double.parseDouble(this.portfolioAtRisk);
        } catch (NumberFormatException e) {
            invalidAmount = true;
        }
        return invalidAmount;
    }

    public String getTotalSavingsAmount() {
        return this.totalSavingsAmount;
    }

    public boolean isTotalSavingsAmountInvalid() {
        boolean invalidAmount = false;
        try {
            Double.parseDouble(this.totalSavingsAmount);
        } catch (NumberFormatException e) {
            invalidAmount = true;
        }
        return invalidAmount;
    }

    public List<LoanCycleCounter> getLoanCycleCounters() {
        return this.loanCycleCounters;
    }


}
