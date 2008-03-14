package org.mifos.application.productdefinition.business;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mifos.framework.util.helpers.TestObjectFactory.TEST_LOCALE;
import junit.framework.JUnit4TestAdapter;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.helpers.TestCaseInitializer;
import org.mifos.application.master.business.InterestTypesEntity;


public class AddInterestCalcRuleTest {

	

	/*
	 * We need the test case initializer in order to set up the
	 * message cache in MifosConfiguration.
	 */
	@BeforeClass
	public static void init() {
		new TestCaseInitializer();
	}
	

	
	@Test 
	public void validateLookupValueKeyTest() throws Exception {
		String validKey = "InterestTypes-DecliningBalance";
		String format = "InterestTypes-";
		assertTrue(AddInterestCalcRule.validateLookupValueKey(format, validKey));
		String invalidKey = "DecliningBalance";
		assertFalse(AddInterestCalcRule.validateLookupValueKey(format, invalidKey));
	}
	
	
	@Test 
	public void constructorTest() throws Exception {
		TestDatabase database = TestDatabase.makeStandard();
		String start = database.dumpForComparison();
		short newRuleId = 2555;
		short categoryId = 1;
		String description = "DecliningBalance";
		AddInterestCalcRule upgrade = null;
		try
		{
			// use deprecated construtor		
			upgrade = new AddInterestCalcRule(
					DatabaseVersionPersistence.APPLICATION_VERSION + 1,
				newRuleId, categoryId, 
				"DecliningBalance",description, 
				TEST_LOCALE,
				"DecliningBalance");
		}
		catch (Exception e)
		{
			assertEquals(e.getMessage(), AddInterestCalcRule.wrongConstructor);
		}
		String invalidKey ="DecliningBalance";
		
		try
		{
			// use invalid lookup key format
			upgrade = new AddInterestCalcRule(DatabaseVersionPersistence.APPLICATION_VERSION + 1, newRuleId , categoryId, invalidKey, description);	
		}
		catch (Exception e)
		{
			assertEquals(e.getMessage(), AddInterestCalcRule.wrongLookupValueKeyFormat);
		}
		String goodKey = "InterestTypes-NewDecliningBalance";
		//	use valid construtor and valid key
		upgrade = new AddInterestCalcRule(DatabaseVersionPersistence.APPLICATION_VERSION + 1, newRuleId, categoryId, goodKey, description);	
		upgrade.upgrade(database.openConnection(), null);
		Session session = database.openSession();
		InterestTypesEntity entity = (InterestTypesEntity) session.get(
				InterestTypesEntity.class, newRuleId);
		assertEquals(goodKey, entity.getLookUpValue().getLookUpName());
		upgrade.downgrade(database.openConnection(), null);
		String afterUpAndDownGrade = database.dumpForComparison();
		assertEquals(start, afterUpAndDownGrade);

	}

	public static junit.framework.Test testSuite() {
		return new JUnit4TestAdapter(AddInterestCalcRuleTest.class);
	}

}
