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

package org.mifos.application.reports.persistence;

import static org.mifos.application.NamedQueryConstants.GET_ACTIVE_BRANCHES_AS_SELECTION_ITEM;
import static org.mifos.application.NamedQueryConstants.GET_ACTIVE_CUSTOMERS_UNDER_LOAN_OFFICER_AS_SELECTION_ITEM;
import static org.mifos.application.NamedQueryConstants.GET_ACTIVE_LOAN_OFFICERS_UNDER_OFFICE_AS_SELECTION_ITEM;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.application.NamedQueryConstants;
import org.mifos.application.collectionsheet.util.helpers.CollectionSheetConstants;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.customers.office.util.helpers.OfficeLevel;
import org.mifos.customers.office.util.helpers.OfficeStatus;
import org.mifos.customers.personnel.util.helpers.PersonnelLevel;
import org.mifos.customers.personnel.util.helpers.PersonnelStatus;
import org.mifos.application.reports.ui.DateSelectionItem;
import org.mifos.application.reports.ui.SelectionItem;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.Persistence;

public class SelectionItemPersistence extends Persistence {
    public List<SelectionItem> getActiveBranchesUnderUser(String officeSearchId) throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("officeLevelId", OfficeLevel.BRANCHOFFICE.getValue());
        queryParameters.put("officeSearchId", officeSearchId + "%");
        queryParameters.put("ACTIVE", OfficeStatus.ACTIVE.getValue());
        return executeNamedQuery(GET_ACTIVE_BRANCHES_AS_SELECTION_ITEM, queryParameters);
    }

    public List<SelectionItem> getActiveLoanOfficersUnderOffice(Integer officeId) throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("officeId", officeId);
        queryParameters.put("personnelLevelId", PersonnelLevel.LOAN_OFFICER.getValue());
        queryParameters.put("ACTIVE", PersonnelStatus.ACTIVE.getValue());
        return executeNamedQuery(GET_ACTIVE_LOAN_OFFICERS_UNDER_OFFICE_AS_SELECTION_ITEM, queryParameters);
    }

    public List<SelectionItem> getActiveCentersUnderUser(Integer branchId, Integer loanOfficerId)
            throws PersistenceException {
        HashMap<String, Object> queryParameters = populateCustomerQueryParams(branchId, loanOfficerId,
                CustomerLevel.CENTER.getValue(), CustomerStatus.CENTER_ACTIVE.getValue());
        return executeNamedQuery(GET_ACTIVE_CUSTOMERS_UNDER_LOAN_OFFICER_AS_SELECTION_ITEM, queryParameters);
    }

    private HashMap<String, Object> populateCustomerQueryParams(Integer branchId, Integer loanOfficerId,
            Short customerLevel, Short customerStatus) {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("personnelId", loanOfficerId);
        queryParameters.put("officeId", branchId);
        queryParameters.put("customerLevelId", customerLevel);
        queryParameters.put("ACTIVE", customerStatus);
        return queryParameters;
    }

    public List<SelectionItem> getActiveGroupsUnderUser(Integer officeId, Integer personnelId)
            throws PersistenceException {
        HashMap<String, Object> queryParameters = populateCustomerQueryParams(officeId, personnelId,
                CustomerLevel.GROUP.getValue(), CustomerStatus.GROUP_ACTIVE.getValue());
        return executeNamedQuery(NamedQueryConstants.GET_ACTIVE_CUSTOMERS_UNDER_LOAN_OFFICER_AS_SELECTION_ITEM,
                queryParameters);
    }

    public List<DateSelectionItem> getMeetingDates(Integer branchId, Integer loanOfficerId, Integer customerId,
            Date from) throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("COLL_SHEET_RUN_STATUS", CollectionSheetConstants.COLLECTION_SHEET_GENERATION_SUCCESSFUL);
        queryParameters.put("customerId", customerId);
        queryParameters.put("CUSTOMER_LEVEL", CustomerLevel.CENTER.getValue());
        queryParameters.put("branchId", branchId);
        queryParameters.put("loanOfficerId", loanOfficerId);
        queryParameters.put("fromDate", from);
        return executeNamedQuery(NamedQueryConstants.GET_MEETING_DATES_AS_SELECTION_ITEM, queryParameters);
    }
}
