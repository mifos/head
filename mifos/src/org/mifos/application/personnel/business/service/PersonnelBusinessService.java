package org.mifos.application.personnel.business.service;

import java.util.List;

import org.mifos.application.configuration.persistence.ConfigurationPersistence;
import org.mifos.application.master.business.SupportedLocalesEntity;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.application.rolesandpermission.dao.RolesandPermissionDAO;
import org.mifos.application.rolesandpermission.util.valueobjects.Role;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.security.util.UserContext;

public class PersonnelBusinessService extends BusinessService {
	@Override
	public BusinessObject getBusinessObject(UserContext userContext) {
		return null;
	}

	public List<PersonnelView> getActiveLoanOfficersInBranch(Short officeId,
			Short loggedInUserId, Short loggedInUserLevelId)
			throws ServiceException {
		try {
			return new PersonnelPersistence().getActiveLoanOfficersInBranch(
					PersonnelLevel.LOAN_OFFICER.getValue(), officeId,
					loggedInUserId, loggedInUserLevelId);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public PersonnelBO getPersonnel(Short personnelId) throws ServiceException {

		try {
			return new PersonnelPersistence().getPersonnel(personnelId);
		} catch (PersistenceException e) {

			throw new ServiceException(e);
		}
	}

	public OfficeBO getOffice(Short officeId) throws ServiceException {
		try {
			return new OfficePersistence().getOffice(officeId);
		} catch (PersistenceException e) {

			throw new ServiceException(e);
		}
	}

	// TODO: modify this function once roles and permission migrated to m2
	public List<Role> getRoles() throws ServiceException {
		try {
			return new RolesandPermissionDAO().getRoles();
		} catch (SystemException e) {
			throw new ServiceException();
		} catch (ApplicationException e) {
			throw new ServiceException();
		}
	}

	public List<SupportedLocalesEntity> getAllLocales() throws ServiceException {
		return new ConfigurationPersistence().getSupportedLocale();
	}
	
	public QueryResult getAllPersonnelNotes(Short personnelId) throws ServiceException {
		try {
			return new PersonnelPersistence().getAllPersonnelNotes(personnelId);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}
	public PersonnelBO getPersonnelByGlobalPersonnelNum(String globalPersonnelNum)throws ServiceException{
		try {
			return new PersonnelPersistence().getPersonnelByGlobalPersonnelNum(globalPersonnelNum);
		} catch (PersistenceException e) {

			throw new ServiceException(e);
		}
	}
}
