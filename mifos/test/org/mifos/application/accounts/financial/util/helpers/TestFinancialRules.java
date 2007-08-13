package org.mifos.application.accounts.financial.util.helpers;

import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.framework.MifosTestCase;

public class TestFinancialRules extends MifosTestCase {

	public void testGetCategoryAssociatedToActionBankbalanceSucess()
			throws FinancialException {
		assertEquals(FinancialRules.getCategoryAssociatedToAction(
				FinancialActionConstants.PRINCIPALPOSTING, FinancialRules.DEBIT),
				CategoryConstants.BANKACCOUNTONE);
	}

	public void testGetCategoryAssociatedToActionException() {
		try {
			FinancialRules.getCategoryAssociatedToAction((short) -1, FinancialRules.DEBIT);
			fail();
		} catch (Exception fine) {

			assertTrue(true);

		}
	}

	public void testGetCategoryAssociatedToActionLoanclientSucess()
			throws FinancialException {
		assertEquals(FinancialRules.getCategoryAssociatedToAction(
				FinancialActionConstants.PRINCIPALPOSTING, FinancialRules.CREDIT),
				CategoryConstants.LOANSADVANCES);
	}
	
	public void testAllCategories() {
		String[] transactionTypes = {FinancialRules.DEBIT, FinancialRules.CREDIT};
	}

}
