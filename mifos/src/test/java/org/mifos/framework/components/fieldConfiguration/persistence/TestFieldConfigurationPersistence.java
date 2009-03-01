package org.mifos.framework.components.fieldConfiguration.persistence;

import java.util.List;

import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.fieldConfiguration.business.EntityMaster;
import org.mifos.framework.components.fieldConfiguration.business.FieldConfigurationEntity;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;

public class TestFieldConfigurationPersistence extends MifosTestCase {
	
	public TestFieldConfigurationPersistence() throws SystemException, ApplicationException {
        super();
    }

    private FieldConfigurationPersistence fieldConfigurationPersistence=
		new FieldConfigurationPersistence();
	
	public void testGetEntityMasterList() throws PersistenceException {
		List<EntityMaster> entityMasterList = 
			fieldConfigurationPersistence.getEntityMasterList();
		assertEquals(22, entityMasterList.size());
	}
	
	public void testGetListOfFields() 
	throws NumberFormatException, PersistenceException {
		List<FieldConfigurationEntity> fieldList =
			fieldConfigurationPersistence.getListOfFields(
				EntityType.LOAN.getValue());
		assertEquals(5, fieldList.size());
	}

}
