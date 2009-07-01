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

import org.mifos.application.meeting.business.RecurrenceTypeEntity;
import org.mifos.application.productdefinition.business.PrdStatusEntity;
import org.mifos.application.productdefinition.business.ProductCategoryBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.persistence.PrdOfferingPersistence;
import org.mifos.application.productdefinition.persistence.SavingsPrdPersistence;
import org.mifos.application.productdefinition.util.helpers.PrdCategoryStatus;
import org.mifos.application.productdefinition.util.helpers.ProductType;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;

public class SavingsPrdBusinessService implements BusinessService {

    @Override
    public BusinessObject getBusinessObject(UserContext userContext) {
        return null;
    }

    public SavingsOfferingBO getSavingsProduct(Short prdOfferingId) throws ServiceException {
        try {
            return new SavingsPrdPersistence().getSavingsProduct(prdOfferingId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<ProductCategoryBO> getActiveSavingsProductCategories() throws ServiceException {
        try {
            return new PrdOfferingPersistence().getApplicableProductCategories(ProductType.SAVINGS,
                    PrdCategoryStatus.ACTIVE);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<RecurrenceTypeEntity> getSavingsApplicableRecurrenceTypes() throws ServiceException {
        try {
            return new SavingsPrdPersistence().getSavingsApplicableRecurrenceTypes();
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<SavingsOfferingBO> getAllSavingsProducts() throws ServiceException {
        try {
            return new SavingsPrdPersistence().getAllSavingsProducts();
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<PrdStatusEntity> getApplicablePrdStatus(Short localeId) throws ServiceException {
        try {
            return new PrdOfferingPersistence().getApplicablePrdStatus(ProductType.SAVINGS, localeId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }

    }

    public Short retrieveDormancyDays() throws ServiceException {
        try {
            return new SavingsPrdPersistence().retrieveDormancyDays();
        } catch (PersistenceException pe) {
            throw new ServiceException(pe);
        }
    }
}
