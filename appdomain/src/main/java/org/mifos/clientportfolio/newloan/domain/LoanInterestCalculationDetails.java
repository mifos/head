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

public class LoanInterestCalculationDetails {

    private final Money loanAmount;
    private final Double interestRate;
    private final GraceType graceType;
    private final Integer gracePeriodDuration;
    private final Integer numberOfInstallments;
    private final Double durationInYears;
    private final Double interestFractionalRatePerInstallment;
    private final LocalDate disbursementDate;
    private final List<DateTime> loanScheduleDates;
    private List<Money> totalInstallmentAmounts = new ArrayList<Money>();

    public LoanInterestCalculationDetails(Money loanAmount, Double interestRate, GraceType graceType,
            Integer gracePeriodDuration, Integer numberOfInstallments, Double durationInYears,
            Double interestFractionalRatePerInstallment, LocalDate disbursementDate, List<DateTime> loanScheduleDates) {
        this.loanAmount = loanAmount;
        this.interestRate = interestRate;
        this.graceType = graceType;
        this.gracePeriodDuration = gracePeriodDuration;
        this.numberOfInstallments = numberOfInstallments;
        this.durationInYears = durationInYears;
        this.interestFractionalRatePerInstallment = interestFractionalRatePerInstallment;
        this.disbursementDate = disbursementDate;
        this.loanScheduleDates = loanScheduleDates;
    }

    public Money getLoanAmount() {
        return this.loanAmount;
    }

    public Double getInterestRate() {
        return this.interestRate;
    }

    public GraceType getGraceType() {
        return this.graceType;
    }

    public Integer getGracePeriodDuration() {
        return this.gracePeriodDuration;
    }

    public Integer getNumberOfInstallments() {
        return this.numberOfInstallments;
    }

    public Double getDurationInYears() {
        return this.durationInYears;
    }

    public Double getInterestFractionalRatePerInstallment() {
        return this.interestFractionalRatePerInstallment;
    }

    public LocalDate getDisbursementDate() {
        return disbursementDate;
    }

    public List<DateTime> getLoanScheduleDates() {
        return loanScheduleDates;
    }
    
    public List<Money> getTotalInstallmentAmounts() {
        return totalInstallmentAmounts;
    }

    public void setTotalInstallmentAmounts(List<Money> totalInstallmentAmounts) {
        this.totalInstallmentAmounts = totalInstallmentAmounts;
    }
}