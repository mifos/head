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

package org.mifos.application.rolesandpermission.utils.helpers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mifos.application.rolesandpermission.business.ActivityEntity;
import org.mifos.application.rolesandpermission.business.service.RolesPermissionsBusinessService;
import org.mifos.application.rolesandpermission.util.helpers.RoleTempleteBuilder;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.ActivityChangeEvent;
import org.mifos.framework.security.util.RoleChangeEvent;

public class RoleTempleteBuilderIntegrationTest extends MifosIntegrationTest {

    public RoleTempleteBuilderIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    public void testLocaleId() {
        RoleTempleteBuilder roleTempleteBuilder = new RoleTempleteBuilder();
        roleTempleteBuilder.setLocaleId(Short.valueOf("1"));
        assertEquals(Short.valueOf("1"), roleTempleteBuilder.getLocaleId());
    }

    public void testSetCurrentActivites() {
        Set<Short> activities = new HashSet<Short>();
        activities.add(Short.valueOf("1"));
        RoleTempleteBuilder roleTempleteBuilder = new RoleTempleteBuilder();
        roleTempleteBuilder.setCurrentActivites(activities);
        activities = roleTempleteBuilder.getCurrentActivites();
        assertEquals(1, activities.size());
    }

    public void testGetRolesTemplete() throws Exception {
        List<ActivityEntity> activities = new RolesPermissionsBusinessService().getActivities();
        for (ActivityEntity activityEntity : activities)
            activityEntity.setLocaleId(Short.valueOf("1"));
        StringBuilder stringBuilder = new RoleTempleteBuilder().getRolesTemplete(activities);
        assertNotNull(stringBuilder);
        assertTrue(stringBuilder.toString().contains("Can create new role"));
        assertTrue(stringBuilder.toString().contains("Can modify a role"));
        assertTrue(stringBuilder.toString().contains("Can delete a role"));
    }

    public void testActivityChangeEvent() {
        ActivityChangeEvent activityChangeEvent = new ActivityChangeEvent("event", "stringObject");
        assertEquals("value of event", "event", activityChangeEvent.getEventType());
        assertEquals("value of object", "stringObject", activityChangeEvent.getObject());
    }

    public void testRoleChangeEvent() {
        RoleChangeEvent roleChangeEvent = new RoleChangeEvent("event", "stringObject");
        assertEquals("value of event", "event", roleChangeEvent.getEventType());
        assertEquals("value of object", "stringObject", roleChangeEvent.getObject());
    }
}
