package org.mifos.framework.components.fieldConfiguration.util.helpers;

import org.mifos.application.ApplicationTestSuite;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.TestUtils;

public class TestFieldConfigurationHelper extends MifosTestCase {
	
	/** TODO: These tests only pass when run as part of
	 * {@link ApplicationTestSuite}, not when run by themselves
	 * on a fresh test database.
	 * There must be some state which is left around or set up
	 * by some other part of the test suite.
	 */

	public void testGetConfiguredFieldName() throws Exception{
		String fieldName=FieldConfigurationHelper.getConfiguredFieldName(
			"Center.Address3",
			TestUtils.ukLocale());
		assertEquals("Village",fieldName);
		fieldName=FieldConfigurationHelper.getConfiguredFieldName(
			"Center.SomeField",
			TestUtils.ukLocale());
		assertEquals(null,fieldName);
	}
	
	public void testGetLocalSpecificFieldNames() throws Exception{
		String fieldName=FieldConfigurationHelper.getLocalSpecificFieldNames(
			"Center.Address3",
			TestUtils.ukLocale());
		assertEquals("Village",fieldName);

		fieldName=FieldConfigurationHelper.getLocalSpecificFieldNames(
			"Center.SomeField",
			TestUtils.ukLocale());
		assertEquals("Center.SomeField",fieldName);
	}

}
