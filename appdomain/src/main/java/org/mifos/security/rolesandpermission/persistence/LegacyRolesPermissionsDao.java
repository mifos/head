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

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.LookUpEntity;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.application.master.persistence.LegacyMasterDao;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.DatabaseMigrator;
import org.mifos.framework.persistence.LegacyGenericDao;
import org.mifos.framework.util.helpers.SearchUtils;
import org.mifos.security.activity.ActivityGeneratorException;
import org.mifos.security.activity.DynamicLookUpValueCreationTypes;
import org.mifos.security.rolesandpermission.business.ActivityEntity;
import org.mifos.security.rolesandpermission.business.RoleActivityEntity;
import org.mifos.security.rolesandpermission.business.RoleBO;
import org.mifos.security.rolesandpermission.business.service.RolesPermissionsBusinessService;
import org.mifos.security.rolesandpermission.util.helpers.RolesAndPermissionConstants;

public class LegacyRolesPermissionsDao extends LegacyGenericDao {

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
            return (ActivityEntity) execUniqueResultNamedQuery(NamedQueryConstants.GET_ACTIVITY_BY_ID, queryParameters);
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
        return getPersistentObject(RoleBO.class, roleId);
    }

    public ActivityEntity retrieveOneActivityEntity(int lookUpId) throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        LegacyMasterDao mp = ApplicationContextProvider.getBean(LegacyMasterDao.class);
        LookUpValueEntity aLookUpValueEntity = mp.getPersistentObject(LookUpValueEntity.class, lookUpId);
        queryParameters.put("aLookUpValueEntity", aLookUpValueEntity);
        Object obj = execUniqueResultNamedQuery(NamedQueryConstants.GETACTIVITYENTITY, queryParameters);
        if (null != obj) {
            return (ActivityEntity) obj;
        }
        return null;
    }

    public int createActivity(DynamicLookUpValueCreationTypes type, short parentActivity, String lookUpDescription)
            throws HibernateException, PersistenceException, ServiceException, ActivityGeneratorException {
        StaticHibernateUtil.startTransaction();
        int lookUpId = createLookUpValue(type, lookUpDescription);
        insertLookUpValueLocale(lookUpId, lookUpDescription);
        ActivityEntity activityEntity = createActivityEntity(parentActivity, lookUpId);
        insertRolesActivity(activityEntity);
        StaticHibernateUtil.commitTransaction();
        return lookUpId;
    }

    private void insertRolesActivity(ActivityEntity activityEntity) throws PersistenceException {
        LegacyRolesPermissionsDao rpp = new LegacyRolesPermissionsDao();
        RoleBO role = rpp.getPersistentObject(RoleBO.class, (short) RolesAndPermissionConstants.ADMIN_ROLE);
        RoleActivityEntity roleActivityEntity = new RoleActivityEntity(role, activityEntity);
        rpp.createOrUpdate(roleActivityEntity);
    }

    private ActivityEntity createActivityEntity(short parentActivity, int lookUpId) throws ServiceException,
            ActivityGeneratorException, PersistenceException {
        ActivityEntity parentActivityEntity;
        LegacyRolesPermissionsDao rpp = new LegacyRolesPermissionsDao();
        if (parentActivity != 0) {
            parentActivityEntity = rpp.getPersistentObject(ActivityEntity.class, parentActivity);
        } else {
            parentActivityEntity = null;
        }
        LookUpValueEntity lookupValueEntity = rpp.getPersistentObject(LookUpValueEntity.class, lookUpId);
        ActivityEntity activityEntity = new ActivityEntity((short) calculateDynamicActivityId(), parentActivityEntity,
                lookupValueEntity);
        rpp.createOrUpdate(activityEntity);

        return activityEntity;
    }

    private void insertLookUpValueLocale(int lookUpId, String lookUpDescription) throws PersistenceException {
        LegacyMasterDao mp = ApplicationContextProvider.getBean(LegacyMasterDao.class);
        LookUpValueLocaleEntity lookUpValueLocaleEntity = new LookUpValueLocaleEntity();
        lookUpValueLocaleEntity.setLookUpId(new Integer(lookUpId));
        lookUpValueLocaleEntity.setLocaleId(DatabaseMigrator.ENGLISH_LOCALE);
        lookUpValueLocaleEntity.setLookUpValue(lookUpDescription);
        mp.createOrUpdate(lookUpValueLocaleEntity);

    }

    private int createLookUpValue(DynamicLookUpValueCreationTypes type, String lookUpDescription)
            throws PersistenceException {

        LookUpValueEntity anLookUp = new LookUpValueEntity();
        LegacyMasterDao mp = ApplicationContextProvider.getBean(LegacyMasterDao.class);
        LookUpEntity lookUpEntity = mp.getPersistentObject(LookUpEntity.class,
                Short.valueOf((short) LookUpEntity.ACTIVITY));
        String lookupName = SearchUtils.generateLookupName(type.name(), lookUpDescription);
        anLookUp.setLookUpName(lookupName);
        anLookUp.setLookUpEntity(lookUpEntity);
        mp.createOrUpdate(anLookUp);
        MessageLookup.getInstance().updateLookupValueInCache(lookupName, lookUpDescription);
        int lookUpId = anLookUp.getLookUpId().intValue();
        return lookUpId;
    }

    public int calculateDynamicActivityId() throws ServiceException, ActivityGeneratorException {
        int activityId = 0;
        for (ActivityEntity activity : new RolesPermissionsBusinessService().getActivities()) {
            if (activity.getId().intValue() < activityId) {
                activityId = activity.getId();
            }
        }
        if (activityId <= Short.MIN_VALUE) {
            throw new ActivityGeneratorException();
        }
        int newActivityId = activityId - 1;

        return newActivityId;
    }

    public LookUpValueLocaleEntity getLookUpValueLocaleEntity(short localId, int lookUpId) throws PersistenceException {
        LegacyMasterDao mp = ApplicationContextProvider.getBean(LegacyMasterDao.class);
        return mp.retrieveOneLookUpValueLocaleEntity(localId, lookUpId);
    }

    public ActivityEntity getActivityEntity(int lookUpId) throws PersistenceException {
        LegacyRolesPermissionsDao rpp = new LegacyRolesPermissionsDao();
        return rpp.retrieveOneActivityEntity(lookUpId);
    }



    public void reparentActivityUsingHibernate(short activityId, Short newParent) throws PersistenceException {
        LegacyRolesPermissionsDao rpp = new LegacyRolesPermissionsDao();
        ActivityEntity parent = rpp.getPersistentObject(ActivityEntity.class, newParent);
        ActivityEntity activity = rpp.getPersistentObject(ActivityEntity.class, activityId);
        activity.setParent(parent);
        rpp.createOrUpdate(activity);
    }

    public void changeActivityMessage(short activityId, short localeId, String newMessage)
            throws PersistenceException {
        LegacyRolesPermissionsDao rpp = new LegacyRolesPermissionsDao();
        LegacyMasterDao mp = ApplicationContextProvider.getBean(LegacyMasterDao.class);
        ActivityEntity activityEntity = rpp.getPersistentObject(ActivityEntity.class, Short
                .valueOf(activityId));
        Integer lookUpId = activityEntity.getActivityNameLookupValues().getLookUpId();
        LookUpValueLocaleEntity lookUpValueLocaleEntity = mp.retrieveOneLookUpValueLocaleEntity(localeId, lookUpId);
        lookUpValueLocaleEntity.setLookUpValue(newMessage);
        mp.createOrUpdate(lookUpValueLocaleEntity);
    }

    public void save(RoleBO roleBO) {
        try {
            super.save(roleBO);
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }
}