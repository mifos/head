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

		DatabaseSetup.executeScript(connection, "sql/mifosdroptables.sql");
		TestDatabase.upgradeToFirstNumberedVersion(connection);
		TestDatabase.runUpgradeScripts(connection);
	}

}
