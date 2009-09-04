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

package org.mifos.application.accounts.business;

import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.util.helpers.Money;

public class FeesTrxnDetailEntity extends PersistentObject {

    private Integer feesTrxnId;
    private AccountTrxnEntity accountTrxn;
    private AccountFeesEntity accountFees;
    private Money feeAmount;

    protected FeesTrxnDetailEntity() {
        // default constructor for hibernate
    }

    public FeesTrxnDetailEntity(final AccountTrxnEntity accountTrxnEntity, final AccountFeesEntity accountFeesEntity, final Money amount) {
        feesTrxnId = null;
        accountTrxn = accountTrxnEntity;
        accountFees = accountFeesEntity;
        feeAmount = amount;
    }

    public AccountFeesEntity getAccountFees() {
        return accountFees;
    }

    public Money getFeeAmount() {
        return feeAmount;
    }

    public Integer getFeesTrxnId() {
        return feesTrxnId;
    }
    
    public AccountTrxnEntity getAccountTrxn() {
        return accountTrxn;
    }
    
    public void setAccountTrxn(final AccountTrxnEntity accountTrxn) {
        this.accountTrxn = accountTrxn;
    }

    public FeesTrxnDetailEntity generateReverseTrxn(final AccountTrxnEntity accountTrxn) {
        return new FeesTrxnDetailEntity(accountTrxn, getAccountFees(), getFeeAmount().negate());
    }
}
