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

package org.mifos.security.rolesandpermission.utils.helpers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.security.rolesandpermission.business.ActivityEntity;
import org.mifos.security.rolesandpermission.business.service.RolesPermissionsBusinessService;
import org.mifos.security.rolesandpermission.util.helpers.RoleTempleteBuilder;
import org.mifos.security.util.ActivityChangeEvent;
import org.mifos.security.util.RoleChangeEvent;

public class RoleTempleteBuilderIntegrationTest extends MifosIntegrationTestCase {

    public RoleTempleteBuilderIntegrationTest() throws Exception {
        super();
    }

    public void testLocaleId() {
        RoleTempleteBuilder roleTempleteBuilder = new RoleTempleteBuilder();
        roleTempleteBuilder.setLocaleId(Short.valueOf("1"));
       Assert.assertEquals(Short.valueOf("1"), roleTempleteBuilder.getLocaleId());
    }

    public void testSetCurrentActivites() {
        Set<Short> activities = new HashSet<Short>();
        activities.add(Short.valueOf("1"));
        RoleTempleteBuilder roleTempleteBuilder = new RoleTempleteBuilder();
        roleTempleteBuilder.setCurrentActivites(activities);
        activities = roleTempleteBuilder.getCurrentActivites();
       Assert.assertEquals(1, activities.size());
    }

    public void testGetRolesTemplete() throws Exception {
        List<ActivityEntity> activities = new RolesPermissionsBusinessService().getActivities();
        StringBuilder stringBuilder = new RoleTempleteBuilder().getRolesTemplete(activities);
        Assert.assertNotNull(stringBuilder);
       Assert.assertTrue(stringBuilder.toString().contains("Can create new role"));
       Assert.assertTrue(stringBuilder.toString().contains("Can modify a role"));
       Assert.assertTrue(stringBuilder.toString().contains("Can delete a role"));
    }

    public void testActivityChangeEvent() {
        ActivityChangeEvent activityChangeEvent = new ActivityChangeEvent("event", "stringObject");
       Assert.assertEquals("value of event", "event", activityChangeEvent.getEventType());
       Assert.assertEquals("value of object", "stringObject", activityChangeEvent.getObject());
    }

    public void testRoleChangeEvent() {
        RoleChangeEvent roleChangeEvent = new RoleChangeEvent("event", "stringObject");
       Assert.assertEquals("value of event", "event", roleChangeEvent.getEventType());
       Assert.assertEquals("value of object", "stringObject", roleChangeEvent.getObject());
    }
}
