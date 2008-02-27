package org.mifos.framework.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mifos.framework.persistence.DatabaseVersionPersistence.APPLICATION_VERSION;
import static org.mifos.framework.persistence.DatabaseVersionPersistence.FIRST_NUMBERED_VERSION;

import java.io.File;

import junit.framework.JUnit4TestAdapter;
import net.sourceforge.mayfly.Database;
import net.sourceforge.mayfly.datastore.DataStore;
import net.sourceforge.mayfly.dump.SqlDumper;

import org.junit.Test;

/*
 * This class runs tests on database upgrade and downgrade scripts (both SQL
 * based and java based).  It uses the earliest version of the database
 * that supported upgrade/downgrade scripts as a starting point and will run
 * through all upgrades and downgrades.  You can exptect this to take a long
 * time to run.
 */
public class LatestTestFull extends LatestTestBase {
	
	@Test
	public void realSchema() throws Exception {
		Database database = TestDatabase.makeDatabase();
		loadRealLatest(database);
		assertEquals(DatabaseVersionPersistence.APPLICATION_VERSION,
				version(database));
		String latestDump = new SqlDumper(false).dump(database.dataStore());

		DataStore upgraded = applyRealUpgrades();
		String upgradeDump = new SqlDumper(false).dump(upgraded);
		assertEquals(latestDump, upgradeDump);
	}

	private DataStore applyRealUpgrades() throws Exception {
		Database database = new Database(firstNumberedVersion());

	    TestDatabase.runUpgradeScripts(database.openConnection());
	    return database.dataStore();
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
		return upAndBack(FIRST_NUMBERED_VERSION, current);
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(LatestTestFull.class);
	}

}
