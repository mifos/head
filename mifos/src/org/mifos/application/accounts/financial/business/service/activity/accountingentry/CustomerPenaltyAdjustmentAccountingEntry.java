package org.mifos.application.accounts.financial.business.service.activity.accountingentry;

import org.mifos.application.accounts.financial.business.FinancialActionBO;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionCache;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.application.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.application.customer.business.CustomerTrxnDetailEntity;
import org.mifos.framework.util.helpers.Money;

public class CustomerPenaltyAdjustmentAccountingEntry extends
		BaseAccountingEntry {

	@Override
	protected void getSpecificAccountActionEntry() throws FinancialException {
		CustomerTrxnDetailEntity customertrxn = (CustomerTrxnDetailEntity)financialActivity
				.getAccountTrxn();
		FinancialActionBO finActionMiscPenalty = FinancialActionCache
				.getFinancialAction(FinancialActionConstants.MISCPENALTYPOSTING);
		
		
		addAccountEntryDetails(removeSign(customertrxn.getMiscPenaltyAmount()),
				finActionMiscPenalty, getGLcode(finActionMiscPenalty
						.getApplicableDebitCharts()), FinancialConstants.CREDIT);
		
		
		addAccountEntryDetails(removeSign(customertrxn.getMiscPenaltyAmount()),
				finActionMiscPenalty, getGLcode(finActionMiscPenalty
						.getApplicableCreditCharts()), FinancialConstants.DEBIT);

	}
}
