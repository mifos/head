package org.mifos.framework.components.configuration.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.FrameworkRuntimeException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.ExceptionConstants;

public class TestConfigurationPersistence extends MifosIntegrationTest {

	public TestConfigurationPersistence() throws SystemException, ApplicationException {
        super();
    }

    private ConfigurationPersistence configurationPersistence;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		configurationPersistence = new ConfigurationPersistence();
	}

	@Override
	protected void tearDown() throws Exception {
		StaticHibernateUtil.closeSession();
		super.tearDown();
	}

	public void testGetDefaultCurrency() throws Exception {
		MifosCurrency defaultCurrency =
			configurationPersistence.getDefaultCurrency();
		assertEquals("Indian Rupee", defaultCurrency.getCurrencyName());
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
				MifosCurrency.CEILING_MODE, 0.0f, (short)1, (short)0,"FRC"));
			currencies.add(new MifosCurrency((short)9, "Euro", "\u20ac",
					MifosCurrency.CEILING_MODE, 0.0f, (short)1, (short)0,"ERO"));

			configurationPersistence.defaultCurrencyFromList(currencies);
			fail();
		}
		catch (FrameworkRuntimeException e) {
			assertEquals("Both Franc and Euro are marked as default currencies",
				e.getMessage());
		}
	}

}
