package org.mifos.application.configuration;

import java.util.Map;

import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.configuration.util.helpers.LabelKey;
import org.mifos.framework.MifosTestCase;

public class TestConfiguration extends MifosTestCase {

	MifosConfiguration configuration;

	@Override
	protected void setUp() throws Exception {
		configuration = MifosConfiguration.getInstance();
	}

	public void testInitializeLabelCache() {

		Map<LabelKey, String> labelCache = configuration.getLabelCache();
		assertEquals(true, labelCache.size() > 10);

	}

	public void testGetLabelValueEnglish() {
		assertEquals("Bulk entry", MifosConfiguration.getInstance()
				.getLabelValue(ConfigurationConstants.BULKENTRY, (short) 1));
	}

	/*
	 * Will be uncommented when spanish values will be entered in master data.
	 * public void testGetLabelValueSpanish(){ assertEquals("Entrada a
	 * granel",MifosConfiguration.getInstance().getLabelValue(ConfigurationConstants.BULKENTRY,(short)2)) ; }
	 */

	public void testInitializeLabelLocaleIdCache() {
		Map<String, Short> loacleIdCache = configuration.getLoacleIdCache();
		assertEquals(true, loacleIdCache.size() > 0);

	}

	public void testLabelKey() {
		LabelKey labelKey = new LabelKey("key", (short)1);
		assertEquals("[localeId=1][key=key]", labelKey.toString());
		assertEquals(false, labelKey.equals(null));
		LabelKey labelKeyToCompare = new LabelKey("key", (short)2);
		assertEquals(false, labelKey.equals(labelKeyToCompare));
	}

}
