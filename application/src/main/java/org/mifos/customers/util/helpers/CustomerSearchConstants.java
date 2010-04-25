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

package org.mifos.customers.util.helpers;

public interface CustomerSearchConstants {

    public String GETPATHCUSTOMERSEARCH = "CustomerSearchAction";

    public String PERSONNELID = "personnelId";
    public String OFFICEID = "officeId";
    public String CUSTOMERLEVELID = "customerLevelId";
    public String CUSTOMERLIST = "customerList";
    public String LOADFORWARD = "LoadForward";
    public Short LOADFORWARDLOANOFFICER = 1;
    public String LOADFORWARDLOANOFFICER_SUCCESS = "load_branch";
    public String LOADFORWARDNONLOANOFFICER_SUCCESS = "load_loanofficer";
    public String LOADFORWARDOFFICE_SUCCESS = "load_success";
    public String OFFICE = "Office";
    public String PERSONNELLEVELID = "personnelLevelId";
    public String PERSONNELSTATUSID = "personnelStatusId";
    public String LOANOFFICERSLIST = "LoanOfficerslist";
    public Short LOADFORWARDNONLOANOFFICER = 2;
    public String OFFICELEVELID = "officeLevelId";
    public String OFFICESEARCHID = "officeSearchId";
    public String OFFICESLIST = "OfficesList";
    public Short LOADFORWARDNONBRANCHOFFICE = 3;
    public String SEARCH_SUCCESS = "search_sucess";
    public String CUSTOMERSEARCHRESULTS = "CustomerSearchResults";
    public String CUSTOMERSEARCSTRING = "searchString";
    public String CUSTOMER_SEARCH_OFFICE_ID = "search_officeId";
    public String CUSTOMERSEARCBRANCH = "searchBranch";
    public String LOADALLBRANCHES_SUCCESS = "loadallbranches_success";
    public String CENTER_ACTIVE = "CENTERACTIVE";
    public String GROUP_ACTIVE = "GROUPACTIVE";
    public String GROUP_ONHOLD = "GROUPONHOLD";
    public Short CENTERACTIVE = 13;
    public Short GROUPACTIVE = 9;
    public Short GROUPONHOLD = 10;

    public String CENTERSEARCH = "centerSearch";
    public String GROUPLIST = "GroupList";
    public String ACCOUNTSEARCHRESULTS = "AccountSearchResultsDto";
    public String CUSTOMERSFORSAVINGSACCOUNT = "customersForSavingsAccount";
    public String LOANACCOUNTIDSEARCH = "loanAccountIdSearch";
    public String IDSEARCH = "IdSearch";

    // Exceptions

    public String NAMEMANDATORYEXCEPTION = "errors.namemandatory";
    public String OFFICEIDMANDATORYEXCEPTION = "errors.idmandatory";

    public short LOAN_TYPE = (short) 1;
    public short SAVINGS_TYPE = (short) 2;

}
