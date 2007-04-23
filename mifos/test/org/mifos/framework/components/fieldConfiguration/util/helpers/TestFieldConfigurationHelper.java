package org.mifos.framework.components.fieldConfiguration.util.helpers;

import org.mifos.application.ApplicationTestSuite;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.util.helpers.TestObjectFactory;

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
			TestObjectFactory.getUserContext().getPreferredLocale());
		assertEquals("Village",fieldName);
		fieldName=FieldConfigurationHelper.getConfiguredFieldName(
			"Center.SomeField",
			TestObjectFactory.getUserContext().getPreferredLocale());
		assertEquals(null,fieldName);
	}
	
	public void testGetLocalSpecificFieldNames() throws Exception{
		String fieldName=FieldConfigurationHelper.getLocalSpecificFieldNames(
			"Center.Address3",
			TestObjectFactory.getUserContext().getPreferredLocale());
		assertEquals("Village",fieldName);

		fieldName=FieldConfigurationHelper.getLocalSpecificFieldNames(
			"Center.SomeField",
			TestObjectFactory.getUserContext().getPreferredLocale());
		assertEquals("Center.SomeField",fieldName);
	}
	

}
