package org.mifos.framework.components.configuration.util.helpers;

import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.configuration.business.SystemConfiguration;
import org.mifos.framework.components.configuration.cache.CacheRepository;
import org.mifos.framework.components.configuration.cache.Key;
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
		//assertNotNull(configuration.getMFILocale());
		assertNotNull(configuration.getMFILocaleId());
	}

	public void testCreateOfficeCache() throws Exception{
		configInitializer.initialize();
		OfficeCache officeCache = configInitializer.createOfficeCache();
		assertNotNull(officeCache);
		OfficeBO headOffice = new OfficePersistence().getHeadOffice();
		
		Key key = new Key(headOffice.getOfficeId(),
				OfficeConfigConstants.CENTER_HIERARCHY_EXIST);
		key.setKey(OfficeConfigConstants.CENTER_HIERARCHY_EXIST);
		assertNotNull(officeCache.getElement(key));

		key.setKey(OfficeConfigConstants.GROUP_CAN_APPLY_LOANS);
		assertNotNull(officeCache.getElement(key));

		key.setKey(OfficeConfigConstants.BACK_DATED_TRXN_ALLOWED);
		assertNotNull(officeCache.getElement(key));

		key.setKey(OfficeConfigConstants.CLIENT_CAN_EXIST_OUTSIDE_GROUP);
		assertNotNull(officeCache.getElement(key));

	}

	public void testInitialize() throws Exception{
		configInitializer.initialize();

		//check values of systemCache
		CacheRepository cacheRepo = CacheRepository.getInstance();
		assertNotNull(cacheRepo.getSystemConfiguration().getSessionTimeOut());
		assertNotNull(cacheRepo.getSystemConfiguration().getCurrency());
		assertNotNull(cacheRepo.getSystemConfiguration().getMFILocale());
		assertNotNull(cacheRepo.getSystemConfiguration().getMFILocaleId());
		assertNotNull(cacheRepo.getSystemConfiguration().getMifosTimeZone());

		//check values of officeCache
		OfficeBO headOffice = new OfficePersistence().getHeadOffice();
		
		Key key = new Key(headOffice.getOfficeId(),
				OfficeConfigConstants.CENTER_HIERARCHY_EXIST);
		key.setKey(OfficeConfigConstants.CENTER_HIERARCHY_EXIST);
		assertNotNull(cacheRepo.getValueFromOfficeCache(key));

		key.setKey(OfficeConfigConstants.GROUP_CAN_APPLY_LOANS);
		assertNotNull(cacheRepo.getValueFromOfficeCache(key));

		key.setKey(OfficeConfigConstants.BACK_DATED_TRXN_ALLOWED);
		assertNotNull(cacheRepo.getValueFromOfficeCache(key));

		key.setKey(OfficeConfigConstants.CLIENT_CAN_EXIST_OUTSIDE_GROUP);
		assertNotNull(cacheRepo.getValueFromOfficeCache(key));

		
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
