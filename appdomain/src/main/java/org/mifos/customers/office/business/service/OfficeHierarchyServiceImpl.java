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

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mifos.accounts.productdefinition.business.GracePeriodTypeEntity;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.LookUpEntity;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.config.business.MifosConfiguration;
import org.mifos.config.persistence.ApplicationConfigurationDao;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.office.business.OfficeLevelEntity;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.office.util.helpers.OfficeLevel;
import org.mifos.dto.domain.OfficeLevelDto;
import org.mifos.dto.domain.UpdateConfiguredOfficeLevelRequest;
import org.mifos.framework.components.mifosmenu.MenuRepository;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelperForStaticHibernateUtil;
import org.mifos.service.BusinessRuleException;

public class OfficeHierarchyServiceImpl implements OfficeHierarchyService {

    private final OfficeDao officeDao;
    private final ApplicationConfigurationDao applicationConfigurationDao;
    private HibernateTransactionHelper transactionHelper = new HibernateTransactionHelperForStaticHibernateUtil();

    public OfficeHierarchyServiceImpl(OfficeDao officeDao, ApplicationConfigurationDao applicationConfigurationDao) {
        this.officeDao = officeDao;
        this.applicationConfigurationDao = applicationConfigurationDao;
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
        } catch (BusinessRuleException e) {
            transactionHelper.rollbackTransaction();
            throw e;
        } catch (Exception e) {
            transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            transactionHelper.closeSession();
        }
    }

    @Override
    public void updateApplicationLabels(List<OfficeLevelEntity> changedOfficeLabels, List<LookUpEntity> lookupEntities, List<GracePeriodTypeEntity> gracePeriods, List<LookUpValueEntity> accountStatuses) {

        try {
            transactionHelper.startTransaction();

            for (OfficeLevelEntity entity : changedOfficeLabels) {
                officeDao.save(entity);
                LookUpValueEntity lookupValue = entity.getLookUpValue();
                String messageText = lookupValue.getMessageText();
                if (StringUtils.isBlank(messageText)) {
                    messageText = MessageLookup.getInstance().lookup(lookupValue.getPropertiesKey());
                }
                MessageLookup.getInstance().updateLookupValueInCache(entity.getLookUpValue().getLookUpName(), messageText);
            }

            for (GracePeriodTypeEntity entity : gracePeriods) {
                applicationConfigurationDao.save(entity);
                LookUpValueEntity lookupValue = entity.getLookUpValue();
                String messageText = lookupValue.getMessageText();
                if (StringUtils.isBlank(messageText)) {
                    messageText = MessageLookup.getInstance().lookup(lookupValue.getPropertiesKey());
                }
                MessageLookup.getInstance().updateLookupValueInCache(entity.getLookUpValue().getLookUpName(), messageText);
            }

            for (LookUpEntity entity : lookupEntities) {
                applicationConfigurationDao.save(entity);
                MifosConfiguration.getInstance().updateKey(entity.getEntityType(), entity.findLabel());
            }

            for (LookUpValueEntity entity : accountStatuses) {
                applicationConfigurationDao.save(entity);
            }

            transactionHelper.commitTransaction();

            if (!accountStatuses.isEmpty()) {
                MenuRepository.getInstance().removeMenuForAllLocale();
                MifosConfiguration.getInstance().initializeLabelCache();
            }

        } catch (BusinessRuleException e) {
            transactionHelper.rollbackTransaction();
            throw e;
        } catch (Exception e) {
            transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            transactionHelper.closeSession();
        }
    }
}