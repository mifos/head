package org.mifos.framework.components.fieldConfiguration.util.helpers;

import java.util.Locale;
import java.util.PropertyResourceBundle;

import org.mifos.application.accounts.financial.util.helpers.FinancialInitializer;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.configuration.exceptions.ConfigurationException;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.hibernate.HibernateStartUp;
import org.mifos.framework.security.authorization.AuthorizationManager;
import org.mifos.framework.security.authorization.HierarchyManager;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.TestObjectFactory;

import org.mifos.framework.MifosTestCase;


public class TestFieldConfigurationHelper extends MifosTestCase {
	
	public void testGetConfiguredFieldName() throws Exception{
		String fieldName=FieldConfigurationHelper.getConfiguredFieldName("Center.Address3",TestObjectFactory.getUserContext().getPereferedLocale());
		assertEquals("Village",fieldName);
		fieldName=FieldConfigurationHelper.getConfiguredFieldName("Center.SomeField",TestObjectFactory.getUserContext().getPereferedLocale());
		assertEquals(null,fieldName);
	}
	
	public void testGetLocalSpecificFieldNames() throws Exception{
		String fieldName=FieldConfigurationHelper.getLocalSpecificFieldNames("Center.Address3",TestObjectFactory.getUserContext().getPereferedLocale());
		assertEquals("Village",fieldName);
		fieldName=FieldConfigurationHelper.getLocalSpecificFieldNames("Center.SomeField",TestObjectFactory.getUserContext().getPereferedLocale());
		assertEquals("Center.SomeField",fieldName);
	}
	

}
