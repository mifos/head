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
package org.mifos.customers.util.helpers;

import org.mifos.framework.business.service.DataTransferObject;
import org.mifos.framework.util.helpers.Money;

/**
 *
 */
public class SavingsDetailDto implements DataTransferObject {
    private final String globalAccountNum;
    private final String prdOfferingName;
    private final Short accountStateId;
    private final String accountStateName;
    private final Money savingsBalance;
    private final Short prdOfferingId;

    public SavingsDetailDto(final String globalAccountNum, final String prdOfferingName, final Short accountStateId,
            final String accountStateName, final Money outstandingBalance) {
        this.prdOfferingId = null;
        this.globalAccountNum = globalAccountNum;
        this.prdOfferingName = prdOfferingName;
        this.accountStateId = accountStateId;
        this.accountStateName = accountStateName;
        this.savingsBalance = outstandingBalance;
    }

    private SavingsDetailDto(Short prdOfferingId, String prdOfferingName) {
        this.prdOfferingId = prdOfferingId;
        this.prdOfferingName = prdOfferingName;
        this.globalAccountNum = null;
        this.accountStateId = null;
        this.accountStateName = null;
        this.savingsBalance = null;
    }

    public String getGlobalAccountNum() {
        return this.globalAccountNum;
    }

    public String getPrdOfferingName() {
        return this.prdOfferingName;
    }

    public Short getAccountStateId() {
        return this.accountStateId;
    }

    public String getAccountStateName() {
        return this.accountStateName;
    }

    public Money getSavingsBalance() {
        return this.savingsBalance;
    }

    public static SavingsDetailDto create(Short withPrdOfferingId, String withPrdOfferingName) {
        return new SavingsDetailDto(withPrdOfferingId, withPrdOfferingName);
    }

    public Short getPrdOfferingId() {
        return this.prdOfferingId;
    }
}