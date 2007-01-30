package org.mifos.application.rolesandpermission.struts;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mifos.application.rolesandpermission.business.ActivityEntity;
import org.mifos.application.rolesandpermission.business.RoleBO;
import org.mifos.application.rolesandpermission.persistence.RolesPermissionsPersistence;
import org.mifos.application.rolesandpermission.struts.action.RolesPermissionsAction;
import org.mifos.application.rolesandpermission.struts.actionforms.RolesPermissionsActionForm;
import org.mifos.application.rolesandpermission.util.helpers.RolesAndPermissionConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.security.authorization.AuthorizationManager;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestRolesPermissionsAction extends MifosMockStrutsTestCase {

	UserContext userContext = null;

	private String flowKey;

	private RoleBO role=null;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml")
				.getPath());
		setConfigFile(ResourceLoader.getURI(
				"org/mifos/application/rolesandpermission/struts-config.xml")
				.getPath());
		userContext = TestObjectFactory.getUserContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		ActivityContext ac = new ActivityContext((short) 0, userContext
				.getBranchId().shortValue(), userContext.getId().shortValue());
		request.getSession(false).setAttribute("ActivityContext", ac);
		flowKey = createFlow(request, RolesPermissionsAction.class);
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(role);
		super.tearDown();
	}


	public void testViewRoles() throws Exception{
		setRequestPathInfo("/rolesPermission.do");
		addRequestParameter("method", "viewRoles");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.viewRoles_success.toString());
		List<RoleBO> roles=(List<RoleBO>)SessionUtils.getAttribute(RolesAndPermissionConstants.ROLES,request);
		assertEquals(2,roles.size());
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}

	public void testLoad() throws Exception{
		setRequestPathInfo("/rolesPermission.do");
		addRequestParameter("method", "load");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.load_success.toString());
		List<ActivityEntity> activities=(List<ActivityEntity>)SessionUtils.getAttribute(RolesAndPermissionConstants.ACTIVITYLIST,request);
		assertNull(SessionUtils.getAttribute(Constants.BUSINESS_KEY,request));
		assertEquals(181,activities.size());
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}

	public void testCreate() throws Exception{

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
		assertEquals(2,role.getActivities().size());

		UserContext userContext = TestObjectFactory.getUserContext();
		Set<Short> roles=new HashSet<Short>();
		roles.add(role.getId());
		userContext.setRoles(roles);
		ActivityContext activityContext = new ActivityContext((short)3,(short)1,(short)0);
		assertTrue(AuthorizationManager.getInstance().isActivityAllowed(userContext,activityContext));
		activityContext = new ActivityContext((short)4,(short)1,(short)0);
		assertTrue(AuthorizationManager.getInstance().isActivityAllowed(userContext,activityContext));
		activityContext = new ActivityContext((short)5,(short)1,(short)0);
		assertFalse(AuthorizationManager.getInstance().isActivityAllowed(userContext,activityContext));

	}

	public void testCreateFailureWhenNameIsNull() throws Exception{
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

	public void testCreateFailureWhenNameIsEmpty() throws Exception{
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

	public void testCreateFailureActivitiesAreNull() throws Exception{
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

	public void testCreateFailureActivitiesAreEmpty() throws Exception{
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

	public void testManage() throws Exception{
		RolesPermissionsPersistence rolesPermissionsPersistence = new RolesPermissionsPersistence();
		role=TestObjectFactory.createRole(TestObjectFactory.getUserContext(),"New Role",rolesPermissionsPersistence.getActivities());
		setRequestPathInfo("/rolesPermission.do");
		addRequestParameter("method", "manage");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter("id", role.getId().toString());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.manage_success.toString());
		assertEquals("New Role",((RolesPermissionsActionForm)request.getSession().getAttribute("rolesPermissionsActionForm")).getName());
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}

	public void testUpdateSuccess() throws Exception{
		RolesPermissionsPersistence rolesPermissionsPersistence = new RolesPermissionsPersistence();
		role=TestObjectFactory.createRole(TestObjectFactory.getUserContext(),"New Role",rolesPermissionsPersistence.getActivities());
		setRequestPathInfo("/rolesPermission.do");
		addRequestParameter("method", "manage");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter("id", role.getId().toString());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.manage_success.toString());
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));

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
		assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));

		role=rolesPermissionsPersistence.getRole(role.getId());
		assertEquals(2,role.getActivities().size());

		UserContext userContext = TestObjectFactory.getUserContext();
		Set<Short> roles=new HashSet<Short>();
		roles.add(role.getId());
		userContext.setRoles(roles);
		ActivityContext activityContext = new ActivityContext((short)3,(short)1,(short)0);
		assertTrue(AuthorizationManager.getInstance().isActivityAllowed(userContext,activityContext));
		activityContext = new ActivityContext((short)4,(short)1,(short)0);
		assertTrue(AuthorizationManager.getInstance().isActivityAllowed(userContext,activityContext));
		activityContext = new ActivityContext((short)5,(short)1,(short)0);
		assertFalse(AuthorizationManager.getInstance().isActivityAllowed(userContext,activityContext));

	}

	public void testUpdateFailureWhenNameIsNull() throws Exception{
		RolesPermissionsPersistence rolesPermissionsPersistence = new RolesPermissionsPersistence();
		role=TestObjectFactory.createRole(TestObjectFactory.getUserContext(),"New Role",rolesPermissionsPersistence.getActivities());
		setRequestPathInfo("/rolesPermission.do");
		addRequestParameter("method", "manage");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter("id", role.getId().toString());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.manage_success.toString());
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));

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
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}

	public void testUpdateFailureWhenNameIsEmpty() throws Exception{
		RolesPermissionsPersistence rolesPermissionsPersistence = new RolesPermissionsPersistence();
		role=TestObjectFactory.createRole(TestObjectFactory.getUserContext(),"New Role",rolesPermissionsPersistence.getActivities());
		setRequestPathInfo("/rolesPermission.do");
		addRequestParameter("method", "manage");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter("id", role.getId().toString());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.manage_success.toString());
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));

		setRequestPathInfo("/rolesPermission.do");
		addRequestParameter("method", "update");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter("name","");
		addRequestParameter("activity(1)", "checkbox");
		addRequestParameter("activity(2)", "3");
		addRequestParameter("activity(3)", "4");
		actionPerform();
		verifyActionErrors(new String[] { RolesAndPermissionConstants.KEYROLENAMENOTSPECIFIED });
		verifyForward(ActionForwards.update_failure.toString());
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}

	public void testUpdateFailureForDuplicateName() throws Exception{
		RolesPermissionsPersistence rolesPermissionsPersistence = new RolesPermissionsPersistence();
		role=TestObjectFactory.createRole(TestObjectFactory.getUserContext(),"New Role",rolesPermissionsPersistence.getActivities());
		setRequestPathInfo("/rolesPermission.do");
		addRequestParameter("method", "manage");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter("id", role.getId().toString());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.manage_success.toString());
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));

		setRequestPathInfo("/rolesPermission.do");
		addRequestParameter("method", "update");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter("name","Admin");
		addRequestParameter("activity(1)", "checkbox");
		addRequestParameter("activity(2)", "3");
		addRequestParameter("activity(3)", "4");
		actionPerform();
		verifyActionErrors(new String[] { RolesAndPermissionConstants.KEYROLEALREADYEXIST });
		verifyForward(ActionForwards.update_failure.toString());
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}

	public void testUpdateFailureWhenActivitiesAreNull() throws Exception{
		RolesPermissionsPersistence rolesPermissionsPersistence = new RolesPermissionsPersistence();
		role=TestObjectFactory.createRole(TestObjectFactory.getUserContext(),"New Role",rolesPermissionsPersistence.getActivities());
		setRequestPathInfo("/rolesPermission.do");
		addRequestParameter("method", "manage");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter("id", role.getId().toString());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.manage_success.toString());
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));

		setRequestPathInfo("/rolesPermission.do");
		addRequestParameter("method", "update");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter("name","New Role");
		actionPerform();
		verifyActionErrors(new String[] { RolesAndPermissionConstants.KEYROLEWITHNOACTIVITIES });
		verifyForward(ActionForwards.update_failure.toString());
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}

	public void testUpdateFailureWhenActivitiesAreEmpty() throws Exception{
		RolesPermissionsPersistence rolesPermissionsPersistence = new RolesPermissionsPersistence();
		role=TestObjectFactory.createRole(TestObjectFactory.getUserContext(),"New Role",rolesPermissionsPersistence.getActivities());
		setRequestPathInfo("/rolesPermission.do");
		addRequestParameter("method", "manage");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter("id", role.getId().toString());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.manage_success.toString());
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));

		setRequestPathInfo("/rolesPermission.do");
		addRequestParameter("method", "update");
		addRequestParameter("activity(1)", "checkbox");
		addRequestParameter("activity(2)", "");
		addRequestParameter("activity(3)", "");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter("name","New Role");
		actionPerform();
		verifyActionErrors(new String[] { RolesAndPermissionConstants.KEYROLEWITHNOACTIVITIES });
		verifyForward(ActionForwards.update_failure.toString());
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}

	public void testPreview() throws Exception{
		RolesPermissionsPersistence rolesPermissionsPersistence = new RolesPermissionsPersistence();
		role=TestObjectFactory.createRole(TestObjectFactory.getUserContext(),"New Role",rolesPermissionsPersistence.getActivities());
		setRequestPathInfo("/rolesPermission.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter("id", role.getId().toString());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.preview_success.toString());
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}

	public void testDelete() throws Exception{
		Short roleId = null;
		RolesPermissionsPersistence rolesPermissionsPersistence = new RolesPermissionsPersistence();
		List<ActivityEntity> activityList=rolesPermissionsPersistence.getActivities();
		ActivityEntity activityEntity_0 = activityList.get(0);
		ActivityEntity activityEntity_1 = activityList.get(1);
		ActivityEntity activityEntity_2 = activityList.get(2);
		ActivityEntity activityEntity_3 = activityList.get(3);
		ActivityEntity activityEntity_4 = activityList.get(4);
		role=TestObjectFactory.createRole(TestObjectFactory.getUserContext(),"New Role",activityList);
		roleId=role.getId();
		setRequestPathInfo("/rolesPermission.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter("id", role.getId().toString());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.preview_success.toString());
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));

		setRequestPathInfo("/rolesPermission.do");
		addRequestParameter("method", "delete");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.delete_success.toString());
		assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));

		role=rolesPermissionsPersistence.getRole("New Role");
		assertNull(role);


		UserContext userContext = TestObjectFactory.getUserContext();
		Set<Short> roles=new HashSet<Short>();
		roles.add(roleId);
		userContext.setRoles(roles);
		ActivityContext activityContext = new ActivityContext(activityEntity_0.getId(),(short)1,(short)0);
		assertFalse(AuthorizationManager.getInstance().isActivityAllowed(userContext,activityContext));
		activityContext = new ActivityContext(activityEntity_1.getId(),(short)1,(short)0);
		assertFalse(AuthorizationManager.getInstance().isActivityAllowed(userContext,activityContext));
		activityContext = new ActivityContext(activityEntity_2.getId(),(short)1,(short)0);
		assertFalse(AuthorizationManager.getInstance().isActivityAllowed(userContext,activityContext));
		activityContext = new ActivityContext(activityEntity_3.getId(),(short)1,(short)0);
		assertFalse(AuthorizationManager.getInstance().isActivityAllowed(userContext,activityContext));
		activityContext = new ActivityContext(activityEntity_4.getId(),(short)1,(short)0);
		assertFalse(AuthorizationManager.getInstance().isActivityAllowed(userContext,activityContext));

	}

	public void testDeleteFailure() throws Exception{
		RolesPermissionsPersistence rolesPermissionsPersistence = new RolesPermissionsPersistence();
		RoleBO roleBO=rolesPermissionsPersistence.getRole("Admin");
		setRequestPathInfo("/rolesPermission.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter("id", roleBO.getId().toString());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.preview_success.toString());
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));

		setRequestPathInfo("/rolesPermission.do");
		addRequestParameter("method", "delete");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyActionErrors(new String[] { RolesAndPermissionConstants.KEYROLEASSIGNEDTOPERSONNEL });
		verifyForward(ActionForwards.delete_failure.toString());
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));

	}


	public void testCancel(){
		setRequestPathInfo("/rolesPermission.do");
		addRequestParameter("method", "cancel");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.cancel_success.toString());
		assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}



}
