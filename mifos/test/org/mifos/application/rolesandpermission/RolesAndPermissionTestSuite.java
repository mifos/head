package org.mifos.application.rolesandpermission;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.application.rolesandpermission.business.ActivityEntityTest;
import org.mifos.application.rolesandpermission.business.TestRoleActivityEntity;
import org.mifos.application.rolesandpermission.business.TestRoleBO;
import org.mifos.application.rolesandpermission.business.service.TestRolesPermissionsBusinessService;
import org.mifos.application.rolesandpermission.persistence.TestRolesAndPermissionPersistence;
import org.mifos.application.rolesandpermission.struts.TestRolesPermissionsAction;
import org.mifos.application.rolesandpermission.struts.tag.TestActivityTag;
import org.mifos.application.rolesandpermission.utils.helpers.RoleTempleteBuilderTest;

public class RolesAndPermissionTestSuite extends TestSuite {

	public RolesAndPermissionTestSuite() throws Exception {
		super();
	}

	public static Test suite() throws Exception {
		TestSuite testSuite = new RolesAndPermissionTestSuite();
		testSuite.addTestSuite(ActivityEntityTest.class);
		testSuite.addTestSuite(TestRoleActivityEntity.class);
		testSuite.addTestSuite(TestRoleBO.class);
		testSuite.addTestSuite(TestRolesPermissionsBusinessService.class);
		testSuite.addTestSuite(TestRolesAndPermissionPersistence.class);
		testSuite.addTestSuite(TestRolesPermissionsAction.class);
		testSuite.addTestSuite(RoleTempleteBuilderTest.class);
		testSuite.addTestSuite(TestActivityTag.class);
		return testSuite;
	}
}
