package org.mifos.application.accounts.business;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mifos.application.master.business.MifosLookUpEntity;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.persistence.Upgrade;

public class AddAccountAction extends Upgrade {

	private final Short locale;
	private final String message;
	private final int action;

	public AddAccountAction(int higherVersion,
		int action, Short locale, String message) {
		super(higherVersion);
		this.action = action;
		this.locale = locale;
		this.message = message;
	}

	@Override
	public void upgrade(Connection connection, DatabaseVersionPersistence databaseVersionPersistence) 
	throws IOException, SQLException {
		int lookupEntity = MifosLookUpEntity.ACCOUNT_ACTION;

		int lookupId = insertLookupValue(connection, lookupEntity);
		insertMessage(connection, lookupId, locale, message);
		addAction(connection, action, lookupId);
		upgradeVersion(connection);
	}

	private void addAction(Connection connection, 
		int actionId, int lookupId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(
			"INSERT INTO ACCOUNT_ACTION(ACCOUNT_ACTION_ID,LOOKUP_ID) " +
			"VALUES(?,?)");
		statement.setInt(1, actionId);
		statement.setInt(2, lookupId);
		statement.executeUpdate();
		statement.close();
	}

	@Override
	public void downgrade(Connection connection, DatabaseVersionPersistence databaseVersionPersistence) throws IOException, SQLException {
		short lookupId = findLookupId(connection);
		deleteFromAccountAction(connection);
		deleteFromLookupValueLocale(connection, lookupId);
		deleteFromLookupValue(connection, lookupId);
		downgradeVersion(connection);
	}

	private short findLookupId(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(
			"select LOOKUP_ID " +
			"from ACCOUNT_ACTION where ACCOUNT_ACTION_ID = ?");
		statement.setInt(1, action);
		ResultSet results = statement.executeQuery();
		if (results.next()) {
			short lookupId = results.getShort("LOOKUP_ID");
			statement.close();
			return lookupId;
		}
		else {
			statement.close();
			throw new RuntimeException(
				"unable to downgrade: no activity with id " + action);
		}
	}

	private void deleteFromAccountAction(Connection connection) 
	throws SQLException {
		PreparedStatement statement = connection.prepareStatement(
			"delete from ACCOUNT_ACTION where ACCOUNT_ACTION_ID = ?");
		statement.setInt(1, action);
		statement.executeUpdate();
		statement.close();
	}

}
