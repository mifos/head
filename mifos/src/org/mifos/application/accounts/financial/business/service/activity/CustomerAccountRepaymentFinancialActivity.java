package org.mifos.application.accounts.financial.business.service.activity;

import java.util.ArrayList;
import java.util.List;

import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.financial.business.service.activity.accountingentry.BaseAccountingEntry;
import org.mifos.application.accounts.financial.business.service.activity.accountingentry.CustomerAccountFeesAccountingEntry;
import org.mifos.application.accounts.financial.business.service.activity.accountingentry.PenaltyAccountingEntry;

public class CustomerAccountRepaymentFinancialActivity extends
		BaseFinancialActivity {

	public CustomerAccountRepaymentFinancialActivity(
			AccountTrxnEntity accountTrxn) {
		super(accountTrxn);
	}

	@Override
	protected List<BaseAccountingEntry> getFinancialActionEntry() {
		List<BaseAccountingEntry> financialActionEntryList = new ArrayList<BaseAccountingEntry>();
		financialActionEntryList.add(new CustomerAccountFeesAccountingEntry());
		financialActionEntryList.add(new PenaltyAccountingEntry());
		return financialActionEntryList;
	}

}
