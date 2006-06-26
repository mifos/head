package org.mifos.application.accounts.financial.business.service.activity;

import java.util.ArrayList;
import java.util.List;

import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.financial.business.service.activity.accountingentry.BaseAccountingEntry;
import org.mifos.application.accounts.financial.business.service.activity.accountingentry.FeesAdjustmentAccountingEntry;
import org.mifos.application.accounts.financial.business.service.activity.accountingentry.InterestAdjustmentAccountingEntry;
import org.mifos.application.accounts.financial.business.service.activity.accountingentry.PenaltyAdjustmentAccountingEntry;
import org.mifos.application.accounts.financial.business.service.activity.accountingentry.PrincipalAdjustmentAccountingEntry;

public class LoanAdjustmentFinancialActivity extends BaseFinancialActivity {

	public LoanAdjustmentFinancialActivity(AccountTrxnEntity accountTrxn) {
		super(accountTrxn);
	}

	@Override
	protected List<BaseAccountingEntry> getFinancialActionEntry() {
		List<BaseAccountingEntry> financialActionEntryList = new ArrayList<BaseAccountingEntry>();
		financialActionEntryList.add(new PrincipalAdjustmentAccountingEntry());
		financialActionEntryList.add(new InterestAdjustmentAccountingEntry());
		financialActionEntryList.add(new FeesAdjustmentAccountingEntry());
		financialActionEntryList.add(new PenaltyAdjustmentAccountingEntry());
		return financialActionEntryList;
	}

}
