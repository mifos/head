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

package org.mifos.clientportfolio.newloan.applicationservice;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

public class CreateLoanAccount {

    private final Integer customerId;
    private final Integer productId;
    private final Integer accountState;
    private final BigDecimal loanAmount;
    private final Double interestRate;
    private final LocalDate disbursementDate;
    private final int numberOfInstallments;
    private final int graceDuration;
    private final Integer sourceOfFundId;
    private final Integer loanPurposeId;
    private final Integer collateralTypeId;
    private final String collateralNotes;
    private final String externalId;

    @SuppressWarnings("PMD")
    public CreateLoanAccount(Integer customerId, Integer productId, Integer accountState, BigDecimal loanAmount, Double interestRate,
            LocalDate disbursementDate, int numberOfInstallments, int graceDuration, Integer sourceOfFundId,
            Integer loanPurposeId, Integer collateralTypeId, String collateralNotes, String externalId) {
        this.customerId = customerId;
        this.productId = productId;
        this.accountState = accountState;
        this.loanAmount = loanAmount;
        this.interestRate = interestRate;
        this.disbursementDate = disbursementDate;
        this.numberOfInstallments = numberOfInstallments;
        this.graceDuration = graceDuration;
        this.sourceOfFundId = sourceOfFundId;
        this.loanPurposeId = loanPurposeId;
        this.collateralTypeId = collateralTypeId;
        this.collateralNotes = collateralNotes;
        this.externalId = externalId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public Integer getProductId() {
        return productId;
    }
    
    public Integer getAccountState() {
        return accountState;
    }

    public BigDecimal getLoanAmount() {
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

    public int getGraceDuration() {
        return graceDuration;
    }

    public Integer getSourceOfFundId() {
        return sourceOfFundId;
    }

    public Integer getLoanPurposeId() {
        return loanPurposeId;
    }

    public Integer getCollateralTypeId() {
        return collateralTypeId;
    }

    public String getCollateralNotes() {
        return collateralNotes;
    }
    
    public String getExternalId() {
        return externalId;
    }
}