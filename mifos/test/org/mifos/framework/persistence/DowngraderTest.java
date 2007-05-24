package org.mifos.framework.persistence;

import static org.junit.Assert.assertEquals;
import static org.mifos.framework.persistence.DatabaseVersionPersistence.APPLICATION_VERSION;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;

import junit.framework.JUnit4TestAdapter;
import net.sourceforge.mayfly.Database;
import net.sourceforge.mayfly.dump.SqlDumper;

import org.junit.Test;

public class DowngraderTest {
	
	private Downgrader downgrader = new Downgrader();
	private ByteArrayOutputStream out = new ByteArrayOutputStream();
	private PrintStream printStream = new PrintStream(out);
	
	@Test public void noArguments() throws Exception {
		String output = run(new String[] { });
		assertEquals("Missing argument for what version to downgrade to.\n",
			output);
	}
	
	@Test public void excessArguments() throws Exception {
		String output = run(new String[] { "43", "53" });
		assertEquals("Excess argument 53.\n",
			output);
	}
	
	@Test public void forgotDashDInAnt() throws Exception {
		String output = run(new String[] { "${downgrade.to}" });
		assertEquals("Argument ${downgrade.to} is not a number.\n",
			output);
	}

	@Test public void nonNumeric() throws Exception {
		String output = run(new String[] { "(-)" });
		assertEquals("Argument (-) is not a number.\n",
			output);
	}
	
	@Test public void tooLow() throws Exception {
		String output = run(new String[] { "99" });
		assertEquals("Attempt to downgrade to 99 which is before 100.\n",
			output);
	}

	@Test public void negative() throws Exception {
		String output = run(new String[] { "-102" });
		assertEquals("Attempt to downgrade to -102 which is before 100.\n",
			output);
	}
	
	@Test public void slightlyTooHigh() throws Exception {
		String output = run(new String[] { "" + APPLICATION_VERSION });
		assertEquals("Attempt to downgrade to " + APPLICATION_VERSION + 
			" which is after " + (APPLICATION_VERSION - 1) + ".\n",
			output);
	}

	@Test public void wayTooHigh() throws Exception {
		String output = run(new String[] { "111888777" });
		assertEquals("Attempt to downgrade to 111888777" + 
			" which is after " + (APPLICATION_VERSION - 1) + ".\n",
			output);
	}
	
	@Test public void success() throws Exception {
		Database database = new Database();
		database.execute(
			"create table DATABASE_VERSION(DATABASE_VERSION integer)");
		database.execute(
			"create table CONFIG_KEY_VALUE_INTEGER(foo integer)");
		database.execute(
			"insert into DATABASE_VERSION(DATABASE_VERSION) VALUES(107)");
		String output = run(new String[] { "106" }, database.openConnection());
		assertEquals("Downgrading to 106...done.\n", output);
		
		assertEquals("CREATE TABLE DATABASE_VERSION(\n" +
			"  DATABASE_VERSION INTEGER\n" +
			");\n\n" +
			"INSERT INTO DATABASE_VERSION(DATABASE_VERSION) VALUES(106);\n\n",
			new SqlDumper().dump(database.dataStore()));
	}

	@Test public void nothingToDo() throws Exception {
		Database database = new Database();
		database.execute(
			"create table DATABASE_VERSION(DATABASE_VERSION integer)");
		database.execute(
			"insert into DATABASE_VERSION(DATABASE_VERSION) VALUES(107)");
		String output = run(new String[] { "107" }, database.openConnection());
		assertEquals("Already at database version 107.\n", output);
	}

	@Test public void attemptToUpgrade() throws Exception {
		Database database = new Database();
		database.execute(
			"create table DATABASE_VERSION(DATABASE_VERSION integer)");
		database.execute(
			"insert into DATABASE_VERSION(DATABASE_VERSION) VALUES(107)");
		String output = run(new String[] { "108" }, database.openConnection());
		assertEquals("Already at database version 107.\n", output);
	}

	private String run(String[] arguments) throws Exception {
		return run(arguments, null);
	}

	private String run(String[] arguments, Connection connection) 
	throws Exception {
		downgrader.run(arguments, printStream, connection);
		printStream.flush();
		return out.toString("UTF-8");
	}
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(DowngraderTest.class);
	}

}
