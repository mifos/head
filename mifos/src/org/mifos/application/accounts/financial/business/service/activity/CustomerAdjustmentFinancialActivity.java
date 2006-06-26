package org.mifos.application.accounts.financial.business.service.activity;

import java.util.ArrayList;
import java.util.List;

import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.financial.business.service.activity.accountingentry.BaseAccountingEntry;
import org.mifos.application.accounts.financial.business.service.activity.accountingentry.CustomerFeesAdjustmentAccountingEntry;
import org.mifos.application.accounts.financial.business.service.activity.accountingentry.CustomerPenaltyAdjustmentAccountingEntry;

public class CustomerAdjustmentFinancialActivity extends
		BaseFinancialActivity {

	public CustomerAdjustmentFinancialActivity(AccountTrxnEntity accountTrxn) {
		super(accountTrxn);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected List<BaseAccountingEntry> getFinancialActionEntry() {
		List<BaseAccountingEntry> financialActionEntryList = new ArrayList<BaseAccountingEntry>();
		financialActionEntryList.add(new CustomerFeesAdjustmentAccountingEntry());
		financialActionEntryList.add(new CustomerPenaltyAdjustmentAccountingEntry());
		return financialActionEntryList;
	}

}
