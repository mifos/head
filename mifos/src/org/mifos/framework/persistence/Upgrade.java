package org.mifos.framework.persistence;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.mifos.framework.exceptions.SystemException;

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

	protected void insertMessage(Connection connection, int lookupId, Short localeToInsert, String nameToInsert) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(
			"insert into LOOKUP_VALUE_LOCALE(" +
			"LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE) " +
			"VALUES(?,?,?)");
		statement.setInt(1, localeToInsert);
		statement.setInt(2, lookupId);
		statement.setString(3, nameToInsert);
		statement.executeUpdate();
		statement.close();
	}

	protected int insertLookupValue(Connection connection, int lookupEntity) throws SQLException {
		/* LOOKUP_ID is not AUTO_INCREMENT until database version 121.
		   Although we perhaps could try to work some magic with the
		   upgrades, it seems better to just insert in the racy way
		   until then.  Upgrades run single-threaded before the 
		   application allows logins, so I think this is fine. */
		int largestLookupId = largestLookupId(connection);
		
		int newLookupId = largestLookupId + 1;
		PreparedStatement statement = connection.prepareStatement(
			"insert into LOOKUP_VALUE(" +
			"LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) " +
			"VALUES(?,?,?)");
		statement.setInt(1, newLookupId);
		statement.setInt(2, lookupEntity);
		
		/* Pretty much all existing code inserts a space here.
		   I'm not sure this field is used for anything. */
		statement.setString(3, " ");
	
		statement.executeUpdate();
		statement.close();
		return newLookupId;
	}

	private int largestLookupId(Connection connection) throws SQLException {
		Statement statement = connection.createStatement();
		ResultSet results = statement.executeQuery(
			"select max(lookup_id) from LOOKUP_VALUE");
		if (!results.next()) {
			throw new SystemException(SystemException.DEFAULT_KEY, 
				"Did not find an existing lookup_id in lookup_value table");
		}
		int largestLookupId = results.getInt(1);
		results.close();
		statement.close();
		return largestLookupId;
	}

	protected void deleteFromLookupValueLocale(Connection connection, short lookupId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(
			"delete from LOOKUP_VALUE_LOCALE where lookup_id = ?");
		statement.setInt(1, lookupId);
		statement.executeUpdate();
		statement.close();
	}

	protected void deleteFromLookupValue(Connection connection, short lookupId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(
			"delete from LOOKUP_VALUE where lookup_id = ?");
		statement.setInt(1, lookupId);
		statement.executeUpdate();
		statement.close();
	}

}
