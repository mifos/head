/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.mifos.accounts.productdefinition.business.GracePeriodTypeEntity;
import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.accounts.savings.persistence.GenericDaoHibernate;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.master.business.LookUpEntity;
import org.mifos.application.master.business.LookUpLabelEntity;
import org.mifos.application.master.business.MasterDataEntity;

public class ApplicationConfigurationDaoHibernate implements ApplicationConfigurationDao {

    private final GenericDao genericDao;

    public ApplicationConfigurationDaoHibernate(final GenericDao genericDao) {
        this.genericDao = genericDao;
    }

    @Override
    public List<GracePeriodTypeEntity> findGracePeriodTypes() {
        return doFetchListOfMasterDataFor(GracePeriodTypeEntity.class);
    }

    @SuppressWarnings("unchecked")
    private <T extends MasterDataEntity> List<T> doFetchListOfMasterDataFor(Class<T> type) {
        Session session = ((GenericDaoHibernate) this.genericDao).getHibernateUtil().getSessionTL();
        List<T> masterEntities = session.createQuery("from " + type.getName()).list();
        for (MasterDataEntity masterData : masterEntities) {
            Hibernate.initialize(masterData.getNames());
        }
        return masterEntities;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<LookUpEntity> findLookupValueTypes() {

        Session session = ((GenericDaoHibernate) this.genericDao).getHibernateUtil().getSessionTL();

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
}
