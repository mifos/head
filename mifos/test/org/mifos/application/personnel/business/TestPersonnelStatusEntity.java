package org.mifos.application.personnel.business;

import org.mifos.application.master.persistence.service.MasterPersistenceService;
import org.mifos.framework.MifosTestCase;

public class TestPersonnelStatusEntity extends MifosTestCase {
	public void testGetPersonnelStatusEntity(){
		
		MasterPersistenceService masterPersistenceService = new MasterPersistenceService();
		PersonnelStatusEntity personnelStatusEntity = (PersonnelStatusEntity)	masterPersistenceService.findById(PersonnelStatusEntity.class,Short.valueOf("1"));
		assertEquals(Short.valueOf("1"),personnelStatusEntity.getId());
		personnelStatusEntity.setLocaleId(Short.valueOf("1"));
		assertEquals("Active",personnelStatusEntity.getName());
	}
}
