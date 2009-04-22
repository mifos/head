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
 
package org.mifos.application.checklist.business.service;

import java.util.List;

import org.mifos.application.checklist.business.AccountCheckListBO;
import org.mifos.application.checklist.business.CheckListBO;
import org.mifos.application.checklist.business.CustomerCheckListBO;
import org.mifos.application.checklist.persistence.CheckListPersistence;
import org.mifos.application.checklist.util.helpers.CheckListConstants;
import org.mifos.application.checklist.util.helpers.CheckListMasterView;
import org.mifos.application.checklist.util.helpers.CheckListStatesView;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;

public class CheckListBusinessService implements BusinessService {

	@Override
	public BusinessObject getBusinessObject(UserContext userContext) {
		return null;
	}

	public List<CheckListMasterView> getCheckListMasterData(
			UserContext userContext) throws ServiceException {
		try {
			return new CheckListPersistence()
					.getCheckListMasterData(userContext.getLocaleId());
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public List<CheckListStatesView> getCustomerStates(Short levelId,
			Short localeId) throws ServiceException {
		try {
			return new CheckListPersistence().retrieveAllCustomerStatusList(
					levelId, localeId);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public List<CheckListStatesView> getAccountStates(Short prdTypeId,
			Short localeId) throws ServiceException {
		try {
			return new CheckListPersistence().retrieveAllAccountStateList(
					prdTypeId, localeId);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public void isValidCheckListState(Short levelId, Short stateId,
			boolean isCustomer) throws ServiceException {
		try {
			Long records = new CheckListPersistence().isValidCheckListState(
					levelId, stateId, isCustomer);
			if (records.intValue() != 0)
				throw new ServiceException(
						CheckListConstants.EXCEPTION_STATE_ALREADY_EXIST);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public List<CustomerCheckListBO> retreiveAllCustomerCheckLists()
			throws ServiceException {
		try {
			return new CheckListPersistence().retreiveAllCustomerCheckLists();
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public List<AccountCheckListBO> retreiveAllAccountCheckLists()
			throws ServiceException {
		try {
			return new CheckListPersistence().retreiveAllAccountCheckLists();
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public CheckListBO getCheckList(Short checkListId) throws ServiceException {
		try {
			return new CheckListPersistence().getCheckList(checkListId);
		} catch (PersistenceException e) {
			try {
				throw new ServiceException(e);
			} catch (ServiceException e1) {
				throw new ServiceException(e);
			}
		}

	}

}
