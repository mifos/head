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
import java.util.List;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification="should disable at filter level and also for pmd - not important for us")
public class LoanRepaymentScheduleItemDto implements Serializable {

    private final Short installmentNumber;
    private final Date dueDate;
    private final Short paymentStatus;
    private final Date paymentDate;
    private final String principal;
    private final String principalPaid;
    private final String interest;
    private final String interestPaid;
    private final String penalty;
    private final String penaltyPaid;
    private final String extraInterest;
    private final String extraInterestPaid;
    private final String miscFee;
    private final String miscFeePaid;
    private final String miscPenalty;
    private final String miscPenaltyPaid;
    private List<AccountFeeScheduleDto> feesActionDetails;

    public LoanRepaymentScheduleItemDto(Short installmentNumber, Date dueDate, Short paymentStatus, Date paymentDate, String principal, String principalPaid, String interest, String interestPaid, String penalty, String penaltyPaid, String extraInterest, String extraInterestPaid, String miscFee, String miscFeePaid, String miscPenalty, String miscPenaltyPaid, List<AccountFeeScheduleDto> feesActionDetails) {
        this.installmentNumber = installmentNumber;
        this.dueDate = dueDate;
        this.paymentStatus = paymentStatus;
        this.paymentDate = paymentDate;
        this.principal = principal;
        this.principalPaid = principalPaid;
        this.interest = interest;
        this.interestPaid = interestPaid;
        this.penalty = penalty;
        this.penaltyPaid = penaltyPaid;
        this.extraInterest = extraInterest;
        this.extraInterestPaid = extraInterestPaid;
        this.miscFee = miscFee;
        this.miscFeePaid = miscFeePaid;
        this.miscPenalty = miscPenalty;
        this.miscPenaltyPaid = miscPenaltyPaid;
        this.feesActionDetails = feesActionDetails;
    }

    public Short getInstallmentNumber() {
        return installmentNumber;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public Short getPaymentStatus() {
        return paymentStatus;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public String getPrincipal() {
        return principal;
    }

    public String getPrincipalPaid() {
        return principalPaid;
    }

    public String getInterest() {
        return interest;
    }

    public String getInterestPaid() {
        return interestPaid;
    }

    public String getPenalty() {
        return penalty;
    }

    public String getPenaltyPaid() {
        return penaltyPaid;
    }

    public String getExtraInterest() {
        return extraInterest;
    }

    public String getExtraInterestPaid() {
        return extraInterestPaid;
    }

    public String getMiscFee() {
        return miscFee;
    }

    public String getMiscFeePaid() {
        return miscFeePaid;
    }

    public String getMiscPenalty() {
        return miscPenalty;
    }

    public String getMiscPenaltyPaid() {
        return miscPenaltyPaid;
    }

    public List<AccountFeeScheduleDto> getFeesActionDetails() {
        return feesActionDetails;
    }
}