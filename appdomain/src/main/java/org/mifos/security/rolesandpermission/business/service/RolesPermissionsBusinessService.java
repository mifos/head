/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.security.rolesandpermission.business.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.framework.business.AbstractBusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.security.rolesandpermission.business.ActivityEntity;
import org.mifos.security.rolesandpermission.business.ActivityRestrictionTypeEntity;
import org.mifos.security.rolesandpermission.business.RoleActivityRestrictionBO;
import org.mifos.security.rolesandpermission.business.RoleBO;
import org.mifos.security.rolesandpermission.persistence.LegacyRolesPermissionsDao;
import org.mifos.security.util.UserContext;
import org.mifos.security.util.helpers.ActivityRestrictionType;

public class RolesPermissionsBusinessService implements BusinessService {

    private LegacyRolesPermissionsDao rolesPermissionsPersistence = getRolesPermissionsPersistence();

    protected LegacyRolesPermissionsDao getRolesPermissionsPersistence() {
        return ApplicationContextProvider.getBean(LegacyRolesPermissionsDao.class);
    }

    @Override
    public AbstractBusinessObject getBusinessObject(UserContext userContext) {
        return null;
    }

    public List<RoleBO> getRoles() throws ServiceException {
        try {
            return rolesPermissionsPersistence.getRoles();
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<ActivityEntity> getActivities() throws ServiceException {
        try {
            return rolesPermissionsPersistence.getActivities();
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public RoleBO getRole(Short roleId) throws ServiceException {
        try {
            return rolesPermissionsPersistence.getRole(roleId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }
    
    public BigDecimal getRoleActivityRestrictionAmountValueByRestrictionTypeId(Short roleId,
            Short activityRestrictionTypeId) throws ServiceException {        
        try {
            RoleBO roleBO = rolesPermissionsPersistence.getRole(roleId);
            Set<RoleActivityRestrictionBO> restrictions = roleBO.getRestrictions();
            for (RoleActivityRestrictionBO restrictionBO : restrictions){
                if ( restrictionBO.getActivityRestrictionType().getId().equals(ActivityRestrictionType.MAX_LOAN_AMOUNT_FOR_APPROVE.getValue())){
                    return restrictionBO.getRestrictionAmountValue();
                }
            }
            return null;
        } catch (PersistenceException e){
            throw new ServiceException(e);
        }
    }
    
    public List<ActivityRestrictionTypeEntity> getActivityRestrictionTypes() throws ServiceException {
        try {
            return rolesPermissionsPersistence.getActivitiesRestrictionTypes();
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

}
