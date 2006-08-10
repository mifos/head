package org.mifos.application.personnel.business.service;

import java.util.List;

import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
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
}
