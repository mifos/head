package org.mifos.application.configuration;

import org.mifos.application.configuration.persistence.ConfigurationPersistence;
import org.mifos.framework.MifosTestCase;



public class TestConfigurationPersistence extends MifosTestCase {
	
	ConfigurationPersistence configurationPersistence ;
	
	@Override
	protected void setUp() throws Exception {
		configurationPersistence= new ConfigurationPersistence();
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

