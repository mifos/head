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

import static org.mifos.framework.persistence.DatabaseVersionPersistence.FIRST_NUMBERED_VERSION;
import static org.mifos.framework.util.helpers.DatabaseSetup.executeScript;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import junit.framework.Assert;
import net.sourceforge.mayfly.Database;
import net.sourceforge.mayfly.dump.SqlDumper;

import org.hibernate.Interceptor;
import org.hibernate.classic.Session;
import org.mifos.application.accounts.financial.util.helpers.FinancialInitializer;
import org.mifos.framework.components.audit.util.helpers.AuditInterceptor;
import org.mifos.framework.hibernate.helper.SessionHolder;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.StandardTestingService;
import org.mifos.framework.util.helpers.DatabaseSetup;

public class TestDatabase implements SessionOpener {

    private Database database = new Database();

    /**
     * Make a {@link Database} which is empty.
     */
    public static Database makeDatabase() {
        Database database = new Database();
        setOptions(database);
        return database;
    }

    /**
     * Make a {@link TestDatabase} which contains the standard data store (all
     * mifos tables, etc). This will be slow for the first test which calls it,
     * but fast for the rest of a test run.
     */
    public static TestDatabase makeStandard() {
        /*
         * We do not call DatabaseSetup.initializeHibernate, at least for now,
         * because we want to make tests that call it more clearly show
         * themselves as slow tests.
         */

        return new TestDatabase();
    }

    public static void setOptions(Database database) {
        database.tableNamesCaseSensitive(true);
    }

    private TestDatabase() {
        database = new Database(DatabaseSetup.getStandardStore());
        setOptions(database);
    }

    public void execute(String sql) {
        database.execute(sql);
    }

    public Session openSession() {
        return DatabaseSetup.mayflySessionFactory().openSession(database.openConnection());
    }

    public Session openSession(Interceptor interceptor) {
        return DatabaseSetup.mayflySessionFactory().openSession(database.openConnection(), interceptor);
    }

    public void dump() throws IOException {
        /*
         * What we'd really like to do, I guess, is just dump that data which
         * isn't in standardStore(). Hmm, seems like a possible mayfly project.
         * If we did that, it could go to the console without filling up
         * Eclipse's console buffer.
         */
        // OutputStreamWriter writer = new OutputStreamWriter(System.out);
        Writer writer = new FileWriter("build/database-dump.sql");
        new SqlDumper().dump(database.dataStore(), writer);
        writer.flush();
    }

    public String dumpForComparison() throws IOException {
        StringWriter writer = new StringWriter();
        new SqlDumper(false).dump(database.dataStore(), writer);
        writer.flush();
        return writer.toString();
    }

    public SessionHolder open() {
        return new SessionHolder(openSession());
    }

    public Connection openConnection() {
        return database.openConnection();
    }

    /**
     * This is for tests where it is difficult to pass around the Session.
     * 
     * Thus, we install it in a static in StaticHibernateUtil.
     * 
     * Make sure to call {@link StaticHibernateUtil#resetDatabase()} from
     * tearDown.
     */
    public Session installInThreadLocal() {
        StaticHibernateUtil.closeSession();
        AuditInterceptor interceptor = new AuditInterceptor();
        Session session1 = openSession(interceptor);
        SessionHolder holder = new SessionHolder(session1);
        holder.setInterceptor(interceptor);
        StaticHibernateUtil.setThreadLocal(holder);
        return session1;
    }

    public static void runUpgradeScripts(Connection connection) throws Exception {
        runUpgradeScripts(FIRST_NUMBERED_VERSION, connection);
    }

    public static void runUpgradeScripts(int fromVersion, Connection connection) throws Exception {
        DatabaseVersionPersistence persistence = new DatabaseVersionPersistence(connection);
        Assert.assertEquals(fromVersion, persistence.read());
        persistence.upgradeDatabase();
    }

    /**
     * Create a database and upgrade it to the first database version with a
     * number. Should be run on an empty database (no tables).
     * 
     * @throws IOException
     */
    public static void upgradeToFirstNumberedVersion(Connection connection) throws SQLException, IOException {
        executeScript("mifosdbcreationscript.sql", connection);
        executeScript("mifosmasterdata.sql", connection);
        executeScript("rmpdbcreationscript.sql", connection);
        executeScript("rmpmasterdata.sql", connection);
        executeScript("Iteration13-DBScripts25092006.sql", connection);
        executeScript("Iteration14-DDL-DBScripts10102006.sql", connection);
        executeScript("Iteration14-DML-DBScripts10102006.sql", connection);
        executeScript("Iteration15-DDL-DBScripts24102006.sql", connection);
        executeScript("Iteration15-DBScripts20061012.sql", connection);
        executeScript("add-version.sql", connection);
        executeScript("Index.sql", connection);
    }

    /**
     * This method was added to work around integration test inter- and
     * intradependencies. Once these dependencies are eliminated (hopefully
     * Summer 2009), this method should be eliminated as well.
     */
    public static void resetMySQLDatabase() throws Exception {
        StaticHibernateUtil.flushAndClearSession();

        Connection connection = getJDBCConnection();
        connection.setAutoCommit(false);
        executeScript("truncate_tables.sql", connection);
        executeScript("latest-data.sql", connection);
        executeScript("custom_data.sql", connection);
        executeScript("testdbinsertionscript.sql", connection);
        connection.commit();
        connection.close();

        // If the database is ever blown away, we must repopulate chart of
        // accounts data since some unit tests rely on its presence. It must
        // be created via this method since adding it via an sql script would
        // invalidate *other* unit tests that assume this method has been
        // called.
        FinancialInitializer.initialize();
    }

    /**
     * MySQL specific schema dump generation
     * 
     * @return database structure as String dump
     * @throws Exception
     */
    public static String getAllTablesStructureDump() throws Exception {
        String tablesDumpList = "";
        Connection connection = getJDBCConnection();
        ResultSet rs = connection.createStatement().executeQuery("SHOW TABLES");
        while (rs.next()) {
            String tableDump = getTableStructureDump(rs.getString(1), connection);
            tablesDumpList += tableDump;
        }
        connection.close();
        return tablesDumpList;
    }

    private static String getTableStructureDump(String tableName, Connection connection) throws Exception {
        StringBuilder sb = new StringBuilder();
        ResultSet rs = connection.createStatement().executeQuery("DESCRIBE " + tableName);
        sb.append(tableName).append(" ");
        while (rs.next()) {
            sb.append("\n").append(rs.getString(1)).append(" ");
            sb.append(rs.getString(2)).append(" ");
            sb.append(rs.getString(3)).append(" NULL ");
            sb.append(rs.getString(4)).append(" KEY ");
            if(rs.getString(5) != null) {
            sb.append(rs.getString(5)).append(" ");
            }
            if(rs.getString(6) != null) {
            sb.append(rs.getString(6)).append(",");
            }
        }
        sb.append(";").append("\n");
        return sb.toString();
    }

    // FIXME Use Spring Managed Connection

    private static Connection getJDBCConnection() throws Exception {
        final Properties databaseSettings = new StandardTestingService().getDatabaseConnectionSettings();
        final String noFkChecksUrl = databaseSettings.getProperty("hibernate.connection.url")
                + "&sessionVariables=FOREIGN_KEY_CHECKS=0";
        return DriverManager.getConnection(noFkChecksUrl,
                databaseSettings.getProperty("hibernate.connection.username"), databaseSettings
                        .getProperty("hibernate.connection.password"));
    }
}
