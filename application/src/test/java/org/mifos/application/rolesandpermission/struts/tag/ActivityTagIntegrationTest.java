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

package org.mifos.application.rolesandpermission.struts.tag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mifos.application.rolesandpermission.RoleTestUtil;
import org.mifos.application.rolesandpermission.business.ActivityEntity;
import org.mifos.application.rolesandpermission.persistence.RolesPermissionsPersistence;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;

public class ActivityTagIntegrationTest extends MifosIntegrationTestCase {

    public ActivityTagIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    public void testConvertToIdSet() throws Exception {
        Set<Short> activities = new ActivityTag().convertToIdSet(getActivities());
        assertNotNull(activities);
        assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT, activities.size());
    }

    public void testGetActivities() throws Exception {
        List<ActivityEntity> activities = new ActivityTag().getActivities(getActivities(), getUiActivities());
        assertNotNull(activities);
        assertEquals(2, activities.size());
    }

    private List<ActivityEntity> getActivities() throws Exception {
        return new RolesPermissionsPersistence().getActivities();
    }

    private Map<String, String> getUiActivities() {
        Map<String, String> uiActivities = new HashMap<String, String>();
        uiActivities.put("1", "checkbox");
        uiActivities.put("2", "3");
        uiActivities.put("3", "4");
        return uiActivities;
    }
}
