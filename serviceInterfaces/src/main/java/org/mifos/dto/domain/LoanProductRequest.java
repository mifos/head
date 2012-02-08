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

import java.util.List;

import org.mifos.dto.screen.AccountingDetailsDto;
import org.mifos.dto.screen.LoanAmountDetailsDto;

@SuppressWarnings("PMD")
public class LoanProductRequest {

    private final ProductDetailsDto productDetails;
    private final boolean includeInLoanCycleCounter;
    private final boolean waiverInterest;
    private final Integer currencyId;
    private final Integer interestRateType;
    private final MinMaxDefaultDto interestRateRange;
    private final RepaymentDetailsDto repaymentDetails;
    private final LoanAmountDetailsDto loanAmountDetails;
    private final List<Integer> applicableFees;
    private final List<Integer> applicablePenalties;
    private final AccountingDetailsDto accountDetails;

    // used for response details
    private boolean multiCurrencyEnabled;
    private String currencyCode;
    private String interestRateTypeName;
    private List<String> fees;
    private List<String> funds;
    private List<String> penalties;
    private String interestGlCodeValue;
    private String principalGlCodeValue;

    public LoanProductRequest(ProductDetailsDto loanProductDetails, final boolean includeInLoanCycleCounter,
            boolean waiverInterest, Integer currencyId, LoanAmountDetailsDto loanAmountDetails,
            Integer interestRateType, MinMaxDefaultDto interestRateRange, RepaymentDetailsDto repaymentDetails,
            List<Integer> applicableFees, List<Integer> applicablePenalties, AccountingDetailsDto accountDetails) {
    this.productDetails = loanProductDetails;
        this.includeInLoanCycleCounter = includeInLoanCycleCounter;
        this.waiverInterest = waiverInterest;
        this.currencyId = currencyId;
        this.loanAmountDetails = loanAmountDetails;
        this.interestRateType = interestRateType;
        this.interestRateRange = interestRateRange;
        this.repaymentDetails = repaymentDetails;
        this.applicableFees = applicableFees;
        this.accountDetails = accountDetails;
        this.applicablePenalties = applicablePenalties;
    }

    public Integer getInterestRateType() {
        return this.interestRateType;
    }

    public MinMaxDefaultDto getInterestRateRange() {
        return this.interestRateRange;
    }

    public RepaymentDetailsDto getRepaymentDetails() {
        return this.repaymentDetails;
    }

    public LoanAmountDetailsDto getLoanAmountDetails() {
        return this.loanAmountDetails;
    }

    public List<Integer> getApplicableFees() {
        return this.applicableFees;
    }

    public AccountingDetailsDto getAccountDetails() {
        return this.accountDetails;
    }

    public boolean isIncludeInLoanCycleCounter() {
        return this.includeInLoanCycleCounter;
    }

    public boolean isWaiverInterest() {
        return this.waiverInterest;
    }

    public Integer getCurrencyId() {
        return this.currencyId;
    }

    public ProductDetailsDto getProductDetails() {
        return this.productDetails;
    }

    public boolean isMultiCurrencyEnabled() {
        return this.multiCurrencyEnabled;
    }

    public void setMultiCurrencyEnabled(boolean multiCurrencyEnabled) {
        this.multiCurrencyEnabled = multiCurrencyEnabled;
    }

    public String getCurrencyCode() {
        return this.currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getInterestRateTypeName() {
        return this.interestRateTypeName;
    }

    public void setInterestRateTypeName(String interestRateTypeName) {
        this.interestRateTypeName = interestRateTypeName;
    }

    public List<String> getFees() {
        return this.fees;
    }

    public void setFees(List<String> fees) {
        this.fees = fees;
    }

    public List<String> getPenalties() {
        return this.penalties;
    }
    
    public void setPenalties(List<String> penalties) {
        this.penalties = penalties;
    }

    public List<String> getFunds() {
        return this.funds;
    }

    public void setFunds(List<String> funds) {
        this.funds = funds;
    }

    public String getInterestGlCodeValue() {
        return this.interestGlCodeValue;
    }

    public void setInterestGlCodeValue(String interestGlCodeValue) {
        this.interestGlCodeValue = interestGlCodeValue;
    }

    public String getPrincipalGlCodeValue() {
        return this.principalGlCodeValue;
    }

    public void setPrincipalGlCodeValue(String principalGlCodeValue) {
        this.principalGlCodeValue = principalGlCodeValue;
    }

    public List<Integer> getApplicablePenalties() {
        return applicablePenalties;
    }
    
    
}