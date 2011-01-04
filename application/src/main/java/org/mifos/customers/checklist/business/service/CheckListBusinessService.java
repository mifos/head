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

package org.mifos.customers.checklist.business.service;

import java.util.List;

import org.mifos.customers.checklist.business.AccountCheckListBO;
import org.mifos.customers.checklist.business.CheckListBO;
import org.mifos.customers.checklist.business.CustomerCheckListBO;
import org.mifos.customers.checklist.persistence.CheckListPersistence;
import org.mifos.customers.checklist.util.helpers.CheckListConstants;
import org.mifos.framework.business.AbstractBusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.security.util.UserContext;

public class CheckListBusinessService implements BusinessService {

    @Override
    public AbstractBusinessObject getBusinessObject(@SuppressWarnings("unused") UserContext userContext) {
        return null;
    }

    protected CheckListPersistence getCheckListPersistence() {
        return new CheckListPersistence();
    }

    public void isValidCheckListState(Short levelId, Short stateId, boolean isCustomer) throws ServiceException {
        try {
            Long records = getCheckListPersistence().isValidCheckListState(levelId, stateId, isCustomer);
            if (records.intValue() != 0) {
                throw new ServiceException(CheckListConstants.EXCEPTION_STATE_ALREADY_EXIST);
            }
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<CustomerCheckListBO> retreiveAllCustomerCheckLists() throws ServiceException {
        try {
            return getCheckListPersistence().retreiveAllCustomerCheckLists();
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<AccountCheckListBO> retreiveAllAccountCheckLists() throws ServiceException {
        try {
            return getCheckListPersistence().retreiveAllAccountCheckLists();
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public CheckListBO getCheckList(Short checkListId) throws ServiceException {
        try {
            return getCheckListPersistence().getCheckList(checkListId);
        } catch (PersistenceException e) {
            try {
                throw new ServiceException(e);
            } catch (ServiceException e1) {
                throw new ServiceException(e);
            }
        }

    }

}
