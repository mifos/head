package org.mifos.framework.components.fieldConfiguration.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.application.NamedQueryConstants;
import org.mifos.framework.components.fieldConfiguration.business.EntityMaster;
import org.mifos.framework.components.fieldConfiguration.business.FieldConfigurationEntity;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigurationConstant;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.Persistence;

public class FieldConfigurationPersistence extends Persistence{
	
	public List<EntityMaster> getEntityMasterList() throws PersistenceException{
		return executeNamedQuery(NamedQueryConstants.GET_ENTITY_MASTER, null);
	}
	
	public List<FieldConfigurationEntity> getListOfFields(Short entityId) throws PersistenceException{
		Map<String,Object>queryParameter=new HashMap<String,Object>();
		queryParameter.put(FieldConfigurationConstant.ENTITY_ID,entityId);
		return executeNamedQuery(NamedQueryConstants.GET_FIELD_LIST, queryParameter);
	}

}
