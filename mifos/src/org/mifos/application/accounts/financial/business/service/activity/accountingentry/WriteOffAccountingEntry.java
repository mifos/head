package org.mifos.application.accounts.financial.business.service.activity.accountingentry;

import org.mifos.application.accounts.business.LoanTrxnDetailEntity;
import org.mifos.application.accounts.financial.business.FinancialActionBO;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionCache;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.application.accounts.financial.util.helpers.FinancialConstants;

public class WriteOffAccountingEntry extends BaseAccountingEntry {

	@Override
	protected void getSpecificAccountActionEntry() throws FinancialException {
		LoanTrxnDetailEntity loanTrxn = (LoanTrxnDetailEntity) financialActivity.getAccountTrxn();
		
		FinancialActionBO finActionWriteOff = FinancialActionCache.getFinancialAction(FinancialActionConstants.WRITEOFF);
		addAccountEntryDetails(loanTrxn.getPrincipalAmount(),
				finActionWriteOff, getGLcode(finActionWriteOff.getApplicableDebitCOA()), FinancialConstants.DEBIT);
		
		addAccountEntryDetails(loanTrxn.getPrincipalAmount(),finActionWriteOff, getGLcode(finActionWriteOff.getApplicableCreditCOA()), FinancialConstants.CREDIT);

	}

}
