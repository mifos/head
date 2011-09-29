/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

import java.util.Locale;

import org.junit.Test;
import org.springframework.util.Assert;

public class LocalizationTest {

    @Test
    public void testLocalization() {
        Localization localization = Localization.getInstance();
        localization.setConfigLocale(new LocaleSetting("en", "US"));
        localization.getConfiguredLocale().equals(Locale.US);
        localization.getConfiguredLocaleId().equals(localization.newLocaleId);

        Assert.notEmpty(localization.getLocaleList(), "List of Locale for UI");

        Assert.notEmpty(localization.getLocaleIdSet(), "List of Locale ID for UI");

        Assert.notNull(localization.getDisplayName(localization.newLocaleId));

        Assert.notEmpty(localization.getLocaleList(), "List of Locale for UI");

        int size = localization.getLocaleIdSet().size();
        for (int i = 1; i <= size; i++) {
            Assert.notNull(localization.getDisplayName((short) i));
        }
    }
}
