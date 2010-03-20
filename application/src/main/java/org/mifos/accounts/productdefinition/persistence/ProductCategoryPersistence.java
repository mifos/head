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

package org.mifos.accounts.productdefinition.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.accounts.productdefinition.business.PrdCategoryStatusEntity;
import org.mifos.accounts.productdefinition.business.ProductCategoryBO;
import org.mifos.accounts.productdefinition.business.ProductTypeEntity;
import org.mifos.accounts.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.NamedQueryConstants;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.Persistence;

public class ProductCategoryPersistence extends Persistence {

    public Short getMaxPrdCategoryId() throws PersistenceException {
        return (Short) execUniqueResultNamedQuery(NamedQueryConstants.PRODUCTCATEGORIES_MAX, null);
    }

    public Integer getProductCategory(String productCategoryName) throws PersistenceException {
        Map<Object, Object> queryParameters = new HashMap<Object, Object>();
        queryParameters.put(ProductDefinitionConstants.PRODUCTCATEGORYNAME, productCategoryName);
        return ((Number) execUniqueResultNamedQuery(NamedQueryConstants.PRODUCTCATEGORIES_COUNT_CREATE, queryParameters))
                .intValue();

    }

    public Integer getProductCategory(String productCategoryName, Short productCategoryId) throws PersistenceException {
        Map<Object, Object> queryParameters = new HashMap<Object, Object>();
        queryParameters.put(ProductDefinitionConstants.PRODUCTCATEGORYNAME, productCategoryName);
        queryParameters.put(ProductDefinitionConstants.PRODUCTCATEGORYID, productCategoryId);
        return ((Number) execUniqueResultNamedQuery(NamedQueryConstants.PRODUCTCATEGORIES_COUNT_UPDATE, queryParameters))
                .intValue();
    }

    @SuppressWarnings("cast")
    public List<ProductTypeEntity> getProductTypes() throws PersistenceException {
        return (List<ProductTypeEntity>) executeNamedQuery(NamedQueryConstants.GET_PRD_TYPES, null);
    }

    @SuppressWarnings("cast")
    public ProductTypeEntity getProductTypes(Short prdtype) throws PersistenceException {
        return (ProductTypeEntity) getPersistentObject(ProductTypeEntity.class, prdtype);
    }

    public ProductCategoryBO findByGlobalNum(String globalNum) throws PersistenceException {
        Map<Object, Object> queryParameters = new HashMap<Object, Object>();
        queryParameters.put("globalNum", globalNum);
        return (ProductCategoryBO) execUniqueResultNamedQuery(NamedQueryConstants.GET_PRODUCTCATEGORY, queryParameters);

    }

    @SuppressWarnings("cast")
    public List<PrdCategoryStatusEntity> getProductCategoryStatusList() throws PersistenceException {
        return (List<PrdCategoryStatusEntity>) executeNamedQuery(NamedQueryConstants.GET_PRDCATEGORYSTATUS, null);

    }

    @SuppressWarnings("cast")
    public List<ProductCategoryBO> getAllCategories() throws PersistenceException {
        return (List<ProductCategoryBO>) executeNamedQuery(NamedQueryConstants.PRODUCTCATEGORIES_SEARCH, null);

    }

    @SuppressWarnings("cast")
    public ProductTypeEntity getProductTypesByID() throws PersistenceException {
        return (ProductTypeEntity) executeNamedQuery(NamedQueryConstants.GET_PRD_TYPES, null);
    }
}
