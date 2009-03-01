package org.mifos.application.personnel.business;

import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestPersonnelStatusEntity extends MifosTestCase {

	public TestPersonnelStatusEntity() throws SystemException, ApplicationException {
        super();
    }

    public void testGetPersonnelStatusEntity() throws Exception {
		MasterPersistence masterPersistenceService = new MasterPersistence();
		PersonnelStatusEntity personnelStatusEntity = (PersonnelStatusEntity)
			masterPersistenceService.getPersistentObject(
				PersonnelStatusEntity.class, (short)1);
		assertEquals(Short.valueOf("1"),personnelStatusEntity.getId());
		personnelStatusEntity.setLocaleId(TestObjectFactory.TEST_LOCALE);
		assertEquals("Active",personnelStatusEntity.getName());
	}

}
