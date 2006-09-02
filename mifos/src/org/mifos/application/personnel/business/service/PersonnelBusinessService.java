package org.mifos.application.personnel.business.service;

import java.util.ArrayList;
import java.util.List;

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
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;

public class PersonnelBusinessService extends BusinessService{
	public BusinessObject getBusinessObject(UserContext userContext) {
		return null;
	}
	
	public List<PersonnelView> getActiveLoanOfficersInBranch(Short officeId , Short loggedInUserId, Short loggedInUserLevelId){
		return new PersonnelPersistence().getActiveLoanOfficersInBranch(PersonnelLevel.LOAN_OFFICER.getValue(), officeId, loggedInUserId, loggedInUserLevelId);
	}
	
	public PersonnelBO getPersonnel(Short personnelId) {
		return new PersonnelPersistence().getPersonnel(personnelId);
	}
	public OfficeBO getOffice(Short officeId) {
		return new OfficePersistence().getOffice(officeId);
	}
	
	//TODO: modify this function once roles and permission migrated to m2
	public List<Role> getRoles() throws ServiceException{
		try {
			return new RolesandPermissionDAO().getRoles();
		} catch (SystemException e) {
			throw new ServiceException();
		} catch (ApplicationException e) {
			throw new ServiceException();
		}
	}
}
