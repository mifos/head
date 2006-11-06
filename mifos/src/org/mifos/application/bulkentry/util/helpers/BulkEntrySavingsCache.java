package org.mifos.application.bulkentry.util.helpers;

import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.util.helpers.YesNoFlag;

public class BulkEntrySavingsCache {

	private SavingsBO account;

	private YesNoFlag yesNoFlag;

	public BulkEntrySavingsCache(SavingsBO account, YesNoFlag yesNoFlag) {
		this.account = account;
		this.yesNoFlag = yesNoFlag;
	}

	public SavingsBO getAccount() {
		return account;
	}

	public void setAccount(SavingsBO account) {
		this.account = account;
	}

	public YesNoFlag getYesNoFlag() {
		return yesNoFlag;
	}

	public void setYesNoFlag(YesNoFlag yesNoFlag) {
		this.yesNoFlag = yesNoFlag;
	}

}
