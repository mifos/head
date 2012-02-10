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

package org.mifos.dto.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.mifos.dto.screen.SavingsRecentActivityDto;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification="should disable at filter level and also for pmd - not important for us")
public class SavingsAccountDetailDto implements Serializable {

    private final SavingsProductDto productDetails;
    private final List<SavingsRecentActivityDto> recentActivity;
    private final List<CustomerNoteDto> recentNoteDtos;
    private final String recommendedOrMandatoryAmount;
    private final String globalAccountNum;
    private final Integer accountId;
    private final Short accountStateId;
    private final String accountStateName;
    private final String accountBalance;
    private final Date dueDate;
    private final String totalAmountDue;
    private final SavingsPerformanceHistoryDto performanceHistory;
    private final String depositTypeName;

    public SavingsAccountDetailDto(SavingsProductDto productDetails, List<SavingsRecentActivityDto> recentActivity,
            List<CustomerNoteDto> recentNoteDtos, String recommendedOrMandatoryAmount, String globalAccountNum,
            Integer accountId, Short accountStateId, String accountStateName, String accountBalance, Date dueDate,
            String totalAmountDue, SavingsPerformanceHistoryDto performanceHistory, String depositTypeName) {
        super();
        this.productDetails = productDetails;
        this.recentActivity = recentActivity;
        this.recentNoteDtos = recentNoteDtos;
        this.recommendedOrMandatoryAmount = recommendedOrMandatoryAmount;
        this.globalAccountNum = globalAccountNum;
        this.accountId = accountId;
        this.accountStateId = accountStateId;
        this.accountStateName = accountStateName;
        this.accountBalance = accountBalance;
        this.dueDate = dueDate;
        this.totalAmountDue = totalAmountDue;
        this.performanceHistory = performanceHistory;
        this.depositTypeName = depositTypeName;
    }

    public SavingsProductDto getProductDetails() {
        return this.productDetails;
    }

    public List<SavingsRecentActivityDto> getRecentActivity() {
        return this.recentActivity;
    }

    public List<CustomerNoteDto> getRecentNoteDtos() {
        return this.recentNoteDtos;
    }

    public String getRecommendedOrMandatoryAmount() {
        return this.recommendedOrMandatoryAmount;
    }

    public String getGlobalAccountNum() {
        return this.globalAccountNum;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public String getAccountStateName() {
        return accountStateName;
    }
    
    public Short getAccountStateId() {
        return accountStateId;
    }

    public String getAccountBalance() {
        return accountBalance;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public String getTotalAmountDue() {
        return totalAmountDue;
    }

    public SavingsPerformanceHistoryDto getPerformanceHistory() {
        return performanceHistory;
    }

    public String getDepositTypeName() {
        return depositTypeName;
    }

}