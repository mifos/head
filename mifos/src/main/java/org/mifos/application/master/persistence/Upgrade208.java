package org.mifos.application.master.persistence;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.persistence.SqlUpgrade;
import org.mifos.framework.persistence.Upgrade;

public class Upgrade208 extends Upgrade {

    private static final int LANGUAGE_ENTITY_NUMBER = 74;

    public Upgrade208() {
		super(208);
	}

	@Override
	public void upgrade(Connection connection, DatabaseVersionPersistence databaseVersionPersistence)
			throws IOException, SQLException {
        addCountryCodes(connection, databaseVersionPersistence);
        addLanguageDescriptionLookupValues(connection);
        addLocales(connection, databaseVersionPersistence);
		upgradeVersion(connection);
	}

    private void addLanguageDescriptionLookupValues(Connection connection) throws SQLException {
        insertLanguageAndLookupValue(connection, 5, "Chinese", "zh");
        insertLanguageAndLookupValue(connection, 6, "Swahili", "sw");
        insertLanguageAndLookupValue(connection, 7, "Arabic", "ar");
    }

    private void addCountryCodes(Connection connection, DatabaseVersionPersistence databaseVersionPersistence)
            throws IOException, SQLException {
        upgradePart(connection, databaseVersionPersistence, "upgrade_to_208_part_1.sql");
    }

    private void addLocales(Connection connection, DatabaseVersionPersistence databaseVersionPersistence)
    throws IOException, SQLException {
        upgradePart(connection, databaseVersionPersistence, "upgrade_to_208_part_3.sql");
    }
    
    private void upgradePart(Connection connection, DatabaseVersionPersistence databaseVersionPersistence, String sqlUpgradeScriptFilename)
    throws IOException, SQLException {
        SqlUpgrade upgradePart = databaseVersionPersistence.findUpgradeScript(this.higherVersion(), sqlUpgradeScriptFilename);
        upgradePart.runScript(connection);
    }

    private void insertLanguageAndLookupValue(Connection connection, int languageId, String languageName, String languageCode) throws SQLException {
        int lookupId = insertLookupValue(connection, LANGUAGE_ENTITY_NUMBER, "Language-" + languageName);
        String insertLanguageSql = "INSERT INTO LANGUAGE(LANG_ID,LANG_NAME,LANG_SHORT_NAME,LOOKUP_ID) VALUES(" + languageId + ", '" + 
                                                                                                                 languageName + "','" + 
                                                                                                                 languageCode + "'," +
                                                                                                                 lookupId + ")";
        execute(connection, insertLanguageSql);
    }

}
