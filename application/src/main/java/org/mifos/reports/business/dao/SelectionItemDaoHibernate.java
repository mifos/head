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

package org.mifos.reports.business.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.customers.office.util.helpers.OfficeLevel;
import org.mifos.customers.office.util.helpers.OfficeStatus;
import org.mifos.customers.personnel.util.helpers.PersonnelLevel;
import org.mifos.customers.personnel.util.helpers.PersonnelStatus;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.reports.ui.SelectionItem;

public class SelectionItemDaoHibernate implements SelectionItemDao {

    private final GenericDao genericDao;

    public SelectionItemDaoHibernate(final GenericDao genericDao) {
        this.genericDao = genericDao;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SelectionItem> getActiveBranchesUnderUser(final String officeSearchId) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("officeLevelId", OfficeLevel.BRANCHOFFICE.getValue());
        queryParameters.put("officeSearchId", officeSearchId + "%");
        queryParameters.put("ACTIVE", OfficeStatus.ACTIVE.getValue());
        return (List<SelectionItem>) this.genericDao.executeNamedQuery("SelectionItem.get_active_offices", queryParameters);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SelectionItem> getActiveCentersUnderUser(final Integer branchId, final Integer loanOfficerId) {

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("personnelId", loanOfficerId);
        queryParameters.put("officeId", branchId);
        queryParameters.put("customerLevelId", CustomerLevel.CENTER.getValue());
        queryParameters.put("ACTIVE", CustomerStatus.CENTER_ACTIVE.getValue());

        return (List<SelectionItem>) this.genericDao.executeNamedQuery("SelectionItem.get_active_customers_under_loanofficers", queryParameters);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SelectionItem> getActiveLoanOfficersUnderOffice(final Integer officeId) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("officeId", officeId);
        queryParameters.put("personnelLevelId", PersonnelLevel.LOAN_OFFICER.getValue());
        queryParameters.put("ACTIVE", PersonnelStatus.ACTIVE.getValue());
        return (List<SelectionItem>) this.genericDao.executeNamedQuery("SelectionItem.get_active_loanofficers_under_office", queryParameters);
    }

//    @SuppressWarnings("unchecked")
//    @Override
//    public List<DateSelectionItem> getMeetingDates(Integer branchId, Integer loanOfficerId, Integer customerId,
//            Date from) {
//        Map<String, Object> queryParameters = new HashMap<String, Object>();
//        queryParameters.put("COLL_SHEET_RUN_STATUS", CollectionSheetConstants.COLLECTION_SHEET_GENERATION_SUCCESSFUL);
//        queryParameters.put("customerId", customerId);
//        queryParameters.put("CUSTOMER_LEVEL", CustomerLevel.CENTER.getValue());
//        queryParameters.put("branchId", branchId);
//        queryParameters.put("loanOfficerId", loanOfficerId);
//        queryParameters.put("fromDate", from);
//        return (List<DateSelectionItem>) this.genericDao.executeNamedQuery(
//                "DateSelectionItem.get_meeting_dates", queryParameters);
//    }
}