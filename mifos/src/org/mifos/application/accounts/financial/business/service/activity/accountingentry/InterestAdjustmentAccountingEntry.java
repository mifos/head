package org.mifos.application.accounts.financial.business.service.activity.accountingentry;

import org.mifos.application.accounts.business.LoanTrxnDetailEntity;
import org.mifos.application.accounts.financial.business.FinancialActionBO;
import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionCache;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.application.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.framework.util.helpers.Money;

public class InterestAdjustmentAccountingEntry extends BaseAccountingEntry {

	@Override
	protected void getSpecificAccountActionEntry() throws FinancialException {
		LoanTrxnDetailEntity loanTrxn = (LoanTrxnDetailEntity) financialActivity
				.getAccountTrxn();
		GLCodeEntity glcodeCredit = ((LoanBO) loanTrxn.getAccount())
				.getLoanOffering().getInterestGLcode();

		FinancialActionBO finActionInterest = FinancialActionCache
				.getFinancialAction(FinancialActionConstants.INTERESTPOSTING);
		addAccountEntryDetails(removeSign(loanTrxn.getInterestAmount()), finActionInterest,
				getGLcode(finActionInterest.getApplicableDebitCOA()),
				FinancialConstants.CREDIT);

		addAccountEntryDetails(removeSign(loanTrxn.getInterestAmount()), finActionInterest,
				glcodeCredit, FinancialConstants.DEBIT);

		// check if rounding is required
		Money roundedAmount = Money.round(loanTrxn.getInterestAmount());
		if (!roundedAmount.equals(loanTrxn.getInterestAmount())) {
			FinancialActionBO finActionRounding = FinancialActionCache
					.getFinancialAction(FinancialActionConstants.ROUNDING);

			addAccountEntryDetails(roundedAmount.subtract(
					loanTrxn.getInterestAmount()).negate(), finActionRounding,
					getGLcode(finActionInterest.getApplicableCreditCOA()),
					FinancialConstants.DEBIT);

			addAccountEntryDetails(roundedAmount.subtract(loanTrxn
					.getInterestAmount()), finActionRounding,
					getGLcode(finActionRounding.getApplicableCreditCOA()),
					FinancialConstants.CREDIT);
		}

	}
	
	private Money removeSign(Money amount){
		if(amount!=null && amount.getAmountDoubleValue()<0)
			return amount.negate();
		else
			return amount;
	}

}
