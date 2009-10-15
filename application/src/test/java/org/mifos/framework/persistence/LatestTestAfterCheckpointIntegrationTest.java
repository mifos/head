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

import java.sql.Connection;
import java.sql.ResultSet;

import junit.framework.Assert;

import org.dbunit.Assertion;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.mifos.application.accounts.financial.util.helpers.FinancialInitializer;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
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

public class LatestTestAfterCheckpointIntegrationTest extends LatestBaseTestCase {

    private Connection connection;

    public LatestTestAfterCheckpointIntegrationTest() {
        MifosLogManager.configureLogging();
        StaticHibernateUtil.initialize();
    }

    @Override
    protected void setUp() throws Exception {
        connection = StaticHibernateUtil.getSessionTL().connection();
        connection.setAutoCommit(false);
        DatabaseSetup.executeScript(connection, "mifosdroptables.sql");
    }

    @Override
    protected void tearDown() throws Exception {
        StaticHibernateUtil.flushAndCloseSession();
    }

    public void testSimple() throws Exception {
        connection.createStatement().execute("drop table if exists foo");
        connection.commit();
        loadLatest(connection);
        IDatabaseConnection dbUnitConnection = new DatabaseConnection(connection);
        IDataSet latestDump = dbUnitConnection.createDataSet();

        connection.createStatement().execute("drop table if exists foo");
        connection.commit();
        applyUpgrades(connection);
        IDataSet upgradeDump = dbUnitConnection.createDataSet();
        Assertion.assertEquals(latestDump, upgradeDump);
    }

    private void applyUpgrades(Connection connection) throws Exception {
        connection.createStatement().execute("create table foo(x integer)");
        connection.createStatement().execute("insert into foo(x) values(5)");
        connection.createStatement().execute("alter table foo add column y integer default 7");
        connection.commit();
    }

    private void loadLatest(Connection connection) throws Exception {
        connection.createStatement().execute("create table foo(x integer, y integer default 7)");
        connection.createStatement().execute("insert into foo(x, y) values(5,7)");
        connection.commit();
    }

    public void testRealSchemaFromCheckpoint() throws Exception {
        DatabaseSetup.executeScript(connection, "latest-schema.sql");
        DatabaseSetup.executeScript(connection, "latest-data.sql");
        Assert.assertEquals(DatabaseVersionPersistence.APPLICATION_VERSION, new DatabaseVersionPersistence(connection)
                .read());

        IDatabaseConnection dbUnitConnection = new DatabaseConnection(connection);
        IDataSet latestDump = dbUnitConnection.createDataSet();

        DatabaseSetup.executeScript(connection, "mifosdroptables.sql");
        TestDatabase.upgradeLatestCheckpointVersion(connection);
        TestDatabase.runUpgradeScripts(LATEST_CHECKPOINT_VERSION, connection);
        IDataSet upgradeDump = dbUnitConnection.createDataSet();
        Assertion.assertEquals(latestDump, upgradeDump);
    }

    public void testDropTables() throws Exception {
        IDatabaseConnection dbUnitConnection = new DatabaseConnection(connection);
        IDataSet blankDB = dbUnitConnection.createDataSet();
        DatabaseSetup.executeScript(connection, "latest-schema.sql");
        DatabaseSetup.executeScript(connection, "mifosdroptables.sql");
        IDataSet cleanedDB = dbUnitConnection.createDataSet();
        Assertion.assertEquals(blankDB, cleanedDB);
    }

    /**
     * The idea here is to figure out whether we are dropping tables in the
     * right order to deal with foreign keys. I'm not sure we fully succeed,
     * however.
     */
    public void testDropTablesWithData() throws Exception {
        IDatabaseConnection dbUnitConnection = new DatabaseConnection(connection);
        IDataSet blankDB = dbUnitConnection.createDataSet();
        DatabaseSetup.executeScript(connection, "latest-schema.sql");
        DatabaseSetup.executeScript(connection, "latest-data.sql");
        DatabaseSetup.executeScript(connection, "mifosdroptables.sql");
        IDataSet cleanedDB = dbUnitConnection.createDataSet();
        Assertion.assertEquals(blankDB, cleanedDB);
    }

    public void testAfterLookupValuesAfterCheckpoint() throws Exception {

        TestDatabase.upgradeLatestCheckpointVersion(connection);
        int nextLookupId = largestLookupId(connection) + 1;
        connection.createStatement().execute(
                "insert into LOOKUP_VALUE(LOOKUP_ID, ENTITY_ID, LOOKUP_NAME) " + "VALUES(" + nextLookupId
                        + ", 19,'TestLookUpName')");
        connection.createStatement().execute(
                "insert into LOOKUP_VALUE_LOCALE(LOCALE_ID, LOOKUP_ID, LOOKUP_VALUE) " + "VALUES(1," + nextLookupId
                        + ",'Martian')");

        upgradeDB(LATEST_CHECKPOINT_VERSION, connection);
        connection.commit();
        // Assert that custom values have been retained
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
        
        //FIXME these steps are required to clear database after the test class finish execution
        // Required is a exit method where these scripts should be excuted
        SqlExecutor.execute(SqlResource.getInstance().getAsStream("mifosdroptables.sql"), connection);
        SqlExecutor.execute(SqlResource.getInstance().getAsStream("latest-schema.sql"), connection);
        SqlExecutor.execute(SqlResource.getInstance().getAsStream("latest-data.sql"), connection);
        SqlExecutor.execute(SqlResource.getInstance().getAsStream("custom_data.sql"), connection);
        SqlExecutor.execute(SqlResource.getInstance().getAsStream("testdbinsertionscript.sql"), connection);
        connection.commit();
        FinancialInitializer.initialize();
    }
}
