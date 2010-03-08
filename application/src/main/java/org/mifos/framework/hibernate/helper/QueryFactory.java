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

package org.mifos.framework.hibernate.helper;

import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.customers.util.helpers.CustomerSearchConstants;

/**
 * This is the interface that is returned on a search operation. Search would
 * typically result in a set of search result objects , these search result
 * objects would be obtained through hibernate scroll for pagination in the
 * front end , the associate hibernate session would be held in this object , a
 * call to close from the front end on this interface would result in the
 * hibernate session object getting closed.
 */
public class QueryFactory {
    /**
     * Return the QueryResult which will be used for query execution
     *
     * @param searchName
     * @return QueryResult
     */
    public static QueryResult getQueryResult(String searchName) {
        if (searchName.equals(CustomerSearchConstants.LOANACCOUNTIDSEARCH)) {
            return new QueryResultAccountIdSearch();
        } else if (searchName.equals(CustomerSearchConstants.CUSTOMERSEARCHRESULTS)) {
            return new QueryResultsMainSearchImpl();
        }

        else if (searchName.equals(PersonnelConstants.USER_LIST)
                || searchName.equals(CustomerSearchConstants.CENTERSEARCH)
                || searchName.equals(CustomerSearchConstants.GROUPLIST)
                || searchName.equals(CustomerSearchConstants.ACCOUNTSEARCHRESULTS)
                || searchName.equals(CustomerSearchConstants.CUSTOMERSFORSAVINGSACCOUNT)) {
            return new QueryResultSearchDTOImpl();
        } else {
            return new QueryResultDTOImpl();
        }
    }
}
