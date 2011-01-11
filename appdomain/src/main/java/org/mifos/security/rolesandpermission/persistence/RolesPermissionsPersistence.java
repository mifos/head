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

package org.mifos.security.rolesandpermission.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.Persistence;
import org.mifos.security.rolesandpermission.business.ActivityEntity;
import org.mifos.security.rolesandpermission.business.RoleBO;

public class RolesPermissionsPersistence extends Persistence {

    public RoleBO getRole(String roleName) throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("RoleName", roleName);
        return (RoleBO) execUniqueResultNamedQuery(NamedQueryConstants.GET_ROLE_FOR_GIVEN_NAME, queryParameters);
    }

    public List<ActivityEntity> getActivities() throws PersistenceException {
        try {
            return getActivities(StaticHibernateUtil.getSessionTL());
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    public ActivityEntity getActivityById(Short id) throws PersistenceException {
        try {
            Map<String, Object> queryParameters = new HashMap<String, Object>();
            queryParameters.put("ACTIVITY_ID", id);
            return (ActivityEntity)execUniqueResultNamedQuery(NamedQueryConstants.GET_ACTIVITY_BY_ID, queryParameters);
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<ActivityEntity> getActivities(Session session) {
        Query query = session.getNamedQuery(NamedQueryConstants.GET_ALL_ACTIVITIES);
        return query.list();
    }

    @SuppressWarnings({ "cast", "unchecked" })
    public List<RoleBO> getRoles() throws PersistenceException {
        return (List<RoleBO>) executeNamedQuery(NamedQueryConstants.GET_ALL_ROLES, null);
    }

    public RoleBO getRole(Short roleId) throws PersistenceException {
        return (RoleBO) getPersistentObject(RoleBO.class, roleId);
    }

    public ActivityEntity retrieveOneActivityEntity(int lookUpId) throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        MasterPersistence mp = new MasterPersistence();
        LookUpValueEntity aLookUpValueEntity = (LookUpValueEntity) mp.getPersistentObject(LookUpValueEntity.class,
                lookUpId);
        queryParameters.put("aLookUpValueEntity", aLookUpValueEntity);
        Object obj = execUniqueResultNamedQuery(NamedQueryConstants.GETACTIVITYENTITY, queryParameters);
        if (null != obj) {
            return (ActivityEntity) obj;
        }
        return null;
    }

    public void save(RoleBO roleBO) {
        try {
            super.save(roleBO);
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }
}