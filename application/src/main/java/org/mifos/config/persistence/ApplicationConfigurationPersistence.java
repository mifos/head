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

package org.mifos.config.persistence;

import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.LookUpLabelEntity;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.application.master.business.MifosLookUpEntity;
import org.mifos.application.master.business.SupportedLocalesEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.Persistence;

/**
 * This class is mainly about lookup values.
 */
public class ApplicationConfigurationPersistence extends Persistence {

    /*
     * TODO: the StaticHibernateUtil.closeSession() calls here are bad If these
     * methods are called as part of another operation, the closing of the
     * session can be unexpected and interrupt an ongoing transaction
     */
    public List<MifosLookUpEntity> getLookupEntities() {

        List<MifosLookUpEntity> entities = null;
        try {
            Session session = StaticHibernateUtil.getSessionTL();
            entities = session.getNamedQuery(NamedQueryConstants.GET_ENTITIES).list();

            for (MifosLookUpEntity entity : entities) {
                Set<LookUpLabelEntity> labels = entity.getLookUpLabels();
                entity.getEntityType();
                for (LookUpLabelEntity label : labels) {
                    label.getLabelText();
                    label.getLocaleId();
                }
            }
        } finally {
            StaticHibernateUtil.closeSession();
        }

        return entities;
    }

    public List<LookUpValueEntity> getLookupValues() {
        List<LookUpValueEntity> values = null;
        try {
            Session session = StaticHibernateUtil.getSessionTL();
            values = session.getNamedQuery(NamedQueryConstants.GET_LOOKUPVALUES).list();
            if (values != null) {
                for (LookUpValueEntity value : values) {
                    Set<LookUpValueLocaleEntity> localeValues = value.getLookUpValueLocales();
                    value.getLookUpName();
                    if (localeValues != null) {
                        for (LookUpValueLocaleEntity locale : localeValues) {

                            locale.getLookUpValue();
                            locale.getLocaleId();
                        }
                    }

                }
            }

        } finally {
            StaticHibernateUtil.closeSession();
        }
        return values;
    }

    // this method is used by Localization class to load all the supported
    // locales to its cache
    public List<SupportedLocalesEntity> getSupportedLocale() {
        List<SupportedLocalesEntity> locales = null;
        try {
            Session session = StaticHibernateUtil.getSessionTL();

            locales = session.getNamedQuery(NamedQueryConstants.SUPPORTED_LOCALE_LIST).list();

            for (SupportedLocalesEntity locale : locales) {
                locale.getLanguage().getLanguageShortName();
                locale.getCountry().getCountryShortName();
                locale.getLocaleId();

            }
        } finally {
            StaticHibernateUtil.closeSession();
        }

        return locales;
    }

    public void addCustomField(CustomFieldDefinitionEntity customField) throws PersistenceException {
        MasterPersistence masterPersistence = new MasterPersistence();
        masterPersistence.addLookUpEntity(customField.getLookUpEntity());
        createOrUpdate(customField);
    }

    public void updateCustomField(CustomFieldDefinitionEntity customField) throws PersistenceException {
        createOrUpdate(customField);
        createOrUpdate(customField.getLookUpEntity());
    }

}
