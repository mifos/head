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
package org.mifos.customers.util.helpers;

import java.util.List;

import org.mifos.framework.business.service.DataTransferObject;

/**
 *
 */
public class GroupPerformanceHistoryDto implements DataTransferObject {
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

    public String getTotalOutStandingLoanAmount() {
        return this.totalOutStandingLoanAmount;
    }

    public String getPortfolioAtRisk() {
        return this.portfolioAtRisk;
    }

    public String getTotalSavingsAmount() {
        return this.totalSavingsAmount;
    }

    public List<LoanCycleCounter> getLoanCycleCounters() {
        return this.loanCycleCounters;
    }


}
