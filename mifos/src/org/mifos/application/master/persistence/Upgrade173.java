/*
 * Copyright (c) 2005-2008 Grameen Foundation USA
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
 * Upgrade173 is a conditional upgrade that removes default custom lookup
 * values (like Saluation, Marital Status, Loan Purpose...) 
 * from the database if the database is clean (the proxy for "clean" is 
 * that no other offices than the default head offices exist in the database)
 */
public class Upgrade173 extends Upgrade {

	public Upgrade173() {
		super(173);
	}

	private int countRows(Connection connection, String tableName) throws SQLException {

		int numFields = 0;
		Statement statement = connection.createStatement();
		try {
			ResultSet results = statement.executeQuery(
			"select count(*) from " + tableName);
			if (!results.next()) {
				throw new SystemException(SystemException.DEFAULT_KEY, 
				"Query failed on table: " + tableName);
			}
			numFields = results.getInt(1);

		} finally {
			statement.close();
		}
		return numFields;

	}
		
	/**
	 * Don't apply the upgrade if there is any data in the database.  
	 * This upgrade is used to keep the testing framework consistent in
	 * being able to upgrade and downgrade a "clean" database.
	 */
	@Override
	public void upgrade(Connection connection, DatabaseVersionPersistence databaseVersionPersistence) throws IOException, SQLException {
		if (noOfficesHaveBeenCreatedByEndUsers(connection)) {
			SqlUpgrade upgrade = databaseVersionPersistence.findUpgradeScript(this.higherVersion(),"upgrade_to_173_conditional.sql");
			upgrade.runScript(connection);
		}
		upgradeVersion(connection);
	}

	/**
	 * By default a single head office is present in a clean database, so
	 * no offices have been created if there is only one office row present.
	 * This test is being used to determine if the database has no user data 
	 * in it.
	 */
	private boolean noOfficesHaveBeenCreatedByEndUsers(Connection connection) throws SQLException {
		return countRows(connection, "OFFICE") == 1;
	}

}
