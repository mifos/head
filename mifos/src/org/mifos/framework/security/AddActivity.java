package org.mifos.framework.security;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.mifos.application.rolesandpermission.util.helpers.RolesAndPermissionConstants;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.persistence.Upgrade;

public class AddActivity extends Upgrade {

	private final int newActivityId;
	private final Short locale;
	private final String activityName;
	private final short parentActivity;

	/**
	 * Define an activity and one name for it.  If you want
	 * to give it names in multiple locales, this upgrade can not
	 * yet do that.
	 * 
	 * @param newActivityId ID for the activity we are creating
	 * @param parentActivity existing ID for the parent
	 * @param locale Locale in which we want to define a name
	 * @param activityName Name to give the activity, in that locale.
	 */
	public AddActivity(
		int higherVersion, int newActivityId, short parentActivity, 
		Short locale, String activityName) {
		super(higherVersion);
		this.newActivityId = newActivityId;
		this.parentActivity = parentActivity;
		this.locale = locale;
		this.activityName = activityName;
	}

	@Override
	public void upgrade(Connection connection) throws IOException, SQLException {
		/* TODO: enumify.  But existing or new enum type?  There is
		   LookUpEntity but I don't see an enum which corresponds.
		   */
		int lookupEntity = 87;

		int lookupId = insertLookupValue(connection, lookupEntity);
		insertMessage(connection, lookupId, locale, activityName);
		addActivityEntity(connection, lookupId);
		allowActivity(connection, newActivityId, 
			RolesAndPermissionConstants.ADMIN_ROLE);
		
		upgradeVersion(connection);
	}

	private void allowActivity(Connection connection, 
		int activityId, int roleId) 
	throws SQLException {
		PreparedStatement statement = connection.prepareStatement(
			"insert into ROLES_ACTIVITY(ACTIVITY_ID, ROLE_ID) VALUES(?, ?)");
		statement.setInt(1, activityId);
		statement.setInt(2, roleId);
		statement.executeUpdate();
		statement.close();
	}

	private void insertMessage(Connection connection, int lookupId, 
			Short localeToInsert, String nameToInsert) 
	throws SQLException {
		PreparedStatement statement = connection.prepareStatement(
			"insert into LOOKUP_VALUE_LOCALE(" +
			"LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE) " +
			"VALUES(?,?,?)");
		statement.setInt(1, localeToInsert);
		statement.setInt(2, lookupId);
		statement.setString(3, nameToInsert);
		statement.executeUpdate();
		statement.close();
	}

	private int insertLookupValue(Connection connection, int lookupEntity) 
	throws SQLException {
		/* LOOKUP_ID is not AUTO_INCREMENT until database version 121.
		   Although we perhaps could try to work some magic with the
		   upgrades, it seems better to just insert in the racy way
		   until then.  Upgrades run single-threaded before the 
		   application allows logins, so I think this is fine. */
		int largestLookupId = largestLookupId(connection);
		
		int newLookupId = largestLookupId + 1;
		PreparedStatement statement = connection.prepareStatement(
			"insert into LOOKUP_VALUE(" +
			"LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) " +
			"VALUES(?,?,?)");
		statement.setInt(1, newLookupId);
		statement.setInt(2, lookupEntity);
		
		/* Pretty much all existing code inserts a space here.
		   I'm not sure this field is used for anything. */
		statement.setString(3, " ");

		statement.executeUpdate();
		statement.close();
		return newLookupId;
	}

	private int largestLookupId(Connection connection) throws SQLException {
		Statement statement = connection.createStatement();
		ResultSet results = statement.executeQuery(
			"select max(lookup_id) from LOOKUP_VALUE");
		if (!results.next()) {
			throw new SystemException(SystemException.DEFAULT_KEY, 
				"Did not find an existing lookup_id in lookup_value table");
		}
		int largestLookupId = results.getInt(1);
		results.close();
		statement.close();
		return largestLookupId;
	}

	private void addActivityEntity(Connection connection, int lookupId) 
	throws SQLException {
		PreparedStatement statement = connection.prepareStatement(
			"insert into ACTIVITY(" +
			"ACTIVITY_ID,PARENT_ID," +
			"ACTIVITY_NAME_LOOKUP_ID,DESCRIPTION_LOOKUP_ID) " +
			"VALUES(?,?,?,?)");
		statement.setInt(1, newActivityId);
		statement.setInt(2, parentActivity);
		statement.setInt(3, lookupId);
		statement.setInt(4, lookupId);
		statement.executeUpdate();
		statement.close(); // should be in finally(?)
	}

	@Override
	public void downgrade(Connection connection) throws IOException, SQLException {
		short lookupId = findLookupId(connection);
		deleteFromRolesActivity(connection);
		deleteFromActivity(connection);
		deleteFromLookupValueLocale(connection, lookupId);
		deleteFromLookupValue(connection, lookupId);
		downgradeVersion(connection);
	}

	private short findLookupId(Connection connection) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(
			"select ACTIVITY_NAME_LOOKUP_ID " +
			"from ACTIVITY where activity_id = ?");
		statement.setInt(1, newActivityId);
		ResultSet results = statement.executeQuery();
		if (results.next()) {
			short lookupId = results.getShort("ACTIVITY_NAME_LOOKUP_ID");
			statement.close();
			return lookupId;
		}
		else {
			statement.close();
			throw new RuntimeException(
				"unable to downgrade: no activity with id " + newActivityId);
		}
	}

	private void deleteFromRolesActivity(Connection connection) 
	throws SQLException {
		PreparedStatement statement = connection.prepareStatement(
			"delete from ROLES_ACTIVITY where ACTIVITY_ID = ?");
		statement.setInt(1, newActivityId);
		statement.executeUpdate();
		statement.close();
	}

	private void deleteFromActivity(Connection connection) 
	throws SQLException {
		PreparedStatement statement = connection.prepareStatement(
			"delete from ACTIVITY where ACTIVITY_ID = ?");
		statement.setInt(1, newActivityId);
		statement.executeUpdate();
		statement.close();
	}

	private void deleteFromLookupValueLocale(
		Connection connection, short lookupId) 
	throws SQLException {
		PreparedStatement statement = connection.prepareStatement(
			"delete from LOOKUP_VALUE_LOCALE where lookup_id = ?");
		statement.setInt(1, lookupId);
		statement.executeUpdate();
		statement.close();
	}

	private void deleteFromLookupValue(
			Connection connection, short lookupId) 
	throws SQLException {
		PreparedStatement statement = connection.prepareStatement(
			"delete from LOOKUP_VALUE where lookup_id = ?");
		statement.setInt(1, lookupId);
		statement.executeUpdate();
		statement.close();
	}

}
