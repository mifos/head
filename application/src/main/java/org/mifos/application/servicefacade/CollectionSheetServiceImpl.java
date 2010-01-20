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
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.HibernateException;
import org.joda.time.LocalDate;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.persistance.ClientAttendanceDao;
import org.mifos.application.accounts.loan.persistance.LoanPersistence;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.persistence.SavingsPersistence;
import org.mifos.application.collectionsheet.persistence.CollectionSheetDao;
import org.mifos.application.customer.client.business.ClientAttendanceBO;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

/**
 * implementation of CollectionSheetService
 * 
 */
public class CollectionSheetServiceImpl implements CollectionSheetService {
    private static final MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER);

    private final ClientAttendanceDao clientAttendanceDao;
    private final LoanPersistence loanPersistence;
    private final AccountPersistence accountPersistence;
    private final SavingsPersistence savingsPersistence;
    private final CollectionSheetDao collectionSheetDao;

    public CollectionSheetServiceImpl(final ClientAttendanceDao clientAttendanceDao,
            final LoanPersistence loanPersistence, final AccountPersistence accountPersistence,
            final SavingsPersistence savingsPersistence, final CollectionSheetDao collectionSheetDao) {
        this.clientAttendanceDao = clientAttendanceDao;
        this.loanPersistence = loanPersistence;
        this.accountPersistence = accountPersistence;
        this.savingsPersistence = savingsPersistence;
        this.collectionSheetDao = collectionSheetDao;
    }

    /**
     * The method saves a collection sheet.
     * 
     * @throws SaveCollectionSheetException
     * */
    public CollectionSheetErrorsView saveCollectionSheet(final SaveCollectionSheetDto saveCollectionSheet)
            throws SaveCollectionSheetException {

        saveCollectionSheet.printSummary();
        Long totalTime;
        Long totalTimeStart = System.currentTimeMillis();
        Long readTime;
        Long saveTime = null;
        Long saveTimeStart;

        Integer topCustomerId = saveCollectionSheet.getSaveCollectionSheetCustomers().get(0).getCustomerId();
        CollectionSheetCustomerDto collectionSheetTopCustomer = new CustomerPersistence()
                .findCustomerWithNoAssocationsLoaded(topCustomerId);
        if (collectionSheetTopCustomer == null) {
            List<InvalidSaveCollectionSheetReason> invalidSaveCollectionSheetReasons = new ArrayList<InvalidSaveCollectionSheetReason>();
            List<String> invalidSaveCollectionSheetReasonsExtended = new ArrayList<String>();
            invalidSaveCollectionSheetReasons.add(InvalidSaveCollectionSheetReason.INVALID_TOP_CUSTOMER);
            invalidSaveCollectionSheetReasonsExtended.add(InvalidSaveCollectionSheetReason.INVALID_TOP_CUSTOMER
                    .toString()
                    + ": Customer Id: " + topCustomerId);
            throw new SaveCollectionSheetException(invalidSaveCollectionSheetReasons,
                    invalidSaveCollectionSheetReasonsExtended);
        }
        Short branchId = collectionSheetTopCustomer.getBranchId();
        String searchId = collectionSheetTopCustomer.getSearchId();

        // session caching: prefetch collection sheet data
        // done prior to structure validation because it loads
        // the customers and accounts to be validated into the session
        SaveCollectionSheetSessionCache saveCollectionSheetSessionCache = new SaveCollectionSheetSessionCache();
        saveCollectionSheetSessionCache
                .loadSessionCacheWithCollectionSheetData(saveCollectionSheet, branchId, searchId);

        try {
            new SaveCollectionSheetStructureValidator().execute(saveCollectionSheet);
        } catch (SaveCollectionSheetException e) {
            System.out.println(e.printInvalidSaveCollectionSheetReasons());
            throw e;
        }

        /*
         * With preprocessing complete...
         * 
         * only errors and warnings from the business model remain
         */

        final List<String> failedSavingsDepositAccountNums = new ArrayList<String>();
        final List<String> failedSavingsWithdrawalNums = new ArrayList<String>();
        final List<String> failedLoanDisbursementAccountNumbers = new ArrayList<String>();
        final List<String> failedLoanRepaymentAccountNumbers = new ArrayList<String>();
        final List<String> failedCustomerAccountPaymentNums = new ArrayList<String>();

        SaveCollectionSheetAssembler saveCollectionSheetAssembler = new SaveCollectionSheetAssembler(
                clientAttendanceDao, loanPersistence, accountPersistence, savingsPersistence);

        final List<ClientAttendanceBO> clientAttendances = saveCollectionSheetAssembler
                .clientAttendanceAssemblerfromDto(saveCollectionSheet.getSaveCollectionSheetCustomers(),
                        saveCollectionSheet.getTransactionDate(), branchId, searchId);

        final AccountPaymentEntity payment = saveCollectionSheetAssembler.accountPaymentAssemblerFromDto(
                saveCollectionSheet.getTransactionDate(), saveCollectionSheet.getPaymentType(), saveCollectionSheet
                        .getReceiptId(), saveCollectionSheet.getReceiptDate(), saveCollectionSheet.getUserId());

        final List<SavingsBO> savingsAccounts = saveCollectionSheetAssembler.savingsAccountAssemblerFromDto(
                saveCollectionSheet.getSaveCollectionSheetCustomers(), payment, failedSavingsDepositAccountNums,
                failedSavingsWithdrawalNums);

        final List<LoanBO> loanAccounts = saveCollectionSheetAssembler.loanAccountAssemblerFromDto(saveCollectionSheet
                .getSaveCollectionSheetCustomers(), payment, failedLoanDisbursementAccountNumbers,
                failedLoanRepaymentAccountNumbers);

        final List<AccountBO> customerAccounts = saveCollectionSheetAssembler.customerAccountAssemblerFromDto(
                saveCollectionSheet.getSaveCollectionSheetCustomers(), payment, failedCustomerAccountPaymentNums);

        boolean databaseErrorOccurred = false;
        Throwable databaseError = null;
        readTime = System.currentTimeMillis() - totalTimeStart;

        try {
            saveTimeStart = System.currentTimeMillis();
            persistCollectionSheet(clientAttendances, loanAccounts, customerAccounts, savingsAccounts);
            saveTime = System.currentTimeMillis() - saveTimeStart;
        } catch (HibernateException e) {
            logger.error("database error saving collection sheet", e);
            databaseErrorOccurred = true;
            databaseError = e;
        }

        totalTime = System.currentTimeMillis() - totalTimeStart;
        printTiming(topCustomerId, totalTime, saveTime, readTime, saveCollectionSheetSessionCache);

        return new CollectionSheetErrorsView(failedSavingsDepositAccountNums, failedSavingsWithdrawalNums,
                failedLoanDisbursementAccountNumbers, failedLoanRepaymentAccountNumbers,
                failedCustomerAccountPaymentNums, databaseErrorOccurred, databaseError);
    }

    private void printTiming(Integer topCustomerId, Long totalTime, Long saveTime, Long readTime,
            SaveCollectionSheetSessionCache saveCollectionSheetSessionCache) {

        final StringBuilder builder = new StringBuilder();
        final String doubleQuote = "\"";
        final String comma = "\", \"";

        builder.append(doubleQuote);
        builder.append("Collection Sheet Timing:");
        builder.append(comma);
        builder.append(topCustomerId);
        builder.append(comma);
        builder.append(totalTime);
        builder.append(comma);
        builder.append(saveTime);
        builder.append(comma);
        builder.append(readTime);
        builder.append(comma);
        builder.append(saveCollectionSheetSessionCache.getPrefetchTotalTime());
        builder.append(comma);
        builder.append(saveCollectionSheetSessionCache.getPrefetchCustomerHierarchyTotalTime());
        builder.append(comma);
        builder.append(saveCollectionSheetSessionCache.getPrefetchCustomerHierarchyCount());
        builder.append(comma);
        builder.append(saveCollectionSheetSessionCache.getPrefetchAccountDataTotalTime());
        builder.append(comma);
        builder.append(saveCollectionSheetSessionCache.getPrefetchAccountDataCount());
        builder.append(comma);
        builder.append(saveCollectionSheetSessionCache.getPrefetchLoanSchedulesTotalTime());
        builder.append(comma);
        builder.append(saveCollectionSheetSessionCache.getPrefetchLoanSchedulesCount());
        builder.append(comma);
        builder.append(saveCollectionSheetSessionCache.getPrefetchAccountFeeDetailsTotalTime());
        builder.append(comma);
        builder.append(saveCollectionSheetSessionCache.getPrefetchAccountFeeDetailsCount());
        builder.append(comma);
        builder.append(saveCollectionSheetSessionCache.getPrefetchCustomerSchedulesTotalTime());
        builder.append(comma);
        builder.append(saveCollectionSheetSessionCache.getPrefetchCustomerSchedulesCount());
        builder.append(doubleQuote);

        doLog(builder.toString());

    }

    private void persistCollectionSheet(final List<ClientAttendanceBO> clientAttendances,
            final List<LoanBO> loanAccounts, final List<AccountBO> customerAccountList,
            final List<SavingsBO> savingAccounts) {

        try {
            StaticHibernateUtil.startTransaction();

            clientAttendanceDao.save(clientAttendances);
            loanPersistence.save(loanAccounts);
            accountPersistence.save(customerAccountList);
            savingsPersistence.save(savingAccounts);

            StaticHibernateUtil.commitTransaction();

        } catch (HibernateException e) {
            StaticHibernateUtil.rollbackTransaction();
            throw e;
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    public CollectionSheetDto retrieveCollectionSheet(final Integer customerId, final LocalDate transactionDate) {

        if (customerId == null) {
            throw new IllegalArgumentException("Invalid Null Customer Id");
        }
        if (transactionDate == null) {
            throw new IllegalArgumentException("Invalid Null Transaction Date: ");
        }

        final List<CollectionSheetCustomerDto> customerHierarchy = collectionSheetDao.findCustomerHierarchy(customerId,
                transactionDate);

        if (customerHierarchy == null || customerHierarchy.size() == 0) {
            throw new IllegalArgumentException("Invalid Customer Id: " + customerId);
        }

        final Short branchId = customerHierarchy.get(0).getBranchId();
        final String searchId = customerHierarchy.get(0).getSearchId() + ".%";

        final CustomerHierarchyParams customerHierarchyParams = new CustomerHierarchyParams(customerId, branchId,
                searchId, transactionDate);

        final Map<Integer, List<CollectionSheetCustomerLoanDto>> allLoanRepaymentsGroupedByCustomerId = collectionSheetDao
                .findAllLoanRepaymentsForCustomerHierarchy(branchId, searchId, transactionDate, customerId);

        final Map<Integer, Map<Integer, List<CollectionSheetLoanFeeDto>>> allLoanFeesGroupedByCustomerIdAndAccountId = collectionSheetDao
                .findOutstandingFeesForLoansOnCustomerHierarchy(branchId, searchId, transactionDate, customerId);

        final Map<Integer, List<CollectionSheetCustomerAccountCollectionDto>> allAccountCollectionsByCustomerId = collectionSheetDao
                .findAccountCollectionsOnCustomerAccount(branchId, searchId, transactionDate, customerId);

        final Map<Integer, List<CollectionSheetCustomerAccountCollectionDto>> feesAssociatedWithAccountCollectionsByCustomerId = collectionSheetDao
                .findOutstandingFeesForCustomerAccountOnCustomerHierarchy(branchId, searchId, transactionDate,
                        customerId);

        final Map<Integer, List<CollectionSheetCustomerLoanDto>> allLoanDisbursements = collectionSheetDao
                .findLoanDisbursementsForCustomerHierarchy(branchId, searchId, transactionDate, customerId);

        final Map<Integer, List<CollectionSheetCustomerSavingDto>> allSavingsDepositsGroupedByCustomerId = collectionSheetDao
                .findSavingsDepositsforCustomerHierarchy(customerHierarchyParams);

        final Map<Integer, List<CollectionSheetCustomerSavingDto>> allSavingsAccountsToBePaidByIndividualClientsGroupedByCustomerId = collectionSheetDao
                .findAllSavingsAccountsPayableByIndividualClientsForCustomerHierarchy(customerHierarchyParams);

        final List<CollectionSheetCustomerDto> populatedCollectionSheetCustomer = new ArrayList<CollectionSheetCustomerDto>();
        for (CollectionSheetCustomerDto collectionSheetCustomer : customerHierarchy) {

            final Integer customerInHierarchyId = collectionSheetCustomer.getCustomerId();
            final List<CollectionSheetCustomerLoanDto> associatedLoanRepayments = allLoanRepaymentsGroupedByCustomerId
                    .get(customerInHierarchyId);

            final Map<Integer, List<CollectionSheetLoanFeeDto>> outstandingFeesOnLoanRepayments = allLoanFeesGroupedByCustomerIdAndAccountId
                    .get(customerInHierarchyId);

            final List<CollectionSheetCustomerLoanDto> associatedLoanDisbursements = allLoanDisbursements
                    .get(customerInHierarchyId);

            final List<CollectionSheetCustomerSavingDto> associatedSavingAccount = allSavingsDepositsGroupedByCustomerId
                    .get(customerInHierarchyId);

            final List<CollectionSheetCustomerSavingDto> associatedIndividualSavingsAccounts = allSavingsAccountsToBePaidByIndividualClientsGroupedByCustomerId
                    .get(customerInHierarchyId);

            final List<CollectionSheetCustomerAccountCollectionDto> customerAccountCollections = allAccountCollectionsByCustomerId
                    .get(customerInHierarchyId);
            final List<CollectionSheetCustomerAccountCollectionDto> customerAccountCollectionFees = feesAssociatedWithAccountCollectionsByCustomerId
                    .get(customerInHierarchyId);

            final CollectionSheetCustomerAccountDto customerAccount = sumAssociatedCustomerAccountCollectionFees(
                    customerAccountCollections, customerAccountCollectionFees);

            populatedCollectionSheetCustomer.add(createNullSafeCollectionSheetCustomer(collectionSheetCustomer,
                    associatedLoanRepayments, outstandingFeesOnLoanRepayments, associatedLoanDisbursements,
                    associatedSavingAccount, associatedIndividualSavingsAccounts, customerAccount));
        }

        return new CollectionSheetDto(populatedCollectionSheetCustomer);
    }

    private CollectionSheetCustomerAccountDto sumAssociatedCustomerAccountCollectionFees(
            final List<CollectionSheetCustomerAccountCollectionDto> customerAccountCollections,
            final List<CollectionSheetCustomerAccountCollectionDto> customerAccountCollectionFees) {

        final CollectionSheetCustomerAccountDto totalCollection = sumAccountCollections(customerAccountCollections);
        final CollectionSheetCustomerAccountDto totalCollectionFee = sumAccountCollectionFees(customerAccountCollectionFees);

        final int accountId = Math.max(totalCollection.getAccountId(), totalCollectionFee.getAccountId());
        final int currencyId = Math.max(totalCollection.getCurrencyId(), totalCollectionFee.getCurrencyId());

        return new CollectionSheetCustomerAccountDto(accountId, (short) currencyId, totalCollection
                .getTotalCustomerAccountCollectionFee()
                + totalCollectionFee.getTotalCustomerAccountCollectionFee());
    }

    private CollectionSheetCustomerAccountDto sumAccountCollectionFees(
            final List<CollectionSheetCustomerAccountCollectionDto> customerAccountCollectionFees) {
        Double totalFee = Double.valueOf("0.0");

        if (customerAccountCollectionFees == null) {
            return new CollectionSheetCustomerAccountDto(-1, Short.valueOf("1"), totalFee);
        }

        if (customerAccountCollectionFees.size() > 1) {
            throw new IllegalStateException("Multiple currency");
        }

        return new CollectionSheetCustomerAccountDto(customerAccountCollectionFees.get(0).getAccountId(),
                customerAccountCollectionFees.get(0).getCurrencyId(), customerAccountCollectionFees.get(0)
                        .getTotalFeeAmountDue());
    }

    private CollectionSheetCustomerAccountDto sumAccountCollections(
            final List<CollectionSheetCustomerAccountCollectionDto> customerAccountCollections) {
        Double totalFee = Double.valueOf("0.0");

        if (customerAccountCollections == null) {
            return new CollectionSheetCustomerAccountDto(-1, Short.valueOf("-1"), totalFee);
        }

        if (customerAccountCollections.size() > 1) {
            throw new IllegalStateException("Multiple currency");
        }

        return new CollectionSheetCustomerAccountDto(customerAccountCollections.get(0).getAccountId(),
                customerAccountCollections.get(0).getCurrencyId(), customerAccountCollections.get(0)
                        .getAccountCollectionPayment());
    }

    @SuppressWarnings("unchecked")
    private CollectionSheetCustomerDto createNullSafeCollectionSheetCustomer(
            final CollectionSheetCustomerDto collectionSheetCustomer,
            final List<CollectionSheetCustomerLoanDto> associatedLoanRepayments,
            final Map<Integer, List<CollectionSheetLoanFeeDto>> outstandingFeesOnLoanRepayments,
            final List<CollectionSheetCustomerLoanDto> allLoanDisbursements,
            final List<CollectionSheetCustomerSavingDto> associatedSavingAccount,
            final List<CollectionSheetCustomerSavingDto> associatedIndividualSavingsAccounts,
            final CollectionSheetCustomerAccountDto customerAccount) {

        final List<CollectionSheetCustomerSavingDto> savingAccounts = (List<CollectionSheetCustomerSavingDto>) ObjectUtils
                .defaultIfNull(associatedSavingAccount, new ArrayList<CollectionSheetCustomerSavingDto>());

        final List<CollectionSheetCustomerSavingDto> individualSavingAccounts = (List<CollectionSheetCustomerSavingDto>) ObjectUtils
                .defaultIfNull(associatedIndividualSavingsAccounts, new ArrayList<CollectionSheetCustomerSavingDto>());

        if (outstandingFeesOnLoanRepayments == null) {

            List<CollectionSheetCustomerLoanDto> loanRepaymentsAndDisbursements = new ArrayList<CollectionSheetCustomerLoanDto>();

            if (associatedLoanRepayments != null) {
                loanRepaymentsAndDisbursements.addAll(associatedLoanRepayments);
            }

            if (allLoanDisbursements != null) {
                loanRepaymentsAndDisbursements.addAll(allLoanDisbursements);
            }

            return new CollectionSheetCustomerDto(collectionSheetCustomer, loanRepaymentsAndDisbursements,
                    savingAccounts, individualSavingAccounts, customerAccount);
        }

        final List<CollectionSheetCustomerLoanDto> loanRepaymentsAndDisbursementsWithFees = new ArrayList<CollectionSheetCustomerLoanDto>();

        if (associatedLoanRepayments != null) {
            for (CollectionSheetCustomerLoanDto collectionSheetCustomerLoan : associatedLoanRepayments) {
                final List<CollectionSheetLoanFeeDto> loanFeesAgainstAccountOfCustomer = outstandingFeesOnLoanRepayments
                        .get(collectionSheetCustomerLoan.getAccountId());

                loanRepaymentsAndDisbursementsWithFees.add(populateCollectionSheetCustomerLoan(
                        collectionSheetCustomerLoan, loanFeesAgainstAccountOfCustomer));
            }
        }

        if (allLoanDisbursements != null) {
            loanRepaymentsAndDisbursementsWithFees.addAll(allLoanDisbursements);
        }

        return new CollectionSheetCustomerDto(collectionSheetCustomer, loanRepaymentsAndDisbursementsWithFees,
                savingAccounts, individualSavingAccounts, customerAccount);
    }

    private CollectionSheetCustomerLoanDto populateCollectionSheetCustomerLoan(
            final CollectionSheetCustomerLoanDto loan, final List<CollectionSheetLoanFeeDto> loanFees) {

        if (loanFees == null || loanFees.isEmpty()) {
            return loan;
        }

        if (loanFees.size() > 1) {
            throw new IllegalStateException("Multiple summed fees exist against loan account [" + loan.getAccountId()
                    + "]. This most likey due to multiple currencies existing which is not supported.");
        }

        loan.setTotalAccountFees(loanFees.get(0).getTotalFeeAmountDue());
        return loan;
    }

    private void doLog(String str) {
        System.out.println(str);
    }
}
