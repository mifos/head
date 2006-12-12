package org.mifos.framework.persistence;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.CharacterCodingException;
import java.sql.SQLException;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import junitx.framework.ObjectAssert;
import net.sourceforge.mayfly.Database;

import org.mifos.framework.util.helpers.DatabaseSetup;

public class DatabaseVersionPersistenceTest extends TestCase {
	
	@Override
	protected void setUp() throws Exception {
		DatabaseSetup.configureLogging();
		DatabaseSetup.initializeHibernate();
	}

	public void testRead() throws Exception {
		new DatabaseVersionPersistence().read();
	}
	
	public void testReadSuccess() throws Exception {
		Database database = new Database();
		database.execute("create table DATABASE_VERSION(DATABASE_VERSION INTEGER)");
		database.execute("insert into DATABASE_VERSION(DATABASE_VERSION) VALUES(53)");
		new DatabaseVersionPersistence().read(database.openConnection());
	}
	
	public void testReadTwoManyRows() throws Exception {
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
	
	public void testReadTwoFewRows() throws Exception {
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
	
	public void testReadNoTable() throws Exception {
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
	
	public void testWrite() throws Exception {
		new DatabaseVersionPersistence().write(77);
		assertEquals(77, 
			new DatabaseVersionPersistence().read());
	}

	public void testIsVersioned() throws Exception {
		assertTrue(new DatabaseVersionPersistence().isVersioned());
	}
	
	/* not repeatable
	public void testNotIsVersioned() throws Exception {
		DatabaseVersionPersistence dvp = new DatabaseVersionPersistence();
		Connection c =  dvp.getConnection();
		c.createStatement().executeUpdate("drop table DATABASE_VERSION");
		c.commit();
		assertFalse(new DatabaseVersionPersistence().isDBVersioned());
		
	}
	*/
	
	public void testNoUpgrade() throws Exception {
		DatabaseVersionPersistence persistence = new DatabaseVersionPersistence();
		URL[] scripts = persistence.scripts(88, 88);
		assertEquals(0, scripts.length);
	}

	public void testUpgrade() throws Exception {
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
		URL[] scripts = persistence.scripts(90, 88);
		assertEquals(2, scripts.length);
		assertEquals("upgrade_to_89.sql", scripts[0].getPath());
		assertEquals("upgrade_to_90.sql", scripts[1].getPath());
	}

	/*
	Like the above test, but with files instead of overriding lookup.
	
	public void testUpgradeWithFile() throws Exception {
		DatabaseVersionPersistence persistence = new DatabaseVersionPersistence();
		URL[] scripts = persistence.scripts(90, 88);
		assertEquals(2, scripts.length);
		scripts[0].openStream().close();
		scripts[1].openStream().close();
	}
	*/

	public void testDowngrade() throws Exception {
		DatabaseVersionPersistence persistence = new DatabaseVersionPersistence();
		try {
			persistence.scripts(87, 88);
			fail();
		}
		catch (UnsupportedOperationException e) {
			assertEquals("downgrades not yet supported (from 88 to 87)", e.getMessage());
		}
	}
	
	public void testReadEmpty() throws Exception {
		assertEquals("", DatabaseVersionPersistence.readFile(
				new ByteArrayInputStream(new byte[0])));
	}

	public void testBadUtf8() throws Exception {
		try {
			DatabaseVersionPersistence.readFile(
					new ByteArrayInputStream(new byte[] { (byte)0x80 }));
			fail();
		}
		catch (RuntimeException e) {
			ObjectAssert.assertInstanceOf(CharacterCodingException.class, e.getCause());
		}
	}
	
	public void testGoodUtf8() throws Exception {
		String euroSign = 
			DatabaseVersionPersistence.readFile(new ByteArrayInputStream(new byte[] {
				(byte)0xe2, (byte)0x82, (byte)0xac }));
		assertEquals("\u20AC", euroSign);
	}

}
