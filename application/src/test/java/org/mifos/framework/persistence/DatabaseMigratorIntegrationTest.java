/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import junit.framework.Assert;
import junitx.framework.StringAssert;

import org.dbunit.Assertion;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mifos.accounts.financial.util.helpers.FinancialInitializer;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.DatabaseSetup;



public class DatabaseMigratorIntegrationTest extends MifosIntegrationTestCase {

    private static java.sql.Connection connection;

    @Before
    public void beforeClass() throws Exception {
        StaticHibernateUtil.initialize();
        connection = StaticHibernateUtil.getSessionTL().connection();
        connection.setAutoCommit(false);

    }

    @AfterClass
     public static void afterClass() throws Exception {
            // Cleaning the database using FK check disabled connection
            // If any one of the test fails or throws error it could lead to
            // multiple failures in other tests during test build
            TestDatabase.createMySQLTestDatabase();
            FinancialInitializer.initialize();
            StaticHibernateUtil.flushAndCloseSession();
        }

    /**
     * Demonstrate the simplest possible non-sequential database upgrade works. For example, upgrading a schema from
     * "1274760000" to "1274761395".
     */

    @Test
    public void testSimpleSQLUpgrade() throws Exception {
        loadNonSeqDatabaseSchema();
        createFooTable(connection);
        IDataSet latestDump = new DatabaseConnection(connection).createDataSet();

        loadNonSeqDatabaseSchema();

        connection.createStatement().execute("drop table  if exists foo");

        SortedMap<Integer, String> upgrades = new TreeMap<Integer, String>();
        upgrades.put(1274760000, DatabaseMigrator.SCRIPT_UPGRADE_TYPE);

        DatabaseMigrator migrator = new DatabaseMigrator(connection, upgrades, "org.mifos.framework.persistence");
        migrator.upgrade();

        IDataSet dump = new DatabaseConnection(connection).createDataSet();
        // check insertion of upgrade id
        ResultSet rs = connection.createStatement().executeQuery(
                "select upgrade_id from applied_upgrades where upgrade_id=" + upgrades.firstKey());
        Assert.assertTrue(rs.next());

        Assertion.assertEquals(latestDump, dump);

    }

    private void createFooTable(Connection connection) throws SQLException {
        connection.createStatement().execute("drop table if exists foo");
        connection.createStatement().execute(
                "CREATE TABLE foo ( " + "FOO_ID INTEGER," + "Description VARCHAR(25),"
                        + "PRIMARY KEY(FOO_ID) ) ENGINE=InnoDB CHARACTER SET utf8 ");

        connection.createStatement().execute("INSERT INTO foo VALUES(1, 'BAR')");

        connection.createStatement().execute("INSERT INTO foo VALUES(2, 'BAZ')");
        connection.commit();
    }

    private void loadNonSeqDatabaseSchema() throws Exception {

        // drop tables
        TestDatabase.dropMySQLDatabase();
        connection.createStatement().execute("drop table if exists foo");
        connection.createStatement().execute("drop table if exists bar");
        connection.createStatement().execute("drop table if exists baz");

        connection.createStatement().execute(
                "create table applied_upgrades(" + "upgrade_id integer)" + "engine=innodb character set utf8;");
        connection.commit();
    }

    @Test
    public void testJavaBasedUpgrade() throws Exception {
        loadNonSeqDatabaseSchema();
        connection.createStatement().execute("drop table if exists baz");
        connection.createStatement().execute(
                "create table baz ( " + "baz_id integer" + ") ENGINE=InnoDB CHARACTER SET utf8 ");
        connection.createStatement().execute("INSERT INTO baz VALUES(1202)");
        connection.commit();
        IDataSet dump1 = new DatabaseConnection(connection).createDataSet();
        ITable expected = dump1.getTable("baz");

        loadNonSeqDatabaseSchema();

        connection.createStatement().execute("drop table  if exists foo");
        SortedMap<Integer, String> upgrades = new TreeMap<Integer, String>();
        upgrades.put(1275913405, DatabaseMigrator.CLASS_UPGRADE_TYPE);

        new DatabaseMigrator(connection, upgrades, "org.mifos.framework.persistence").upgrade();

        IDataSet dump2 = new DatabaseConnection(connection).createDataSet();
        Assertion.assertEquals(expected, dump2.getTable("baz"));
    }

    @Test
    public void testMergedUpgrade() throws Exception {

    }

    @Test
    public void testReallyOldDatabase() throws Exception {
        TestDatabase.dropMySQLDatabase();
        createFooTable(connection);

        DatabaseMigrator migrator = new DatabaseMigrator(connection);
        try {
            migrator.isNSDU(connection);

        } catch (Exception e) {
            StringAssert.assertContains("Database is too old to be upgraded", e.getMessage());
        }

    }

    @Test
    public void testFirstRun() throws Exception {

        DatabaseSetup.executeScript("mifosdroptables.sql", connection);
        connection.createStatement().execute("drop table if exists foo");
        connection.createStatement().execute("drop table if exists bar");
        connection.createStatement().execute("drop table if exists baz");
        connection.commit();

        // load dummy schema containing database_version & applied_upgrades tables
        connection.createStatement().execute(
                "create table database_version(" + "database_version integer)" + "engine=innodb character set utf8;");

        connection.createStatement().execute(
                "create table applied_upgrades(" + "upgrade_id integer)" + "engine=innodb character set utf8;");

        // insert database version
        connection.createStatement().execute("insert into database_version values(253)");
        connection.commit();

        SortedMap<Integer, String> availableUpgrades = new TreeMap<Integer, String>();
        availableUpgrades.put(1275913405, DatabaseMigrator.CLASS_UPGRADE_TYPE);
        availableUpgrades.put(1274760000, DatabaseMigrator.SCRIPT_UPGRADE_TYPE);

        Map<Integer, Integer> legacyUpgradesMap = new HashMap<Integer, Integer>(5);
        legacyUpgradesMap.put(142, 1276821345);
        legacyUpgradesMap.put(153, 1276821391);
        legacyUpgradesMap.put(170, 1276821409);
        legacyUpgradesMap.put(201, 1276821432);
        legacyUpgradesMap.put(253, 1276821600);

        DatabaseMigrator migrator = new DatabaseMigrator(connection, availableUpgrades,
                "org.mifos.framework.persistence");
        migrator.firstRun(legacyUpgradesMap);

        // check appliedUPgrades table contains Unix time-stamps for upgrades
        ResultSet rs = connection.createStatement().executeQuery("select count(*) from applied_upgrades");
        rs.next();
        Assert.assertEquals(rs.getInt(1), 5);

    }


    public void testMethodUpgrade() throws Exception {

        loadNonSeqDatabaseSchema();

        SortedMap<Integer, String> upgrades = new TreeMap<Integer, String>();
        upgrades.put(1277124044, DatabaseMigrator.METHOD_UPGRADE_TYPE);

        DatabaseMigrator migrator = new DatabaseMigrator(connection, upgrades, "org.mifos.framework.persistence");

        migrator.upgrade();

        ResultSet rs = connection.createStatement().executeQuery("select count(*) from foo");
        rs.next();
        Assert.assertEquals(rs.getInt(1), 2);
    }
}
