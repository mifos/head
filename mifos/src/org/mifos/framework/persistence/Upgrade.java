package org.mifos.framework.persistence;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.StringUtils;


public abstract class Upgrade {

	private final int higherVersion;
	protected static final int lookupValueChangeVersion = 174;
	public static final String wrongConstructor = "This db version is higher than 174 so it needs to use the constructor with lookupValueKey parameter.";

	protected Upgrade(int higherVersion) {
		this.higherVersion = higherVersion;
	}

	
	abstract public void upgrade(Connection connection, DatabaseVersionPersistence databaseVersionPersistence) 
	throws IOException, SQLException;

	abstract public void downgrade(Connection connection, DatabaseVersionPersistence databaseVersionPersistence) 
	throws IOException, SQLException;
	
	public static boolean validateLookupValueKey(String format, String key)
	{
		boolean result = false;
		if (!StringUtils.isNullAndEmptySafe(key))
			return result;
		if (!key.startsWith(format, 0))
			return result;
		result = true;
		return result;
	}
	

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

	protected void insertMessage(Connection connection, int lookupId, 
		Short localeToInsert, String message) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(
			"insert into LOOKUP_VALUE_LOCALE(" +
			"LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE) " +
			"VALUES(?,?,?)");
		statement.setInt(1, localeToInsert);
		statement.setInt(2, lookupId);
		statement.setString(3, message);
		statement.executeUpdate();
		statement.close();
	}

	protected static void updateMessage(Connection connection, int lookupId, 
		short locale, String newMessage) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(
			"update LOOKUP_VALUE_LOCALE set LOOKUP_VALUE = ? " +
			"where LOCALE_ID = ? and LOOKUP_ID = ?");
		statement.setString(1, newMessage);
		statement.setInt(2, locale);
		statement.setInt(3, lookupId);
		statement.executeUpdate();
		statement.close();
	}

	/*
	 * This method is used for version 174 and lower (it was used in Upgrade169) and must not be used after 174
	 */
	protected int insertLookupValue(Connection connection, 
			int lookupEntity) throws SQLException {
		return insertLookupValue(connection, lookupEntity, " ");
	}
	
	
	/*
	 * This method is used for version 174 and lower and must not be used after 174
	 */
	protected int insertLookupValue(Connection connection, 
			int lookupEntity, String lookupKey) throws SQLException {
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
		statement.setString(3, lookupKey);
	
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

	protected void addLookupEntity(Connection connection, int entityId, String name, String description) 
	throws SQLException {
		PreparedStatement statement = connection.prepareStatement(
			"INSERT INTO LOOKUP_ENTITY(ENTITY_ID,ENTITY_NAME,DESCRIPTION)"
			+ "VALUES(?,?,?)");
		statement.setInt(1, entityId);
		statement.setString(2, name);
		statement.setString(3, description);
		statement.executeUpdate();
		statement.close();
	}

	protected void removeLookupEntity(Connection connection, int entityId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(
				"DELETE FROM LOOKUP_ENTITY WHERE ENTITY_ID = ?");
		statement.setInt(1, entityId);
		statement.executeUpdate();
		statement.close();
	}

	protected void execute(Connection connection, String sql) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.executeUpdate();
		statement.close();
	}

}


