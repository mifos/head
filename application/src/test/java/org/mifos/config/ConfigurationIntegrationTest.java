/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.config;

import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mifos.config.business.MifosConfiguration;
import org.mifos.config.util.helpers.ConfigurationConstants;
import org.mifos.config.util.helpers.LabelKey;
import org.mifos.framework.MifosIntegrationTestCase;

public class ConfigurationIntegrationTest extends MifosIntegrationTestCase {

    MifosConfiguration configuration;

    @Before
    public void setUp() throws Exception {
        configuration = MifosConfiguration.getInstance();
    }

    @Test
    public void testInitializeLabelCache() {

        Map<LabelKey, String> labelCache = configuration.getLabelCache();
       Assert.assertEquals(true, labelCache.size() > 10);

    }

    /**
     *
     */
    @Ignore
    @Test
    public void testGetLabelValueEnglish() {
       Assert.assertEquals("Bulk entry", MifosConfiguration.getInstance().getLabelValue(ConfigurationConstants.BULKENTRY,
                (short) 1));
    }

    /*
     * Will be uncommented when spanish values will be entered in master data.
     * @Test
    public void testGetLabelValueSpanish(){Assert.assertEquals("Entrada agranel
     * ",MifosConfiguration.getInstance().getLabelValue(ConfigurationConstants.BULKENTRY,(short)2))
     * ; }
     */

    @Test
    public void testLabelKey() {
        LabelKey labelKey = new LabelKey("key", (short) 1);
       Assert.assertEquals("[localeId=1][key=key]", labelKey.toString());
       Assert.assertEquals(false, labelKey.equals(null));
        LabelKey labelKeyToCompare = new LabelKey("key", (short) 2);
       Assert.assertEquals(false, labelKey.equals(labelKeyToCompare));
    }

}
