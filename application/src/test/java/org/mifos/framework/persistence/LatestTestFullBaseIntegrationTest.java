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
import static org.mifos.framework.persistence.DatabaseVersionPersistence.FIRST_NUMBERED_VERSION;
import static org.mifos.framework.util.helpers.DatabaseSetup.executeScript;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;

import junit.framework.Assert;

import org.dbunit.Assertion;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mifos.application.accounts.financial.util.helpers.FinancialInitializer;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

/*
 * This class runs tests on database upgrade scripts (both SQL
 * based and java based).  It uses the earliest version of the database
 * that supported upgrade scripts as a starting point and will run
 * through all upgrades.  You can expect this to take a long
 * time to run.
 * 
 * This test is ignored because it wasn't a part of old suite arch.
 * 
 */
@Ignore
public class LatestTestFullBaseIntegrationTest {

    private static Connection connection;

    @BeforeClass
    public static void beforeClass() throws Exception {
        MifosLogManager.configureLogging();
        StaticHibernateUtil.initialize();
        connection = StaticHibernateUtil.getSessionTL().connection();
        connection.setAutoCommit(false);
    }

    @Before
    public void setUp() throws Exception {
        dropLatestDatabase();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        // Cleaning the database
        executeScript("mifosdroptables.sql", connection);
        executeScript("latest-schema.sql", connection);
        executeScript("latest-data.sql", connection);
        executeScript("custom_data.sql", connection);
        executeScript("testdbinsertionscript.sql", connection);
        connection.commit();
        FinancialInitializer.initialize();
        StaticHibernateUtil.flushAndCloseSession();
    }

    @Test
    public void testRealSchema() throws Exception {
        createLatestDatabaseWithLatestData();
        Assert.assertEquals(DatabaseVersionPersistence.APPLICATION_VERSION, new DatabaseVersionPersistence(connection)
                .read());

        IDatabaseConnection dbUnitConnection = new DatabaseConnection(connection);
        IDataSet latestDataDump = dbUnitConnection.createDataSet();
        String latestDump = TestDatabase.getAllTablesStructureDump();

        dropLatestDatabase();

        TestDatabase.upgradeToFirstNumberedVersion(connection);
        TestDatabase.runUpgradeScripts(connection);

        String upgradeDump = TestDatabase.getAllTablesStructureDump();
        IDataSet upgradeDataDump = dbUnitConnection.createDataSet();
        Assert.assertEquals(latestDump, upgradeDump);
        Assertion.assertEquals(latestDataDump, upgradeDataDump);
    }

    @Test
    public void testAfterLookupValues() throws Exception {
        TestDatabase.upgradeToFirstNumberedVersion(connection);
        /*
         * A customer will typically add records such as these during
         * customization.
         */
        connection.createStatement().execute(
                "insert into LOOKUP_VALUE(LOOKUP_ID, ENTITY_ID, LOOKUP_NAME) " + "VALUES(569,19,' ')");
        connection.createStatement().execute(
                "insert into LOOKUP_VALUE_LOCALE(LOCALE_ID, LOOKUP_ID, LOOKUP_VALUE) " + "VALUES(1,569,'Martian')");

        upgradeAllFromVersion(FIRST_NUMBERED_VERSION);

        // Assert that custom values have been retained
        ResultSet rs = connection.createStatement().executeQuery("select * from lookup_value where lookup_id=569");
        rs.next();
        Assert.assertEquals(19, rs.getInt("entity_id"));
        Assert.assertEquals(" ", rs.getString("lookup_name"));

        rs = connection.createStatement().executeQuery("select * from lookup_value_locale where lookup_id=569");
        rs.next();
        Assert.assertEquals(1, rs.getInt("locale_id"));
        Assert.assertEquals("Martian", rs.getString("lookup_value"));
        rs.close();
    }

    private void upgradeAllFromVersion(int fromVersion) throws Exception {
        for (int currentVersion = fromVersion; currentVersion < APPLICATION_VERSION; ++currentVersion) {
            int higherVersion = currentVersion + 1;
            try {
                upgradeNextVersion(higherVersion);
            } catch (Exception failure) {
                throw new Exception("Cannot upgrade to " + higherVersion, failure);
            }
        }
    }

    private void upgradeNextVersion(int nextVersion) throws Exception {
        DatabaseVersionPersistence persistence = new DatabaseVersionPersistence(connection);
        Upgrade upgrade = persistence.findUpgrade(nextVersion);
        if (upgrade instanceof SqlUpgrade)
            assertNoHardcodedValues((SqlUpgrade) upgrade, nextVersion);

        upgrade.upgrade(connection);
    }

    private void assertNoHardcodedValues(SqlUpgrade upgrade, int version) throws Exception {
        String[] sqlStatements = SqlExecutor.readFile((InputStream) upgrade.sql().getContent());
        for (int i = 0; i < sqlStatements.length; i++) {
            Assert.assertTrue("Upgrade " + version + " contains hard-coded lookup values", HardcodedValues
                    .checkLookupValue(sqlStatements[i]));
            Assert.assertTrue("Upgrade " + version + " contains hard-coded lookup value locales", HardcodedValues
                    .checkLookupValueLocale(sqlStatements[i]));
        }
    }

    private void createLatestDatabaseWithLatestData() throws Exception {
        executeScript("latest-schema.sql", connection);
        executeScript("latest-data.sql", connection);
        connection.commit();
    }

    private void dropLatestDatabase() throws Exception {
        executeScript("mifosdroptables.sql", connection);
        connection.commit();
    }
}
