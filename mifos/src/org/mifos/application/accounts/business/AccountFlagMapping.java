package org.mifos.application.accounts.business;

import org.mifos.framework.business.PersistentObject;

public class AccountFlagMapping extends PersistentObject{

	private Integer accountFlagId;
	private AccountStateFlagEntity flag;

	public Integer getAccountFlagId() {
		return accountFlagId;
	}

	private void setAccountFlagId(Integer accountFlagId) {
		this.accountFlagId = accountFlagId;
	}

	public AccountStateFlagEntity getFlag() {
		return flag;
	}

	public void setFlag(AccountStateFlagEntity flag) {
		this.flag = flag;
	}
	
}
