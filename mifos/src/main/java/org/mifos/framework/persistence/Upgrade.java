/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.framework.persistence;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.StringUtils;

public abstract class Upgrade {

    private final int higherVersion;
    protected static final int lookupValueChangeVersion = 174;
    public static final String wrongConstructor = "This db version is higher than 174 so it needs to use the constructor with lookupValueKey parameter.";

    protected Upgrade(int higherVersion) {
        this.higherVersion = higherVersion;
    }

    abstract public void upgrade(Connection connection, DatabaseVersionPersistence databaseVersionPersistence)
            throws IOException, SQLException;

    public static boolean validateLookupValueKey(String format, String key) {
        if (!StringUtils.isNullAndEmptySafe(key))
            return false;
        if (!key.startsWith(format, 0))
            return false;
        return true;
    }

    public int higherVersion() {
        return higherVersion;
    }

    public int lowerVersion() {
        return higherVersion - 1;
    }

    protected void upgradeVersion(Connection connection) throws SQLException {
        changeVersion(connection, higherVersion(), lowerVersion());
    }

    private void changeVersion(Connection connection, int newVersion, int existingVersion) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("update DATABASE_VERSION "
                + "set DATABASE_VERSION = ? where DATABASE_VERSION = ?");
        statement.setInt(1, newVersion);
        statement.setInt(2, existingVersion);
        statement.executeUpdate();
        connection.commit();
    }

    protected void insertMessage(Connection connection, int lookupId, Short localeToInsert, String message)
            throws SQLException {
        PreparedStatement statement = connection.prepareStatement("insert into LOOKUP_VALUE_LOCALE("
                + "LOCALE_ID,LOOKUP_ID,LOOKUP_VALUE) " + "VALUES(?,?,?)");
        statement.setInt(1, localeToInsert);
        statement.setInt(2, lookupId);
        statement.setString(3, message);
        statement.executeUpdate();
        statement.close();
    }

    protected static void updateMessage(Connection connection, int lookupId, short locale, String newMessage)
            throws SQLException {
        PreparedStatement statement = connection.prepareStatement("update LOOKUP_VALUE_LOCALE set LOOKUP_VALUE = ? "
                + "where LOCALE_ID = ? and LOOKUP_ID = ?");
        statement.setString(1, newMessage);
        statement.setInt(2, locale);
        statement.setInt(3, lookupId);
        statement.executeUpdate();
        statement.close();
    }

    /**
     * This method is used for version 174 and lower (it was used in Upgrade169)
     * and must not be used after 174 because after 174, a lookup key is
     * required for a lookup value to be displayed. Prior to version 174 an
     * empty (" ") key was passed in because the key was unused.
     * 
     * @deprecated
     */
    protected int insertLookupValue(Connection connection, int lookupEntity) throws SQLException {
        return insertLookupValue(connection, lookupEntity, " ");
    }

    /**
     * Add a new Lookup Value. A Lookup Value is a string key that is resolved
     * to a message by looking up the key value in a properties file. The
     * message can be overridden by a LookupValueLocale in the database that is
     * associated with a given LookupValue.
     * 
     * @param connection
     *            the database connection to use
     * @param lookupEntity
     *            the primary key of the lookup entity that this key is
     *            associated with
     * @param lookupKey
     *            the string key to lookup in a properties file to get the
     *            message to display
     * @return the newly generated lookup id (primary key) for the lookup value
     *         just inserted
     * @throws SQLException
     */
    protected int insertLookupValue(Connection connection, int lookupEntity, String lookupKey) throws SQLException {
        /*
         * LOOKUP_ID is not AUTO_INCREMENT until database version 121. Although
         * we perhaps could try to work some magic with the upgrades, it seems
         * better to just insert in the racy way until then. Upgrades run
         * single-threaded before the application allows logins, so I think this
         * is fine.
         */
        int largestLookupId = largestLookupId(connection);

        int newLookupId = largestLookupId + 1;
        PreparedStatement statement = connection.prepareStatement("insert into LOOKUP_VALUE("
                + "LOOKUP_ID,ENTITY_ID,LOOKUP_NAME) " + "VALUES(?,?,?)");
        statement.setInt(1, newLookupId);
        statement.setInt(2, lookupEntity);
        statement.setString(3, lookupKey);

        statement.executeUpdate();
        statement.close();
        return newLookupId;
    }

    protected int largestLookupId(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet results = statement.executeQuery("select max(lookup_id) from LOOKUP_VALUE");
        if (!results.next()) {
            throw new SystemException(SystemException.DEFAULT_KEY,
                    "Did not find an existing lookup_id in lookup_value table");
        }
        int largestLookupId = results.getInt(1);
        results.close();
        statement.close();
        return largestLookupId;
    }

    protected void deleteFromLookupValueLocale(Connection connection, short lookupId) throws SQLException {
        PreparedStatement statement = connection
                .prepareStatement("delete from LOOKUP_VALUE_LOCALE where lookup_id = ?");
        statement.setInt(1, lookupId);
        statement.executeUpdate();
        statement.close();
    }

    protected void deleteFromLookupValue(Connection connection, short lookupId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("delete from LOOKUP_VALUE where lookup_id = ?");
        statement.setInt(1, lookupId);
        statement.executeUpdate();
        statement.close();
    }

    protected void addLookupEntity(Connection connection, int entityId, String name, String description)
            throws SQLException {
        PreparedStatement statement = connection
                .prepareStatement("INSERT INTO LOOKUP_ENTITY(ENTITY_ID,ENTITY_NAME,DESCRIPTION)" + "VALUES(?,?,?)");
        statement.setInt(1, entityId);
        statement.setString(2, name);
        statement.setString(3, description);
        statement.executeUpdate();
        statement.close();
    }

    protected void removeLookupEntity(Connection connection, int entityId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("DELETE FROM LOOKUP_ENTITY WHERE ENTITY_ID = ?");
        statement.setInt(1, entityId);
        statement.executeUpdate();
        statement.close();
    }

    protected void execute(Connection connection, String sql) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.executeUpdate();
        statement.close();
    }

    /**
     * By default a single head office is present in a clean database, so no
     * offices have been created if there is only one office row present. This
     * test is being used to determine if the database has no user data in it.
     */
    protected boolean noOfficesHaveBeenCreatedByEndUsers(Connection connection) throws SQLException {
        return countRows(connection, "OFFICE") == 1;
    }

    private int countRows(Connection connection, String tableName) throws SQLException {

        int numFields = 0;
        Statement statement = connection.createStatement();
        try {
            ResultSet results = statement.executeQuery("select count(*) from " + tableName);
            if (!results.next()) {
                throw new SystemException(SystemException.DEFAULT_KEY, "Query failed on table: " + tableName);
            }
            numFields = results.getInt(1);

        } finally {
            statement.close();
        }
        return numFields;

    }

}
