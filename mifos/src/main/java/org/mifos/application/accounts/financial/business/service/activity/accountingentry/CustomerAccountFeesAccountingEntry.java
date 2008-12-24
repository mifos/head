package org.mifos.application.accounts.financial.business.service.activity.accountingentry;

import java.util.Iterator;
import java.util.Set;

import org.mifos.application.accounts.business.FeesTrxnDetailEntity;
import org.mifos.application.accounts.financial.business.FinancialActionBO;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionCache;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.application.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.application.customer.business.CustomerTrxnDetailEntity;

public class CustomerAccountFeesAccountingEntry extends BaseAccountingEntry {

	@Override
	protected void getSpecificAccountActionEntry() throws FinancialException {
		CustomerTrxnDetailEntity customertrxn = (CustomerTrxnDetailEntity) financialActivity
				.getAccountTrxn();
		Set<FeesTrxnDetailEntity> feesTrxn = customertrxn.getFeesTrxnDetails();
		Iterator<FeesTrxnDetailEntity> iterFees = feesTrxn.iterator();
		FinancialActionBO finActionFee = FinancialActionCache
				.getFinancialAction(FinancialActionConstants.FEEPOSTING);
		while (iterFees.hasNext()) {
			FeesTrxnDetailEntity feeTrxn = iterFees.next();

			addAccountEntryDetails(feeTrxn.getFeeAmount(), finActionFee,
					feeTrxn.getAccountFees().getFees().getGlCode(),
					FinancialConstants.CREDIT);

			addAccountEntryDetails(feeTrxn.getFeeAmount(), finActionFee,
					getGLcode(finActionFee.getApplicableDebitCharts()),
					FinancialConstants.DEBIT);
		}
		// For Misc Fee
		FinancialActionBO finActionMiscFee = FinancialActionCache
				.getFinancialAction(FinancialActionConstants.CUSTOMERACCOUNTMISCFEESPOSTING);
		addAccountEntryDetails(customertrxn.getMiscFeeAmount(), finActionMiscFee,
				getGLcode(finActionMiscFee.getApplicableDebitCharts()),
				FinancialConstants.DEBIT);

		addAccountEntryDetails(customertrxn.getMiscFeeAmount(), finActionMiscFee,
				getGLcode(finActionMiscFee.getApplicableCreditCharts()),
				FinancialConstants.CREDIT);

	}

}
