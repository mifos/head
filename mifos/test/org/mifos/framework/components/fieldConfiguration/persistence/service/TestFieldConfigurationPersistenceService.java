package org.mifos.framework.components.fieldConfiguration.persistence.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.financial.util.helpers.FinancialInitializer;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.framework.components.fieldConfiguration.business.FieldConfigurationEntity;
import org.mifos.framework.components.fieldConfiguration.persistence.FieldConfigurationPersistence;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigurationConstant;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.hibernate.HibernateStartUp;
import org.mifos.framework.persistence.service.PersistenceService;
import org.mifos.framework.security.authorization.AuthorizationManager;
import org.mifos.framework.security.authorization.HierarchyManager;
import org.mifos.framework.struts.plugin.valueObjects.EntityMaster;
import org.mifos.framework.util.helpers.FilePaths;

public class TestFieldConfigurationPersistenceService extends TestCase{
	
	private static FieldConfigurationPersistenceService fieldConfigurationPersistenceService = new FieldConfigurationPersistenceService();

	public void testGetEntityMasterList(){
		List<EntityMaster> entityMasterList = fieldConfigurationPersistenceService.getEntityMasterList();
		assertEquals(entityMasterList.size(),23);
	}
	
	public void testGetListOfFields(){
		List<FieldConfigurationEntity> fieldList=fieldConfigurationPersistenceService.getListOfFields(Short.valueOf("22"));
		assertEquals(fieldList.size(),5);
	}

}
