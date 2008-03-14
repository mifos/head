package org.mifos.framework.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import junit.framework.JUnit4TestAdapter;
import net.sourceforge.mayfly.Database;

import org.junit.Test;

public class UpgradeTest {
	
	@Test public void incrementVersion() throws Exception {
		Database database = DummyUpgrade.databaseWithVersionTable(53);
		new DummyUpgrade(54).upgradeVersion(database.openConnection());
		assertEquals(54, 
			new DatabaseVersionPersistence(database.openConnection()).read()
		);
	}
	
	@Test public void notReadyToIncrement() throws Exception {
		Database database = DummyUpgrade.databaseWithVersionTable(53);
		new DummyUpgrade(55).upgradeVersion(database.openConnection());
		assertEquals(53, 
			new DatabaseVersionPersistence(database.openConnection()).read()
		);
	}
	
	@Test public void decrementVersion() throws Exception {
		Database database = DummyUpgrade.databaseWithVersionTable(54);
		new DummyUpgrade(54).downgradeVersion(database.openConnection());
		assertEquals(53, 
			new DatabaseVersionPersistence(database.openConnection()).read()
		);
	}
	
	@Test public void notReadyToDecrement() throws Exception {
		Database database = DummyUpgrade.databaseWithVersionTable(54);
		new DummyUpgrade(55).downgradeVersion(database.openConnection());
		assertEquals(54, 
			new DatabaseVersionPersistence(database.openConnection()).read()
		);
	}
	
	@Test 
	public void validateLookupValueKeyTest() throws Exception {
		String validKey = "Permissions-Groups-CanBlacklistAGroup";
		String format = "Permissions-";
		assertTrue(DummyUpgrade.validateLookupValueKey(format, validKey));
		String invalidKey = "Groups-CanBlacklistAGroup";
		assertFalse(DummyUpgrade.validateLookupValueKey(format, invalidKey));
		invalidKey = null;
		assertFalse(DummyUpgrade.validateLookupValueKey(format, invalidKey));
		invalidKey = "";
		assertFalse(DummyUpgrade.validateLookupValueKey(format, invalidKey));
	}
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(UpgradeTest.class);
	}

}
