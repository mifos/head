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
package org.mifos.application.accounts.savings.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.mifos.application.servicefacade.CollectionSheetCustomerSavingDto;
import org.mifos.application.servicefacade.CustomerHierarchyParams;

/**
 *
 */
public class SavingsDaoHibernate implements SavingsDao {

    private final GenericDao baseDao;

    public SavingsDaoHibernate(final GenericDao baseDao) {
        this.baseDao = baseDao;
    }

    @SuppressWarnings("unchecked")
    public List<CollectionSheetCustomerSavingDto> findAllMandatorySavingAccountsForClientsOrGroupsWithCompleteGroupStatusForCustomerHierarchy(
            final CustomerHierarchyParams customerHierarchyParams) {

        final Map<String, Object> topOfHierarchyParameters = new HashMap<String, Object>();
        topOfHierarchyParameters.put("CUSTOMER_ID", customerHierarchyParams.getCustomerAtTopOfHierarchyId());
        topOfHierarchyParameters.put("TRANSACTION_DATE", customerHierarchyParams.getTransactionDate().toString());

        final List<CollectionSheetCustomerSavingDto> mandatorySavingsOnRootCustomer = (List<CollectionSheetCustomerSavingDto>) baseDao
                .executeNamedQueryWithResultTransformer(
                        "findAllMandatorySavingAccountsForClientsOrGroupsWithCompleteGroupStatusForTopOfCustomerHierarchy",
                        topOfHierarchyParameters, CollectionSheetCustomerSavingDto.class);

        final Map<String, Object> restOfHierarchyParameters = new HashMap<String, Object>();
        restOfHierarchyParameters.put("BRANCH_ID", customerHierarchyParams.getBranchId());
        restOfHierarchyParameters.put("SEARCH_ID", customerHierarchyParams.getSearchId());
        restOfHierarchyParameters.put("TRANSACTION_DATE", customerHierarchyParams.getTransactionDate().toString());

        final List<CollectionSheetCustomerSavingDto> mandatorySavingsOnRestOfHierarchy = (List<CollectionSheetCustomerSavingDto>) baseDao
                .executeNamedQueryWithResultTransformer(
                        "findAllMandatorySavingAccountsForClientsOrGroupsWithCompleteGroupStatusForRestOfCustomerHierarchy",
                        restOfHierarchyParameters, CollectionSheetCustomerSavingDto.class);

        return nullSafeSavingsHierarchy(mandatorySavingsOnRootCustomer, mandatorySavingsOnRestOfHierarchy);
    }

    @SuppressWarnings("unchecked")
    public List<CollectionSheetCustomerSavingDto> findAllVoluntarySavingAccountsForClientsAndGroupsWithCompleteGroupStatusForCustomerHierarchy(
            final CustomerHierarchyParams customerHierarchyParams) {

        final Map<String, Object> topOfHierarchyParameters = new HashMap<String, Object>();
        topOfHierarchyParameters.put("CUSTOMER_ID", customerHierarchyParams.getCustomerAtTopOfHierarchyId());
        topOfHierarchyParameters.put("TRANSACTION_DATE", customerHierarchyParams.getTransactionDate().toString());

        final List<CollectionSheetCustomerSavingDto> voluntarySavingsOnRootCustomer = (List<CollectionSheetCustomerSavingDto>) baseDao
                .executeNamedQueryWithResultTransformer(
                        "findAllVoluntarySavingsAccountsForClientsAndGroupsWithCompleteGroupStatusForTopOfCustomerHierarchy",
                        topOfHierarchyParameters, CollectionSheetCustomerSavingDto.class);

        final Map<String, Object> restOfHierarchyParameters = new HashMap<String, Object>();
        restOfHierarchyParameters.put("BRANCH_ID", customerHierarchyParams.getBranchId());
        restOfHierarchyParameters.put("SEARCH_ID", customerHierarchyParams.getSearchId());
        restOfHierarchyParameters.put("TRANSACTION_DATE", customerHierarchyParams.getTransactionDate().toString());

        final List<CollectionSheetCustomerSavingDto> voluntarySavingsOnRestOfHierarchy = (List<CollectionSheetCustomerSavingDto>) baseDao
                .executeNamedQueryWithResultTransformer(
                        "findAllVoluntarySavingsAccountsForClientsAndGroupsWithCompleteGroupStatusForRestOfCustomerHierarchy",
                        restOfHierarchyParameters, CollectionSheetCustomerSavingDto.class);

        return nullSafeSavingsHierarchy(voluntarySavingsOnRootCustomer, voluntarySavingsOnRestOfHierarchy);
    }

    @SuppressWarnings("unchecked")
    public List<CollectionSheetCustomerSavingDto> findAllSavingAccountsForCentersOrGroupsWithPerIndividualStatusForCustomerHierarchy(
            final CustomerHierarchyParams customerHierarchyParams) {

        final Map<String, Object> topOfHierarchyParameters = new HashMap<String, Object>();
        topOfHierarchyParameters.put("CUSTOMER_ID", customerHierarchyParams.getCustomerAtTopOfHierarchyId());
        topOfHierarchyParameters.put("TRANSACTION_DATE", customerHierarchyParams.getTransactionDate().toString());

        final List<CollectionSheetCustomerSavingDto> centerOrPerIndividualGroupSavingsOnRootCustomer = (List<CollectionSheetCustomerSavingDto>) baseDao
                .executeNamedQueryWithResultTransformer(
                        "findAllSavingsAccountsForCentersAndGroupsWithPerIndividualStatusForTopOfCustomerHierarchy",
                        topOfHierarchyParameters, CollectionSheetCustomerSavingDto.class);

        final Map<String, Object> restOfHierarchyParameters = new HashMap<String, Object>();
        restOfHierarchyParameters.put("BRANCH_ID", customerHierarchyParams.getBranchId());
        restOfHierarchyParameters.put("SEARCH_ID", customerHierarchyParams.getSearchId());
        restOfHierarchyParameters.put("TRANSACTION_DATE", customerHierarchyParams.getTransactionDate().toString());

        final List<CollectionSheetCustomerSavingDto> perIndividualGroupSavingsOnRestOfHierarchy = (List<CollectionSheetCustomerSavingDto>) baseDao
                .executeNamedQueryWithResultTransformer(
                        "findAllSavingsAccountsForCentersAndGroupsWithPerIndividualStatusForRestOfCustomerHierarchy",
                        restOfHierarchyParameters, CollectionSheetCustomerSavingDto.class);

        return nullSafeSavingsHierarchy(centerOrPerIndividualGroupSavingsOnRootCustomer,
                perIndividualGroupSavingsOnRestOfHierarchy);
    }

    @SuppressWarnings("unchecked")
    public List<CollectionSheetCustomerSavingDto> findAllMandatorySavingAccountsForIndividualChildrenOfCentersOrGroupsWithPerIndividualStatusForCustomerHierarchy(
            final CustomerHierarchyParams customerHierarchyParams) {

        final Map<String, Object> topOfHierarchyParameters = new HashMap<String, Object>();
        topOfHierarchyParameters.put("CUSTOMER_ID", customerHierarchyParams.getCustomerAtTopOfHierarchyId());
        topOfHierarchyParameters.put("TRANSACTION_DATE", customerHierarchyParams.getTransactionDate().toString());

        final List<CollectionSheetCustomerSavingDto> centerOrPerIndividualGroupSavingsOnRootCustomer = (List<CollectionSheetCustomerSavingDto>) baseDao
                .executeNamedQueryWithResultTransformer(
                        "findAllMandatorySavingsAccountsForCentersAndGroupsWithPerIndividualStatusForTopOfCustomerHierarchy",
                        topOfHierarchyParameters, CollectionSheetCustomerSavingDto.class);

        final Map<String, Object> restOfHierarchyParameters = new HashMap<String, Object>();
        restOfHierarchyParameters.put("BRANCH_ID", customerHierarchyParams.getBranchId());
        restOfHierarchyParameters.put("SEARCH_ID", customerHierarchyParams.getSearchId());
        restOfHierarchyParameters.put("TRANSACTION_DATE", customerHierarchyParams.getTransactionDate().toString());

        final List<CollectionSheetCustomerSavingDto> perIndividualGroupSavingsOnRestOfHierarchy = (List<CollectionSheetCustomerSavingDto>) baseDao
                .executeNamedQueryWithResultTransformer(
                        "findAllMandatorySavingsAccountsForCentersAndGroupsWithPerIndividualStatusForRestOfCustomerHierarchy",
                        restOfHierarchyParameters, CollectionSheetCustomerSavingDto.class);

        return nullSafeSavingsHierarchy(centerOrPerIndividualGroupSavingsOnRootCustomer,
                perIndividualGroupSavingsOnRestOfHierarchy);
    }

    @SuppressWarnings("unchecked")
    public List<CollectionSheetCustomerSavingDto> findAllVoluntarySavingAccountsForIndividualChildrenOfCentersOrGroupsWithPerIndividualStatusForCustomerHierarchy(
            final CustomerHierarchyParams customerHierarchyParams) {
        final Map<String, Object> topOfHierarchyParameters = new HashMap<String, Object>();
        topOfHierarchyParameters.put("CUSTOMER_ID", customerHierarchyParams.getCustomerAtTopOfHierarchyId());
        topOfHierarchyParameters.put("TRANSACTION_DATE", customerHierarchyParams.getTransactionDate().toString());

        final List<CollectionSheetCustomerSavingDto> centerOrPerIndividualGroupSavingsOnRootCustomer = (List<CollectionSheetCustomerSavingDto>) baseDao
                .executeNamedQueryWithResultTransformer(
                        "findAllVoluntarySavingsAccountsForCentersAndGroupsWithPerIndividualStatusForTopOfCustomerHierarchy",
                        topOfHierarchyParameters, CollectionSheetCustomerSavingDto.class);

        final Map<String, Object> restOfHierarchyParameters = new HashMap<String, Object>();
        restOfHierarchyParameters.put("BRANCH_ID", customerHierarchyParams.getBranchId());
        restOfHierarchyParameters.put("SEARCH_ID", customerHierarchyParams.getSearchId());
        restOfHierarchyParameters.put("TRANSACTION_DATE", customerHierarchyParams.getTransactionDate().toString());

        final List<CollectionSheetCustomerSavingDto> perIndividualGroupSavingsOnRestOfHierarchy = (List<CollectionSheetCustomerSavingDto>) baseDao
                .executeNamedQueryWithResultTransformer(
                        "findAllVoluntarySavingsAccountsForCentersAndGroupsWithPerIndividualStatusForRestOfCustomerHierarchy",
                        restOfHierarchyParameters, CollectionSheetCustomerSavingDto.class);

        return nullSafeSavingsHierarchy(centerOrPerIndividualGroupSavingsOnRootCustomer,
                perIndividualGroupSavingsOnRestOfHierarchy);
    }

    @SuppressWarnings("unchecked")
    private List<CollectionSheetCustomerSavingDto> nullSafeSavingsHierarchy(
            final List<CollectionSheetCustomerSavingDto> mandatorySavingsOnRootCustomer,
            final List<CollectionSheetCustomerSavingDto> mandatorySavingsOnRestOfHierarchy) {

        List<CollectionSheetCustomerSavingDto> nullSafeSavings = (List<CollectionSheetCustomerSavingDto>) ObjectUtils
                .defaultIfNull(mandatorySavingsOnRootCustomer, new ArrayList<CollectionSheetCustomerSavingDto>());

        List<CollectionSheetCustomerSavingDto> nullSafeRest = (List<CollectionSheetCustomerSavingDto>) ObjectUtils
                .defaultIfNull(mandatorySavingsOnRestOfHierarchy, new ArrayList<CollectionSheetCustomerSavingDto>());

        nullSafeSavings.addAll(nullSafeRest);
        return nullSafeSavings;
    }
}
