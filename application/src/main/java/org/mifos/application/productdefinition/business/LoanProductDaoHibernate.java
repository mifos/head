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
package org.mifos.application.productdefinition.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.savings.persistence.GenericDao;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.customer.business.CustomerLevelEntity;
import org.mifos.application.master.business.ValueListElement;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.productdefinition.persistence.LoanProductDao;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;

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

}
