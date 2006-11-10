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
