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
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.config.GeneralConfig;
import org.mifos.core.MifosRuntimeException;

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

    public void loadSessionCacheWithCollectionSheetData(final SaveCollectionSheetDto saveCollectionSheet,
            Short branchId, String searchId) {

        Boolean allowDataPrefetching = GeneralConfig.getAllowDataPrefetchingWhenSavingCollectionSheets();

        if (allowDataPrefetching) {
            List<Object> prefetchObjectList = null;
            Integer topCustomerId = saveCollectionSheet.getSaveCollectionSheetCustomers().get(0).getCustomerId();

            analyseSaveCollectionSheet(saveCollectionSheet);

            if (worthCaching) {
                Long eTime;
                Long sTime = System.currentTimeMillis();

                prefetchObjectList = submitSavePreFetch("prefetchCustomerHierarchy", branchId, searchId, topCustomerId,
                        null);

                makeNonZeroAccountLists(saveCollectionSheet.getSaveCollectionSheetCustomers());

                if (allAccounts.size() > 0) {
                    prefetchObjectList = submitSavePreFetch("prefetchAccountData", branchId, searchId, topCustomerId,
                            allAccounts);
                }

                if (worthCachingRepayments || worthCachingDisbursals) {
                    prefetchObjectList = submitSavePreFetch("prefetchLoanSchedules", branchId, searchId, topCustomerId,
                            loanAccounts);
                }

                if (worthCachingRepayments || worthCachingDisbursals || worthCachingACCollections) {
                    prefetchObjectList = submitSavePreFetch("prefetchAccountFeeDetails", branchId, searchId,
                            topCustomerId, allAccounts);
                }

                if (worthCachingACCollections) {
                    prefetchObjectList = submitSavePreFetch("prefetchCustomerSchedules", branchId, searchId,
                            topCustomerId, customerAccounts);
                }

                eTime = System.currentTimeMillis() - sTime;
                doLog("Id: " + topCustomerId + " - prefetch took " + eTime + "ms" + "     Worth Caching: "
                        + worthCaching.toString() + "     Worth Caching Repayments: "
                        + worthCachingRepayments.toString() + "     Worth Caching Disbursals: "
                        + worthCachingDisbursals.toString() + "     Worth Caching AC Collections: "
                        + worthCachingACCollections.toString() + "     Worth Caching Savings: "
                        + worthCachingSavings.toString());
            } else {
                doLog("Id: " + topCustomerId + " - Not worth caching");
            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<Object> submitSavePreFetch(final String queryName, final Short branchId, final String searchId,
            final Integer topCustomerId, final List<Integer> accountIds) {
        Long eTime;
        Long sTime = System.currentTimeMillis();

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

        List<Object> listObject = query.list();

        eTime = System.currentTimeMillis() - sTime;
        doLog("Id: " + topCustomerId + " - " + queryName + " took " + eTime + "ms  Count is: " + listObject.size());
        return listObject;
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

    private void doLog(String str) {
        System.out.println(str);
    }
}
