package org.mifos.application.accounts.financial.business.service.activity.accountingentry;

import org.mifos.application.accounts.financial.business.FinancialActionBO;
import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionCache;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.application.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanTrxnDetailEntity;

public class WriteOffAccountingEntry extends BaseAccountingEntry {

	@Override
	protected void getSpecificAccountActionEntry() throws FinancialException {
		LoanTrxnDetailEntity loanTrxn = (LoanTrxnDetailEntity) financialActivity
				.getAccountTrxn();

		FinancialActionBO finActionWriteOff = FinancialActionCache
				.getFinancialAction(FinancialActionConstants.WRITEOFF);
		addAccountEntryDetails(loanTrxn.getPrincipalAmount(),
				finActionWriteOff, getGLcode(finActionWriteOff
						.getApplicableDebitCharts()), FinancialConstants.DEBIT);
		GLCodeEntity glcodeCredit = ((LoanBO) loanTrxn.getAccount())
				.getLoanOffering().getPrincipalGLcode();
		addAccountEntryDetails(loanTrxn.getPrincipalAmount(),
				finActionWriteOff, glcodeCredit, FinancialConstants.CREDIT);

	}

}
