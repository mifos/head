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
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.persistance.LoanPersistence;
import org.mifos.application.accounts.loan.util.helpers.LoanAccountView;
import org.mifos.application.accounts.loan.util.helpers.LoanAccountsProductView;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.LocalizationConverter;
import org.mifos.framework.util.helpers.Money;

/**
 *
 */
public class LoanAccountAssembler {

    private static final MifosLogger logger = MifosLogManager.getLogger(LoanAccountAssembler.class.getName());
    private final LoanPersistence loanPersistence;

    public LoanAccountAssembler(LoanPersistence loanPersistence) {
        this.loanPersistence = loanPersistence;
    }

    public List<LoanBO> fromDto(final List<LoanAccountsProductView> loanAccountProductViews,
            final AccountPaymentEntity payment, final List<String> failedLoanDisbursementAccountNumbers,
            final List<String> failedLoanRepaymentAccountNumbers) {

        final List<LoanBO> loans = new ArrayList<LoanBO>();

        for (LoanAccountsProductView loanAccountsProductView : loanAccountProductViews) {

            for (LoanAccountView accountView : loanAccountsProductView.getLoanAccountViews()) {
                final Integer accountId = accountView.getAccountId();
                final LoanBO account = findLoanAccountByIdWithLoanSchedulesInitialized(accountId);
                final String globalAccountNum = account.getGlobalAccountNum();

                if (accountView.isDisbursalAccount()) {

                    final Double amount = getDoubleValue(loanAccountsProductView.getDisBursementAmountEntered());
                    if ((amount != null) && (amount.doubleValue() > 0)) {
                        try {
                            final Money amountToDisburse = new Money();

                            final AccountPaymentEntity accountDisbursalPayment = new AccountPaymentEntity(account,
                                    amountToDisburse, payment.getReceiptNumber(), payment.getReceiptDate(),
                                    new PaymentTypeEntity(payment.getPaymentType().getId()), payment.getPaymentDate());
                            accountDisbursalPayment.setCreatedByUser(payment.getCreatedByUser());

                            account.disburseLoan(accountDisbursalPayment, null);
                            loans.add(account);
                        } catch (AccountException ae) {
                            logger.warn("Disbursal of loan on account [" + globalAccountNum
                                    + "] failed. Account changes will not be persisted due to: " + ae.getMessage());
                            failedLoanDisbursementAccountNumbers.add(accountId.toString());
                        }
                    }
                } else {

                    final Double amount = getDoubleValue(loanAccountsProductView.getEnteredAmount());
                    if ((amount != null) && (amount > 0.0)) {
                        Money enteredAmount;
                        if (loanAccountsProductView.getLoanAccountViews().size() > 1) {
                            enteredAmount = new Money(Configuration.getInstance().getSystemConfig().getCurrency(),
                                    String.valueOf(accountView.getTotalAmountDue()));
                        } else {
                            enteredAmount = new Money(Configuration.getInstance().getSystemConfig().getCurrency(),
                                    loanAccountsProductView.getEnteredAmount());
                        }

                        try {
                            final AccountPaymentEntity accountDisbursalPayment = new AccountPaymentEntity(account,
                                    enteredAmount, payment.getReceiptNumber(), payment.getReceiptDate(),
                                    new PaymentTypeEntity(payment.getPaymentType().getId()), payment.getPaymentDate());
                            accountDisbursalPayment.setCreatedByUser(payment.getCreatedByUser());

                            PaymentData paymentData = PaymentData.createPaymentData(enteredAmount, payment
                                    .getCreatedByUser(), payment.getPaymentType().getId(), payment.getPaymentDate());
                            paymentData.setReceiptDate(payment.getReceiptDate());
                            paymentData.setReceiptNum(payment.getReceiptNumber());

                            account.applyPayment(paymentData, false);
                            loans.add(account);
                        } catch (AccountException ae) {
                            logger.warn("Payment of loan on account [" + globalAccountNum
                                    + "] failed. Account changes will not be persisted due to: " + ae.getMessage());
                            failedLoanRepaymentAccountNumbers.add(accountId.toString());
                        }
                    }
                }
            }

        }

        return loans;
    }

    private LoanBO findLoanAccountByIdWithLoanSchedulesInitialized(final Integer loanId) {
        try {
            return loanPersistence.getLoanAccountWithAccountActionsInitialized(loanId);
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    private Double getDoubleValue(final String str) {
        return StringUtils.isNotBlank(str) ? new LocalizationConverter().getDoubleValueForCurrentLocale(str) : null;
    }
}
