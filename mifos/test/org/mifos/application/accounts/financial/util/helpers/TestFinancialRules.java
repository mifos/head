package org.mifos.application.accounts.financial.util.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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

	@Test
	public void testSupportedActions() throws Exception {
		assertEquals("11201", FinancialRules.getInstance()
				.getGLAccountForAction(
						FinancialActionConstants.PRINCIPALPOSTING,
						FinancialConstants.DEBIT));
		assertEquals("13100", FinancialRules.getInstance()
				.getGLAccountForAction(
						FinancialActionConstants.PRINCIPALPOSTING,
						FinancialConstants.CREDIT));
	}

	/**
	 * Ensure a lookup for a category of a nonexistant financial action fails.
	 */
	@Test(expected = RuntimeException.class)
	public void testUnsupportedAction01() throws Exception {
		FinancialRules.getInstance().getGLAccountForAction((short) -1,
				FinancialConstants.DEBIT);
	}

	/**
	 * Reversal adjustments aren't supported???
	 */
	public void testUnsupportedAction02() throws Exception {
		assertNull(FinancialRules.getInstance().getGLAccountForAction(
				FinancialActionConstants.REVERSAL_ADJUSTMENT,
				FinancialConstants.CREDIT));
	}

	/**
	 * Reversal adjustments aren't supported???
	 */
	public void testUnsupportedAction03() throws Exception {
		assertNull(FinancialRules.getInstance().getGLAccountForAction(
				FinancialActionConstants.REVERSAL_ADJUSTMENT,
				FinancialConstants.DEBIT));
	}
}
