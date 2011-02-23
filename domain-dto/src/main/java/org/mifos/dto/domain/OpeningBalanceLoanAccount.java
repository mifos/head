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

import org.joda.time.LocalDate;

public class OpeningBalanceLoanAccount {

    private final String customerGlobalId;
    private final String loanProductGlobalId;
    private final Short accountState;
    private final String loanAmountDisbursed;
    private final LocalDate disbursementDate;
    private final String amountPaidToDate;
    private final Integer numberOfInstallments;
    private final LocalDate firstInstallmentDate;
    private final LocalDate currentInstallmentDate;
    private final Integer loanCycle;

    public OpeningBalanceLoanAccount(String customerGlobalId, String loanProductGlobalId, Short accountState,
            String loanAmountDisbursed, LocalDate disbursementDate, String amountPaidToDate, Integer numberOfInstallments,
            LocalDate firstInstallmentDate, LocalDate currentInstallmentDate, Integer loanCycle) {
        this.customerGlobalId = customerGlobalId;
        this.loanProductGlobalId = loanProductGlobalId;
        this.accountState = accountState;
        this.loanAmountDisbursed = loanAmountDisbursed;
        this.disbursementDate = disbursementDate;
        this.amountPaidToDate = amountPaidToDate;
        this.numberOfInstallments = numberOfInstallments;
        this.firstInstallmentDate = firstInstallmentDate;
        this.currentInstallmentDate = currentInstallmentDate;
        this.loanCycle = loanCycle;
    }

    public String getCustomerGlobalId() {
        return this.customerGlobalId;
    }

    public String getLoanProductGlobalId() {
        return this.loanProductGlobalId;
    }

    public Short getAccountState() {
        return this.accountState;
    }

    public String getLoanAmountDisbursed() {
        return this.loanAmountDisbursed;
    }

    public LocalDate getDisbursementDate() {
        return this.disbursementDate;
    }

    public String getAmountPaidToDate() {
        return this.amountPaidToDate;
    }

    public Integer getNumberOfInstallments() {
        return this.numberOfInstallments;
    }

    public LocalDate getFirstInstallmentDate() {
        return this.firstInstallmentDate;
    }

    public LocalDate getCurrentInstallmentDate() {
        return this.currentInstallmentDate;
    }

    public Integer getLoanCycle() {
        return this.loanCycle;
    }

}