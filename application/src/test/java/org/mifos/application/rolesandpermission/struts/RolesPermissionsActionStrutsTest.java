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

package org.mifos.application.rolesandpermission.struts;

import java.util.List;

import junit.framework.Assert;

import org.mifos.application.rolesandpermission.RoleTestUtil;
import org.mifos.application.rolesandpermission.business.ActivityEntity;
import org.mifos.application.rolesandpermission.business.RoleBO;
import org.mifos.application.rolesandpermission.persistence.RolesPermissionsPersistence;
import org.mifos.application.rolesandpermission.struts.action.RolesPermissionsAction;
import org.mifos.application.rolesandpermission.struts.actionforms.RolesPermissionsActionForm;
import org.mifos.application.rolesandpermission.util.helpers.RolesAndPermissionConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.security.authorization.AuthorizationManager;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class RolesPermissionsActionStrutsTest extends MifosMockStrutsTestCase {

    public RolesPermissionsActionStrutsTest() throws Exception {
        super();
    }

    UserContext userContext = null;

    private String flowKey;

    private RoleBO role = null;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        userContext = TestUtils.makeUser();
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        addRequestParameter("recordLoanOfficerId", "1");
        addRequestParameter("recordOfficeId", "1");
        ActivityContext ac = new ActivityContext((short) 0, userContext.getBranchId().shortValue(), userContext.getId()
                .shortValue());
        request.getSession(false).setAttribute("ActivityContext", ac);
        flowKey = createFlow(request, RolesPermissionsAction.class);
    }

    @Override
    protected void tearDown() throws Exception {
        TestObjectFactory.cleanUp(role);
        super.tearDown();
    }

    public void testViewRoles() throws Exception {
        setRequestPathInfo("/rolesPermission.do");
        addRequestParameter("method", "viewRoles");
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.viewRoles_success.toString());
        List<RoleBO> roles = (List<RoleBO>) SessionUtils.getAttribute(RolesAndPermissionConstants.ROLES, request);
       Assert.assertEquals(2, roles.size());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    public void testLoad() throws Exception {
        TestDatabase.resetMySQLDatabase();
        setRequestPathInfo("/rolesPermission.do");
        addRequestParameter("method", "load");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.load_success.toString());
        List<ActivityEntity> activities = (List<ActivityEntity>) SessionUtils.getAttribute(
                RolesAndPermissionConstants.ACTIVITYLIST, request);
        Assert.assertNull(SessionUtils.getAttribute(Constants.BUSINESS_KEY, request));
       Assert.assertEquals(RoleTestUtil.EXPECTED_ACTIVITY_COUNT, activities.size());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    public void testCreate() throws Exception {

        setRequestPathInfo("/rolesPermission.do");
        addRequestParameter("method", "load");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.load_success.toString());

        setRequestPathInfo("/rolesPermission.do");
        addRequestParameter("method", "create");
        addRequestParameter("name", "New Role");
        addRequestParameter("activity(1)", "checkbox");
        addRequestParameter("activity(2)", "3");
        addRequestParameter("activity(3)", "4");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.create_success.toString());

        RolesPermissionsPersistence rolesPermissionsPersistence = new RolesPermissionsPersistence();
        role = rolesPermissionsPersistence.getRole("New Role");
       Assert.assertEquals(2, role.getActivities().size());

        UserContext userContext = TestUtils.makeUser(role.getId());
        ActivityContext activityContext = new ActivityContext((short) 3, (short) 1, (short) 0);
       Assert.assertTrue(AuthorizationManager.getInstance().isActivityAllowed(userContext, activityContext));
        activityContext = new ActivityContext((short) 4, (short) 1, (short) 0);
       Assert.assertTrue(AuthorizationManager.getInstance().isActivityAllowed(userContext, activityContext));
        activityContext = new ActivityContext((short) 5, (short) 1, (short) 0);
        Assert.assertFalse(AuthorizationManager.getInstance().isActivityAllowed(userContext, activityContext));

    }

    public void testCreateFailureWhenNameIsNull() throws Exception {
        setRequestPathInfo("/rolesPermission.do");
        addRequestParameter("method", "load");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.load_success.toString());

        setRequestPathInfo("/rolesPermission.do");
        addRequestParameter("method", "create");
        addRequestParameter("activity(1)", "checkbox");
        addRequestParameter("activity(2)", "3");
        addRequestParameter("activity(3)", "4");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.create_failure.toString());
    }

    public void testCreateFailureWhenNameIsEmpty() throws Exception {
        setRequestPathInfo("/rolesPermission.do");
        addRequestParameter("method", "load");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.load_success.toString());

        setRequestPathInfo("/rolesPermission.do");
        addRequestParameter("method", "create");
        addRequestParameter("name", "");
        addRequestParameter("activity(1)", "checkbox");
        addRequestParameter("activity(2)", "3");
        addRequestParameter("activity(3)", "4");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.create_failure.toString());
    }

    public void testCreateFailureActivitiesAreNull() throws Exception {
        setRequestPathInfo("/rolesPermission.do");
        addRequestParameter("method", "load");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.load_success.toString());

        setRequestPathInfo("/rolesPermission.do");
        addRequestParameter("method", "create");
        addRequestParameter("name", "New Role");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.create_failure.toString());
    }

    public void testCreateFailureActivitiesAreEmpty() throws Exception {
        setRequestPathInfo("/rolesPermission.do");
        addRequestParameter("method", "load");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.load_success.toString());

        setRequestPathInfo("/rolesPermission.do");
        addRequestParameter("method", "create");
        addRequestParameter("name", "New Role");
        addRequestParameter("activity(1)", "checkbox");
        addRequestParameter("activity(2)", "");
        addRequestParameter("activity(3)", "");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.create_failure.toString());
    }

    public void testManage() throws Exception {
        RolesPermissionsPersistence persistence = new RolesPermissionsPersistence();
        role = TestObjectFactory.createRole(TestUtils.makeUser(), "New Role", persistence.getActivities());
        setRequestPathInfo("/rolesPermission.do");
        addRequestParameter("method", "manage");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("id", role.getId().toString());
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.manage_success.toString());
       Assert.assertEquals("New Role", ((RolesPermissionsActionForm) request.getSession().getAttribute(
                "rolesPermissionsActionForm")).getName());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    public void testUpdateSuccess() throws Exception {
        RolesPermissionsPersistence persistence = new RolesPermissionsPersistence();
        role = TestObjectFactory.createRole(TestUtils.makeUser(), "New Role", persistence.getActivities());
        setRequestPathInfo("/rolesPermission.do");
        addRequestParameter("method", "manage");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("id", role.getId().toString());
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.manage_success.toString());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));

        setRequestPathInfo("/rolesPermission.do");
        addRequestParameter("method", "update");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("name", role.getName());
        addRequestParameter("activity(1)", "checkbox");
        addRequestParameter("activity(2)", "3");
        addRequestParameter("activity(3)", "4");
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.update_success.toString());
        Assert.assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));

        role = persistence.getRole(role.getId());
       Assert.assertEquals(2, role.getActivities().size());

        UserContext userContext = TestUtils.makeUser(role.getId());
        ActivityContext activityContext = new ActivityContext((short) 3, (short) 1, (short) 0);
       Assert.assertTrue(AuthorizationManager.getInstance().isActivityAllowed(userContext, activityContext));
        activityContext = new ActivityContext((short) 4, (short) 1, (short) 0);
       Assert.assertTrue(AuthorizationManager.getInstance().isActivityAllowed(userContext, activityContext));
        activityContext = new ActivityContext((short) 5, (short) 1, (short) 0);
        Assert.assertFalse(AuthorizationManager.getInstance().isActivityAllowed(userContext, activityContext));
    }

    public void testUpdateFailureWhenNameIsNull() throws Exception {
        RolesPermissionsPersistence persistence = new RolesPermissionsPersistence();
        role = TestObjectFactory.createRole(TestUtils.makeUser(), "New Role", persistence.getActivities());
        setRequestPathInfo("/rolesPermission.do");
        addRequestParameter("method", "manage");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("id", role.getId().toString());
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.manage_success.toString());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));

        setRequestPathInfo("/rolesPermission.do");
        addRequestParameter("method", "update");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("activity(1)", "checkbox");
        addRequestParameter("activity(2)", "3");
        addRequestParameter("activity(3)", "4");
        addRequestParameter("name", "");
        actionPerform();
        verifyActionErrors(new String[] { RolesAndPermissionConstants.KEYROLENAMENOTSPECIFIED });
        verifyForward(ActionForwards.update_failure.toString());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    public void testUpdateFailureWhenNameIsEmpty() throws Exception {
        RolesPermissionsPersistence persistence = new RolesPermissionsPersistence();
        role = TestObjectFactory.createRole(TestUtils.makeUser(), "New Role", persistence.getActivities());
        setRequestPathInfo("/rolesPermission.do");
        addRequestParameter("method", "manage");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("id", role.getId().toString());
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.manage_success.toString());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));

        setRequestPathInfo("/rolesPermission.do");
        addRequestParameter("method", "update");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("name", "");
        addRequestParameter("activity(1)", "checkbox");
        addRequestParameter("activity(2)", "3");
        addRequestParameter("activity(3)", "4");
        actionPerform();
        verifyActionErrors(new String[] { RolesAndPermissionConstants.KEYROLENAMENOTSPECIFIED });
        verifyForward(ActionForwards.update_failure.toString());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    public void testUpdateFailureForDuplicateName() throws Exception {
        RolesPermissionsPersistence persistence = new RolesPermissionsPersistence();
        role = TestObjectFactory.createRole(TestUtils.makeUser(), "New Role", persistence.getActivities());
        setRequestPathInfo("/rolesPermission.do");
        addRequestParameter("method", "manage");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("id", role.getId().toString());
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.manage_success.toString());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));

        setRequestPathInfo("/rolesPermission.do");
        addRequestParameter("method", "update");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("name", "Admin");
        addRequestParameter("activity(1)", "checkbox");
        addRequestParameter("activity(2)", "3");
        addRequestParameter("activity(3)", "4");
        actionPerform();
        verifyActionErrors(new String[] { RolesAndPermissionConstants.KEYROLEALREADYEXIST });
        verifyForward(ActionForwards.update_failure.toString());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    public void testUpdateFailureWhenActivitiesAreNull() throws Exception {
        RolesPermissionsPersistence persistence = new RolesPermissionsPersistence();
        role = TestObjectFactory.createRole(TestUtils.makeUser(), "New Role", persistence.getActivities());
        setRequestPathInfo("/rolesPermission.do");
        addRequestParameter("method", "manage");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("id", role.getId().toString());
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.manage_success.toString());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));

        setRequestPathInfo("/rolesPermission.do");
        addRequestParameter("method", "update");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("name", "New Role");
        actionPerform();
        verifyActionErrors(new String[] { RolesAndPermissionConstants.KEYROLEWITHNOACTIVITIES });
        verifyForward(ActionForwards.update_failure.toString());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    public void testUpdateFailureWhenActivitiesAreEmpty() throws Exception {
        RolesPermissionsPersistence persistence = new RolesPermissionsPersistence();
        role = TestObjectFactory.createRole(TestUtils.makeUser(), "New Role", persistence.getActivities());
        setRequestPathInfo("/rolesPermission.do");
        addRequestParameter("method", "manage");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("id", role.getId().toString());
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.manage_success.toString());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));

        setRequestPathInfo("/rolesPermission.do");
        addRequestParameter("method", "update");
        addRequestParameter("activity(1)", "checkbox");
        addRequestParameter("activity(2)", "");
        addRequestParameter("activity(3)", "");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("name", "New Role");
        actionPerform();
        verifyActionErrors(new String[] { RolesAndPermissionConstants.KEYROLEWITHNOACTIVITIES });
        verifyForward(ActionForwards.update_failure.toString());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    public void testPreview() throws Exception {
        RolesPermissionsPersistence persistence = new RolesPermissionsPersistence();
        role = TestObjectFactory.createRole(TestUtils.makeUser(), "New Role", persistence.getActivities());
        setRequestPathInfo("/rolesPermission.do");
        addRequestParameter("method", "preview");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("id", role.getId().toString());
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.preview_success.toString());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    public void testDelete() throws Exception {
        Short roleId = null;
        RolesPermissionsPersistence rolesPermissionsPersistence = new RolesPermissionsPersistence();
        List<ActivityEntity> activityList = rolesPermissionsPersistence.getActivities();
        ActivityEntity activityEntity_0 = activityList.get(0);
        ActivityEntity activityEntity_1 = activityList.get(1);
        ActivityEntity activityEntity_2 = activityList.get(2);
        ActivityEntity activityEntity_3 = activityList.get(3);
        ActivityEntity activityEntity_4 = activityList.get(4);
        role = TestObjectFactory.createRole(TestUtils.makeUser(), "New Role", activityList);
        roleId = role.getId();
        setRequestPathInfo("/rolesPermission.do");
        addRequestParameter("method", "preview");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("id", role.getId().toString());
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.preview_success.toString());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));

        setRequestPathInfo("/rolesPermission.do");
        addRequestParameter("method", "delete");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.delete_success.toString());
        Assert.assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));

        role = rolesPermissionsPersistence.getRole("New Role");
        Assert.assertNull(role);

        UserContext userContext = TestUtils.makeUser(roleId);
        ActivityContext activityContext = new ActivityContext(activityEntity_0.getId(), (short) 1, (short) 0);
        Assert.assertFalse(AuthorizationManager.getInstance().isActivityAllowed(userContext, activityContext));
        activityContext = new ActivityContext(activityEntity_1.getId(), (short) 1, (short) 0);
        Assert.assertFalse(AuthorizationManager.getInstance().isActivityAllowed(userContext, activityContext));
        activityContext = new ActivityContext(activityEntity_2.getId(), (short) 1, (short) 0);
        Assert.assertFalse(AuthorizationManager.getInstance().isActivityAllowed(userContext, activityContext));
        activityContext = new ActivityContext(activityEntity_3.getId(), (short) 1, (short) 0);
        Assert.assertFalse(AuthorizationManager.getInstance().isActivityAllowed(userContext, activityContext));
        activityContext = new ActivityContext(activityEntity_4.getId(), (short) 1, (short) 0);
        Assert.assertFalse(AuthorizationManager.getInstance().isActivityAllowed(userContext, activityContext));

    }

    public void testDeleteFailure() throws Exception {
        RolesPermissionsPersistence rolesPermissionsPersistence = new RolesPermissionsPersistence();
        RoleBO roleBO = rolesPermissionsPersistence.getRole("Admin");
        setRequestPathInfo("/rolesPermission.do");
        addRequestParameter("method", "preview");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("id", roleBO.getId().toString());
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.preview_success.toString());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));

        setRequestPathInfo("/rolesPermission.do");
        addRequestParameter("method", "delete");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyActionErrors(new String[] { RolesAndPermissionConstants.KEYROLEASSIGNEDTOPERSONNEL });
        verifyForward(ActionForwards.delete_failure.toString());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));

    }

    public void testCancel() {
        setRequestPathInfo("/rolesPermission.do");
        addRequestParameter("method", "cancel");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.cancel_success.toString());
        Assert.assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

}
