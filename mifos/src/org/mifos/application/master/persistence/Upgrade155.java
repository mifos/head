package org.mifos.application.master.persistence;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

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
	
}
