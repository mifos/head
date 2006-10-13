package org.mifos.framework.components.fieldConfiguration.util.helpers;

import org.mifos.framework.MifosTestCase;
import org.mifos.framework.util.helpers.TestObjectFactory;


public class TestFieldConfigurationHelper extends MifosTestCase {
	
	public void testGetConfiguredFieldName() throws Exception{
		String fieldName=FieldConfigurationHelper.getConfiguredFieldName("Center.Address3",TestObjectFactory.getUserContext().getPereferedLocale());
		assertEquals("Village",fieldName);
		fieldName=FieldConfigurationHelper.getConfiguredFieldName("Center.SomeField",TestObjectFactory.getUserContext().getPereferedLocale());
		assertEquals(null,fieldName);
	}
	
	public void testGetLocalSpecificFieldNames() throws Exception{
		String fieldName=FieldConfigurationHelper.getLocalSpecificFieldNames("Center.Address3",TestObjectFactory.getUserContext().getPereferedLocale());
		assertEquals("Village",fieldName);
		fieldName=FieldConfigurationHelper.getLocalSpecificFieldNames("Center.SomeField",TestObjectFactory.getUserContext().getPereferedLocale());
		assertEquals("Center.SomeField",fieldName);
	}
	

}
