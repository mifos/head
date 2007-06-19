package org.mifos.framework.persistence;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class Upgrade {

	private final int higherVersion;

	protected Upgrade(int higherVersion) {
		this.higherVersion = higherVersion;
	}

	abstract public void upgrade(Connection connection) 
	throws IOException, SQLException;

	abstract public void downgrade(Connection connection) 
	throws IOException, SQLException;

	public int higherVersion() {
		return higherVersion;
	}

	public int lowerVersion() {
		return higherVersion - 1;
	}

	protected void upgradeVersion(Connection connection) throws SQLException {
		changeVersion(connection, higherVersion(), lowerVersion());
	}

	protected void downgradeVersion(Connection connection) throws SQLException {
		changeVersion(connection, lowerVersion(), higherVersion());
	}

	private void changeVersion(Connection connection, 
			int newVersion, int existingVersion) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(
			"update DATABASE_VERSION " +
			"set DATABASE_VERSION = ? where DATABASE_VERSION = ?");
		statement.setInt(1, newVersion);
		statement.setInt(2, existingVersion);
		statement.executeUpdate();
		connection.commit();
	}

}
