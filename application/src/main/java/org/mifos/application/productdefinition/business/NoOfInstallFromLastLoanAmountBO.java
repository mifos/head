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

package org.mifos.application.productdefinition.business;

import static org.mifos.framework.util.helpers.NumberUtils.isBetween;

public class NoOfInstallFromLastLoanAmountBO extends LoanOfferingInstallmentRange {

    @SuppressWarnings("unused")
    private final Short noOfInstallFromLastLoanID;
    private Double startRange;
    private Double endRange;

    public NoOfInstallFromLastLoanAmountBO(Short minNoOfInstall, Short maxNoOfInstall, Short defaultNoOfInstall,
            Double startRange, Double endRange, LoanOfferingBO loanOffering) {
        super(minNoOfInstall, maxNoOfInstall, defaultNoOfInstall, loanOffering);
        this.startRange = startRange;
        this.endRange = endRange;
        this.noOfInstallFromLastLoanID = null;
    }

    public NoOfInstallFromLastLoanAmountBO() {
        this(null, null, null, null, null, null);
    }

    public Double getEndRange() {
        return endRange;
    }

    public void setEndRange(Double endRange) {
        this.endRange = endRange;
    }

    public Double getStartRange() {
        return startRange;
    }

    public void setStartRange(Double startRange) {
        this.startRange = startRange;
    }

    boolean loanAmountInRange(Double value) {
        return isBetween(startRange, endRange, value);
    }
}
