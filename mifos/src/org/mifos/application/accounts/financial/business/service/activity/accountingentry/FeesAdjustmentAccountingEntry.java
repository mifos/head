package org.mifos.application.accounts.financial.business.service.activity.accountingentry;

import java.util.Iterator;
import java.util.Set;

import org.mifos.application.accounts.business.FeesTrxnDetailEntity;
import org.mifos.application.accounts.financial.business.FinancialActionBO;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionCache;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.application.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.application.accounts.loan.business.LoanTrxnDetailEntity;
import org.mifos.framework.util.helpers.Money;

public class FeesAdjustmentAccountingEntry extends BaseAccountingEntry {

	@Override
	protected void getSpecificAccountActionEntry() throws FinancialException {
		LoanTrxnDetailEntity loanTrxn = (LoanTrxnDetailEntity) financialActivity
				.getAccountTrxn();
		Set<FeesTrxnDetailEntity> feesTrxn = loanTrxn.getFeesTrxnDetails();
		Iterator<FeesTrxnDetailEntity> iterFees = feesTrxn.iterator();
		FinancialActionBO finActionFee = FinancialActionCache
				.getFinancialAction(FinancialActionConstants.FEEPOSTING);
		while (iterFees.hasNext()) {
			FeesTrxnDetailEntity feeTrxn = iterFees.next();

			addAccountEntryDetails(removeSign(feeTrxn.getFeeAmount()), finActionFee,
					feeTrxn.getAccountFees().getFees().getGlCode(),
					FinancialConstants.DEBIT);

			addAccountEntryDetails(removeSign(feeTrxn.getFeeAmount()), finActionFee,
					getGLcode(finActionFee.getApplicableDebitCOA()),
					FinancialConstants.CREDIT);
		}
		// For Misc Fee
		FinancialActionBO finActionMiscFee = FinancialActionCache
				.getFinancialAction(FinancialActionConstants.MISCFEEPOSTING);
		addAccountEntryDetails(removeSign(loanTrxn.getMiscFeeAmount()), finActionMiscFee,
				getGLcode(finActionMiscFee.getApplicableDebitCOA()),
				FinancialConstants.CREDIT);

		addAccountEntryDetails(removeSign(loanTrxn.getMiscFeeAmount()), finActionMiscFee,
				getGLcode(finActionMiscFee.getApplicableCreditCOA()),
				FinancialConstants.DEBIT);

	}
	
	private Money removeSign(Money amount){
		if(amount!=null && amount.getAmountDoubleValue()<0)
			return amount.negate();
		else
			return amount;
	}

}
