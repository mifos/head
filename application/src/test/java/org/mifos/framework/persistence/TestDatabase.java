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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import junit.framework.Assert;

import org.mifos.accounts.financial.util.helpers.FinancialInitializer;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.StandardTestingService;

public class TestDatabase {

    public static void runUpgradeScripts(int fromVersion, Connection connection) throws Exception {
        DatabaseVersionPersistence persistence = new DatabaseVersionPersistence(connection);
        Assert.assertEquals(fromVersion, persistence.read());
        persistence.upgradeDatabase();
    }

    /**
     * This method was added to work around integration test inter- and intra-dependencies. Once these dependencies in
     * main code are eliminated, we should be able to use Spring managed testing environment (transaction) this method
     * should be eliminated as well.
     */
    public static void resetMySQLDatabase() throws Exception {
        StaticHibernateUtil.flushAndClearSession();
        executeScript("truncate_tables.sql", getJDBCConnection());
        insertTestData();

        // If the database is ever blown away, we must re-populate chart of
        // accounts data since some unit tests rely on its presence. It must
        // be created via this method since adding it via an sql script would
        // invalidate *other* unit tests that assume this method has been
        // called.
        FinancialInitializer.initialize();
    }

    public static void createMySQLTestDatabase() throws Exception {
        dropMySQLDatabase();
        executeScript("latest-schema.sql", getJDBCConnection());
        insertTestData();
    }

    public static void insertTestData() throws Exception {
        executeScript("latest-data.sql", getJDBCConnection());
        executeScript("custom_data.sql", getJDBCConnection());
        executeScript("testdbinsertionscript.sql", getJDBCConnection());
    }

    /*
     * public static void createNotMappedTables() throws Exception { Connection connection = getJDBCConnection(); String
     * sql =
     * "CREATE TABLE if not exists DATABASE_VERSION ( DATABASE_VERSION INTEGER ) ENGINE=InnoDB CHARACTER SET utf8";
     * connection.createStatement().execute(sql); sql = "ALTER TABLE LOOKUP_ENTITY ADD COLUMN DESCRIPTION VARCHAR(200)";
     * connection.createStatement().execute(sql); connection.close(); }
     */

    public static void dropMySQLDatabase() throws Exception {
        executeScript("mifosdroptables.sql", getJDBCConnection());
    }

    /**
     * MySQL specific schema dump generation
     *
     * @return database structure as String dump
     * @throws Exception
     */
    public static String getAllTablesStructureDump() throws Exception {
        Connection connection = getJDBCConnection();
        ResultSet rs = connection.createStatement().executeQuery("SHOW TABLES");
        StringBuilder sb = new StringBuilder();
        while (rs.next()) {
            getCreateTableDump(rs.getString(1), connection, sb);
        }
        return sb.toString();
    }

    private static void getCreateTableDump(String tableName, Connection connection, StringBuilder sb) throws Exception {
        ResultSet rs = connection.createStatement().executeQuery("SHOW CREATE TABLE " + tableName);
        while (rs.next()) {
            sb.append(sortConstraints(rs.getString(2))).append(" \n");
        }
    }

    private static String sortConstraints(String sql) {
        StringTokenizer st = new StringTokenizer(sql, "\n");
        String tableStart = "";
        String tableEnd = "";
        String constraintsKeys = "";
        List<String> constraintsKeysList = new ArrayList<String>();
        boolean readingConstraints = false;
        while (st.hasMoreTokens()) {
            String line = st.nextToken();
            if (line.contains(" CONSTRAINT ") || line.contains(" KEY ")) {
                constraintsKeysList.add(line);
                readingConstraints = true;
            } else {
                if (!readingConstraints) {
                    tableStart += line + "\n";
                } else {
                    // FIXME To see why it's disabled, enable it and run LatestTestAfterCheckpointIntegrationTest
                    // http://mifosforge.jira.com/browse/MIFOS-2875
                    // tableEnd += line +"\n";
                }
            }
        }
        Collections.sort(constraintsKeysList);

        for (String field : constraintsKeysList) {
            constraintsKeys += field.replace(",", "") + "\n";
        }

        return tableStart + constraintsKeys + tableEnd;
    }

    // FIXME Use Spring Managed Connection
    /**
     * Foreign key disabled connection
     */


    private static Connection fkDisabledConnection;

    public static Connection getJDBCConnection() throws Exception {
        if(fkDisabledConnection == null) {
            fkDisabledConnection = initializeFKDisabledConnection();
        }
        return fkDisabledConnection;
    }

    private static Connection initializeFKDisabledConnection() throws Exception {

        final Properties databaseSettings = new StandardTestingService().getDatabaseConnectionSettings();
        final String url = databaseSettings.getProperty("hibernate.connection.url");
        final String param = "&sessionVariables=FOREIGN_KEY_CHECKS=0";
        final String user = databaseSettings.getProperty("hibernate.connection.username");
        final String password = databaseSettings.getProperty("hibernate.connection.password");

        return DriverManager.getConnection(url + param, user, password);
    }
}
