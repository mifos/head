package org.mifos.framework.security;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.mifos.framework.persistence.Upgrade;

public class AddActivity extends Upgrade {

	private final int newActivityId;
	private final Short locale;
	private final String activityName;

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
	protected AddActivity(
		int higherVersion, int newActivityId, short parentActivity, 
		Short locale, String activityName) {
		super(higherVersion);
		this.newActivityId = newActivityId;
		this.locale = locale;
		this.activityName = activityName;
	}

	@Override
	public void downgrade(Connection connection) throws IOException, SQLException {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void upgrade(Connection connection) throws IOException, SQLException {
		int lookupEntity = 87; // TODO: enumify.  But existing or new enum type?
		int lookupId = insertLookupValue(connection, lookupEntity);
		insertMessage(connection, lookupId, locale, activityName);
		addActivityEntity(connection, lookupId);
	}

	private void insertMessage(Connection connection, int lookupId, 
			Short localeToInsert, String nameToInsert) {
	}

	private int insertLookupValue(Connection connection, int lookupEntity) {
		// insert into LOOKUP_VALUE table (TODO: make LOOKUP_ID AUTO_INCREMENT)
		return 0;
	}

	private void addActivityEntity(Connection connection, int lookupId) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(
			"insert into ACTIVITY(" +
			"ACTIVITY_ID,PARENT_ID," +
			"ACTIVITY_NAME_LOOKUP_ID,DESCRIPTION_LOOKUP_ID) " +
			"VALUES(?,null,?,?)");
		statement.setInt(1, newActivityId);
		//		statement.setObject(2, null);
		statement.setInt(2, lookupId);
		statement.setInt(3, lookupId);
		statement.executeUpdate();
	}

}
