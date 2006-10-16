package org.mifos.application.rolesandpermission.business.service;

import org.mifos.application.rolesandpermission.business.RoleBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestRolesPermissionsBusinessService extends MifosTestCase {

	RolesPermissionsBusinessService rolesPermissionsBusinessService = new RolesPermissionsBusinessService();

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		HibernateUtil.closeSession();
	}

	public void testGetRoles() throws Exception{
		assertEquals(2,rolesPermissionsBusinessService.getRoles().size());
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

	public void testGetActivities() throws Exception{
		assertEquals(177,rolesPermissionsBusinessService.getActivities().size());
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

	public void testGetRoleForGivenId() throws Exception{
		RoleBO role = rolesPermissionsBusinessService.getRole(Short.valueOf("1"));
		assertNotNull(role);
		assertEquals(156,role.getActivities().size());
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
