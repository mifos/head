package org.mifos.framework.components.configuration.persistence.service;

import java.util.List;

import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.business.SupportedLocalesEntity;
import org.mifos.application.meeting.business.WeekDaysEntity;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.configuration.business.ConfigEntity;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class TestConfigurationPersistenceService extends MifosTestCase {

	private ConfigurationPersistenceService configurationPersistenceService;
	
	protected void setUp() throws Exception {
		super.setUp();
		configurationPersistenceService = new ConfigurationPersistenceService();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		HibernateUtil.closeSession();
	}
	
	public void testGetDefaultCurrency() throws Exception{
		MifosCurrency  mifosCurrency = configurationPersistenceService.getDefaultCurrency();
		assertEquals("RUPEE" , mifosCurrency.getCurrencyName());
	}
	
	public void testGetSystemConfiguration() throws Exception{
		ConfigEntity systemConfig = configurationPersistenceService.getSystemConfiguration();
		assertNotNull(systemConfig);
	}
	
	public void testGetOfficeConfiguration() throws Exception{
		List<ConfigEntity> configList = configurationPersistenceService.getOfficeConfiguration();
		assertNotNull(configList);
		assertEquals(Integer.valueOf("1").intValue(),configList.size());
	}
	
	public void testGetSupportedLocale() throws Exception{
		SupportedLocalesEntity locale = configurationPersistenceService.getSupportedLocale();
		assertNotNull(locale);
		assertEquals(Short.valueOf("1"),locale.getLocaleId());
	}
	
	public void testGetWeekDaysList() throws Exception{
		List<WeekDaysEntity> weekDaysList = configurationPersistenceService.getWeekDaysList();
		assertNotNull(weekDaysList);
		assertEquals(Integer.valueOf("7").intValue(),weekDaysList.size());
	}
}
