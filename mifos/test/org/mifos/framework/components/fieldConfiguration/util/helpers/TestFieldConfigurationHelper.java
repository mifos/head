package org.mifos.framework.components.fieldConfiguration.util.helpers;

import org.mifos.framework.MifosTestCase;
import org.mifos.framework.util.helpers.TestObjectFactory;


public class TestFieldConfigurationHelper extends MifosTestCase {
	
	public void testGetConfiguredFieldName() throws Exception{
		String fieldName=FieldConfigurationHelper.getConfiguredFieldName("Center.Address3",TestObjectFactory.getUserContext().getPreferredLocale());
		assertEquals("Village",fieldName);
		fieldName=FieldConfigurationHelper.getConfiguredFieldName("Center.SomeField",TestObjectFactory.getUserContext().getPreferredLocale());
		assertEquals(null,fieldName);
	}
	
	public void testGetLocalSpecificFieldNames() throws Exception{
		String fieldName=FieldConfigurationHelper.getLocalSpecificFieldNames("Center.Address3",TestObjectFactory.getUserContext().getPreferredLocale());
		assertEquals("Village",fieldName);
		fieldName=FieldConfigurationHelper.getLocalSpecificFieldNames("Center.SomeField",TestObjectFactory.getUserContext().getPreferredLocale());
		assertEquals("Center.SomeField",fieldName);
	}
	

}
