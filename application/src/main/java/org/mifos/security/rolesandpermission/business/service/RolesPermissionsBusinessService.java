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

package org.mifos.security.rolesandpermission.business.service;

import java.util.List;

import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.security.rolesandpermission.business.ActivityEntity;
import org.mifos.security.rolesandpermission.business.RoleBO;
import org.mifos.security.rolesandpermission.persistence.RolesPermissionsPersistence;
import org.mifos.security.util.UserContext;

public class RolesPermissionsBusinessService implements BusinessService {

    private RolesPermissionsPersistence rolesPermissionsPersistence = new RolesPermissionsPersistence();

    @Override
    public BusinessObject getBusinessObject(UserContext userContext) {
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

}
