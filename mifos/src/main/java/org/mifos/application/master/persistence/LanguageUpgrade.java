package org.mifos.application.master.persistence;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.persistence.SqlUpgrade;
import org.mifos.framework.persistence.Upgrade;

public abstract class LanguageUpgrade extends Upgrade {

    private static final int LANGUAGE_ENTITY_NUMBER = 74;
    private int databaseVersion;

    public LanguageUpgrade(int databaseVersion) {
		super(databaseVersion);
		this.databaseVersion = databaseVersion;
	}

    /** 
     * This method adds language name, code pairs to the map to be added to the database
     * 
     * @param languageNameAndCodesToAdd - add data to this map
     */
    public abstract void addData(List<String[]> languageNameAndCodesToAdd) ;

	@Override
	public void upgrade(Connection connection, DatabaseVersionPersistence databaseVersionPersistence)
			throws IOException, SQLException {
        addCountryCodes(connection, databaseVersionPersistence, databaseVersion);
        addLanguageDescriptionLookupValues(connection);
        addLocales(connection, databaseVersionPersistence, databaseVersion);
		upgradeVersion(connection);
	}

    private void addLanguageDescriptionLookupValues(Connection connection) throws SQLException {
        List<String[]> languageData = getLanguageData();
        for(String[] languageEntry: languageData) {
            String languageName = languageEntry[0];
            String languageCode = languageEntry[1];
            insertLanguageAndLookupValue(connection, getLargestLanguageId(connection) + 1, languageName, languageCode );
        }
    }

    private List<String[]> getLanguageData() {
        List<String[]> languageNameAndCodesToAdd = new ArrayList<String[]>();
        addData(languageNameAndCodesToAdd);
        return languageNameAndCodesToAdd;
    }

    private void addCountryCodes(Connection connection, DatabaseVersionPersistence databaseVersionPersistence, int upgradeVersion)
            throws IOException, SQLException {
        upgradePart(connection, databaseVersionPersistence, "upgrade_to_" + upgradeVersion + "_part_1.sql");
    }

    private void addLocales(Connection connection, DatabaseVersionPersistence databaseVersionPersistence, int upgradeVersion)
    throws IOException, SQLException {
        upgradePart(connection, databaseVersionPersistence, "upgrade_to_" + upgradeVersion + "_part_3.sql");
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

    private int getLargestLanguageId(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet results = statement.executeQuery(
            "select max(LANG_ID) from LANGUAGE");
        if (!results.next()) {
            throw new SystemException(SystemException.DEFAULT_KEY, 
                "Did not find an existing LANG_ID in LANGUAGE table");
        }
        int largestLangId = results.getInt(1);
        results.close();
        statement.close();
        return largestLangId;
    }
    
    
}
