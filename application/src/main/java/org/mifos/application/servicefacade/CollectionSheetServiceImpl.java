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
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.persistance.ClientAttendanceDao;
import org.mifos.application.accounts.loan.persistance.LoanPersistence;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.persistence.SavingsPersistence;
import org.mifos.application.collectionsheet.persistence.CollectionSheetDao;
import org.mifos.application.customer.client.business.ClientAttendanceBO;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

/**
 *
 */
public class CollectionSheetServiceImpl implements CollectionSheetService {

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

    public void saveCollectionSheet(final List<ClientAttendanceBO> clientAttendances, final List<LoanBO> loanAccounts,
            final List<AccountBO> customerAccountList, final List<SavingsBO> savingAccounts) {

        try {
            StaticHibernateUtil.startTransaction();

            clientAttendanceDao.save(clientAttendances);

            loanPersistence.save(loanAccounts);

            accountPersistence.save(customerAccountList);

            savingsPersistence.save(savingAccounts);

            StaticHibernateUtil.commitTransaction();
        } catch (Exception e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    public CollectionSheetDto retrieveCollectionSheet(final Integer customerId, final Date transactionDate) {

        final List<CollectionSheetCustomerDto> customerHierarchy = collectionSheetDao.findCustomerHierarchy(customerId,
                transactionDate);

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
                    customerAccountCollections,
                    customerAccountCollectionFees);

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
                customerAccountCollectionFees.get(0).getCurrencyId(),
                customerAccountCollectionFees.get(0).getTotalFeeAmountDue());
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
                customerAccountCollections.get(0).getCurrencyId(),
                customerAccountCollections.get(0).getAccountCollectionPayment());
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

    private CollectionSheetCustomerLoanDto populateCollectionSheetCustomerLoan(final CollectionSheetCustomerLoanDto loan,
            final List<CollectionSheetLoanFeeDto> loanFees) {

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
}
