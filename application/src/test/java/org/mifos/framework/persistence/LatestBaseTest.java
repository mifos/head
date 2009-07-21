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

import static org.mifos.framework.persistence.DatabaseVersionPersistence.APPLICATION_VERSION;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.sourceforge.mayfly.Database;
import net.sourceforge.mayfly.datastore.DataStore;

import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.DatabaseSetup;

/**
 * Contains common methods used to test database upgrade scripts on the test
 * classes that extend this base class.
 */
public class LatestBaseTest extends TestCase {

    protected int version(Database database) throws SQLException {
        return new DatabaseVersionPersistence(database.openConnection()).read();
    }

    /**
     * Similar to what we get from {@link DatabaseSetup#getStandardStore()} but
     * without testdbinsertionscript.sql.
     */
    protected void loadRealLatest(Database database) {
        DatabaseSetup.executeScript(database, "latest-schema.sql");
        DatabaseSetup.executeScript(database, "latest-data.sql");
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

    protected DataStore upgrade(int fromVersion, DataStore current) throws Exception {
        for (int currentVersion = fromVersion; currentVersion < APPLICATION_VERSION; ++currentVersion) {
            int higherVersion = currentVersion + 1;
            try {
                current = upgrade(current, higherVersion);
            } catch (Exception failure) {
                throw new Exception("Cannot upgrade to " + higherVersion, failure);
            }
        }
        return current;
    }

    protected DataStore upgrade(DataStore current, int nextVersion) throws Exception {
        Database database = new Database(current);
        DatabaseVersionPersistence persistence = new DatabaseVersionPersistence(database.openConnection());
        Upgrade upgrade = persistence.findUpgrade(nextVersion);
        if (upgrade instanceof SqlUpgrade)
            assertNoHardcodedValues((SqlUpgrade) upgrade, nextVersion);

        upgrade.upgrade(database.openConnection());
        return database.dataStore();
    }

    private void assertNoHardcodedValues(SqlUpgrade upgrade, int version) throws Exception {
        String[] sqlStatements = SqlUpgrade.readFile((InputStream) upgrade.sql().getContent());
        for (int i = 0; i < sqlStatements.length; i++) {
            Assert.assertTrue("Upgrade " + version + " contains hard-coded lookup values", HardcodedValues
                    .checkLookupValue(sqlStatements[i]));
            Assert.assertTrue("Upgrade " + version + " contains hard-coded lookup value locales", HardcodedValues
                    .checkLookupValueLocale(sqlStatements[i]));
        }
    }
}
