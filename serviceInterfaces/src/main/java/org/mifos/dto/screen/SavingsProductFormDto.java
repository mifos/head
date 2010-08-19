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

public class SavingsProductFormDto {

    private final List<ListElement> productCategories;
    private final List<ListElement> applicableToCustomers;
    private final List<ListElement> depositTypes;
    private final List<ListElement> despositAmountAppliesTo;
    private final List<ListElement> interestBalanceTypes;
    private final List<ListElement> interestTimePeriodTypes;
    private final List<ListElement> principalGlCodes;
    private final List<ListElement> interestGlCodes;

    public SavingsProductFormDto(final List<ListElement> productCategories,
            final List<ListElement> applicableToCustomers, final List<ListElement> depositTypes,
            final List<ListElement> despositAmountAppliesTo, final List<ListElement> interestBalanceTypes,
            final List<ListElement> interestTimePeriodTypes, final List<ListElement> principalGlCodes,
            final List<ListElement> interestGlCodes) {
        this.productCategories = productCategories;
        this.applicableToCustomers = applicableToCustomers;
        this.depositTypes = depositTypes;
        this.despositAmountAppliesTo = despositAmountAppliesTo;
        this.interestBalanceTypes = interestBalanceTypes;
        this.interestTimePeriodTypes = interestTimePeriodTypes;
        this.principalGlCodes = principalGlCodes;
        this.interestGlCodes = interestGlCodes;
    }

    public List<ListElement> getProductCategories() {
        return this.productCategories;
    }

    public List<ListElement> getApplicableToCustomers() {
        return this.applicableToCustomers;
    }

    public List<ListElement> getDepositTypes() {
        return this.depositTypes;
    }

    public List<ListElement> getDespositAmountAppliesTo() {
        return this.despositAmountAppliesTo;
    }

    public List<ListElement> getInterestBalanceTypes() {
        return this.interestBalanceTypes;
    }

    public List<ListElement> getInterestTimePeriodTypes() {
        return this.interestTimePeriodTypes;
    }

    public List<ListElement> getPrincipalGlCodes() {
        return this.principalGlCodes;
    }

    public List<ListElement> getInterestGlCodes() {
        return this.interestGlCodes;
    }

}