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

import static org.mifos.framework.util.helpers.DatabaseSetup.executeScript;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.Assert;

import org.dbunit.Assertion;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.junit.Before;
import org.junit.Test;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

/**
 * This class runs tests on database upgrade scripts (both SQL based and java based). It uses a version of the database
 * referred to as a "checkpoint" as a starting point. The database checkpoint version that it starts with can be
 * adjusted by updating sql/latest-schema-checkupoint.sql and sql/latest-data-checkupoint.sql with a pair of the
 * corresponding latest-schema.sql and latest-data.sql files for a given database version. The static variable
 * DatabaseVersionPersistence.LATEST_CHECKPOINT_VERSION must then be set to the database version number of the
 * latest-xxx.sql files that have been used to update the latest-xxx-checkpoint.sql files. This test will run upgrade
 * scripts using LATEST_CHECKPOINT_VERSION as a starting point. In general LATEST_CHECKPOINT_VERSION should be a
 * database version that is at least 3-5 upgrades ago in order to allow for fixes to be made to recent upgrades when
 * necessary.
 */
public class LatestTestAfterCheckpointIntegrationTest extends MifosIntegrationTestCase {

    private static Connection connection;
    private static DatabaseConnection dbUnitConnection;

    @Before
    public void setUp() throws Exception {
        connection = StaticHibernateUtil.getSessionTL().connection();
        dbUnitConnection = new DatabaseConnection(connection);
    }

    @Test
    public void testSimple() throws Exception {
        loadLatest();
        IDataSet latestDump = dbUnitConnection.createDataSet();
        connection.createStatement().execute("drop table database_upgrade_test");
        applyUpgrades();
        IDataSet upgradeDump = dbUnitConnection.createDataSet();
        Assertion.assertEquals(latestDump, upgradeDump);
    }

    @Test
    public void testRealSchemaFromCheckpoint() throws Exception {
        String tmpDir = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator");

        dropLatestDatabase();
        String blankDB = TestDatabase.getAllTablesStructureDump();
        createLatestDatabaseWithLatestData();

        // FIXME for some reason the comparison of DatabaseDataSet (IDataSet) doesn't expose the difference at assert in
        // datasets FlatXmlDataSet seems to work here.
        IDataSet latestDataDump = dbUnitConnection.createDataSet();
        FlatXmlDataSet.write(latestDataDump, new FileOutputStream(tmpDir + "latestDataDump.xml"));
        latestDataDump = new FlatXmlDataSet(new File(tmpDir + "latestDataDump.xml"));
        final String latestSchemaDump = TestDatabase.getAllTablesStructureDump();
        new FileOutputStream(tmpDir + "latestSchemaDump").write(latestSchemaDump.getBytes());

        dropLatestDatabase();
        String cleanedDB = TestDatabase.getAllTablesStructureDump();
        /**
         * The idea here is to figure out whether we are dropping tables in the right order to deal with foreign keys. I'm
         * not sure we fully succeed, however.
         */
        Assert.assertEquals(blankDB, cleanedDB);
        createLatestCheckPointDatabaseWithLatestData();

        DatabaseMigrator migrator = new DatabaseMigrator(connection);
        migrator.upgrade();

        String upgradeSchemaDump = TestDatabase.getAllTablesStructureDump();
        new FileOutputStream(tmpDir + "upgradeSchemaDump").write(upgradeSchemaDump.getBytes());
        IDataSet upgradeDataDump = dbUnitConnection.createDataSet();
        FlatXmlDataSet.write(upgradeDataDump, new FileOutputStream(tmpDir + "upgradeDataDump.xml"));
        upgradeDataDump = new FlatXmlDataSet(new File(tmpDir + "upgradeDataDump.xml"));

        Assert.assertEquals("DB Schema (latest & upgraded) do not match", latestSchemaDump, upgradeSchemaDump);
        Assertion.assertEquals(latestDataDump, upgradeDataDump);


        int nextLookupId = largestLookupId() + 1;
        connection.createStatement().execute(
                "insert into lookup_value(lookup_id, entity_id, lookup_name) " + "values(" + nextLookupId
                        + ", 19,'TestLookUpName')");
        connection.createStatement().execute(
                "insert into lookup_value_locale(locale_id, lookup_id, lookup_value) " + "values(1," + nextLookupId
                        + ",'Martian')");


        migrator = new DatabaseMigrator(connection);
        migrator.upgrade();

        ResultSet rs = connection.createStatement().executeQuery(
                "select * from lookup_value where lookup_id=" + nextLookupId);
        rs.next();
        Assert.assertEquals(19, rs.getInt("entity_id"));
        Assert.assertEquals("TestLookUpName", rs.getString("lookup_name"));

        rs = connection.createStatement().executeQuery(
                "select * from lookup_value_locale where lookup_id=" + nextLookupId);
        rs.next();
        Assert.assertEquals(1, rs.getInt("locale_id"));
        Assert.assertEquals("Martian", rs.getString("lookup_value"));
        rs.close();
    }

    private int largestLookupId() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet results = statement.executeQuery("select max(lookup_id) from lookup_value");
        if (!results.next()) {
            throw new SystemException(SystemException.DEFAULT_KEY,
                    "Did not find an existing lookup_id in lookup_value table");
        }
        int largestLookupId = results.getInt(1);
        results.close();
        statement.close();
        return largestLookupId;
    }

    private void dropLatestDatabase() throws Exception {
        TestDatabase.dropMySQLDatabase();
    }

    private void applyUpgrades() throws Exception {
        connection.createStatement().execute("create table database_upgrade_test(x integer)");
        connection.createStatement().execute("insert into database_upgrade_test(x) values(5)");
        connection.createStatement().execute("alter table database_upgrade_test add column y integer default 7");
        connection.commit();
    }

    private void loadLatest() throws Exception {
        connection.createStatement().execute("drop table if exists database_upgrade_test");
        connection.commit();
        connection.createStatement().execute("create table database_upgrade_test(x integer, y integer default 7)");
        connection.createStatement().execute("insert into database_upgrade_test(x, y) values(5,7)");
        connection.commit();
    }

    private void createLatestDatabaseWithLatestData() throws Exception {
        executeScript("mifosdroptables.sql", connection);
        executeScript("latest-schema.sql", connection);
        executeScript("latest-data.sql", connection);
        connection.commit();
    }

    private void createLatestCheckPointDatabaseWithLatestData() throws Exception {
        executeScript("mifosdroptables-checkpoint.sql", connection);
        executeScript("latest-schema-checkpoint.sql", connection);
        executeScript("latest-data-checkpoint.sql", connection);
        connection.commit();
    }
}