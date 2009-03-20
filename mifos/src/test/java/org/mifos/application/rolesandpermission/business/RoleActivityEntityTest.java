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
 
package org.mifos.application.rolesandpermission.business;

import junit.framework.TestCase;

import org.mifos.framework.TestUtils;
import org.mifos.framework.components.logger.TestLogger;

public class RoleActivityEntityTest extends TestCase {
	
	public void testEquals() throws Exception {
		RoleBO role77 = new RoleBO(new TestLogger(), 77);
		RoleBO another77 = new RoleBO(new TestLogger(), 77);
		RoleBO role78 = new RoleBO(new TestLogger(), 78);
		
		ActivityEntity activity66 = new ActivityEntity(66);
		
		TestUtils.assertAllEqual(
			new Object[] {
				new RoleActivityEntity(role77, activity66),
				new RoleActivityEntity(role77, activity66),
				new RoleActivityEntity(another77, activity66)
			});
		TestUtils.assertIsNotEqual(
			new RoleActivityEntity(role77, activity66),
			new RoleActivityEntity(role78, activity66));
		TestUtils.assertIsNotEqual(
			new RoleActivityEntity(role77, activity66),
			"object of some other type");
	}

}
