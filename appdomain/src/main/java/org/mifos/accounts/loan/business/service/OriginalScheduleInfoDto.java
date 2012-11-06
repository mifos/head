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

package org.mifos.accounts.loan.business.service;

import java.util.Date;
import java.util.List;

import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.customers.api.DataTransferObject;

public class OriginalScheduleInfoDto implements DataTransferObject {
    private String loanAmount;
    private Date disbursementDate;
    private List<RepaymentScheduleInstallment> originalLoanScheduleInstallment;

    public OriginalScheduleInfoDto(String loanAmount, Date disbursementDate, List<RepaymentScheduleInstallment> originalLoanScheduleInstallment) {
        this.loanAmount = loanAmount;
        this.disbursementDate = disbursementDate;
        this.originalLoanScheduleInstallment = originalLoanScheduleInstallment;
    }

    public String getLoanAmount() {
        return loanAmount;
    }

    public Date getDisbursementDate() {
        return disbursementDate;
    }

    public List<RepaymentScheduleInstallment> getOriginalLoanScheduleInstallment() {
        return originalLoanScheduleInstallment;
    }

    public boolean hasOriginalInstallments() {
        return originalLoanScheduleInstallment != null && !originalLoanScheduleInstallment.isEmpty();
    }
    
}
