package org.mifos.application.master.persistence;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.persistence.Upgrade;

public class Upgrade155 extends Upgrade {

	public Upgrade155() {
		super(155);
	}

	@Override
	public void upgrade(Connection connection, DatabaseVersionPersistence databaseVersionPersistence)
			throws IOException, SQLException {
		execute(connection, "INSERT INTO COUNTRY(COUNTRY_ID,COUNTRY_NAME,COUNTRY_SHORT_NAME) VALUES(7,\'Iceland\',\'IS\')");
		int lookup_id = insertLookupValue(connection, 74);
		execute(connection, "INSERT INTO LANGUAGE(LANG_ID,LANG_NAME,LANG_SHORT_NAME,LOOKUP_ID) VALUES(2,\'Icelandic\',\'IS\'," + lookup_id + ")"); 
		execute(connection, "INSERT INTO SUPPORTED_LOCALE(LOCALE_ID,COUNTRY_ID,LANG_ID,LOCALE_NAME,DEFAULT_LOCALE) VALUES(2,7,2,\'Icelandic\',0)");
		upgradeVersion(connection);
	}
	
	@Override
	public void downgrade(Connection connection,
			DatabaseVersionPersistence databaseVersionPersistence)
			throws IOException, SQLException {
		short lookupIdIcelandic = getLookupIdForSupporteLanguageId(connection, 2);
		execute(connection, "DELETE FROM SUPPORTED_LOCALE WHERE LOCALE_ID = 2 AND LOCALE_NAME = \'Icelandic\'");
		execute(connection, "DELETE FROM LANGUAGE WHERE LANG_ID = 2 AND LANG_NAME = \'Icelandic\'");
		deleteFromLookupValue(connection, lookupIdIcelandic);
		execute(connection, "DELETE FROM COUNTRY WHERE COUNTRY_ID = 7 AND COUNTRY_NAME = \'Iceland\'");
		downgradeVersion(connection);
	}
	
	private short getLookupIdForSupporteLanguageId(Connection connection, int languageId) throws SQLException {
		Statement statement = connection.createStatement();
		try {
			ResultSet results = statement.executeQuery(
			"select LOOKUP_ID from LANGUAGE where LANG_ID = " + languageId);
			if (!results.next()) {
				throw new SystemException(SystemException.DEFAULT_KEY, 
				"Query failed on table LANGUAGE for LANG_ID: " + languageId);
			}
			return results.getShort(1);
		} finally {
			statement.close();
		}
	}
}
