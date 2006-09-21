package org.mifos.application.rolesandpermission.business.service;

import org.mifos.application.rolesandpermission.business.RoleBO;
import org.mifos.framework.MifosTestCase;

public class TestRolesPermissionsBusinessService extends MifosTestCase {
	
	RolesPermissionsBusinessService rolesPermissionsBusinessService = new RolesPermissionsBusinessService();
	
	public void testGetRoles() throws Exception{
		assertEquals(2,rolesPermissionsBusinessService.getRoles().size());
	}

	public void testGetActivities() throws Exception{
		assertEquals(175,rolesPermissionsBusinessService.getActivities().size());
	}
	
	public void testGetRoleForGivenId() throws Exception{
		RoleBO role = rolesPermissionsBusinessService.getRole(Short.valueOf("1"));
		assertNotNull(role);
		assertEquals(155,role.getActivities().size());
	}
	
}
