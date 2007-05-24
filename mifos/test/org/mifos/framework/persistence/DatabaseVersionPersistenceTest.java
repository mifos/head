package org.mifos.framework.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.CharacterCodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import junit.framework.AssertionFailedError;
import junit.framework.JUnit4TestAdapter;
import junitx.framework.ObjectAssert;
import net.sourceforge.mayfly.Database;

import org.junit.Before;
import org.junit.Test;
import org.mifos.framework.TestDatabase;
import org.mifos.framework.util.helpers.DatabaseSetup;

public class DatabaseVersionPersistenceTest {
	
	@Before
	public void setUp() throws Exception {
		DatabaseSetup.configureLogging();
		DatabaseSetup.initializeHibernate();
	}

	@Test public void read() throws Exception {
		new DatabaseVersionPersistence().read();
	}
	
	@Test public void readSuccess() throws Exception {
		Database database = new Database();
		database.execute("create table DATABASE_VERSION(DATABASE_VERSION INTEGER)");
		database.execute("insert into DATABASE_VERSION(DATABASE_VERSION) VALUES(53)");
		new DatabaseVersionPersistence().read(database.openConnection());
	}
	
	@Test public void readTwoRows() throws Exception {
		Database database = new Database();
		database.execute("create table DATABASE_VERSION(DATABASE_VERSION INTEGER)");
		database.execute("insert into DATABASE_VERSION(DATABASE_VERSION) VALUES(53)");
		database.execute("insert into DATABASE_VERSION(DATABASE_VERSION) VALUES(54)");
		try {
			new DatabaseVersionPersistence().read(database.openConnection());
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
			new DatabaseVersionPersistence().read(database.openConnection());
			fail();
		}
		catch (RuntimeException e) {
			assertEquals("No row in DATABASE_VERSION", e.getMessage());
		}
	}
	
	@Test public void readNoTable() throws Exception {
		/* This is the case where the user has an old database (from before
		   version 100).  They will need to upgrade to 100 manually.  */
		Database database = new Database();
		try {
			new DatabaseVersionPersistence().read(database.openConnection());
			fail();
		}
		catch (SQLException e) {
		}
	}
	
	@Test public void write() throws Exception {
		new DatabaseVersionPersistence().write(77);
		assertEquals(77, 
			new DatabaseVersionPersistence().read());
	}

	@Test public void isVersioned() throws Exception {
		assertTrue(new DatabaseVersionPersistence().isVersioned());
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
		DatabaseVersionPersistence persistence = new DatabaseVersionPersistence();
		List<Upgrade> scripts = persistence.scripts(88, 88);
		assertEquals(0, scripts.size());
	}

	@Test public void upgrade() throws Exception {
		DatabaseVersionPersistence persistence = new DatabaseVersionPersistence() {
			@Override
			URL lookup(String name) {
				if ("upgrade_to_89.sql".equals(name) 
					|| "upgrade_to_90.sql".equals(name)) {
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
		List<Upgrade> scripts = persistence.scripts(90, 88);
		assertEquals(2, scripts.size());
		assertEquals("upgrade_to_89.sql", 
			((SqlUpgrade) scripts.get(0)).sql().getPath());
		assertEquals("upgrade_to_90.sql", 
			((SqlUpgrade) scripts.get(1)).sql().getPath());
	}

	/*
	Like the above test, but with files instead of overriding lookup.
	
	@Test public void testUpgradeWithFile() throws Exception {
		DatabaseVersionPersistence persistence = new DatabaseVersionPersistence();
		URL[] scripts = persistence.scripts(90, 88);
		assertEquals(2, scripts.length);
		scripts[0].openStream().close();
		scripts[1].openStream().close();
	}
	*/

	@Test public void detectDowngrade() throws Exception {
		DatabaseVersionPersistence persistence = 
			new DatabaseVersionPersistence();
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
	
	@Test public void downgradeScripts() throws Exception {
		DatabaseVersionPersistence persistence = 
			new DatabaseVersionPersistence();
		List<Upgrade> upgrades = persistence.downgrades(87, 88);
		assertEquals(1, upgrades.size());
		SqlUpgrade first = (SqlUpgrade) upgrades.get(0);
		assertEquals(88, first.higherVersion());
	}
	
	@Test public void downgradesInOrderOfExecution() throws Exception {
		DatabaseVersionPersistence persistence = 
			new DatabaseVersionPersistence();
		List<Upgrade> upgrades = persistence.downgrades(86, 88);
		assertEquals(2, upgrades.size());
		assertEquals(88, ((SqlUpgrade) upgrades.get(0)).higherVersion());
		assertEquals(87, ((SqlUpgrade) upgrades.get(1)).higherVersion());
	}
	
	@Test public void readEmpty() throws Exception {
		String[] sqlStatements = SqlUpgrade.readFile(
						new ByteArrayInputStream(new byte[0]));
		assertEquals(0, sqlStatements.length);
	}

	@Test public void badUtf8() throws Exception {
		try {
			SqlUpgrade.readFile(
					new ByteArrayInputStream(new byte[] { (byte)0x80 }));
			fail();
		}
		catch (RuntimeException e) {
			ObjectAssert.assertInstanceOf(CharacterCodingException.class, e.getCause());
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
		DatabaseVersionPersistence persistence = new DatabaseVersionPersistence();
		Database database = new Database();
		database.execute("create table DATABASE_VERSION(DATABASE_VERSION INTEGER)");
		database.execute("insert into DATABASE_VERSION(DATABASE_VERSION) VALUES(78)");
		Connection conn = database.openConnection();
		conn.setAutoCommit(false);
		persistence.upgradeDatabase(conn, 80);
		conn.commit();
		
		readOneValueFromFoo(conn);
	}

	private void readOneValueFromFoo(Connection conn) throws SQLException {
		Statement statement = conn.createStatement();
		ResultSet results = statement.executeQuery("select * from FOO");
		assertTrue(results.next());
		int valueFromFoo = results.getInt(1);
		assertEquals(53, valueFromFoo);
		assertFalse(results.next());
		results.close();
		statement.close();
	}
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(DatabaseVersionPersistenceTest.class);
	}

}
