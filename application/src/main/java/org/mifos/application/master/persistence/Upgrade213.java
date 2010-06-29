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

package org.mifos.application.master.persistence;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.persistence.SqlUpgrade;
import org.mifos.framework.persistence.Upgrade;
import org.mifos.framework.util.SqlUpgradeScriptFinder;

/**
 * Upgrade213 is a conditional upgrade that creates 5 indexes
 * (CUSTOMER_ATTENDANCE_MEETING_DATE_IDX ON CUSTOMER_ATTENDANCE,
 * LOAN_COUNTER_CLIENT_PERF_IDX ON LOAN_COUNTER, CUSTOMER_BRANCH_SEARCH_IDX ON
 * CUSTOMER, CUSTOMER_DOB_STATUS_IDX ON CUSTOMER, LOOKUP_ENTITYNAME_IDX ON
 * LOOKUP_ENTITY)
 */
public class Upgrade213 extends Upgrade {

    public Upgrade213() {
        super(213);
    }

    @Override
    public void upgrade(Connection connection) throws IOException, SQLException {

        if (!hasIndex(connection, "customer", "customer_branch_search_idx")) {
            SqlUpgrade upgrade = SqlUpgradeScriptFinder.findUpgradeScript(this.higherVersion(),
                    "upgrade_to_213_part1.sql");

            upgrade.runScript(connection);
        }

        if (!hasIndex(connection, "customer", "customer_dob_status_idx")) {
            SqlUpgrade upgrade = SqlUpgradeScriptFinder.findUpgradeScript(this.higherVersion(),
                    "upgrade_to_213_part2.sql");

            upgrade.runScript(connection);
        }

        if (!hasIndex(connection, "customer_attendance", "customer_attendance_meeting_date_idx")) {
            SqlUpgrade upgrade = SqlUpgradeScriptFinder.findUpgradeScript(this.higherVersion(),
                    "upgrade_to_213_part3.sql");

            upgrade.runScript(connection);
        }

        if (!hasIndex(connection, "loan_counter", "loan_counter_client_pref_idx")) {
            SqlUpgrade upgrade = SqlUpgradeScriptFinder.findUpgradeScript(this.higherVersion(),
                    "upgrade_to_213_part4.sql");

            upgrade.runScript(connection);
        }

        if (!hasIndex(connection, "lookup_entity", "lookup_entityname_idx")) {
            SqlUpgrade upgrade = SqlUpgradeScriptFinder.findUpgradeScript(this.higherVersion(),
                    "upgrade_to_213_part5.sql");

            upgrade.runScript(connection);
        }

        upgradeVersion(connection);
    }

    /**
     * Returns whether the particular index exists in the given table
     */
    protected boolean hasIndex(Connection connection, String tableName, String indexName) throws SQLException {

        boolean indexFound = false;

        final String tableSchema = getMySqlSchemaName(connection);

        int numFields = 0;
        String sql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA=? AND TABLE_NAME LIKE ? AND INDEX_NAME=?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, tableSchema);
        statement.setString(2, tableName);
        statement.setString(3, indexName);

        try {
            ResultSet results = statement.executeQuery();
            if (!results.next()) {
                throw new SystemException(SystemException.DEFAULT_KEY, "Query failed for SQL " + sql);
            }
            numFields = results.getInt(1);
        } finally {
            statement.close();
        }

        if (numFields == 0) {
            getLogger().info("The index =>" + indexName + " on table " + tableName + " does not exisit");
            indexFound = false;
        } else {
            getLogger().info(
                    "The index =>" + indexName + " on table " + tableName
                            + " has already beean created in the database");
            indexFound = true;
        }

        return indexFound;
    }

    private String getMySqlSchemaName(Connection connection) throws SQLException {
        String databaseName;
        final URI infoURL;
        try {
            URI mysqlOnly;
            /*
             * TODO: Very similar to code in SystemInfo. Consider refactoring.
             */
            mysqlOnly = new URI(connection.getMetaData().getURL());

            infoURL = new URI(mysqlOnly.getSchemeSpecificPart());
            databaseName = infoURL.getPath();

            if (databaseName != null) {
                databaseName = databaseName.replaceFirst("/", "");
            } else {
                throw new SQLException("Cannot obtain database name");
            }

        } catch (URISyntaxException e) {
            throw new SQLException(e);
        }
        return databaseName;
    }

}
