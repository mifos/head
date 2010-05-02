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

import java.util.List;

import org.mifos.framework.exceptions.ServiceException;
import org.mifos.reports.business.dao.SelectionItemDao;
import org.mifos.reports.ui.SelectionItem;

public class ReportsParameterService implements IReportsParameterService {

    private SelectionItemDao selectionItemDao;

    public ReportsParameterService(final SelectionItemDao selectionItemDao) {
        this.selectionItemDao = selectionItemDao;
    }

    @Override
    public List<SelectionItem> getActiveBranchesUnderUser(String officeSearchId) throws ServiceException {
        return selectionItemDao.getActiveBranchesUnderUser(officeSearchId);
    }

    @Override
    public List<SelectionItem> getActiveCentersUnderUser(Integer branchId, Integer loanOfficerId)
            throws ServiceException {

        return selectionItemDao.getActiveCentersUnderUser(branchId, loanOfficerId);
    }

    @Override
    public List<SelectionItem> getActiveLoanOfficersUnderOffice(Integer branchId) throws ServiceException {
        return selectionItemDao.getActiveLoanOfficersUnderOffice(branchId);
    }

//    @Override
//    public List<DateSelectionItem> getMeetingDates(Integer branchId, Integer loanOfficerId, Integer centerId, Date from)
//            throws ServiceException {
//        return selectionItemDao.getMeetingDates(branchId, loanOfficerId, centerId, from);
//    }

    /*
     * dummy method called to invalidate the cache
     */
    @Override
    public boolean invalidate() {
        return true;
    }
}