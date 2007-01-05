package org.mifos.application.accounts.financial.business.service.activity;

import java.util.ArrayList;
import java.util.List;

import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.financial.business.service.activity.accountingentry.BaseAccountingEntry;
import org.mifos.application.accounts.financial.business.service.activity.accountingentry.DisbursalAmountReversalAccountingEntry;

public class DisbursalAmountReversalFinancialActivity extends
		BaseFinancialActivity {

	public DisbursalAmountReversalFinancialActivity(
			AccountTrxnEntity accountTrxn) {
		super(accountTrxn);
	}

	@Override
	protected List<BaseAccountingEntry> getFinancialActionEntry() {
		List<BaseAccountingEntry> financialActionEntryList = new ArrayList<BaseAccountingEntry>();
		financialActionEntryList
				.add(new DisbursalAmountReversalAccountingEntry());
		return financialActionEntryList;
	}

}
