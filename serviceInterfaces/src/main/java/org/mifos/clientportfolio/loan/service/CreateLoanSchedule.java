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

package org.mifos.clientportfolio.loan.service;

import org.joda.time.LocalDate;

public class CreateLoanSchedule {

    private final Integer customerId;
    private final Integer productId;
    private final String loanAmount;
    private final Double interestRate;
    private final LocalDate disbursementDate;
    private final int numberOfInstallments;

    public CreateLoanSchedule(Integer customerId, Integer productId, String loanAmount, Double interestRate,
            LocalDate disbursementDate, int numberOfInstallments) {
        this.customerId = customerId;
        this.productId = productId;
        this.loanAmount = loanAmount;
        this.interestRate = interestRate;
        this.disbursementDate = disbursementDate;
        this.numberOfInstallments = numberOfInstallments;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public Integer getProductId() {
        return productId;
    }

    public String getLoanAmount() {
        return loanAmount;
    }

    public Double getInterestRate() {
        return interestRate;
    }

    public LocalDate getDisbursementDate() {
        return disbursementDate;
    }

    public int getNumberOfInstallments() {
        return numberOfInstallments;
    }
}