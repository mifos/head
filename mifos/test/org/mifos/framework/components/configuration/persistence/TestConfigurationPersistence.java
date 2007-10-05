package org.mifos.framework.components.configuration.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.business.SupportedLocalesEntity;
import org.mifos.application.meeting.business.WeekDaysEntity;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.configuration.business.ConfigEntity;
import org.mifos.framework.exceptions.FrameworkRuntimeException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.ExceptionConstants;

public class TestConfigurationPersistence extends MifosTestCase {

	private ConfigurationPersistence configurationPersistence;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		configurationPersistence = new ConfigurationPersistence();
	}

	@Override
	protected void tearDown() throws Exception {
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testGetDefaultCurrency() throws Exception {
		MifosCurrency defaultCurrency =
			configurationPersistence.getDefaultCurrency();
		assertEquals("RUPEE", defaultCurrency.getCurrencyName());
	}

	public void testNoDefaultCurrency() throws Exception {
		try {
			configurationPersistence.defaultCurrencyFromList(
				Collections.EMPTY_LIST);
			fail();
		}
		catch (FrameworkRuntimeException e) {
			assertEquals("No Default Currency Specified", e.getMessage());
			e.setValues(null);
			assertNull(e.getValues());
			assertEquals(ExceptionConstants.FRAMEWORKRUNTIMEEXCEPTION, e.getKey());
		}
	}

	public void testAmbiguousDefaultCurrency() throws Exception {
		try {
			List currencies = new ArrayList();
			currencies.add(new MifosCurrency((short)8, "Franc", "Fr",
				MifosCurrency.CEILING_MODE, 0.0f, (short)1, (short)0));
			currencies.add(new MifosCurrency((short)9, "Euro", "\u20ac",
					MifosCurrency.CEILING_MODE, 0.0f, (short)1, (short)0));

			configurationPersistence.defaultCurrencyFromList(currencies);
			fail();
		}
		catch (FrameworkRuntimeException e) {
			assertEquals("Both Franc and Euro are marked as default currencies",
				e.getMessage());
		}
	}

	public void testGetOfficeConfiguration() throws Exception {
		List<ConfigEntity> configList =
			configurationPersistence.getOfficeConfiguration();
		assertEquals(1, configList.size());
	}

	//kim commented out on 10/02 will remove soon this test is removed because
	// this method is removed 
	//public void testGetSupportedLocale() throws Exception {
	//	SupportedLocalesEntity locale =
	//		configurationPersistence.getSupportedLocale();
	//	assertEquals(Short.valueOf("1"),locale.getLocaleId());
	//}

	public void testGetWeekDaysList() throws Exception{
		List<WeekDaysEntity> weekDaysList =
			configurationPersistence.getWeekDaysList();
		assertEquals(7, weekDaysList.size());
	}

}
