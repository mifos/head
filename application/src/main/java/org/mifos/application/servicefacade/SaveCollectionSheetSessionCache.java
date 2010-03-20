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
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.config.GeneralConfig;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.persistence.CustomerPersistence;

/**
 * Loads Collection Sheet data into Hibernate session cache.
 *
 * This minimises the number of read requests required to process the Collection Sheet.
 */
public class SaveCollectionSheetSessionCache {

    private final CustomerPersistence customerPersistence = new CustomerPersistence();

    // lists of non-zero accounts
    private List<Integer> customerAccounts = new ArrayList<Integer>();
    private List<Integer> loanAccounts = new ArrayList<Integer>();
    private List<Integer> savingsAccounts = new ArrayList<Integer>();
    private List<Integer> allAccounts = new ArrayList<Integer>();

    private Boolean worthCaching = false;
    private Boolean worthCachingRepayments = false;
    private Boolean worthCachingDisbursals = false;
    private Boolean worthCachingACCollections = false;
    private Boolean worthCachingSavings = false;

    private Long prefetchTotalTime = null;

    private Long prefetchCustomerHierarchyTotalTime = null;
    private Integer prefetchCustomerHierarchyCount = null;
    private Long prefetchAccountDataTotalTime = null;
    private Integer prefetchAccountDataCount = null;
    private Long prefetchLoanSchedulesTotalTime = null;
    private Integer prefetchLoanSchedulesCount = null;
    private Long prefetchAccountFeeDetailsTotalTime = null;
    private Integer prefetchAccountFeeDetailsCount = null;
    private Long prefetchCustomerSchedulesTotalTime = null;
    private Integer prefetchCustomerSchedulesCount = null;

    public void loadSessionCacheWithCollectionSheetData(final SaveCollectionSheetDto saveCollectionSheet,
            Short branchId, String searchId) {

        Long prefetchTotalTimeStart = System.currentTimeMillis();
        Long sTime;
        Boolean allowDataPrefetching = GeneralConfig.getAllowDataPrefetchingWhenSavingCollectionSheets();

        if (allowDataPrefetching) {
            List<Object> prefetchObjectList = null;

            analyseSaveCollectionSheet(saveCollectionSheet);

            if (worthCaching) {

                sTime = System.currentTimeMillis();
                prefetchObjectList = submitSavePreFetch("prefetchCustomerHierarchy", branchId, searchId, null);
                prefetchCustomerHierarchyTotalTime = System.currentTimeMillis() - sTime;
                prefetchCustomerHierarchyCount = prefetchObjectList.size();

                makeNonZeroAccountLists(saveCollectionSheet.getSaveCollectionSheetCustomers());

                if (allAccounts.size() > 0) {
                    sTime = System.currentTimeMillis();
                    prefetchObjectList = submitSavePreFetch("prefetchAccountData", branchId, searchId, allAccounts);
                    prefetchAccountDataTotalTime = System.currentTimeMillis() - sTime;
                    prefetchAccountDataCount = prefetchObjectList.size();
                }

                if (worthCachingRepayments || worthCachingDisbursals) {
                    sTime = System.currentTimeMillis();
                    prefetchObjectList = submitSavePreFetch("prefetchLoanSchedules", branchId, searchId, loanAccounts);
                    prefetchLoanSchedulesTotalTime = System.currentTimeMillis() - sTime;
                    prefetchLoanSchedulesCount = prefetchObjectList.size();
                }

                if (worthCachingRepayments || worthCachingDisbursals || worthCachingACCollections) {
                    sTime = System.currentTimeMillis();
                    prefetchObjectList = submitSavePreFetch("prefetchAccountFeeDetails", branchId, searchId,
                            allAccounts);
                    prefetchAccountFeeDetailsTotalTime = System.currentTimeMillis() - sTime;
                    prefetchAccountFeeDetailsCount = prefetchObjectList.size();
                }

                if (worthCachingACCollections) {
                    sTime = System.currentTimeMillis();
                    prefetchObjectList = submitSavePreFetch("prefetchCustomerSchedules", branchId, searchId,
                            customerAccounts);
                    prefetchCustomerSchedulesTotalTime = System.currentTimeMillis() - sTime;
                    prefetchCustomerSchedulesCount = prefetchObjectList.size();
                }

                prefetchTotalTime = System.currentTimeMillis() - prefetchTotalTimeStart;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<Object> submitSavePreFetch(final String queryName, final Short branchId, final String searchId,
            final List<Integer> accountIds) {

        Session session = customerPersistence.getHibernateUtil().getSessionTL();
        Query query = session.getNamedQuery(queryName);
        query.setParameter("BRANCH_ID", branchId);
        query.setParameter("SEARCH_ID", searchId);
        query.setParameter("SEARCH_ID2", searchId + ".%");
        if (accountIds != null) {
            // query should fall over if empty list passed through
            if (accountIds.size() == 0) {
                throw new MifosRuntimeException("Empty Account Id List for Query: " + queryName);
            }
            query.setParameterList("ACCOUNT_IDS", accountIds);
        }

        return query.list();
    }

    private void analyseSaveCollectionSheet(SaveCollectionSheetDto saveCollectionSheet) {

        if (saveCollectionSheet.countOneLevelUnder() + saveCollectionSheet.countTwoLevelsUnder() > 2) {
            worthCaching = true;

            if (saveCollectionSheet.countCustomerAccounts() > 2) {
                worthCachingACCollections = true;
            }
            if (saveCollectionSheet.countLoanDisbursements() > 2) {
                worthCachingDisbursals = true;
            }
            if (saveCollectionSheet.countLoanRepayments() > 2) {
                worthCachingRepayments = true;
            }

        }
    }

    private void makeNonZeroAccountLists(List<SaveCollectionSheetCustomerDto> saveCollectionSheetCustomers) {

        if (saveCollectionSheetCustomers != null && saveCollectionSheetCustomers.size() > 0) {
            for (SaveCollectionSheetCustomerDto saveCollectionSheetCustomer : saveCollectionSheetCustomers) {

                SaveCollectionSheetCustomerAccountDto saveCollectionSheetCustomerAccount = saveCollectionSheetCustomer
                        .getSaveCollectionSheetCustomerAccount();
                if (null != saveCollectionSheetCustomerAccount) {
                    if (saveCollectionSheetCustomerAccount.getTotalCustomerAccountCollectionFee().compareTo(
                            BigDecimal.ZERO) > 0) {
                        customerAccounts.add(saveCollectionSheetCustomerAccount.getAccountId());
                    }
                }

                List<SaveCollectionSheetCustomerLoanDto> saveCollectionSheetCustomerLoans = saveCollectionSheetCustomer
                        .getSaveCollectionSheetCustomerLoans();
                if (null != saveCollectionSheetCustomerLoans && saveCollectionSheetCustomerLoans.size() > 0) {
                    for (SaveCollectionSheetCustomerLoanDto saveCollectionSheetCustomerLoan : saveCollectionSheetCustomerLoans) {

                        if ((saveCollectionSheetCustomerLoan.getTotalDisbursement().compareTo(BigDecimal.ZERO) > 0)
                                || (saveCollectionSheetCustomerLoan.getTotalLoanPayment().compareTo(BigDecimal.ZERO) > 0)) {
                            loanAccounts.add(saveCollectionSheetCustomerLoan.getAccountId());
                        }
                    }
                }

                List<SaveCollectionSheetCustomerSavingDto> saveCollectionSheetCustomerSavings = saveCollectionSheetCustomer
                        .getSaveCollectionSheetCustomerSavings();
                if (null != saveCollectionSheetCustomerSavings && saveCollectionSheetCustomerSavings.size() > 0) {
                    for (SaveCollectionSheetCustomerSavingDto saveCollectionSheetCustomerSaving : saveCollectionSheetCustomerSavings) {

                        if ((saveCollectionSheetCustomerSaving.getTotalWithdrawal().compareTo(BigDecimal.ZERO) > 0)
                                || (saveCollectionSheetCustomerSaving.getTotalDeposit().compareTo(BigDecimal.ZERO) > 0)) {
                            savingsAccounts.add(saveCollectionSheetCustomerSaving.getAccountId());
                        }
                    }
                }

            }
        }

        allAccounts.addAll(customerAccounts);
        allAccounts.addAll(loanAccounts);
        allAccounts.addAll(savingsAccounts);
    }

    public Long getPrefetchTotalTime() {
        return this.prefetchTotalTime;
    }

    public Long getPrefetchCustomerHierarchyTotalTime() {
        return this.prefetchCustomerHierarchyTotalTime;
    }

    public Integer getPrefetchCustomerHierarchyCount() {
        return this.prefetchCustomerHierarchyCount;
    }

    public Long getPrefetchAccountDataTotalTime() {
        return this.prefetchAccountDataTotalTime;
    }

    public Integer getPrefetchAccountDataCount() {
        return this.prefetchAccountDataCount;
    }

    public Long getPrefetchLoanSchedulesTotalTime() {
        return this.prefetchLoanSchedulesTotalTime;
    }

    public Integer getPrefetchLoanSchedulesCount() {
        return this.prefetchLoanSchedulesCount;
    }

    public Long getPrefetchAccountFeeDetailsTotalTime() {
        return this.prefetchAccountFeeDetailsTotalTime;
    }

    public Integer getPrefetchAccountFeeDetailsCount() {
        return this.prefetchAccountFeeDetailsCount;
    }

    public Long getPrefetchCustomerSchedulesTotalTime() {
        return this.prefetchCustomerSchedulesTotalTime;
    }

    public Integer getPrefetchCustomerSchedulesCount() {
        return this.prefetchCustomerSchedulesCount;
    }

}
