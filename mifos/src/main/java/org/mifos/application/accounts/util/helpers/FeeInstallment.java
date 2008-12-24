package org.mifos.application.accounts.util.helpers;

import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.framework.util.helpers.Money;

public class FeeInstallment
{
	private Short installmentId;
	private Money accountFee;
	private AccountFeesEntity accountFeesEntity=null;

	public Money getAccountFee() {
		return accountFee;
	}
	public void setAccountFee(Money accountFee) {
		this.accountFee = accountFee;
	}
	public AccountFeesEntity getAccountFeesEntity() {
		return accountFeesEntity;
	}
	public void setAccountFeesEntity(AccountFeesEntity accountFeesEntity) {
		this.accountFeesEntity = accountFeesEntity;
	}
	public Short getInstallmentId() {
		return installmentId;
	}
	public void setInstallmentId(Short installmentId) {
		this.installmentId = installmentId;
	}

}
