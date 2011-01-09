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

package org.mifos.accounts.savings.business.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.Hibernate;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.persistence.SavingsPersistence;
import org.mifos.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.util.helpers.AccountExceptionConstants;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.framework.business.AbstractBusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.security.util.UserContext;

public class SavingsBusinessService implements BusinessService {
    private SavingsPersistence savingsPersistence = getSavingsPersistence();


    protected SavingsPersistence getSavingsPersistence() {
        return new SavingsPersistence();
    }

    private static final Logger logger = LoggerFactory.getLogger(SavingsBusinessService.class);

    @Override
    public AbstractBusinessObject getBusinessObject(@SuppressWarnings("unused")UserContext userContext) {
        return null;
    }

    public List<CustomFieldDefinitionEntity> retrieveCustomFieldsDefinition() throws ServiceException {
        logger.debug("In SavingsBusinessService::retrieveCustomFieldsDefinition()");
        try {
            List<CustomFieldDefinitionEntity> customFields = savingsPersistence
                    .retrieveCustomFieldsDefinition(SavingsConstants.SAVINGS_CUSTOM_FIELD_ENTITY_TYPE);
            Hibernate.initialize(customFields);
            return customFields;
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<SavingsBO> getAllClosedAccounts(Integer customerId) throws ServiceException {
        try {
            return savingsPersistence.getAllClosedAccount(customerId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public void persistSavings(SavingsBO savingsBO) throws AccountException {
        try {
            savingsBO.save();
            StaticHibernateUtil.commitTransaction();

        } catch (AccountException e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new AccountException(AccountExceptionConstants.CREATEEXCEPTION, e);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }
}
