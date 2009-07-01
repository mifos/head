/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.application.rolesandpermission.business.service;

import org.mifos.application.rolesandpermission.RoleTestUtil;
import org.mifos.application.rolesandpermission.business.RoleBO;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class RolesPermissionsBusinessServiceIntegrationTest extends MifosIntegrationTest {

    public RolesPermissionsBusinessServiceIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    RolesPermissionsBusinessService rolesPermissionsBusinessService = new RolesPermissionsBusinessService();

    @Override
    protected void tearDown() throws Exception {
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testGetRoles() throws Exception {
        assertEquals(2, rolesPermissionsBusinessService.getRoles().size());
    }

    public void testGetRolesFailure() {
        TestObjectFactory.simulateInvalidConnection();
        try {
            rolesPermissionsBusinessService.getRoles();
            assertTrue(false);
        } catch (ServiceException e) {
            assertTrue(true);
        }
    }

    public void testGetActivities() throws Exception {
        assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT, rolesPermissionsBusinessService.getActivities().size());
    }

    public void testGetActivitiesFailure() {
        TestObjectFactory.simulateInvalidConnection();
        try {
            rolesPermissionsBusinessService.getActivities();
            assertTrue(false);
        } catch (ServiceException e) {
            assertTrue(true);
        }
    }

    public void testGetRoleForGivenId() throws Exception {
        RoleBO role = rolesPermissionsBusinessService.getRole(Short.valueOf("1"));
        assertNotNull(role);
        assertEquals(RoleTestUtil.EXPECTED_ACTIVITIES_FOR_ROLE, role.getActivities().size());
    }

    public void testGetRoleForGivenIdFailure() {
        TestObjectFactory.simulateInvalidConnection();
        try {
            rolesPermissionsBusinessService.getRole(Short.valueOf("1"));
            assertTrue(false);
        } catch (ServiceException e) {
            assertTrue(true);
        }
    }

}
