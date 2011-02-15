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

package org.mifos.accounts.productdefinition.business.service;

import java.util.Iterator;
import java.util.List;

import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.PrdApplicableMasterEntity;
import org.mifos.accounts.productdefinition.business.PrdStatusEntity;
import org.mifos.accounts.productdefinition.business.ProductCategoryBO;
import org.mifos.accounts.productdefinition.persistence.LoanPrdPersistence;
import org.mifos.accounts.productdefinition.persistence.LoanProductDao;
import org.mifos.accounts.productdefinition.persistence.PrdOfferingPersistence;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.PrdCategoryStatus;
import org.mifos.accounts.productdefinition.util.helpers.ProductType;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.persistence.LegacyMasterDao;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.framework.business.AbstractBusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.security.util.UserContext;

public class LoanPrdBusinessService implements BusinessService {

    LegacyMasterDao legacyMasterDao = ApplicationContextProvider.getBean(LegacyMasterDao.class);

    @Override
    public AbstractBusinessObject getBusinessObject(@SuppressWarnings("unused") final UserContext userContext) {
        return null;
    }

    public List<ProductCategoryBO> getActiveLoanProductCategories() throws ServiceException {
        try {
            return getPrdOfferingPersistence().getApplicableProductCategories(ProductType.LOAN,
                    PrdCategoryStatus.ACTIVE);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    protected PrdOfferingPersistence getPrdOfferingPersistence() {
        return new PrdOfferingPersistence();
    }

    /**
     * @deprecated - see {@link LoanProductDao#retrieveLoanApplicableProductCategories}
     */
    @Deprecated
    public List<? extends MasterDataEntity> getLoanApplicableCustomerTypes(final Short localeId) throws ServiceException {
        List<PrdApplicableMasterEntity> applList = legacyMasterDao.findMasterDataEntitiesWithLocale(PrdApplicableMasterEntity.class, localeId);
        if (applList != null) {
            for (Iterator<PrdApplicableMasterEntity> iter = applList.iterator(); iter.hasNext();) {
                MasterDataEntity masterData = iter.next();
                if (masterData.getId().equals(ApplicableTo.CENTERS.getValue())) {
                    iter.remove();
                }
            }
        }
        return applList;
    }

    public LoanOfferingBO getLoanOffering(final Short prdofferingId) throws ServiceException {
        try {
            return getLoanPrdPersistence().getLoanOffering(prdofferingId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<PrdStatusEntity> getApplicablePrdStatus(final Short localeId) throws ServiceException {
        try {
            return getPrdOfferingPersistence().getApplicablePrdStatus(ProductType.LOAN, localeId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public LoanOfferingBO getLoanOffering(final Short loanOfferingId, final Short localeId) throws ServiceException {
        try {
            return getLoanPrdPersistence().getLoanOffering(loanOfferingId, localeId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<LoanOfferingBO> getAllLoanOfferings(final Short localeId) throws ServiceException {
        try {
            return getLoanPrdPersistence().getAllLoanOfferings(localeId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    protected LoanPrdPersistence getLoanPrdPersistence() {
        return new LoanPrdPersistence();
    }
}
