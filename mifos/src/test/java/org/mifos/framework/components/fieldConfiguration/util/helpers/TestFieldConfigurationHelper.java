package org.mifos.framework.components.fieldConfiguration.util.helpers;

import org.mifos.application.ApplicationTestSuite;
import org.mifos.application.rolesandpermission.util.helpers.RolesAndPermissionConstants;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;

public class TestFieldConfigurationHelper extends MifosTestCase {
	
	public TestFieldConfigurationHelper() throws SystemException, ApplicationException {
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
