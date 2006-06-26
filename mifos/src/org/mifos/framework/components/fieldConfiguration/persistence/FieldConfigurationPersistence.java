package org.mifos.framework.components.fieldConfiguration.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.NamedQueryConstants;
import org.mifos.framework.components.fieldConfiguration.business.FieldConfigurationEntity;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigurationConstant;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.Persistence;
import org.mifos.framework.struts.plugin.valueObjects.EntityMaster;

public class FieldConfigurationPersistence extends Persistence{
	
	public List<EntityMaster> getEntityMasterList(){
		return executeNamedQuery(NamedQueryConstants.GET_ENTITY_MASTER, null);
	}
	
	public List<FieldConfigurationEntity> getListOfFields(Short entityId){
		Map<String,Object>queryParameter=new HashMap<String,Object>();
		queryParameter.put(FieldConfigurationConstant.ENTITY_ID,entityId);
		return executeNamedQuery(NamedQueryConstants.GET_FIELD_LIST, queryParameter);
	}

}
