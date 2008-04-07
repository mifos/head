package org.mifos.application.accounts.business;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.MifosLookUpEntity;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.persistence.Upgrade;


public class AddAccountAction extends Upgrade {

	private final Short locale;
	private final String lookupValueKey;
	private final int action;
	private final String message;
	protected static final String wrongLookupValueKeyFormat = "The key format must be AccountAction-...";
	protected static final String keyFormat = "AccountAction-";

	/* This constructor is used for version 174 and lower. 
	 * And it must not be used afterward
	 */
	public AddAccountAction(int higherVersion,
		int action, Short locale, String message) {
		super(higherVersion);
		if (higherVersion > lookupValueChangeVersion)
			throw new RuntimeException(wrongConstructor);
		this.action = action;
		this.locale = locale;
		this.message = message;
		this.lookupValueKey = " ";
	}
	
	/*
	 * This constructor must be used after version 174. The lookupValueKey must in the format
	 * AccountAction-...
	 */
	public AddAccountAction(int higherVersion,
			int action, String lookupValueKey)  {
			super(higherVersion);
			if (!validateLookupValueKey(keyFormat, lookupValueKey))
				throw new RuntimeException(wrongLookupValueKeyFormat);
			this.action = action;
			this.locale = MasterDataEntity.CUSTOMIZATION_LOCALE_ID;
			this.lookupValueKey = lookupValueKey;
			this.message = null;
		}

	@Override
	public void upgrade(Connection connection, DatabaseVersionPersistence databaseVersionPersistence) 
	throws IOException, SQLException {
		int lookupEntity = MifosLookUpEntity.ACCOUNT_ACTION;

		int lookupId = insertLookupValue(connection, lookupEntity, lookupValueKey);
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
