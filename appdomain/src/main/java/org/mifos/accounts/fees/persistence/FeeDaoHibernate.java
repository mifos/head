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

package org.mifos.accounts.fees.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.mifos.accounts.fees.business.CategoryTypeEntity;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeeFormulaEntity;
import org.mifos.accounts.fees.business.FeeFrequencyTypeEntity;
import org.mifos.accounts.fees.business.FeePaymentEntity;
import org.mifos.accounts.fees.business.FeeStatusEntity;
import org.mifos.accounts.fees.business.RateFeeBO;
import org.mifos.accounts.fees.util.helpers.FeeCategory;
import org.mifos.accounts.fees.util.helpers.FeeFormula;
import org.mifos.accounts.fees.util.helpers.FeeFrequencyType;
import org.mifos.accounts.fees.util.helpers.FeePayment;
import org.mifos.accounts.fees.util.helpers.FeeStatus;
import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.core.MifosRuntimeException;
import org.mifos.dto.domain.FeeDto;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.springframework.beans.factory.annotation.Autowired;

public class FeeDaoHibernate implements FeeDao {

    private final GenericDao genericDao;

    @Autowired
    public FeeDaoHibernate(GenericDao genericDao) {
        this.genericDao = genericDao;
    }

    @Override
    public FeeBO findById(Short feeId) {

        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("feeId", feeId);

        return (FeeBO) this.genericDao.executeUniqueResultNamedQuery("findFeeById", queryParameters);
    }

    @Override
    public FeeDto findDtoById(Short feeId) {

        Session session = StaticHibernateUtil.getSessionTL();
        Criteria criteriaQuery = session.createCriteria(FeeBO.class);
        criteriaQuery.add(Restrictions.eq("id", feeId));
        criteriaQuery.setFetchMode("lookUpValue", FetchMode.JOIN);
        criteriaQuery.setFetchMode("categoryType", FetchMode.JOIN);
        criteriaQuery.setFetchMode("feeStatus", FetchMode.JOIN);
        criteriaQuery.setFetchMode("feeFrequency", FetchMode.JOIN);

        FeeBO fee = (FeeBO) criteriaQuery.uniqueResult();
        return fee.toDto();
    }


    @Override
    public void save(FeeBO fee) {
        this.genericDao.createOrUpdate(fee);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<FeeDto> retrieveAllProductFees() {
        List<FeeBO> allProductFees = (List<FeeBO>) this.genericDao.executeNamedQuery("retrieveProductFees", null);

        return assembleFeeDto(allProductFees);
    }

    @Override
	@SuppressWarnings("unchecked")
    public List<Short> getUpdatedFeesForCustomer() {
        return (List<Short>) genericDao.executeNamedQuery("retrieveUpdatedFeesApplicableToCustomers", null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<FeeDto> retrieveAllCustomerFees() {
        List<FeeBO> allCustomerFees = (List<FeeBO>) this.genericDao.executeNamedQuery("retrieveCustomerFees", null);
        return assembleFeeDto(allCustomerFees);
    }

    @Override
	@SuppressWarnings("unchecked")
    public List<FeeBO> getAllAppllicableFeeForLoanCreation() {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("active", FeeStatus.ACTIVE.getValue());
        queryParameters.put("category", FeeCategory.LOAN.getValue());
        return (List<FeeBO>) genericDao.executeNamedQuery(NamedQueryConstants.GET_ALL_APPLICABLE_FEE_FOR_LOAN_CREATION, queryParameters);
    }

    private List<FeeDto> assembleFeeDto(List<FeeBO> allProductFees) {
        List<FeeDto> fees = new ArrayList<FeeDto>();
        for (FeeBO fee : allProductFees) {
            fees.add(fee.toDto());
        }
        return fees;
    }

    @Override
    public List<CategoryTypeEntity> doRetrieveFeeCategories() {
        return doFetchListOfMasterDataFor(CategoryTypeEntity.class);
    }

    @Override
    public List<FeeFormulaEntity> retrieveFeeFormulae() {
        return doFetchListOfMasterDataFor(FeeFormulaEntity.class);
    }

    @Override
    public List<FeeFrequencyTypeEntity> retrieveFeeFrequencies() {
        return doFetchListOfMasterDataFor(FeeFrequencyTypeEntity.class);
    }

    @Override
    public List<FeePaymentEntity> retrieveFeePayments() {
        return doFetchListOfMasterDataFor(FeePaymentEntity.class);
    }

    @Override
    public List<FeeStatusEntity> retrieveFeeStatuses() {
        return doFetchListOfMasterDataFor(FeeStatusEntity.class);
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

    @Override
    public FeeFrequencyTypeEntity findFeeFrequencyEntityByType(FeeFrequencyType feeFrequencyType) {
        return retrieveMasterEntity(FeeFrequencyTypeEntity.class, feeFrequencyType.getValue());
    }

    @Override
    public CategoryTypeEntity findFeeCategoryTypeEntityByType(FeeCategory categoryType) {
        return retrieveMasterEntity(CategoryTypeEntity.class, categoryType.getValue());
    }

    @Override
    public FeeFormulaEntity findFeeFormulaEntityByType(FeeFormula feeFormula) {
        return retrieveMasterEntity(FeeFormulaEntity.class, feeFormula.getValue());
    }

    @Override
    public FeePaymentEntity findFeePaymentEntityByType(FeePayment feePayment) {
        return retrieveMasterEntity(FeePaymentEntity.class, feePayment.getValue());
    }

    @SuppressWarnings("unchecked")
    private <T extends MasterDataEntity> T retrieveMasterEntity(final Class<T> entityType, final Short entityId) {

        Session session = StaticHibernateUtil.getSessionTL();
        Criteria criteriaQuery = session.createCriteria(entityType);
        criteriaQuery.add(Restrictions.eq("id", entityId));
        criteriaQuery.setFetchMode("lookUpValue", FetchMode.JOIN);

        return (T) criteriaQuery.uniqueResult();
    }

    @Override
    public <T> T initializeAndUnproxy(T var) {
        return genericDao.initializeAndUnproxy(var);
    }

	@Override
	public void remove(FeeBO fee, boolean isInProducts, boolean isFeeInUsedLoan, boolean remove) throws Exception {
		if (isInProducts && !isFeeInUsedLoan) {	
	        Map<String, Object> queryParameters = new HashMap<String, Object>();
	        queryParameters.put("FEEID", fee.getFeeId());
			this.genericDao.executeNamedQueryDelete("deleteFeeFromPrdOfferingFees", queryParameters);
			
			if (remove) {
			    this.genericDao.delete(fee);
			}
		}
		else if (isInProducts && isFeeInUsedLoan && !remove) {
			 Map<String, Object> queryParameters = new HashMap<String, Object>();
			 queryParameters.put("FEEID", fee.getFeeId());
			 this.genericDao.executeNamedQueryDelete("deleteFeeFromPrdOfferingFees", queryParameters);
			 this.genericDao.executeNamedQueryDelete("deleteFeeFromAccountFeesAndTrhxHistoryAndOriginalFeeSchedule", 
					 queryParameters);
		}
		else if (!isInProducts && !isFeeInUsedLoan && remove) {
			this.genericDao.delete(fee);
		}
		else {
			throw new MifosRuntimeException();
		}
	}

	@Override
	public Short findFeeAppliedToLoan(Short feeId) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("FEEID", feeId);        
        return (Short) this.genericDao.executeUniqueResultNamedQuery("findFeeAppliedToLoan", queryParameters);
	}
	
	@Override
	public Short findFeeInSchedule(Short feeId) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("FEEID", feeId);        
        return (Short) this.genericDao.executeUniqueResultNamedQuery("findFeeInSchedule", queryParameters);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Short> getAllUsedLoansWithAttachedFee() {
		return (List<Short>) this.genericDao.executeNamedQuery("getAllUsedLoansWithAttachedFee", null);
	}
	
	@Override
	public RateFeeBO findRateFeeById(Short feeId){
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("feeId", feeId);

        return (RateFeeBO) this.genericDao.executeUniqueResultNamedQuery("findRateFeeById", queryParameters);
	}
}