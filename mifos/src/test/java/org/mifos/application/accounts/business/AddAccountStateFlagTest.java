package org.mifos.application.accounts.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mifos.framework.util.helpers.TestObjectFactory.TEST_LOCALE;
import junit.framework.JUnit4TestAdapter;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.persistence.Upgrade;
import org.mifos.framework.util.helpers.TestCaseInitializer;


public class AddAccountStateFlagTest {

	private static final short FLAG_FEET_TOO_BIG = 12;
	//private TestDatabase database;

	/*
	 * We need the test case initializer in order to set up the
	 * message cache in MifosConfiguration.
	 */
	@BeforeClass
	public static void init() {
		new TestCaseInitializer();
	}
	
	/*@Before public void setUp() throws SystemException, ApplicationException {
		
		database = TestDatabase.makeStandard();
		database.installInThreadLocal();
		
	}*/

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
	}

	private void upgradeAndCheck(TestDatabase database, Upgrade upgrade) 
	throws Exception {
		upgrade.upgrade(database.openConnection(), null);
		/*
		 * Below is a workaround to make this test case work
		 * The upgrade is being done in a Mayfly database.
		 * After the upgrade, we set up the Mayfly session to be
		 * used by the next call to HibernateUtils.getSessionTL()
		 * Then call init() on MifosConfiguration to refresh the
		 * values from the Mayfly database, so that the call to 
		 * flag.getName() below can find the new value.
		 */
		database.installInThreadLocal();
		MifosConfiguration.getInstance().init();
		
		Session session = database.openSession();
		AccountStateFlagEntity flag = (AccountStateFlagEntity) session.get(
			AccountStateFlagEntity.class, FLAG_FEET_TOO_BIG);
		flag.setLocaleId(TEST_LOCALE);

		assertEquals((Object)FLAG_FEET_TOO_BIG, (Object)flag.getId());
		assertEquals(10, (short) flag.getStatusId());
		assertEquals(false, flag.isFlagRetained());
		assertEquals("Feet too big", flag.getFlagDescription());

		assertEquals("Rejected because feet are too big", flag.getName());
	}
	
	@Test 
	public void validateLookupValueKeyTest() throws Exception {
		String validKey = "AccountFlags-Withdraw";
		String format = "AccountFlags-";
		assertTrue(AddAccountStateFlag.validateLookupValueKey(format, validKey));
		String invalidKey = "Withdraw";
		assertFalse(AddAccountStateFlag.validateLookupValueKey(format, invalidKey));
	}
	
	@Test 
	public void constructorTest() throws Exception {
		TestDatabase database = TestDatabase.makeStandard();
		short newId = 31500;
		AddAccountStateFlag upgrade = null;
		try
		{
			// use deprecated construtor		
			upgrade = new AddAccountStateFlag(
					DatabaseVersionPersistence.APPLICATION_VERSION + 1,
				newId,
				"NewAccountStateFlag",
				TEST_LOCALE,
				"NewAccountStateFlag");
		}
		catch (Exception e)
		{
			assertEquals(e.getMessage(), AddAccountStateFlag.wrongConstructor);
		}
		String invalidKey ="NewAccountStateFlag";
		
		try
		{
			// use invalid lookup key format
			upgrade = new AddAccountStateFlag(DatabaseVersionPersistence.APPLICATION_VERSION + 1, newId , invalidKey, invalidKey);	
		}
		catch (Exception e)
		{
			assertEquals(e.getMessage(), AddAccountStateFlag.wrongLookupValueKeyFormat);
		}
		String goodKey = "AccountFlags-NewAccountStateFlag";
		//	use valid construtor and valid key
		upgrade = new AddAccountStateFlag(DatabaseVersionPersistence.APPLICATION_VERSION + 1, newId, goodKey, goodKey);	
		upgrade.upgrade(database.openConnection(), null);
		Session session = database.openSession();
		AccountStateFlagEntity flag = (AccountStateFlagEntity) session.get(
				AccountStateFlagEntity.class, newId);
		assertEquals(goodKey, flag.getLookUpValue().getLookUpName());
		MifosConfiguration.getInstance().init();

	}

	public static junit.framework.Test testSuite() {
		return new JUnit4TestAdapter(AddAccountStateFlagTest.class);
	}

}
