package org.mifos.framework;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;

import net.sourceforge.mayfly.Database;
import net.sourceforge.mayfly.dump.SqlDumper;

import org.hibernate.classic.Session;
import org.mifos.framework.hibernate.helper.SessionHolder;
import org.mifos.framework.persistence.SessionOpener;
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

}
