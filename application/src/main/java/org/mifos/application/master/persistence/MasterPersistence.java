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

package org.mifos.application.master.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomValueList;
import org.mifos.application.master.business.CustomValueListElement;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.MifosLookUpEntity;
import org.mifos.application.master.business.ValueListElement;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.config.Localization;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.persistence.Persistence;
import org.mifos.security.activity.DynamicLookUpValueCreationTypes;
import org.mifos.framework.util.helpers.SearchUtils;

/**
 * This class is mostly used to look up instances of (a subclass of)
 * {@link MasterDataEntity} in the database. Most of what is here can better be
 * accomplished by enums and by {@link MessageLookup}.
 * 
 * Test cases: {@link MasterPersistenceIntegrationTest}
 */
public class MasterPersistence extends Persistence {

    /**
     * Only two non-test usages, one that may never be called and one for
     * getting labels.
     */
    public CustomValueList getLookUpEntity(final String entityName, final Short localeId) throws ApplicationException,
            SystemException {
        try {
            Session session = getSession();

            Query queryEntity = session.getNamedQuery("masterdata.entityvalue");
            queryEntity.setString("entityType", entityName);

            CustomValueList entity = (CustomValueList) queryEntity.uniqueResult();

            entity.setCustomValueListElements(lookUpValue(entityName, localeId, session));

            return entity;
        } catch (Exception e) {
            throw new ApplicationException(e);
        }
    }

    private List<CustomValueListElement> lookUpValue(final String entityName, final Short localeId, final Session session)
            throws PersistenceException {
        Query queryEntity = session.getNamedQuery("masterdata.entitylookupvalue");
        queryEntity.setString("entityType", entityName);
        List<CustomValueListElement> entityList = queryEntity.list();

        return entityList;
    }

    public short getLocaleId(final Locale locale) {
        return Localization.getInstance().getLocaleId();
    }

    /**
     * Used once in getMasterData, otherwise, test usage and one other method
     * MifosPropertyMessageResources.getCustomValueListElements (and that method
     * may never be called)
     */
    public CustomValueList getCustomValueList(final String entityName, final String classPath, final String column)
            throws ApplicationException, SystemException {
        Session session = null;
        try {
            session = getSession();

            Query queryEntity = session.getNamedQuery("masterdata.entityvalue");
            queryEntity.setString("entityType", entityName);

            CustomValueList entity = (CustomValueList) queryEntity.uniqueResult();
            
            List<CustomValueListElement> listElements = getCustomValueListElements(entityName, classPath, column,
                    session);
            entity.setCustomValueListElements(listElements);
            return entity;
        } catch (Exception e) {
            throw new ApplicationException(e);
        }
    }

    private List<CustomValueListElement> getCustomValueListElements(final String entityName,
            final String entityClass, final String column, final Session session) {
        Query queryEntity = session
                .createQuery("select new org.mifos.application.master.business.CustomValueListElement(" + "mainTable."
                        + column + " ,lookup.lookUpId,lookupvalue.lookUpValue,lookup.lookUpName) "
                        + "from org.mifos.application.master.business.LookUpValueEntity lookup,"
                        + "org.mifos.application.master.business.LookUpValueLocaleEntity lookupvalue," + entityClass
                        + " mainTable " + "where mainTable.lookUpId = lookup.lookUpId"
                        + " and lookup.lookUpEntity.entityType = ?" + " and lookup.lookUpId = lookupvalue.lookUpId"
                        + " and lookupvalue.localeId = ?");
        queryEntity.setString(0, entityName);

        // Jan 16, 2008 work in progress
        // all override or custom values are now stored in locale 1
        // queryEntity.setShort(1, localeId);
        queryEntity.setShort(1, (short) 1);
        List<CustomValueListElement> entityList = queryEntity.list();

        return entityList;
    }

    public List<MasterDataEntity> retrieveMasterEntities(final Class clazz, final Short localeId) throws PersistenceException {
        try {
            Session session = getSession();
            List<MasterDataEntity> masterEntities = session.createQuery("from " + clazz.getName()).list();
            for (MasterDataEntity masterData : masterEntities) {
                initialize(masterData.getNames());
                masterData.setLocaleId(localeId);
            }
            return masterEntities;
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    public MasterDataEntity retrieveMasterEntity(final Short entityId, final Class clazz, final Short localeId)
            throws PersistenceException {
        try {
            Session session = getSession();
            List<MasterDataEntity> masterEntity = session.createQuery(
                    "from " + clazz.getName() + " masterEntity where masterEntity.id = " + entityId).list();
            if (masterEntity != null && masterEntity.size() > 0) {
                MasterDataEntity masterDataEntity = masterEntity.get(0);
                masterDataEntity.setLocaleId(localeId);
                initialize(masterDataEntity.getNames());
                return masterDataEntity;
            }
            throw new PersistenceException("errors.entityNotFound");
        } catch (Exception he) {
            throw new PersistenceException(he);
        }
    }

    public List<CustomFieldDefinitionEntity> retrieveCustomFieldsDefinition(final EntityType entityType)
            throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(MasterConstants.ENTITY_TYPE, entityType.getValue());
        return executeNamedQuery(NamedQueryConstants.RETRIEVE_CUSTOM_FIELDS, queryParameters);

    }

    public CustomFieldDefinitionEntity retrieveOneCustomFieldDefinition(final Short fieldId) throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("fieldId", fieldId);
        return (CustomFieldDefinitionEntity) execUniqueResultNamedQuery(NamedQueryConstants.RETRIEVE_ONE_CUSTOM_FIELD,
                queryParameters);

    }

    /**
     * This method is used to retrieve both custom and fixed value list
     * elements.
     */
    public List<ValueListElement> retrieveMasterEntities(final String entityName, final Short localeId) throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("entityType", entityName);
        List<ValueListElement> elements = executeNamedQuery(NamedQueryConstants.MASTERDATA_MIFOS_ENTITY_VALUE,
                queryParameters);

        return elements;

    }

    /*
     * @param entityId - the primary key of a LookUpValueEntity
     * 
     * @param localeId - a locale primary key which we now ignore
     */
    public String retrieveMasterEntities(final Integer entityId, final Short localeId) throws PersistenceException {

        LookUpValueEntity lookupValue = (LookUpValueEntity) getPersistentObject(LookUpValueEntity.class, entityId);
        return MessageLookup.getInstance().lookup(lookupValue);
    }

    public List<MasterDataEntity> retrieveMasterDataEntity(final String classPath) throws PersistenceException {
        List<MasterDataEntity> queryResult = null;
        try {
            queryResult = getSession().createQuery("from " + classPath).list();
        } catch (Exception he) {
            throw new PersistenceException(he);
        }
        return queryResult;
    }

    public MasterDataEntity getMasterDataEntity(final Class clazz, final Short id) throws PersistenceException {
        return (MasterDataEntity) getPersistentObject(clazz, id);
    }

    /**
     * Update the String value of a LookUpValueLocaleEntity.
     * 
     * @param lookupValueEntityId
     *            - the database id of the LookUpValueLocaleEntity object
     *            representing a ValueListElement
     */
    public void updateValueListElementForLocale(final Integer lookupValueEntityId, final String newValue)
            throws PersistenceException {
        LookUpValueEntity lookupValueEntity = (LookUpValueEntity) getPersistentObject(LookUpValueEntity.class,
                lookupValueEntityId);
        Set<LookUpValueLocaleEntity> lookUpValueLocales = lookupValueEntity.getLookUpValueLocales();
        if (lookUpValueLocales != null) {
            for (LookUpValueLocaleEntity entity : lookUpValueLocales) {
                if (entity.getLookUpId().equals(lookupValueEntityId)) {
                    entity.setLookUpValue(newValue);
                    createOrUpdate(entity);
                    MessageLookup.getInstance().updateLookupValueInCache(lookupValueEntity.getLookUpName(), newValue);
                    break;
                }
            }
        }

    }

    public LookUpValueEntity addValueListElementForLocale(final DynamicLookUpValueCreationTypes type, final Short lookupEnityId,
            final String newElementText) throws PersistenceException {

        // String lookUpName = SearchUtils.camelCase(newElementText + "." +
        // System.currentTimeMillis());
        String lookUpName = SearchUtils.generateLookupName(type.name(), newElementText);
        return addValueListElementForLocale(lookupEnityId, newElementText, lookUpName);
    }

    /**
     * Create a new list element for a single locale.
     * 
     * It would be nicer for this to operate on objects rather than ids, but it
     * is a first step that works.
     */
    public LookUpValueEntity addValueListElementForLocale(final Short lookupEnityId, final String newElementText, final String lookUpName)
            throws PersistenceException {
        MifosLookUpEntity lookUpEntity = (MifosLookUpEntity) getPersistentObject(MifosLookUpEntity.class, lookupEnityId);
        LookUpValueEntity lookUpValueEntity = new LookUpValueEntity();
        lookUpValueEntity.setLookUpEntity(lookUpEntity);
        lookUpValueEntity.setLookUpName(lookUpName);
        createOrUpdate(lookUpValueEntity);

        LookUpValueLocaleEntity lookUpValueLocaleEntity = new LookUpValueLocaleEntity();
        lookUpValueLocaleEntity.setLocaleId(MasterDataEntity.CUSTOMIZATION_LOCALE_ID);
        lookUpValueLocaleEntity.setLookUpValue(newElementText);
        lookUpValueLocaleEntity.setLookUpId(lookUpValueEntity.getLookUpId());
        createOrUpdate(lookUpValueLocaleEntity);

        // MifosConfiguration.getInstance().updateKey(lookUpValueEntity,
        // newElementText);
        MessageLookup.getInstance().updateLookupValueInCache(lookUpValueEntity, newElementText);

        return lookUpValueEntity;
    }

    /**
     * This method is intended to delete a single LookUpValueEntity and all its
     * associated LookUpValueLocaleEntity objects. The primary purpose is for
     * test script cleanup, since deletion of LookUpValueEntity elements is not
     * allowed in the main app.
     * 
     * It would be nicer for this to operate on objects rather than ids, but it
     * is a first step that works.
     */
    public void deleteValueListElement(final Integer lookupValueEntityId) throws PersistenceException {
        LookUpValueEntity lookUpValueEntity = (LookUpValueEntity) getPersistentObject(LookUpValueEntity.class,
                lookupValueEntityId);

        // the cascade property defined for lookUpValueLocales member of
        // LookUpValueEntity
        // means that deleting the LookUpValueEntity should delete all the
        // associated
        // LookUpValueLocaleEntity objects as well.
        delete(lookUpValueEntity);
        MifosConfiguration.getInstance().deleteKey(lookUpValueEntity.getLookUpName());
    }

    public void addLookUpEntity(final MifosLookUpEntity lookUpEntity) throws PersistenceException {

        createOrUpdate(lookUpEntity);
    }

    public LookUpValueLocaleEntity retrieveOneLookUpValueLocaleEntity(final short localeId, final int lookUpId)
            throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("aLocaleId", Short.valueOf(localeId));
        queryParameters.put("aLookUpId", lookUpId);
        Object obj = execUniqueResultNamedQuery(NamedQueryConstants.GETLOOKUPVALUELOCALE, queryParameters);
        if (null != obj) {
            return (LookUpValueLocaleEntity) obj;
        }
        return null;
    }

}
