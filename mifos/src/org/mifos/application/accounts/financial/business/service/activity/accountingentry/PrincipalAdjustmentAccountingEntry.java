package org.mifos.application.accounts.financial.business.service.activity.accountingentry;

import org.mifos.application.accounts.financial.business.FinancialActionBO;
import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionCache;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.application.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanTrxnDetailEntity;
import org.mifos.framework.util.helpers.Money;

public class PrincipalAdjustmentAccountingEntry extends BaseAccountingEntry {

	@Override
	protected void getSpecificAccountActionEntry() throws FinancialException {
		LoanTrxnDetailEntity loanTrxn = (LoanTrxnDetailEntity) financialActivity
				.getAccountTrxn();
		GLCodeEntity glcodeCredit = ((LoanBO) loanTrxn.getAccount())
				.getLoanOffering().getPrincipalGLcode();

		FinancialActionBO finActionPrincipal = FinancialActionCache
				.getFinancialAction(FinancialActionConstants.PRINCIPALPOSTING);
		addAccountEntryDetails(removeSign(loanTrxn.getPrincipalAmount()),
				finActionPrincipal, getGLcode(finActionPrincipal
						.getApplicableDebitCharts()), FinancialConstants.CREDIT);

		addAccountEntryDetails(removeSign(loanTrxn.getPrincipalAmount()),
				finActionPrincipal, glcodeCredit, FinancialConstants.DEBIT);

		// check if rounding is required
		Money roundedAmount = Money.round(loanTrxn.getPrincipalAmount());
		if (!roundedAmount.equals(loanTrxn.getPrincipalAmount())) {
			FinancialActionBO finActionRounding = FinancialActionCache
					.getFinancialAction(FinancialActionConstants.ROUNDING);

			addAccountEntryDetails(roundedAmount.subtract(
					loanTrxn.getPrincipalAmount()).negate(), finActionRounding,
					getGLcode(finActionPrincipal.getApplicableCreditCharts()),
					FinancialConstants.CREDIT);

			addAccountEntryDetails(roundedAmount.subtract(loanTrxn
					.getPrincipalAmount()), finActionRounding,
					getGLcode(finActionRounding.getApplicableCreditCharts()),
					FinancialConstants.DEBIT);
		}

	}

}
