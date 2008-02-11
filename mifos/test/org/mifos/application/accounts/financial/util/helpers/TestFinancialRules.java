package org.mifos.application.accounts.financial.util.helpers;

import static org.junit.Assert.assertTrue;
import junit.framework.JUnit4TestAdapter;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mifos.framework.util.helpers.TestCaseInitializer;

public class TestFinancialRules {
	public static junit.framework.Test testSuite() {
		return new JUnit4TestAdapter(TestFinancialRules.class);
	}

	@BeforeClass
	public static void init() throws Exception {
		// initialize Spring, Hibernate, etc.
		new TestCaseInitializer();
	}

	// TODO: replace these with something? Not sure what they were for.
//	@Test
//	public void testGetCategoryAssociatedToActionBankbalanceSuccess()
//			throws Exception {
//		assertEquals(FinancialRules.getCategoryAssociatedToAction(
//				FinancialActionConstants.PRINCIPALPOSTING,
//				FinancialConstants.DEBIT), CategoryConstants.BANKACCOUNTONE);
//	}
//
//	@Test
//	public void testGetCategoryAssociatedToActionLoanclientSuccess()
//			throws Exception {
//		assertEquals(FinancialRules.getCategoryAssociatedToAction(
//				FinancialActionConstants.PRINCIPALPOSTING,
//				FinancialConstants.CREDIT), CategoryConstants.LOANSADVANCES);
//	}

	@Test
	public void testSupportedActions() throws Exception {
		// TODO: use assertTrue() with String as first arg with a meaningful
		// description
		assertTrue(FinancialRules.getInstance().getCategoryAssociatedToAction(
				FinancialActionConstants.PRINCIPALPOSTING,
				FinancialConstants.DEBIT) > 0);
		
		// TODO: use assertTrue() with String as first arg with a meaningful
		// description
		assertTrue(FinancialRules.getInstance().getCategoryAssociatedToAction(
				FinancialActionConstants.PRINCIPALPOSTING,
				FinancialConstants.CREDIT) > 0);
	}

	/**
	 * Ensure a lookup for a category of a nonexistant financial action fails.
	 */
	@Test(expected = RuntimeException.class)
	public void testUnsupportedAction01() throws Exception {
		FinancialRules.getInstance().getCategoryAssociatedToAction((short) -1,
				FinancialConstants.DEBIT);
	}

	/**
	 * Reversal adjustments aren't supported???
	 */
	@Test(expected = RuntimeException.class)
	public void testUnsupportedAction02() throws Exception {
		FinancialRules.getInstance().getCategoryAssociatedToAction(
				FinancialActionConstants.REVERSAL_ADJUSTMENT,
				FinancialConstants.CREDIT);
	}

	/**
	 * Reversal adjustments aren't supported???
	 */
	@Test(expected = RuntimeException.class)
	public void testUnsupportedAction03() throws Exception {
		FinancialRules.getInstance().getCategoryAssociatedToAction(
				FinancialActionConstants.REVERSAL_ADJUSTMENT,
				FinancialConstants.DEBIT);
	}
}
