package org.mifos.framework.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.CharacterCodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.AssertionFailedError;
import junit.framework.JUnit4TestAdapter;
import junitx.framework.ObjectAssert;
import junitx.framework.StringAssert;
import net.sourceforge.mayfly.Database;

import org.junit.Before;
import org.junit.Test;
import org.mifos.framework.util.helpers.DatabaseSetup;

public class DatabaseVersionPersistenceTest {
	
	@Before
	public void setUp() throws Exception {
		DatabaseSetup.configureLogging();
	}

	@Test public void readSuccess() throws Exception {
		Database database = new Database();
		database.execute("create table DATABASE_VERSION(DATABASE_VERSION INTEGER)");
		database.execute("insert into DATABASE_VERSION(DATABASE_VERSION) VALUES(53)");
		new DatabaseVersionPersistence(database.openConnection()).read();
	}
	
	@Test public void readTwoRows() throws Exception {
		Database database = new Database();
		database.execute("create table DATABASE_VERSION(DATABASE_VERSION INTEGER)");
		database.execute("insert into DATABASE_VERSION(DATABASE_VERSION) VALUES(53)");
		database.execute("insert into DATABASE_VERSION(DATABASE_VERSION) VALUES(54)");
		try {
			new DatabaseVersionPersistence(database.openConnection()).read();
			fail();
		}
		catch (RuntimeException e) {
			assertEquals("too many rows in DATABASE_VERSION", e.getMessage());
		}
	}
	
	@Test public void readNoRows() throws Exception {
		Database database = new Database();
		database.execute("create table DATABASE_VERSION(DATABASE_VERSION INTEGER)");
		try {
			new DatabaseVersionPersistence(database.openConnection()).read();
			fail();
		}
		catch (RuntimeException e) {
			assertEquals("No row in DATABASE_VERSION", e.getMessage());
		}
	}
	
	@Test(expected=SQLException.class)
	public void readNoTable() throws Exception {
		/* This is the case where the user has an old database (from before
		   version 100).  They will need to upgrade to 100 manually.  */
		Database database = new Database();
		new DatabaseVersionPersistence(database.openConnection()).read();
	}
	
	@Test public void write() throws Exception {
		Database database = DummyUpgrade.databaseWithVersionTable(53);
		new DatabaseVersionPersistence(database.openConnection()).write(77);
		assertEquals(77, 
			new DatabaseVersionPersistence(database.openConnection()).read());
	}

	@Test public void isNotVersioned() throws Exception {
		Database database = TestDatabase.makeDatabase();
		DatabaseVersionPersistence persistence = 
			new DatabaseVersionPersistence(database.openConnection());
		assertFalse(persistence.isVersioned());
		
		database.execute(
			"create table DATABASE_VERSION(DATABASE_VERSION INTEGER)");
		database.execute(
			"insert into DATABASE_VERSION(DATABASE_VERSION) VALUES(43)");
		assertTrue(persistence.isVersioned());
	}
	
	@Test public void noUpgrade() throws Exception {
		DatabaseVersionPersistence persistence = 
			new DatabaseVersionPersistence(null);
		List<Upgrade> scripts = persistence.scripts(88, 88);
		assertEquals(0, scripts.size());
	}

	@Test public void upgrade() throws Exception {
		DatabaseVersionPersistence persistence = sqlFor89And90();
		List<Upgrade> scripts = persistence.scripts(90, 88);
		assertEquals(2, scripts.size());
		assertEquals("upgrade_to_89.sql", 
			((SqlUpgrade) scripts.get(0)).sql().getPath());
		assertEquals("upgrade_to_90.sql", 
			((SqlUpgrade) scripts.get(1)).sql().getPath());
	}

	private DatabaseVersionPersistence sqlFor89And90() {
		return new DatabaseVersionPersistence(null) {
			@Override
			URL getSqlResourceLocation(String name) {
				if ("upgrade_to_89.sql".equals(name) 
					|| "upgrade_to_90.sql".equals(name)) {
					try {
						return new URL("file:" + name);
					} catch (MalformedURLException e) {
						throw (AssertionFailedError)new AssertionFailedError().initCause(e);
					}
				}
				throw new AssertionFailedError("got unexpected " + name);
			}
		};
	}

	@Test public void detectDowngrade() throws Exception {
		DatabaseVersionPersistence persistence = 
			new DatabaseVersionPersistence(null);
		try {
			persistence.scripts(87, 88);
			fail();
		}
		catch (UnsupportedOperationException e) {
			assertEquals(
				"your database needs to be downgraded from 88 to 87", 
				e.getMessage());
		}
	}
	
	@Test public void readEmpty() throws Exception {
		String[] sqlStatements = SqlUpgrade.readFile(
						new ByteArrayInputStream(new byte[0]));
		assertEquals(0, sqlStatements.length);
	}
	
	@Test public void blankLines() throws Exception {
		checkSplit(
			"command\n\n\n\n\n\n\n", 
			"\ncommand");
		checkSplit(
			"command;\n", 
			"\ncommand");
		checkSplit(
			"command;\n\n\n\n\n\n\n", 
			"\ncommand");
	}
	
	@Test public void slashStarComments() throws Exception {
		checkSplit("/* foo; bar */", "\n/* foo; bar */");
		
		/* Trying to fix this case seems to just descend even
		   further down the path of the half-assed lexer. */
		checkSplit("/* foo;\n bar */", "\n/* foo", "\nbar */");
	}
	
	@Test public void comments() throws Exception {
		/* Many of the details here, like what comments get swallowed
		   and placement of whitespace, aren't very important.
		   It just seems better to have tests so we 
		   know what the code is doing.
		   */
		checkSplit("-- ignore me");
		checkSplit("// ignore me");
		checkSplit(" // ignore me", "\n// ignore me");
		checkSplit(" -- ignore me", "\n-- ignore me\n");
		checkSplit("insert into foo(x) values('x-y')", 
			"\ninsert into foo(x) values('x-y')");
		checkSplit("insert into foo(x) values('x--y')", 
			"\ninsert into foo(x) values('x--y')\n");
	}
	
	@Test public void splitIntoTwo() throws Exception {
		checkSplit("foo;\nbar\n", "\nfoo", "\nbar");
		checkSplit("foo;\nbar;\n", "\nfoo", "\nbar");
	}

	private void checkSplit(String sql, String... expected) 
	throws UnsupportedEncodingException {
		String[] statements = SqlUpgrade.readFile(
			new ByteArrayInputStream(sql.getBytes("UTF-8")));
		assertEquals(expected.length, statements.length);
		for (int i = 0; i < expected.length; ++i) {
			assertEquals(expected[i], statements[i]);
		}
	}

	@Test public void badUtf8() throws Exception {
		try {
			SqlUpgrade.readFile(
					new ByteArrayInputStream(new byte[] { (byte)0x80 }));
			fail();
		}
		catch (RuntimeException e) {
			ObjectAssert.assertInstanceOf(
				CharacterCodingException.class, e.getCause());
		}
	}
	
	@Test public void goodUtf8() throws Exception {
		String[] sqlStatements = SqlUpgrade.readFile(
				new ByteArrayInputStream(new byte[] {
						(byte)0xe2, (byte)0x82, (byte)0xac }));
		assertEquals(1, sqlStatements.length);
		String euroSign = sqlStatements[0];
		assertEquals("\n\u20AC", euroSign);
	}

	@Test public void executeStream() throws Exception {
		SqlUpgrade persistence = new SqlUpgrade(null, -1);
		Connection conn = new Database().openConnection();
		byte[] sql = (
				"create table FOO(DATABASE_VERSION INTEGER);\n"+
				"--some comment\n"+
				"insert into FOO(DATABASE_VERSION) VALUES(53);\n"
				).getBytes("UTF-8");
		ByteArrayInputStream in = new ByteArrayInputStream(sql);
		persistence.execute(in, conn);
		conn.commit();
		readOneValueFromFoo(conn);
	}

	@Test public void upgradeDatabase() throws Exception {
		Database database = new Database();
		database.execute("create table DATABASE_VERSION(DATABASE_VERSION INTEGER)");
		database.execute("insert into DATABASE_VERSION(DATABASE_VERSION) VALUES(78)");
		Connection connection = database.openConnection();
		connection.setAutoCommit(false);
		DatabaseVersionPersistence persistence = 
			new DatabaseVersionPersistence(connection);
		persistence.setSqlResource(new SqlResourceForTest());
		persistence.upgradeDatabase(connection, 80);
		connection.commit();
		
		readOneValueFromFoo(connection);
	}

	private void readOneValueFromFoo(Connection connection) 
	throws SQLException {
		Statement statement = connection.createStatement();
		ResultSet results = statement.executeQuery("select * from FOO");
		assertTrue(results.next());
		int valueFromFoo = results.getInt(1);
		assertEquals(53, valueFromFoo);
		assertFalse(results.next());
		results.close();
		statement.close();
	}
	
	@Test public void errorWrapping() throws Exception {
		Database database = new Database();
		database.execute("create table DATABASE_VERSION(DATABASE_VERSION INTEGER)");
		database.execute(
			"insert into DATABASE_VERSION(DATABASE_VERSION) VALUES(78)");
		Connection connection = database.openConnection();
		Upgrade upgrade = new Upgrade(79) {

			@Override
			public void upgrade(Connection connection, DatabaseVersionPersistence databaseVersionPersistence) {
				throw new RuntimeException("tried but failed");
			}
			
		};
		DatabaseVersionPersistence persistence = 
			javaOnlyPersistence(database, upgrade);
		try {
			persistence.upgradeDatabase(connection, 79);
			fail();
		}
		catch (RuntimeException e) {
			assertEquals("error in upgrading to 79", e.getMessage());
			assertEquals("tried but failed", e.getCause().getMessage());
		}
	}

	@Test public void notJavaOrSql() throws Exception {
		Database database = new Database();
		DatabaseVersionPersistence persistence = 
			new DatabaseVersionPersistence(database.openConnection(),
				Collections.EMPTY_MAP) {
			@Override
			URL getSqlResourceLocation(String name) {
				return null;
			}
		};
		try {
			persistence.findUpgrade(69);
			fail();
		}
		catch (IllegalStateException e) {
			StringAssert.assertStartsWith(
				"Did not find upgrade to 69 in java " +
				"or in upgrade_to_69.sql next to ",
				e.getMessage());
		}
	}
	
	@Test public void javaOnly() throws Exception {
		Database database = new Database();
		DatabaseVersionPersistence persistence = javaOnlyPersistence(database);
		DummyUpgrade found = (DummyUpgrade) persistence.findUpgrade(69);
		found.upgrade(null, null);
		assertEquals("upgrade to 69\n", found.getLog());
	}

	@Test public void javaConditional() throws Exception {
		Database database = new Database();
		DatabaseVersionPersistence persistence = conditionalPersistence(database);
		SqlUpgrade found = persistence.findUpgradeScript(10, CONDITIONAL_UPGRADE_10_NAME);
		assertTrue(found != null);
		SqlUpgrade found_downgrade = persistence.findUpgradeScript(10,CONDITIONAL_DOWNGRADE_10_NAME);
		assertTrue(found_downgrade != null);
	}

	private DatabaseVersionPersistence javaOnlyPersistence(Database database) {
		return javaOnlyPersistence(database, new DummyUpgrade(69));
	}

	private DatabaseVersionPersistence javaOnlyPersistence(
		Database database, Upgrade upgrade) {
		Map<Integer, Upgrade> registrations = new HashMap<Integer, Upgrade>();
		DatabaseVersionPersistence.register(registrations, upgrade);
		DatabaseVersionPersistence persistence = 
			new DatabaseVersionPersistence(database.openConnection(),
				registrations) {
			@Override
			URL getSqlResourceLocation(String name) {
				return null;
			}
		};
		return persistence;
	}
	
	@Test public void javaAndSql() throws Exception {
		Database database = new Database();
		DatabaseVersionPersistence persistence = javaAndSqlPersistence(database);
		
		try {
			persistence.findUpgrade(69);
			fail();
		}
		catch (IllegalStateException e) {
			assertEquals(
				"Found upgrade to 69 both in java and in upgrade_to_69.sql",
				e.getMessage());
		}
	}

	private DatabaseVersionPersistence javaAndSqlPersistence(Database database) {
		Map<Integer, Upgrade> registrations = new HashMap<Integer, Upgrade>();
		DatabaseVersionPersistence.register(registrations, new DummyUpgrade(69));
		DatabaseVersionPersistence persistence = 
			new DatabaseVersionPersistence(database.openConnection(),
				registrations) {
			@Override
			URL getSqlResourceLocation(String name) {
				if ("upgrade_to_69.sql".equals(name)) {
					try {
						return new URL("file:" + name);
					} catch (MalformedURLException e) {
						throw (AssertionFailedError)new AssertionFailedError().initCause(e);
					}
				}
				else {
					throw new AssertionFailedError("got unexpected " + name);
				}
			}
		};
		return persistence;
	}

	private static String CONDITIONAL_UPGRADE_10_NAME = "upgrade_to_10_conditional.sql";
	private static String CONDITIONAL_DOWNGRADE_10_NAME = "downgrade_from_10_conditional.sql";

	private DatabaseVersionPersistence conditionalPersistence(Database database) {
		DatabaseVersionPersistence persistence = 
			new DatabaseVersionPersistence(database.openConnection()) {
			@Override
			public URL getSqlResourceLocation(String name) {
				if (CONDITIONAL_UPGRADE_10_NAME.equals(name) || 
					CONDITIONAL_DOWNGRADE_10_NAME.equals(name)) {
					try {
						return new URL("file:" + name);
					} catch (MalformedURLException e) {
						throw (AssertionFailedError)new AssertionFailedError().initCause(e);
					}
				}
				else {
					throw new AssertionFailedError("got unexpected " + name);
				}
			}
		};
		return persistence;
	}
	
	
	@Test public void duplicateRegistration() throws Exception {
		Map<Integer, Upgrade> register = new HashMap<Integer, Upgrade>();
		DatabaseVersionPersistence.register(register, new DummyUpgrade(70));
		try {
			DatabaseVersionPersistence.register(register, new DummyUpgrade(70));
			fail();
		}
		catch (IllegalStateException e) {
			assertEquals("already have an upgrade to 70", e.getMessage());
		}
	}
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(DatabaseVersionPersistenceTest.class);
	}

}
