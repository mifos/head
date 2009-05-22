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
 
package org.mifos.framework.components.fieldConfiguration.util.helpers;

import org.mifos.application.ApplicationTestSuite;
import org.mifos.application.rolesandpermission.util.helpers.RolesAndPermissionConstants;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;

public class FieldConfigurationHelperIntegrationTest extends MifosIntegrationTest {
	
	public FieldConfigurationHelperIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    /** TODO: These tests only pass when run as part of
	 * {@link ApplicationTestSuite}, not when run by themselves
	 * on a fresh test database.
	 * There must be some state which is left around or set up
	 * by some other part of the test suite.
	 */

	public void testGetConfiguredFieldName() throws Exception{
		String fieldName=FieldConfigurationHelper.getConfiguredFieldName(
			"Center.Address3",
			TestUtils
			.makeUser(RolesAndPermissionConstants.ADMIN_ROLE));
		assertEquals("Village",fieldName);
		fieldName=FieldConfigurationHelper.getConfiguredFieldName(
			"Center.SomeField",
			TestUtils
			.makeUser(RolesAndPermissionConstants.ADMIN_ROLE));
		assertEquals(null,fieldName);
	}
	
	public void testGetLocalSpecificFieldNames() throws Exception{
		String fieldName=FieldConfigurationHelper.getLocalSpecificFieldNames(
			"Center.Address3",
			TestUtils
			.makeUser(RolesAndPermissionConstants.ADMIN_ROLE));
		assertEquals("Village",fieldName);

		fieldName=FieldConfigurationHelper.getLocalSpecificFieldNames(
			"Center.SomeField",
			TestUtils
			.makeUser(RolesAndPermissionConstants.ADMIN_ROLE));
		assertEquals("Center.SomeField",fieldName);
	}

}
