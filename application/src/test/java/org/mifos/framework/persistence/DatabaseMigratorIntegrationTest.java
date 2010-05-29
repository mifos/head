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
import java.sql.SQLException;

import org.dbunit.Assertion;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.mifos.framework.util.helpers.DatabaseSetup;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class DatabaseMigratorIntegrationTest {

    private static java.sql.Connection connection;
    private DatabaseMigrator databaseMigrator;

    @BeforeClass
    public void beforeClass() throws Exception {
        connection = TestDatabase.getJDBCConnection();
        connection.setAutoCommit(false);
        databaseMigrator = new DatabaseMigrator();
    }

    @AfterClass
    public void afterClass() throws Exception {
        connection.close();
    }

    /**
     * Demonstrate the simplest possible non-sequential database upgrade works. For example, upgrading a schema from
     * "1274760000" to "1274761395".
     */
    @Test
    public void testHappyPath() throws Exception {
        loadNonSeqDatabaseSchema();
        createFooTable(connection);
        IDataSet latestDump = new DatabaseConnection(connection).createDataSet();

        loadNonSeqDatabaseSchema();

        connection.createStatement().execute("drop table  if exists foo");

        databaseMigrator.checkUnAppliedUpgradesAndUpgrade();
        IDataSet dump = new DatabaseConnection(connection).createDataSet();
        Assertion.assertEquals(latestDump, dump);

        // check if database is upgraded to 1274761395

    }

    private void createFooTable(Connection connection) throws SQLException {
        connection.createStatement().execute("drop table if exists foo");
        connection.createStatement().execute("CREATE TABLE FOO ( "+
                "FOO_ID INTEGER,"+
                "Description VARCHAR(25),"+
                "PRIMARY KEY(FOO_ID) ) ENGINE=InnoDB CHARACTER SET utf8 ");


        connection.createStatement().execute("INSERT INTO FOO VALUES(1, 'BAR')");

        connection.createStatement().execute("INSERT INTO FOO VALUES(2, 'BAZ')");
        connection.commit();
    }

    private void loadNonSeqDatabaseSchema() throws Exception {
        DatabaseSetup.executeScript("mifosdroptables-non-seq.sql", connection);
        DatabaseSetup.executeScript("latest-schema-non-sequential.sql", connection);
        connection.commit();
    }

    @Test
    public void testMergedUpgrade() throws Exception {
    }
}
