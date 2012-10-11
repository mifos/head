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
import java.math.BigDecimal;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_NO_SERIALVERSIONID", justification="should disable at filter level and also for pmd - not important for us")
public class SavingsDetailDto implements Serializable {

    private final String globalAccountNum;
    private final String prdOfferingName;
    private final Short accountStateId;
    private final String accountStateName;
    private final String savingsBalance;
    private final Short prdOfferingId;
    private final BigDecimal maxWithdrawalAmount;
    private final String savingsType;

    public SavingsDetailDto(final String globalAccountNum, final String prdOfferingName, final Short accountStateId,
            final String accountStateName, final String outstandingBalance) {
        this.prdOfferingId = null;
        this.globalAccountNum = globalAccountNum;
        this.prdOfferingName = prdOfferingName;
        this.accountStateId = accountStateId;
        this.accountStateName = accountStateName;
        this.savingsBalance = outstandingBalance;
        this.maxWithdrawalAmount = null;
        this.savingsType = null;
    }

    private SavingsDetailDto(Short prdOfferingId, String prdOfferingName) {
        this.prdOfferingId = prdOfferingId;
        this.prdOfferingName = prdOfferingName;
        this.globalAccountNum = null;
        this.accountStateId = null;
        this.accountStateName = null;
        this.savingsBalance = null;
        this.maxWithdrawalAmount = null;
        this.savingsType = null;
    }
    
    public SavingsDetailDto(final String globalAccountNum, final String prdOfferingName, final Short accountStateId,
            final String accountStateName, final String outstandingBalance, final BigDecimal maxWithdrawalAmount, final String savingsType) {
        this.prdOfferingId = null;
        this.globalAccountNum = globalAccountNum;
        this.prdOfferingName = prdOfferingName;
        this.accountStateId = accountStateId;
        this.accountStateName = accountStateName;
        this.savingsBalance = outstandingBalance;
        this.maxWithdrawalAmount = maxWithdrawalAmount;
        this.savingsType = savingsType;
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

    public String getSavingsBalance() {
        return this.savingsBalance;
    }

    public static SavingsDetailDto create(Short withPrdOfferingId, String withPrdOfferingName) {
        return new SavingsDetailDto(withPrdOfferingId, withPrdOfferingName);
    }

    public Short getPrdOfferingId() {
        return this.prdOfferingId;
    }

    public BigDecimal getMaxWithdrawalAmount() {
	    return maxWithdrawalAmount;
    }

    public String getSavingsType() {
        return savingsType;
    }
}
