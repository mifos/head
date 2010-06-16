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

package org.mifos.application.master.business.service;

import java.util.List;

import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.ValueListElement;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.customers.business.CustomerDto;
import org.mifos.customers.office.business.OfficeDetailsDto;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelDto;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.framework.business.AbstractBusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.security.util.UserContext;

public class MasterDataService implements BusinessService {

    private final PersonnelPersistence personnelPersistence = new PersonnelPersistence();
    private final OfficePersistence officePersistence = new OfficePersistence();
    private final CustomerPersistence customerPersistence = new CustomerPersistence();
    private final MasterPersistence masterPersistence = new MasterPersistence();

    @Override
    public AbstractBusinessObject getBusinessObject(UserContext userContext) {
        return null;
    }

    public List<PersonnelDto> getListOfActiveLoanOfficers(Short levelId, Short officeId, Short userId,
            Short userLevelId) throws ServiceException {
        try {
            return personnelPersistence.getActiveLoanOfficersInBranch(levelId, officeId, userId, userLevelId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<OfficeDetailsDto> getActiveBranches(Short branchId) throws ServiceException {
        try {
            return officePersistence.getActiveOffices(branchId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }

    }

    public List<CustomerDto> getListOfActiveParentsUnderLoanOfficer(Short personnelId, Short customerLevel,
            Short officeId) throws ServiceException {
        try {
            return customerPersistence.getActiveParentList(personnelId, customerLevel, officeId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }

    }

    /**
     * @deprecated - use {@link CustomerDao#retrieveCustomFieldsForCenter(UserContext)}
     */
    @Deprecated
    public List<CustomFieldDefinitionEntity> retrieveCustomFieldsDefinition(EntityType entityType)
            throws ServiceException {
        try {
            return new MasterPersistence().retrieveCustomFieldsDefinition(entityType);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public String retrieveMasterEntities(Integer entityId, Short localeId) throws ServiceException {
        try {
            return new MasterPersistence().retrieveMasterEntities(entityId, localeId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<ValueListElement> retrieveMasterEntities(String entityName, Short localeId) throws ServiceException {
        try {
            return new MasterPersistence().retrieveMasterEntities(entityName, localeId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public MasterDataEntity getMasterDataEntity(Class clazz, Short id) throws ServiceException {
        try {
            return new MasterPersistence().getMasterDataEntity(clazz, id);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public <T extends MasterDataEntity>  T retrieveMasterEntity(Class<T> entityType, Short entityId, Short localeId)
      throws PersistenceException {
        return new MasterPersistence().retrieveMasterEntity(entityType, entityId, localeId);
    }

    public <T extends MasterDataEntity>  List<T> retrieveMasterEntities(Class<T> entityType, Short localeId) throws ServiceException {
        try {
          return masterPersistence.retrieveMasterEntities(entityType, localeId);
      } catch (PersistenceException e) {
          throw new ServiceException(e);
      }
    }
}
