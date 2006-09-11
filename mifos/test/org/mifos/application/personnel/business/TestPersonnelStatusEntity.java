package org.mifos.application.personnel.business;

import org.mifos.application.master.persistence.service.MasterPersistenceService;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.PersistenceException;

public class TestPersonnelStatusEntity extends MifosTestCase {
	public void testGetPersonnelStatusEntity() throws NumberFormatException, PersistenceException{
		
		MasterPersistenceService masterPersistenceService = new MasterPersistenceService();
		PersonnelStatusEntity personnelStatusEntity = (PersonnelStatusEntity)	masterPersistenceService.findById(PersonnelStatusEntity.class,Short.valueOf("1"));
		assertEquals(Short.valueOf("1"),personnelStatusEntity.getId());
		personnelStatusEntity.setLocaleId(Short.valueOf("1"));
		assertEquals("Active",personnelStatusEntity.getName());
	}
}
