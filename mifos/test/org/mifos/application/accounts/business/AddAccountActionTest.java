package org.mifos.application.accounts.business;

import static org.junit.Assert.assertEquals;
import static org.mifos.framework.util.helpers.TestObjectFactory.TEST_LOCALE;
import junit.framework.JUnit4TestAdapter;

import org.hibernate.Session;
import org.junit.Test;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.persistence.Upgrade;


public class AddAccountActionTest {

	private static final short SEND_TO_ORPHANS = 43;

	@Test
	public void startFromStandardStore() throws Exception {
		TestDatabase database = TestDatabase.makeStandard();
		String start = database.dumpForComparison();
		
		Upgrade upgrade = new AddAccountAction(
			72,
			SEND_TO_ORPHANS,
			TEST_LOCALE,
			"Send money to orphans");

		upgradeAndCheck(database, upgrade);
		upgrade.downgrade(database.openConnection());
		String afterUpAndDownGrade = database.dumpForComparison();

		assertEquals(start, afterUpAndDownGrade);
	}

	private void upgradeAndCheck(TestDatabase database, Upgrade upgrade) 
	throws Exception {
		upgrade.upgrade(database.openConnection());
		Session session = database.openSession();
		AccountActionEntity action = (AccountActionEntity) session.get(
				AccountActionEntity.class, SEND_TO_ORPHANS);
		action.setLocaleId(TEST_LOCALE);

		assertEquals(SEND_TO_ORPHANS, action.getId());
		assertEquals("Send money to orphans", action.getName());
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(AddAccountActionTest.class);
	}


}
