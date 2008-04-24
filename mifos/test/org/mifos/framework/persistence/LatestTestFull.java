package org.mifos.framework.persistence;

import static org.junit.Assert.assertEquals;
import static org.mifos.framework.persistence.DatabaseVersionPersistence.FIRST_NUMBERED_VERSION;

import java.sql.ResultSet;

import junit.framework.JUnit4TestAdapter;
import net.sourceforge.mayfly.Database;
import net.sourceforge.mayfly.datastore.DataStore;
import net.sourceforge.mayfly.dump.SqlDumper;

import org.junit.Test;

/*
 * This class runs tests on database upgrade scripts (both SQL
 * based and java based).  It uses the earliest version of the database
 * that supported upgrade scripts as a starting point and will run
 * through all upgrades.  You can expect this to take a long
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
	public void afterLookupValues() throws Exception {
		Database database = new Database(firstNumberedVersion());
		/* A customer will typically add records such as these during customization.  */
		database.execute("insert into LOOKUP_VALUE(LOOKUP_ID, ENTITY_ID, LOOKUP_NAME) " +
			"VALUES(569,19,' ')");
		database.execute("insert into LOOKUP_VALUE_LOCALE(LOCALE_ID, LOOKUP_ID, LOOKUP_VALUE) " +
			"VALUES(1,569,'Martian')");

		upgrade(database.dataStore());
		
		// Assert that custom values have been retained
		ResultSet rs = database.query("select * from lookup_value where lookup_id=569");
		rs.next();
		assertEquals(19, rs.getInt("entity_id"));
		assertEquals(" ", rs.getString("lookup_name"));
		
		rs = database.query("select * from lookup_value_locale where lookup_id=569");
		rs.next();
		assertEquals(1, rs.getInt("locale_id"));
		assertEquals("Martian", rs.getString("lookup_value"));
		rs.close();
	}

	private DataStore upgrade(DataStore current) throws Exception {
		return upgrade(FIRST_NUMBERED_VERSION, current);
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(LatestTestFull.class);
	}

}
