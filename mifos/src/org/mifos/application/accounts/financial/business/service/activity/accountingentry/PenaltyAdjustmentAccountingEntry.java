package org.mifos.application.accounts.financial.business.service.activity.accountingentry;

import org.mifos.application.accounts.financial.business.FinancialActionBO;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionCache;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.application.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.application.accounts.loan.business.LoanTrxnDetailEntity;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.customer.business.CustomerTrxnDetailEntity;
import org.mifos.framework.util.helpers.Money;

public class PenaltyAdjustmentAccountingEntry extends BaseAccountingEntry {

	@Override
	protected void getSpecificAccountActionEntry() throws FinancialException {
		Money amount = new Money();
		if (financialActivity.getAccountTrxn().getAccount().getAccountType()
				.getAccountTypeId().equals(AccountTypes.LOANACCOUNT.getValue()))
			amount = ((LoanTrxnDetailEntity) financialActivity.getAccountTrxn())
					.getMiscPenaltyAmount();
		else if (financialActivity.getAccountTrxn().getAccount()
				.getAccountType().getAccountTypeId().equals(AccountTypes.CUSTOMERACCOUNT.getValue()))
			amount = ((CustomerTrxnDetailEntity) financialActivity
					.getAccountTrxn()).getMiscPenaltyAmount();

		FinancialActionBO finActionMiscPenalty = FinancialActionCache
				.getFinancialAction(FinancialActionConstants.MISCPENALTYPOSTING);
		addAccountEntryDetails(removeSign(amount), finActionMiscPenalty,
				getGLcode(finActionMiscPenalty.getApplicableDebitCharts()),
				FinancialConstants.CREDIT);

		addAccountEntryDetails(removeSign(amount), finActionMiscPenalty,
				getGLcode(finActionMiscPenalty.getApplicableCreditCharts()),
				FinancialConstants.DEBIT);

	}
	
	private Money removeSign(Money amount){
		if(amount!=null && amount.getAmountDoubleValue()<0)
			return amount.negate();
		else
			return amount;
	}

}
