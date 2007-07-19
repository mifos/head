package org.mifos.framework.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mifos.framework.persistence.DatabaseVersionPersistence.APPLICATION_VERSION;
import static org.mifos.framework.persistence.DatabaseVersionPersistence.FIRST_NUMBERED_VERSION;

import java.io.File;
import java.sql.SQLException;

import junit.framework.JUnit4TestAdapter;
import net.sourceforge.mayfly.Database;
import net.sourceforge.mayfly.datastore.DataStore;
import net.sourceforge.mayfly.dump.SqlDumper;

import org.junit.Test;
import org.mifos.framework.util.helpers.DatabaseSetup;

public class LatestTest {
	
	@Test
	public void simple() throws Exception {
		Database database = TestDatabase.makeDatabase();
		loadLatest(database);
		String latestDump = new SqlDumper().dump(database.dataStore());

		database = TestDatabase.makeDatabase();
		applyUpgrades(database);
		String upgradeDump = new SqlDumper().dump(database.dataStore());
		assertEquals(latestDump, upgradeDump);
	}

	private void applyUpgrades(Database database) {
		database.execute("create table foo(x integer)");
		database.execute("insert into foo(x) values(5)");
		database.execute("alter table foo add column y integer default 7");
	}

	private void loadLatest(Database database) {
		database.execute("create table foo(x integer, y integer default 7)");
		database.execute("insert into foo(x, y) values(5,7)");
	}

	@Test
	public void realSchema() throws Exception {
		Database database = TestDatabase.makeDatabase();
		loadRealLatest(database);
		assertEquals(DatabaseVersionPersistence.APPLICATION_VERSION,
				version(database));
		String latestDump = new SqlDumper().dump(database.dataStore());

		DataStore upgraded = applyRealUpgrades();
		String upgradeDump = new SqlDumper().dump(upgraded);
		assertEquals(latestDump, upgradeDump);
	}

	private int version(Database database) throws SQLException {
		return new DatabaseVersionPersistence(database.openConnection()).read();
	}

	private DataStore applyRealUpgrades() throws Exception {
		Database database = new Database(firstNumberedVersion());

	    TestDatabase.runUpgradeScripts(database.openConnection());
	    return database.dataStore();
	}

	/**
	 * Similar to what we get from {@link DatabaseSetup#getStandardStore()}
	 * but without testdbinsertionscript.sql.
	 */
	private void loadRealLatest(Database database) {
	    DatabaseSetup.executeScript(database, "sql/latest-schema.sql");
	    DatabaseSetup.executeScript(database, "sql/latest-data.sql");
	}

	@Test
	public void dropTables() throws Exception {
		Database database = TestDatabase.makeDatabase();
		String blankDB = new SqlDumper().dump(database.dataStore());
		DatabaseSetup.executeScript(database, "sql/latest-schema.sql");
		DatabaseSetup.executeScript(database, "sql/mifosdroptables.sql");
		String cleanedDB = new SqlDumper().dump(database.dataStore());
		assertEquals(blankDB, cleanedDB);
	}
	
	/**
	 * The idea here is to figure out whether we are dropping tables
	 * in the right order to deal with foreign keys.  I'm not sure
	 * we fully succeed, however.
	 */
	@Test
	public void dropTablesWithData() throws Exception {
		TestDatabase database = TestDatabase.makeStandard();
		DatabaseSetup.executeScript(
			database.openConnection(), "sql/mifosdroptables.sql");
		assertEquals("", database.dumpForComparison());
	}
	
	@Test
	public void downgrades() throws Exception {
		DataStore current = firstNumberedVersion();
		current = upAndBack(current);
	}

	private static DataStore firstNumberedVersion;

	private DataStore firstNumberedVersion() throws Exception {
		if (firstNumberedVersion == null) {
			Database database = TestDatabase.makeDatabase();
			TestDatabase.upgradeToFirstNumberedVersion(database.openConnection());
			firstNumberedVersion = database.dataStore();
		}
		return firstNumberedVersion;
	}

	@Test
	public void noDowngradeWithoutUpgrade() throws Exception {
		for (int version = FIRST_NUMBERED_VERSION;
			version < APPLICATION_VERSION;
			++version) {
			String upgrade = "sql/upgrade_to_" + version + ".sql";
			String downgrade = "sql/downgrade_from_" + version + ".sql";
			if (new File(downgrade).exists()
				&& ! new File(upgrade).exists()) {
				fail("found " + downgrade + " without " + upgrade);
			}
		}
	}
	
	@Test
	public void afterLookupValues() throws Exception {
		Database database = new Database(firstNumberedVersion());
		
		/* A customer will typically add records such as these during
		   customization.  */
		database.execute("insert into " +
			"LOOKUP_VALUE(LOOKUP_ID, ENTITY_ID, LOOKUP_NAME) " +
			"VALUES(569,19,' ')");
		database.execute("insert into " +
			"LOOKUP_VALUE_LOCALE(LOCALE_ID, LOOKUP_ID, LOOKUP_VALUE) " +
			"VALUES(1,569,'Martian')");

		upAndBack(database.dataStore());
	}

	private DataStore upAndBack(DataStore current) throws Exception {
		for (int currentVersion = FIRST_NUMBERED_VERSION; 
			currentVersion < APPLICATION_VERSION;
			++currentVersion) {
			int higherVersion = currentVersion + 1;
			try {
				current = upAndBack(current, higherVersion);
			}
			catch (Exception failure) {
				throw new Exception("Cannot upgrade to " + higherVersion,
					failure);
			}
		}
		return current;
	}

	private DataStore upAndBack(DataStore current, int nextVersion) 
	throws Exception {
		Database database = new Database(current);
		String before = new SqlDumper(false).dump(database.dataStore());

		DatabaseVersionPersistence persistence =
			new FileReadingPersistence(database.openConnection());
		Upgrade upgrade = persistence.findUpgrade(nextVersion);

		upgrade.upgrade(database.openConnection());
		DataStore upgraded = database.dataStore();
		upgrade.downgrade(database.openConnection());
		String after = new SqlDumper(false).dump(database.dataStore());
		assertEquals("for higherVersion=" + nextVersion, before, after);
		
		return upgraded;
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(LatestTest.class);
	}

}
