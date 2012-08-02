/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import javax.sql.DataSource;

import org.springframework.beans.factory.FactoryBean;

public class TestDatabase implements FactoryBean<TestDatabase> {

    private static DataSource integrationDataSource;

    private static TestDatabase testDatabase;

    private TestDatabase() {

    }

    public static TestDatabase getInstance() {
        if(testDatabase == null) {
            testDatabase = new TestDatabase();
        }
        return testDatabase;
    }


    /**
     * @return the integrationDataSource
     */
    public DataSource getIntegrationDataSource() {
        return integrationDataSource;
    }

    /**
     * @param integrationDataSource the integrationDataSource to set
     */
    public void setIntegrationDataSource(DataSource integrationDataSource) {
        this.integrationDataSource = integrationDataSource;
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
        StringBuilder sb = new StringBuilder();
        Connection connection = getJDBCConnection();
        ResultSet rs = connection.createStatement().executeQuery("SHOW TABLES");
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
                }
            }
        }
        Collections.sort(constraintsKeysList);

        for (String field : constraintsKeysList) {
            constraintsKeys += field.replace(",", "") + "\n";
        }

        return tableStart + constraintsKeys + tableEnd;
    }

    private static Connection fkDisabledConnection;

    private static Connection getJDBCConnection() throws Exception {
        if(fkDisabledConnection == null) {
            fkDisabledConnection = initializeFKDisabledConnection();
        }
        return fkDisabledConnection;
    }

    private static Connection initializeFKDisabledConnection() throws Exception {
        return integrationDataSource.getConnection();
    }

    @Override
    public TestDatabase getObject() throws Exception {
        // TODO Auto-generated method stub
        return getInstance();
    }

    @Override
    public Class<?> getObjectType() {
        return TestDatabase.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
