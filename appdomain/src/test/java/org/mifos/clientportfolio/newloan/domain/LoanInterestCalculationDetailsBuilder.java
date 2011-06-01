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

package org.mifos.clientportfolio.newloan.domain;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.framework.util.helpers.Money;

public class LoanInterestCalculationDetailsBuilder {

    private Money loanAmount;
    private Double interestRate;
    private GraceType graceType;
    private Integer gracePeriodDuration;
    private Integer numberOfInstallments;
    private Double durationInYears;
    private Double interestFractionalRatePerInstallment;
    private LocalDate disbursementDate = new LocalDate();
    private List<DateTime> loanSchedules = new ArrayList<DateTime>();

    public LoanInterestCalculationDetails build() {
        return new LoanInterestCalculationDetails(loanAmount, interestRate, graceType, gracePeriodDuration, numberOfInstallments, durationInYears, interestFractionalRatePerInstallment, disbursementDate, loanSchedules);
    }

    public LoanInterestCalculationDetailsBuilder withLoanAmount(String loanAmount) {
        this.loanAmount = TestMoneyUtil.createMoney(loanAmount);
        return this;
    }

    public LoanInterestCalculationDetailsBuilder withInterestRate(String interestRate) {
        this.interestRate = Double.valueOf(interestRate);
        return this;
    }

    public LoanInterestCalculationDetailsBuilder withDurationInYears(String durationInYears) {
        this.durationInYears = Double.valueOf(durationInYears);
        return this;
    }

    public LoanInterestCalculationDetailsBuilder withGraceType(GraceType graceType) {
        this.graceType = graceType;
        return this;
    }

    public LoanInterestCalculationDetailsBuilder withGraceDurationOf(int graceDuration) {
        this.gracePeriodDuration = graceDuration;
        return this;
    }

    public LoanInterestCalculationDetailsBuilder withNumberOfInstallments(int numberOfInstallments) {
        this.numberOfInstallments = numberOfInstallments;
        return this;
    }

    public LoanInterestCalculationDetailsBuilder withInterestFractionalRatePerInstallmentOf(String interestFractionalRatePerInstallment) {
        this.interestFractionalRatePerInstallment = Double.valueOf(interestFractionalRatePerInstallment);
        return this;
    }
}