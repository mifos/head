package org.mifos.framework.components.fieldConfiguration.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.sql.SQLException;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.persistence.Upgrade;


public class AddFieldTest {
	
	@Test
	public void startFromStandardStore() throws Exception {
		TestDatabase database = TestDatabase.makeStandard();
		String start = database.dumpForComparison();

		Upgrade upgrade = upgradeAndCheck(database);
		upgrade.downgrade(database.openConnection());
		String afterUpAndDownGrade = database.dumpForComparison();

		assertEquals(start, afterUpAndDownGrade);
	}

	/*INSERT INTO FIELD_CONFIGURATION(FIELD_CONFIG_ID,FIELD_NAME,ENTITY_ID,MANDATORY_FLAG,HIDDEN_FLAG)
VALUES(74,'AssignClients',1,0,0);

*/
	private Upgrade upgradeAndCheck(TestDatabase database) 
	throws IOException, SQLException, ApplicationException {
		int newId = 203;
		AddField upgrade = new AddField(
			DatabaseVersionPersistence.APPLICATION_VERSION + 1,
			newId,
			"AssignClients",
			EntityType.CLIENT,
			false,
			false);
		upgrade.upgrade(database.openConnection());
		FieldConfigurationEntity fetched = (FieldConfigurationEntity) 
			database.openSession().get(FieldConfigurationEntity.class, newId);
		assertEquals(newId, fetched.getFieldConfigId());
		assertFalse(fetched.isHidden());
		assertFalse(fetched.isMandatory());
		assertEquals(EntityType.CLIENT, fetched.getEntityType());
		assertEquals("AssignClients", fetched.getFieldName());
		
		/* This upgrade doesn't yet have the ability to set the parent.
		 * Looks like we'll probably need that some day (not for 106 upgrade).
		 */
		assertEquals(null, fetched.getParentFieldConfig());
		
		return upgrade;
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(AddFieldTest.class);
	}

}
