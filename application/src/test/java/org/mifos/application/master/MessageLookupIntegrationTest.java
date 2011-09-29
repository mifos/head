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

package org.mifos.application.master;

import java.util.Locale;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.config.LocaleSetting;
import org.mifos.config.Localization;
import org.mifos.config.business.MifosConfiguration;
import org.mifos.config.util.helpers.ConfigurationConstants;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.security.util.UserContext;

public class MessageLookupIntegrationTest extends MifosIntegrationTestCase {

    private static MessageLookup messageLookup;

    @Before
    public void setUp() throws Exception {
        messageLookup = MessageLookup.getInstance();
    }

    @Test
    public void testWeekDayLookup() {
        // default locale
       Assert.assertEquals("Monday", messageLookup.lookup(WeekDay.MONDAY, Locale.US));
       Assert.assertEquals("Tuesday", messageLookup.lookup(WeekDay.TUESDAY, Locale.US));
       Assert.assertEquals("Wednesday", messageLookup.lookup(WeekDay.WEDNESDAY, Locale.US));
       Assert.assertEquals("Thursday", messageLookup.lookup(WeekDay.THURSDAY, Locale.US));
       Assert.assertEquals("Friday", messageLookup.lookup(WeekDay.FRIDAY, Locale.US));
       Assert.assertEquals("Saturday", messageLookup.lookup(WeekDay.SATURDAY, Locale.US));
       Assert.assertEquals("Sunday", messageLookup.lookup(WeekDay.SUNDAY, Locale.US));
        // Spanish locale
       Assert.assertEquals("Lunes", messageLookup.lookup(WeekDay.MONDAY, new Locale("es")));
        // French locale
       Assert.assertEquals("lundi", messageLookup.lookup(WeekDay.MONDAY, new Locale("fr")));
    }

    private void ChangeLocale(LocaleSetting newLocale) {
        Localization localization = Localization.getInstance();
        localization.setConfigLocale(newLocale);
        MifosConfiguration.getInstance().init();

    }

    @Test
    public void shouldHaveDefaultLabelOfAddress1() {

     // Get the default label for ADDRESS1 from main locale
     Assert.assertEquals("Address 1", messageLookup.lookupLabel(ConfigurationConstants.ADDRESS1));
    }

    @Test
    public void shouldHaveDefaultLabelOfAddress2() {

     // Get the default label for ADDRESS1 from main locale
     Assert.assertEquals("Address 2", messageLookup.lookupLabel(ConfigurationConstants.ADDRESS2));
    }
}
