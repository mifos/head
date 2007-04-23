package org.mifos.framework.components.fieldConfiguration.persistence.service;

import java.util.List;

import org.mifos.framework.components.fieldConfiguration.business.EntityMaster;
import org.mifos.framework.components.fieldConfiguration.business.FieldConfigurationEntity;
import org.mifos.framework.components.fieldConfiguration.persistence.FieldConfigurationPersistence;
import org.mifos.framework.exceptions.PersistenceException;

public class FieldConfigurationPersistenceService {
	
	private static FieldConfigurationPersistence fieldConfigurationPersistence =
		new FieldConfigurationPersistence();

	public List<EntityMaster> getEntityMasterList() throws PersistenceException{
		return fieldConfigurationPersistence.getEntityMasterList();
	}
	
	public List<FieldConfigurationEntity> getListOfFields(Short entityId) throws PersistenceException{
		return fieldConfigurationPersistence.getListOfFields(entityId);
	}
}
