package org.mifos.application.office.business.service;

import java.util.List;

import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.OfficeView;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;

public class OfficeBusinessService extends BusinessService {

	private OfficePersistence officePersistence = new OfficePersistence();

	@Override
	public BusinessObject getBusinessObject(UserContext userContext) {
		return null;
	}

	public List<OfficeView> getActiveParents(OfficeLevel level, Short localeId)
			throws ServiceException {
		try {
			return officePersistence.getActiveParents(level, localeId);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public List<OfficeView> getConfiguredLevels(Short localeId)
			throws ServiceException {
		try {
			return officePersistence.getActiveLevels(localeId);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}

	}

	public OfficeBO getOffice(Short officeId) throws ServiceException {
		try {
			return officePersistence.getOffice(officeId);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public List<OfficeView> getStatusList(Short localeId)
			throws ServiceException {
		try {
			return officePersistence.getStatusList(localeId);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public List<OfficeBO> getOfficesTillBranchOffice() throws ServiceException {
		try {
			return officePersistence.getOfficesTillBranchOffice();
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public List<OfficeBO> getBranchOffices() throws ServiceException {
		try {
			return officePersistence.getBranchOffices();
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public List<OfficeView> getChildOffices(String searchId)
			throws ServiceException {
		try {
			return officePersistence.getChildOffices(searchId);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}
	public List<OfficeBO> getActiveBranchesUnderUser(PersonnelBO personnel) throws ServiceException {
		try {
			return officePersistence.getActiveBranchesUnderUser(personnel.getOfficeSearchId());
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}
}
