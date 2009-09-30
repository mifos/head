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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.persistence.SavingsPersistence;
import org.mifos.application.accounts.savings.util.helpers.SavingsAccountView;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryView;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.helpers.Money;

public class SavingsAccountAssembler {
    private static final MifosLogger logger = MifosLogManager.getLogger(SavingsAccountAssembler.class.getName());

    private final SavingsPersistence savingsPersistence;
    private final CustomerPersistence customerPersistence;

    public SavingsAccountAssembler(final SavingsPersistence savingsPersistence,
            final CustomerPersistence customerPersistence) {
        this.savingsPersistence = savingsPersistence;
        this.customerPersistence = customerPersistence;
    }

    public List<SavingsBO> fromDto(final List<CollectionSheetEntryView> collectionSheeetEntryViews,
            final AccountPaymentEntity payment, final List<String> failedSavingsDepositAccountNums,
            final List<String> failedSavingsWithdrawalNums) {

        final String receiptNumber = payment.getReceiptNumber();
        final Date receiptDate = payment.getReceiptDate();
        final PaymentTypeEntity paymentType = payment.getPaymentType();
        final Date paymentDate = payment.getPaymentDate();
        final PersonnelBO user = payment.getCreatedByUser();

        final Map<Integer, List<SavingsAccountView>> savingAccountsViewsByCustomerId = new HashMap<Integer, List<SavingsAccountView>>();

        populateMapOfSavingAccountViewsByCustomerId(collectionSheeetEntryViews, savingAccountsViewsByCustomerId);

        final Map<Integer, SavingsBO> savingsAccountsByAccountId = new HashMap<Integer, SavingsBO>();

        for (Integer customerId : savingAccountsViewsByCustomerId.keySet()) {
            final List<SavingsAccountView> allSavingsAssociatedWithCustomer = savingAccountsViewsByCustomerId
                    .get(customerId);

            try {
                final CustomerBO payingCustomer = customerPersistence.getCustomer(customerId);

                for (SavingsAccountView savingAccountView : allSavingsAssociatedWithCustomer) {

                    final Money amountToDeposit = new Money(
                            Configuration.getInstance().getSystemConfig().getCurrency(), savingAccountView
                                    .getDepositAmountEntered());
                    final Money amountToWithdraw = new Money(Configuration.getInstance().getSystemConfig()
                            .getCurrency(), savingAccountView.getWithDrawalAmountEntered());

                    final Integer accountId = savingAccountView.getAccountId();
                    boolean storeAccountForSavingLater = false;
                    SavingsBO account;
                    if (savingsAccountsByAccountId.containsKey(accountId)) {
                        account = savingsAccountsByAccountId.get(accountId);
                    } else {
                        account = savingsPersistence.findById(accountId);
                    }

                    if (amountToDeposit.getAmount() != null) {

                        final AccountPaymentEntity accountDeposit = new AccountPaymentEntity(account, amountToDeposit,
                                receiptNumber, receiptDate, paymentType, paymentDate);
                        accountDeposit.setCreatedByUser(user);

                        try {
                            account.deposit(accountDeposit, payingCustomer);
                            storeAccountForSavingLater = true;
                        } catch (AccountException e) {
                            logger.warn("Savings deposit on account [" + account.getAccountId()
                                    + "] failed. Account changes will not be persisted due to: " + e.getMessage());
                            failedSavingsDepositAccountNums.add(account.getAccountId().toString());
                        }
                    }

                    if (amountToWithdraw.getAmount() != null) {
                        final AccountPaymentEntity accountWithdrawal = new AccountPaymentEntity(account,
                                amountToWithdraw, receiptNumber, receiptDate, paymentType, paymentDate);
                        accountWithdrawal.setCreatedByUser(user);

                        try {
                            account.withdraw(accountWithdrawal, payingCustomer);
                            storeAccountForSavingLater = true;
                        } catch (AccountException e) {
                            logger.warn("Savings withdrawl on account [" + account.getAccountId()
                                    + "] failed. Account changes will not be persisted due to: " + e.getMessage());
                            failedSavingsWithdrawalNums.add(account.getAccountId().toString());
                        }
                    }

                    if (storeAccountForSavingLater) {
                        savingsAccountsByAccountId.put(accountId, account);
                    }
                }
            } catch (PersistenceException e) {
                throw new MifosRuntimeException(e);
            }
        }

        final List<SavingsBO> savingsList = new ArrayList<SavingsBO>();
        final Set<Entry<Integer, SavingsBO>> savingsAccounts = savingsAccountsByAccountId.entrySet();
        for (Entry<Integer, SavingsBO> entry : savingsAccounts) {
            savingsList.add(entry.getValue());
        }
        return savingsList;
    }

    private void populateMapOfSavingAccountViewsByCustomerId(
            final List<CollectionSheetEntryView> collectionSheeetEntryViews,
            final Map<Integer, List<SavingsAccountView>> savingAccountsViewsByCustomerId) {
        for (CollectionSheetEntryView parent : collectionSheeetEntryViews) {
            final Integer customerId = parent.getCustomerDetail().getCustomerId();

            if (savingAccountsViewsByCustomerId.containsKey(customerId)) {
                final List<SavingsAccountView> savingsAccountsViews = savingAccountsViewsByCustomerId.get(customerId);
                savingsAccountsViews.addAll(parent.getSavingsAccountDetails());
            } else {
                final List<SavingsAccountView> savingsAccountsViews = new ArrayList<SavingsAccountView>(parent
                        .getSavingsAccountDetails());
                savingAccountsViewsByCustomerId.put(customerId, savingsAccountsViews);
            }
        }
    }

}