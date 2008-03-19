package org.mifos.framework.components.configuration.persistence;

import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.MifosTestCase;

public class ConfigurationPersistenceTest extends MifosTestCase {
	public void testGetCurrencyForCurrencyId() throws Exception {
		ConfigurationPersistence configurationPersistence = new ConfigurationPersistence();
		MifosCurrency currency = (MifosCurrency) configurationPersistence
				.getPersistentObject(MifosCurrency.class, Short.valueOf("2"));
		assertNotNull(currency);
		assertEquals("RUPEE", currency.getCurrencyName());
	}
}
