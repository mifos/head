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

package org.mifos.security;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import junit.framework.Assert;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mifos.accounts.business.AddAccountAction;
import org.mifos.application.master.business.LookUpEntity;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.Upgrade;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.rolesandpermission.business.ActivityEntity;
import org.mifos.security.rolesandpermission.persistence.LegacyRolesPermissionsDao;
import org.mifos.security.rolesandpermission.util.helpers.RolesAndPermissionConstants;
import org.mifos.security.util.ActivityContext;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;

public class AddActivityIntegrationTest extends MifosIntegrationTestCase {

    private Session session;

    @Autowired
    private LegacyRolesPermissionsDao legacyRolesPermissionsDao;

    @Before
    public void setUp() throws Exception {
        session = StaticHibernateUtil.getSessionTL();

    }

    @Ignore
    @Test
    public void testStartFromStandardStore() throws Exception {
        upgradeAndCheck();
    }

    @Ignore
    @Test
    public void testStartFromSystemWithAddedLookupValues() throws Exception {

        for (int i = 0; i < 10; ++i) {
            LookUpValueEntity entity = new LookUpValueEntity();
            entity.setLookUpName("test look up value " + i);
            LookUpEntity lookUpEntity = new LookUpEntity();
            lookUpEntity.setEntityId((short) 87);
            entity.setLookUpEntity(lookUpEntity);
            session.save(entity);
        }
        upgradeAndCheck();
    }

    private Upgrade upgradeAndCheck() throws IOException, SQLException, ApplicationException {
//        Connection connection = session.connection();
//        connection.setAutoCommit(true);
        short newId = 17032;
        AddActivity upgrade = new AddActivity(newId, SecurityConstants.LOAN_MANAGEMENT, "Can use the executive washroom");
        Connection connection = dataSource.getConnection();
        upgrade.upgrade(connection);
        connection.close();

        session = StaticHibernateUtil.getSessionTL();
        ActivityEntity fetched = (ActivityEntity) session.get(ActivityEntity.class, newId);

       Assert.assertEquals("Can use the executive washroom", fetched.getActivityName());

       Assert.assertEquals(SecurityConstants.LOAN_MANAGEMENT, (short) fetched.getParent().getId());

        ActivityContext activityContext = new ActivityContext(newId, TestObjectFactory.HEAD_OFFICE);

        UserContext admin = TestUtils.makeUser(RolesAndPermissionConstants.ADMIN_ROLE);
       Assert.assertTrue(legacyRolesPermissionsDao.isActivityAllowed(admin, activityContext));

        UserContext nonAdmin = TestUtils.makeUser(TestUtils.DUMMY_ROLE);
        Assert.assertFalse(legacyRolesPermissionsDao.isActivityAllowed(nonAdmin, activityContext));
        return upgrade;
    }

    @Test
    public void testNoParent() throws Exception {
        short newId = 17032;
        AddActivity upgrade = new AddActivity(newId, null, "Can use the executive washroom");
        Connection connection = dataSource.getConnection();
        upgrade.upgrade(connection);
        connection.close();
        ActivityEntity fetched = (ActivityEntity) session.get(ActivityEntity.class, newId);
       Assert.assertEquals(null, fetched.getParent());
    }

    @Test
    public void testValidateLookupValueKeyTest() throws Exception {
        String validKey = "Permissions-CanCreateFunds";
        String format = "Permissions-";
       Assert.assertTrue(AddAccountAction.validateLookupValueKey(format, validKey));
        String invalidKey = "CanCreateFunds";
        Assert.assertFalse(AddAccountAction.validateLookupValueKey(format, invalidKey));
    }

    @Ignore
    @Test
    public void testConstructorTest() throws Exception {
        Connection conn = dataSource.getConnection();
        short newId = 30000;
        AddActivity upgrade = null;
        String invalidKey = "NewActivity";

        try {
            // use invalid lookup key format
            upgrade = new AddActivity(invalidKey, newId, null);
        } catch (Exception e) {
           Assert.assertEquals(e.getMessage(), AddActivity.wrongLookupValueKeyFormat);
        }
        String goodKey = "Permissions-NewActivity";
        // use valid constructor and valid key
        upgrade = new AddActivity(goodKey, newId, null);
        upgrade.upgrade(conn);
        conn.close();
        ActivityEntity fetched = (ActivityEntity) session.get(ActivityEntity.class, newId);
       Assert.assertEquals(null, fetched.getParent());
       Assert.assertEquals(goodKey, fetched.getActivityName());
       Assert.assertEquals(goodKey, fetched.getActivityNameLookupValues().getLookUpName());
    }
}
