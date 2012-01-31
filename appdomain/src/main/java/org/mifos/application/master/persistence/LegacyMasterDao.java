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

package org.mifos.application.master.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.CustomValueDto;
import org.mifos.application.master.business.CustomValueListElementDto;
import org.mifos.application.master.business.LookUpEntity;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.config.Localization;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.dto.domain.ValueListElement;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.hibernate.helper.Transactional;
import org.mifos.framework.persistence.LegacyGenericDao;
import org.mifos.framework.util.helpers.SearchUtils;
import org.mifos.security.activity.DynamicLookUpValueCreationTypes;

/**
 * This class is mostly used to look up instances of (a subclass of)
 * {@link MasterDataEntity} in the database. Most of what is here can better be
 * accomplished by enums and by {@link MessageLookup}.
 */
public class LegacyMasterDao extends LegacyGenericDao {

    protected LegacyMasterDao() {
    }

    /**
     * Only two non-test usages, one that may never be called and one for getting labels.
     */
    @Transactional
    public CustomValueDto getLookUpEntity(final String entityName) throws SystemException {
        Session session = getSession();
        Query queryEntity = session.getNamedQuery("masterdata.entityvalue");
        queryEntity.setString("entityType", entityName);
        CustomValueDto entity = (CustomValueDto) queryEntity.uniqueResult();
        entity.setCustomValueListElements(lookUpValue(entityName, session));
        return entity;
    }

    @SuppressWarnings("unchecked")
    private List<CustomValueListElementDto> lookUpValue(final String entityName, final Session session) {
        Query queryEntity = session.getNamedQuery("masterdata.entitylookupvalue");
        queryEntity.setString("entityType", entityName);
        List<CustomValueListElementDto> entityList = queryEntity.list();
        return entityList;
    }

    /**
     * Used once in getMasterData, otherwise, test usage and one other method
     * MifosPropertyMessageResources.getCustomValueListElements (and that method may never be called)
     */
    public CustomValueDto getCustomValueList(final String entityName, final String classPath, final String column)
            throws SystemException {
        Session session = null;
        session = getSession();
        Query queryEntity = session.getNamedQuery("masterdata.entityvalue");
        queryEntity.setString("entityType", entityName);
        CustomValueDto entity = (CustomValueDto) queryEntity.uniqueResult();
        List<CustomValueListElementDto> listElements = getCustomValueListElements(entityName, classPath, column, session);
        entity.setCustomValueListElements(listElements);
        return entity;
    }

    @SuppressWarnings("unchecked")
    private List<CustomValueListElementDto> getCustomValueListElements(final String entityName,
            final String entityClass, final String column, final Session session) {
        Query queryEntity = session
                .createQuery("select new org.mifos.application.master.business.CustomValueListElementDto(" + "mainTable."
                        + column + " ,lookup.lookUpId,lookupvalue.lookUpValue,lookup.lookUpName) "
                        + "from org.mifos.application.master.business.LookUpValueEntity lookup,"
                        + "org.mifos.application.master.business.LookUpValueLocaleEntity lookupvalue," + entityClass
                        + " mainTable " + "where mainTable.lookUpId = lookup.lookUpId"
                        + " and lookup.lookUpEntity.entityType = ?" + " and lookup.lookUpId = lookupvalue.lookUpId"
                        + " and lookupvalue.localeId = ?");
        queryEntity.setString(0, entityName);
        queryEntity.setShort(1, (short) 1);
        List<CustomValueListElementDto> entityList = queryEntity.list();

        return entityList;
    }

    /**
     * use Dao specific calls - see feeDao.doRetrieveFeeCategories
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public <T extends MasterDataEntity> List<T> findMasterDataEntitiesWithLocale(final Class<T> type) {
        Session session = getSession();
        List<T> masterEntities = session.createQuery("from " + type.getName()).list();
        for (MasterDataEntity masterData : masterEntities) {
            Hibernate.initialize(masterData.getNames());
        }
        return masterEntities;
    }

    @SuppressWarnings("unchecked")
    public <T extends MasterDataEntity> T findMasterDataEntityWithLocale(final Class<T> entityType, final Short entityId) throws PersistenceException {
        Session session = getSession();
        List<T> masterEntities = session.createQuery(
                "from " + entityType.getName() + " masterEntity where masterEntity.id = " + entityId).list();
        if (masterEntities != null && masterEntities.size() > 0) {
            T masterDataEntity = masterEntities.get(0);
            Hibernate.initialize(masterDataEntity.getNames());
            return masterDataEntity;
        }
        throw new PersistenceException("errors.entityNotFound");
    }

    /**
     * This method is used to retrieve both custom and fixed value list
     * elements.
     * @deprecated - use specific methods on {@link CustomerDao}
     * @see CustomerDao#retrieveSalutations()
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public List<ValueListElement> findValueListElements(final String entityName)  {
        Query query = getSession().getNamedQuery(NamedQueryConstants.MASTERDATA_MIFOS_ENTITY_VALUE);
        query.setParameter("entityType", entityName);
        return query.list();

    }

    /*
     * @param entityId - the primary key of a LookUpValueEntity
     */
    public String getMessageForLookupEntity(final Integer entityId) throws PersistenceException {
        LookUpValueEntity lookupValue = getPersistentObject(LookUpValueEntity.class, entityId);
        return ApplicationContextProvider.getBean(MessageLookup.class).lookup(lookupValue);
    }

    @SuppressWarnings("unchecked")
    public <T extends MasterDataEntity> List<T> findMasterDataEntities(final Class<T> clazz) {
        List<MasterDataEntity> queryResult = getSession().createQuery("from " + clazz.getName()).list();
        return (List<T>) queryResult;
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
        LookUpValueEntity lookupValueEntity = getPersistentObject(LookUpValueEntity.class,
                lookupValueEntityId);
        Set<LookUpValueLocaleEntity> lookUpValueLocales = lookupValueEntity.getLookUpValueLocales();
        if (lookUpValueLocales != null) {
            for (LookUpValueLocaleEntity entity : lookUpValueLocales) {
                if (entity.getLookUpId().equals(lookupValueEntityId)) {
                    entity.setLookUpValue(newValue);
                    createOrUpdate(entity);
                    ApplicationContextProvider.getBean(MessageLookup.class).updateLookupValueInCache(lookupValueEntity.getLookUpName(), newValue);
                    StaticHibernateUtil.commitTransaction();
                    break;
                }
            }
        }

    }

    public LookUpValueEntity addValueListElementForLocale(final DynamicLookUpValueCreationTypes type, final Short lookupEnityId,
            final String newElementText) throws PersistenceException {

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
        LookUpEntity lookUpEntity = getPersistentObject(LookUpEntity.class, lookupEnityId);
        LookUpValueEntity lookUpValueEntity = new LookUpValueEntity();
        lookUpValueEntity.setLookUpEntity(lookUpEntity);
        lookUpValueEntity.setLookUpName(lookUpName);
        createOrUpdate(lookUpValueEntity);

        LookUpValueLocaleEntity lookUpValueLocaleEntity = new LookUpValueLocaleEntity();
        lookUpValueLocaleEntity.setLocaleId(Localization.ENGLISH_LOCALE_ID);
        lookUpValueLocaleEntity.setLookUpValue(newElementText);
        lookUpValueLocaleEntity.setLookUpId(lookUpValueEntity.getLookUpId());
        createOrUpdate(lookUpValueLocaleEntity);

        // MifosConfiguration.getInstance().updateKey(lookUpValueEntity,
        // newElementText);
        ApplicationContextProvider.getBean(MessageLookup.class).updateLookupValueInCache(lookUpValueEntity, newElementText);

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
        LookUpValueEntity lookUpValueEntity = getPersistentObject(LookUpValueEntity.class,
                lookupValueEntityId);
        delete(lookUpValueEntity);
        ApplicationContextProvider.getBean(MessageLookup.class).deleteKey(lookUpValueEntity.getLookUpName());
    }

    public void addLookUpEntity(final LookUpEntity lookUpEntity) throws PersistenceException {
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