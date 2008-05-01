package org.mifos.application.master.persistence;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.persistence.Upgrade;
import org.mifos.framework.util.helpers.StringUtils;
import org.mifos.framework.security.activity.DynamicLookUpValueCreationTypes;


public class Upgrade183 extends Upgrade{
	
	public Upgrade183() {
		super(183);
	}
	
	
	private void updateLookUpName(Connection connection, String lookUpName, Short lookUpId) throws SQLException
	{
		execute(connection, "UPDATE  LOOKUP_VALUE SET LOOKUP_NAME = \'" + lookUpName + "\' WHERE LOOKUP_ID="+ lookUpId);
	}
	
	private  void updateLookUpNames(Connection connection) throws SQLException
	{
		String unusedLookupName1 = "DBUpgrade.OfficeLevels.Unsued";
		String unusedLookupName2 = "DBUpgrade.PrdApplicableMaster.Unused";
		String unusedLookupName3 = "DBUpgrade.InterestCalcRule.Unused";
		String unusedLookupName4 = "DBUpgrade.Address3.Unused";
		String unusedLookupName5 = "DBUpgrade.City.Unused";
		String unusedLookupName6 = "DBUpgrade.LoanPurposes1.Unused";
		String unusedLookupName7 = "DBUpgrade.LoanPurposes2.Unused";
		
        
		Statement statement = connection.createStatement();
		String query = "select le.entity_name, lv.lookup_id from lookup_value lv, lookup_entity le " +
		"where lv.entity_id=le.entity_id and (lv.lookup_name=' ' or lv.lookup_name='' or lv.lookup_name is null)";
		try {
			ResultSet results = statement.executeQuery(query);
			if (results != null)
			{
				short counter = 0;
				while (results.next()) {
					String entityName = results.getString(1);
					Short lookUpId = results.getShort(2);
					String newElementText = null;
					String lookUpName = null;
					if (lookUpId.equals((short)65))
						lookUpName = unusedLookupName1;
					else if (lookUpId.equals((short)71))
						lookUpName = unusedLookupName2;
					else if (lookUpId.equals((short)90))
						lookUpName = unusedLookupName3;
					else if (lookUpId.equals((short)223))
						lookUpName = unusedLookupName4;
					else if (lookUpId.equals((short)224))
						lookUpName = unusedLookupName5;
					else if (lookUpId.equals((short)259))
						lookUpName = unusedLookupName6;
					else if (lookUpId.equals((short)263))
						lookUpName = unusedLookupName7;
					else
					{
						newElementText = entityName + counter;
						lookUpName = StringUtils.generateLookupName(DynamicLookUpValueCreationTypes.DBUpgrade.name(), newElementText);
						counter++;
					}
					updateLookUpName(connection, lookUpName, lookUpId);
				}
			}

		} finally {
			statement.close();
		}
		
	}
	
	private  void updateDuplicateLookUpName(Connection connection, String lookUpName, short count) throws SQLException
	{
		Statement statement = connection.createStatement();
		String query = "select lookup_id from lookup_value where lookup_name=\'" + lookUpName + "\' order by lookup_id";
		try 
		{
			ResultSet results = statement.executeQuery(query);
			
			if (results != null)
			{
				while (results.next()) {
					Short lookUpId = results.getShort(1);
					String newLookUpName = lookUpName + ".Duplicate." + lookUpId;
					updateLookUpName(connection, newLookUpName, lookUpId);
				}
			}
			
		}
		finally {
			statement.close();
		}
	}
	
	private  void fixDuplicateLookUpNames(Connection connection) throws SQLException
	{
		Statement statement = connection.createStatement();
		String query = "select lookup_name, count(lookup_name) from lookup_value group by lookup_name having count(lookup_name) > 1";
		try 
		{
			ResultSet results = statement.executeQuery(query);
			if (results != null)
			{
				while (results.next()) {
					String lookUpName = results.getString(1);
					Short count = results.getShort(2);
					while (count > 0)
					{
						updateDuplicateLookUpName(connection, lookUpName, count);
						count--;
					}
				}
			}
			
		}
		finally {
			statement.close();
		}
	}
	
	@Override
	public void upgrade(Connection connection, DatabaseVersionPersistence databaseVersionPersistence) throws IOException, SQLException {
		updateLookUpNames(connection);
		fixDuplicateLookUpNames(connection);
		addUniqueToLookUpName(connection);
		upgradeVersion(connection);
	}
	
	private void addUniqueToLookUpName(Connection connection) throws SQLException
	{
		execute(connection, "CREATE UNIQUE INDEX LOOKUP_NAME_IDX ON LOOKUP_VALUE(LOOKUP_NAME)");
	}
	
}
