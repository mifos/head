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
 * Upgrade167 is a conditional upgrade that removes default custom fields
 * from the database if there are no custom fields in use other than
 * those required by the default 'mifos' user and 'HO' office.  If there
 * are any custom fields in use, then it just leaves all custom fields alone.
 */
public class Upgrade167 extends Upgrade {

	public Upgrade167() {
		super(167);
	}

	private int CountRows(Connection connection, String tableName) throws SQLException {

		int numFields = 0;
		Statement statement = connection.createStatement();
		try {
			ResultSet results = statement.executeQuery(
			"select count(*) from " + tableName);
			if (!results.next()) {
				throw new SystemException(SystemException.DEFAULT_KEY, 
				"Query failed on table: " + tableName);
			}
			numFields = results.getInt(1);

		} finally {
			statement.close();
		}
		return numFields;

	}
	
	private boolean CustomFieldsHaveBeenCreated(Connection connection, String tableName, int expectedCount) throws SQLException {
		return CountRows(connection, tableName) > expectedCount;
	}
	
	@Override
	public void upgrade(Connection connection, DatabaseVersionPersistence databaseVersionPersistence) throws IOException, SQLException {	
		if (!CustomFieldsHaveBeenCreated(connection,"PERSONNEL_CUSTOM_FIELD", 1) &&
				!CustomFieldsHaveBeenCreated(connection,"OFFICE_CUSTOM_FIELD", 4) &&
				!CustomFieldsHaveBeenCreated(connection,"ACCOUNT_CUSTOM_FIELD", 0) &&
				!CustomFieldsHaveBeenCreated(connection,"CUSTOMER_CUSTOM_FIELD", 0)) {

			execute(connection, "DELETE FROM PERSONNEL_CUSTOM_FIELD WHERE PERSONNEL_ID = 1");
			execute(connection, "DELETE FROM OFFICE_CUSTOM_FIELD WHERE OFFICE_ID = 1"); 
			
			execute(connection, "DELETE FROM CUSTOM_FIELD_DEFINITION WHERE FIELD_ID = 3"); 
			execute(connection, "DELETE FROM CUSTOM_FIELD_DEFINITION WHERE FIELD_ID = 4"); 
			execute(connection, "DELETE FROM CUSTOM_FIELD_DEFINITION WHERE FIELD_ID = 5"); 
			execute(connection, "DELETE FROM CUSTOM_FIELD_DEFINITION WHERE FIELD_ID = 6"); 
			execute(connection, "DELETE FROM CUSTOM_FIELD_DEFINITION WHERE FIELD_ID = 9"); 
			execute(connection, "DELETE FROM CUSTOM_FIELD_DEFINITION WHERE FIELD_ID = 10"); 
			execute(connection, "DELETE FROM CUSTOM_FIELD_DEFINITION WHERE FIELD_ID = 11"); 
			execute(connection, "DELETE FROM CUSTOM_FIELD_DEFINITION WHERE FIELD_ID = 12"); 
			execute(connection, "DELETE FROM CUSTOM_FIELD_DEFINITION WHERE FIELD_ID = 13"); 
			execute(connection, "DELETE FROM CUSTOM_FIELD_DEFINITION WHERE FIELD_ID = 7"); 
			execute(connection, "DELETE FROM CUSTOM_FIELD_DEFINITION WHERE FIELD_ID = 8"); 

			execute(connection, "DELETE FROM LOOKUP_VALUE_LOCALE WHERE LOOKUP_ID = 155"); 
			execute(connection, "DELETE FROM LOOKUP_VALUE_LOCALE WHERE LOOKUP_ID = 156"); 
			execute(connection, "DELETE FROM LOOKUP_VALUE_LOCALE WHERE LOOKUP_ID = 157"); 
			execute(connection, "DELETE FROM LOOKUP_VALUE_LOCALE WHERE LOOKUP_ID = 158"); 
			execute(connection, "DELETE FROM LOOKUP_VALUE_LOCALE WHERE LOOKUP_ID = 159"); 
			execute(connection, "DELETE FROM LOOKUP_VALUE_LOCALE WHERE LOOKUP_ID = 160"); 
			execute(connection, "DELETE FROM LOOKUP_VALUE_LOCALE WHERE LOOKUP_ID = 161"); 
			execute(connection, "DELETE FROM LOOKUP_VALUE_LOCALE WHERE LOOKUP_ID = 162"); 
			execute(connection, "DELETE FROM LOOKUP_VALUE_LOCALE WHERE LOOKUP_ID = 163"); 
			execute(connection, "DELETE FROM LOOKUP_VALUE_LOCALE WHERE LOOKUP_ID = 164"); 
			
			execute(connection, "DELETE FROM LOOKUP_VALUE WHERE LOOKUP_ID = 155"); 
			execute(connection, "DELETE FROM LOOKUP_VALUE WHERE LOOKUP_ID = 156"); 
			execute(connection, "DELETE FROM LOOKUP_VALUE WHERE LOOKUP_ID = 157"); 
			execute(connection, "DELETE FROM LOOKUP_VALUE WHERE LOOKUP_ID = 158"); 
			execute(connection, "DELETE FROM LOOKUP_VALUE WHERE LOOKUP_ID = 159"); 
			execute(connection, "DELETE FROM LOOKUP_VALUE WHERE LOOKUP_ID = 160"); 
			execute(connection, "DELETE FROM LOOKUP_VALUE WHERE LOOKUP_ID = 161"); 
			execute(connection, "DELETE FROM LOOKUP_VALUE WHERE LOOKUP_ID = 162"); 
			execute(connection, "DELETE FROM LOOKUP_VALUE WHERE LOOKUP_ID = 163"); 
			execute(connection, "DELETE FROM LOOKUP_VALUE WHERE LOOKUP_ID = 164"); 

			execute(connection, "DELETE FROM LOOKUP_LABEL WHERE LABEL_ID = 108"); 
			execute(connection, "DELETE FROM LOOKUP_LABEL WHERE LABEL_ID = 110"); 
			execute(connection, "DELETE FROM LOOKUP_LABEL WHERE LABEL_ID = 112"); 
			execute(connection, "DELETE FROM LOOKUP_LABEL WHERE LABEL_ID = 114"); 
			execute(connection, "DELETE FROM LOOKUP_LABEL WHERE LABEL_ID = 118"); 
			execute(connection, "DELETE FROM LOOKUP_LABEL WHERE LABEL_ID = 120"); 
			execute(connection, "DELETE FROM LOOKUP_LABEL WHERE LABEL_ID = 122"); 
			execute(connection, "DELETE FROM LOOKUP_LABEL WHERE LABEL_ID = 124"); 
			execute(connection, "DELETE FROM LOOKUP_LABEL WHERE LABEL_ID = 126"); 

			execute(connection, "DELETE FROM LOOKUP_ENTITY WHERE ENTITY_ID = 58 AND ENTITY_NAME = \'ReplacementStatus\'"); 
			execute(connection, "DELETE FROM LOOKUP_ENTITY WHERE ENTITY_ID = 59 AND ENTITY_NAME = \'GRTStaffId\'"); 
			execute(connection, "DELETE FROM LOOKUP_ENTITY WHERE ENTITY_ID = 60 AND ENTITY_NAME = \'MeetingTime\'"); 
			execute(connection, "DELETE FROM LOOKUP_ENTITY WHERE ENTITY_ID = 61 AND ENTITY_NAME = \'DistanceFromBoToCenter\'"); 
			execute(connection, "DELETE FROM LOOKUP_ENTITY WHERE ENTITY_ID = 63 AND ENTITY_NAME = \'NoOfClientsPerGroup\'"); 
			execute(connection, "DELETE FROM LOOKUP_ENTITY WHERE ENTITY_ID = 64 AND ENTITY_NAME = \'NoOfClientsPerCenter\'"); 
			execute(connection, "DELETE FROM LOOKUP_ENTITY WHERE ENTITY_ID = 65 AND ENTITY_NAME = \'DistanceFromHoToBO\'"); 
			execute(connection, "DELETE FROM LOOKUP_ENTITY WHERE ENTITY_ID = 66 AND ENTITY_NAME = \'ExternalLoanId\'"); 
			execute(connection, "DELETE FROM LOOKUP_ENTITY WHERE ENTITY_ID = 67 AND ENTITY_NAME = \'ExternalSavingsId\'");

			execute(connection, "UPDATE LOOKUP_ENTITY SET DESCRIPTION = \'External ID\' WHERE ENTITY_ID = 62 AND ENTITY_NAME = \'ExternalId\'");
		}
		upgradeVersion(connection);
	}

	@Override
	public void downgrade(Connection connection, DatabaseVersionPersistence databaseVersionPersistence) 
	throws IOException, SQLException {
		// if any custom fields have been added, then don't recreate default custom
		// fields when downgrading
		if (!CustomFieldsHaveBeenCreated(connection,"PERSONNEL_CUSTOM_FIELD", 0) &&
				!CustomFieldsHaveBeenCreated(connection,"OFFICE_CUSTOM_FIELD", 0) &&
				!CustomFieldsHaveBeenCreated(connection,"ACCOUNT_CUSTOM_FIELD", 0) &&
				!CustomFieldsHaveBeenCreated(connection,"CUSTOMER_CUSTOM_FIELD", 0)) {
			
			execute(connection, "UPDATE LOOKUP_ENTITY SET DESCRIPTION = \'Custom Field external ID for Personnel and office\' " +
				"WHERE ENTITY_ID = 62 AND ENTITY_NAME = \'ExternalId\'");
			
			execute(connection, "INSERT INTO LOOKUP_ENTITY(ENTITY_ID,ENTITY_NAME,DESCRIPTION) " + 
				"VALUES(58,\'ReplacementStatus\',\'Custom Field ReplacementStatus for Client\')");
			execute(connection, "INSERT INTO LOOKUP_ENTITY(ENTITY_ID,ENTITY_NAME,DESCRIPTION) " + 
				"VALUES(59,\'GRTStaffId\',\'Custom Field GRTStaffId for Group\')");
			execute(connection, "INSERT INTO LOOKUP_ENTITY(ENTITY_ID,ENTITY_NAME,DESCRIPTION) " + 
				"VALUES(60,\'MeetingTime\',\'Custom Field Meeting Time for Center\')");
			execute(connection, "INSERT INTO LOOKUP_ENTITY(ENTITY_ID,ENTITY_NAME,DESCRIPTION) " + 
				"VALUES(61,\'DistanceFromBoToCenter\',\'Custom Field Distance from BO To Center\')");
			execute(connection, "INSERT INTO LOOKUP_ENTITY(ENTITY_ID,ENTITY_NAME,DESCRIPTION) " + 
				"VALUES(63,\'NoOfClientsPerGroup\',\'Custom Field  No. of Clients per Group\')");
			execute(connection, "INSERT INTO LOOKUP_ENTITY(ENTITY_ID,ENTITY_NAME,DESCRIPTION) " + 
				"VALUES(64,\'NoOfClientsPerCenter\',\'Custom Field No. of Clients per Center\')");
			execute(connection, "INSERT INTO LOOKUP_ENTITY(ENTITY_ID,ENTITY_NAME,DESCRIPTION) " + 
				"VALUES(65,\'DistanceFromHoToBO\',\'Custom Field Distance from HO To BO for office\')");
			execute(connection, "INSERT INTO LOOKUP_ENTITY(ENTITY_ID,ENTITY_NAME,DESCRIPTION) " + 
				"VALUES(66,\'ExternalLoanId\',\'Custom Field ExternalID for office\')");
			execute(connection, "INSERT INTO LOOKUP_ENTITY(ENTITY_ID,ENTITY_NAME,DESCRIPTION) " + 
				"VALUES(67,\'ExternalSavingsId\',\'Custom Field ExternalSavingsId\')");

			execute(connection, "INSERT INTO LOOKUP_LABEL(LABEL_ID,ENTITY_ID,LOCALE_ID,ENTITY_NAME) " + 
				"VALUES(108,58,1,\'Replacement Status\')");
			execute(connection, "INSERT INTO LOOKUP_LABEL(LABEL_ID,ENTITY_ID,LOCALE_ID,ENTITY_NAME) " + 
				"VALUES(110,59,1,\'GRT Staff Id\')");
			execute(connection, "INSERT INTO LOOKUP_LABEL(LABEL_ID,ENTITY_ID,LOCALE_ID,ENTITY_NAME) " + 
				"VALUES(112,60,1,\'Meeting Time for Center\')");
			execute(connection, "INSERT INTO LOOKUP_LABEL(LABEL_ID,ENTITY_ID,LOCALE_ID,ENTITY_NAME) " + 
				"VALUES(114,61,1,\'Distance from BO to Center\')");
			execute(connection, "INSERT INTO LOOKUP_LABEL(LABEL_ID,ENTITY_ID,LOCALE_ID,ENTITY_NAME) " + 
				"VALUES(118,63,1,\'Number of Clients per Group\')");
			execute(connection, "INSERT INTO LOOKUP_LABEL(LABEL_ID,ENTITY_ID,LOCALE_ID,ENTITY_NAME) " + 
				"VALUES(120,64,1,\'Number of Clients per Center\')");
			execute(connection, "INSERT INTO LOOKUP_LABEL(LABEL_ID,ENTITY_ID,LOCALE_ID,ENTITY_NAME) " + 
				"VALUES(122,65,1,\'Distance from HO to BO for office\')");
			execute(connection, "INSERT INTO LOOKUP_LABEL(LABEL_ID,ENTITY_ID,LOCALE_ID,ENTITY_NAME) " + 
				"VALUES(124,66,1,\'External Loan Id\')");
			execute(connection, "INSERT INTO LOOKUP_LABEL(LABEL_ID,ENTITY_ID,LOCALE_ID,ENTITY_NAME) " + 
				"VALUES(126,67,1,\'External Savings Id\')");

			execute(connection, "INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) " + 
				"VALUES(155,58,\' \')");
			execute(connection, "INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) " + 
				"VALUES(156,59,\' \')");
			execute(connection, "INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) " + 
				"VALUES(157,60,\' \')");
			execute(connection, "INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) " + 
				"VALUES(158,61,\' \')");
			execute(connection, "INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) " + 
				"VALUES(159,62,\' \')");
			execute(connection, "INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) " + 
				"VALUES(160,63,\' \')");
			execute(connection, "INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) " + 
				"VALUES(161,64,\' \')");
			execute(connection, "INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) " + 
				"VALUES(162,65,\' \')");
			execute(connection, "INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) " + 
				"VALUES(163,66,\' \')");
			execute(connection, "INSERT INTO LOOKUP_VALUE(LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) " + 
				"VALUES(164,67,\' \')");

			execute(connection, "INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE) " + 
				"VALUES(285,1,155,\'Custom Field 1\')");
			execute(connection, "INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE) " + 
				"VALUES(287,1,156,\'Custom Field 2\')");
			execute(connection, "INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE) " + 
				"VALUES(289,1,157,\'Custom Field 3\')");
			execute(connection, "INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE) " + 
				"VALUES(291,1,158,\'Custom Field 4\')");
			execute(connection, "INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE) " + 
				"VALUES(293,1,159,\'Custom Field 5\')");
			execute(connection, "INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE) " + 
				"VALUES(295,1,160,\'Custom Field 6\')");
			execute(connection, "INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE) " + 
				"VALUES(297,1,161,\'Custom Field 7\')");
			execute(connection, "INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE) " + 
				"VALUES(299,1,162,\'Custom Field 8\')");
			execute(connection, "INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE) " + 
				"VALUES(301,1,163,\'Custom Field 9\')");
			execute(connection, "INSERT INTO LOOKUP_VALUE_LOCALE(LOOKUP_VALUE_ID,LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE) " + 
				"VALUES(303,1,164,\'Custom Field 10\')");

			execute(connection, "INSERT INTO CUSTOM_FIELD_DEFINITION(FIELD_ID,ENTITY_ID,FIELD_TYPE,ENTITY_TYPE,MANDATORY_FLAG,LEVEL_ID,DEFAULT_VALUE)  " + 
				"VALUES (3,58,2,1,0,1,NULL)");
			execute(connection, "INSERT INTO CUSTOM_FIELD_DEFINITION(FIELD_ID,ENTITY_ID,FIELD_TYPE,ENTITY_TYPE,MANDATORY_FLAG,LEVEL_ID,DEFAULT_VALUE)  " + 
				"VALUES (4,59,2,12,1,2,NULL)");
			execute(connection, "INSERT INTO CUSTOM_FIELD_DEFINITION(FIELD_ID,ENTITY_ID,FIELD_TYPE,ENTITY_TYPE,MANDATORY_FLAG,LEVEL_ID,DEFAULT_VALUE) " + 
				"VALUES (5,60,2,20,0,3,NULL)");
			execute(connection, "INSERT INTO CUSTOM_FIELD_DEFINITION(FIELD_ID,ENTITY_ID,FIELD_TYPE,ENTITY_TYPE,MANDATORY_FLAG,LEVEL_ID,DEFAULT_VALUE) " + 
				"VALUES (6,61,1,20,1,3,NULL)");
			execute(connection, "INSERT INTO CUSTOM_FIELD_DEFINITION(FIELD_ID,ENTITY_ID,FIELD_TYPE,ENTITY_TYPE,MANDATORY_FLAG,LEVEL_ID,DEFAULT_VALUE) " + 
				"VALUES (9,62,2,17,0,1,NULL)");
			execute(connection, "INSERT INTO CUSTOM_FIELD_DEFINITION(FIELD_ID,ENTITY_ID,FIELD_TYPE,ENTITY_TYPE,MANDATORY_FLAG,LEVEL_ID,DEFAULT_VALUE) " + 
				"VALUES (10,62,2,15,0,1,NULL)");
			execute(connection, "INSERT INTO CUSTOM_FIELD_DEFINITION(FIELD_ID,ENTITY_ID,FIELD_TYPE,ENTITY_TYPE,MANDATORY_FLAG,LEVEL_ID,DEFAULT_VALUE)  " + 
				"VALUES (11,63,1,15,1,2,NULL)");
			execute(connection, "INSERT INTO CUSTOM_FIELD_DEFINITION(FIELD_ID,ENTITY_ID,FIELD_TYPE,ENTITY_TYPE,MANDATORY_FLAG,LEVEL_ID,DEFAULT_VALUE) " + 
				"VALUES (12,64,1,15,1,2,NULL)");
			execute(connection, "INSERT INTO CUSTOM_FIELD_DEFINITION(FIELD_ID,ENTITY_ID,FIELD_TYPE,ENTITY_TYPE,MANDATORY_FLAG,LEVEL_ID,DEFAULT_VALUE) " + 
				"VALUES (13,65,1,15,0,2,NULL)");
			execute(connection, "INSERT INTO CUSTOM_FIELD_DEFINITION(FIELD_ID,ENTITY_ID,FIELD_TYPE,ENTITY_TYPE,MANDATORY_FLAG,LEVEL_ID,DEFAULT_VALUE) " + 
				"VALUES (7,66,2,22,0,1,NULL)");
			execute(connection, "INSERT INTO CUSTOM_FIELD_DEFINITION(FIELD_ID,ENTITY_ID,FIELD_TYPE,ENTITY_TYPE,MANDATORY_FLAG,LEVEL_ID,DEFAULT_VALUE) " + 
				"VALUES (8,67,2,21,0,1,NULL)");

			execute(connection, "INSERT INTO PERSONNEL_CUSTOM_FIELD (PERSONNEL_CUSTOM_FIELD_ID,FIELD_ID,PERSONNEL_ID,FIELD_VALUE) " + 
				"VALUES(1,9,1,\'\')");
			execute(connection, "INSERT INTO OFFICE_CUSTOM_FIELD (OFFICE_CUSTOM_FIELD_ID, OFFICE_ID,FIELD_ID,FIELD_VALUE) VALUES(1,1,10,\'\')");
			execute(connection, "INSERT INTO OFFICE_CUSTOM_FIELD (OFFICE_CUSTOM_FIELD_ID, OFFICE_ID,FIELD_ID,FIELD_VALUE) VALUES(2,1,11,\'\')");
			execute(connection, "INSERT INTO OFFICE_CUSTOM_FIELD (OFFICE_CUSTOM_FIELD_ID, OFFICE_ID,FIELD_ID,FIELD_VALUE) VALUES(3,1,12,\'\')");
			execute(connection, "INSERT INTO OFFICE_CUSTOM_FIELD (OFFICE_CUSTOM_FIELD_ID, OFFICE_ID,FIELD_ID,FIELD_VALUE) VALUES(4,1,13,\'\')");
		}
		downgradeVersion(connection);
	}

}
