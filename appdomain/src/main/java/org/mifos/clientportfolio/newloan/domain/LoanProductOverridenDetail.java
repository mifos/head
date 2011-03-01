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

import org.joda.time.LocalDate;
import org.mifos.framework.util.helpers.Money;

public class LoanProductOverridenDetail {

    private final Money loanAmount;
    private final LocalDate disbursementDate;
    private final Double interestRate;
    private final int numberOfInstallments;
    private final int graceDuration;

    public LoanProductOverridenDetail(Money loanAmount, LocalDate disbursementDate, Double interestRate, int numberOfInstallments, int graceDuration) {
        this.loanAmount = loanAmount;
        this.disbursementDate = disbursementDate;
        this.interestRate = interestRate;
        this.numberOfInstallments = numberOfInstallments;
        this.graceDuration = graceDuration;
    }

    public Money getLoanAmount() {
        return loanAmount;
    }

    public LocalDate getDisbursementDate() {
        return disbursementDate;
    }

    public Double getInterestRate() {
        return interestRate;
    }

    public int getNumberOfInstallments() {
        return numberOfInstallments;
    }

    public int getGraceDuration() {
        return graceDuration;
    }
}