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

package org.mifos.security;

import static org.mifos.framework.util.helpers.TestObjectFactory.TEST_LOCALE;

import java.io.IOException;
import java.sql.SQLException;

import junit.framework.Assert;

import org.hibernate.Session;
import org.mifos.accounts.business.AddAccountAction;
import org.mifos.config.business.MifosConfiguration;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.business.MifosLookUpEntity;
import org.mifos.security.rolesandpermission.business.ActivityEntity;
import org.mifos.security.rolesandpermission.util.helpers.RolesAndPermissionConstants;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.persistence.Upgrade;
import org.mifos.security.authorization.AuthorizationManager;
import org.mifos.security.util.ActivityContext;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class AddActivityIntegrationTest extends MifosIntegrationTestCase {

    public AddActivityIntegrationTest() throws Exception {
        super();
    }
    
    private Session session;
    
    @Override
    public void setUp() {
        session = StaticHibernateUtil.getSessionTL(); 
    }
    
    @Override
    public void tearDown() throws Exception {
        TestDatabase.resetMySQLDatabase();
    }

    public void testStartFromStandardStore() throws Exception {
        upgradeAndCheck();
    }

    public void testStartFromSystemWithAddedLookupValues() throws Exception {

        for (int i = 0; i < 10; ++i) {
            LookUpValueEntity entity = new LookUpValueEntity();
            entity.setLookUpName("test look up value " + i);
            MifosLookUpEntity mifosLookUpEntity = new MifosLookUpEntity();
            mifosLookUpEntity.setEntityId((short) 87);
            entity.setLookUpEntity(mifosLookUpEntity);
            session.save(entity);
        }
        upgradeAndCheck();
    }

    private Upgrade upgradeAndCheck() throws IOException, SQLException, ApplicationException {
        short newId = 17032;
        int databaseVersion = 172;
        AddActivity upgrade = new AddActivity(databaseVersion + 1, newId, SecurityConstants.LOAN_MANAGEMENT,
                TEST_LOCALE, "Can use the executive washroom");
        upgrade.upgrade(session.connection());

        MifosConfiguration.getInstance().init();
        session = StaticHibernateUtil.getSessionTL(); 
        ActivityEntity fetched = (ActivityEntity) session.get(ActivityEntity.class, newId);

       Assert.assertEquals("Can use the executive washroom", fetched.getActivityName());

       Assert.assertEquals(SecurityConstants.LOAN_MANAGEMENT, (short) fetched.getParent().getId());

        ActivityContext activityContext = new ActivityContext(newId, TestObjectFactory.HEAD_OFFICE);
        AuthorizationManager authorizer = AuthorizationManager.getInstance();
        authorizer.init(session);

        UserContext admin = TestUtils.makeUser(RolesAndPermissionConstants.ADMIN_ROLE);
       Assert.assertTrue(authorizer.isActivityAllowed(admin, activityContext));

        UserContext nonAdmin = TestUtils.makeUser(TestUtils.DUMMY_ROLE);
        Assert.assertFalse(authorizer.isActivityAllowed(nonAdmin, activityContext));
        return upgrade;
    }

    public void testNoParent() throws Exception {
        short newId = 17032;
        int databaseVersion = 172;
        AddActivity upgrade = new AddActivity(databaseVersion + 1, newId, null, TEST_LOCALE,
                "Can use the executive washroom");
        upgrade.upgrade(session.connection());
        ActivityEntity fetched = (ActivityEntity) session.get(ActivityEntity.class, newId);
       Assert.assertEquals(null, fetched.getParent());
    }

    public void testValidateLookupValueKeyTest() throws Exception {
        String validKey = "Permissions-CanCreateFunds";
        String format = "Permissions-";
       Assert.assertTrue(AddAccountAction.validateLookupValueKey(format, validKey));
        String invalidKey = "CanCreateFunds";
        Assert.assertFalse(AddAccountAction.validateLookupValueKey(format, invalidKey));
    }

    public void testConstructorTest() throws Exception {
        short newId = 30000;
        AddActivity upgrade = null;
        try {
            // use deprecated construtor
            upgrade = new AddActivity(DatabaseVersionPersistence.APPLICATION_VERSION + 1, newId, null, TEST_LOCALE,
                    "NewActivity");
        } catch (Exception e) {
           Assert.assertEquals(e.getMessage(), AddActivity.wrongConstructor);
        }
        String invalidKey = "NewActivity";

        try {
            // use invalid lookup key format
            upgrade = new AddActivity(DatabaseVersionPersistence.APPLICATION_VERSION + 1, invalidKey, newId, null);
        } catch (Exception e) {
           Assert.assertEquals(e.getMessage(), AddActivity.wrongLookupValueKeyFormat);
        }
        String goodKey = "Permissions-NewActivity";
        // use valid construtor and valid key
        upgrade = new AddActivity(DatabaseVersionPersistence.APPLICATION_VERSION + 1, goodKey, newId, null);
        upgrade.upgrade(session.connection());
        ActivityEntity fetched = (ActivityEntity) session.get(ActivityEntity.class, newId);
       Assert.assertEquals(null, fetched.getParent());
       Assert.assertEquals(goodKey, fetched.getActivityName());
       Assert.assertEquals(goodKey, fetched.getActivityNameLookupValues().getLookUpName());
    }
}
