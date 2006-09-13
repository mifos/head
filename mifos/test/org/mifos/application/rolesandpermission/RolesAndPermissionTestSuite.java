package org.mifos.application.rolesandpermission;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.application.rolesandpermission.business.TestActivityEntity;
import org.mifos.application.rolesandpermission.business.TestRoleActivityEntity;
import org.mifos.application.rolesandpermission.business.TestRoleBO;
import org.mifos.framework.MifosTestSuite;

public class RolesAndPermissionTestSuite extends MifosTestSuite {

	
	public RolesAndPermissionTestSuite() throws Exception {
		super();
	}

	public static Test suite() throws Exception {
		TestSuite testSuite = new RolesAndPermissionTestSuite();
		testSuite.addTestSuite(TestActivityEntity.class);
		testSuite.addTestSuite(TestRoleActivityEntity.class);
		testSuite.addTestSuite(TestRoleBO.class);
		return testSuite;
	}
}
