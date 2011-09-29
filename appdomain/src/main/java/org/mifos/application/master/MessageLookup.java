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
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.application.admin.servicefacade.CustomizedTextServiceFacade;
import org.mifos.application.master.business.LookUpEntity;
import org.mifos.application.master.business.LookUpLabelEntity;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.persistence.LegacyMasterDao;
import org.mifos.config.Localization;
import org.mifos.config.LocalizedTextLookup;
import org.mifos.config.business.MifosConfiguration;
import org.mifos.config.exceptions.ConfigurationException;
import org.mifos.config.persistence.ApplicationConfigurationDao;
import org.mifos.customers.office.business.OfficeLevelEntity;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.security.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

/**
 * This class looks up messages from tables like {@link LookUpValueEntity},
 * {@link LookUpValueLocaleEntity} and the like.
 * <p>
 * The idea is that we'll be able to come up with a simpler mechanism than the
 * rather convoluted one in {@link LegacyMasterDao}, {@link MasterDataEntity},
 * etc. Or at least we can centralize where we call the convoluted mechanism.
 * <p>
 * Also see {@link ApplicationConfigurationDao}.
 * <p>
 * The word "label" might be better than "message"; at least that's what we call
 * them in places like LabelConfigurationAction.
 * <p>
 * An initial pass has been made at moving to resource bundle based
 * localization. A Spring MessageSource is injected into the single instance of
 * MessageLookup. The MessageSource is used to manage the loading and lookup
 * from external resource bundle files. See {@link FilePaths#SPRING_CONFIG_CORE}
 * for the Spring configuration.
 * <p>
 * Enumerated types which implement the {@link LocalizedTextLookup} interface
 * can be passed to {@link MessageLookup} to look up a localized text string for
 * each instance of the enumerated type.
 * <p>
 * Text strings for enumerated types can currently be found in
 * org/mifos/config/localizedResources/MessageLookupMessages.properties (and
 * associated versions for different locales).
 */
public class MessageLookup implements MessageSourceAware {
    private static MessageLookup messageLookupInstance = new MessageLookup();

    @Autowired(required=false)
    private ApplicationConfigurationDao applicationConfigurationDao;

    @Autowired(required=false)
    private GenericDao genericDao;

    @Autowired(required=false)
    LegacyMasterDao legacyMasterDao;

    @Autowired(required=false)
	CustomizedTextServiceFacade customizedTextServiceFacade;

    private MessageSource messageSource;

    public static final MessageLookup getInstance() {
        return messageLookupInstance;
    }

    /**
     * Use {@link #getInstance()} instead.
     */
    private MessageLookup() {
    }

	public String replaceSubstitutions(String message) {
		return customizedTextServiceFacade.replaceSubstitutions(message);
	}

    public String lookup(LocalizedTextLookup namedObject, Locale locale) {
        return customizedTextServiceFacade.replaceSubstitutions(lookup(namedObject.getPropertiesKey(), locale));
    }

    /*
     * TODO: this will need to change in order to support per user Locale
     * selection
     */
    public String lookup(LocalizedTextLookup namedObject) {
        return customizedTextServiceFacade.replaceSubstitutions(lookup(namedObject.getPropertiesKey()));
    }

    public String lookup(String lookupKey) {
        Locale locale = Localization.getInstance().getConfiguredLocale();
        return lookup(lookupKey, locale);
    }

    public String lookup(LocalizedTextLookup namedObject, Object[] params) {
        Locale locale = Localization.getInstance().getConfiguredLocale();
        return customizedTextServiceFacade.replaceSubstitutions(
        		messageSource.getMessage(namedObject.getPropertiesKey(), params, namedObject.getPropertiesKey(), locale));
    }

    public String lookup(LocalizedTextLookup namedObject, UserContext user) {
        return lookup(namedObject, user.getPreferredLocale());
    }

    public String lookup(String lookupKey, Locale locale) {
        try {
            String textMessage = MifosConfiguration.getInstance().getLabel(lookupKey);
            // if we don't find a message above, then it means that it has not
            // been customized and
            // we should return the default message from the properties file
            return StringUtils.isEmpty(textMessage) ?
            		customizedTextServiceFacade.replaceSubstitutions(messageSource.getMessage(lookupKey, null, lookupKey, locale))
                    : customizedTextServiceFacade.replaceSubstitutions(textMessage);
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public String lookup(String lookupKey, UserContext userContext) {
        Locale locale = userContext.getPreferredLocale();
        return lookup(lookupKey, locale);
    }

    public String lookupLabel(String labelKey) {
        return lookupLabel(labelKey, Localization.getInstance().getConfiguredLocale());
    }

    public String lookupLabel(String labelKey, UserContext userContext) {
        return lookupLabel(labelKey, userContext.getPreferredLocale());
    }

    /*
     * Return a label for given label key. Label keys are listed in {@link
     * ConfigurationConstants}.
     */
    protected String lookupLabel(String labelKey, Locale locale) {
        try {
            String labelText = MifosConfiguration.getInstance().getLabel(labelKey);
            // if we don't find a label here, then it means that it has not been
            // customized and
            // we should return the default label from the properties file
            return StringUtils.isEmpty(labelText) ? lookup(labelKey + ".Label", locale) : labelText;
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * Set a custom label value that will override resource bundle values.
     *
     * TODO: we need to add a method for getting and/or setting a label value
     * directly rather than having to iterate. Also, we don't necessarily want
     * to reinitialize the MifosConfiguration after each update. Ultimately, it
     * would be cleaner to just use a key-value lookup to implement these
     * overrides.
     */
    public void setCustomLabel(String labelKey, String value, UserContext userContext) throws PersistenceException {

        // only update the value if there is a change
        if (lookupLabel(labelKey, userContext).compareTo(value) != 0) {
            // getLookupEntities currently closes the Hibernate session (which
            // is bad)
            for (LookUpEntity entity : applicationConfigurationDao.findLookupEntities()) {
                if (entity.getEntityType().equals(labelKey)) {
                    Set<LookUpLabelEntity> labels = entity.getLookUpLabels();
                    for (LookUpLabelEntity label : labels) {
                        label.setLabelName(value);
                        genericDao.createOrUpdate(label);
                        // because the session is closed at the beginning of
                        // this method, we need to make sure we commit
                        // this is bad too, and should go away with some
                        // refactoring
                        StaticHibernateUtil.commitTransaction();
                        updateLookupValueInCache(labelKey, value);
                    }
                }
            }
        }
        // if (updateCache) {
        // MifosConfiguration.getInstance().init();
        // }

    }

    public void updateLookupValueInCache(LocalizedTextLookup keyContainer, String newLookupValue) {
        MifosConfiguration.getInstance().updateKey(keyContainer, newLookupValue);
    }

    public void updateLookupValueInCache(String lookupKey, String newLookupValue) {
        MifosConfiguration.getInstance().updateKey(lookupKey, newLookupValue);
    }

    /**
     * @deprecated - don't use from pojo domain model for updating entities lookup values
     *
     * @see OfficeLevelEntity#update(String)
     */
    @Deprecated
    public void updateLookupValue(LookUpValueEntity lookupValueEntity, String newValue) {

        Set<LookUpValueLocaleEntity> lookUpValueLocales = lookupValueEntity.getLookUpValueLocales();
        if ((lookUpValueLocales != null) && StringUtils.isNotBlank(newValue)) {
            for (LookUpValueLocaleEntity entity : lookUpValueLocales) {
                if (entity.getLookUpId().equals(lookupValueEntity.getLookUpId())
                        && (entity.getLookUpValue() == null || !entity.getLookUpValue().equals(newValue))) {
                    entity.setLookUpValue(newValue);
                    try {
                        legacyMasterDao.createOrUpdate(entity);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex.getMessage());
                    }
                    MessageLookup.getInstance().updateLookupValueInCache(lookupValueEntity.getLookUpName(), newValue);
                    break;
                }
            }
        }
    }

    /**
     * This is a dependency injection method used by Spring to inject a
     * MessageSource for resource bundle based message lookup.
     */
    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

}
