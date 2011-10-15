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

package org.mifos.accounts.productdefinition.persistence;

import java.util.HashMap;
import java.util.List;

import org.hibernate.Hibernate;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.PrdOfferingBO;
import org.mifos.accounts.productdefinition.business.PrdStatusEntity;
import org.mifos.accounts.productdefinition.business.ProductCategoryBO;
import org.mifos.accounts.productdefinition.business.ProductTypeEntity;
import org.mifos.accounts.productdefinition.util.helpers.PrdCategoryStatus;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.accounts.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.accounts.productdefinition.util.helpers.ProductType;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.application.NamedQueryConstants;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.LegacyGenericDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unchecked")
public class PrdOfferingPersistence extends LegacyGenericDao {
    private static final Logger logger = LoggerFactory.getLogger(PrdOfferingPersistence.class);

    public Short getMaxPrdOffering() throws PersistenceException {
        logger.debug("getting the max prd offering id");
        return (Short) execUniqueResultNamedQuery(NamedQueryConstants.PRODUCTOFFERING_MAX, null);
    }

    public PrdStatusEntity getPrdStatus(PrdStatus prdStatus) throws PersistenceException {
        logger.debug("getting the product status for :" + prdStatus);
        return getPersistentObject(PrdStatusEntity.class, prdStatus.getValue());
    }

    public Integer getProductOfferingNameCount(String productOfferingName) throws PersistenceException {
        logger.debug("getting the product offering name count for :" + productOfferingName);
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(ProductDefinitionConstants.PRDOFFERINGNAME, productOfferingName);
        return ((Number) execUniqueResultNamedQuery(NamedQueryConstants.PRODUCTOFFERING_CREATEOFFERINGNAMECOUNT,
                queryParameters)).intValue();
    }

    public Integer getProductOfferingShortNameCount(String productOfferingShortName) throws PersistenceException {
        logger.debug("getting the product offering short name count for :" + productOfferingShortName);
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(ProductDefinitionConstants.PRDOFFERINGSHORTNAME, productOfferingShortName);
        return ((Number) execUniqueResultNamedQuery(NamedQueryConstants.PRODUCTOFFERING_CREATEOFFERINGSHORTNAMECOUNT,
                queryParameters)).intValue();
    }

    public List<ProductCategoryBO> getApplicableProductCategories(ProductType productType,
            PrdCategoryStatus prdCategoryStatus) throws PersistenceException {
        logger.debug("getting the applicable product categories");
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(ProductDefinitionConstants.PRODUCTTYPEID, productType.getValue());
        queryParameters.put(ProductDefinitionConstants.PRODUCTCATEGORYSTATUSID, prdCategoryStatus.getValue());
        List<ProductCategoryBO> queryResult = executeNamedQuery(NamedQueryConstants.PRDAPPLICABLE_CATEGORIES,
                queryParameters);

        if (null != queryResult && queryResult.size() > 0) {
            for (ProductCategoryBO productCategory : queryResult) {
                productCategory.getProductType();
            }
        }
        logger.debug("getting the applicable product categories Done and : " + queryResult);
        return queryResult;
    }

    @SuppressWarnings("cast")
    public List<PrdStatusEntity> getApplicablePrdStatus(ProductType productType, Short localeId)
            throws PersistenceException {
        logger.debug("getting the applicable product Status");
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(ProductDefinitionConstants.PRODUCTTYPEID, productType.getValue());
        List<PrdStatusEntity> prdStatusList = (List<PrdStatusEntity>) executeNamedQuery(
                NamedQueryConstants.PRODUCT_STATUS, queryParameters);
        for (PrdStatusEntity prdStatus : prdStatusList) {
            Hibernate.initialize(prdStatus);
            Hibernate.initialize(prdStatus.getPrdState());
        }
        logger.debug("getting the applicable product Status Done and : " + prdStatusList);
        return prdStatusList;
    }

    public List<PrdOfferingBO> getAllPrdOffringByType(String prdType) throws PersistenceException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(ProductDefinitionConstants.PRODUCTTYPE, Short.valueOf(prdType));
        if (prdType.equals(ProductType.LOAN.getValue().toString())) {
            queryParameters.put(AccountConstants.PRDSTATUS, PrdStatus.LOAN_ACTIVE.getValue());
        } else if (prdType.equals(ProductType.SAVINGS.getValue().toString())) {
            queryParameters.put(AccountConstants.PRDSTATUS, PrdStatus.SAVINGS_ACTIVE.getValue());
        }

        return executeNamedQuery(NamedQueryConstants.PRD_BYTYPE, queryParameters);

    }

    public List<PrdOfferingBO> getAllowedPrdOfferingsByType(String prdId, String prdType) throws PersistenceException {

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(ProductDefinitionConstants.PRODUCTTYPE, Short.valueOf(prdType));
        queryParameters.put(ProductDefinitionConstants.PRODUCTID, Short.valueOf(prdId));
        if (prdType.equals(ProductType.LOAN.getValue().toString())) {
            queryParameters.put(AccountConstants.PRDSTATUS, PrdStatus.LOAN_ACTIVE.getValue());
        } else if (prdType.equals(ProductType.SAVINGS.getValue().toString())) {
            queryParameters.put(AccountConstants.PRDSTATUS, PrdStatus.SAVINGS_ACTIVE.getValue());
        }

        return executeNamedQuery(NamedQueryConstants.ALLOWED_PRD_OFFERING_BYTYPE, queryParameters);

    }

    public List<PrdOfferingBO> getAllowedPrdOfferingsForMixProduct(String prdId, String prdType)
            throws PersistenceException {

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(ProductDefinitionConstants.PRODUCTTYPE, Short.valueOf(prdType));
        queryParameters.put(ProductDefinitionConstants.PRODUCTID, Short.valueOf(prdId));
        if (prdType.equals(ProductType.LOAN.getValue().toString())) {
            queryParameters.put(AccountConstants.PRDSTATUS, PrdStatus.LOAN_ACTIVE.getValue());
        } else if (prdType.equals(ProductType.SAVINGS.getValue().toString())) {
            queryParameters.put(AccountConstants.PRDSTATUS, PrdStatus.SAVINGS_ACTIVE.getValue());
        }
        return executeNamedQuery(NamedQueryConstants.ALLOWED_PRD_OFFERING_FOR_MIXPRODUCT, queryParameters);

    }

    public List<PrdOfferingBO> getNotAllowedPrdOfferingsByType(String prdId) throws PersistenceException {

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(ProductDefinitionConstants.PRODUCTID, Short.valueOf(prdId));
        return executeNamedQuery(NamedQueryConstants.NOT_ALLOWED_PRD_OFFERING_BYTYPE, queryParameters);

    }

    public List<PrdOfferingBO> getNotAllowedPrdOfferingsForMixProduct(String prdId, String prdType)
            throws PersistenceException {

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(ProductDefinitionConstants.PRODUCTID, Short.valueOf(prdId));
        return executeNamedQuery(NamedQueryConstants.NOT_ALLOWED_PRD_OFFERING_FOR_MIXPRODUCT, queryParameters);

    }

    public PrdOfferingBO getPrdOffering(Short prdofferingId) throws PersistenceException {
        return getPersistentObject(PrdOfferingBO.class, prdofferingId);
    }

    public LoanOfferingBO getLoanPrdOffering(Short loanPrdOfferingId) throws PersistenceException {
        return getPersistentObject(LoanOfferingBO.class, loanPrdOfferingId);
    }

    public PrdOfferingBO getPrdOfferingByID(Short prdId) throws PersistenceException {
        logger.debug("getting the product offering by id :" + prdId);
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(ProductDefinitionConstants.PRODUCTID, Short.valueOf(prdId));
        PrdOfferingBO prdOffring = (PrdOfferingBO) execUniqueResultNamedQuery(NamedQueryConstants.PRD_BYID,
                queryParameters);
        return prdOffring;
    }

    public ProductTypeEntity getProductTypes(Short prdtype) throws PersistenceException {
        return getPersistentObject(ProductTypeEntity.class, prdtype);
    }

    @SuppressWarnings("cast")
    public List<PrdOfferingBO> getPrdOfferingMix() throws PersistenceException {
        return (List<PrdOfferingBO>) executeNamedQuery(NamedQueryConstants.LOAD_PRODUCTS_OFFERING_MIX, null);
    }
}