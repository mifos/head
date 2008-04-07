package org.mifos.application.holiday.persistence;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
import org.mifos.application.master.business.MifosLookUpEntity;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.persistence.Upgrade;

public class Upgrade104 extends Upgrade {

	private Upgrade sameDay = new AddRepaymentRule(104, 
		RepaymentRuleTypes.SAME_DAY, DatabaseVersionPersistence.ENGLISH_LOCALE, "Same Day");
	private Upgrade nextMeetingOrRepayment = new AddRepaymentRule(104, 
		RepaymentRuleTypes.NEXT_MEETING_OR_REPAYMENT, 
		DatabaseVersionPersistence.ENGLISH_LOCALE, "Next Meeting/Repayment");
	private Upgrade nextWorkingDay = new AddRepaymentRule(104,
		RepaymentRuleTypes.NEXT_WORKING_DAY,
		DatabaseVersionPersistence.ENGLISH_LOCALE, "Next Working Day");
	
	public Upgrade104() {
		super(104);
	}

	@Override
	public void upgrade(Connection connection, DatabaseVersionPersistence databaseVersionPersistence) throws IOException, SQLException {
		execute(connection, "CREATE TABLE REPAYMENT_RULE ("
										+ "REPAYMENT_RULE_ID SMALLINT AUTO_INCREMENT NOT NULL,"
										+ "REPAYMENT_RULE_LOOKUP_ID INTEGER,"
										+ "PRIMARY KEY(REPAYMENT_RULE_ID),"
										+ "FOREIGN KEY(REPAYMENT_RULE_LOOKUP_ID)"
										+ "  REFERENCES LOOKUP_VALUE(LOOKUP_ID)"
										+ "  ON DELETE NO ACTION"
										+ "  ON UPDATE NO ACTION)"
										+ "ENGINE=InnoDB CHARACTER SET utf8");
		
		execute(connection, "DROP TABLE HOLIDAY");
		
		execute(connection, "CREATE TABLE HOLIDAY ("
						+ "OFFICE_ID SMALLINT NOT NULL,"
						+ "HOLIDAY_FROM_DATE DATE NOT NULL,"
						+ "HOLIDAY_THRU_DATE DATE,"
						+ "HOLIDAY_NAME VARCHAR(100),"
						+ "REPAYMENT_RULE_ID SMALLINT NOT NULL,"
						+ "PRIMARY KEY(OFFICE_ID, HOLIDAY_FROM_DATE),"
						+ "FOREIGN KEY(OFFICE_ID)"
						+ "  REFERENCES OFFICE(OFFICE_ID)"
						+ "  ON DELETE NO ACTION"
						+ "  ON UPDATE NO ACTION,"
						+ "FOREIGN KEY(REPAYMENT_RULE_ID)"
						+ "  REFERENCES REPAYMENT_RULE(REPAYMENT_RULE_ID)"
						+ "  ON DELETE NO ACTION"
						+ "  ON UPDATE NO ACTION)"
						+ "ENGINE=InnoDB CHARACTER SET utf8");
		
		addLookupEntity(connection, MifosLookUpEntity.REPAYMENT_RULE, "Repayment Rule", "Repayment Rule Types");
		
		sameDay.upgrade(connection, databaseVersionPersistence);
		nextMeetingOrRepayment.upgrade(connection, databaseVersionPersistence);
		nextWorkingDay.upgrade(connection, databaseVersionPersistence);
		
		upgradeVersion(connection);
	}

	@Override
	public void downgrade(Connection connection, DatabaseVersionPersistence databaseVersionPersistence) 
	throws IOException, SQLException {
		nextWorkingDay.downgrade(connection, databaseVersionPersistence);
		nextMeetingOrRepayment.downgrade(connection, databaseVersionPersistence);
		sameDay.downgrade(connection, databaseVersionPersistence);
		
		removeLookupEntity(connection, MifosLookUpEntity.REPAYMENT_RULE);

		execute(connection, "DROP TABLE HOLIDAY");
		execute(
			connection,
			"CREATE TABLE HOLIDAY (" +
			"  OFFICE_ID SMALLINT NOT NULL," +
			"  HOLIDAY_DATE DATE NOT NULL," +
			"  HOLIDATY_NAME VARCHAR(100)," +
			"  PRIMARY KEY(OFFICE_ID, HOLIDAY_DATE)," +
			"  FOREIGN KEY(OFFICE_ID)" +
			"    REFERENCES OFFICE(OFFICE_ID)" +
			"      ON DELETE NO ACTION" +
			"      ON UPDATE NO ACTION" +
			")" +
			"ENGINE=InnoDB CHARACTER SET utf8");

		execute(
			connection,
			"INSERT INTO HOLIDAY(OFFICE_ID, HOLIDAY_DATE, HOLIDATY_NAME)" +
			"VALUES(1,'2005-12-20','Chrismass')");
		execute(
			connection,
			"INSERT INTO HOLIDAY(OFFICE_ID, HOLIDAY_DATE, HOLIDATY_NAME)" +
			"VALUES(1,'2005-12-21','Diwali')");
		execute(
			connection,
			"INSERT INTO HOLIDAY(OFFICE_ID, HOLIDAY_DATE, HOLIDATY_NAME)" +
			"VALUES(1,'2005-12-10','Holi')");
		execute(
			connection,
			"INSERT INTO HOLIDAY(OFFICE_ID, HOLIDAY_DATE, HOLIDATY_NAME)" +
			"VALUES(1,'2005-12-11','Eid')");
		execute(
			connection,
			"INSERT INTO HOLIDAY(OFFICE_ID, HOLIDAY_DATE, HOLIDATY_NAME)" +
			"VALUES(1,'2005-12-22','Pongal')");

		execute(connection, "DROP TABLE REPAYMENT_RULE");

		downgradeVersion(connection);
	}

}
