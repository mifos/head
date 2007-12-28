package org.mifos.framework.components.configuration.util.helpers;

import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.configuration.business.SystemConfiguration;
import org.mifos.framework.components.configuration.cache.OfficeCache;
import org.mifos.framework.exceptions.StartUpException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.ExceptionConstants;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestConfigurationIntializer extends MifosTestCase{
	private ConfigurationInitializer configInitializer;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		configInitializer= new ConfigurationInitializer();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		HibernateUtil.closeSession();
	}

	public void testCreateSystemCache() throws Exception{
		SystemConfiguration configuration = 
			configInitializer.createSystemConfiguration();
		assertNotNull(configuration);
		assertNotNull(configuration.getSessionTimeOut());
		assertNotNull(configuration.getCurrency());
		assertNotNull(configuration.getMifosTimeZone());
		assertNotNull(configuration.getMFILocale());
		assertNotNull(configuration.getMFILocaleId());
	}

 	public void testCreateOfficeCache() throws Exception{
 		configInitializer.initialize();
 		OfficeCache officeCache = configInitializer.createOfficeCache();
 		assertNotNull(officeCache);
	}

	public void testStartUpException() throws Exception {
		TestObjectFactory.simulateInvalidConnection();
		try {
			configInitializer.initialize();
			fail();
		} catch (StartUpException sue) {
			assertEquals(ExceptionConstants.STARTUP_EXCEPTION, sue.getKey());
		} finally {
			HibernateUtil.closeSession();
		}
	}
}
