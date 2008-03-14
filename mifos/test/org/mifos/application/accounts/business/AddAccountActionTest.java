package org.mifos.application.accounts.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mifos.framework.util.helpers.TestObjectFactory.TEST_LOCALE;
import junit.framework.JUnit4TestAdapter;

import org.hibernate.Session;
import org.junit.Test;
import org.mifos.application.rolesandpermission.business.ActivityEntity;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.persistence.Upgrade;
import org.mifos.framework.security.AddActivity;


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
		upgrade.downgrade(database.openConnection(), null);
		String afterUpAndDownGrade = database.dumpForComparison();

		assertEquals(start, afterUpAndDownGrade);
	}

	private void upgradeAndCheck(TestDatabase database, Upgrade upgrade) 
	throws Exception {
		upgrade.upgrade(database.openConnection(), null);
		Session session = database.openSession();
		AccountActionEntity action = (AccountActionEntity) session.get(
				AccountActionEntity.class, SEND_TO_ORPHANS);
		action.setLocaleId(TEST_LOCALE);

		assertEquals(SEND_TO_ORPHANS, action.getId());
		assertEquals(" ", action.getLookUpValue().getLookUpName());
	}
	

	@Test 
	public void validateLookupValueKeyTest() throws Exception {
		String validKey = "AccountAction-LoanRepayment";
		String format = "AccountAction-";
		assertTrue(AddAccountAction.validateLookupValueKey(format, validKey));
		String invalidKey = "Action-LoanRepayment";
		assertFalse(AddAccountAction.validateLookupValueKey(format, invalidKey));
	}
	
	@Test 
	public void constructorTest() throws Exception {
		TestDatabase database = TestDatabase.makeStandard();
		String start = database.dumpForComparison();
		short newId = 31000;
		AddAccountAction upgrade = null;
		try
		{
			// use deprecated construtor
			upgrade = new AddAccountAction(
					DatabaseVersionPersistence.APPLICATION_VERSION + 1,
					newId,
					TEST_LOCALE,
					"NewAccountAction");
		}
		catch (Exception e)
		{
			assertEquals(e.getMessage(), AddAccountAction.wrongConstructor);
		}
		String invalidKey ="NewAccountAction";
		
		try
		{
			// use invalid lookup key format
			upgrade = new AddAccountAction(DatabaseVersionPersistence.APPLICATION_VERSION + 1, newId ,invalidKey);	
		}
		catch (Exception e)
		{
			assertEquals(e.getMessage(), AddAccountAction.wrongLookupValueKeyFormat);
		}
		String goodKey = "AccountAction-NewAccountAction";
		//	use valid construtor and valid key
		upgrade = new AddAccountAction(DatabaseVersionPersistence.APPLICATION_VERSION + 1, newId, goodKey);	
		upgrade.upgrade(database.openConnection(), null);
		Session session = database.openSession();
		AccountActionEntity action = (AccountActionEntity) session.get(
				AccountActionEntity.class, newId);
		assertEquals(goodKey, action.getLookUpValue().getLookUpName());
		upgrade.downgrade(database.openConnection(), null);
		String afterUpAndDownGrade = database.dumpForComparison();
		assertEquals(start, afterUpAndDownGrade);
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(AddAccountActionTest.class);
	}


}
