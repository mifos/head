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

package org.mifos.application.productdefinition.business.service;

import java.util.List;

import org.mifos.application.productdefinition.business.PrdCategoryStatusEntity;
import org.mifos.application.productdefinition.business.ProductCategoryBO;
import org.mifos.application.productdefinition.business.ProductTypeEntity;
import org.mifos.application.productdefinition.persistence.ProductCategoryPersistence;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;

public class ProductCategoryBusinessService implements BusinessService {

    @Override
    public BusinessObject getBusinessObject(UserContext userContext) {
        return null;
    }

    public List<ProductTypeEntity> getProductTypes() throws ServiceException {
        try {
            return new ProductCategoryPersistence().getProductTypes();
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public ProductCategoryBO findByGlobalNum(String globalNum) throws ServiceException {
        try {
            return new ProductCategoryPersistence().findByGlobalNum(globalNum);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<PrdCategoryStatusEntity> getProductCategoryStatusList() throws ServiceException {
        try {
            return new ProductCategoryPersistence().getProductCategoryStatusList();
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<ProductCategoryBO> getAllCategories() throws ServiceException {
        try {
            return new ProductCategoryPersistence().getAllCategories();
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

}
