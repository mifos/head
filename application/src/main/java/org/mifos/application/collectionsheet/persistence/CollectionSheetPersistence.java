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

package org.mifos.application.collectionsheet.persistence;

import static org.mifos.application.NamedQueryConstants.COLLECTION_SHEET_CUSTOMERS_WITH_SPECIFIED_MEETING_DATE_AS_SQL;
import static org.mifos.application.NamedQueryConstants.COLLECTION_SHEET_CUSTOMER_LOANS_WITH_SPECIFIED_MEETING_DATE_AS_SQL;
import static org.mifos.application.NamedQueryConstants.COLLECTION_SHEET_CUSTOMER_SAVINGSS_WITH_SPECIFIED_MEETING_DATE_AS_SQL;
import static org.mifos.application.NamedQueryConstants.CUSTOMER_SCHEDULE_GET_SCHEDULE_FOR_IDS;
import static org.mifos.application.NamedQueryConstants.LOAN_SCHEDULE_GET_SCHEDULE_FOR_IDS;
import static org.mifos.application.NamedQueryConstants.SAVING_SCHEDULE_GET_SCHEDULE_FOR_IDS;
import static org.mifos.framework.util.CollectionUtils.splitListIntoParts;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.mifos.application.NamedQueryConstants;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.application.collectionsheet.util.helpers.CollectionSheetConstants;
import org.mifos.application.customer.util.helpers.QueryParamConstants;
import org.mifos.application.servicefacade.CollectionSheetService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.Persistence;

/**
 * no longer used in relation to Collection sheet functionality
 * 
 * @see CollectionSheetService#retrieveCollectionSheet(Integer, java.util.Date)
 */
@Deprecated
public class CollectionSheetPersistence extends Persistence {

    public CollectionSheetPersistence() {
        super();
    }

    /**
     * The query returns all rows where meeting date is the same as the date
     * parameter and the status of the customer is either active or hold. Also
     * they should have at least one active loan or Savings or Customer account
     */
    public List<AccountActionDateEntity> getCustFromAccountActionsDate(final Date date) throws PersistenceException {
        Map<String, Object> params = Collections.singletonMap(CollectionSheetConstants.MEETING_DATE, (Object) date);
        List<List<String>> actionDateQueries = new ArrayList<List<String>>();
        actionDateQueries.add(Arrays.asList(COLLECTION_SHEET_CUSTOMERS_WITH_SPECIFIED_MEETING_DATE_AS_SQL,
                CUSTOMER_SCHEDULE_GET_SCHEDULE_FOR_IDS));
        actionDateQueries.add(Arrays.asList(COLLECTION_SHEET_CUSTOMER_LOANS_WITH_SPECIFIED_MEETING_DATE_AS_SQL,
                LOAN_SCHEDULE_GET_SCHEDULE_FOR_IDS));
        actionDateQueries.add(Arrays.asList(COLLECTION_SHEET_CUSTOMER_SAVINGSS_WITH_SPECIFIED_MEETING_DATE_AS_SQL,
                SAVING_SCHEDULE_GET_SCHEDULE_FOR_IDS));
        List<AccountActionDateEntity> accountActionDate = new ArrayList<AccountActionDateEntity>();
        for (List<String> list : actionDateQueries) {
            accountActionDate.addAll(convertIdsToObjectUsingQuery(executeNamedQuery(list.get(0), params), list.get(1)));
        }
        return accountActionDate;
    }

    /**
     * Get list of account objects which are in the state approved or disbursed
     * to loan officer and have disbursal date same as the date passed.
     */
    public List<LoanBO> getLnAccntsWithDisbursalDate(final Date date) throws PersistenceException {
        return executeNamedQuery(NamedQueryConstants.CUSTOMERS_WITH_SPECIFIED_DISBURSAL_DATE, Collections.singletonMap(
                CollectionSheetConstants.MEETING_DATE, date));
    }

    // Hibernate cannot handle too many items in the IN CLAUSE in one shot
    // http://opensource.atlassian.com/projects/hibernate/browse/HHH-1985
    private static final int MAX_LIST_SIZE_FOR_HIBERNATE_IN_CLAUSE = 5000;

    public List convertIdsToObjectUsingQuery(final List<Integer> ids, final String queryName) {
        if (ids.isEmpty()) {
            return new ArrayList();
        }
        List<List> parts = splitListIntoParts(ids, MAX_LIST_SIZE_FOR_HIBERNATE_IN_CLAUSE);
        Query query = createdNamedQuery(queryName);
        List result = new ArrayList();
        for (List part : parts) {
            query.setParameterList(QueryParamConstants.ID_LIST, part);
            result.addAll(query.list());
        }
        return result;
    }
}
