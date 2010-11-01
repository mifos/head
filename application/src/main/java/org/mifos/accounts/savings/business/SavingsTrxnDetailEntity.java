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

package org.mifos.accounts.savings.business;

import java.util.Date;

import org.joda.time.DateTime;
import org.mifos.accounts.business.AccountActionEntity;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.business.AccountTrxnEntity;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.framework.util.helpers.Money;

public class SavingsTrxnDetailEntity extends AccountTrxnEntity {

    private final Money depositAmount;
    private final Money withdrawlAmount;
    private final Money interestAmount;
    private final Money balance;

    public static SavingsTrxnDetailEntity savingsInterestPosting(AccountPaymentEntity interestPayment,
            CustomerBO customer, Money savingsBalance, Date nextIntPostDate, DateTime dueDate, PersonnelBO createdBy) {
        AccountActionEntity accountAction = new AccountActionEntity(AccountActionTypes.SAVINGS_INTEREST_POSTING);
        SavingsTrxnDetailEntity relatedTrxnNotApplicable = null;
        return new SavingsTrxnDetailEntity(interestPayment,
                customer, accountAction, accountAction, interestPayment.getAmount(), savingsBalance, createdBy, null,
                nextIntPostDate, null, "", dueDate.toDate(), relatedTrxnNotApplicable);
    }

    public static SavingsTrxnDetailEntity savingsDeposit(AccountPaymentEntity newAccountPayment, CustomerBO customer,
            Money savingsBalance, Money depositAmount, PersonnelBO createdBy,
            Date dueDate, Date actionDate, Date transactionCreatedDate, Short installmentId) {

        AccountActionEntity accountAction = new AccountActionEntity(AccountActionTypes.SAVINGS_DEPOSIT);
        SavingsTrxnDetailEntity relatedTrxnNotApplicable = null;
        return new SavingsTrxnDetailEntity(newAccountPayment, customer, accountAction, accountAction, depositAmount, savingsBalance,
                createdBy, dueDate, actionDate, installmentId, "", transactionCreatedDate, relatedTrxnNotApplicable);
    }

    public static SavingsTrxnDetailEntity savingsWithdrawal(AccountPaymentEntity newAccountPayment, CustomerBO customer,
            Money savingsBalance, Money withdrawalAmount, PersonnelBO createdBy,
            Date dueDate, Date oldTrxnDate, Date transactionCreatedDate) {

        AccountActionEntity accountAction = new AccountActionEntity(AccountActionTypes.SAVINGS_WITHDRAWAL);
        Short installmentIdNotNeeded = null;
        SavingsTrxnDetailEntity relatedTrxnNotApplicable = null;
        return new SavingsTrxnDetailEntity(newAccountPayment, customer, accountAction, accountAction, withdrawalAmount, savingsBalance,
                createdBy, dueDate, oldTrxnDate, installmentIdNotNeeded, "", transactionCreatedDate, relatedTrxnNotApplicable);
    }

    public static SavingsTrxnDetailEntity savingsDepositAdjustment(AccountPaymentEntity accountPayment, CustomerBO customer,
            Money balAfterAdjust, Money negate, PersonnelBO loggedInUser, Date dueDate, Date actionDate, Date transactionCreatedDate,
            String adjustmentComment, SavingsTrxnDetailEntity relatedTrxn) {

        AccountActionEntity applicationTransactionType = new AccountActionEntity(AccountActionTypes.SAVINGS_ADJUSTMENT);
        AccountActionEntity monetaryTransactionType = new AccountActionEntity(AccountActionTypes.SAVINGS_DEPOSIT);
        Short installmentIdNotNeeded = null;
        SavingsTrxnDetailEntity adjustment = new SavingsTrxnDetailEntity(accountPayment, customer, applicationTransactionType, monetaryTransactionType, negate, balAfterAdjust,
                loggedInUser, dueDate, actionDate, installmentIdNotNeeded, adjustmentComment, transactionCreatedDate, relatedTrxn);

        return adjustment;
    }

    public static SavingsTrxnDetailEntity savingsWithdrawalAdjustment(AccountPaymentEntity accountPayment, CustomerBO customer,
            Money balAfterAdjust, Money negate, PersonnelBO loggedInUser, Date dueDate, Date actionDate, Date transactionCreatedDate,
            String adjustmentComment, SavingsTrxnDetailEntity relatedTrxn) {

        AccountActionEntity applicationTransactionType = new AccountActionEntity(AccountActionTypes.SAVINGS_ADJUSTMENT);
        AccountActionEntity monetaryTransactionType = new AccountActionEntity(AccountActionTypes.SAVINGS_WITHDRAWAL);
        Short installmentIdNotNeeded = null;
        SavingsTrxnDetailEntity adjustment = new SavingsTrxnDetailEntity(accountPayment, customer, applicationTransactionType, monetaryTransactionType, negate, balAfterAdjust,
                loggedInUser, dueDate, actionDate, installmentIdNotNeeded, adjustmentComment, transactionCreatedDate, relatedTrxn);

        return adjustment;
    }

    public static SavingsTrxnDetailEntity savingsAdjustment(AccountPaymentEntity accountPayment, CustomerBO customer,
            Money balAfterAdjust, Money negate, PersonnelBO loggedInUser, Date dueDate, Date actionDate, Date transactionCreatedDate,
            String adjustmentComment, SavingsTrxnDetailEntity relatedTrxn) {

        AccountActionEntity applicationTransactionType = new AccountActionEntity(AccountActionTypes.SAVINGS_ADJUSTMENT);
        AccountActionEntity monetaryTransactionType = new AccountActionEntity(AccountActionTypes.SAVINGS_ADJUSTMENT);
        Short installmentIdNotNeeded = null;
        SavingsTrxnDetailEntity adjustment = new SavingsTrxnDetailEntity(accountPayment, customer, applicationTransactionType, monetaryTransactionType, negate, balAfterAdjust,
                loggedInUser, dueDate, actionDate, installmentIdNotNeeded, adjustmentComment, transactionCreatedDate, relatedTrxn);

        return adjustment;
    }

    /**
     * default constructor for hibernate
     */
    protected SavingsTrxnDetailEntity() {
        depositAmount = null;
        withdrawlAmount = null;
        interestAmount = null;
        balance = null;
    }

    public SavingsTrxnDetailEntity(AccountPaymentEntity accountPaymentEntity, CustomerBO customer,
            AccountActionEntity accountActionType, AccountActionEntity monetaryTransactionType, Money amount, Money balance, PersonnelBO createdBy,
            java.util.Date dueDate, java.util.Date actionDate, Short installmentId, String comment, java.util.Date transactionDate, SavingsTrxnDetailEntity relatedTrxn) {

        super(accountPaymentEntity, accountActionType, installmentId, dueDate, createdBy, customer, actionDate, amount, comment, relatedTrxn, transactionDate);

        this.balance = balance;
        MifosCurrency currency = accountPaymentEntity.getAccount().getCurrency();
        if (AccountActionTypes.SAVINGS_WITHDRAWAL.equals(monetaryTransactionType.asEnum())) {
            this.depositAmount = new Money(currency);
            this.withdrawlAmount = amount;
            this.interestAmount = new Money(currency);
        } else if (AccountActionTypes.SAVINGS_DEPOSIT.equals(monetaryTransactionType.asEnum())) {
            this.depositAmount = amount;
            this.withdrawlAmount = new Money(currency);
            this.interestAmount = new Money(currency);
        } else if (AccountActionTypes.SAVINGS_INTEREST_POSTING.equals(monetaryTransactionType.asEnum())) {
            this.depositAmount = new Money(currency);
            this.withdrawlAmount = new Money(currency);
            this.interestAmount = amount;
        } else {
            this.depositAmount = new Money(currency);
            this.withdrawlAmount = new Money(currency);
            this.interestAmount = new Money(currency);
        }
    }

    @Override
    protected AccountTrxnEntity generateReverseTrxn(PersonnelBO loggedInUser, String adjustmentComment) throws AccountException {
        SavingsTrxnDetailEntity reverseAccntTrxn = null;
        Money balAfterAdjust = null;

        if (isSavingsDeposit()) {

            balAfterAdjust = this.balance.subtract(this.depositAmount);
            reverseAccntTrxn = SavingsTrxnDetailEntity.savingsDepositAdjustment(getAccountPayment(), getCustomer(),
                    balAfterAdjust, this.depositAmount.negate(), loggedInUser,
                    getDueDate(), getActionDate(), new DateTime().toDate(), adjustmentComment, this);

        } else if (isSavingsWithdrawal()) {

            balAfterAdjust = this.balance.add(this.withdrawlAmount);
            reverseAccntTrxn = SavingsTrxnDetailEntity.savingsWithdrawalAdjustment(getAccountPayment(), getCustomer(),
                    balAfterAdjust, this.withdrawlAmount.negate(), loggedInUser,
                    getDueDate(), getActionDate(), new DateTime().toDate(), adjustmentComment, this);
        } else {
            reverseAccntTrxn = SavingsTrxnDetailEntity.savingsAdjustment(getAccountPayment(), getCustomer(),
                    balAfterAdjust, getAmount().negate(), loggedInUser,
                    getDueDate(), getActionDate(), new DateTime().toDate(), adjustmentComment, this);
        }
        return reverseAccntTrxn;
    }

    public Money getDepositAmount() {
        return depositAmount;
    }

    public Money getWithdrawlAmount() {
        return withdrawlAmount;
    }

    public Money getBalance() {
        return balance;
    }

    public Money getInterestAmount() {
        return interestAmount;
    }
}