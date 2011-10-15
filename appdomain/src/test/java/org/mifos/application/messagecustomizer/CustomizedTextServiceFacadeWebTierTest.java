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

package org.mifos.application.messagecustomizer;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.admin.servicefacade.CustomizedTextDto;
import org.mifos.framework.components.mifosmenu.MenuRepository;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.MessageSource;

@RunWith(MockitoJUnitRunner.class)
public class CustomizedTextServiceFacadeWebTierTest {

    private CustomizedTextServiceFacadeWebTier customizedTextServiceFacadeWebTier;

    @Mock
    private MessageSource messageSource;
    @Mock
    private CustomizedTextDao customizedTextDao;
    @Mock
    private HibernateTransactionHelper hibernateTransactionHelper;
    @Mock
    private MenuRepository menuRepository;

    @Before
    public void setup() {
        customizedTextServiceFacadeWebTier = new CustomizedTextServiceFacadeWebTier(customizedTextDao, messageSource);
        customizedTextServiceFacadeWebTier.setMenuRepository(menuRepository);
        customizedTextServiceFacadeWebTier.setTransactionHelper(hibernateTransactionHelper);
    }

    @Test
    public void shouldAddOrUpdateCustomizedText() {
        String originalText = "original";
        String customText = "custom";
        customizedTextServiceFacadeWebTier.addOrUpdateCustomizedText(originalText, customText);

        verify(customizedTextDao).addOrUpdateCustomizedText(originalText, customText);
    }

    @Test
    public void shouldRemoveCustomizedText() {
        String originalText = "original";
        customizedTextServiceFacadeWebTier.removeCustomizedText(originalText);

        verify(customizedTextDao).removeCustomizedText(originalText);
    }

    @Test
    public void shouldGetCustomizedText() {
        String originalText = "original";
        String customText = "custom";

        when(customizedTextDao.findCustomizedTextByOriginalText(originalText)).thenReturn(
                new CustomizedText(originalText, customText));
        CustomizedTextDto customizedTextDto = customizedTextServiceFacadeWebTier.getCustomizedTextDto(originalText);

        assertThat(customizedTextDto.getOriginalText(), is(originalText));
        assertThat(customizedTextDto.getCustomText(), is(customText));
    }

    @Test
    public void shouldReplaceText() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("original", "custom");
        map.put("Clients", "People");
        map.put("Client", "Person");
        map.put("Admin", "Administrator");

        when(customizedTextDao.getCustomizedText()).thenReturn(map);

        String newString = customizedTextServiceFacadeWebTier.replaceSubstitutions("" + "This is the original message");
        assertThat(newString, is("This is the custom message"));

        // this illustrates a limitiation of the current approach, "Admin" is replaced in the first part of
        // "Administrators". But we can't do something like only replace whole words since this breaks
        // i18n for locales like Chinese
        newString = customizedTextServiceFacadeWebTier
                .replaceSubstitutions("The Admin function is performed by admins, Admins and Administrators");
        assertThat(newString,
                is("The Administrator function is performed by admins, Administrators and Administratoristrators"));

        newString = customizedTextServiceFacadeWebTier.replaceSubstitutions("" + "Many Clients have a Client");
        assertThat(newString, is("Many People have a Person"));
    }

    @Test
    public void shouldConvertClientAndRemoveLoan() {
        Map<String, String> messageMap = new LinkedHashMap<String, String>();
        messageMap.put("Client.Label", "Borrower");
        messageMap.put("Loan.Label", "Loan");

        Map<String, String> messageMap2 = new LinkedHashMap<String, String>();
        messageMap2.put("Client.Label", "Borrower");
        messageMap2.put("Loan", "Loan");

        when(messageSource.getMessage(eq("Client.Label"), any(Object[].class), any(Locale.class))).thenReturn("Client");
        when(messageSource.getMessage(eq("Loan.Label"), any(Object[].class), any(Locale.class))).thenReturn("Loan");

        customizedTextServiceFacadeWebTier = spy(customizedTextServiceFacadeWebTier);
        when(customizedTextServiceFacadeWebTier.retrieveCustomizedText()).thenReturn(messageMap)
                .thenReturn(messageMap2);
        doNothing().when(customizedTextServiceFacadeWebTier).addOrUpdateCustomizedText(any(String.class),
                any(String.class));
        doNothing().when(customizedTextServiceFacadeWebTier).removeCustomizedText(any(String.class));

        customizedTextServiceFacadeWebTier.convertMigratedLabelKeysToLocalizedText(Locale.ENGLISH);

        verify(customizedTextServiceFacadeWebTier).addOrUpdateCustomizedText("Client", "Borrower");
        verify(customizedTextServiceFacadeWebTier).removeCustomizedText("Client.Label");

        verify(customizedTextServiceFacadeWebTier).addOrUpdateCustomizedText("Loan", "Loan");
        verify(customizedTextServiceFacadeWebTier).removeCustomizedText("Loan.Label");
        verify(customizedTextServiceFacadeWebTier).removeCustomizedText("Loan");

    }

}
