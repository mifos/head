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

package org.mifos.accounts.savings.business;

import java.sql.Timestamp;
import java.util.Date;

import org.mifos.accounts.business.AccountActionEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.framework.business.AbstractEntity;
import org.mifos.framework.util.helpers.Money;

public class SavingsActivityEntity extends AbstractEntity {

    private final Integer id;
    private final PersonnelBO trxnCreatedBy;
    private final AccountBO account;
    private final Timestamp trxnCreatedDate;
    private final AccountActionEntity activity;
    private final Money amount;
    private final Money balanceAmount;

    public static SavingsActivityEntity savingsInterestPosting(SavingsBO savings, PersonnelBO personnel,
            Money savingsBalance, Money interestToBePosted, Date nextIntPostDate) {
        AccountActionEntity accountAction = new AccountActionEntity(AccountActionTypes.SAVINGS_INTEREST_POSTING);
        return new SavingsActivityEntity(personnel, accountAction, interestToBePosted, savingsBalance, nextIntPostDate, savings);
    }

    public static SavingsActivityEntity savingsAdjustment(SavingsBO savings, PersonnelBO personnel, Money savingsBalance, Money amountAdjusted, Date adjustedOn) {
        AccountActionEntity accountAction = new AccountActionEntity(AccountActionTypes.SAVINGS_ADJUSTMENT);
        return new SavingsActivityEntity(personnel, accountAction, amountAdjusted, savingsBalance, adjustedOn, savings);
    }

    public static SavingsActivityEntity savingsWithdrawal(SavingsBO savings, PersonnelBO loggedInUser, Money savingsBalance, Money withdrawalAmount, Date paymentDate) {
        AccountActionEntity accountAction = new AccountActionEntity(AccountActionTypes.SAVINGS_WITHDRAWAL);
        return new SavingsActivityEntity(loggedInUser, accountAction, withdrawalAmount, savingsBalance, paymentDate, savings);
    }

    public static SavingsActivityEntity savingsWaiveAmountDueOnNextDeposit(SavingsBO savings, PersonnelBO personnel,
            Money savingsBalance, Money totalAmountDueForNextDeposit, Date activityDate) {
        AccountActionEntity accountAction = new AccountActionEntity(AccountActionTypes.WAIVEOFFDUE);
        return new SavingsActivityEntity(personnel, accountAction, totalAmountDueForNextDeposit, savingsBalance, activityDate, savings);
    }

    public static SavingsActivityEntity savingsWaiveDepositAmountOverdue(SavingsBO savings, PersonnelBO personnel,
            Money savingsBalance, Money totalAmountInArrears, Date activityDate) {
        AccountActionEntity accountAction = new AccountActionEntity(AccountActionTypes.WAIVEOFFOVERDUE);
        return new SavingsActivityEntity(personnel, accountAction, totalAmountInArrears, savingsBalance, activityDate, savings);
    }

    protected SavingsActivityEntity() {
        this.id = null;
        this.account = null;
        this.trxnCreatedBy = null;
        this.activity = null;
        this.amount = null;
        this.balanceAmount = null;
        this.trxnCreatedDate = null;
    }

    public SavingsActivityEntity(final PersonnelBO trxnCreatedBy, final AccountActionEntity activity, final Money amount,
            final Money balanceAmount, final Date trxnDate, final AccountBO account) {
        id = null;
        this.trxnCreatedBy = trxnCreatedBy;
        this.activity = activity;
        this.amount = amount;
        this.balanceAmount = balanceAmount;
        this.trxnCreatedDate = new Timestamp(trxnDate.getTime());
        this.account = account;
    }

    public AccountBO getAccount() {
        return account;
    }

    public AccountActionEntity getActivity() {
        return activity;
    }

    public Money getAmount() {
        return amount;
    }

    public Money getBalanceAmount() {
        return balanceAmount;
    }

    public Integer getId() {
        return id;
    }

    @SuppressWarnings("unused")
    // see .hbm.xml file
    private PersonnelBO getTrxnCreatedBy() {
        return trxnCreatedBy;
    }

    public Timestamp getTrxnCreatedDate() {
        return trxnCreatedDate;
    }
}