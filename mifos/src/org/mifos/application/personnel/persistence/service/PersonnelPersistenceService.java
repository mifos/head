package org.mifos.application.personnel.persistence.service;

import java.util.List;

import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.service.PersistenceService;

public class PersonnelPersistenceService extends PersistenceService{
	
	private PersonnelPersistence serviceImpl = new PersonnelPersistence();
		
	public List<PersonnelView> getActiveLoanOfficersInBranch(Short levelId, Short officeId , Short userId, Short userLevelId) throws PersistenceException{
		return serviceImpl.getActiveLoanOfficersInBranch(levelId,officeId,userId,userLevelId);
	}
	
	public PersonnelBO getPersonnel(Short personnelId){
		return serviceImpl.getPersonnel(personnelId);
	}
}
