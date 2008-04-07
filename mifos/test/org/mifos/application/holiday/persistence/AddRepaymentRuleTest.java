package org.mifos.application.holiday.persistence;



import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mifos.framework.util.helpers.TestObjectFactory.TEST_LOCALE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
import org.mifos.application.holiday.persistence.AddRepaymentRule;
import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
import org.mifos.application.holiday.business.RepaymentRuleEntity;


public class AddRepaymentRuleTest {


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
		String validKey = "RepaymentRule-NewSameDay";
		String format = "RepaymentRule-";
		assertTrue(AddRepaymentRule.validateLookupValueKey(format, validKey));
		String invalidKey = "NewSameDay";
		assertFalse(AddRepaymentRule.validateLookupValueKey(format, invalidKey));
	}
	

	
	@Test 
	public void constructorTest() throws Exception {
		TestDatabase database = TestDatabase.makeStandard();
		String start = database.dumpForComparison();
		AddRepaymentRule upgrade = null;
		try
		{
			// use deprecated construtor		
			upgrade = new AddRepaymentRule(
					DatabaseVersionPersistence.APPLICATION_VERSION + 1,
				RepaymentRuleTypes.SAME_DAY, 
				TEST_LOCALE,
				"SameDayValue");
		}
		catch (Exception e)
		{
			assertEquals(e.getMessage(), AddRepaymentRule.wrongConstructor);
		}
		String invalidKey ="SameDay";
		
		try
		{
			// use invalid lookup key format
			upgrade = new AddRepaymentRule(DatabaseVersionPersistence.APPLICATION_VERSION + 1, RepaymentRuleTypes.SAME_DAY,  invalidKey);	
		}
		catch (Exception e)
		{
			assertEquals(e.getMessage(), AddRepaymentRule.wrongLookupValueKeyFormat);
		}

		String goodKey = "RepaymentRule-NextWorkingDay";
		//	use valid construtor and valid key
		upgrade = new AddRepaymentRule(DatabaseVersionPersistence.APPLICATION_VERSION + 1, RepaymentRuleTypes.NEXT_WORKING_DAY,  goodKey);	
		String afterUpAndDownGrade = database.dumpForComparison();
		assertEquals(start, afterUpAndDownGrade);

	}

	public static junit.framework.Test testSuite() {
		return new JUnit4TestAdapter(AddRepaymentRuleTest.class);
	}

}

