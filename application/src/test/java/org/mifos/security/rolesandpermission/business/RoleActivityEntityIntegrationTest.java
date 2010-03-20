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

package org.mifos.security.rolesandpermission.business;

import junit.framework.Assert;

import org.hibernate.Query;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

public class RoleActivityEntityIntegrationTest extends MifosIntegrationTestCase {

    public RoleActivityEntityIntegrationTest() throws Exception {
        super();
    }

    public void testGetRoleActivity() {
        RoleActivityEntity roleActivityEntity = getRoleActivity((short) 1, (short) 1);
        Assert.assertNull(roleActivityEntity);
        roleActivityEntity = getRoleActivity(Short.valueOf("1"), Short.valueOf("3"));
        Assert.assertNotNull(roleActivityEntity);
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

}
