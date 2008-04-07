package org.mifos.framework.persistence;

import static org.junit.Assert.assertEquals;
import static org.mifos.framework.persistence.DatabaseVersionPersistence.APPLICATION_VERSION;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import junit.framework.JUnit4TestAdapter;
import net.sourceforge.mayfly.Database;
import net.sourceforge.mayfly.JdbcDriver;
import net.sourceforge.mayfly.dump.SqlDumper;

import org.junit.Test;

public class DowngraderTest {
	
	private static final int FIRST_VERSION_FOR_TEST = 50;

	private DummyUpgrade dummyUpgrade;

	@Test public void noArguments() throws Exception {
		String output = run(new String[] { });
		assertEquals("Too few arguments.\n" +
			"Should be: downgradeTo jdbcUrl driver user password\n",
			output);
	}
	
	@Test public void almostEnoughArguments() throws Exception {
		String output = run(new String[] { "100", 
			"jdbc:foo:somewhere", "org.mifos.driver", "user"});
		assertEquals("Too few arguments.\n" +
			"Should be: downgradeTo jdbcUrl driver user password\n",
			output);
	}
	
	@Test public void excessArguments() throws Exception {
		String output = run(new String[] { "100", 
			"jdbc:foo:somewhere", "org.mifos.driver", "user", "password",
			"53" });
		assertEquals("Excess argument 53.\n",
			output);
	}
	
	@Test public void jdbcArguments() throws Exception {
		Downgrader downgrader = new Downgrader();
		downgrader.parse(new String[] { "100", 
			"jdbc:foo:somewhere", "org.mifos.driver", "user", "password" });
		assertEquals(null, downgrader.error);
		assertEquals("jdbc:foo:somewhere", downgrader.jdbcUrl);
		assertEquals("org.mifos.driver", downgrader.driver);
		assertEquals("user", downgrader.username);
		assertEquals("password", downgrader.password);
	}
	
	@Test public void forgotDashDInAnt() throws Exception {
		String output = run("${downgrade.to}");
		assertEquals("Argument ${downgrade.to} is not a number.\n",
			output);
	}

	@Test public void nonNumeric() throws Exception {
		String output = run("(-)");
		assertEquals("Argument (-) is not a number.\n",
			output);
	}
	
	@Test public void tooLow() throws Exception {
		String output = run("49");
		assertEquals("Attempt to downgrade to 49 which is before 50.\n",
			output);
	}

	@Test public void negative() throws Exception {
		String output = run("-102");
		assertEquals("Attempt to downgrade to -102 which is before 50.\n",
			output);
	}
	
	@Test public void slightlyTooHigh() throws Exception {
		String output = run("" + APPLICATION_VERSION);
		assertEquals("Attempt to downgrade to " + APPLICATION_VERSION + 
			" which is after " + (APPLICATION_VERSION - 1) + ".\n",
			output);
	}

	@Test public void wayTooHigh() throws Exception {
		String output = run("111888777");
		assertEquals("Attempt to downgrade to 111888777" + 
			" which is after " + (APPLICATION_VERSION - 1) + ".\n",
			output);
	}
	
	@Test public void openConnection() throws Exception {
		Downgrader downgrader = new Downgrader();
		downgrader.jdbcUrl = JdbcDriver.create(new Database().dataStore());
		downgrader.driver = JdbcDriver.class.getName();
		downgrader.username = "ignoreMe";
		downgrader.password = "ignoreMeToo";
		
		Connection connection = downgrader.openConnection();
		Statement statement = connection.createStatement();
		statement.executeUpdate("create table foo(x integer)");
		statement.close();
		connection.close();
	}
	
	@Test(expected=SQLException.class) 
	public void badJdbcUrl() throws Exception {
		Downgrader downgrader = new Downgrader();
		downgrader.jdbcUrl = "jdbc:mayfly:some:random:string";
		downgrader.driver = JdbcDriver.class.getName();
		downgrader.username = "ignoreMe";
		downgrader.password = "ignoreMeToo";
		
		downgrader.openConnection();
	}
	
	@Test(expected=ClassNotFoundException.class) 
	public void badDriver() throws Exception {
		Downgrader downgrader = new Downgrader();
		downgrader.jdbcUrl = JdbcDriver.create(new Database().dataStore());
		downgrader.driver = "org.mifos.nonexistent";
		downgrader.username = "ignoreMe";
		downgrader.password = "ignoreMeToo";
		
		downgrader.openConnection();
	}
	
	@Test public void success() throws Exception {
		Database database = new Database();
		database.execute(
			"create table DATABASE_VERSION(DATABASE_VERSION integer)");
		database.execute(
			"insert into DATABASE_VERSION(DATABASE_VERSION) VALUES(57)");
		String output = run("56", database.openConnection());
		assertEquals("Downgrading to 56...done.\n", output);
		
		assertEquals("CREATE TABLE DATABASE_VERSION(\n" +
			"  DATABASE_VERSION INTEGER\n" +
			");\n\n" +
			"INSERT INTO DATABASE_VERSION(DATABASE_VERSION) VALUES(56);\n\n",
			new SqlDumper().dump(database.dataStore()));
		assertEquals("downgrade from 57\n", dummyUpgrade.getLog());
	}

	@Test public void nothingToDo() throws Exception {
		Database database = new Database();
		database.execute(
			"create table DATABASE_VERSION(DATABASE_VERSION integer)");
		database.execute(
			"insert into DATABASE_VERSION(DATABASE_VERSION) VALUES(107)");
		String output = run("107", database.openConnection());
		assertEquals("Already at database version 107.\n", output);
	}

	@Test public void attemptToUpgrade() throws Exception {
		Database database = new Database();
		database.execute(
			"create table DATABASE_VERSION(DATABASE_VERSION integer)");
		database.execute(
			"insert into DATABASE_VERSION(DATABASE_VERSION) VALUES(107)");
		String output = run("108", database.openConnection());
		assertEquals("Already at database version 107.\n", output);
	}

	private String run(String downgradeToArgument) throws Exception {
		return run(downgradeToArgument, null);
	}

	private String run(String downgradeToArgument, Connection connection) 
	throws Exception {
		return run(new String[] {
			downgradeToArgument, "url", "driver", "user", "password" },
			connection);
	}

	private String run(String[] arguments) throws Exception {
		return run(arguments, null);
	}

	private String run(String[] arguments, Connection connection) 
	throws Exception {
		Downgrader downgrader = new Downgrader(FIRST_VERSION_FOR_TEST);
		downgrader.parse(arguments);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(out);
		downgrader.run(printStream, connection, makeRegister());
		printStream.flush();
		return out.toString("UTF-8");
	}
	
	private Map<Integer, Upgrade> makeRegister() {
		Map<Integer, Upgrade> registrations = new HashMap<Integer, Upgrade>();
		dummyUpgrade = new DummyUpgrade(57);
		DatabaseVersionPersistence.register(registrations, dummyUpgrade);
		return registrations;
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(DowngraderTest.class);
	}

}
