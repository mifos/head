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

package org.mifos.dto.domain;

import java.util.List;

import org.mifos.dto.screen.AccountingDetailsDto;
import org.mifos.dto.screen.LoanAmountDetails;
import org.mifos.dto.screen.LoanProductDetails;

public class LoanProductRequest {

    private final LoanProductDetails loanProductDetails;
    private final Integer interestRateType;
    private final MinMaxDefaultDto<Double> interestRateRange;
    private final RepaymentDetailsDto repaymentDetails;
    private final LoanAmountDetails loanAmountDetails;
    private final List<Integer> applicableFees;
    private final AccountingDetailsDto accountDetails;


    public LoanProductRequest(LoanProductDetails loanProductDetails, LoanAmountDetails loanAmountDetails, Integer interestRateType,
            MinMaxDefaultDto<Double> interestRateRange, RepaymentDetailsDto repaymentDetails, List<Integer> applicableFees, AccountingDetailsDto accountDetails) {
        this.loanProductDetails = loanProductDetails;
        this.loanAmountDetails = loanAmountDetails;
        this.interestRateType = interestRateType;
        this.interestRateRange = interestRateRange;
        this.repaymentDetails = repaymentDetails;
        this.applicableFees = applicableFees;
        this.accountDetails = accountDetails;
    }

    public Integer getInterestRateType() {
        return this.interestRateType;
    }

    public MinMaxDefaultDto<Double> getInterestRateRange() {
        return this.interestRateRange;
    }

    public RepaymentDetailsDto getRepaymentDetails() {
        return this.repaymentDetails;
    }

    public LoanProductDetails getLoanProductDetails() {
        return this.loanProductDetails;
    }

    public LoanAmountDetails getLoanAmountDetails() {
        return this.loanAmountDetails;
    }

    public List<Integer> getApplicableFees() {
        return this.applicableFees;
    }

    public AccountingDetailsDto getAccountDetails() {
        return this.accountDetails;
    }
}