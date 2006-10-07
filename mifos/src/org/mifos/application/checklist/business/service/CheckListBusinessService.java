package org.mifos.application.checklist.business.service;

import java.util.List;

import org.mifos.application.checklist.persistence.CheckListPersistence;
import org.mifos.application.checklist.util.helpers.CheckListMasterView;
import org.mifos.application.checklist.util.helpers.CheckListStatesView;
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
}
