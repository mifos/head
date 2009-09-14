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
package org.mifos.application.servicefacade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.persistence.SavingsPersistence;
import org.mifos.application.accounts.savings.util.helpers.SavingsAccountView;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryGridDto;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryView;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.helpers.Money;

/**
 *
 */
public class SavingsAccountAssembler {

    private final SavingsPersistence savingsPersistence;
    private final PersonnelPersistence personnelPersistence;

    public SavingsAccountAssembler(final SavingsPersistence savingsPersistence,
            final PersonnelPersistence personnelPersistence) {
        this.savingsPersistence = savingsPersistence;
        this.personnelPersistence = personnelPersistence;
    }

    public List<SavingsBO> fromDto(final List<CollectionSheetEntryView> collectionSheeetEntryViews,
            final CollectionSheetEntryGridDto previousCollectionSheetEntryDto, final Short userId,
            final List<String> failedSavingsDepositAccountNums, final List<String> failedSavingsWithdrawalNums) {
        
        final List<SavingsBO> savingsAccounts = new ArrayList<SavingsBO>();
        for (CollectionSheetEntryView parent : collectionSheeetEntryViews) {

            for (SavingsAccountView savingAccountView : parent.getSavingsAccountDetails()) {

                final Money amountToDeposit = new Money(Configuration.getInstance().getSystemConfig().getCurrency(),
                        savingAccountView.getDepositAmountEntered());
                final Money amountToWithdraw = new Money(Configuration.getInstance().getSystemConfig().getCurrency(),
                        savingAccountView.getWithDrawalAmountEntered());

                final String receiptNumber = previousCollectionSheetEntryDto.getReceiptId();
                final Date receiptDate = previousCollectionSheetEntryDto.getReceiptDate();
                final PaymentTypeEntity paymentType = new PaymentTypeEntity(previousCollectionSheetEntryDto
                        .getPaymentType().getId());
                final Date paymentDate = previousCollectionSheetEntryDto.getTransactionDate();

                final Integer accountId = savingAccountView.getAccountId();
                boolean storeAccountForSavingLater = false;
                try {
                    final SavingsBO account = savingsPersistence.findById(accountId);
                    final PersonnelBO user = personnelPersistence.findPersonnelById(userId);

                    if (amountToDeposit.getAmount() != null
                            && amountToDeposit.getAmountDoubleValue() > Double.valueOf("0.0")) {

                        final AccountPaymentEntity accountDeposit = new AccountPaymentEntity(account, amountToDeposit,
                                receiptNumber, receiptDate, paymentType, paymentDate);
                        accountDeposit.setCreatedByUser(user);

                        try {
                            account.deposit(accountDeposit);
                            storeAccountForSavingLater = true;
                        } catch (AccountException e) {
                            failedSavingsDepositAccountNums.add(account.getGlobalAccountNum());
                        }
                    }

                    if (amountToWithdraw.getAmount() != null
                            && amountToWithdraw.getAmountDoubleValue() > Double.valueOf("0.0")) {
                        final AccountPaymentEntity accountWithdrawal = new AccountPaymentEntity(account,
                                amountToWithdraw, receiptNumber, receiptDate, paymentType, paymentDate);
                        accountWithdrawal.setCreatedByUser(user);

                        try {
                            account.withdraw(accountWithdrawal);
                            storeAccountForSavingLater = true;
                        } catch (AccountException e) {
                            failedSavingsWithdrawalNums.add(account.getGlobalAccountNum());
                        }
                    }

                    if (storeAccountForSavingLater) {
                        savingsAccounts.add(account);
                    }

                } catch (PersistenceException pe) {
                    throw new MifosRuntimeException(pe);
                }
            }
        }
        
        return savingsAccounts;
    }

}