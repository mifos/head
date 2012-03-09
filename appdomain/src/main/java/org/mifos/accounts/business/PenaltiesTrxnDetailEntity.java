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

package org.mifos.accounts.business;

import org.mifos.framework.business.AbstractEntity;
import org.mifos.framework.util.helpers.Money;

public class PenaltiesTrxnDetailEntity extends AbstractEntity {

    private Integer penaltiesTrxnId;
    private AccountTrxnEntity accountTrxn;
    private AccountPenaltiesEntity accountPenalties;
    private Money penaltyAmount;

    protected PenaltiesTrxnDetailEntity() {
        // default constructor for hibernate
    }

    public PenaltiesTrxnDetailEntity(final AccountTrxnEntity accountTrxnEntity, final AccountPenaltiesEntity accountPenaltiesEntity, final Money amount) {
        penaltiesTrxnId = null;
        accountTrxn = accountTrxnEntity;
        accountPenalties = accountPenaltiesEntity;
        penaltyAmount = amount;
    }

    public AccountPenaltiesEntity getAccountPenalties() {
        return accountPenalties;
    }

    public Money getPenaltyAmount() {
        return penaltyAmount;
    }

    public Integer getPenaltiesTrxnId() {
        return penaltiesTrxnId;
    }

    public AccountTrxnEntity getAccountTrxn() {
        return accountTrxn;
    }

    public void setAccountTrxn(final AccountTrxnEntity accountTrxn) {
        this.accountTrxn = accountTrxn;
    }

    public PenaltiesTrxnDetailEntity generateReverseTrxn(final AccountTrxnEntity accountTrxn) {
        return new PenaltiesTrxnDetailEntity(accountTrxn, getAccountPenalties(), getPenaltyAmount().negate());
    }
}