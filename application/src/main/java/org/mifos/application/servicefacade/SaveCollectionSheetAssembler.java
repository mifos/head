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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.persistance.ClientAttendanceDao;
import org.mifos.accounts.loan.persistance.LoanPersistence;
import org.mifos.accounts.persistence.AccountPersistence;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.persistence.SavingsPersistence;
import org.mifos.accounts.util.helpers.PaymentData;
import org.mifos.customers.business.CustomerAccountBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.client.business.ClientAttendanceBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.client.persistence.ClientPersistence;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;

/**
 * Methods to assemble mifos business model from Save Collection Sheet Dto's
 * 
 */
public class SaveCollectionSheetAssembler {
    private static final MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER);

    private final CustomerPersistence customerPersistence = new CustomerPersistence();
    private final ClientPersistence clientPersistence = new ClientPersistence();
    private final PersonnelPersistence personnelPersistence = new PersonnelPersistence();

    private final ClientAttendanceDao clientAttendanceDao;
    private final LoanPersistence loanPersistence;
    private final AccountPersistence accountPersistence;
    private final SavingsPersistence savingsPersistence;

    public SaveCollectionSheetAssembler(final ClientAttendanceDao clientAttendanceDao,
            final LoanPersistence loanPersistence, final AccountPersistence accountPersistence,
            final SavingsPersistence savingsPersistence) {
        this.clientAttendanceDao = clientAttendanceDao;
        this.loanPersistence = loanPersistence;
        this.accountPersistence = accountPersistence;
        this.savingsPersistence = savingsPersistence;
    }

    public List<SavingsBO> savingsAccountAssemblerFromDto(
            final List<SaveCollectionSheetCustomerDto> saveCollectionSheetCustomers,
            final AccountPaymentEntity payment, final List<String> failedSavingsDepositAccountNums,
            final List<String> failedSavingsWithdrawalNums) {

        final List<SavingsBO> savingsList = new ArrayList<SavingsBO>();

        for (SaveCollectionSheetCustomerDto saveCollectionSheetCustomer : saveCollectionSheetCustomers) {

            buildModelForSavingsAccounts(saveCollectionSheetCustomer.getSaveCollectionSheetCustomerSavings(), payment,
                    failedSavingsDepositAccountNums, failedSavingsWithdrawalNums, saveCollectionSheetCustomer
                            .getCustomerId(), savingsList);

            buildModelForSavingsAccounts(saveCollectionSheetCustomer.getSaveCollectionSheetCustomerIndividualSavings(),
                    payment, failedSavingsDepositAccountNums, failedSavingsWithdrawalNums, saveCollectionSheetCustomer
                            .getCustomerId(), savingsList);

        }

        return savingsList;
    }

    private void buildModelForSavingsAccounts(
            final List<SaveCollectionSheetCustomerSavingDto> saveCollectionSheetCustomerSavings,
            final AccountPaymentEntity payment, final List<String> failedSavingsDepositAccountNums,
            final List<String> failedSavingsWithdrawalNums, final Integer customerId, final List<SavingsBO> savingsList) {

        if (null != saveCollectionSheetCustomerSavings && saveCollectionSheetCustomerSavings.size() > 0) {
            final String receiptNumber = payment.getReceiptNumber();
            final Date receiptDate = payment.getReceiptDate();
            final PaymentTypeEntity paymentType = payment.getPaymentType();
            final Date paymentDate = payment.getPaymentDate();
            final PersonnelBO user = payment.getCreatedByUser();

            for (SaveCollectionSheetCustomerSavingDto saveCollectionSheetCustomerSaving : saveCollectionSheetCustomerSavings) {

                final BigDecimal amountToDeposit = saveCollectionSheetCustomerSaving.getTotalDeposit();
                final BigDecimal amountToWithdraw = saveCollectionSheetCustomerSaving.getTotalWithdrawal();

                Boolean isDeposit = isPositiveAmountEntered(amountToDeposit);
                Boolean isWithdrawal = isPositiveAmountEntered(amountToWithdraw);

                if (isDeposit || isWithdrawal) {
                    boolean storeAccountForSavingLater = false;
                    try {
                        final CustomerBO payingCustomer = customerPersistence.getCustomer(customerId);
                        final SavingsBO account = savingsPersistence.findById(saveCollectionSheetCustomerSaving
                                .getAccountId());

                        if (isDeposit) {
                            final AccountPaymentEntity accountDeposit = new AccountPaymentEntity(account, new Money(
                                    Money.getDefaultCurrency(), amountToDeposit.toString()), receiptNumber,
                                    receiptDate, paymentType, paymentDate);
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

                        if (isWithdrawal) {
                            final AccountPaymentEntity accountWithdrawal = new AccountPaymentEntity(account, new Money(
                                    Money.getDefaultCurrency(), amountToWithdraw.toString()), receiptNumber,
                                    receiptDate, paymentType, paymentDate);
                            accountWithdrawal.setCreatedByUser(user);

                            try {
                                account.withdraw(accountWithdrawal, payingCustomer);
                                storeAccountForSavingLater = true;
                            } catch (AccountException e) {
                                logger.warn("Savings withdrawal on account [" + account.getAccountId()
                                        + "] failed. Account changes will not be persisted due to: " + e.getMessage());
                                failedSavingsWithdrawalNums.add(account.getAccountId().toString());
                            }
                        }

                        if (storeAccountForSavingLater) {
                            if (!savingsList.contains(account)) {
                                savingsList.add(account);
                            }
                        }
                    } catch (PersistenceException e) {
                        throw new MifosRuntimeException(e);
                    }
                }
            }
        }
    }

    private Boolean isPositiveAmountEntered(final BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public List<LoanBO> loanAccountAssemblerFromDto(
            final List<SaveCollectionSheetCustomerDto> saveCollectionSheetCustomers,
            final AccountPaymentEntity payment, final List<String> failedLoanDisbursementAccountNumbers,
            final List<String> failedLoanRepaymentAccountNumbers) {

        final List<LoanBO> loans = new ArrayList<LoanBO>();

        for (SaveCollectionSheetCustomerDto saveCollectionSheetCustomer : saveCollectionSheetCustomers) {
            if (saveCollectionSheetCustomer.getSaveCollectionSheetCustomerLoans() != null
                    && saveCollectionSheetCustomer.getSaveCollectionSheetCustomerLoans().size() > 0) {

                for (SaveCollectionSheetCustomerLoanDto saveCollectionSheetCustomerLoan : saveCollectionSheetCustomer
                        .getSaveCollectionSheetCustomerLoans()) {
                    final Integer accountId = saveCollectionSheetCustomerLoan.getAccountId();
                    final LoanBO account = findLoanAccountById(accountId);
                    final String globalAccountNum = account.getGlobalAccountNum();

                    final BigDecimal disbursalAmount = saveCollectionSheetCustomerLoan.getTotalDisbursement();
                    if (null != disbursalAmount && disbursalAmount.compareTo(BigDecimal.ZERO) > 0) {

                        try {
                            final AccountPaymentEntity accountDisbursalPayment = new AccountPaymentEntity(account,
                                    new Money(account.getCurrency(), disbursalAmount.toString()), payment
                                            .getReceiptNumber(), payment.getReceiptDate(), payment.getPaymentType(),
                                    payment.getPaymentDate());
                            accountDisbursalPayment.setCreatedByUser(payment.getCreatedByUser());

                            account.disburseLoan(accountDisbursalPayment);
                            loans.add(account);
                        } catch (AccountException ae) {
                            logger.warn("Disbursal of loan on account [" + globalAccountNum
                                    + "] failed. Account changes will not be persisted due to: " + ae.getMessage());
                            failedLoanDisbursementAccountNumbers.add(globalAccountNum);
                        }

                    } else {
                        final BigDecimal loanPaymentAmount = saveCollectionSheetCustomerLoan.getTotalLoanPayment();

                        if (loanPaymentAmount != null && loanPaymentAmount.compareTo(BigDecimal.ZERO) > 0) {
                            try {
                                final PaymentData paymentData = getCustomerAccountPaymentDataView(new Money(account
                                        .getCurrency(), loanPaymentAmount.toString()), payment);
                                account.applyPayment(paymentData, false);
                                loans.add(account);
                            } catch (AccountException ae) {
                                logger.warn("Loan repayment on account [" + globalAccountNum
                                        + "] failed. Account changes will not be persisted due to: " + ae.getMessage());
                                failedLoanRepaymentAccountNumbers.add(globalAccountNum);
                            }
                        }
                    }
                }
            }
        }

        return loans;
    }

    public List<AccountBO> customerAccountAssemblerFromDto(
            final List<SaveCollectionSheetCustomerDto> saveCollectionSheetCustomers,
            final AccountPaymentEntity payment, final List<String> failedCustomerAccountPaymentNums) {

        final List<AccountBO> customerAccountList = new ArrayList<AccountBO>();
        for (SaveCollectionSheetCustomerDto saveCollectionSheetCustomer : saveCollectionSheetCustomers) {

            SaveCollectionSheetCustomerAccountDto saveCollectionSheetCustomerAccount = saveCollectionSheetCustomer
                    .getSaveCollectionSheetCustomerAccount();
            if (null != saveCollectionSheetCustomerAccount) {
                final BigDecimal amount = saveCollectionSheetCustomerAccount.getTotalCustomerAccountCollectionFee();
                if (null != amount && amount.compareTo(BigDecimal.ZERO) > 0) {

                    final PaymentData accountPaymentDataView = getCustomerAccountPaymentDataView(new Money(Money
                            .getDefaultCurrency(), amount.toString()), payment);

                    final Integer accountId = saveCollectionSheetCustomer.getSaveCollectionSheetCustomerAccount()
                            .getAccountId();

                    try {
                        final CustomerAccountBO account = findCustomerAccountById(accountId);
                        account.applyPayment(accountPaymentDataView, false);
                        customerAccountList.add(account);
                    } catch (AccountException ae) {
                        logger.warn("Payment of collection/fee on account [" + accountId
                                + "] failed. Account changes will not be persisted due to: " + ae.getMessage());
                        failedCustomerAccountPaymentNums.add(accountId.toString());
                    }
                }
            }
        }

        return customerAccountList;
    }

    private PaymentData getCustomerAccountPaymentDataView(final Money totalAmount, final AccountPaymentEntity payment) {

        PaymentData paymentData = PaymentData.createPaymentData(totalAmount, payment.getCreatedByUser(), payment
                .getPaymentType().getId(), payment.getPaymentDate());
        paymentData.setReceiptDate(payment.getReceiptDate());
        paymentData.setReceiptNum(payment.getReceiptNumber());

        return paymentData;
    }

    public AccountPaymentEntity accountPaymentAssemblerFromDto(final LocalDate transactionDate,
            final Short paymentType, final String receiptId, final LocalDate receiptDate, final Short userId) {

        final PersonnelBO user = personnelPersistence.findPersonnelById(userId);
        final AccountPaymentEntity payment = new AccountPaymentEntity(null, new Money(Money.getDefaultCurrency()),
                receiptId, DateUtils.getDateFromLocalDate(receiptDate), new PaymentTypeEntity(paymentType), DateUtils
                        .getDateFromLocalDate(transactionDate));
        payment.setCreatedByUser(user);

        return payment;
    }

    public List<ClientAttendanceBO> clientAttendanceAssemblerfromDto(
            final List<SaveCollectionSheetCustomerDto> saveCollectionSheetCustomers, final LocalDate transactionDate,
            final Short branchId, final String searchId) {

        List<ClientAttendanceBO> clientAttendanceList = null;

        try {
            clientAttendanceList = clientAttendanceDao.findClientAttendance(branchId, searchId, transactionDate);
            for (SaveCollectionSheetCustomerDto saveCollectionSheetCustomer : saveCollectionSheetCustomers) {
                ClientBO client = clientPersistence.getClient(saveCollectionSheetCustomer.getCustomerId());

                if (null != client) {
                    ClientAttendanceBO clientAttendance = findClientAttendance(clientAttendanceList, client);
                    if (clientAttendance == null) {
                        clientAttendance = new ClientAttendanceBO();
                        clientAttendance.setCustomer(client);
                        clientAttendance.setMeetingDate(DateUtils.getDateFromLocalDate(transactionDate));
                    }
                    clientAttendance.setAttendance(saveCollectionSheetCustomer.getAttendanceId());
                    clientAttendanceList.add(clientAttendance);
                }
            }
        } catch (PersistenceException e) {
            throw new MifosRuntimeException("Failure assembling client attendance list.", e);
        }
        return clientAttendanceList;

    }

    private ClientAttendanceBO findClientAttendance(final List<ClientAttendanceBO> clientAttendanceList,
            final ClientBO client) {
        if (clientAttendanceList != null && clientAttendanceList.size() > 0) {
            for (ClientAttendanceBO clientAttendance : clientAttendanceList) {
                if (clientAttendance.getCustomer().getCustomerId() == client.getCustomerId()) {
                    return clientAttendance;
                }
            }
        }
        return null;
    }

    private CustomerAccountBO findCustomerAccountById(final Integer accountId) {
        try {
            return (CustomerAccountBO) accountPersistence.getAccount(accountId);
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    private LoanBO findLoanAccountById(final Integer loanId) {
        try {
            return loanPersistence.getAccount(loanId);
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    private void doLog(String str) {
        System.out.println(str);
    }
}
