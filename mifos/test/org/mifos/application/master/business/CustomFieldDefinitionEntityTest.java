package org.mifos.application.master.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.ApplicationInitializer;

public class CustomFieldDefinitionEntityTest {
	@BeforeClass
	public static void init() throws Exception {
		ApplicationInitializer.initializeSpring();
	}

	@Test
	public void testMandatory() {
		MifosLookUpEntity customFieldName = new MifosLookUpEntity();
		customFieldName.setEntityId((short)1);
		customFieldName.setEntityType("something");

		CustomFieldDefinitionEntity customFieldNotMandatory = new CustomFieldDefinitionEntity(
				customFieldName, (short)0, CustomFieldType.ALPHA_NUMERIC, EntityType.CLIENT,
				"default value", YesNoFlag.NO);
		CustomFieldDefinitionEntity customFieldMandatory = new CustomFieldDefinitionEntity(
				customFieldName, (short)0, CustomFieldType.ALPHA_NUMERIC, EntityType.CLIENT,
				"default value", YesNoFlag.YES);

		assertFalse(customFieldNotMandatory.isMandatory());
		assertEquals(customFieldNotMandatory.getMandatoryStringValue(),"no");
		
		assertTrue(customFieldMandatory.isMandatory());
		assertEquals(customFieldMandatory.getMandatoryStringValue(),"yes");		
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(CustomFieldDefinitionEntityTest.class);
	}
	
}
