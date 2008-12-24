package org.mifos.framework.persistence;

import static org.mifos.framework.persistence.DatabaseVersionPersistence.FIRST_NUMBERED_VERSION;
import static org.mifos.framework.util.helpers.DatabaseSetup.executeScript;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;

import net.sourceforge.mayfly.Database;
import net.sourceforge.mayfly.dump.SqlDumper;

import org.hibernate.Interceptor;
import org.hibernate.classic.Session;
import org.junit.Assert;
import org.mifos.application.accounts.financial.util.helpers.FinancialInitializer;
import org.mifos.framework.components.audit.util.helpers.AuditInterceptor;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.hibernate.helper.SessionHolder;
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
	 * Make a {@link TestDatabase} which contains the
	 * standard data store (all mifos tables, etc).
	 * This will be slow for the first test which calls
	 * it, but fast for the rest of a test run.
	 */
	public static TestDatabase makeStandard() {
		/* We do not call DatabaseSetup.initializeHibernate,
		   at least for now, because we want to make tests
		   that call it more clearly show themselves
		   as slow tests. */

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
		return DatabaseSetup.mayflySessionFactory()
			.openSession(database.openConnection());
	}
	
	public Session openSession(Interceptor interceptor) {
		return DatabaseSetup.mayflySessionFactory().openSession(
				database.openConnection(), interceptor);
	}

	public void dump() throws IOException {
		/* What we'd really like to do, I guess, is just dump that
		   data which isn't in standardStore().  Hmm, seems like
		   a possible mayfly project.  If we did that, it could go
		   to the console without filling up Eclipse's console buffer. */
//		OutputStreamWriter writer = new OutputStreamWriter(System.out);
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
	 * Thus, we install it in a static in HibernateUtil.
	 * 
	 * Make sure to call {@link HibernateUtil#resetDatabase()} from tearDown.
	 */
	public Session installInThreadLocal() {
		HibernateUtil.closeSession();
		AuditInterceptor interceptor = new AuditInterceptor();
		Session session1 = openSession(interceptor);
		SessionHolder holder = new SessionHolder(session1);
		holder.setInterceptor(interceptor);
		HibernateUtil.setThreadLocal(holder);
		return session1;
	}

	public static void runUpgradeScripts(Connection connection)
	throws Exception {
		runUpgradeScripts(FIRST_NUMBERED_VERSION, connection);
	}

	public static void runUpgradeScripts(int fromVersion, Connection connection)
	throws Exception {
		DatabaseVersionPersistence persistence = new DatabaseVersionPersistence(connection);
	    Assert.assertEquals(fromVersion, persistence.read());
	    persistence.upgradeDatabase();
	}
	
	/**
	 * Create a database and upgrade it to the first database version
	 * with a number.  Should be run on an empty database (no tables).
	 * @throws IOException 
	 */
	public static void upgradeToFirstNumberedVersion(Connection connection) 
	throws SQLException, IOException {
		executeScript(connection, "mifosdbcreationscript.sql");
	    executeScript(connection, "mifosmasterdata.sql");
	    executeScript(connection, "rmpdbcreationscript.sql");
	    executeScript(connection, "rmpmasterdata.sql");
	    executeScript(connection, "Iteration13-DBScripts25092006.sql");
	    executeScript(connection, "Iteration14-DDL-DBScripts10102006.sql");
	    executeScript(connection, "Iteration14-DML-DBScripts10102006.sql");
	    executeScript(connection, "Iteration15-DDL-DBScripts24102006.sql");
	    executeScript(connection, "Iteration15-DBScripts20061012.sql");
	    executeScript(connection, "add-version.sql");
	    executeScript(connection, "Index.sql");
	}

	/**
	 * Create a database and upgrade it to the latest checkpoint database 
	 * version.  Should be run on an empty database (no tables).
	 * @throws IOException 
	 */
	public static void upgradeLatestCheckpointVersion(Connection connection) 
	throws SQLException, IOException {
	    executeScript(connection, "latest-schema-checkpoint.sql");
		executeScript(connection, "latest-data-checkpoint.sql");
	}

	public static void resetMySQLDatabase() throws Exception {
		Connection connection = HibernateUtil.getSessionTL().connection();
		HibernateUtil.startTransaction();
		SqlUpgrade.execute(SqlResource.getInstance().getAsStream("mifosdroptables.sql"), connection);
		SqlUpgrade.execute(SqlResource.getInstance().getAsStream("latest-schema.sql"), connection);
		SqlUpgrade.execute(SqlResource.getInstance().getAsStream("latest-data.sql"), connection);
		SqlUpgrade.execute(SqlResource.getInstance().getAsStream("custom_data.sql"), connection);
		SqlUpgrade.execute(SqlResource.getInstance().getAsStream("testdbinsertionscript.sql"), connection);

		// Don't execute "init_mifos_password.sql" ... this is only for
		// "production" databases (those that Mifos running under an app
		// server will connect to).

		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		
		// If the database is ever blown away, we must repopulate chart of
		// accounts data since some unit tests rely on its presence. It must
		// be created via this method since adding it via an sql script would
		// invalidate *other* unit tests that assume this method has been
		// called.
		FinancialInitializer.initialize();
	}	
}
