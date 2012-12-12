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

package org.mifos.customers.util.helpers;

public interface CustomerSearchConstants {

    String GETPATHCUSTOMERSEARCH = "CustomerSearchAction";

    String PERSONNELID = "personnelId";
    String OFFICEID = "officeId";
    String CUSTOMERLEVELID = "customerLevelId";
    String CUSTOMERLIST = "customerList";
    String LOADFORWARD = "LoadForward";
    Short LOADFORWARDLOANOFFICER = 1;
    String LOADFORWARDLOANOFFICER_SUCCESS = "load_branch";
    String LOADFORWARDNONLOANOFFICER_SUCCESS = "load_loanofficer";
    String LOADFORWARDOFFICE_SUCCESS = "load_success";
    String OFFICE = "Office";
    String PERSONNELLEVELID = "personnelLevelId";
    String PERSONNELSTATUSID = "personnelStatusId";
    String LOANOFFICERSLIST = "LoanOfficerslist";
    Short LOADFORWARDNONLOANOFFICER = 2;
    String OFFICELEVELID = "officeLevelId";
    String OFFICESEARCHID = "officeSearchId";
    String OFFICESLIST = "OfficesList";
    Short LOADFORWARDNONBRANCHOFFICE = 3;
    String SEARCH_SUCCESS = "search_sucess";
    String CUSTOMERSEARCHRESULTS = "CustomerSearchResults";
    String CUSTOMERSEARCSTRING = "searchString";
    String CUSTOMER_SEARCH_OFFICE_ID = "search_officeId";
    String CUSTOMERSEARCBRANCH = "searchBranch";
    String LOADALLBRANCHES_SUCCESS = "loadallbranches_success";
    String CENTER_ACTIVE = "CENTERACTIVE";
    String GROUP_ACTIVE = "GROUPACTIVE";
    String GROUP_ONHOLD = "GROUPONHOLD";
    Short CENTERACTIVE = 13;
    Short GROUPACTIVE = 9;
    Short GROUPONHOLD = 10;

    String CENTERSEARCH = "centerSearch";
    String GROUPLIST = "GroupList";
    String ACCOUNTSEARCHRESULTS = "AccountSearchResultsDto";
    String CUSTOMERSFORSAVINGSACCOUNT = "customersForSavingsAccount";
    String LOANACCOUNTIDSEARCH = "loanAccountIdSearch";
    String IDSEARCH = "IdSearch";

    // Exceptions

    String NAMEMANDATORYEXCEPTION = "errors.namemandatory";
    String OFFICEIDMANDATORYEXCEPTION = "errors.idmandatory";

    short LOAN_TYPE = (short) 1;
    short SAVINGS_TYPE = (short) 2;
    short GROUP_LOAN_TYPE = (short) 5;

}
