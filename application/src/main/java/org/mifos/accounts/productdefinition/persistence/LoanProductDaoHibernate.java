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

package org.mifos.accounts.productdefinition.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.mifos.accounts.productdefinition.business.GracePeriodTypeEntity;
import org.mifos.accounts.productdefinition.business.InterestCalcTypeEntity;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.PrdApplicableMasterEntity;
import org.mifos.accounts.productdefinition.business.ProductCategoryBO;
import org.mifos.accounts.productdefinition.business.ProductTypeEntity;
import org.mifos.accounts.productdefinition.business.RecommendedAmntUnitEntity;
import org.mifos.accounts.productdefinition.business.SavingsTypeEntity;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.accounts.productdefinition.util.helpers.InterestCalcType;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.accounts.productdefinition.util.helpers.PrdCategoryStatus;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.accounts.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.accounts.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.accounts.productdefinition.util.helpers.SavingsType;
import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.accounts.savings.persistence.GenericDaoHibernate;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.master.business.InterestTypesEntity;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.ValueListElement;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.customers.business.CustomerLevelEntity;
import org.mifos.service.BusinessRuleException;

/**
 *
 */
public class LoanProductDaoHibernate implements LoanProductDao {

    private final GenericDao genericDao;

    public LoanProductDaoHibernate(final GenericDao genericDao) {
        this.genericDao = genericDao;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<LoanOfferingBO> findActiveLoanProductsApplicableToCustomerLevel(final CustomerLevelEntity customerLevel) {

        if (customerLevel == null) {
            throw new IllegalArgumentException("customerLevel cannot be null");
        }

        final Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(AccountConstants.PRDSTATUS, PrdStatus.LOAN_ACTIVE.getValue());
        queryParameters.put(AccountConstants.PRODUCT_APPLICABLE_TO, customerLevel.getProductApplicableType());
        return (List<LoanOfferingBO>) genericDao.executeNamedQuery(NamedQueryConstants.APPLICABLE_LOAN_PRODUCTS,
                queryParameters);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ValueListElement> findAllLoanPurposes() {

        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("entityType", MasterConstants.LOAN_PURPOSES);
        return (List<ValueListElement>) genericDao.executeNamedQuery(NamedQueryConstants.MASTERDATA_MIFOS_ENTITY_VALUE,
                queryParameters);
    }

    @Override
    public ProductTypeEntity findLoanProductConfiguration() {

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("productTypeId", AccountTypes.LOAN_ACCOUNT.getValue());

        return (ProductTypeEntity) this.genericDao.executeUniqueResultNamedQuery("findProductTypeConfigurationById", queryParameters);
    }

    @Override
    public void save(ProductCategoryBO productCategory) {
        this.genericDao.createOrUpdate(productCategory);
    }

    @Override
    public void save(ProductTypeEntity productType) {
        this.genericDao.createOrUpdate(productType);
    }

    @Override
    public void save(LoanOfferingBO loanProduct) {
        this.genericDao.createOrUpdate(loanProduct);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Object[]> findAllLoanProducts() {
        return (List<Object[]>) genericDao.executeNamedQuery("findAllLoanProducts", null);
    }

    @Override
    public List<PrdApplicableMasterEntity> retrieveSavingsApplicableProductCategories() {
        List<PrdApplicableMasterEntity> applicableCategories = doFetchListOfMasterDataFor(PrdApplicableMasterEntity.class);
        return applicableCategories;
    }

    @Override
    public List<PrdApplicableMasterEntity> retrieveLoanApplicableProductCategories() {
        List<PrdApplicableMasterEntity> applicableCategories = filter(doFetchListOfMasterDataFor(PrdApplicableMasterEntity.class));
        return applicableCategories;
    }

    private List<PrdApplicableMasterEntity> filter(List<PrdApplicableMasterEntity> nonFiltered) {
        List<PrdApplicableMasterEntity> applicableCategories = new ArrayList<PrdApplicableMasterEntity>();
        for (PrdApplicableMasterEntity entity : nonFiltered) {
            if (!ApplicableTo.CENTERS.getValue().equals(entity.getId())) {
                applicableCategories.add(entity);
            }
        }
        return applicableCategories;
    }

    @Override
    public List<GracePeriodTypeEntity> retrieveGracePeriodTypes() {
        return doFetchListOfMasterDataFor(GracePeriodTypeEntity.class);
    }

    @Override
    public List<InterestCalcTypeEntity> retrieveInterestCalcTypes() {
        return doFetchListOfMasterDataFor(InterestCalcTypeEntity.class);
    }

    @Override
    public InterestCalcTypeEntity retrieveInterestCalcType(InterestCalcType interestCalcType) {
        InterestCalcTypeEntity result = null;
        List<InterestCalcTypeEntity> allSavingsTypes = retrieveInterestCalcTypes();
        for (InterestCalcTypeEntity entity : allSavingsTypes) {
            if (entity.getId().equals(interestCalcType.getValue())) {
                result = entity;
            }
        }
        return result;
    }

    @Override
    public List<InterestTypesEntity> retrieveInterestTypes() {
        return doFetchListOfMasterDataFor(InterestTypesEntity.class);
    }

    @Override
    public List<SavingsTypeEntity> retrieveSavingsTypes() {
        return doFetchListOfMasterDataFor(SavingsTypeEntity.class);
    }

    @Override
    public SavingsTypeEntity retrieveSavingsType(SavingsType savingsType) {
        SavingsTypeEntity result = null;
        List<SavingsTypeEntity> allSavingsTypes = retrieveSavingsTypes();
        for (SavingsTypeEntity entity : allSavingsTypes) {
            if (entity.getId().equals(savingsType.getValue())) {
                result = entity;
            }
        }
        return result;
    }

    @Override
    public List<RecommendedAmntUnitEntity> retrieveRecommendedAmountTypes() {
        return doFetchListOfMasterDataFor(RecommendedAmntUnitEntity.class);
    }

    @Override
    public RecommendedAmntUnitEntity retrieveRecommendedAmountType(RecommendedAmountUnit recommendedAmountType) {
        RecommendedAmntUnitEntity result = null;
        List<RecommendedAmntUnitEntity> allSavingsTypes = retrieveRecommendedAmountTypes();
        for (RecommendedAmntUnitEntity entity : allSavingsTypes) {
            if (entity.getId().equals(recommendedAmountType.getValue())) {
                result = entity;
            }
        }
        return result;
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

    @Override
    public InterestTypesEntity findInterestType(InterestType interestType) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("ID", interestType.getValue());
        return (InterestTypesEntity) genericDao.executeUniqueResultNamedQuery("findInterestTypeById", queryParameters);
    }

    @Override
    public PrdApplicableMasterEntity findApplicableProductType(ApplicableTo applicableTo) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("ID", applicableTo.getValue());
        return (PrdApplicableMasterEntity) genericDao.executeUniqueResultNamedQuery("findApplicableProductTypeById", queryParameters);
    }

    @Override
    public GracePeriodTypeEntity findGracePeriodType(GraceType gracePeriodType) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("ID", gracePeriodType.getValue());
        return (GracePeriodTypeEntity) genericDao.executeUniqueResultNamedQuery("findGracePeriodTypeById", queryParameters);
    }

    @Override
    public ProductCategoryBO findActiveProductCategoryById(Integer category) {

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("productCategoryID", category.shortValue());
        queryParameters.put("prdCategoryStatusId", PrdCategoryStatus.ACTIVE.getValue());
        return (ProductCategoryBO) this.genericDao.executeUniqueResultNamedQuery("product.findById", queryParameters);
    }

    @Override
    public ProductCategoryBO findProductCategoryByNameAndType(String productCategoryName, Short productCategoryId) {

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("productCategoryID", productCategoryId);
        queryParameters.put("productCategoryName", productCategoryName);
        return (ProductCategoryBO) this.genericDao.executeUniqueResultNamedQuery("product.findByNameAndId", queryParameters);
    }

    @Override
    public void validateNameIsAvailableForCategory(String productCategoryName, Short productCategoryId) {
        ProductCategoryBO category = findProductCategoryByNameAndType(productCategoryName, productCategoryId);
        if (category != null) {
            throw new BusinessRuleException(ProductDefinitionConstants.DUPLICATE_CATEGORY_NAME);
        }
    }

    @Override
    public ProductCategoryBO findProductCategoryByGlobalNum(String globalPrdCategoryNum) {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("globalPrdCategoryNum", globalPrdCategoryNum);
        return (ProductCategoryBO) this.genericDao.executeUniqueResultNamedQuery("product.findByGlobalNum", queryParameters);
    }

    @Override
    public LoanOfferingBO findById(Integer productId) {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("prdOfferingId", productId.shortValue());

        return (LoanOfferingBO) this.genericDao.executeUniqueResultNamedQuery("loanProduct.byid", queryParameters);
    }
}