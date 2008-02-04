package org.mifos.application.master.persistence;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.persistence.Upgrade;

/*
 * Upgrade169 is an upgrade that cleans up remaining custom field related
 * entries, sets all label text in the database to null so that these
 * labels can serve as overrides for properties file based localized
 * labels, and introduces a locale for Spanish and French.
 */
public class Upgrade169 extends Upgrade {

	public Upgrade169() {
		super(169);
	}

	private short getLookupIdForSupporteLanguageId(Connection connection, int languageId) throws SQLException {

		short lookupId = 0;
		Statement statement = connection.createStatement();
		try {
			ResultSet results = statement.executeQuery(
			"select LOOKUP_ID from LANGUAGE where LANG_ID = " + languageId);
			if (!results.next()) {
				throw new SystemException(SystemException.DEFAULT_KEY, 
				"Query failed on table LANGUAGE for LANG_ID: " + languageId);
			}
			lookupId = results.getShort(1);

		} finally {
			statement.close();
		}
		return lookupId;

	}
	
	@Override
	public void upgrade(Connection connection, DatabaseVersionPersistence databaseVersionPersistence) throws IOException, SQLException {	
		execute(connection, "DELETE FROM PERSONNEL_CUSTOM_FIELD WHERE PERSONNEL_ID = 1");
		/* Clean up unused custom field entities and labels */ 
		execute(connection, "DELETE FROM  LOOKUP_LABEL WHERE LABEL_ID = 61 AND ENTITY_NAME = \'CustomField1\'"); 
		execute(connection, "DELETE FROM  LOOKUP_LABEL WHERE LABEL_ID = 63 AND ENTITY_NAME = \'CustomField2\'"); 
		execute(connection, "DELETE FROM  LOOKUP_LABEL WHERE LABEL_ID = 65 AND ENTITY_NAME = \'CustomField3\'"); 

		execute(connection, "DELETE FROM  LOOKUP_VALUE_LOCALE WHERE LOOKUP_VALUE_ID = 123 AND LOOKUP_VALUE = \'CustomField1\'"); 
		execute(connection, "DELETE FROM  LOOKUP_VALUE_LOCALE WHERE LOOKUP_VALUE_ID = 125 AND LOOKUP_VALUE = \'CustomField2\'"); 
		execute(connection, "DELETE FROM  LOOKUP_VALUE_LOCALE WHERE LOOKUP_VALUE_ID = 127 AND LOOKUP_VALUE = \'CustomField3\'"); 
		
		execute(connection, "DELETE FROM  LOOKUP_VALUE WHERE LOOKUP_ID = 62 AND ENTITY_ID = 31"); 
		execute(connection, "DELETE FROM  LOOKUP_VALUE WHERE LOOKUP_ID = 63 AND ENTITY_ID = 32"); 
		execute(connection, "DELETE FROM  LOOKUP_VALUE WHERE LOOKUP_ID = 64 AND ENTITY_ID = 33"); 
		
		execute(connection, "DELETE FROM  LOOKUP_ENTITY WHERE ENTITY_ID = 31 AND ENTITY_NAME = \'PersonnelCustomField1\'"); 
		execute(connection, "DELETE FROM  LOOKUP_ENTITY WHERE ENTITY_ID = 32 AND ENTITY_NAME = \'PersonnelCustomField2\'"); 
		execute(connection, "DELETE FROM  LOOKUP_ENTITY WHERE ENTITY_ID = 33 AND ENTITY_NAME = \'PersonnelCustomField3\'"); 
		
		/* set all lookup labels to be null so that text will come from resource bundles */ 
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = NULL"); 
		
		/* Spanish locale support */ 
		execute(connection, "INSERT INTO COUNTRY(COUNTRY_ID,COUNTRY_NAME,COUNTRY_SHORT_NAME) VALUES(8,\'Spain\',\'ES\')");
		int lookup_id = insertLookupValue(connection, 74);
		execute(connection, "INSERT INTO LANGUAGE(LANG_ID,LANG_NAME,LANG_SHORT_NAME,LOOKUP_ID) VALUES(3,\'Spanish\',\'es\'," + lookup_id + ")"); 
		execute(connection, "INSERT INTO SUPPORTED_LOCALE(LOCALE_ID,COUNTRY_ID,LANG_ID,LOCALE_NAME,DEFAULT_LOCALE) VALUES(3,8,3,\'Spanish\',0)"); 
		
		/* French locale support */ 
		execute(connection, "INSERT INTO COUNTRY(COUNTRY_ID,COUNTRY_NAME,COUNTRY_SHORT_NAME) VALUES(9,\'France\',\'FR\')"); 
		lookup_id = insertLookupValue(connection, 74);
		execute(connection, "INSERT INTO LANGUAGE(LANG_ID,LANG_NAME,LANG_SHORT_NAME,LOOKUP_ID) VALUES(4,\'French\',\'fr\'," + lookup_id + ")"); 
		execute(connection, "INSERT INTO SUPPORTED_LOCALE(LOCALE_ID,COUNTRY_ID,LANG_ID,LOCALE_NAME,DEFAULT_LOCALE) VALUES(4,9,4,\'French\',0)"); 

		upgradeVersion(connection);
	}

	@Override
	public void downgrade(Connection connection, DatabaseVersionPersistence databaseVersionPersistence) 
	throws IOException, SQLException {
		execute(connection, "INSERT INTO LOOKUP_ENTITY(ENTITY_ID,ENTITY_NAME,DESCRIPTION) " + 
			"VALUES(31,\'PersonnelCustomField1\',\'PersonnelCustomField1\')");
		execute(connection, "INSERT INTO LOOKUP_ENTITY(ENTITY_ID,ENTITY_NAME,DESCRIPTION) " + 
			"VALUES(32,\'PersonnelCustomField2\',\'personnel custom fields\')");
		execute(connection, "INSERT INTO LOOKUP_ENTITY(ENTITY_ID,ENTITY_NAME,DESCRIPTION) " + 
			"VALUES(33,\'PersonnelCustomField3\',\'personnel custom fields\')");

		execute(connection, "INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) " + 
			"VALUES(62,31,\' \')");
		execute(connection, "INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) " + 
			"VALUES(63,32,\' \')");
		execute(connection, "INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) " + 
			"VALUES(64,33,\' \')");

		execute(connection, "INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE) " + 
			"VALUES(123,1,62,\'CustomField1\')");
		execute(connection, "INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE) " + 
			"VALUES(125,1,63,\'CustomField2\')");
		execute(connection, "INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE) " + 
			"VALUES(127,1,64,\'CustomField3\')");

		execute(connection, "INSERT INTO LOOKUP_LABEL(LABEL_ID,ENTITY_ID,LOCALE_ID,ENTITY_NAME) " + 
			"VALUES(61,31,1,\'CustomField1\')");
		execute(connection, "INSERT INTO LOOKUP_LABEL(LABEL_ID,ENTITY_ID,LOCALE_ID,ENTITY_NAME) " + 
			"VALUES(63,32,1,\'CustomField2\')");
		execute(connection, "INSERT INTO LOOKUP_LABEL(LABEL_ID,ENTITY_ID,LOCALE_ID,ENTITY_NAME) " + 
			"VALUES(65,33,1,\'CustomField3\')");

		/* restore text in place of null labels */ 
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Client Status\' WHERE LABEL_ID = 1");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Group Status\' WHERE LABEL_ID = 3");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Center Status\' WHERE LABEL_ID = 5");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Office Status\' WHERE LABEL_ID = 7");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Loan Status\' WHERE LABEL_ID = 9");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Personnel Status\' WHERE LABEL_ID = 11");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Group Flag\' WHERE LABEL_ID = 13");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Fee Type\' WHERE LABEL_ID = 15");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Titles\' WHERE LABEL_ID = 17");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Poverty Status\' WHERE LABEL_ID = 19");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Center\' WHERE LABEL_ID = 21");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Group\' WHERE LABEL_ID = 23");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Client\' WHERE LABEL_ID = 25");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Office\' WHERE LABEL_ID = 27");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Salutation\' WHERE LABEL_ID = 29");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Gender\' WHERE LABEL_ID = 31");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'MartialStatus\' WHERE LABEL_ID = 33");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Citizenship\' WHERE LABEL_ID = 35");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Ethnicity\' WHERE LABEL_ID = 37");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'EducationLevel\' WHERE LABEL_ID = 39");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Occupation\' WHERE LABEL_ID = 41");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Handicapped\' WHERE LABEL_ID = 43");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Postal Code\' WHERE LABEL_ID = 47");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Product State\' WHERE LABEL_ID = 49");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Loan\' WHERE LABEL_ID = 51");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Savings\' WHERE LABEL_ID = 53");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'User Title\' WHERE LABEL_ID = 57");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'User Hierarchy\' WHERE LABEL_ID = 59");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'OfficeLevel\' WHERE LABEL_ID = 67");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'PrdApplicableMaster\' WHERE LABEL_ID = 69");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Week Days\' WHERE LABEL_ID = 71");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Days Rank\' WHERE LABEL_ID = 73");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'InterestTypes\' WHERE LABEL_ID = 75");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'CategoryTypes\' WHERE LABEL_ID = 76");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'InterestCalcRule\' WHERE LABEL_ID = 77");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'GracePeriodTypes\' WHERE LABEL_ID = 79");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'CollateralTypes\' WHERE LABEL_ID = 80");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Office Code\' WHERE LABEL_ID = 81");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Product Category Status\' WHERE LABEL_ID = 83");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Product Status\' WHERE LABEL_ID = 85");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'SAVINGS TYPE\' WHERE LABEL_ID = 87");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'REC AMNT UNIT\' WHERE LABEL_ID = 89");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'INT CALC TYPES\' WHERE LABEL_ID = 91");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'YES/NO\' WHERE LABEL_ID = 93");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Account Type\' WHERE LABEL_ID = 95");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Spouse/Father\' WHERE LABEL_ID = 97");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Customer Status\' WHERE LABEL_ID = 99");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Fee Payment\' WHERE LABEL_ID = 100");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Fee Formula Master\' WHERE LABEL_ID = 102");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Personnel Status\' WHERE LABEL_ID = 104");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Personnel\' WHERE LABEL_ID = 106");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'External Id\' WHERE LABEL_ID = 116");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Fee Status\' WHERE LABEL_ID = 128");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Account Action\' WHERE LABEL_ID = 130");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'AccountFlags\' WHERE LABEL_ID = 132");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'PaymentType\' WHERE LABEL_ID = 134");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Savings Status\' WHERE LABEL_ID = 136");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Language\' WHERE LABEL_ID = 151");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'CustomerAttendance\' WHERE LABEL_ID = 154");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Financial Action\' WHERE LABEL_ID = 156");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Bulk entry\' WHERE LABEL_ID = 158");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Address 3\' WHERE LABEL_ID = 160");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'City/District\' WHERE LABEL_ID = 162");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Interest\' WHERE LABEL_ID = 164");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Loan Purposes\' WHERE LABEL_ID = 166");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'State\' WHERE LABEL_ID = 167");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Address1\' WHERE LABEL_ID = 168");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Address2\' WHERE LABEL_ID = 169");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Government ID\' WHERE LABEL_ID = 170");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Permissions\' WHERE LABEL_ID = 171");
		execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = \'Interest\' WHERE LABEL_ID = 172");

		/* Spanish locale support */ 
		short lookupIdSpanish = getLookupIdForSupporteLanguageId(connection, 3);
		execute(connection, "DELETE FROM SUPPORTED_LOCALE WHERE LOCALE_ID = 3 AND LOCALE_NAME = \'Spanish\'");
		execute(connection, "DELETE FROM LANGUAGE WHERE LANG_ID = 3 AND LANG_NAME = \'Spanish\'");
		deleteFromLookupValue(connection, lookupIdSpanish);
		execute(connection, "DELETE FROM COUNTRY WHERE COUNTRY_ID = 8 AND COUNTRY_NAME = \'Spain\'");

		/* French locale support */ 
		short lookupIdFrench = getLookupIdForSupporteLanguageId(connection, 4);
		execute(connection, "DELETE FROM SUPPORTED_LOCALE WHERE LOCALE_ID = 4 AND LOCALE_NAME = \'French\'");
		execute(connection, "DELETE FROM LANGUAGE WHERE LANG_ID = 4 AND LANG_NAME = \'French\'");
		deleteFromLookupValue(connection, lookupIdFrench);
		execute(connection, "DELETE FROM COUNTRY WHERE COUNTRY_ID = 9 AND COUNTRY_NAME = \'France\'");

		downgradeVersion(connection);
	}
}
