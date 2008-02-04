package org.mifos.framework.persistence;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import net.sourceforge.mayfly.Database;


final class DummyUpgrade extends Upgrade {
	private StringBuilder log = new StringBuilder();

	DummyUpgrade(int higherVersion) {
		super(higherVersion);
	}

	@Override
	public void downgrade(Connection connection, DatabaseVersionPersistence databaseVersionPersistence) throws IOException, SQLException {
		log.append("downgrade from " + higherVersion() + "\n");
		if (connection != null) {
			downgradeVersion(connection);
		}
	}

	@Override
	public void upgrade(Connection connection, DatabaseVersionPersistence databaseVersionPersistence) throws IOException, SQLException {
		log.append("upgrade to " + higherVersion() + "\n");
		if (connection != null) {
			upgradeVersion(connection);
		}
	}
	
	String getLog() {
		return log.toString();
	}

	public static Database databaseWithVersionTable(int version) {
		Database database = TestDatabase.makeDatabase();
		database.execute("create table DATABASE_VERSION(DATABASE_VERSION INTEGER)");
		database.execute(
			"insert into DATABASE_VERSION(DATABASE_VERSION) VALUES(" +
			version +
			")");
		return database;
	}

}