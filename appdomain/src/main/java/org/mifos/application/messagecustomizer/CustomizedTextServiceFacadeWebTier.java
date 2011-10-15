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

import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.mifos.application.admin.servicefacade.CustomizedTextDto;
import org.mifos.application.admin.servicefacade.CustomizedTextServiceFacade;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.components.mifosmenu.MenuRepository;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelperForStaticHibernateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

public class CustomizedTextServiceFacadeWebTier implements CustomizedTextServiceFacade {

    @Autowired
    private CustomizedTextDao customizedTextDao;

    @Autowired
    private MessageSource messageSource;

    // for testing purposes, allow these legacy objects to be injected
    private HibernateTransactionHelper transactionHelper;
    private MenuRepository menuRepository;

    @Autowired
    public CustomizedTextServiceFacadeWebTier(CustomizedTextDao customizedTextDao, MessageSource messageSource) {
        super();
        this.customizedTextDao = customizedTextDao;
        this.messageSource = messageSource;
        transactionHelper = new HibernateTransactionHelperForStaticHibernateUtil();

    }

    protected void setTransactionHelper(HibernateTransactionHelper hibernateTransactionHelper) {
        this.transactionHelper = hibernateTransactionHelper;
    }

    protected MenuRepository getMenuRepository() {
        if (menuRepository == null) {
            menuRepository = MenuRepository.getInstance();
        }
        return menuRepository;
    }

    protected void setMenuRepository(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }


    private void updateLegacyCaches() {
        // legacy menu code caches strings, so force the menus to rebuild after a change
        getMenuRepository().removeMenuForAllLocale();
    }

    @Override
    public void addOrUpdateCustomizedText(String originalText, String customText) {
        try {
            this.transactionHelper.startTransaction();

            customizedTextDao.addOrUpdateCustomizedText(originalText, customText);

            this.transactionHelper.commitTransaction();
        } catch (Exception e) {
            this.transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            this.transactionHelper.closeSession();
        }
        updateLegacyCaches();
    }

    @Override
    public void removeCustomizedText(String originalText) {
        try {
            this.transactionHelper.startTransaction();

            customizedTextDao.removeCustomizedText(originalText);

            this.transactionHelper.commitTransaction();
        } catch (Exception e) {
            this.transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            this.transactionHelper.closeSession();
        }
        updateLegacyCaches();
    }

    @Override
    public Map<String, String> retrieveCustomizedText() {
        return customizedTextDao.getCustomizedText();
    }

    /**
     * Take an input message and replace any instances of customized text strings that are found in the message. If
     * there end up being any performance issues around this method (for example, if the logic for this method needs to
     * be more complex --regular expressions, etc.) then this method is a good candidate for using the Spring @Cacheable
     * annotation mechanism
     *
     * @see org.mifos.application.admin.servicefacade.CustomizedTextServiceFacade#replaceSubstitutions(java.lang.String)
     */
    @Override
    public String replaceSubstitutions(String message) {
        if (message == null)
            return message;

        String newMessage = message;

        Map<String, String> messageFilterMap = customizedTextDao.getCustomizedText();

        for (Map.Entry<String, String> entry : messageFilterMap.entrySet()) {
            newMessage = newMessage.replace(entry.getKey(), entry.getValue());
        }
        return newMessage;
    }

    @Override
    public CustomizedTextDto getCustomizedTextDto(String originalText) {
        CustomizedText customizedText = customizedTextDao.findCustomizedTextByOriginalText(originalText);

        return new CustomizedTextDto(customizedText.getOriginalText(), customizedText.getCustomText());
    }

    /**
     * This method is used as the second step in migrating legacy customized labels to the new customized text
     * mechanism. Changeset MIFOS-4633_2 copies existing custom labels to the new customized_text table with the
     * original_text field as the entity name + ".Label". This was needed since the Liquibase conversion doesn't know
     * what the configured locale is for Mifos or how to lookup properties values.
     *
     * An example of possible resulting migrated data would be: original_text: Client.Label custom_text: Borrower
     *
     * So we know that the "Client" entity label was customized. The method below will check for original_text entries
     * that have been migrated (ie. end in ".Label" and lookup the localized text that was the default label for that
     * entity. It replaces the migrated original_text with the localized text.
     *
     * Finally it checks to see if there are cases where original_text maps to custom_text where both are the same (this
     * would happen if in the original
     */
    @Override
    public void convertMigratedLabelKeysToLocalizedText(Locale locale) {
        Map<String, String> messageMap = retrieveCustomizedText();

        for (Entry<String, String> entry : messageMap.entrySet()) {
            if (entry.getKey().endsWith(".Label")) {
                // create a new entry with the localized string for the label
                String localizedMessageKey = messageSource.getMessage(entry.getKey(), null, locale);
                addOrUpdateCustomizedText(localizedMessageKey, entry.getValue());
                removeCustomizedText(entry.getKey());
            }
        }
        messageMap = retrieveCustomizedText();

        // remove any entries where the old and new messages are exactly the same
        for (Entry<String, String> entry : messageMap.entrySet()) {
            if (entry.getKey().equals(entry.getValue())) {
                removeCustomizedText(entry.getKey());
            }
        }
    }
}
