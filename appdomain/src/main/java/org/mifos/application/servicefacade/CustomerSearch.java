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

package org.mifos.application.servicefacade;

import org.mifos.framework.hibernate.helper.QueryResult;

public class CustomerSearch {

    private final QueryResult searchResult;
    private final String searchString;
    private final String officeId;
    private final String officeName;

    public CustomerSearch(QueryResult searchResult, String searchString, String officeId, String officeName) {
        this.searchResult = searchResult;
        this.searchString = searchString;
        this.officeId = officeId;
        this.officeName = officeName;
    }

    public QueryResult getSearchResult() {
        return this.searchResult;
    }

    public String getSearchString() {
        return this.searchString;
    }

    public String getOfficeId() {
        return this.officeId;
    }

    public String getOfficeName() {
        return this.officeName;
    }

}
