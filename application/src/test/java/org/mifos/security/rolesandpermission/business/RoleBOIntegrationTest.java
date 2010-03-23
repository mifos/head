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

package org.mifos.security.rolesandpermission.business;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import org.hibernate.Query;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.rolesandpermission.RoleTestUtil;
import org.mifos.security.rolesandpermission.exceptions.RolesPermissionException;
import org.mifos.security.rolesandpermission.persistence.RolesPermissionsPersistence;
import org.mifos.security.rolesandpermission.util.helpers.RolesAndPermissionConstants;

public class RoleBOIntegrationTest extends MifosIntegrationTestCase {

    public RoleBOIntegrationTest() throws Exception {
        super();
    }

    public void testGetRole() throws Exception {
        RolesPermissionsPersistence persistence = new RolesPermissionsPersistence();
        RoleBO roleBO = persistence.getRole("Admin");
        Assert.assertNotNull(roleBO);
       Assert.assertEquals("Admin", roleBO.getName());
       Assert.assertEquals(RoleTestUtil.EXPECTED_ACTIVITIES_FOR_ROLE, roleBO.getActivities().size());
    }

    public void testCreateRole() throws Exception {
        RolesPermissionsPersistence RolesPermissionsPersistence = new RolesPermissionsPersistence();
        List<ActivityEntity> activities = RolesPermissionsPersistence.getActivities();
       Assert.assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT, activities.size());
        RoleBO roleBO = new RoleBO(TestObjectFactory.getContext(), "Test Role", activities);
        roleBO.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        roleBO = RolesPermissionsPersistence.getRole("Test Role");
       Assert.assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT, roleBO.getActivities().size());
       Assert.assertEquals(roleBO.getCreatedBy(), Short.valueOf("1"));
       Assert.assertEquals(DateUtils.getCurrentDateWithoutTimeStamp(), DateUtils.getDateWithoutTimeStamp(roleBO
                .getCreatedDate().getTime()));

        RolesPermissionsPersistence.delete(roleBO);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        roleBO = RolesPermissionsPersistence.getRole("Test Role");
        Assert.assertNull(roleBO);
    }

    public void testCreateFailureWhenRoleNameIsNull() throws Exception {
        RolesPermissionsPersistence RolesPermissionsPersistence = new RolesPermissionsPersistence();
        List<ActivityEntity> activities = RolesPermissionsPersistence.getActivities();
       Assert.assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT, activities.size());
        RoleBO roleBO = null;
        try {
            roleBO = new RoleBO(TestObjectFactory.getContext(), null, activities);
            roleBO.save();
            StaticHibernateUtil.commitTransaction();
            StaticHibernateUtil.closeSession();
            Assert.fail();
        } catch (RolesPermissionException e) {
           Assert.assertEquals(RolesAndPermissionConstants.KEYROLENAMENOTSPECIFIED, e.getKey());
        }

        try {
            roleBO = new RoleBO(TestObjectFactory.getContext(), "", activities);
            roleBO.save();
            StaticHibernateUtil.commitTransaction();
            StaticHibernateUtil.closeSession();
            Assert.fail();
        } catch (RolesPermissionException e) {
           Assert.assertEquals(RolesAndPermissionConstants.KEYROLENAMENOTSPECIFIED, e.getKey());
        }
    }

    public void testCreateFailureForEmptyRoleName() throws Exception {
        RolesPermissionsPersistence RolesPermissionsPersistence = new RolesPermissionsPersistence();
        List<ActivityEntity> activities = RolesPermissionsPersistence.getActivities();
       Assert.assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT, activities.size());
        RoleBO roleBO = null;
        try {
            roleBO = new RoleBO(TestObjectFactory.getContext(), "", activities);
            roleBO.save();
            StaticHibernateUtil.commitTransaction();
            StaticHibernateUtil.closeSession();
            Assert.fail();
        } catch (RolesPermissionException e) {
           Assert.assertEquals(RolesAndPermissionConstants.KEYROLENAMENOTSPECIFIED, e.getKey());
        }
    }

    public void testCreateFailureForDuplicateRoleName() throws Exception {
        RolesPermissionsPersistence RolesPermissionsPersistence = new RolesPermissionsPersistence();
        List<ActivityEntity> activities = RolesPermissionsPersistence.getActivities();
       Assert.assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT, activities.size());
        RoleBO roleBO = null;
        try {
            roleBO = new RoleBO(TestObjectFactory.getContext(), "Admin", activities);
            roleBO.save();
            StaticHibernateUtil.commitTransaction();
            StaticHibernateUtil.closeSession();
            Assert.fail();
        } catch (RolesPermissionException e) {
           Assert.assertEquals(RolesAndPermissionConstants.KEYROLEALREADYEXIST, e.getKey());
        }
    }

    public void testCreateFailureForNullActivities() throws Exception {
        RolesPermissionsPersistence RolesPermissionsPersistence = new RolesPermissionsPersistence();
        List<ActivityEntity> activities = RolesPermissionsPersistence.getActivities();
       Assert.assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT, activities.size());
        RoleBO roleBO = null;
        try {
            roleBO = new RoleBO(TestObjectFactory.getContext(), "Test Role", null);
            roleBO.save();
            StaticHibernateUtil.commitTransaction();
            StaticHibernateUtil.closeSession();
            Assert.fail();
        } catch (RolesPermissionException e) {
           Assert.assertEquals(RolesAndPermissionConstants.KEYROLEWITHNOACTIVITIES, e.getKey());
        }
    }

    public void testCreateFailureForEmptyActivities() throws Exception {
        RolesPermissionsPersistence RolesPermissionsPersistence = new RolesPermissionsPersistence();
        List<ActivityEntity> activities = RolesPermissionsPersistence.getActivities();
       Assert.assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT, activities.size());
        RoleBO roleBO = null;
        try {
            roleBO = new RoleBO(TestObjectFactory.getContext(), "Test Role", new ArrayList<ActivityEntity>());
            roleBO.save();
            StaticHibernateUtil.commitTransaction();
            StaticHibernateUtil.closeSession();
            Assert.fail();
        } catch (RolesPermissionException e) {
           Assert.assertEquals(RolesAndPermissionConstants.KEYROLEWITHNOACTIVITIES, e.getKey());
        }
    }

    public void testDeleteRole() throws Exception {
        RolesPermissionsPersistence RolesPermissionsPersistence = new RolesPermissionsPersistence();
        List<ActivityEntity> activities = RolesPermissionsPersistence.getActivities();
       Assert.assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT, activities.size());
        RoleBO roleBO = new RoleBO(TestObjectFactory.getContext(), "Test Role", activities);
        roleBO.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        roleBO = RolesPermissionsPersistence.getRole("Test Role");
       Assert.assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT, roleBO.getActivities().size());
       Assert.assertEquals(roleBO.getCreatedBy(), Short.valueOf("1"));
       Assert.assertEquals(DateUtils.getCurrentDateWithoutTimeStamp(), DateUtils.getDateWithoutTimeStamp(roleBO
                .getCreatedDate().getTime()));
        roleBO.delete();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        roleBO = RolesPermissionsPersistence.getRole("Test Role");
        Assert.assertNull(roleBO);
    }

    public void testDeleteRoleFailureForRoleAssignedToPersonnel() throws Exception {
        RolesPermissionsPersistence RolesPermissionsPersistence = new RolesPermissionsPersistence();
        RoleBO roleBO = RolesPermissionsPersistence.getRole("Admin");
        try {
            roleBO.delete();
            StaticHibernateUtil.commitTransaction();
            StaticHibernateUtil.closeSession();
            Assert.fail();
        } catch (RolesPermissionException e) {
           Assert.assertEquals(RolesAndPermissionConstants.KEYROLEASSIGNEDTOPERSONNEL, e.getKey());
        } finally {
            roleBO = RolesPermissionsPersistence.getRole("Admin");
            Assert.assertNotNull(roleBO);
        }
    }

    public void testUpdateRemoveActivities() throws Exception {
        RolesPermissionsPersistence RolesPermissionsPersistence = new RolesPermissionsPersistence();
        List<ActivityEntity> activities = RolesPermissionsPersistence.getActivities();
       Assert.assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT, activities.size());
        RoleBO roleBO = new RoleBO(TestObjectFactory.getContext(), "Test Role", activities);
        roleBO.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        roleBO = RolesPermissionsPersistence.getRole("Test Role");
        ActivityEntity activity_1 = roleBO.getActivities().get(0);
        for (Iterator<ActivityEntity> iter = activities.iterator(); iter.hasNext();) {
            ActivityEntity activityEntity = iter.next();
            if (activityEntity.getId().equals(activity_1.getId())) {
                iter.remove();
            }
        }

        ActivityEntity activity_2 = roleBO.getActivities().get(1);
        for (Iterator<ActivityEntity> iter = activities.iterator(); iter.hasNext();) {
            ActivityEntity activityEntity = iter.next();
            if (activityEntity.getId().equals(activity_2.getId())) {
                iter.remove();
            }
        }
        roleBO.update(PersonnelConstants.SYSTEM_USER, "Test Role", activities);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        roleBO = RolesPermissionsPersistence.getRole("Test Role");

       Assert.assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT - 2, roleBO.getActivities().size());
       Assert.assertEquals(roleBO.getUpdatedBy(), Short.valueOf("1"));
       Assert.assertEquals(DateUtils.getCurrentDateWithoutTimeStamp(), DateUtils.getDateWithoutTimeStamp(roleBO
                .getUpdatedDate().getTime()));
        RoleActivityEntity roleActivityEntity = getRoleActivity(roleBO.getId(), activity_1.getId());
        Assert.assertNull(roleActivityEntity);
        roleActivityEntity = getRoleActivity(roleBO.getId(), activity_2.getId());
        Assert.assertNull(roleActivityEntity);

        roleBO.delete();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
    }

    public void testUpdateAddingActivities() throws Exception {
        RolesPermissionsPersistence RolesPermissionsPersistence = new RolesPermissionsPersistence();
        List<ActivityEntity> activities = RolesPermissionsPersistence.getActivities();
       Assert.assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT, activities.size());
        ActivityEntity activity_1 = activities.get(0);
        activities.remove(0);
        ActivityEntity activity_2 = activities.get(1);
        activities.remove(1);
       Assert.assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT - 2, activities.size());
        RoleBO roleBO = new RoleBO(TestObjectFactory.getContext(), "Test Role", activities);
        roleBO.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        roleBO = RolesPermissionsPersistence.getRole("Test Role");
       Assert.assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT - 2, roleBO.getActivities().size());
        RoleActivityEntity roleActivityEntity = getRoleActivity(roleBO.getId(), activity_1.getId());
        Assert.assertNull(roleActivityEntity);
        roleActivityEntity = getRoleActivity(roleBO.getId(), activity_2.getId());
        Assert.assertNull(roleActivityEntity);

       Assert.assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT - 2, activities.size());
        activities.add(activity_1);
        activities.add(activity_2);
       Assert.assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT, activities.size());

        roleBO.update(PersonnelConstants.SYSTEM_USER, "Test Role", activities);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        roleBO = RolesPermissionsPersistence.getRole("Test Role");

       Assert.assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT, roleBO.getActivities().size());
       Assert.assertEquals(roleBO.getUpdatedBy(), Short.valueOf("1"));
       Assert.assertEquals(DateUtils.getCurrentDateWithoutTimeStamp(), DateUtils.getDateWithoutTimeStamp(roleBO
                .getUpdatedDate().getTime()));
        roleActivityEntity = getRoleActivity(roleBO.getId(), activity_1.getId());
        Assert.assertNotNull(roleActivityEntity);
        roleActivityEntity = getRoleActivity(roleBO.getId(), activity_2.getId());
        Assert.assertNotNull(roleActivityEntity);

        roleBO.delete();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
    }

    public void testUpdateForChangingName() throws Exception {
        RolesPermissionsPersistence RolesPermissionsPersistence = new RolesPermissionsPersistence();
        List<ActivityEntity> activities = RolesPermissionsPersistence.getActivities();
       Assert.assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT, activities.size());
        RoleBO roleBO = new RoleBO(TestObjectFactory.getContext(), "Test Role", activities);
        roleBO.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        roleBO = RolesPermissionsPersistence.getRole("Test Role");
        roleBO.update(PersonnelConstants.SYSTEM_USER, "New role", activities);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        roleBO = RolesPermissionsPersistence.getRole("New role");

       Assert.assertEquals("New role", roleBO.getName());
       Assert.assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT, roleBO.getActivities().size());
       Assert.assertEquals(roleBO.getUpdatedBy(), Short.valueOf("1"));
       Assert.assertEquals(DateUtils.getCurrentDateWithoutTimeStamp(), DateUtils.getDateWithoutTimeStamp(roleBO
                .getUpdatedDate().getTime()));

        roleBO.delete();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
    }

    public void testUpdateFailureForDuplicateName() throws Exception {
        RolesPermissionsPersistence RolesPermissionsPersistence = new RolesPermissionsPersistence();
        List<ActivityEntity> activities = RolesPermissionsPersistence.getActivities();
       Assert.assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT, activities.size());
        RoleBO roleBO = new RoleBO(TestObjectFactory.getContext(), "Test Role", activities);
        roleBO.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        roleBO = RolesPermissionsPersistence.getRole("Test Role");
        try {
            roleBO.update(PersonnelConstants.SYSTEM_USER, "Admin", activities);
            StaticHibernateUtil.commitTransaction();
            StaticHibernateUtil.closeSession();
            Assert.fail();
        } catch (RolesPermissionException e) {
           Assert.assertEquals(RolesAndPermissionConstants.KEYROLEALREADYEXIST, e.getKey());
        } finally {
            roleBO.delete();
            StaticHibernateUtil.commitTransaction();
            StaticHibernateUtil.closeSession();
        }
    }

    public void testUpdateFailureForRoleNameAsNull() throws Exception {
        RolesPermissionsPersistence RolesPermissionsPersistence = new RolesPermissionsPersistence();
        List<ActivityEntity> activities = RolesPermissionsPersistence.getActivities();
       Assert.assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT, activities.size());
        RoleBO roleBO = new RoleBO(TestObjectFactory.getContext(), "Test Role", activities);
        roleBO.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        roleBO = RolesPermissionsPersistence.getRole("Test Role");
        try {
            roleBO.update(PersonnelConstants.SYSTEM_USER, null, activities);
            StaticHibernateUtil.commitTransaction();
            StaticHibernateUtil.closeSession();
            Assert.fail();
        } catch (RolesPermissionException e) {
           Assert.assertEquals(RolesAndPermissionConstants.KEYROLENAMENOTSPECIFIED, e.getKey());
        } finally {
            roleBO.delete();
            StaticHibernateUtil.commitTransaction();
            StaticHibernateUtil.closeSession();
        }
    }

    public void testUpdateFailureForRoleNameAsEmptyString() throws Exception {
        RolesPermissionsPersistence RolesPermissionsPersistence = new RolesPermissionsPersistence();
        List<ActivityEntity> activities = RolesPermissionsPersistence.getActivities();
       Assert.assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT, activities.size());
        RoleBO roleBO = new RoleBO(TestObjectFactory.getContext(), "Test Role", activities);
        roleBO.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        roleBO = RolesPermissionsPersistence.getRole("Test Role");
        try {
            roleBO.update(PersonnelConstants.SYSTEM_USER, "", activities);
            StaticHibernateUtil.commitTransaction();
            StaticHibernateUtil.closeSession();
            Assert.fail();
        } catch (RolesPermissionException e) {
           Assert.assertEquals(RolesAndPermissionConstants.KEYROLENAMENOTSPECIFIED, e.getKey());
        } finally {
            roleBO.delete();
            StaticHibernateUtil.commitTransaction();
            StaticHibernateUtil.closeSession();
        }
    }

    public void testUpdateFailureForNullActivities() throws Exception {
        RolesPermissionsPersistence RolesPermissionsPersistence = new RolesPermissionsPersistence();
        List<ActivityEntity> activities = RolesPermissionsPersistence.getActivities();
       Assert.assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT, activities.size());
        RoleBO roleBO = new RoleBO(TestObjectFactory.getContext(), "Test Role", activities);
        roleBO.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        roleBO = RolesPermissionsPersistence.getRole("Test Role");
        try {
            roleBO.update(PersonnelConstants.SYSTEM_USER, "Test Role", null);
            StaticHibernateUtil.commitTransaction();
            StaticHibernateUtil.closeSession();
            Assert.fail();
        } catch (RolesPermissionException e) {
           Assert.assertEquals(RolesAndPermissionConstants.KEYROLEWITHNOACTIVITIES, e.getKey());
        } finally {
            roleBO.delete();
            StaticHibernateUtil.commitTransaction();
            StaticHibernateUtil.closeSession();
        }
    }

    public void testUpdateFailureForEmptyActivities() throws Exception {
        RolesPermissionsPersistence RolesPermissionsPersistence = new RolesPermissionsPersistence();
        List<ActivityEntity> activities = RolesPermissionsPersistence.getActivities();
       Assert.assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT, activities.size());
        RoleBO roleBO = new RoleBO(TestObjectFactory.getContext(), "Test Role", activities);
        roleBO.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        roleBO = RolesPermissionsPersistence.getRole("Test Role");
        try {
            roleBO.update(PersonnelConstants.SYSTEM_USER, "Test Role", new ArrayList<ActivityEntity>());
            StaticHibernateUtil.commitTransaction();
            StaticHibernateUtil.closeSession();
            Assert.fail();
        } catch (RolesPermissionException e) {
           Assert.assertEquals(RolesAndPermissionConstants.KEYROLEWITHNOACTIVITIES, e.getKey());
        } finally {
            roleBO.delete();
            StaticHibernateUtil.commitTransaction();
            StaticHibernateUtil.closeSession();
        }
    }

    private RoleActivityEntity getRoleActivity(Short roleId, Short activityId) {
        Query query = StaticHibernateUtil
                .getSessionTL()
                .createQuery(
                        "from org.mifos.security.rolesandpermission.business.RoleActivityEntity roleActivity where roleActivity.role=? and roleActivity.activity=?");
        query.setShort(0, roleId);
        query.setShort(1, activityId);
        RoleActivityEntity roleActivityEntity = (RoleActivityEntity) query.uniqueResult();
        return roleActivityEntity;
    }

    public void testSaveRoleForInvalidConnection() throws Exception {
        try {
            RolesPermissionsPersistence RolesPermissionsPersistence = new RolesPermissionsPersistence();
            List<ActivityEntity> activities = RolesPermissionsPersistence.getActivities();
           Assert.assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT, activities.size());
            RoleBO roleBO = new RoleBO(TestObjectFactory.getContext(), "Test Role", activities);
            TestObjectFactory.simulateInvalidConnection();
            roleBO.save();
            Assert.fail();
        } catch (RolesPermissionException e) {
           Assert.assertTrue(true);
        } finally {
            StaticHibernateUtil.closeSession();
        }

    }

}
