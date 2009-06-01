/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
 
package org.mifos.application.configuration;

import java.util.Map;

import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.configuration.util.helpers.LabelKey;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;

public class ConfigurationIntegrationTest extends MifosIntegrationTest {

	public ConfigurationIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    MifosConfiguration configuration;

	@Override
	protected void setUp() throws Exception {
	    super.setUp();
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

	

	public void testLabelKey() {
		LabelKey labelKey = new LabelKey("key", (short)1);
		assertEquals("[localeId=1][key=key]", labelKey.toString());
		assertEquals(false, labelKey.equals(null));
		LabelKey labelKeyToCompare = new LabelKey("key", (short)2);
		assertEquals(false, labelKey.equals(labelKeyToCompare));
	}

}
