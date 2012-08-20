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

import org.joda.time.LocalDate;

public class OpeningBalanceSavingsAccount {

    private final String accountNumber;
    private final String customerGlobalId;
    private final String productGlobalId;
    private final Short accountState;
    private final String recommendedOrMandatoryAmount;
    private final String openingBalance;
    private final LocalDate activationDate;

    public OpeningBalanceSavingsAccount(final String customerGlobalId, final String productGlobalId,
            final Short accountState, final String recommendedOrMandatoryAmount, final String openingBalance, final LocalDate activationDate, final String accountNumber) {
        this.customerGlobalId = customerGlobalId;
        this.productGlobalId = productGlobalId;
        this.accountState = accountState;
        this.recommendedOrMandatoryAmount = recommendedOrMandatoryAmount;
        this.openingBalance = openingBalance;
        this.activationDate = activationDate;
        this.accountNumber = accountNumber;
    }

    public String getCustomerGlobalId() {
        return this.customerGlobalId;
    }

    public String getProductGlobalId() {
        return this.productGlobalId;
    }

    public Short getAccountState() {
        return this.accountState;
    }

    public String getRecommendedOrMandatoryAmount() {
        return this.recommendedOrMandatoryAmount;
    }

    public String getOpeningBalance() {
        return this.openingBalance;
    }

    public LocalDate getActivationDate() {
        return this.activationDate;
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }
    
}