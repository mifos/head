package org.mifos.framework.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mifos.framework.persistence.DatabaseVersionPersistence.APPLICATION_VERSION;
import static org.mifos.framework.persistence.DatabaseVersionPersistence.LATEST_CHECKPOINT_VERSION;

import java.io.File;

import junit.framework.JUnit4TestAdapter;
import net.sourceforge.mayfly.Database;
import net.sourceforge.mayfly.datastore.DataStore;
import net.sourceforge.mayfly.dump.SqlDumper;

import org.junit.Test;
import org.mifos.framework.util.helpers.DatabaseSetup;

/*
 * This class runs tests on database upgrade and downgrade scripts (both SQL
 * based and java based).  It uses a version of the database referred to as
 * a "checkpoint" as a starting point.  The database checkpoint version that
 * it starts with can be adjusted by updating sql/latest-schema-checkupoint.sql
 * and sql/latest-data-checkupoint.sql with a pair of the corresponding 
 * latest-schema.sql and latest-data.sql files for a given database version.
 * The static variable DatabaseVersionPersistence.LATEST_CHECKPOINT_VERSION 
 * must then be set to the database version number of the latest-xxx.sql files 
 * that have been used to update the latest-xxx-checkpoint.sql files.  
 * This test will run upgrade/downgrade scripts using LATEST_CHECKPOINT_VERSION
 * as a starting point.  In general LATEST_CHECKPOINT_VERSION should be a 
 * database version that is at least 3-5 upgrades ago in order to allow for 
 * fixes to be made to recent upgrades when necessary.
 */

public class LatestTestAfterCheckpoint extends LatestTestBase {
	
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
	public void realSchemaFromCheckpoint() throws Exception {
		Database database = TestDatabase.makeDatabase();
		loadRealLatest(database);
		assertEquals(DatabaseVersionPersistence.APPLICATION_VERSION,
				version(database));
		String latestDump = new SqlDumper(false).dump(database.dataStore());

		DataStore upgraded = applyRealUpgradesFromCheckpoint();
		String upgradeDump = new SqlDumper(false).dump(upgraded);
		assertEquals(latestDump, upgradeDump);
	}

	
	private DataStore applyRealUpgradesFromCheckpoint() throws Exception {
		Database database = new Database(latestCheckpointVersion());

	    TestDatabase.runUpgradeScripts(LATEST_CHECKPOINT_VERSION, database.openConnection());
	    return database.dataStore();
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
	public void downgradesFromCheckpoint() throws Exception {
		DataStore current = latestCheckpointVersion();
		current = upAndBack(LATEST_CHECKPOINT_VERSION, current);
	}
	
	private static DataStore latestCheckpointVersion;

	private DataStore latestCheckpointVersion() throws Exception {
		if (latestCheckpointVersion == null) {
			Database database = TestDatabase.makeDatabase();
			TestDatabase.upgradeLatestCheckpointVersion(database.openConnection());
			latestCheckpointVersion = database.dataStore();
		}
		return latestCheckpointVersion;
	}
	
	@Test
	public void noDowngradeWithoutUpgradeAfterCheckpoint() throws Exception {
		for (int version = LATEST_CHECKPOINT_VERSION;
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
	public void afterLookupValuesAfterCheckpoint() throws Exception {
		Database database = new Database(latestCheckpointVersion());
		
		/* A customer will typically add records such as these during
		   customization.  */
		int lookupId = largestLookupId(database.openConnection());
		int nextLookupId = lookupId + 1;
		database.execute("insert into " +
				"LOOKUP_VALUE(LOOKUP_ID, ENTITY_ID, LOOKUP_NAME) " +
				"VALUES(" + nextLookupId + ", 19,' ')");
		database.execute("insert into " +
				"LOOKUP_VALUE_LOCALE(LOCALE_ID, LOOKUP_ID, LOOKUP_VALUE) " +
				"VALUES(1," + nextLookupId + ",'Martian')");
		
		upAndBack(LATEST_CHECKPOINT_VERSION, database.dataStore());
	}

	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(LatestTestAfterCheckpoint.class);
	}

}
