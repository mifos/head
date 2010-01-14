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

import java.util.HashMap;
import java.util.List;

import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.config.GeneralConfig;
import org.mifos.framework.exceptions.PersistenceException;

/**
 * Loads Collection Sheet data into Hibernate session cache.
 * 
 * This minimises the number of read requests required to process the Collection
 * Sheet.
 */
public class SaveCollectionSheetSessionCache {

    private final CustomerPersistence customerPersistence = new CustomerPersistence();

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

                try {

                    prefetchObjectList = submitSavePreFetch("prefetchCustomerHierarchy", branchId, searchId,
                            topCustomerId);
                    prefetchObjectList = submitSavePreFetch("prefetchAccountData", branchId, searchId, topCustomerId);

                    if (worthCachingRepayments || worthCachingDisbursals) {
                        prefetchObjectList = submitSavePreFetch("prefetchLoanSchedules", branchId, searchId,
                                topCustomerId);
                    }

                    if (worthCachingRepayments || worthCachingDisbursals || worthCachingACCollections) {
                        prefetchObjectList = submitSavePreFetch("prefetchAccountFeeDetails", branchId, searchId,
                                topCustomerId);
                    }

                    if (worthCachingACCollections) {
                        prefetchObjectList = submitSavePreFetch("prefetchCustomerSchedules", branchId, searchId,
                                topCustomerId);
                    }

                    if (worthCachingRepayments) {
                        //this next one should go in ACCollection I think and can probably be ditched like account_payment query
                        prefetchObjectList = submitSavePreFetch("prefetchCustomerActivityDetails", branchId, searchId,
                                topCustomerId);
                    }

                    if (worthCachingDisbursals) {
                        //can probably be ditched like account_payment query
                        prefetchObjectList = submitSavePreFetch("prefetchAccountStatusChangeHistory", branchId,
                                searchId, topCustomerId);
                    }

                } catch (PersistenceException e1) {
                    e1.printStackTrace();
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
            final Integer topCustomerId) throws PersistenceException {
        Long eTime;
        Long sTime = System.currentTimeMillis();

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("BRANCH_ID", branchId);
        params.put("SEARCH_ID", searchId);
        params.put("SEARCH_ID2", searchId + ".%");

        List<Object> listObject = customerPersistence.executeNamedQuery(queryName, params);
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

    private void doLog(String str) {
        System.out.println(str);
    }
}
