/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.application.collectionsheet.persistence;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.mifos.accounts.loan.persistance.LegacyLoanDao;
import org.mifos.accounts.savings.persistence.SavingsDao;
import org.mifos.application.servicefacade.CollectionSheetCustomerAccountCollectionDto;
import org.mifos.application.servicefacade.CollectionSheetCustomerDto;
import org.mifos.application.servicefacade.CollectionSheetCustomerLoanDto;
import org.mifos.application.servicefacade.CollectionSheetCustomerSavingDto;
import org.mifos.application.servicefacade.CollectionSheetCustomerSavingsAccountDto;
import org.mifos.application.servicefacade.CollectionSheetFormEnteredDataDto;
import org.mifos.application.servicefacade.CollectionSheetLoanFeeDto;
import org.mifos.application.servicefacade.CustomerHierarchyParams;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.LegacyGenericDao;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 */
public class CollectionSheetDaoHibernate extends LegacyGenericDao implements CollectionSheetDao {

    private final SavingsDao savingsDao;

    @Autowired
    private LegacyLoanDao legacyLoanDao;

    @Autowired
    public CollectionSheetDaoHibernate(final SavingsDao savingsDao) {
        this.savingsDao = savingsDao;
    }

    @Override
	@SuppressWarnings("unchecked")
    public List<CollectionSheetCustomerDto> findCustomerHierarchy(final Integer customerId,
            final LocalDate transactionDate) {

        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("CUSTOMER_ID", customerId);
        queryParameters.put("TRANSACTION_DATE", transactionDate.toString());

        CollectionSheetCustomerDto topCustomer = execUniqueResultNamedQueryWithResultTransformer(
                "findCustomerAtTopOfHierarchyAsDto", queryParameters, CollectionSheetCustomerDto.class);

        if (topCustomer == null) {
            return new ArrayList<CollectionSheetCustomerDto>();
        }

        final Map<String, Object> withinHierarchyQueryParameters = new HashMap<String, Object>();
        withinHierarchyQueryParameters.put("BRANCH_ID", topCustomer.getBranchId());
        withinHierarchyQueryParameters.put("SEARCH_ID", topCustomer.getSearchId() + ".%");
        withinHierarchyQueryParameters.put("TRANSACTION_DATE", transactionDate.toString());

        final List<CollectionSheetCustomerDto> restOfHierarchy = executeNamedQueryWithResultTransformer(
                "findCustomersWithinHierarchyAsDto", withinHierarchyQueryParameters, CollectionSheetCustomerDto.class);

        final List<CollectionSheetCustomerDto> collectionSheetCutomerList = new ArrayList<CollectionSheetCustomerDto>();
        collectionSheetCutomerList.add(topCustomer);
        collectionSheetCutomerList.addAll(restOfHierarchy);

        return collectionSheetCutomerList;
    }
    
    @Override
  	@SuppressWarnings("unchecked")
      public List<CollectionSheetCustomerDto> findCustomerHierarchy(final CollectionSheetFormEnteredDataDto formEnteredDataDto,
              final LocalDate transactionDate) {
		Integer customerId=formEnteredDataDto.getCustomer().getCustomerId();
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("CUSTOMER_ID", customerId);
		queryParameters.put("TRANSACTION_DATE", transactionDate.toString());

		CollectionSheetCustomerDto topCustomer = execUniqueResultNamedQueryWithResultTransformer(
				"findCustomerAtTopOfHierarchyAsDto", queryParameters, CollectionSheetCustomerDto.class);

		if (topCustomer == null) {
			return new ArrayList<CollectionSheetCustomerDto>();
		}
		
		List<CollectionSheetCustomerDto> rest=new ArrayList<CollectionSheetCustomerDto>();
		
		Map<String, Object> withinHierarchyQueryParameters = new HashMap<String, Object>();
		withinHierarchyQueryParameters.put("BRANCH_ID", topCustomer.getBranchId());
		withinHierarchyQueryParameters.put("TRANSACTION_DATE", transactionDate.toString());
		
		if(formEnteredDataDto.getGroup()==null){
			
		    withinHierarchyQueryParameters.put("SEARCH_ID", topCustomer.getSearchId() + ".%");
		    rest = executeNamedQueryWithResultTransformer("findCustomersWithinHierarchyAsDto", withinHierarchyQueryParameters, CollectionSheetCustomerDto.class);
		}
		else{
			String groupSearchId=formEnteredDataDto.getGroup().getCustomerSearchId();
			if(formEnteredDataDto.getMember()==null){
				withinHierarchyQueryParameters.put("SEARCH_ID", groupSearchId);
				List<CollectionSheetCustomerDto> group = executeNamedQueryWithResultTransformer("findCustomersWithinHierarchyAsDto", withinHierarchyQueryParameters, CollectionSheetCustomerDto.class);
				withinHierarchyQueryParameters.put("SEARCH_ID", groupSearchId + ".%");
				List<CollectionSheetCustomerDto> members = executeNamedQueryWithResultTransformer("findCustomersWithinHierarchyAsDto", withinHierarchyQueryParameters, CollectionSheetCustomerDto.class);
				rest.addAll(group);
				rest.addAll(members);
			}
			else{
				String memberSearchId=formEnteredDataDto.getMember().getCustomerSearchId();
				withinHierarchyQueryParameters.put("SEARCH_ID", groupSearchId);
				List<CollectionSheetCustomerDto> group = executeNamedQueryWithResultTransformer("findCustomersWithinHierarchyAsDto", withinHierarchyQueryParameters, CollectionSheetCustomerDto.class);
				withinHierarchyQueryParameters.put("SEARCH_ID", memberSearchId);
				List<CollectionSheetCustomerDto> members = executeNamedQueryWithResultTransformer("findCustomersWithinHierarchyAsDto", withinHierarchyQueryParameters, CollectionSheetCustomerDto.class);
				rest.addAll(group);
				rest.addAll(members);
			}
		}
		
		final List<CollectionSheetCustomerDto> restOfHierarchy=rest;
		final List<CollectionSheetCustomerDto> collectionSheetCutomerList = new ArrayList<CollectionSheetCustomerDto>();
		collectionSheetCutomerList.add(topCustomer);
		collectionSheetCutomerList.addAll(restOfHierarchy);
		return collectionSheetCutomerList;
      }

    /*
     *
     */
    @Override
	@SuppressWarnings("unchecked")
    public Map<Integer, List<CollectionSheetCustomerLoanDto>> findAllLoanRepaymentsForCustomerHierarchy(
            final Short branchId, final String searchId, final LocalDate transactionDate,
            final Integer customerAtTopOfHierarchyId) {

        final Map<Integer, List<CollectionSheetCustomerLoanDto>> allLoanRepaymentsGroupedByCustomerId = new HashMap<Integer, List<CollectionSheetCustomerLoanDto>>();

        final Map<String, Object> topOfHierarchyParameters = new HashMap<String, Object>();
        topOfHierarchyParameters.put("BRANCH_ID", branchId);
        topOfHierarchyParameters.put("CUSTOMER_ID", customerAtTopOfHierarchyId);
        topOfHierarchyParameters.put("TRANSACTION_DATE", transactionDate.toString());

        final CollectionSheetCustomerLoanDto loanRepaymentsForCustomerAtTopOfHierarchy = execUniqueResultNamedQueryWithResultTransformer(
                "findLoanRepaymentsforCustomerAtTopOfHierarchyAsDto", topOfHierarchyParameters,
                CollectionSheetCustomerLoanDto.class);

        if (loanRepaymentsForCustomerAtTopOfHierarchy != null) {
            allLoanRepaymentsGroupedByCustomerId.put(customerAtTopOfHierarchyId, Arrays
                    .asList(loanRepaymentsForCustomerAtTopOfHierarchy));
        }

        final Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("BRANCH_ID", branchId);
        queryParameters.put("SEARCH_ID", searchId);
        queryParameters.put("TRANSACTION_DATE", transactionDate.toString());

        final List<CollectionSheetCustomerLoanDto> loanRepayments = executeNamedQueryWithResultTransformer(
                "findLoanRepaymentsforCustomerHierarchyAsDto", queryParameters, CollectionSheetCustomerLoanDto.class);

        for (CollectionSheetCustomerLoanDto customerLoan : loanRepayments) {

            final Integer customerId = customerLoan.getCustomerId();

            if (allLoanRepaymentsGroupedByCustomerId.containsKey(customerId)) {
                final List<CollectionSheetCustomerLoanDto> loansForCustomer = allLoanRepaymentsGroupedByCustomerId
                        .get(customerId);
                loansForCustomer.add(customerLoan);
            } else {
                final List<CollectionSheetCustomerLoanDto> customerLoansForCustomer = new ArrayList<CollectionSheetCustomerLoanDto>();
                customerLoansForCustomer.add(customerLoan);
                allLoanRepaymentsGroupedByCustomerId.put(customerId, customerLoansForCustomer);
            }
        }

        addInformationAboutActiveLoans(allLoanRepaymentsGroupedByCustomerId, queryParameters);

        return allLoanRepaymentsGroupedByCustomerId;
    }

    @SuppressWarnings("unchecked")
    private void addInformationAboutActiveLoans(Map<Integer, List<CollectionSheetCustomerLoanDto>> allLoanRepaymentsGroupedByCustomerId, Map<String, Object> queryParameters) {
        final List<CollectionSheetCustomerLoanDto> activeLoans = executeNamedQueryWithResultTransformer(
                "findActiveLoansforCustomerHierarchyAsDto", queryParameters, CollectionSheetCustomerLoanDto.class);

        for (CollectionSheetCustomerLoanDto customerLoan : activeLoans) {

            final Integer customerId = customerLoan.getCustomerId();

            if (allLoanRepaymentsGroupedByCustomerId.containsKey(customerId)) {
                final List<CollectionSheetCustomerLoanDto> loansForCustomer = allLoanRepaymentsGroupedByCustomerId
                        .get(customerId);
                Comparator<CollectionSheetCustomerLoanDto> comparator = new Comparator<CollectionSheetCustomerLoanDto>() {

                    @Override
                    public int compare(CollectionSheetCustomerLoanDto cseDto1, CollectionSheetCustomerLoanDto cseDto2) {
                        return cseDto1.getAccountId().compareTo(cseDto2.getAccountId());
                    }
                };
                Collections.sort(loansForCustomer, comparator);
                if (Collections.binarySearch(loansForCustomer, customerLoan, comparator) < 0) {
                    loansForCustomer.add(customerLoan);
                }
            } else {
                final List<CollectionSheetCustomerLoanDto> customerLoansForCustomer = new ArrayList<CollectionSheetCustomerLoanDto>();
                customerLoansForCustomer.add(customerLoan);
                allLoanRepaymentsGroupedByCustomerId.put(customerId, customerLoansForCustomer);
            }
        }
    }

    @Override
	@SuppressWarnings("unchecked")
    public Map<Integer, Map<Integer, List<CollectionSheetLoanFeeDto>>> findOutstandingFeesForLoansOnCustomerHierarchy(
            final Short branchId, final String searchId, final LocalDate transactionDate,
            final Integer customerAtTopOfHierarchyId) {

        final Map<Integer, Map<Integer, List<CollectionSheetLoanFeeDto>>> outstandingLoanFeesGroupedByCustomerId = new HashMap<Integer, Map<Integer, List<CollectionSheetLoanFeeDto>>>();

        final Map<String, Object> topOfHierarchyQueryParams = new HashMap<String, Object>();
        topOfHierarchyQueryParams.put("BRANCH_ID", branchId);
        topOfHierarchyQueryParams.put("CUSTOMER_ID", customerAtTopOfHierarchyId);
        topOfHierarchyQueryParams.put("TRANSACTION_DATE", transactionDate.toString());

        final List<CollectionSheetLoanFeeDto> outstandingLoanFeesForTopCustomer = executeNamedQueryWithResultTransformer(
                "findOutstandingFeesForLoansOnCustomerAtTopOfHierarchyAsDto", topOfHierarchyQueryParams,
                CollectionSheetLoanFeeDto.class);

        if (outstandingLoanFeesForTopCustomer != null) {
            populateLoanFeesMap(outstandingLoanFeesGroupedByCustomerId, outstandingLoanFeesForTopCustomer);
        }

        final Map<String, Object> withinHierarchyQueryParameters = new HashMap<String, Object>();
        withinHierarchyQueryParameters.put("BRANCH_ID", branchId);
        withinHierarchyQueryParameters.put("SEARCH_ID", searchId);
        withinHierarchyQueryParameters.put("TRANSACTION_DATE", transactionDate.toString());

        final List<CollectionSheetLoanFeeDto> outstandingLoanFees = executeNamedQueryWithResultTransformer(
                "findOutstandingFeesForLoansOnCustomerHierarchyAsDto", withinHierarchyQueryParameters,
                CollectionSheetLoanFeeDto.class);

        if (outstandingLoanFees == null) {
            return outstandingLoanFeesGroupedByCustomerId;
        }

        populateLoanFeesMap(outstandingLoanFeesGroupedByCustomerId, outstandingLoanFees);

        return outstandingLoanFeesGroupedByCustomerId;
    }

    @Override
	@SuppressWarnings("unchecked")
    public Map<Integer, List<CollectionSheetCustomerAccountCollectionDto>> findAccountCollectionsOnCustomerAccount(
            final Short branchId, final String searchId, final LocalDate transactionDate,
            final Integer customerAtTopOfHierarchyId) {

        final Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("CUSTOMER_ID", customerAtTopOfHierarchyId);
        queryParameters.put("TRANSACTION_DATE", transactionDate.toString());

        final Map<Integer, List<CollectionSheetCustomerAccountCollectionDto>> accountCollectionsOnCustomerAccountGroupedByCustomerId = new HashMap<Integer, List<CollectionSheetCustomerAccountCollectionDto>>();

        CollectionSheetCustomerAccountCollectionDto accountCollectionFeeForHierarchyCustomer = execUniqueResultNamedQueryWithResultTransformer(
                "findAccountCollectionsOnCustomerAccountForTopCustomerOfHierarchy", queryParameters,
                CollectionSheetCustomerAccountCollectionDto.class);

        if (accountCollectionFeeForHierarchyCustomer != null) {
            accountCollectionsOnCustomerAccountGroupedByCustomerId.put(customerAtTopOfHierarchyId, Arrays
                    .asList(accountCollectionFeeForHierarchyCustomer));
        }

        final Map<String, Object> withinHierarchyQueryParameters = new HashMap<String, Object>();
        withinHierarchyQueryParameters.put("BRANCH_ID", branchId);
        withinHierarchyQueryParameters.put("SEARCH_ID", searchId);
        withinHierarchyQueryParameters.put("TRANSACTION_DATE", transactionDate.toString());

        final List<CollectionSheetCustomerAccountCollectionDto> customerAccountFees = executeNamedQueryWithResultTransformer(
                "findAccountCollectionsOnCustomerAccountForCustomerHierarchyAsDto", withinHierarchyQueryParameters,
                CollectionSheetCustomerAccountCollectionDto.class);

        for (CollectionSheetCustomerAccountCollectionDto accountCollectionFee : customerAccountFees) {

            final Integer customerId = accountCollectionFee.getCustomerId();

            if (accountCollectionsOnCustomerAccountGroupedByCustomerId.containsKey(customerId)) {

                final List<CollectionSheetCustomerAccountCollectionDto> customerAccountFeesList = accountCollectionsOnCustomerAccountGroupedByCustomerId
                        .get(customerId);
                customerAccountFeesList.add(accountCollectionFee);
            } else {
                final List<CollectionSheetCustomerAccountCollectionDto> customerAccountFeesList = new ArrayList<CollectionSheetCustomerAccountCollectionDto>();
                customerAccountFeesList.add(accountCollectionFee);

                accountCollectionsOnCustomerAccountGroupedByCustomerId.put(customerId, customerAccountFeesList);
            }
        }

        return accountCollectionsOnCustomerAccountGroupedByCustomerId;
    }

    @Override
	@SuppressWarnings("unchecked")
    public Map<Integer, List<CollectionSheetCustomerAccountCollectionDto>> findOutstandingFeesForCustomerAccountOnCustomerHierarchy(
            final Short branchId, final String searchId, final LocalDate transactionDate,
            final Integer customerAtTopOfHierarchyId) {

        final Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("CUSTOMER_ID", customerAtTopOfHierarchyId);
        queryParameters.put("TRANSACTION_DATE", transactionDate.toString());

        final Map<Integer, List<CollectionSheetCustomerAccountCollectionDto>> accountCollectionsOnCustomerAccountGroupedByCustomerId = new HashMap<Integer, List<CollectionSheetCustomerAccountCollectionDto>>();

        final CollectionSheetCustomerAccountCollectionDto accountCollectionFeeForHierarchyCustomer = execUniqueResultNamedQueryWithResultTransformer(
                "findOutstandingCustomerAccountFeesForTopCustomerOfHierarchyAsDto", queryParameters,
                CollectionSheetCustomerAccountCollectionDto.class);

        if (accountCollectionFeeForHierarchyCustomer != null) {
            accountCollectionsOnCustomerAccountGroupedByCustomerId.put(customerAtTopOfHierarchyId, Arrays
                    .asList(accountCollectionFeeForHierarchyCustomer));
        }

        final Map<String, Object> withinHierarchyQueryParameters = new HashMap<String, Object>();
        withinHierarchyQueryParameters.put("BRANCH_ID", branchId);
        withinHierarchyQueryParameters.put("SEARCH_ID", searchId);
        withinHierarchyQueryParameters.put("TRANSACTION_DATE", transactionDate.toString());

        final List<CollectionSheetCustomerAccountCollectionDto> customerAccountFees = executeNamedQueryWithResultTransformer(
                "findOutstandingFeesForCustomerAccountOnCustomerHierarchyAsDto", withinHierarchyQueryParameters,
                CollectionSheetCustomerAccountCollectionDto.class);

        if (customerAccountFees == null) {
            return accountCollectionsOnCustomerAccountGroupedByCustomerId;
        }

        for (CollectionSheetCustomerAccountCollectionDto accountCollectionFee : customerAccountFees) {

            final Integer customerId = accountCollectionFee.getCustomerId();
            if (accountCollectionsOnCustomerAccountGroupedByCustomerId.containsKey(customerId)) {

                final List<CollectionSheetCustomerAccountCollectionDto> customerAccountFeesList = accountCollectionsOnCustomerAccountGroupedByCustomerId
                        .get(customerId);
                customerAccountFeesList.add(accountCollectionFee);
            } else {
                final List<CollectionSheetCustomerAccountCollectionDto> customerAccountFeesList = new ArrayList<CollectionSheetCustomerAccountCollectionDto>();
                customerAccountFeesList.add(accountCollectionFee);

                accountCollectionsOnCustomerAccountGroupedByCustomerId.put(customerId, customerAccountFeesList);
            }
        }

        return accountCollectionsOnCustomerAccountGroupedByCustomerId;
    }

    @Override
	@SuppressWarnings("unchecked")
    public Map<Integer, List<CollectionSheetCustomerLoanDto>> findLoanDisbursementsForCustomerHierarchy(
            final Short branchId, final String searchId, final LocalDate transactionDate,
            final Integer customerAtTopOfHierarchyId) {

        final Map<Integer, List<CollectionSheetCustomerLoanDto>> loanDisbursementsGroupedByCustomerId = new HashMap<Integer, List<CollectionSheetCustomerLoanDto>>();

        final Map<String, Object> topOfHierarchyQueryParameters = new HashMap<String, Object>();
        topOfHierarchyQueryParameters.put("BRANCH_ID", branchId);
        topOfHierarchyQueryParameters.put("CUSTOMER_ID", customerAtTopOfHierarchyId);
        topOfHierarchyQueryParameters.put("TRANSACTION_DATE", transactionDate.toString());

        final List<CollectionSheetCustomerLoanDto> allLoanDisbursementsForTopCustomer = executeNamedQueryWithResultTransformer(
                "findLoanDisbursementsforCustomerAtTopOfHierarchyAsDto", topOfHierarchyQueryParameters,
                CollectionSheetCustomerLoanDto.class);

        if (allLoanDisbursementsForTopCustomer != null) {
            populateLoanDisbursement(loanDisbursementsGroupedByCustomerId, allLoanDisbursementsForTopCustomer);
        }

        final Map<String, Object> withinHierarchyQueryParameters = new HashMap<String, Object>();
        withinHierarchyQueryParameters.put("BRANCH_ID", branchId);
        withinHierarchyQueryParameters.put("SEARCH_ID", searchId);
        withinHierarchyQueryParameters.put("TRANSACTION_DATE", transactionDate.toString());

        final List<CollectionSheetCustomerLoanDto> allLoanDisbursements = executeNamedQueryWithResultTransformer(
                "findLoanDisbursementsforCustomerHierarchyAsDto", withinHierarchyQueryParameters,
                CollectionSheetCustomerLoanDto.class);

        if (allLoanDisbursements != null) {
            populateLoanDisbursement(loanDisbursementsGroupedByCustomerId, allLoanDisbursements);
        }

        return loanDisbursementsGroupedByCustomerId;
    }

    @Override
	public Map<Integer, List<CollectionSheetCustomerSavingDto>> findSavingsDepositsforCustomerHierarchy(
            final CustomerHierarchyParams customerHierarchyParams) {

        final List<CollectionSheetCustomerSavingDto> mandatorySavingAccount = savingsDao
                .findAllMandatorySavingAccountsForClientsOrGroupsWithCompleteGroupStatusForCustomerHierarchy(customerHierarchyParams);

        final List<CollectionSheetCustomerSavingDto> voluntarySavingAccount = savingsDao
                .findAllVoluntarySavingAccountsForClientsAndGroupsWithCompleteGroupStatusForCustomerHierarchy(customerHierarchyParams);

        final List<CollectionSheetCustomerSavingDto> allSavingsAccounts = new ArrayList<CollectionSheetCustomerSavingDto>();
        allSavingsAccounts.addAll(mandatorySavingAccount);
        allSavingsAccounts.addAll(voluntarySavingAccount);

        return convertListToMapGroupedByCustomerId(allSavingsAccounts);
    }

    @Override
	public Map<Integer, List<CollectionSheetCustomerSavingDto>> findAllSavingsAccountsPayableByIndividualClientsForCustomerHierarchy(
            final CustomerHierarchyParams customerHierarchyParams) {

        final List<CollectionSheetCustomerSavingDto> mandatorySavings = savingsDao
                .findAllMandatorySavingAccountsForIndividualChildrenOfCentersOrGroupsWithPerIndividualStatusForCustomerHierarchy(customerHierarchyParams);

        final List<CollectionSheetCustomerSavingDto> voluntarySavings = savingsDao
                .findAllVoluntarySavingAccountsForIndividualChildrenOfCentersOrGroupsWithPerIndividualStatusForCustomerHierarchy(customerHierarchyParams);

        final List<CollectionSheetCustomerSavingDto> allIndividualSavings = new ArrayList<CollectionSheetCustomerSavingDto>();
        allIndividualSavings.addAll(mandatorySavings);
        allIndividualSavings.addAll(voluntarySavings);

        return convertListToMapGroupedByCustomerId(allIndividualSavings);
    }

    private Map<Integer, List<CollectionSheetCustomerSavingDto>> convertListToMapGroupedByCustomerId(
            final List<CollectionSheetCustomerSavingDto> allSavingsAccounts) {

        final Map<Integer, List<CollectionSheetCustomerSavingDto>> savingsGroupedByCustomerId = new HashMap<Integer, List<CollectionSheetCustomerSavingDto>>();

        for (CollectionSheetCustomerSavingDto savingsAccountDto : allSavingsAccounts) {
            final Integer customerId = savingsAccountDto.getCustomerId();
            if (savingsGroupedByCustomerId.containsKey(customerId)) {
                savingsGroupedByCustomerId.get(customerId).add(savingsAccountDto);
            } else {
                List<CollectionSheetCustomerSavingDto> savingsDtoList = new ArrayList<CollectionSheetCustomerSavingDto>();
                savingsDtoList.add(savingsAccountDto);
                savingsGroupedByCustomerId.put(customerId, savingsDtoList);
            }
        }

        return savingsGroupedByCustomerId;
    }

    private void populateLoanDisbursement(
            final Map<Integer, List<CollectionSheetCustomerLoanDto>> loanDisbursementsGroupedByCustomerId,
            final List<CollectionSheetCustomerLoanDto> allLoanDisbursements) {
        for (CollectionSheetCustomerLoanDto loanDisbursementAccount : allLoanDisbursements) {
            final Integer customerId = loanDisbursementAccount.getCustomerId();
            final Integer accountId = loanDisbursementAccount.getAccountId();

            Double amountDueAtDisbursement;
            if (Constants.YES == loanDisbursementAccount.getPayInterestAtDisbursement()) {
                amountDueAtDisbursement = findAmountDueWhenInterestIsDueAtDibursementTime(accountId);
            } else {
                amountDueAtDisbursement = legacyLoanDao.getFeeAmountAtDisbursement(accountId,
                        Money.getDefaultCurrency()).getAmountDoubleValue();
            }

            loanDisbursementAccount.setAmountDueAtDisbursement(amountDueAtDisbursement);

            if (loanDisbursementsGroupedByCustomerId.containsKey(customerId)) {

                final List<CollectionSheetCustomerLoanDto> loanAccountList = loanDisbursementsGroupedByCustomerId
                        .get(customerId);
                loanAccountList.add(loanDisbursementAccount);

            } else {
                final List<CollectionSheetCustomerLoanDto> loanAccountList = new ArrayList<CollectionSheetCustomerLoanDto>();
                loanAccountList.add(loanDisbursementAccount);

                loanDisbursementsGroupedByCustomerId.put(customerId, loanAccountList);
            }
        }
    }

    private void populateLoanFeesMap(
            final Map<Integer, Map<Integer, List<CollectionSheetLoanFeeDto>>> outstandingLoanFeesGroupedByCustomerId,
            final List<CollectionSheetLoanFeeDto> outstandingLoanFees) {

        for (CollectionSheetLoanFeeDto loanFee : outstandingLoanFees) {

            final Integer customerId = loanFee.getCustomerId();
            final Integer accountId = loanFee.getAccountId();

            if (outstandingLoanFeesGroupedByCustomerId.containsKey(customerId)) {

                final Map<Integer, List<CollectionSheetLoanFeeDto>> loanFeesGroupedByAccountId = outstandingLoanFeesGroupedByCustomerId
                        .get(customerId);

                if (loanFeesGroupedByAccountId.containsKey(accountId)) {
                    final List<CollectionSheetLoanFeeDto> loanFeesForAccount = loanFeesGroupedByAccountId
                            .get(accountId);
                    loanFeesForAccount.add(loanFee);
                } else {
                    final List<CollectionSheetLoanFeeDto> loanFeesForAccount = new ArrayList<CollectionSheetLoanFeeDto>();
                    loanFeesForAccount.add(loanFee);
                }
            } else {
                final List<CollectionSheetLoanFeeDto> loanFeesForAccount = new ArrayList<CollectionSheetLoanFeeDto>();
                loanFeesForAccount.add(loanFee);

                final Map<Integer, List<CollectionSheetLoanFeeDto>> loanFeesGroupedByAccountId = new HashMap<Integer, List<CollectionSheetLoanFeeDto>>();
                loanFeesGroupedByAccountId.put(accountId, loanFeesForAccount);

                outstandingLoanFeesGroupedByCustomerId.put(customerId, loanFeesGroupedByAccountId);
            }
        }
    }

    private Double findAmountDueWhenInterestIsDueAtDibursementTime(final Integer accountId) {

        final Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("ACCOUNT_ID", accountId);

        try {
            final Object[] loanScheduleData = (Object[]) execUniqueResultNamedQuery("findFirstLoanSchedule",
                    queryParameters);

            final Integer loanScheduleId = Integer.valueOf(loanScheduleData[0].toString());

            final Double firstScheduledPaymentAmount = calculateAmountDueFromLoanScheduleFields(loanScheduleData);

            final Map<String, Object> feeQueryParameters = new HashMap<String, Object>();
            queryParameters.put("LOAN_SCHEDULE_ID", loanScheduleId);

            final Object[] loanScheduleFee = (Object[]) execUniqueResultNamedQuery(
                    "findLoanFeeSchedulesForALoanSchedule", feeQueryParameters);

            final Double feesDueOnLoanSchedule = calculateFeesDueOnLoanSchedule(loanScheduleFee);

            return firstScheduledPaymentAmount.doubleValue() + feesDueOnLoanSchedule.doubleValue();
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    private Double calculateFeesDueOnLoanSchedule(final Object[] loanScheduleFee) {

        if (loanScheduleFee == null) {
            return Double.valueOf("0.0");
        }

        final BigDecimal feeAmountDue = BigDecimal.valueOf(Double.valueOf(loanScheduleFee[0].toString()));
        final BigDecimal feeAmountPaid = BigDecimal.valueOf(Double.valueOf(loanScheduleFee[1].toString()));

        return feeAmountDue.doubleValue() - feeAmountPaid.doubleValue();
    }

    private Double calculateAmountDueFromLoanScheduleFields(final Object[] loanScheduleData) {

        if (loanScheduleData == null) {
            return Double.valueOf("0.0");
        }

        final BigDecimal principalDue = BigDecimal.valueOf(Double.valueOf(loanScheduleData[1].toString()));
        final BigDecimal principalPaid = BigDecimal.valueOf(Double.valueOf(loanScheduleData[2].toString()));
        final BigDecimal interestDue = BigDecimal.valueOf(Double.valueOf(loanScheduleData[3].toString()));
        final BigDecimal interestPaid = BigDecimal.valueOf(Double.valueOf(loanScheduleData[4].toString()));
        final BigDecimal penaltyDue = BigDecimal.valueOf(Double.valueOf(loanScheduleData[5].toString()));
        final BigDecimal penaltyPaid = BigDecimal.valueOf(Double.valueOf(loanScheduleData[6].toString()));
        final BigDecimal miscFeesDue = BigDecimal.valueOf(Double.valueOf(loanScheduleData[7].toString()));
        final BigDecimal miscFeesPaid = BigDecimal.valueOf(Double.valueOf(loanScheduleData[8].toString()));
        final BigDecimal miscPenaltyDue = BigDecimal.valueOf(Double.valueOf(loanScheduleData[9].toString()));
        final BigDecimal miscPenaltyPaid = BigDecimal.valueOf(Double.valueOf(loanScheduleData[10].toString()));

        return principalDue.doubleValue()
                + interestDue.doubleValue()
                + penaltyDue.doubleValue()
                + miscFeesDue.doubleValue()
                + miscPenaltyDue.doubleValue()
                - (principalPaid.doubleValue() + interestPaid.doubleValue() + penaltyPaid.doubleValue()
                        + miscFeesPaid.doubleValue() + miscPenaltyPaid.doubleValue());
    }

    @Override
	public List<CollectionSheetCustomerSavingsAccountDto> findAllSavingAccountsForCustomerHierarchy(
            CustomerHierarchyParams customerHierarchyParams) {
        return savingsDao.findAllSavingAccountsForCustomerHierarchy(customerHierarchyParams);
    }
}
