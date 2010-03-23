/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.reports.branchreport.persistence;

import static org.mifos.customers.util.helpers.QueryParamConstants.CUSTOMER_LEVEL_ID;
import static org.mifos.customers.util.helpers.QueryParamConstants.CUSTOMER_STATUS_DESCRIPTION;
import static org.mifos.customers.util.helpers.QueryParamConstants.OFFICE_ID;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.application.NamedQueryConstants;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.customers.util.helpers.QueryParamConstants;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.Persistence;

public class BranchReportSqlPersistence extends Persistence {

    public Integer getCustomerCount(Short officeId, CustomerLevel customerLevel) throws PersistenceException {
        HashMap<String, Object> params = populateQueryParams(officeId, customerLevel);
        return getCountFromQueryResult(executeNamedQuery(NamedQueryConstants.GET_SQL_CUSTOMER_COUNT_FOR_OFFICE, params));
    }

    public Map<String, Integer> getCustomerCountBasedOnStatus(Short officeId, CustomerLevel customerLevel,
            List<String> customerStatusDescriptions) throws PersistenceException {
        Query query = createQueryForCustomerCountBasedOnStatus(
                NamedQueryConstants.GET_SQL_CUSTOMER_COUNT_BASED_ON_STATUS_FOR_OFFICE, officeId, customerLevel,
                customerStatusDescriptions);
        return extractResultFromResultset(query.list());
    }

    public Integer getActiveBorrowersCount(Short officeId, CustomerLevel customerLevel) throws PersistenceException {
        Query query = createdNamedQuery(NamedQueryConstants.GET_SQL_ACTIVE_ACCOUNT_USER_COUNT_FOR_OFFICE);
        query.setParameterList("accountStateIds", Arrays.asList(AccountState.LOAN_ACTIVE_IN_BAD_STANDING,
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING));

        Map<String, Object> params = populateParamsForActiveClientAccountSummary(officeId, customerLevel,
                AccountTypes.LOAN_ACCOUNT);
        setParametersInQuery(query, NamedQueryConstants.GET_SQL_ACTIVE_ACCOUNT_USER_COUNT_FOR_OFFICE, params);
        return getCountFromQueryResult(runQuery(query));
    }

    private Map<String, Object> populateParamsForActiveClientAccountSummary(Short officeId,
            CustomerLevel customerLevel, AccountTypes accountTypes) {
        Map<String, Object> params = populateQueryParams(officeId, customerLevel);
        params.put("accountTypeId", accountTypes.getValue());
        return params;
    }

    public Integer getVeryPoorActiveBorrowersCount(Short officeId, CustomerLevel customerLevel)
            throws PersistenceException {
        Query query = createdNamedQuery(NamedQueryConstants.GET_SQL_VERY_POOR_ACTIVE_ACCOUNT_USER_COUNT_FOR_OFFICE);
        query.setParameterList("accountStateIds", Arrays.asList(AccountState.LOAN_ACTIVE_IN_BAD_STANDING,
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING));

        Map<String, Object> params = populateParamsForActiveClientAccountSummary(officeId, customerLevel,
                AccountTypes.LOAN_ACCOUNT);
        setParametersInQuery(query, NamedQueryConstants.GET_SQL_VERY_POOR_ACTIVE_ACCOUNT_USER_COUNT_FOR_OFFICE, params);
        return getCountFromQueryResult(runQuery(query));
    }

    public Integer getActiveSaversCount(Short officeId, CustomerLevel customerLevel) throws PersistenceException {
        Map<String, Object> params = populateParamsForActiveClientAccountSummary(officeId, customerLevel,
                AccountTypes.SAVINGS_ACCOUNT);
        Query query = createdNamedQuery(NamedQueryConstants.GET_SQL_ACTIVE_ACCOUNT_USER_COUNT_FOR_OFFICE);
        query.setParameterList("accountStateIds", Arrays.asList(AccountState.SAVINGS_ACTIVE));
        setParametersInQuery(query, NamedQueryConstants.GET_SQL_ACTIVE_ACCOUNT_USER_COUNT_FOR_OFFICE, params);
        return getCountFromQueryResult(runQuery(query));
    }

    public Integer getVeryPoorActiveSaversCount(Short officeId, CustomerLevel customerLevel)
            throws PersistenceException {
        Map<String, Object> params = populateParamsForActiveClientAccountSummary(officeId, customerLevel,
                AccountTypes.SAVINGS_ACCOUNT);
        Query query = createdNamedQuery(NamedQueryConstants.GET_SQL_VERY_POOR_ACTIVE_ACCOUNT_USER_COUNT_FOR_OFFICE);
        query.setParameterList("accountStateIds", Arrays.asList(AccountState.SAVINGS_ACTIVE));
        setParametersInQuery(query, NamedQueryConstants.GET_SQL_VERY_POOR_ACTIVE_ACCOUNT_USER_COUNT_FOR_OFFICE, params);
        return getCountFromQueryResult(runQuery(query));
    }

    public Integer getReplacementCountForOffice(Short officeId, CustomerLevel customerLevel, Short fieldId,
            String fieldValue) throws PersistenceException {
        return getCountFromQueryResult(executeNamedQuery(NamedQueryConstants.GET_SQL_REPLACEMENT_COUNT_FOR_OFFICE,
                populateQueryForReplacementCount(officeId, customerLevel, fieldId, fieldValue)));
    }

    private HashMap<String, Object> populateQueryForReplacementCount(Short officeId, CustomerLevel customerLevel,
            Short fieldId, String fieldValue) {
        HashMap<String, Object> params = populateQueryParams(officeId, customerLevel);
        params.put(QueryParamConstants.CUSTOMER_CUSTOM_FIELD_ID, fieldId);
        params.put(QueryParamConstants.CUSOTMER_CUSTOM_FIELD_VALUE, fieldValue);
        return params;
    }

    public Integer getVeryPoorReplaceCountForOffice(Short officeId, CustomerLevel customerLevel, Short fieldId,
            String fieldValue) throws PersistenceException {
        return getCountFromQueryResult(executeNamedQuery(
                NamedQueryConstants.GET_SQL_VERY_POOR_REPLACEMENT_COUNT_FOR_OFFICE, populateQueryForReplacementCount(
                        officeId, customerLevel, fieldId, fieldValue)));
    }

    private Map<String, Object> populateParamsForActiveClientAccountSummary(Short officeId,
            CustomerLevel customerLevel, AccountTypes accountTypes, AccountState accountState) {
        Map<String, Object> params = populateQueryParams(officeId, customerLevel);
        params.put("accountTypeId", accountTypes.getValue());
        params.put("accountStateId", accountState.getValue());
        return params;
    }

    private HashMap<String, Object> populateQueryParams(Short officeId, CustomerLevel customerLevel) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put(OFFICE_ID, officeId);
        params.put(CUSTOMER_LEVEL_ID, customerLevel.getValue());
        return params;
    }

    private Query createQueryForCustomerCountBasedOnStatus(String queryName, Short officeId,
            CustomerLevel customerLevel, List<String> customerStatusDescriptions) {
        Query query = createdNamedQuery(queryName);
        query.setParameterList(CUSTOMER_STATUS_DESCRIPTION, customerStatusDescriptions);
        query.setProperties(populateQueryParams(officeId, customerLevel));
        return query;
    }

    public Map<String, Integer> getVeryPoorCustomerCountBasedOnStatus(Short officeId, CustomerLevel customerLevel,
            List<String> customerStatuseDescriptions) {
        Query query = createQueryForCustomerCountBasedOnStatus(
                NamedQueryConstants.GET_SQL_VERY_POOR_CUSTOMER_COUNT_BASED_ON_STATUS_FOR_OFFICE, officeId,
                customerLevel, customerStatuseDescriptions);
        return extractResultFromResultset(query.list());
    }

    public Map<String, Integer> extractResultFromResultset(List resultSet) {
        Map<String, Integer> returnValues = new HashMap<String, Integer>();
        List<Object[]> results = resultSet;
        for (Object[] objects : results) {
            returnValues.put((String) objects[0], (Integer) objects[1]);
        }
        return returnValues;
    }
}
