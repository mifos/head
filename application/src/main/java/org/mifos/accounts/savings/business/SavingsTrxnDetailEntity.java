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

package org.mifos.accounts.savings.business;

import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.business.AccountTrxnEntity;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.savings.util.helpers.SavingsHelper;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.framework.persistence.Persistence;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.Money;

public class SavingsTrxnDetailEntity extends AccountTrxnEntity {

    private final Money depositAmount;

    private final Money withdrawlAmount;

    private final Money interestAmount;

    private final Money balance;

    protected SavingsTrxnDetailEntity() {
        depositAmount = null;
        withdrawlAmount = null;
        interestAmount = null;
        balance = null;
    }

    public SavingsTrxnDetailEntity(Money depositAmount, Money withdrawlAmount, Money interestAmount, Money balance) {
        this.depositAmount = depositAmount;
        this.withdrawlAmount = withdrawlAmount;
        this.interestAmount = interestAmount;
        this.balance = balance;
    }

    public SavingsTrxnDetailEntity(AccountPaymentEntity accountPaymentEntity, CustomerBO customer,
            AccountActionTypes accountActionType, Money amount, Money balance, PersonnelBO createdBy,
            java.util.Date dueDate, java.util.Date transactionDate, Short installmentId, String comment,
            Persistence persistence) {
        this(accountPaymentEntity, customer,
            accountActionType, amount, balance, createdBy,
            dueDate, transactionDate, installmentId, comment,
            persistence, new DateTimeService().getCurrentJavaDateTime());
    }

    public SavingsTrxnDetailEntity(AccountPaymentEntity accountPaymentEntity, CustomerBO customer,
            AccountActionTypes accountActionType, Money amount, Money balance, PersonnelBO createdBy,
            java.util.Date dueDate, java.util.Date actionDate, Short installmentId, String comment,
            Persistence persistence, java.util.Date postingDate) {
        super(accountPaymentEntity, accountActionType, installmentId, dueDate, createdBy, customer, actionDate,
                amount, comment, null, persistence, postingDate);
        this.balance = balance;
        MifosCurrency currency = accountPaymentEntity.getAccount().getCurrency();
        if (accountActionType.equals(AccountActionTypes.SAVINGS_WITHDRAWAL)) {
            this.depositAmount = new Money(currency);
            this.withdrawlAmount = amount;
            this.interestAmount = new Money(currency);
        } else if (accountActionType.equals(AccountActionTypes.SAVINGS_DEPOSIT)) {
            this.depositAmount = amount;
            this.withdrawlAmount = new Money(currency);
            this.interestAmount = new Money(currency);
        } else if (accountActionType.equals(AccountActionTypes.SAVINGS_INTEREST_POSTING)) {
            this.depositAmount = new Money(currency);
            this.withdrawlAmount = new Money(currency);
            this.interestAmount = amount;
        } else {
            this.depositAmount = new Money(currency);
            this.withdrawlAmount = new Money(currency);
            this.interestAmount = new Money(currency);
        }
    }

    public SavingsTrxnDetailEntity(AccountPaymentEntity accountPaymentEntity, AccountActionTypes accountActionType,
            Money amount, Money balance, PersonnelBO createdBy, CustomerBO customer, java.util.Date dueDate,
            java.util.Date transactionDate, String comments, AccountTrxnEntity relatedTrxn, Persistence persistence) {
        super(accountPaymentEntity, accountActionType, null, dueDate, createdBy, customer, transactionDate, amount,
                comments, relatedTrxn, persistence);
        this.balance = balance;
        MifosCurrency currency = accountPaymentEntity.getAccount().getCurrency();
        Short lastAccountAction = new SavingsHelper().getPaymentActionType(accountPaymentEntity);
        if (lastAccountAction.equals(AccountActionTypes.SAVINGS_WITHDRAWAL.getValue())) {
            this.depositAmount = new Money(currency);
            this.withdrawlAmount = amount;
            this.interestAmount = new Money(currency);
        } else if (lastAccountAction.equals(AccountActionTypes.SAVINGS_DEPOSIT.getValue())) {
            this.depositAmount = amount;
            this.withdrawlAmount = new Money(currency);
            this.interestAmount = new Money(currency);
        } else if (lastAccountAction.equals(AccountActionTypes.SAVINGS_INTEREST_POSTING.getValue())) {
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
    protected AccountTrxnEntity generateReverseTrxn(PersonnelBO loggedInUser, String adjustmentComment)
    throws AccountException {
        MasterPersistence masterPersistence = new MasterPersistence();
        SavingsTrxnDetailEntity reverseAccntTrxn = null;
        Money balAfterAdjust = null;
        if (getAccountActionEntity().getId().equals(AccountActionTypes.SAVINGS_DEPOSIT.getValue())) {
            balAfterAdjust = getBalance().subtract(getDepositAmount());
            reverseAccntTrxn = new SavingsTrxnDetailEntity(getAccountPayment(),
                    AccountActionTypes.SAVINGS_ADJUSTMENT, getDepositAmount().negate(),
                    balAfterAdjust, loggedInUser, getCustomer(), getDueDate(), getActionDate(), adjustmentComment,
                    this, masterPersistence);
        } else if (getAccountActionEntity().getId().equals(AccountActionTypes.SAVINGS_WITHDRAWAL.getValue())) {
            balAfterAdjust = getBalance().add(getWithdrawlAmount());
            reverseAccntTrxn = new SavingsTrxnDetailEntity(getAccountPayment(),
                    AccountActionTypes.SAVINGS_ADJUSTMENT, getWithdrawlAmount().negate(),
                    balAfterAdjust, loggedInUser, getCustomer(), getDueDate(), getActionDate(), adjustmentComment,
                    this, masterPersistence);

        } else {
            reverseAccntTrxn = new SavingsTrxnDetailEntity(getAccountPayment(),
                    AccountActionTypes.SAVINGS_ADJUSTMENT, getAmount().negate(),
                    balAfterAdjust, getPersonnel(), getCustomer(), getDueDate(), getActionDate(),
                    adjustmentComment, this, masterPersistence);
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
