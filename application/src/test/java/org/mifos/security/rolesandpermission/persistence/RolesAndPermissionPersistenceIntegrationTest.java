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

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.security.rolesandpermission.RoleTestUtil;
import org.mifos.security.rolesandpermission.business.ActivityEntity;
import org.mifos.security.rolesandpermission.business.RoleBO;
import org.springframework.beans.factory.annotation.Autowired;

public class RolesAndPermissionPersistenceIntegrationTest extends MifosIntegrationTestCase {

    @Autowired
    LegacyRolesPermissionsDao legacyRolesPermissionsDao;

    @Test
    public void testGetRole() throws Exception {
        RoleBO role = legacyRolesPermissionsDao.getRole("Admin");
       Assert.assertEquals(RoleTestUtil.EXPECTED_ACTIVITIES_FOR_ROLE, role.getActivities().size());
    }

    @Test
    public void testGetActivities() throws Exception {
        List<ActivityEntity> activities = legacyRolesPermissionsDao.getActivities();
       Assert.assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT, activities.size());
    }

    @Test
    public void testGetRoles() throws Exception {
        List<RoleBO> roles = legacyRolesPermissionsDao.getRoles();
       Assert.assertEquals(3, roles.size());
    }

    @Test
    public void testGetRoleForGivenId() throws Exception {
        RoleBO role = legacyRolesPermissionsDao.getRole((short) 1);
       Assert.assertEquals(RoleTestUtil.EXPECTED_ACTIVITIES_FOR_ROLE, role.getActivities().size());
    }

}
