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

package org.mifos.config.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.mifos.accounts.business.AccountStateEntity;
import org.mifos.accounts.business.AccountStateFlagEntity;
import org.mifos.accounts.productdefinition.business.GracePeriodTypeEntity;
import org.mifos.accounts.productdefinition.business.PrdOfferingBO;
import org.mifos.accounts.productsmix.business.ProductMixBO;
import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.master.business.LookUpEntity;
import org.mifos.application.master.business.LookUpLabelEntity;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.customers.api.CustomerLevel;
import org.mifos.customers.business.CustomerStatusEntity;
import org.mifos.customers.business.CustomerStatusFlagEntity;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.springframework.beans.factory.annotation.Autowired;

public class ApplicationConfigurationDaoHibernate implements ApplicationConfigurationDao {

    private final GenericDao genericDao;

    @Autowired
    public ApplicationConfigurationDaoHibernate(final GenericDao genericDao) {
        this.genericDao = genericDao;
    }

    @Override
    public List<GracePeriodTypeEntity> findGracePeriodTypes() {
        return doFetchListOfMasterDataFor(GracePeriodTypeEntity.class);
    }

    @SuppressWarnings("unchecked")
    private <T extends MasterDataEntity> List<T> doFetchListOfMasterDataFor(Class<T> type) {
        Session session = StaticHibernateUtil.getSessionTL();
        List<T> masterEntities = session.createQuery("from " + type.getName()).list();
        for (MasterDataEntity masterData : masterEntities) {
            Hibernate.initialize(masterData.getNames());
            Hibernate.initialize(masterData.getLookUpValue());
            Hibernate.initialize(masterData.getLookUpValue().getLookUpValueLocales());
        }
        return masterEntities;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<LookUpEntity> findLookupEntities() {
        Session session = StaticHibernateUtil.getSessionTL();
        List<LookUpEntity> entities = session.getNamedQuery(NamedQueryConstants.GET_ENTITIES).list();

        for (LookUpEntity entity : entities) {
            Set<LookUpLabelEntity> labels = entity.getLookUpLabels();
            entity.getEntityType();
            for (LookUpLabelEntity label : labels) {
                label.getLabelText();
                label.getLocaleId();
            }
        }
        return entities;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<LookUpValueEntity> findLookupValues() {
        List<LookUpValueEntity> values = null;

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
        return values;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<AccountStateEntity> findAllAccountStateEntities() {

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("prdTypeId", AccountTypes.LOAN_ACCOUNT.getValue());

        List<AccountStateEntity> queryResult = (List<AccountStateEntity>) this.genericDao.executeNamedQuery(
                NamedQueryConstants.RETRIEVEALLACCOUNTSTATES, queryParameters);
        initializeAccountStates(queryResult);

        List<AccountStateEntity> allStateEntities = new ArrayList<AccountStateEntity>(queryResult);

        queryParameters = new HashMap<String, Object>();
        queryParameters.put("prdTypeId", AccountTypes.SAVINGS_ACCOUNT.getValue());

        queryResult = (List<AccountStateEntity>) this.genericDao.executeNamedQuery(
                NamedQueryConstants.RETRIEVEALLACCOUNTSTATES, queryParameters);
        initializeAccountStates(queryResult);

        allStateEntities.addAll(queryResult);

        return allStateEntities;
    }

    private void initializeAccountStates(List<AccountStateEntity> queryResult) {
        for (AccountStateEntity accountStateEntity : queryResult) {
            for (AccountStateFlagEntity accountStateFlagEntity : accountStateEntity.getFlagSet()) {
                Hibernate.initialize(accountStateFlagEntity);
                Hibernate.initialize(accountStateFlagEntity.getNames());
            }
            Hibernate.initialize(accountStateEntity.getNames());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CustomerStatusEntity> findAllCustomerStatuses() {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("LEVEL_ID", CustomerLevel.CLIENT.getValue());
        List<CustomerStatusEntity> queryResult = (List<CustomerStatusEntity>) this.genericDao.executeNamedQuery(NamedQueryConstants.GET_CUSTOMER_STATUS_LIST, queryParameters);
        for (CustomerStatusEntity customerStatus : queryResult) {
            for (CustomerStatusFlagEntity customerStatusFlagEntity : customerStatus.getFlagSet()) {
                Hibernate.initialize(customerStatusFlagEntity);
                Hibernate.initialize(customerStatusFlagEntity.getNames());
            }
            Hibernate.initialize(customerStatus.getLookUpValue());
        }
        return queryResult;
    }

    @Override
    public LookUpEntity findLookupValueByEntityType(final String entityType) {

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("entityType", entityType);

        LookUpEntity entity = (LookUpEntity) this.genericDao.executeUniqueResultNamedQuery("findLookupEntityByEntityType", queryParameters);

        return entity;
    }

    @Override
    public void save(LookUpEntity entity) {
        this.genericDao.createOrUpdate(entity);
    }

    @Override
    public void save(MasterDataEntity entity) {
        this.genericDao.createOrUpdate(entity);
    }

    @Override
    public void save(LookUpValueEntity entity) {
        this.genericDao.createOrUpdate(entity);
    }

    @Override
    public void save(ProductMixBO productMix) {
        this.genericDao.createOrUpdate(productMix);
    }

    @Override
    public void save(PrdOfferingBO product) {
        this.genericDao.createOrUpdate(product);
    }

    @Override
    public void delete(ProductMixBO productMix) {
        this.genericDao.delete(productMix);
    }
}