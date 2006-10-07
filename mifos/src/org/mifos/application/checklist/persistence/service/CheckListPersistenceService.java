package org.mifos.application.checklist.persistence.service;

import org.mifos.application.checklist.business.CheckListBO;
import org.mifos.application.checklist.persistence.CheckListPersistence;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.service.PersistenceService;

public class CheckListPersistenceService extends PersistenceService {
	
	private CheckListPersistence checkListPersistence ;

	public CheckListPersistenceService() {
		checkListPersistence = new CheckListPersistence();
	}
	
	public void save(CheckListBO checkListBO) {
		//checkListPersistence.createOrUpdate(checkListBO);
	}

	public CheckListBO get(Short checkListId) throws PersistenceException
	{
		return checkListPersistence.get(checkListId);
	}
	
	public void delete(CheckListBO checkListBO)
	{
		//checkListPersistence.delete(checkListBO);
	}
	
	public void update(CheckListBO checkListBO)
	{
		//checkListPersistence.update(checkListBO);
	}
}
