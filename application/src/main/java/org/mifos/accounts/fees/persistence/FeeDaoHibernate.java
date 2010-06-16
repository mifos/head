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

package org.mifos.accounts.fees.persistence;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.mifos.accounts.fees.business.CategoryTypeEntity;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeeFormulaEntity;
import org.mifos.accounts.fees.business.FeeFrequencyTypeEntity;
import org.mifos.accounts.fees.business.FeePaymentEntity;
import org.mifos.accounts.fees.persistence.FeeDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.accounts.fees.servicefacade.FeeDto;
import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.accounts.savings.persistence.GenericDaoHibernate;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.master.business.MasterDataEntity;

public class FeeDaoHibernate implements FeeDao {

    private final GenericDao genericDao;

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
        FeeBO fee = findById(feeId);
        return fee.toDto();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<FeeDto> retrieveAllProductFees() {
        List<FeeBO> allProductFees = (List<FeeBO>) this.genericDao.executeNamedQuery(NamedQueryConstants.RETRIEVE_PRODUCT_FEES, null);

        return assembleFeeDto(allProductFees);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<FeeDto> retrieveAllCustomerFees() {
        List<FeeBO> allCustomerFees = (List<FeeBO>) this.genericDao.executeNamedQuery(NamedQueryConstants.RETRIEVE_CUSTOMER_FEES, null);

        return assembleFeeDto(allCustomerFees);
    }

    private List<FeeDto> assembleFeeDto(List<FeeBO> allProductFees) {
        List<FeeDto> fees = new ArrayList<FeeDto>();
        for (FeeBO fee : allProductFees) {
            fees.add(fee.toDto());
        }
        return fees;
    }

    @Override
    public List<CategoryTypeEntity> retrieveFeeCategories() {
        return retreiveMasterDataEntityTypes(CategoryTypeEntity.class);
    }

    @Override
    public List<FeeFormulaEntity> retrieveFeeFormulae() {
        return retreiveMasterDataEntityTypes(FeeFormulaEntity.class);
    }

    @Override
    public List<FeeFrequencyTypeEntity> retrieveFeeFrequencies() {
        return retreiveMasterDataEntityTypes(FeeFrequencyTypeEntity.class);
    }

    @Override
    public List<FeePaymentEntity> retrieveFeePayments() {
        return retreiveMasterDataEntityTypes(FeePaymentEntity.class);
    }

    @SuppressWarnings("unchecked")
    private <T extends MasterDataEntity> List<T> retreiveMasterDataEntityTypes(Class<T> type) {
        Session session = ((GenericDaoHibernate) this.genericDao).getHibernateUtil().getSessionTL();
        List<T> masterEntities = session.createQuery("from " + type.getName()).list();
        for (MasterDataEntity masterData : masterEntities) {
            Hibernate.initialize(masterData.getNames());
        }
        return masterEntities;
    }
}