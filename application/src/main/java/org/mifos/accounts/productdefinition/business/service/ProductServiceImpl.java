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

import org.mifos.accounts.productdefinition.business.service.ProductService;

import org.mifos.accounts.productdefinition.business.ProductTypeEntity;
import org.mifos.accounts.productdefinition.persistence.LoanProductDao;
import org.mifos.accounts.productdefinition.persistence.SavingsProductDao;
import org.mifos.core.MifosRuntimeException;
import org.mifos.dto.screen.ProductConfigurationDto;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelperForStaticHibernateUtil;

public class ProductServiceImpl implements ProductService {

    private final LoanProductDao loanProductDao;
    private final SavingsProductDao savingsProductDao;
    private HibernateTransactionHelper transactionHelper = new HibernateTransactionHelperForStaticHibernateUtil();

    public ProductServiceImpl(LoanProductDao loanProductDao, SavingsProductDao savingsProductDao) {
        this.loanProductDao = loanProductDao;
        this.savingsProductDao = savingsProductDao;
    }

    @Override
    public void updateLatenessAndDormancy(ProductTypeEntity loanProductConfiguration, ProductTypeEntity savingsProductConfiguration, ProductConfigurationDto productConfiguration) {

        loanProductConfiguration.setLatenessDays(productConfiguration.getLatenessDays().shortValue());
        savingsProductConfiguration.setDormancyDays(productConfiguration.getDormancyDays().shortValue());

        try {
            transactionHelper.startTransaction();
            this.loanProductDao.save(loanProductConfiguration);
            this.savingsProductDao.save(savingsProductConfiguration);
            transactionHelper.commitTransaction();
        } catch (Exception e) {
            transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            transactionHelper.closeSession();
        }
    }
}