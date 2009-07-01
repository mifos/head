/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.framework.persistence;

import static org.mifos.framework.persistence.DatabaseVersionPersistence.LATEST_CHECKPOINT_VERSION;

import java.sql.ResultSet;

import net.sourceforge.mayfly.Database;
import net.sourceforge.mayfly.datastore.DataStore;
import net.sourceforge.mayfly.dump.SqlDumper;

import org.mifos.framework.util.helpers.DatabaseSetup;

/*
 * This class runs tests on database upgrade scripts (both SQL
 * based and java based).  It uses a version of the database referred to as
 * a "checkpoint" as a starting point.  The database checkpoint version that
 * it starts with can be adjusted by updating sql/latest-schema-checkupoint.sql
 * and sql/latest-data-checkupoint.sql with a pair of the corresponding 
 * latest-schema.sql and latest-data.sql files for a given database version.
 * The static variable DatabaseVersionPersistence.LATEST_CHECKPOINT_VERSION 
 * must then be set to the database version number of the latest-xxx.sql files 
 * that have been used to update the latest-xxx-checkpoint.sql files.  
 * This test will run upgrade scripts using LATEST_CHECKPOINT_VERSION
 * as a starting point.  In general LATEST_CHECKPOINT_VERSION should be a 
 * database version that is at least 3-5 upgrades ago in order to allow for 
 * fixes to be made to recent upgrades when necessary.
 */

public class LatestTestAfterCheckpointBaseTest extends LatestBaseTest {

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

    public void testRealSchemaFromCheckpoint() throws Exception {
        Database database = TestDatabase.makeDatabase();
        loadRealLatest(database);
        assertEquals(DatabaseVersionPersistence.APPLICATION_VERSION, version(database));
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

    public void testDropTables() throws Exception {
        Database database = TestDatabase.makeDatabase();
        String blankDB = new SqlDumper().dump(database.dataStore());
        DatabaseSetup.executeScript(database, "latest-schema.sql");
        DatabaseSetup.executeScript(database, "mifosdroptables.sql");
        String cleanedDB = new SqlDumper().dump(database.dataStore());
        assertEquals(blankDB, cleanedDB);
    }

    /**
     * The idea here is to figure out whether we are dropping tables in the
     * right order to deal with foreign keys. I'm not sure we fully succeed,
     * however.
     */
    public void testDropTablesWithData() throws Exception {
        TestDatabase database = TestDatabase.makeStandard();
        DatabaseSetup.executeScript(database.openConnection(), "mifosdroptables.sql");
        assertEquals("", database.dumpForComparison());
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

    public void testAfterLookupValuesAfterCheckpoint() throws Exception {
        Database database = new Database(latestCheckpointVersion());

        int nextLookupId = largestLookupId(database.openConnection()) + 1;
        database.execute("insert into LOOKUP_VALUE(LOOKUP_ID, ENTITY_ID, LOOKUP_NAME) " + "VALUES(" + nextLookupId
                + ", 19,' ')");
        database.execute("insert into LOOKUP_VALUE_LOCALE(LOCALE_ID, LOOKUP_ID, LOOKUP_VALUE) " + "VALUES(1,"
                + nextLookupId + ",'Martian')");

        upgrade(LATEST_CHECKPOINT_VERSION, database.dataStore());

        // Assert that custom values have been retained
        ResultSet rs = database.query("select * from lookup_value where lookup_id=" + nextLookupId);
        rs.next();
        assertEquals(19, rs.getInt("entity_id"));
        assertEquals(" ", rs.getString("lookup_name"));

        rs = database.query("select * from lookup_value_locale where lookup_id=" + nextLookupId);
        rs.next();
        assertEquals(1, rs.getInt("locale_id"));
        assertEquals("Martian", rs.getString("lookup_value"));
        rs.close();
    }
}
