package org.mifos.application.accounts.financial.util.helpers;

import junit.framework.TestCase;

import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.financial.util.helpers.CategoryConstants;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.application.accounts.financial.util.helpers.FinancialRules;

public class TestFinancialRules extends TestCase {

	public void testGetCategoryAssociatedToActionBankbalanceSucess()
			throws FinancialException {
		assertEquals(FinancialRules.getCategoryAssociatedToAction(
				FinancialActionConstants.PRINCIPALPOSTING, "debit"),
				CategoryConstants.BANKACCOUNTONE);
	}

	public void testGetCategoryAssociatedToActionException() {
		try {
			FinancialRules.getCategoryAssociatedToAction((short) -1, "debit");
			assertTrue(false);
		} catch (FinancialException fine) {

			assertTrue(true);

		}
	}

	public void testGetCategoryAssociatedToActionLoanclientSucess()
			throws FinancialException {
		assertEquals(FinancialRules.getCategoryAssociatedToAction(
				FinancialActionConstants.PRINCIPALPOSTING, "credit"),
				CategoryConstants.LOANSADVANCES);
	}
	
	

}
