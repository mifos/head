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

import java.sql.Connection;
import java.sql.DriverManager;

import org.mifos.framework.util.helpers.DatabaseSetup;

/**
 * At least for now, this is just a way to do some of what LatestTest
 * does, but for MySQL.  It isn't (yet at least) a general purpose
 * command line upgrader.
 */
public class Upgrader {

	public static void main(String[] args) throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		Connection connection = DriverManager.getConnection(
			"jdbc:mysql://localhost:3306/test", "root", "mysql");
		connection.setAutoCommit(false);

		DatabaseSetup.executeScript(connection, "mifosdroptables.sql");
		TestDatabase.upgradeToFirstNumberedVersion(connection);
		TestDatabase.runUpgradeScripts(connection);
	}

}
