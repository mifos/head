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
import java.util.List;
import java.util.Locale;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mifos.dto.domain.LoanCreationInstallmentDto;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID"}, justification="should disable at filter level and also for pmd - not important for us")
public class LoanScheduleDto implements Serializable {

    private final String accountOwner;
    private final String loanAmount;
    
    private final LocalDate disbursementDate;
    private final List<LoanCreationInstallmentDto> installments;
    private final Locale locale;

    public LoanScheduleDto(String accountOwner, String loanAmount, LocalDate disbursementDate,
            List<LoanCreationInstallmentDto> installments, Locale locale) {
        this.accountOwner = accountOwner;
        this.loanAmount = loanAmount;
        this.disbursementDate = disbursementDate;
        this.installments = installments;
        this.locale = locale;
    }

    public String getAccountOwner() {
        return accountOwner;
    }

    public String getLoanAmount() {
        return loanAmount;
    }

    public LocalDate getDisbursementDate() {
        return this.disbursementDate;
    }
    
    public String getLocalisedDisbursementDate() {
        DateTimeFormatter formatter = DateTimeFormat.forStyle("M-").withLocale(locale);
        return formatter.print(this.disbursementDate);
    }

    public List<LoanCreationInstallmentDto> getInstallments() {
        return installments;
    }
}