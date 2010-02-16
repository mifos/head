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

import java.util.Date;

import org.mifos.accounts.business.AccountActionEntity;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.savings.persistence.SavingsPersistence;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.framework.util.helpers.Money;

/**
 *
 */
public class SavingsTransactionActivityHelperImpl implements SavingsTransactionActivityHelper {

    @Override
    public SavingsActivityEntity createSavingsActivityForDeposit(final PersonnelBO createdBy,
            final Money amountToDeposit, final Money savingsBalance, final Date createdDate,
            final SavingsBO savingsAccount) {

        final AccountActionEntity savingsAccountDepositAction = new AccountActionEntity(
                AccountActionTypes.SAVINGS_DEPOSIT);
        return new SavingsActivityEntity(createdBy, savingsAccountDepositAction, amountToDeposit, savingsBalance,
                createdDate, savingsAccount);
    }

    @Override
    public SavingsActivityEntity createSavingsActivityForWithdrawal(final AccountPaymentEntity payment,
            final Money savingsBalance, final SavingsBO savingsBO) {

        final AccountActionEntity savingsAccountWithdrawalAction = new AccountActionEntity(
                AccountActionTypes.SAVINGS_WITHDRAWAL);
        final SavingsActivityEntity savingsActivity = new SavingsActivityEntity(payment.getCreatedByUser(),
                savingsAccountWithdrawalAction, payment.getAmount(), savingsBalance, payment.getPaymentDate(),
                savingsBO);
        return savingsActivity;
    }

    @Override
    public SavingsTrxnDetailEntity createSavingsTrxnForDeposit(final AccountPaymentEntity payment, final Money amount,
            final CustomerBO payingCustomer, final SavingsScheduleEntity savingsInstallment, final Money savingsBalance) {

        final Date transactionDate = payment.getPaymentDate();
        final PersonnelBO createdBy = payment.getCreatedByUser();

        Date dueDate = transactionDate;
        Short installmentNumber = null;
        if (savingsInstallment != null) {
            dueDate = savingsInstallment.getActionDate();
            installmentNumber = savingsInstallment.getInstallmentId();
        }

        final SavingsTrxnDetailEntity accountTrxnBO = new SavingsTrxnDetailEntity(payment, payingCustomer,
                AccountActionTypes.SAVINGS_DEPOSIT, amount, savingsBalance, createdBy, dueDate, transactionDate,
                installmentNumber, "", new SavingsPersistence());

        return accountTrxnBO;
    }

    @Override
    public SavingsTrxnDetailEntity createSavingsTrxnForWithdrawal(final AccountPaymentEntity payment,
            final Money amountToWithdraw, final CustomerBO payingCustomer, final Money accountBalance) {

        final Date transactionDate = payment.getPaymentDate();
        final PersonnelBO createdBy = payment.getCreatedByUser();

        return new SavingsTrxnDetailEntity(payment, payingCustomer, AccountActionTypes.SAVINGS_WITHDRAWAL,
                amountToWithdraw, accountBalance, createdBy, transactionDate, transactionDate, null, "",
                new SavingsPersistence());
    }
}
