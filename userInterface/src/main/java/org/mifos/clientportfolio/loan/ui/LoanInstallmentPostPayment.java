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

package org.mifos.clientportfolio.loan.ui;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification="should disable at filter level and also for pmd - not important for us")
public class LoanInstallmentPostPayment {

    private Integer installmentNumber;
    private LocalDate dueDate;
    private LocalDate lastPaymentDate;
    private BigDecimal feesPaid;
    private BigDecimal interestPaid;
    private BigDecimal principalPaid;
    private BigDecimal totalInstallmentPaid;
    private Double total;

    public LoanInstallmentPostPayment() {
    }

    public LoanInstallmentPostPayment(Integer installmentNumber, LocalDate dueDate, LocalDate lastPaymentDate,
            BigDecimal feesPaid, BigDecimal interestPaid, BigDecimal principalPaid, BigDecimal totalInstallmentPaid,
            Double total) {
        this.installmentNumber = installmentNumber;
        this.dueDate = dueDate;
        this.lastPaymentDate = lastPaymentDate;
        this.feesPaid = feesPaid;
        this.interestPaid = interestPaid;
        this.principalPaid = principalPaid;
        this.totalInstallmentPaid = totalInstallmentPaid;
        this.total = total;
    }

    public LocalDate getLastPaymentDate() {
        return lastPaymentDate;
    }

    public void setLastPaymentDate(LocalDate lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }

    public BigDecimal getFeesPaid() {
        return feesPaid;
    }

    public void setFeesPaid(BigDecimal feesPaid) {
        this.feesPaid = feesPaid;
    }

    public BigDecimal getInterestPaid() {
        return interestPaid;
    }

    public void setInterestPaid(BigDecimal interestPaid) {
        this.interestPaid = interestPaid;
    }

    public BigDecimal getPrincipalPaid() {
        return principalPaid;
    }

    public void setPrincipalPaid(BigDecimal principalPaid) {
        this.principalPaid = principalPaid;
    }

    public BigDecimal getTotalInstallmentPaid() {
        return totalInstallmentPaid;
    }

    public void setTotalInstallmentPaid(BigDecimal totalInstallmentPaid) {
        this.totalInstallmentPaid = totalInstallmentPaid;
    }

    public Integer getInstallmentNumber() {
        return installmentNumber;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Double getTotal() {
        return total;
    }

    public void setInstallmentNumber(Integer installmentNumber) {
        this.installmentNumber = installmentNumber;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public boolean isNotFullyPaid() {
        return this.totalInstallmentPaid.doubleValue() != this.total.doubleValue();
    }
}