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

package org.mifos.accounts.productdefinition.business;

import org.mifos.framework.util.LocalizationConverter;

public abstract class LoanAmountOption extends AmountRange {
    private Double defaultLoanAmount;
    private LoanOfferingBO loanOffering;

    public LoanAmountOption(Double minLoanAmount, Double maxLoanAmount, Double defaultLoanAmount,
            LoanOfferingBO loanOffering) {
        super(minLoanAmount, maxLoanAmount);
        this.defaultLoanAmount = defaultLoanAmount;
        this.loanOffering = loanOffering;
    }

    public Double getDefaultLoanAmount() {
        return defaultLoanAmount;
    }

    public String getDefaultLoanAmountString() {
        LocalizationConverter converter = new LocalizationConverter(loanOffering.getCurrency());
        return converter.getDoubleStringForMoney(getDefaultLoanAmount());
    }

    public String getMaxLoanAmountString() {
        LocalizationConverter converter = new LocalizationConverter(loanOffering.getCurrency());
        return converter.getDoubleStringForMoney(getMaxLoanAmount());
    }

    public String getMinLoanAmountString() {
        LocalizationConverter converter = new LocalizationConverter(loanOffering.getCurrency());
        return converter.getDoubleStringForMoney(getMinLoanAmount());
    }

    public void setDefaultLoanAmount(Double defaultLoanAmount) {
        this.defaultLoanAmount = defaultLoanAmount;
    }

    public LoanOfferingBO getLoanOffering() {
        return loanOffering;
    }

    public void setLoanOffering(LoanOfferingBO loanOffering) {
        this.loanOffering = loanOffering;
    }
}