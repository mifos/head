package org.mifos.framework.persistence;

import static org.mifos.framework.persistence.DatabaseVersionPersistence.APPLICATION_VERSION;
import static org.mifos.framework.persistence.DatabaseVersionPersistence.FIRST_NUMBERED_VERSION;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import junit.framework.TestCase;
import net.sourceforge.mayfly.Database;
import net.sourceforge.mayfly.datastore.DataStore;
import net.sourceforge.mayfly.dump.SqlDumper;

import org.mifos.framework.TestDatabase;
import org.mifos.framework.util.helpers.DatabaseSetup;

public class LatestTest extends TestCase {
	
	public void testSimple() throws Exception {
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
	
	public void testRealSchema() throws Exception {
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
		Database database = new Database(upgradeToFirstNumberedVersion());

	    runUpgradeScripts(database);
	    return database.dataStore();
	}

	private DataStore upgradeToFirstNumberedVersion() {
		Database database = TestDatabase.makeDatabase();
		DatabaseSetup.executeScript(database, "sql/mifosdbcreationscript.sql");
	    DatabaseSetup.executeScript(database, "sql/mifosmasterdata.sql");
	    DatabaseSetup.executeScript(database, "sql/rmpdbcreationscript.sql");
	    DatabaseSetup.executeScript(database, "sql/rmpmasterdata.sql");
	    DatabaseSetup.executeScript(database, "sql/Iteration13-DBScripts25092006.sql");
	    DatabaseSetup.executeScript(database, "sql/Iteration14-DDL-DBScripts10102006.sql");
	    DatabaseSetup.executeScript(database, "sql/Iteration14-DML-DBScripts10102006.sql");
	    DatabaseSetup.executeScript(database, "sql/Iteration15-DDL-DBScripts24102006.sql");
	    DatabaseSetup.executeScript(database, "sql/Iteration15-DBScripts20061012.sql");
	    DatabaseSetup.executeScript(database, "sql/add-version.sql");
	    DatabaseSetup.executeScript(database, "sql/Index.sql");
	    return database.dataStore();
	}

	private void runUpgradeScripts(Database database) 
	throws SQLException, IOException {
	    Connection conn = database.openConnection();
	    DatabaseVersionPersistence persistence = 
	    	new FileReadingPersistence(conn);	    
	    int version  = persistence.read();
	    assertEquals(FIRST_NUMBERED_VERSION, version);
	    List<Upgrade> scripts = persistence.scripts(
	    	APPLICATION_VERSION, version);
	    persistence.execute(scripts, conn);
	}

	/**
	 * Similar to what we get from {@link DatabaseSetup#getStandardStore()}
	 * but without testdbinsertionscript.sql.
	 */
	private void loadRealLatest(Database database) {
	    DatabaseSetup.executeScript(database, "sql/latest-schema.sql");
	    DatabaseSetup.executeScript(database, "sql/latest-data.sql");
	}

	public void testDropTables() throws Exception {
		Database database = TestDatabase.makeDatabase();
		String blankDB = new SqlDumper().dump(database.dataStore());
		DatabaseSetup.executeScript(database, "sql/latest-schema.sql");
		DatabaseSetup.executeScript(database, "sql/mifosdroptables.sql");
		String cleanedDB = new SqlDumper().dump(database.dataStore());
		assertEquals(blankDB, cleanedDB);
	}
	
	public void testDowngrades() throws Exception {
		DataStore current = this.upgradeToFirstNumberedVersion();
		for (int currentVersion = FIRST_NUMBERED_VERSION; 
			currentVersion < APPLICATION_VERSION;
			++currentVersion) {
			current = upAndBack(current, currentVersion + 1);
		}
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
		assertEquals(before, after);
		
		return upgraded;
	}

}
