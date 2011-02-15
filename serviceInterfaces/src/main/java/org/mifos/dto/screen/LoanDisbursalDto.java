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

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification="should disable at filter level and also for pmd - not important for us")
public class LoanDisbursalDto implements Serializable {

    private final Integer accountId;
    private final Date proposedDate;
    private final String loanAmount;
    private final String amountPaidAtDisbursement;
    private final boolean backDatedTransactionsAllowed;
    private final boolean repaymentIndependentOfMeetingSchedule;
    private final boolean multiCurrencyEnabled;
    private final Short currencyId;

    public LoanDisbursalDto(Integer accountId, Date proposedDate, String loanAmount, String amountPaidAtDisbursement,
            boolean backDatedTransactionsAllowed, boolean repaymentIndependentOfMeetingSchedule, boolean multiCurrencyEnabled, Short currencyId) {
        this.accountId = accountId;
        this.proposedDate = proposedDate;
        this.loanAmount = loanAmount;
        this.amountPaidAtDisbursement = amountPaidAtDisbursement;
        this.backDatedTransactionsAllowed = backDatedTransactionsAllowed;
        this.repaymentIndependentOfMeetingSchedule = repaymentIndependentOfMeetingSchedule;
        this.multiCurrencyEnabled = multiCurrencyEnabled;
        this.currencyId = currencyId;
    }

    public Integer getAccountId() {
        return this.accountId;
    }

    public Date getProposedDate() {
        return this.proposedDate;
    }

    public String getLoanAmount() {
        return this.loanAmount;
    }

    public String getAmountPaidAtDisbursement() {
        return this.amountPaidAtDisbursement;
    }

    public boolean isBackDatedTransactionsAllowed() {
        return this.backDatedTransactionsAllowed;
    }

    public boolean isRepaymentIndependentOfMeetingSchedule() {
        return this.repaymentIndependentOfMeetingSchedule;
    }

    public boolean isMultiCurrencyEnabled() {
        return this.multiCurrencyEnabled;
    }

    public Short getCurrencyId() {
        return this.currencyId;
    }
}