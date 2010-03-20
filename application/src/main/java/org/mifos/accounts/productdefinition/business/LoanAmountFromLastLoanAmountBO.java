/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.accounts.productdefinition.business;

import static org.mifos.framework.util.helpers.NumberUtils.isBetween;

public class LoanAmountFromLastLoanAmountBO extends LoanAmountOption {
    private Double startRange;
    private Double endRange;
    private final Short loanAmountFromLastLoanID;

    public LoanAmountFromLastLoanAmountBO(Double minLoanAmount, Double maxLoanAmount, Double defaultLoanAmount,
            Double startRange, Double endRange, LoanOfferingBO loanOffering) {
        super(minLoanAmount, maxLoanAmount, defaultLoanAmount, loanOffering);
        this.endRange = endRange;
        this.startRange = startRange;
        this.loanAmountFromLastLoanID = null;
    }

    public LoanAmountFromLastLoanAmountBO() {
        this(null, null, null, null, null, null);
    }

    public Double getEndRange() {
        return endRange;
    }

    public Double getStartRange() {
        return startRange;
    }

    public Short getLoanAmountFromLastLoanID() {
        return loanAmountFromLastLoanID;
    }

    public void setEndRange(Double endRange) {
        this.endRange = endRange;
    }

    public void setStartRange(Double startRange) {
        this.startRange = startRange;
    }

    boolean isLoanAmountBetweenRange(Double lastLoanAmount) {
        return isBetween(getStartRange(), getEndRange(), lastLoanAmount);
    }
}
