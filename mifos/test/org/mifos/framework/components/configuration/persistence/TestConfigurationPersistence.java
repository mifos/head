package org.mifos.framework.components.configuration.persistence;

import java.util.List;

import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.business.SupportedLocalesEntity;
import org.mifos.application.meeting.business.WeekDaysEntity;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.configuration.business.ConfigEntity;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class TestConfigurationPersistence extends MifosTestCase {

	private ConfigurationPersistence configurationPersistence;
	
	protected void setUp() throws Exception {
		super.setUp();
		configurationPersistence = new ConfigurationPersistence();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		HibernateUtil.closeSession();
	}
	
	public void testGetDefaultCurrency() throws Exception{
		MifosCurrency  mifosCurrency = configurationPersistence.getDefaultCurrency();
		assertEquals("RUPEE" , mifosCurrency.getCurrencyName());
	}
	
	public void testGetSystemConfiguration() throws Exception{
		ConfigEntity systemConfig = configurationPersistence.getSystemConfiguration();
		assertNotNull(systemConfig);
	}
	
	public void testGetOfficeConfiguration() throws Exception{
		List<ConfigEntity> configList = configurationPersistence.getOfficeConfiguration();
		assertNotNull(configList);
		assertEquals(Integer.valueOf("1").intValue(),configList.size());
	}
	
	public void testGetSupportedLocale() throws Exception{
		SupportedLocalesEntity locale = configurationPersistence.getSupportedLocale();
		assertNotNull(locale);
		assertEquals(Short.valueOf("1"),locale.getLocaleId());
	}
	
	public void testGetWeekDaysList() throws Exception{
		List<WeekDaysEntity> weekDaysList = configurationPersistence.getWeekDaysList();
		assertNotNull(weekDaysList);
		assertEquals(Integer.valueOf("7").intValue(),weekDaysList.size());
	}
}
