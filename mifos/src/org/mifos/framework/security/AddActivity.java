package org.mifos.framework.security;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mifos.application.master.business.LookUpEntity;
import org.mifos.application.rolesandpermission.util.helpers.RolesAndPermissionConstants;
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
		int lookupEntity = LookUpEntity.ACTIVITY;

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
		statement.close();
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

}
