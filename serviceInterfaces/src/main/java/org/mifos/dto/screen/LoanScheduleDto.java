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

package org.mifos.dto.screen;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifos.dto.domain.LoanCreationInstallmentDto;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID"}, justification="should disable at filter level and also for pmd - not important for us")
public class LoanScheduleDto implements Serializable {

    private final String accountOwner;
    private final Double loanAmount;
    
    private final LocalDate disbursementDate;
    private final List<LoanCreationInstallmentDto> installments;

    public LoanScheduleDto(String accountOwner, Double loanAmount, LocalDate disbursementDate,
            List<LoanCreationInstallmentDto> installments) {
        this.accountOwner = accountOwner;
        this.loanAmount = loanAmount;
        this.disbursementDate = disbursementDate;
        this.installments = installments;
    }

    public String getAccountOwner() {
        return accountOwner;
    }

    public Double getLoanAmount() {
        return loanAmount;
    }

    public Date getDisbursementDate() {
        return this.disbursementDate.toDateMidnight().toDate();
    }

    public List<LoanCreationInstallmentDto> getInstallments() {
        return installments;
    }
}