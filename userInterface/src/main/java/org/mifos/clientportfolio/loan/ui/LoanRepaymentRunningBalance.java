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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.joda.time.LocalDate;
import org.mifos.dto.domain.LoanCreationInstallmentDto;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification="should disable at filter level and also for pmd - not important for us")
public class LoanRepaymentRunningBalance implements Serializable {

    private BigDecimal principal;
    private BigDecimal interest;
    private BigDecimal fees;
    private BigDecimal totalInstallment;
    private LoanCreationInstallmentDto installmentDetails;
    private BigDecimal total;
    private Date paymentDate;
    private Short paymentTypeId;

    public LoanRepaymentRunningBalance() {
        //
    }
    
    public LoanRepaymentRunningBalance(LoanCreationInstallmentDto installmentDetails, BigDecimal total, BigDecimal principal, BigDecimal interest, BigDecimal fees,
            BigDecimal totalInstallment, LocalDate paymentDate, Short paymentTypeId) {
        this.installmentDetails = installmentDetails;
        this.total = total;
        this.principal = principal;
        this.interest = interest;
        this.fees = fees;
        this.totalInstallment = totalInstallment;
        this.paymentDate = paymentDate.toDateMidnight().toDate();
        this.paymentTypeId = paymentTypeId;
    }

    public BigDecimal getPrincipal() {
        return principal;
    }

    public BigDecimal getInterest() {
        return interest;
    }

    public BigDecimal getFees() {
        return fees;
    }

    public BigDecimal getTotalInstallment() {
        return totalInstallment;
    }

    public void setPrincipal(BigDecimal principal) {
        this.principal = principal;
    }

    public void setInterest(BigDecimal interest) {
        this.interest = interest;
    }

    public void setFees(BigDecimal fees) {
        this.fees = fees;
    }

    public void setTotalInstallment(BigDecimal totalInstallment) {
        this.totalInstallment = totalInstallment;
    }
    
    public LoanCreationInstallmentDto getInstallmentDetails() {
        return installmentDetails;
    }

    public void setInstallmentDetails(LoanCreationInstallmentDto installmentDetails) {
        this.installmentDetails = installmentDetails;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Short getPaymentTypeId() {
        return paymentTypeId;
    }

    public void setPaymentTypeId(Short paymentTypeId) {
        this.paymentTypeId = paymentTypeId;
    }
}