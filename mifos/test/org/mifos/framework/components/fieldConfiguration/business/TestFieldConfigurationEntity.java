package org.mifos.framework.components.fieldConfiguration.business;

import junit.framework.TestCase;

import org.mifos.application.accounts.financial.util.helpers.FinancialInitializer;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.hibernate.HibernateStartUp;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.authorization.AuthorizationManager;
import org.mifos.framework.security.authorization.HierarchyManager;
import org.mifos.framework.struts.plugin.valueObjects.EntityMaster;
import org.mifos.framework.util.helpers.FilePaths;



public class TestFieldConfigurationEntity extends TestCase{

	public void testGetFieldConfigurationEntity(){
		FieldConfigurationEntity fieldConfigurationEntity=(FieldConfigurationEntity)HibernateUtil.getSessionTL().get(FieldConfigurationEntity.class,Integer.valueOf("1"));
		assertEquals(fieldConfigurationEntity.getFieldName(),"SecondLastName");
	}
}
