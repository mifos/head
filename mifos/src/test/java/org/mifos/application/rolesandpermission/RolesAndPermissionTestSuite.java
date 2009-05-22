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
 
package org.mifos.application.rolesandpermission;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.application.rolesandpermission.business.ActivityEntityIntegrationTest;
import org.mifos.application.rolesandpermission.business.RoleActivityEntityIntegrationTest;
import org.mifos.application.rolesandpermission.business.RoleBOIntegrationTest;
import org.mifos.application.rolesandpermission.business.service.RolesPermissionsBusinessServiceIntegrationTest;
import org.mifos.application.rolesandpermission.persistence.RolesAndPermissionPersistenceIntegrationTest;
import org.mifos.application.rolesandpermission.struts.RolesPermissionsActionTest;
import org.mifos.application.rolesandpermission.struts.tag.ActivityTagIntegrationTest;
import org.mifos.application.rolesandpermission.utils.helpers.RoleTempleteBuilderIntegrationTest;

public class RolesAndPermissionTestSuite extends TestSuite {

	public RolesAndPermissionTestSuite() throws Exception {
		super();
	}

	public static Test suite() throws Exception {
		TestSuite testSuite = new RolesAndPermissionTestSuite();
		testSuite.addTestSuite(ActivityEntityIntegrationTest.class);
		testSuite.addTestSuite(RoleActivityEntityIntegrationTest.class);
		testSuite.addTestSuite(RoleBOIntegrationTest.class);
		testSuite.addTestSuite(RolesPermissionsBusinessServiceIntegrationTest.class);
		testSuite.addTestSuite(RolesAndPermissionPersistenceIntegrationTest.class);
		testSuite.addTestSuite(RolesPermissionsActionTest.class);
		testSuite.addTestSuite(RoleTempleteBuilderIntegrationTest.class);
		testSuite.addTestSuite(ActivityTagIntegrationTest.class);
		return testSuite;
	}
}
