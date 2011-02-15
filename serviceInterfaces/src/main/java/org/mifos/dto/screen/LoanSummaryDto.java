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

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = { "SE_NO_SERIALVERSIONID", "EI_EXPOSE_REP", "EI_EXPOSE_REP2" }, justification = "should disable at filter level and also for pmd - not important for us")
public class LoanSummaryDto implements Serializable {

    private final String originalPrincipal;
    private final String principalPaid;
    private final String principalDue;

    private final String originalInterest;
    private final String interestPaid;
    private final String interestDue;

    private final String originalFees;
    private final String feesPaid;
    private final String feesDue;

    private final String originalPenalty;
    private final String penaltyPaid;
    private final String penaltyDue;

    private final String totalLoanAmnt;
    private final String totalAmntPaid;
    private final String totalAmntDue;

    public LoanSummaryDto(String originalPrincipal, String principalPaid, String principalDue, String originalInterest,
            String interestPaid, String interestDue, String originalFees, String feesPaid, String feesDue,
            String originalPenalty, String penaltyPaid, String penaltyDue, String totalLoanAmnt, String totalAmntPaid,
            String totalAmntDue) {
        super();
        this.originalPrincipal = originalPrincipal;
        this.principalPaid = principalPaid;
        this.principalDue = principalDue;
        this.originalInterest = originalInterest;
        this.interestPaid = interestPaid;
        this.interestDue = interestDue;
        this.originalFees = originalFees;
        this.feesPaid = feesPaid;
        this.feesDue = feesDue;
        this.originalPenalty = originalPenalty;
        this.penaltyPaid = penaltyPaid;
        this.penaltyDue = penaltyDue;
        this.totalLoanAmnt = totalLoanAmnt;
        this.totalAmntPaid = totalAmntPaid;
        this.totalAmntDue = totalAmntDue;
    }

    public String getOriginalPrincipal() {
        return this.originalPrincipal;
    }

    public String getPrincipalPaid() {
        return this.principalPaid;
    }

    public String getPrincipalDue() {
        return this.principalDue;
    }

    public String getOriginalInterest() {
        return this.originalInterest;
    }

    public String getInterestPaid() {
        return this.interestPaid;
    }

    public String getInterestDue() {
        return this.interestDue;
    }

    public String getOriginalFees() {
        return this.originalFees;
    }

    public String getFeesPaid() {
        return this.feesPaid;
    }

    public String getFeesDue() {
        return this.feesDue;
    }

    public String getOriginalPenalty() {
        return this.originalPenalty;
    }

    public String getPenaltyPaid() {
        return this.penaltyPaid;
    }

    public String getPenaltyDue() {
        return this.penaltyDue;
    }

    public String getTotalLoanAmnt() {
        return this.totalLoanAmnt;
    }

    public String getTotalAmntPaid() {
        return this.totalAmntPaid;
    }

    public String getTotalAmntDue() {
        return this.totalAmntDue;
    }
}
