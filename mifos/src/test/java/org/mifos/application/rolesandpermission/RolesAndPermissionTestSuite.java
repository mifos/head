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
