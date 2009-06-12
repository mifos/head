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

package org.mifos.application.reports.business.service;

import java.util.Date;
import java.util.List;

import org.mifos.application.reports.persistence.SelectionItemPersistence;
import org.mifos.application.reports.ui.DateSelectionItem;
import org.mifos.application.reports.ui.SelectionItem;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;

public class ReportsParameterService implements IReportsParameterService {

    private SelectionItemPersistence selectionItemPersistence;

    public ReportsParameterService(SelectionItemPersistence selectionItemPersistence) {
        super();
        this.selectionItemPersistence = selectionItemPersistence;
    }

    public ReportsParameterService() {
        selectionItemPersistence = new SelectionItemPersistence();
    }

    public List<SelectionItem> getActiveBranchesUnderUser(String officeSearchId) throws ServiceException {
        try {
            return selectionItemPersistence.getActiveBranchesUnderUser(officeSearchId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<SelectionItem> getActiveLoanOfficersUnderOffice(Integer branchId) throws ServiceException {
        try {
            return selectionItemPersistence.getActiveLoanOfficersUnderOffice(branchId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<SelectionItem> getActiveCentersUnderUser(Integer branchId, Integer loanOfficerId)
            throws ServiceException {
        try {
            return selectionItemPersistence.getActiveCentersUnderUser(branchId, loanOfficerId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<DateSelectionItem> getMeetingDates(Integer branchId, Integer loanOfficerId, Integer centerId, Date from)
            throws ServiceException {
        try {
            return selectionItemPersistence.getMeetingDates(branchId, loanOfficerId, centerId, from);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    // dummy method called to invalidate the cache
    public boolean invalidate() {
        return true;
    }
}
