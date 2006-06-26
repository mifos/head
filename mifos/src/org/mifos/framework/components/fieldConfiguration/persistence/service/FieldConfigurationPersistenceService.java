package org.mifos.framework.components.fieldConfiguration.persistence.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.application.NamedQueryConstants;
import org.mifos.framework.components.fieldConfiguration.business.FieldConfigurationEntity;
import org.mifos.framework.components.fieldConfiguration.persistence.FieldConfigurationPersistence;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigurationConstant;
import org.mifos.framework.persistence.service.PersistenceService;
import org.mifos.framework.struts.plugin.valueObjects.EntityMaster;

public class FieldConfigurationPersistenceService extends PersistenceService{
	
	private static FieldConfigurationPersistence fieldConfigurationPersistence = new FieldConfigurationPersistence();

	public List<EntityMaster> getEntityMasterList(){
		return fieldConfigurationPersistence.getEntityMasterList();
	}
	
	public List<FieldConfigurationEntity> getListOfFields(Short entityId){
		return fieldConfigurationPersistence.getListOfFields(entityId);
	}
}
