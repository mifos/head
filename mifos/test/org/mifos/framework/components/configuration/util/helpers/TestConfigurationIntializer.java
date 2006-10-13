package org.mifos.framework.components.configuration.util.helpers;

import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.configuration.cache.Cache;
import org.mifos.framework.components.configuration.cache.CacheRepository;
import org.mifos.framework.components.configuration.cache.Key;
import org.mifos.framework.components.configuration.cache.OfficeCache;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class TestConfigurationIntializer extends MifosTestCase{
	private ConfigurationInitializer configInitializer;
	
	protected void setUp() throws Exception {
		super.setUp();
		configInitializer= new ConfigurationInitializer();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		HibernateUtil.closeSession();
	}
	
	public void testCreateSystemCache() throws Exception{
		Cache cache = configInitializer.createSystemCache();
		assertNotNull(cache);
		assertNotNull(cache.getElement(ConfigConstants.SESSION_TIMEOUT));
		assertNotNull(cache.getElement(ConfigConstants.CURRENCY));
		assertNotNull(cache.getElement(ConfigConstants.TIMEZONE));
		assertNotNull(cache.getElement(ConfigConstants.MFI_LOCALE));
	}
	
	public void testCreateOfficeCache() throws Exception{
		configInitializer.initialize();
		OfficeCache officeCache = configInitializer.createOfficeCache();
		assertNotNull(officeCache);
		OfficeBO headOffice = new OfficePersistence().getHeadOffice();
		Key key = new Key(headOffice.getOfficeId(),OfficeConfigConstants.SCHEDULE_TYPE_FOR_MEETING_ON_HOLIDAY);
		assertNotNull(officeCache.getElement(key));
		
		key.setKey(OfficeConfigConstants.DAYS_FOR_CAL_DEFINITION);
		assertNotNull(officeCache.getElement(key));
		
		key.setKey(OfficeConfigConstants.NAME_SEQUENCE);
		assertNotNull(officeCache.getElement(key));
		
		key.setKey(OfficeConfigConstants.CENTER_HIERARCHY_EXIST);
		assertNotNull(officeCache.getElement(key));
		
		key.setKey(OfficeConfigConstants.GROUP_CAN_APPLY_LOANS);
		assertNotNull(officeCache.getElement(key));
		
		key.setKey(OfficeConfigConstants.BACK_DATED_TRXN_ALLOWED);
		assertNotNull(officeCache.getElement(key));
		
		key.setKey(OfficeConfigConstants.CLIENT_CAN_EXIST_OUTSIDE_GROUP);
		assertNotNull(officeCache.getElement(key));
		
		key.setKey(OfficeConfigConstants.NO_OF_INTEREST_DAYS);
		assertNotNull(officeCache.getElement(key));
	}
	
	public void testInitialize() throws Exception{
		configInitializer.initialize();
		
		//check values of systemCache
		CacheRepository cacheRepo = CacheRepository.getInstance();
		assertNotNull(cacheRepo.getValueFromSystemCache(ConfigConstants.SESSION_TIMEOUT));
		assertNotNull(cacheRepo.getValueFromSystemCache(ConfigConstants.CURRENCY));
		assertNotNull(cacheRepo.getValueFromSystemCache(ConfigConstants.MFI_LOCALE));
		assertNotNull(cacheRepo.getValueFromSystemCache(ConfigConstants.TIMEZONE));
		
		//check values of officeCache
		OfficeBO headOffice = new OfficePersistence().getHeadOffice();
		Key key = new Key(headOffice.getOfficeId(),OfficeConfigConstants.SCHEDULE_TYPE_FOR_MEETING_ON_HOLIDAY);
		assertNotNull(cacheRepo.getValueFromOfficeCache(key));
		
		key.setKey(OfficeConfigConstants.DAYS_FOR_CAL_DEFINITION);
		assertNotNull(cacheRepo.getValueFromOfficeCache(key));
		
		key.setKey(OfficeConfigConstants.NAME_SEQUENCE);
		assertNotNull(cacheRepo.getValueFromOfficeCache(key));
		
		key.setKey(OfficeConfigConstants.CENTER_HIERARCHY_EXIST);
		assertNotNull(cacheRepo.getValueFromOfficeCache(key));
		
		key.setKey(OfficeConfigConstants.GROUP_CAN_APPLY_LOANS);
		assertNotNull(cacheRepo.getValueFromOfficeCache(key));
		
		key.setKey(OfficeConfigConstants.BACK_DATED_TRXN_ALLOWED);
		assertNotNull(cacheRepo.getValueFromOfficeCache(key));
		
		key.setKey(OfficeConfigConstants.CLIENT_CAN_EXIST_OUTSIDE_GROUP);
		assertNotNull(cacheRepo.getValueFromOfficeCache(key));
		
		key.setKey(OfficeConfigConstants.NO_OF_INTEREST_DAYS);
		assertNotNull(cacheRepo.getValueFromOfficeCache(key));
	}
}
