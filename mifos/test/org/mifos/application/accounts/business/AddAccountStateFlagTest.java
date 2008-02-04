package org.mifos.application.accounts.business;

import static org.junit.Assert.assertEquals;
import static org.mifos.framework.util.helpers.TestObjectFactory.TEST_LOCALE;
import junit.framework.JUnit4TestAdapter;

import org.hibernate.Session;
import org.junit.Test;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.persistence.Upgrade;


public class AddAccountStateFlagTest {

	private static final short FLAG_FEET_TOO_BIG = 12;

	@Test
	public void startFromStandardStore() throws Exception {
		TestDatabase database = TestDatabase.makeStandard();
		String start = database.dumpForComparison();
		
		Upgrade upgrade = new AddAccountStateFlag(
			72,
			FLAG_FEET_TOO_BIG,
			"Feet too big",
			TEST_LOCALE,
			"Rejected because feet are too big");

		upgradeAndCheck(database, upgrade);
		upgrade.downgrade(database.openConnection(), null);
		String afterUpAndDownGrade = database.dumpForComparison();

		assertEquals(start, afterUpAndDownGrade);
	}

	private void upgradeAndCheck(TestDatabase database, Upgrade upgrade) 
	throws Exception {
		upgrade.upgrade(database.openConnection(), null);
		Session session = database.openSession();
		AccountStateFlagEntity flag = (AccountStateFlagEntity) session.get(
			AccountStateFlagEntity.class, FLAG_FEET_TOO_BIG);
		flag.setLocaleId(TEST_LOCALE);

		assertEquals(FLAG_FEET_TOO_BIG, flag.getId());
		assertEquals(10, flag.getStatusId());
		assertEquals(false, flag.isFlagRetained());
		assertEquals("Feet too big", flag.getFlagDescription());

		assertEquals("Rejected because feet are too big", flag.getName());
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(AddAccountStateFlagTest.class);
	}

}
