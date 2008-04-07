package org.mifos.application.checklist.business.service;

import java.util.List;

import org.mifos.application.checklist.business.AccountCheckListBO;
import org.mifos.application.checklist.business.CheckListBO;
import org.mifos.application.checklist.business.CustomerCheckListBO;
import org.mifos.application.checklist.persistence.CheckListPersistence;
import org.mifos.application.checklist.util.helpers.CheckListMasterView;
import org.mifos.application.checklist.util.helpers.CheckListStatesView;
import org.mifos.application.checklist.util.resources.CheckListConstants;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;

public class CheckListBusinessService extends BusinessService {

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
