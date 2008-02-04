package org.mifos.framework.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import junit.framework.JUnit4TestAdapter;
import net.sourceforge.mayfly.Database;

import org.junit.Test;


public class CompositeUpgradeTest {
	
	@Test public void basics() throws Exception {
		DummyUpgrade upgradeOne = new DummyUpgrade(53);
		DummyUpgrade upgradeTwo = new DummyUpgrade(53);
		Upgrade composite =
			new CompositeUpgrade(upgradeOne, upgradeTwo);
		assertEquals(53, composite.higherVersion());
		Connection data = simpleDatabase(52);
		composite.upgrade(data, null);
		assertEquals("upgrade to 53\n", upgradeOne.getLog());
		assertEquals("upgrade to 53\n", upgradeTwo.getLog());
		
		assertEquals(53, new DatabaseVersionPersistence(data).read());
	}
	
	@Test public void mismatch() throws Exception {
		DummyUpgrade upgradeOne = new DummyUpgrade(111);
		DummyUpgrade upgradeTwo = new DummyUpgrade(112);
		try {
			new CompositeUpgrade(upgradeOne, upgradeTwo);
			fail();
		}
		catch (RuntimeException e) {
			assertEquals(
				"got upgrades to 111 and 112 but expected matching versions", 
				e.getMessage());
		}
	}
	
	@Test public void empty() throws Exception {
		try {
			new CompositeUpgrade();
			fail();
		}
		catch (RuntimeException e) {
			assertEquals(
				"must specify at least one upgrade", 
				e.getMessage());
		}
	}

	@Test public void downgrades() throws Exception {
		DummyUpgrade upgradeOne = new DummyUpgrade(53);
		Upgrade composite =
			new CompositeUpgrade(upgradeOne);
		assertEquals(53, composite.higherVersion());
		Connection data = simpleDatabase(53);
		composite.downgrade(data, null);
		assertEquals("downgrade from 53\n", upgradeOne.getLog());
		assertEquals(52, new DatabaseVersionPersistence(data).read());
	}

	StringBuilder log;

	@Test public void order() throws Exception {
		log = new StringBuilder();
		Upgrade composite =
			new CompositeUpgrade(
				new MyUpgrade("first"), 
				new MyUpgrade("second"), 
				new MyUpgrade("third"));

		Connection data = simpleDatabase(52);
		composite.upgrade(data, null);
		composite.downgrade(data, null);

		assertEquals(
			"upgrading first\n" +
			"upgrading second\n" +
			"upgrading third\n" +
			"downgrading third\n" +
			"downgrading second\n" +
			"downgrading first\n", 
			log.toString());

		assertEquals(52, new DatabaseVersionPersistence(data).read());
	}
	
	class MyUpgrade extends Upgrade {

		private final String which;

		MyUpgrade(String which) {
			super(53);
			this.which = which;
		}

		@Override
		public void downgrade(Connection connection, DatabaseVersionPersistence databaseVersionPersistence) throws IOException, SQLException {
			log.append("downgrading " + which + "\n");
		}

		@Override
		public void upgrade(Connection connection, DatabaseVersionPersistence databaseVersionPersistence) throws IOException, SQLException {
			log.append("upgrading " + which + "\n");
		}
		
	}
	
	private Connection simpleDatabase(int version) {
		Database database = DummyUpgrade.databaseWithVersionTable(version);
		return database.openConnection();
	}
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(CompositeUpgradeTest.class);
	}

}
