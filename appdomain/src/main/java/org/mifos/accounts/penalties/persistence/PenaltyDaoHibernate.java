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

package org.mifos.accounts.penalties.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.mifos.accounts.penalties.business.PenaltyBO;
import org.mifos.accounts.penalties.business.PenaltyCategoryEntity;
import org.mifos.accounts.penalties.business.PenaltyFormulaEntity;
import org.mifos.accounts.penalties.business.PenaltyFrequencyEntity;
import org.mifos.accounts.penalties.business.PenaltyPeriodEntity;
import org.mifos.accounts.penalties.business.PenaltyStatusEntity;
import org.mifos.accounts.penalties.util.helpers.PenaltyCategory;
import org.mifos.accounts.penalties.util.helpers.PenaltyFormula;
import org.mifos.accounts.penalties.util.helpers.PenaltyFrequency;
import org.mifos.accounts.penalties.util.helpers.PenaltyPeriod;
import org.mifos.accounts.penalties.util.helpers.PenaltyStatus;
import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.springframework.beans.factory.annotation.Autowired;

public class PenaltyDaoHibernate implements PenaltyDao {
    private GenericDao genericDao;

    @Autowired
    public PenaltyDaoHibernate(GenericDao genericDao) {
        this.genericDao = genericDao;
    }
    
    @Override
    public PenaltyBO findPenaltyById(int penaltyId) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("id", (short)penaltyId);
        
        return (PenaltyBO) this.genericDao.executeUniqueResultNamedQuery(NamedQueryConstants.GET_PENALTY_BY_ID, queryParameters);
    }
    
    @Override
    public List<PenaltyBO> findAllLoanPenalties() {
        return (List<PenaltyBO>) this.genericDao.executeNamedQuery(NamedQueryConstants.GET_LOAN_PENALTIES, null);
    }

    @Override
    public List<PenaltyBO> findAllSavingPenalties() {
        return (List<PenaltyBO>) this.genericDao.executeNamedQuery(NamedQueryConstants.GET_SAVING_PENALTIES, null);
    }
    
    @Override
    public PenaltyPeriodEntity findPenaltyPeriodEntityByType(PenaltyPeriod penaltyPeriod) {
        return retrieveMasterEntity(PenaltyPeriodEntity.class, penaltyPeriod.getValue());
    }

    @Override
    public PenaltyFormulaEntity findPenaltyFormulaEntityByType(PenaltyFormula penaltyFormula) {
        return retrieveMasterEntity(PenaltyFormulaEntity.class, penaltyFormula.getValue());
    }

    @Override
    public void save(PenaltyBO penaltyBO) {
        this.genericDao.createOrUpdate(penaltyBO);
    }

    @Override
    public PenaltyFrequencyEntity findPenaltyFrequencyEntityByType(PenaltyFrequency penaltyFrequency) {
        return retrieveMasterEntity(PenaltyFrequencyEntity.class, penaltyFrequency.getValue());
    }

    @Override
    public PenaltyCategoryEntity findPenaltyCategoryEntityByType(PenaltyCategory penaltyCategory) {
        return retrieveMasterEntity(PenaltyCategoryEntity.class, penaltyCategory.getValue());
    }
    
    @Override
    public PenaltyStatusEntity findPenaltyStatusEntityByType(PenaltyStatus penaltyStatus) {
        return retrieveMasterEntity(PenaltyStatusEntity.class, penaltyStatus.getValue());
    }
    
    @Override
    public List<PenaltyCategoryEntity> getPenaltiesCategories() {
        return doFetchListOfMasterDataFor(PenaltyCategoryEntity.class);
    }

    @Override
    public List<PenaltyPeriodEntity> getPenaltiesPeriods() {
        return doFetchListOfMasterDataFor(PenaltyPeriodEntity.class);
    }

    @Override
    public List<PenaltyFormulaEntity> getPenaltiesFormulas() {
        return doFetchListOfMasterDataFor(PenaltyFormulaEntity.class);
    }

    @Override
    public List<PenaltyFrequencyEntity> getPenaltiesFrequencies() {
        return doFetchListOfMasterDataFor(PenaltyFrequencyEntity.class);
    }
    
    @Override
    public List<PenaltyStatusEntity> getPenaltiesStatuses() {
        return doFetchListOfMasterDataFor(PenaltyStatusEntity.class);
    }
    
    @SuppressWarnings("unchecked")
    private <T extends MasterDataEntity> T retrieveMasterEntity(final Class<T> entityType, final Short entityId) {
        Session session = StaticHibernateUtil.getSessionTL();
        Criteria criteriaQuery = session.createCriteria(entityType);
        criteriaQuery.add(Restrictions.eq("id", entityId));
        criteriaQuery.setFetchMode("lookUpValue", FetchMode.JOIN);

        return (T) criteriaQuery.uniqueResult();
    }
    
    @SuppressWarnings("unchecked")
    private <T extends MasterDataEntity> List<T> doFetchListOfMasterDataFor(Class<T> type) {
        Session session = StaticHibernateUtil.getSessionTL();
        List<T> masterEntities = session.createQuery("from " + type.getName()).list();
        
        for (MasterDataEntity masterData : masterEntities) {
            Hibernate.initialize(masterData.getNames());
        }
        
        return masterEntities;
    }

}
