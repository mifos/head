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

package org.mifos.dto.screen;

import java.util.List;

public class LoanProductFormDto {

    private final List<ListElement> productCategories;
    private final List<ListElement> gracePeriodTypes;
    private final List<ListElement> sourceOfFunds;
    private final List<ListElement> loanFees;
    private final List<ListElement> principalGlCodes;
    private final List<ListElement> interestGlCodes;
    private final List<ListElement> interestCalculationTypes;
    private final List<ListElement> applicableCustomerTypes;
    private final List<ListElement> statusOptions;

    public LoanProductFormDto(List<ListElement> productCategories, List<ListElement> gracePeriodTypes,
            List<ListElement> sourceOfFunds, List<ListElement> loanFees, List<ListElement> principalGlCodes,
            List<ListElement> interestGlCodes, List<ListElement> interestCalculationTypes, List<ListElement> applicableCustomerTypes,  List<ListElement> statusOptions) {
        this.productCategories = productCategories;
        this.gracePeriodTypes = gracePeriodTypes;
        this.sourceOfFunds = sourceOfFunds;
        this.loanFees = loanFees;
        this.principalGlCodes = principalGlCodes;
        this.interestGlCodes = interestGlCodes;
        this.interestCalculationTypes = interestCalculationTypes;
        this.applicableCustomerTypes = applicableCustomerTypes;
        this.statusOptions = statusOptions;
    }

    public List<ListElement> getProductCategories() {
        return this.productCategories;
    }

    public List<ListElement> getGracePeriodTypes() {
        return this.gracePeriodTypes;
    }

    public List<ListElement> getSourceOfFunds() {
        return this.sourceOfFunds;
    }

    public List<ListElement> getLoanFees() {
        return this.loanFees;
    }

    public List<ListElement> getPrincipalGlCodes() {
        return this.principalGlCodes;
    }

    public List<ListElement> getInterestGlCodes() {
        return this.interestGlCodes;
    }

    public List<ListElement> getInterestCalculationTypes() {
        return this.interestCalculationTypes;
    }

    public List<ListElement> getApplicableCustomerTypes() {
        return this.applicableCustomerTypes;
    }

    public List<ListElement> getStatusOptions() {
        return this.statusOptions;
    }
}