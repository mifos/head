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

package org.mifos.application.master.persistence;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.mifos.application.accounts.financial.business.GLCategoryType;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.persistence.SqlUpgrade;
import org.mifos.framework.persistence.Upgrade;
import org.mifos.framework.util.SqlUpgradeScriptFinder;

/**
 * Conditional upgrade that removes default Chart of Accounts configuration from
 * the database if the database is clean (the proxy for "clean" is that no other
 * records exist which reference the gl_code table)
 */
public class Upgrade176 extends Upgrade {
    public Upgrade176() {
        super(176);
    }

    private boolean isTableEmpty(String tableName, Connection connection) throws SQLException {

        int numRows = 0;
        Statement statement = connection.createStatement();
        try {
            ResultSet results = statement.executeQuery("select count(*) from " + tableName);
            if (!results.next()) {
                throw new SystemException(SystemException.DEFAULT_KEY, "Query failed on table: " + tableName);
            }
            numRows = results.getInt(1);

        } finally {
            statement.close();
        }

        return numRows < 1;
    }

    /**
     * Don't apply the upgrade if there is any data in tables that have foreign
     * keys to the chart of accounts. This upgrade is used to keep the testing
     * framework consistent in being able to upgrade and downgrade a "clean"
     * database.
     */
    @Override
    public void upgrade(Connection connection) throws IOException, SQLException {
        execute(connection, "ALTER TABLE coa ADD COLUMN CATEGORY_TYPE VARCHAR(20)");
        if (isTableEmpty("fees", connection) && isTableEmpty("financial_trxn", connection)
                && isTableEmpty("loan_offering", connection) && isTableEmpty("penalty", connection)
                && isTableEmpty("prd_offering", connection) && isTableEmpty("program", connection)
                && isTableEmpty("savings_offering", connection)) {

            // looks like a fresh database, at least in terms of the chart of
            // accounts data. Blow away all chart of accounts tables and let
            // FinancialInitializer do its thing.
            SqlUpgrade upgrade = SqlUpgradeScriptFinder.findUpgradeScript(this.higherVersion(), "upgrade_to_176_conditional.sql");

            upgrade.runScript(connection);
        } else {
            // Some chart of accounts data exists. Try upgrading with a little
            // more finesse.

            // Yep, we're using string concatenation and not positional
            // parameters, so don't copy this unless you know what you're doing.
            // It's ok in this case because the string we're stuffing in there
            // is basically known/hardcoded. This may or may not work depending
            // on how customized a particular database is. If not, it will be
            // need to be hand-migrated.
            execute(connection, "UPDATE coa SET category_type = '" + GLCategoryType.ASSET
                    + "' WHERE COA_Name = 'ASSETS'");
            execute(connection, "UPDATE coa SET category_type = '" + GLCategoryType.LIABILITY
                    + "' WHERE COA_Name = 'LIABILITIES'");
            execute(connection, "UPDATE coa SET category_type = '" + GLCategoryType.INCOME
                    + "' WHERE COA_Name = 'INCOME'");
            execute(connection, "UPDATE coa SET category_type = '" + GLCategoryType.EXPENDITURE
                    + "' WHERE COA_Name = 'EXPENDITURE'");
        }
        upgradeVersion(connection);
    }

}
