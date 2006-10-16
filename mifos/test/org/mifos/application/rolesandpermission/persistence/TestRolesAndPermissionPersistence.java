package org.mifos.application.rolesandpermission.persistence;

import java.util.List;

import org.mifos.application.rolesandpermission.business.ActivityEntity;
import org.mifos.application.rolesandpermission.business.RoleBO;
import org.mifos.framework.MifosTestCase;

public class TestRolesAndPermissionPersistence extends MifosTestCase {

	public void testGetRole() throws Exception {
		RoleBO role = new RolesPermissionsPersistence().getRole("Admin");
		assertEquals(156,role.getActivities().size());
	}

	public void testGetActivities() throws Exception{
		List<ActivityEntity> activities = new RolesPermissionsPersistence().getActivities();
		assertEquals(177,activities.size());
	}

	public void testGetRoles() throws Exception{
		List<RoleBO> roles = new RolesPermissionsPersistence().getRoles();
		assertEquals(2,roles.size());
	}

	public void testGetRoleForGivenId() throws Exception{
		RoleBO role = new RolesPermissionsPersistence().getRole(Short.valueOf("1"));
		assertEquals(156,role.getActivities().size());
	}

}
