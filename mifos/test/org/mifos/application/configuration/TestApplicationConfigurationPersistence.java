package org.mifos.application.configuration;

import org.mifos.application.configuration.persistence.ApplicationConfigurationPersistence;
import org.mifos.framework.MifosTestCase;



public class TestApplicationConfigurationPersistence extends MifosTestCase {
	
	ApplicationConfigurationPersistence configurationPersistence ;
	
	@Override
	protected void setUp() throws Exception {
		configurationPersistence= new ApplicationConfigurationPersistence();
	}

	public void testGetLookupEntities(){
		assertNotNull(configurationPersistence.getLookupEntities());
	}
	public void testGetLookupValues(){
		assertNotNull(configurationPersistence.getLookupValues());
	}
	
	public void testGetSupportedLocale(){
		assertNotNull(configurationPersistence.getSupportedLocale());
	}
}

