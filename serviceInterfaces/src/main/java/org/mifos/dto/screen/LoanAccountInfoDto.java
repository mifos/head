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

package org.mifos.dto.screen;

import org.joda.time.LocalDate;

@SuppressWarnings("PMD")
public class LoanAccountInfoDto {

    private Integer customerId;
    private LocalDate disbursementDate;
    private Short fundId;

    private Short productId;
    private String loanAmount;

    private boolean interestDeductedAtDisbursement;
    private Double interest;
    private Short gracePeriod;

    private String maxLoanAmount;
    private String minLoanAmount;
    private Short numOfInstallments;
    private Short maxNumOfInstallments;
    private Short minNumOfInstallments;
    private String externalId;
    private Integer selectedLoanPurpose;
    private String collateralNote;
    private Integer selectedCollateralType;
    private Short accountState;

    public Short getProductId() {
        return this.productId;
    }

    public void setProductId(Short productId) {
        this.productId = productId;
    }

    public String getLoanAmount() {
        return this.loanAmount;
    }

    public void setLoanAmount(String loanAmount) {
        this.loanAmount = loanAmount;
    }

    public Short getNumOfInstallments() {
        return this.numOfInstallments;
    }

    public void setNumOfInstallments(Short numOfInstallments) {
        this.numOfInstallments = numOfInstallments;
    }

    public boolean isInterestDeductedAtDisbursement() {
        return this.interestDeductedAtDisbursement;
    }

    public void setInterestDeductedAtDisbursement(boolean isInterestDeductedAtDisbursement) {
        this.interestDeductedAtDisbursement = isInterestDeductedAtDisbursement;
    }

    public Double getInterest() {
        return this.interest;
    }

    public void setInterest(Double interest) {
        this.interest = interest;
    }

    public Short getGracePeriod() {
        return this.gracePeriod;
    }

    public void setGracePeriod(Short gracePeriod) {
        this.gracePeriod = gracePeriod;
    }

    public String getMaxLoanAmount() {
        return this.maxLoanAmount;
    }

    public void setMaxLoanAmount(String maxLoanAmount) {
        this.maxLoanAmount = maxLoanAmount;
    }

    public String getMinLoanAmount() {
        return this.minLoanAmount;
    }

    public void setMinLoanAmount(String minLoanAmount) {
        this.minLoanAmount = minLoanAmount;
    }

    public Short getMaxNumOfInstallments() {
        return this.maxNumOfInstallments;
    }

    public void setMaxNumOfInstallments(Short maxNumOfInstallments) {
        this.maxNumOfInstallments = maxNumOfInstallments;
    }

    public Short getMinNumOfInstallments() {
        return this.minNumOfInstallments;
    }

    public void setMinNumOfInstallments(Short minNumOfInstallments) {
        this.minNumOfInstallments = minNumOfInstallments;
    }

    public String getExternalId() {
        return this.externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Integer getSelectedLoanPurpose() {
        return this.selectedLoanPurpose;
    }

    public void setSelectedLoanPurpose(Integer selectedLoanPurpose) {
        this.selectedLoanPurpose = selectedLoanPurpose;
    }

    public String getCollateralNote() {
        return this.collateralNote;
    }

    public void setCollateralNote(String collateralNote) {
        this.collateralNote = collateralNote;
    }

    public Integer getSelectedCollateralType() {
        return this.selectedCollateralType;
    }

    public void setSelectedCollateralType(Integer selectedCollateralType) {
        this.selectedCollateralType = selectedCollateralType;
    }

    public Short getAccountState() {
        return this.accountState;
    }

    public void setAccountState(Short accountState) {
        this.accountState = accountState;
    }

    public Integer getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public LocalDate getDisbursementDate() {
        return this.disbursementDate;
    }

    public void setDisbursementDate(LocalDate disbursementDate) {
        this.disbursementDate = disbursementDate;
    }

    public Short getFundId() {
        return this.fundId;
    }

    public void setFundId(Short fundId) {
        this.fundId = fundId;
    }
}