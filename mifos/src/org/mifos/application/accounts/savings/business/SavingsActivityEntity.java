package org.mifos.application.accounts.savings.business;

import java.sql.Timestamp;
import java.util.Date;

import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.util.helpers.Money;

public class SavingsActivityEntity extends PersistentObject {

	private Integer id;

	private PersonnelBO trxnCreatedBy;

	private AccountBO account;

	private Timestamp trxnCreatedDate;

	private AccountActionEntity activity;

	private Money amount;

	private Money balanceAmount;

	protected SavingsActivityEntity() {

	}

	public SavingsActivityEntity(PersonnelBO trxnCreatedBy,
			AccountActionEntity activity, Money amount, Money balanceAmount, Date trxnDate) {
		this.trxnCreatedBy = trxnCreatedBy;
		this.activity = activity;
		this.amount = amount;
		this.balanceAmount = balanceAmount;
		this.trxnCreatedDate = new Timestamp(trxnDate.getTime());
	}

	public AccountBO getAccount() {
		return account;
	}

	public void setAccount(AccountBO account) {
		this.account = account;
	}

	public AccountActionEntity getActivity() {
		return activity;
	}

	private void setActivity(AccountActionEntity activity) {
		this.activity = activity;
	}

	public Money getAmount() {
		return amount;
	}

	private void setAmount(Money amount) {
		this.amount = amount;
	}

	public Money getBalanceAmount() {
		return balanceAmount;
	}

	private void setBalanceAmount(Money balanceAmount) {
		this.balanceAmount = balanceAmount;
	}

	public Integer getId() {
		return id;
	}

	private void setId(Integer id) {
		this.id = id;
	}

	private PersonnelBO getTrxnCreatedBy() {
		return trxnCreatedBy;
	}

	public void setTrxnCreatedBy(PersonnelBO trxnCreatedBy) {
		this.trxnCreatedBy = trxnCreatedBy;
	}

	public Timestamp getTrxnCreatedDate() {
		return trxnCreatedDate;
	}

	private void setTrxnCreatedDate(Timestamp trxnCreatedDate) {
		this.trxnCreatedDate = trxnCreatedDate;
	}

}
