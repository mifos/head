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
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.builders.MifosUserBuilder;
import org.mifos.config.Localization;
import org.mifos.config.util.helpers.ConfigurationConstants;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.security.MifosUser;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

public class MessageLookupIntegrationTest extends MifosIntegrationTestCase {

    private MessageLookup messageLookup;


    @Before
    public void setUp() throws Exception {
        messageLookup = ApplicationContextProvider.getBean(MessageLookup.class);
        SecurityContext securityContext = new SecurityContextImpl();
        MifosUser principal = new MifosUserBuilder().nonLoanOfficer().withAdminRole().build();
        Authentication authentication = new TestingAuthenticationToken(principal, principal);
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void testWeekDayLookup() {
        // default locale
       Assert.assertEquals("Monday", messageLookup.lookup(WeekDay.MONDAY));
       Assert.assertEquals("Tuesday", messageLookup.lookup(WeekDay.TUESDAY));
       Assert.assertEquals("Wednesday", messageLookup.lookup(WeekDay.WEDNESDAY));
       Assert.assertEquals("Thursday", messageLookup.lookup(WeekDay.THURSDAY));
       Assert.assertEquals("Friday", messageLookup.lookup(WeekDay.FRIDAY));
       Assert.assertEquals("Saturday", messageLookup.lookup(WeekDay.SATURDAY));
       Assert.assertEquals("Sunday", messageLookup.lookup(WeekDay.SUNDAY));

       // Spanish locale
       MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
       Short savedLocaleId = user.getPreferredLocaleId();
       user.setPreferredLocaleId(Localization.getInstance().getLocaleId(Localization.SPANISH));
       Assert.assertEquals("Lunes", messageLookup.lookup(WeekDay.MONDAY));
       user.setPreferredLocaleId(savedLocaleId);
        // French locale
       user.setPreferredLocaleId(Localization.getInstance().getLocaleId(Locale.FRANCE));
       Assert.assertEquals("lundi", messageLookup.lookup(WeekDay.MONDAY));
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
