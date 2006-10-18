package org.mifos.framework.components.fieldConfiguration.persistence;

import java.util.List;

import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.fieldConfiguration.business.EntityMaster;
import org.mifos.framework.components.fieldConfiguration.business.FieldConfigurationEntity;
import org.mifos.framework.exceptions.PersistenceException;

public class TestFieldConfigurationPersistence extends MifosTestCase{
	
	
	private FieldConfigurationPersistence fieldConfigurationPersistence=new FieldConfigurationPersistence();
	
	public void testGetEntityMasterList() throws PersistenceException{
		List<EntityMaster> entityMasterList = fieldConfigurationPersistence.getEntityMasterList();
		assertEquals(entityMasterList.size(),23);
	}
	
	public void testGetListOfFields() throws NumberFormatException, PersistenceException{
		List<FieldConfigurationEntity> fieldList=fieldConfigurationPersistence.getListOfFields(Short.valueOf("22"));
		assertEquals(fieldList.size(),5);
	}

}
