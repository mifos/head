/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.reports.business.dto;

import org.mifos.application.collectionsheet.business.CollSheetLnDetailsEntity;
import org.mifos.framework.util.helpers.MoneyUtils;

public class LoanProductDetails {
    private final Double balance;
    private final Double due;
    private final Double otherFee;
    private final Double otherFine;
    private final Double issues;
    private final String offeringShortName;

    public LoanProductDetails(CollSheetLnDetailsEntity loanDetails, String offeringShortName) {
        this.offeringShortName = offeringShortName;
        if (loanDetails == null) {
            balance = due = issues = otherFee = otherFine = 0d;
        } else {
            balance = MoneyUtils.getMoneyDoubleValue(loanDetails.getTotalPrincipalDue());
            due = MoneyUtils.getMoneyDoubleValue(loanDetails.getTotalAmntDue());
            issues = MoneyUtils.getMoneyDoubleValue(loanDetails.getAmntToBeDisbursed());
            otherFee = MoneyUtils.getMoneyDoubleValue(loanDetails.getTotalFees());
            otherFine = MoneyUtils.getMoneyDoubleValue(loanDetails.getTotalPenalty());
        }
    }

    public Double getBalance() {
        return balance;
    }

    public Double getDue() {
        return due;
    }

    public Double getIssues() {
        return issues;
    }

    Double getOtherFee() {
        return otherFee;
    }

    Double getOtherFine() {
        return otherFine;
    }

    public String getOfferingShortName() {
        return offeringShortName;
    }
}
