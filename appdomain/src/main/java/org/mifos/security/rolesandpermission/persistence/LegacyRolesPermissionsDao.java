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

package org.mifos.security.rolesandpermission.persistence;

import static org.mifos.security.authorization.HierarchyManager.BranchLocation.BELOW;
import static org.mifos.security.authorization.HierarchyManager.BranchLocation.SAME;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.mifos.config.Localization;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.personnel.util.helpers.PersonnelLevel;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SecurityException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.LegacyGenericDao;
import org.mifos.framework.util.helpers.SearchUtils;
import org.mifos.security.activity.ActivityGeneratorException;
import org.mifos.security.activity.DynamicLookUpValueCreationTypes;
import org.mifos.security.authorization.HierarchyManager;
import org.mifos.security.rolesandpermission.business.ActivityEntity;
import org.mifos.security.rolesandpermission.business.RoleBO;
import org.mifos.security.rolesandpermission.business.service.RolesPermissionsBusinessService;
import org.mifos.security.rolesandpermission.util.helpers.RolesAndPermissionConstants;
import org.mifos.security.util.ActivityContext;
import org.mifos.security.util.PersonRoles;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;

public class LegacyRolesPermissionsDao extends LegacyGenericDao {

    @Autowired
    LegacyMasterDao legacyMasterDao;

    private LegacyRolesPermissionsDao() {
    }

    public RoleBO getRole(String roleName) throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("RoleName", roleName);
        return (RoleBO) execUniqueResultNamedQuery(NamedQueryConstants.GET_ROLE_FOR_GIVEN_NAME, queryParameters);
    }

    /**
     * This function returns the PersonRoles object which contains the person
     * information and the set of all the roles related to that user
     *
     * @param uid
     *            user id
     * @return PersonRoles
     * @throws HibernateProcessException
     */
    public Set getUserRoles(short uid) throws SystemException, ApplicationException {
        Set roles = null;
        try {
            Session session = StaticHibernateUtil.getSessionTL();
            Query personRoles = session.getNamedQuery(NamedQueryConstants.GETPERSONROLES);
            personRoles.setShort("ID", uid);
            List<PersonRoles> lst = personRoles.list();
            if (null != lst && lst.size() > 0) {
                PersonRoles pr = lst.get(0);
                roles = pr.getRoles();
            }
        } catch (HibernateException he) {
            throw new SecurityException(SecurityConstants.GENERALERROR, he);
        }
        return roles;
    }

    /**
     * This function is used to find the leaf activities in the system as those
     * are the actual activities user can perform in the system rest are used
     * for grouping activities only
     *
     * @return List of leafs activity id's
     * @throws HibernateProcessException
     */
    public List<Short> getLeafActivities() throws SystemException, ApplicationException {
        List<ActivityEntity> activityList = ApplicationContextProvider.getBean(LegacyRolesPermissionsDao.class).getActivities();
        List<Short> leafs = new ArrayList<Short>();
        buildLeafItems(activityList, leafs);
        return leafs;
    }
    /**
     * Internal helper function used to find the leaf activities this function
     * is once each for the top level activity which has parent as 0
     *
     * @param l
     *            list of activities in the system
     * @param leafs
     *            list of leafs id's
     */
    private static void buildLeafItems(List<ActivityEntity> l, List<Short> leafs) {
        List<ActivityEntity> li = getChildren(l, Short.valueOf("0"));

        for (int i = 0; i < li.size(); i++) {
            makeLeafItems(l, li.get(i).getId(), leafs);
        }
    }

    /**
     * This is internal helper function used to find the childern of the given
     * activity it takes the list of all the activity in the system and find out
     * which are childern of passed activity
     *
     * @param activities
     *            List of Activity objects in the system
     * @param id
     *            id of the activity whose childern we are trying to find out
     *            right now
     * @return
     */
    private static List<ActivityEntity> getChildren(List<ActivityEntity> activities, Short id) {
        List<ActivityEntity> l = new ArrayList<ActivityEntity>();

        /*
         * for (int i = 0; i < activities.size(); i++) { if
         * (activities.get(i).getParent().shortValue() == id.shortValue()) {
         * l.add(activities.get(i)); } }
         */
        // if id=0 then we are looking for top level activities
        for (int i = 0; i < activities.size(); i++) {
            ActivityEntity parent = activities.get(i).getParent();
            if (id.shortValue() == 0) {

                if (null == parent) {
                    l.add(activities.get(i));
                }

            } else {

                if (null != parent) {
                    if (parent.getId().shortValue() == id.shortValue()) {
                        l.add(activities.get(i));
                    }
                }

            }
        }
        return l;
    }

    public boolean isActivityAllowed(UserContext userContext, ActivityContext activityContext) {
        try {
            ActivityEntity activity = getActivityById(activityContext.getActivityId());
            if (activity == null) {
                return false;
            }

            Set<Short> activityAllowedRoles = activity.getRoleIds();
            if (activityAllowedRoles == null) {
                return false;
            }

            Set<Short> userRoles = userContext.getRoles();

            activityAllowedRoles.retainAll(userRoles);

            if (activityAllowedRoles.isEmpty()) {
                return false;
            }

            HierarchyManager.BranchLocation where = HierarchyManager.getInstance().compareOfficeInHierarchy(userContext, activityContext.getRecordOfficeId());
            return checkAccessByHierarchy(activityContext.getRecordLoanOfficer(), where, userContext.getLevel(), userContext.getId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isAccessAllowed(UserContext userContext, Short recordOfficeId, Short recordLoanOfficerId) {
        HierarchyManager.BranchLocation where = HierarchyManager.getInstance().compareOfficeInHierarchy(userContext, recordOfficeId);
        return checkAccessByHierarchy(recordLoanOfficerId, where, userContext.getLevel(), userContext.getId());
    }

    private boolean checkAccessByHierarchy(short recordLoanOfficer, HierarchyManager.BranchLocation where, PersonnelLevel personnelLevel, short userId) {
        if (where == SAME) {
            // 1 check if record belongs to him if so let him do
            if (userId == recordLoanOfficer) {
                return true;
            } else if (PersonnelLevel.LOAN_OFFICER == personnelLevel) {
                return false;
            }

            return true;

        } else if (where == BELOW && PersonnelLevel.LOAN_OFFICER != personnelLevel) {
            return true;
        }

        return false;
    }

    /**
     * This function is called recursively for each top level activities in the
     * system till we reach the leafs this is the function where we find the
     * leafs
     *
     * @param l
     *            List of Activity objects in the system
     * @param id
     *            id of current activity
     * @param leafs
     *            list of all the leafs activity in the system this ia a out
     *            parameter
     */
    private static void makeLeafItems(List<ActivityEntity> l, Short id, List<Short> leafs) {
        List<ActivityEntity> lst = getChildren(l, id);
        for (int i = 0; i < lst.size(); i++) {
            Short id2 = lst.get(i).getId();
            // check whether it is leaf
            List<ActivityEntity> li = getChildren(l, id2);
            if (li.size() == 0) {
                leafs.add(id2);
            } else {
                makeLeafItems(l, id2, leafs);
            }
        }
    }

    public List<ActivityEntity> getActivities() throws PersistenceException {
        try {
            Query query = getSession().getNamedQuery(NamedQueryConstants.GET_ALL_ACTIVITIES);
            return query.list();
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    public List<Short> getActivitieIds() throws PersistenceException {
        try {
            Query query = getSession().getNamedQuery(NamedQueryConstants.GET_ALL_ACTIVITIES);
            List<ActivityEntity> activities = query.list();
            List<Short> activityIds = new ArrayList();
            for(ActivityEntity activity: activities) {
                activityIds.add(activity.getId());
            }
            return activityIds;
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

    @SuppressWarnings({ "cast", "unchecked" })
    public List<RoleBO> getRoles() throws PersistenceException {
        return (List<RoleBO>) executeNamedQuery(NamedQueryConstants.GET_ALL_ROLES, null);
    }

    public RoleBO getRole(Short roleId) throws PersistenceException {
        return getPersistentObject(RoleBO.class, roleId);
    }

    public ActivityEntity retrieveOneActivityEntity(int lookUpId) throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        LookUpValueEntity aLookUpValueEntity = getPersistentObject(LookUpValueEntity.class, lookUpId);
        queryParameters.put("aLookUpValueEntity", aLookUpValueEntity);
        Object obj = execUniqueResultNamedQuery(NamedQueryConstants.GETACTIVITYENTITY, queryParameters);
        if (null != obj) {
            return (ActivityEntity) obj;
        }
        return null;
    }

    public int createActivityForReports(short parentActivity, String lookUpDescription)
            throws HibernateException, PersistenceException, ServiceException, ActivityGeneratorException {
        StaticHibernateUtil.startTransaction();
        int lookUpId = createLookUpValue(DynamicLookUpValueCreationTypes.BirtReport, lookUpDescription);
        insertLookUpValueLocale(lookUpId, lookUpDescription);
        ActivityEntity activityEntity = createActivityEntity(parentActivity, lookUpId);
        RoleBO role = getPersistentObject(RoleBO.class, RolesAndPermissionConstants.ADMIN_ROLE);
        role.getActivities().add(activityEntity);
        StaticHibernateUtil.commitTransaction();
        return lookUpId;
    }

    private ActivityEntity createActivityEntity(short parentActivity, int lookUpId) throws ServiceException,
            ActivityGeneratorException, PersistenceException {
        ActivityEntity parentActivityEntity;
        if (parentActivity != 0) {
            parentActivityEntity = getPersistentObject(ActivityEntity.class, parentActivity);
        } else {
            parentActivityEntity = null;
        }
        LookUpValueEntity lookupValueEntity = getPersistentObject(LookUpValueEntity.class, lookUpId);
        ActivityEntity activityEntity = new ActivityEntity((short) calculateDynamicActivityId(), parentActivityEntity,
                lookupValueEntity);
        createOrUpdate(activityEntity);
        return activityEntity;
    }

    private void insertLookUpValueLocale(int lookUpId, String lookUpDescription) throws PersistenceException {
        LookUpValueLocaleEntity lookUpValueLocaleEntity = new LookUpValueLocaleEntity();
        lookUpValueLocaleEntity.setLookUpId(new Integer(lookUpId));
        lookUpValueLocaleEntity.setLocaleId(Localization.ENGLISH_LOCALE_ID);
        lookUpValueLocaleEntity.setLookUpValue(lookUpDescription);
        createOrUpdate(lookUpValueLocaleEntity);

    }

    private int createLookUpValue(DynamicLookUpValueCreationTypes type, String lookUpDescription)
            throws PersistenceException {
        LookUpValueEntity anLookUp = new LookUpValueEntity();
        LookUpEntity lookUpEntity = getPersistentObject(LookUpEntity.class,
                Short.valueOf((short) LookUpEntity.ACTIVITY));
        String lookupName = SearchUtils.generateLookupName(type.name(), lookUpDescription);
        anLookUp.setLookUpName(lookupName);
        anLookUp.setLookUpEntity(lookUpEntity);
        createOrUpdate(anLookUp);
        ApplicationContextProvider.getBean(MessageLookup.class).updateLookupValueInCache(lookupName, lookUpDescription);
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

    public ActivityEntity getActivityEntity(int lookUpId) throws PersistenceException {
        LegacyRolesPermissionsDao rpp = new LegacyRolesPermissionsDao();
        return rpp.retrieveOneActivityEntity(lookUpId);
    }



    public void reparentActivityUsingHibernate(short activityId, Short newParent) throws PersistenceException {
        ActivityEntity parent = getPersistentObject(ActivityEntity.class, newParent);
        ActivityEntity activity = getPersistentObject(ActivityEntity.class, activityId);
        activity.setParent(parent);
        createOrUpdate(activity);
    }

    public void changeActivityMessage(short activityId, short localeId, String newMessage)
            throws PersistenceException {
        ActivityEntity activityEntity = getPersistentObject(ActivityEntity.class, Short
                .valueOf(activityId));
        Integer lookUpId = activityEntity.getActivityNameLookupValues().getLookUpId();
        LookUpValueLocaleEntity lookUpValueLocaleEntity = legacyMasterDao.retrieveOneLookUpValueLocaleEntity(localeId, lookUpId);
        lookUpValueLocaleEntity.setLookUpValue(newMessage);
        createOrUpdate(lookUpValueLocaleEntity);
    }

    public void save(RoleBO roleBO) {
        try {
            super.save(roleBO);
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }
}