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

import java.util.Locale;

import junit.framework.Assert;

import org.mifos.config.business.MifosConfiguration;
import org.mifos.config.util.helpers.ConfigurationConstants;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.config.ConfigLocale;
import org.mifos.config.Localization;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.security.util.UserContext;

public class MessageLookupIntegrationTest extends MifosIntegrationTestCase {
    public MessageLookupIntegrationTest() throws Exception {
        super();
    }

    private static MessageLookup messageLookup;


    @Override
    public void setUp() throws Exception {
        super.setUp();
        messageLookup = MessageLookup.getInstance();
    }

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

    private void ChangeLocale(ConfigLocale newLocale) {
        Localization localization = Localization.getInstance();
        localization.setConfigLocale(newLocale);
        localization.refresh();
        MifosConfiguration.getInstance().init();

    }

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
           Assert.assertEquals("Group", messageLookup.lookupLabel(ConfigurationConstants.GROUP, Locale.US));

            // Get the label from the Spanish resource bundle
            ChangeLocale(new ConfigLocale("es", "ES"));
           Assert.assertEquals("Grupo", messageLookup.lookupLabel(ConfigurationConstants.GROUP, new Locale("es")));

            // Get the label from the French resource bundle
            ChangeLocale(new ConfigLocale("fr", "FR"));
           Assert.assertEquals("Groupe", messageLookup.lookupLabel(ConfigurationConstants.GROUP, new Locale("fr")));

            // Override the resource bundle text with a custom label
            String TEST_GROUP_NAME = "TestGroup";
            messageLookup.setCustomLabel(ConfigurationConstants.GROUP, TEST_GROUP_NAME, userContext);

            // The custom label should come back for each locale
           Assert.assertEquals(TEST_GROUP_NAME, messageLookup.lookupLabel(ConfigurationConstants.GROUP, new Locale("fr")));
            ChangeLocale(new ConfigLocale("es", "ES"));
           Assert.assertEquals(TEST_GROUP_NAME, messageLookup.lookupLabel(ConfigurationConstants.GROUP, new Locale("es")));

            // Reset the custom label and then we should get the locale
            // specific label again
            messageLookup.setCustomLabel(ConfigurationConstants.GROUP, "", userContext);
           Assert.assertEquals("Grupo", messageLookup.lookupLabel(ConfigurationConstants.GROUP, new Locale("es")));

           Assert.assertEquals("Replacement Status", messageLookup.lookup("ReplacementStatus", new Locale("us")));

           Assert.assertEquals("Number of Clients per Center", messageLookup.lookup("NoOfClientsPerCenter", new Locale("us")));
           Assert.assertEquals("Number of Clients per Center", messageLookup.lookup("NoOfClientsPerCenter.Label", new Locale(
                    "us")));
           Assert.assertEquals("Number of Clients per Group", messageLookup.lookup("NoOfClientsPerGroup", new Locale("us")));
           Assert.assertEquals("Number of Clients per Group", messageLookup.lookup("NoOfClientsPerGroup.Label", new Locale(
                    "us")));
           Assert.assertEquals("Distance from HO to BO for office", messageLookup.lookup("DistanceFromHoToBO", new Locale(
                    "us")));
           Assert.assertEquals("Distance from HO to BO for office", messageLookup.lookup("DistanceFromHoToBO.Label",
                    new Locale("us")));

        } finally {
            messageLookup.setCustomLabel(ConfigurationConstants.GROUP, "", userContext);
            ChangeLocale(originalConfig);
        }
    }

}
