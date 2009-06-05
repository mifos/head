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

package org.mifos.application.master;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import junit.framework.JUnit4TestAdapter;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.config.ConfigLocale;
import org.mifos.config.Localization;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.TestCaseInitializer;

public class MessageLookupTest {
    private static MessageLookup messageLookup;

    @BeforeClass
    public static void init() throws Exception {
        try {
            Class.forName(TestCaseInitializer.class.getName());
        } catch (ClassNotFoundException e) {
            throw new Error("Failed to start up", e);
        }
        messageLookup = MessageLookup.getInstance();
    }

    @Test
    public void testWeekDayLookup() {
        // default locale
        assertEquals("Monday", messageLookup.lookup(WeekDay.MONDAY, Locale.US));
        assertEquals("Tuesday", messageLookup.lookup(WeekDay.TUESDAY, Locale.US));
        assertEquals("Wednesday", messageLookup.lookup(WeekDay.WEDNESDAY, Locale.US));
        assertEquals("Thursday", messageLookup.lookup(WeekDay.THURSDAY, Locale.US));
        assertEquals("Friday", messageLookup.lookup(WeekDay.FRIDAY, Locale.US));
        assertEquals("Saturday", messageLookup.lookup(WeekDay.SATURDAY, Locale.US));
        assertEquals("Sunday", messageLookup.lookup(WeekDay.SUNDAY, Locale.US));
        // Spanish locale
        assertEquals("lunes", messageLookup.lookup(WeekDay.MONDAY, new Locale("es")));
        // French locale
        assertEquals("lundi", messageLookup.lookup(WeekDay.MONDAY, new Locale("fr")));
    }

    private void ChangeLocale(ConfigLocale newLocale) {
        Localization localization = Localization.getInstance();
        localization.setConfigLocale(newLocale);
        localization.refresh();
        MifosConfiguration.getInstance().init();

    }

    @Test
    public void testLabelLookup() throws PersistenceException {
        Localization localization = Localization.getInstance();
        ConfigLocale originalConfig = localization.getConfigLocale();
        UserContext userContext = TestUtils.makeUserWithLocales();

        try {
            // make sure that the GROUP label has not been set
            // currently there is some test that is doing this and not cleaning
            // up,
            // so this test will fail unless the GROUP label is cleared
            messageLookup.setCustomLabel(ConfigurationConstants.GROUP, "", userContext);

            // Get the default label from the English resource bundle
            assertEquals("Group", messageLookup.lookupLabel(ConfigurationConstants.GROUP, Locale.US));

            // Get the label from the Spanish resource bundle
            ChangeLocale(new ConfigLocale("es", "ES"));
            assertEquals("Grupo", messageLookup.lookupLabel(ConfigurationConstants.GROUP, new Locale("es")));

            // Get the label from the French resource bundle
            ChangeLocale(new ConfigLocale("fr", "FR"));
            assertEquals("Groupe", messageLookup.lookupLabel(ConfigurationConstants.GROUP, new Locale("fr")));

            // Override the resource bundle text with a custom label
            String TEST_GROUP_NAME = "TestGroup";
            messageLookup.setCustomLabel(ConfigurationConstants.GROUP, TEST_GROUP_NAME, userContext);

            // The custom label should come back for each locale
            assertEquals(TEST_GROUP_NAME, messageLookup.lookupLabel(ConfigurationConstants.GROUP, new Locale("fr")));
            ChangeLocale(new ConfigLocale("es", "ES"));
            assertEquals(TEST_GROUP_NAME, messageLookup.lookupLabel(ConfigurationConstants.GROUP, new Locale("es")));

            // Reset the custom label and then we should get the locale
            // specific label again
            messageLookup.setCustomLabel(ConfigurationConstants.GROUP, "", userContext);
            assertEquals("Grupo", messageLookup.lookupLabel(ConfigurationConstants.GROUP, new Locale("es")));

            assertEquals("Replacement Status", messageLookup.lookup("ReplacementStatus", new Locale("us")));

            assertEquals("Number of Clients per Center", messageLookup.lookup("NoOfClientsPerCenter", new Locale("us")));
            assertEquals("Number of Clients per Center", messageLookup.lookup("NoOfClientsPerCenter.Label", new Locale(
                    "us")));
            assertEquals("Number of Clients per Group", messageLookup.lookup("NoOfClientsPerGroup", new Locale("us")));
            assertEquals("Number of Clients per Group", messageLookup.lookup("NoOfClientsPerGroup.Label", new Locale(
                    "us")));
            assertEquals("Distance from HO to BO for office", messageLookup.lookup("DistanceFromHoToBO", new Locale(
                    "us")));
            assertEquals("Distance from HO to BO for office", messageLookup.lookup("DistanceFromHoToBO.Label",
                    new Locale("us")));

        } finally {
            messageLookup.setCustomLabel(ConfigurationConstants.GROUP, "", userContext);
            localization.setCountryCodeLanguageCodeToConfigFile(originalConfig);
        }
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(MessageLookupTest.class);
    }

}
