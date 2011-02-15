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

import java.util.Date;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification="")
public class CustomerHistoricalDataUpdateRequest {

    private final Date mfiJoiningDate;
    private final String interestPaid;
    private final String loanAmount;
    private final Integer loanCycleNumber;
    private final Integer missedPaymentsCount;
    private final String notes;
    private final String productName;
    private final String totalAmountPaid;
    private final Integer totalPaymentsCount;

    public CustomerHistoricalDataUpdateRequest(Date mfiJoiningDate, String interestPaid, String loanAmount,
            Integer loanCycleNumber, Integer missedPaymentsCount, String notes, String productName,
            String totalAmountPaid, Integer totalPaymentsCount) {
        this.mfiJoiningDate = mfiJoiningDate;
        this.interestPaid = interestPaid;
        this.loanAmount = loanAmount;
        this.loanCycleNumber = loanCycleNumber;
        this.missedPaymentsCount = missedPaymentsCount;
        this.notes = notes;
        this.productName = productName;
        this.totalAmountPaid = totalAmountPaid;
        this.totalPaymentsCount = totalPaymentsCount;
    }

    public Date getMfiJoiningDate() {
        return this.mfiJoiningDate;
    }

    public String getInterestPaid() {
        return this.interestPaid;
    }

    public String getLoanAmount() {
        return this.loanAmount;
    }

    public Integer getLoanCycleNumber() {
        return this.loanCycleNumber;
    }

    public Integer getMissedPaymentsCount() {
        return this.missedPaymentsCount;
    }

    public String getNotes() {
        return this.notes;
    }

    public String getProductName() {
        return this.productName;
    }

    public String getTotalAmountPaid() {
        return this.totalAmountPaid;
    }

    public Integer getTotalPaymentsCount() {
        return this.totalPaymentsCount;
    }
}