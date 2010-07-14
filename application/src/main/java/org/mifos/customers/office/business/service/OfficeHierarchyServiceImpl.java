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

package org.mifos.customers.office.business.service;

import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.office.business.OfficeLevelEntity;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.office.util.helpers.OfficeLevel;
import org.mifos.dto.domain.OfficeLevelDto;
import org.mifos.dto.domain.UpdateConfiguredOfficeLevelRequest;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelperForStaticHibernateUtil;

public class OfficeHierarchyServiceImpl implements OfficeHierarchyService {

    private final OfficeDao officeDao;
    private HibernateTransactionHelper transactionHelper = new HibernateTransactionHelperForStaticHibernateUtil();

    public OfficeHierarchyServiceImpl(OfficeDao officeDao) {
        this.officeDao = officeDao;
    }

    @Override
    public void updateOfficeHierarchyConfiguration(UpdateConfiguredOfficeLevelRequest updateRequest) {

        try {
            transactionHelper.startTransaction();

            OfficeLevelDto existingOfficeLevels = officeDao.findOfficeLevelsWithConfiguration();

            if (updateRequest.isRegionalOfficeEnabled() != existingOfficeLevels.isRegionalOfficeEnabled()) {
                OfficeLevelEntity entity = officeDao.retrieveOfficeLevel(OfficeLevel.REGIONALOFFICE);
                entity.updateTo(updateRequest.isRegionalOfficeEnabled());
                if (existingOfficeLevels.isRegionalOfficeEnabled()) {
                    officeDao.validateNoOfficesExistGivenOfficeLevel(OfficeLevel.REGIONALOFFICE);
                }
                this.officeDao.save(entity);
                transactionHelper.flushSession();
            }

            if (updateRequest.isSubRegionalOfficeEnabled() != existingOfficeLevels.isSubRegionalOfficeEnabled()) {
                OfficeLevelEntity entity = officeDao.retrieveOfficeLevel(OfficeLevel.SUBREGIONALOFFICE);
                entity.updateTo(updateRequest.isSubRegionalOfficeEnabled());
                if (existingOfficeLevels.isSubRegionalOfficeEnabled()) {
                    officeDao.validateNoOfficesExistGivenOfficeLevel(OfficeLevel.SUBREGIONALOFFICE);
                }

                this.officeDao.save(entity);
                transactionHelper.flushSession();
            }

            if (updateRequest.isAreaOfficeEnabled() != existingOfficeLevels.isAreaOfficeEnabled()) {
                OfficeLevelEntity entity = officeDao.retrieveOfficeLevel(OfficeLevel.AREAOFFICE);
                entity.updateTo(updateRequest.isAreaOfficeEnabled());
                if (existingOfficeLevels.isSubRegionalOfficeEnabled()) {
                    officeDao.validateNoOfficesExistGivenOfficeLevel(OfficeLevel.AREAOFFICE);
                }

                this.officeDao.save(entity);
                transactionHelper.flushSession();
            }

            transactionHelper.commitTransaction();
        } catch (Exception e) {
            transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            transactionHelper.closeSession();
        }
    }
}