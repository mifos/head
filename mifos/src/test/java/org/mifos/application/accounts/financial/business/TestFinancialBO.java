package org.mifos.application.accounts.financial.business;

import java.util.Iterator;
import java.util.Set;

import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionCache;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.TestConstants;

public class TestFinancialBO extends MifosIntegrationTest {

	public TestFinancialBO() throws SystemException, ApplicationException {
        super();
    }

    public void testGetApplicableDebit() throws FinancialException {
		FinancialActionBO finActionPrincipal = FinancialActionCache
				.getFinancialAction(FinancialActionConstants.PRINCIPALPOSTING);

		Set<COABO> applicableDebitCategory = finActionPrincipal
				.getApplicableDebitCharts();

		assertEquals(applicableDebitCategory.size(), 1);
		Iterator<COABO> iterSubCategory = applicableDebitCategory.iterator();
		while (iterSubCategory.hasNext()) {

			COABO subCategoryCOA = iterSubCategory.next();
			assertEquals("Bank Account 1", subCategoryCOA.getAccountName());
		}

	}

	public void testGetApplicableCredit() throws FinancialException {
		FinancialActionBO finActionPrincipal = FinancialActionCache
				.getFinancialAction(FinancialActionConstants.PRINCIPALPOSTING);

		Set<COABO> applicableCreditCategory = finActionPrincipal
				.getApplicableCreditCharts();

		assertEquals(TestConstants.FINANCIAL_PRINCIPALPOSTING_SIZE,
				applicableCreditCategory.size());
	}

	public void testRoundingCredit() throws FinancialException {
		FinancialActionBO finActionRounding = FinancialActionCache
				.getFinancialAction(FinancialActionConstants.ROUNDING);
		Set<COABO> applicableCreditCategory = finActionRounding
				.getApplicableCreditCharts();
		assertEquals(applicableCreditCategory.size(), 1);
		for (COABO coa : applicableCreditCategory) {
			assertEquals("Income from 999 Account", coa.getAccountName());
		}

	}

}
