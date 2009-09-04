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

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.util.helpers.CustomerAccountPaymentData;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryInstallmentView;
import org.mifos.application.customer.business.CustomerAccountBO;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerAccountView;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.LocalizationConverter;
import org.mifos.framework.util.helpers.Money;

/**
 *
 */
public class CustomerAccountAssembler {

    private static final MifosLogger logger = MifosLogManager.getLogger(CustomerAccountAssembler.class.getName());
    private final CustomerPersistence customerPersistence;

    public CustomerAccountAssembler(final CustomerPersistence customerPersistence) {
        this.customerPersistence = customerPersistence;
    }

    public List<AccountBO> fromDto(final List<CustomerAccountView> customerAccountViews,
            final AccountPaymentEntity payment, final List<String> failedCustomerAccountPaymentNums) {

        final List<AccountBO> customerAccountList = new ArrayList<AccountBO>();
        for (CustomerAccountView customerAccountView : customerAccountViews) {

            if (null != customerAccountView) {

                final String amount = customerAccountView.getCustomerAccountAmountEntered();
                if (null != amount
                        && !LocalizationConverter.getInstance().getDoubleValueForCurrentLocale(amount).equals(0.0)) {

                    final Integer accountId = customerAccountView.getAccountId();
                    
                    final PaymentData accountPaymentDataView = getCustomerAccountPaymentDataView(customerAccountView
                            .getAccountActionDates(), customerAccountView.getTotalAmountDue(), payment);

                    String globalCustomerAccountNum = "Unknown";
                    try {
                        final CustomerAccountBO account = findCustomerAccountByIdWithLoanSchedulesInitialized(accountId);
                        globalCustomerAccountNum = account.getGlobalAccountNum();
                        account.applyPayment(accountPaymentDataView, true);
                        customerAccountList.add(account);
                    } catch (AccountException ae) {
                        logger.warn("Payment of loan on account [" + globalCustomerAccountNum
                                + "] failed. Account changes will not be persisted due to: " + ae.getMessage());
                        failedCustomerAccountPaymentNums.add(globalCustomerAccountNum);
                    }
                }
            }
        }

        return customerAccountList;
    }
    
    private PaymentData getCustomerAccountPaymentDataView(final List<CollectionSheetEntryInstallmentView> accountActions,
            final Money totalAmount,
            final AccountPaymentEntity payment) {

        final PersonnelBO personnel = payment.getCreatedByUser();
        final String receiptNum = payment.getReceiptNumber();
        final Short paymentId = payment.getPaymentType().getId();
        final Date receiptDate = payment.getReceiptDate();
        final Date transactionDate = payment.getPaymentDate();

        PaymentData paymentData = PaymentData.createPaymentData(totalAmount, personnel, paymentId, transactionDate);
        paymentData.setRecieptDate(receiptDate);
        paymentData.setRecieptNum(receiptNum);

        for (CollectionSheetEntryInstallmentView actionDate : accountActions) {
            CustomerAccountPaymentData customerAccountPaymentData = new CustomerAccountPaymentData(actionDate);
            paymentData.addAccountPaymentData(customerAccountPaymentData);
        }

        return paymentData;
    }

    private CustomerAccountBO findCustomerAccountByIdWithLoanSchedulesInitialized(final Integer customerAccountId) {
        try {
            return customerPersistence.getCustomerAccountWithAccountActionsInitialized(customerAccountId);
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }
}
