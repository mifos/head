package org.mifos.application.accounts.financial.business;

import java.util.Iterator;
import java.util.Set;

import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.financial.util.helpers.CategoryConstants;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionCache;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.application.accounts.financial.util.helpers.FinancialInitializer;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.util.helpers.TestConstants;

public class TestFinancialBO extends MifosTestCase {

	@Override
	protected void setUp() throws Exception {
		FinancialInitializer.initialize();
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
			if (subCategoryCOA.getCategoryId() == CategoryConstants.BANKACCOUNTONE)
				assertTrue(true);
			else
				assertTrue(false);
		}

	}

	public void testGetApplicableCredit() throws FinancialException {
		FinancialActionBO finActionPrincipal = FinancialActionCache
				.getFinancialAction(FinancialActionConstants.PRINCIPALPOSTING);

		Set<COABO> applicableCreditCategory = finActionPrincipal
				.getApplicableCreditCharts();

		assertEquals(applicableCreditCategory.size(),
				TestConstants.FINANCIAL_PRINCIPALPOSTING_SIZE);
	}

	public void testRoundingCredit() throws FinancialException {
		FinancialActionBO finActionRounding = FinancialActionCache
				.getFinancialAction(FinancialActionConstants.ROUNDING);
		Set<COABO> applicableCreditCategory = finActionRounding
				.getApplicableCreditCharts();
		assertEquals(applicableCreditCategory.size(), 1);
		for (COABO coa : applicableCreditCategory) {
			assertEquals(coa.getCategoryId().shortValue(),
					CategoryConstants.ROUNDINGGL);
		}

	}

}
