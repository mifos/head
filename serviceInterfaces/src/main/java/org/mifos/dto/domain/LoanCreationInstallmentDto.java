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

import java.io.Serializable;
import java.util.Date;

import org.joda.time.LocalDate;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification="should disable at filter level and also for pmd - not important for us")
public class LoanCreationInstallmentDto implements Serializable {

    private final Integer installmentNumber;
    private LocalDate dueDate;
    private Double principal;
    private final Double interest;
    private final Double fees;
    private final Double penalty;
    private Double total;

    public LoanCreationInstallmentDto(Integer installmentNumber, LocalDate dueDate, Double principal, Double interest, Double fees, Double penalty, Double total) {
        this.installmentNumber = installmentNumber;
        this.dueDate = dueDate;
        this.principal = principal;
        this.interest = interest;
        this.fees = fees;
        this.penalty = penalty;
        this.total = total;
    }

    public Integer getInstallmentNumber() {
        return installmentNumber;
    }
    
    public Date getDueDate() {
        return this.dueDate.toDateMidnight().toDate();
    }
    
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    public Double getFees() {
        return fees;
    }

    public Double getInterest() {
        return interest;
    }

    public Double getPenalty() {
        return penalty;
    }

    public Double getPrincipal() {
        return principal;
    }
    
    public void setPrincipal(Double principal) {
        this.principal = principal;
    }
    
    public Double getTotal() {
        return total;
    }
    
    public void setTotal(Double total) {
        this.total = total;
    }
}