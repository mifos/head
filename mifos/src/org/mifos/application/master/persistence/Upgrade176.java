/*
 * Copyright (c) 2005-2008 Grameen Foundation USA
 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005
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

import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.persistence.SqlUpgrade;
import org.mifos.framework.persistence.Upgrade;

/**
 * Conditional upgrade that removes default Chart of Accounts configuration from
 * the database if the database is clean (the proxy for "clean" is that no other
 * records exist which reference the gl_code table)
 */
public class Upgrade176 extends Upgrade {
	public Upgrade176() {
		super(176);
	}

	private boolean isTableEmpty(String tableName, Connection connection)
			throws SQLException {

		int numRows = 0;
		Statement statement = connection.createStatement();
		try {
			ResultSet results = statement.executeQuery("select count(*) from "
					+ tableName);
			if (!results.next()) {
				throw new SystemException(SystemException.DEFAULT_KEY,
						"Query failed on table: " + tableName);
			}
			numRows = results.getInt(1);

		}
		finally {
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
	public void upgrade(Connection connection, DatabaseVersionPersistence dvp)
			throws IOException, SQLException {
		if (isTableEmpty("fees", connection)
				&& isTableEmpty("financial_trxn", connection)
				&& isTableEmpty("loan_offering", connection)
				&& isTableEmpty("penalty", connection)
				&& isTableEmpty("prd_offering", connection)
				&& isTableEmpty("program", connection)
				&& isTableEmpty("savings_offering", connection)) {

			SqlUpgrade upgrade = dvp.findUpgradeDowngradeScript(this
					.higherVersion(), "upgrade_to_176_conditional.sql");

			upgrade.runScript(connection);
		}
		upgradeVersion(connection);
	}

	@Override
	public void downgrade(Connection connection,
			DatabaseVersionPersistence databaseVersionPersistence)
			throws IOException, SQLException {
		if (isTableEmpty("coa", connection)) {
			SqlUpgrade upgrade = databaseVersionPersistence
					.findUpgradeDowngradeScript(this.higherVersion(),
							"downgrade_from_176_conditional.sql");
			upgrade.runScript(connection);
		}
		downgradeVersion(connection);
	}

}
