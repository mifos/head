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

package org.mifos.reports.business.service;

import static org.mifos.framework.util.helpers.NumberUtils.convertIntegerToShort;
import static org.mifos.framework.util.helpers.NumberUtils.convertShortToInteger;
import static org.mifos.reports.ui.SelectionItem.ALL_CENTER_SELECTION_ITEM;
import static org.mifos.reports.ui.SelectionItem.ALL_LOAN_OFFICER_SELECTION_ITEM;
import static org.mifos.reports.ui.SelectionItem.NA_BRANCH_OFFICE_SELECTION_ITEM;
import static org.mifos.reports.ui.SelectionItem.NA_CENTER_SELECTION_ITEM;
import static org.mifos.reports.ui.SelectionItem.NA_LOAN_OFFICER_SELECTION_ITEM;
import static org.mifos.reports.ui.SelectionItem.SELECT_BRANCH_OFFICE_SELECTION_ITEM;
import static org.mifos.reports.ui.SelectionItem.SELECT_CENTER_SELECTION_ITEM;
import static org.mifos.reports.ui.SelectionItem.SELECT_LOAN_OFFICER_SELECTION_ITEM;

import java.util.ArrayList;
import java.util.List;

import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.framework.util.CollectionUtils;
import org.mifos.reports.business.dao.SelectionItemDao;
import org.mifos.reports.ui.SelectionItem;

public class CascadingReportParameterService {

    private final SelectionItemDao selectionItemDao;
    private final PersonnelDao personnelDao;

    public CascadingReportParameterService(SelectionItemDao selectionItemDao, PersonnelDao personnelDao) {
        this.selectionItemDao = selectionItemDao;
        this.personnelDao = personnelDao;
    }

    public List<SelectionItem> getBranchOfficesUnderUser(PersonnelBO user) {
        if (user == null) {
            return CollectionUtils.asList(NA_BRANCH_OFFICE_SELECTION_ITEM);
        }

        List<SelectionItem> offices = selectionItemDao.getActiveBranchesUnderUser(user.getOfficeSearchId());

        if (offices.isEmpty()) {
            return CollectionUtils.asList(NA_BRANCH_OFFICE_SELECTION_ITEM);
        }

        ArrayList<SelectionItem> branchOffices = new ArrayList<SelectionItem>();
        branchOffices.add(SELECT_BRANCH_OFFICE_SELECTION_ITEM);
        branchOffices.addAll(offices);
        return branchOffices;
    }

    public List<SelectionItem> getActiveLoanOfficersUnderUserInBranch(PersonnelBO user, Integer branchId) {
        List<SelectionItem> officers = new ArrayList<SelectionItem>();

        if (NA_BRANCH_OFFICE_SELECTION_ITEM.sameAs(branchId)) {
            officers.add(NA_LOAN_OFFICER_SELECTION_ITEM);
            return officers;
        }
        if (branchId == null || SELECT_BRANCH_OFFICE_SELECTION_ITEM.sameAs(branchId)) {
            officers.add(SELECT_LOAN_OFFICER_SELECTION_ITEM);
            return officers;
        }

        if (user == null) {
            officers.add(NA_LOAN_OFFICER_SELECTION_ITEM);
            return officers;
        }

        if (user.isLoanOfficer()) {
            officers.add(SELECT_LOAN_OFFICER_SELECTION_ITEM);
            officers.add(new SelectionItem(convertShortToInteger(user.getPersonnelId()), user.getDisplayName()));
            return officers;
        }

        List<SelectionItem> loanOfficers = selectionItemDao.getActiveLoanOfficersUnderOffice(branchId);
        officers.add(loanOfficers.isEmpty() ? NA_LOAN_OFFICER_SELECTION_ITEM : ALL_LOAN_OFFICER_SELECTION_ITEM);
        officers.addAll(loanOfficers);
        return officers;
    }

    public List<SelectionItem> getActiveCentersInBranchForLoanOfficer(Integer branchId, Integer loanOfficerId) {
        List<SelectionItem> activeCenters = new ArrayList<SelectionItem>();
        if (loanOfficerId == null || SELECT_LOAN_OFFICER_SELECTION_ITEM.sameAs(loanOfficerId)) {
            return CollectionUtils.asList(SELECT_CENTER_SELECTION_ITEM);
        }

        if (NA_LOAN_OFFICER_SELECTION_ITEM.sameAs(loanOfficerId)) {
            return CollectionUtils.asList(NA_CENTER_SELECTION_ITEM);
        }

        List<SelectionItem> loanOfficers = null;
        if (ALL_LOAN_OFFICER_SELECTION_ITEM.sameAs(loanOfficerId)) {
            loanOfficers = selectionItemDao.getActiveLoanOfficersUnderOffice(branchId);
        } else {
            PersonnelBO loanOfficer = this.personnelDao.findPersonnelById(loanOfficerId.shortValue());

            loanOfficers = CollectionUtils.asList(new SelectionItem(
                    convertShortToInteger(loanOfficer.getPersonnelId()), loanOfficer.getDisplayName()));
        }

        List<SelectionItem> allCentersUnderBranch = new ArrayList<SelectionItem>();
        for (SelectionItem loanOfficer : loanOfficers) {
            List<SelectionItem> activeCentersUnderUser = selectionItemDao.getActiveCentersUnderUser(branchId,
                    loanOfficer.getId());
            allCentersUnderBranch.addAll(activeCentersUnderUser);
        }
        activeCenters.add(allCentersUnderBranch.isEmpty() ? NA_CENTER_SELECTION_ITEM : ALL_CENTER_SELECTION_ITEM);
        activeCenters.addAll(allCentersUnderBranch);
        return activeCenters;
    }

    public List<SelectionItem> getBranchOffices(Integer userId) {
        return getBranchOfficesUnderUser(this.personnelDao.findPersonnelById(convertIntegerToShort(userId)));
    }

    public List<SelectionItem> getActiveLoanOfficers(Integer userId, Integer branchId) {
        return getActiveLoanOfficersUnderUserInBranch(this.personnelDao.findPersonnelById(convertIntegerToShort(userId)), branchId);
    }

    public List<SelectionItem> getActiveCentersForLoanOfficer(Integer loanOfficerId, Integer branchId) {
        return getActiveCentersInBranchForLoanOfficer(branchId, loanOfficerId);
    }
}