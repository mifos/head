package org.mifos.application.accounts.savings.business;

import java.sql.Timestamp;
import java.util.Date;

import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.util.helpers.Money;

public class SavingsActivityEntity extends PersistentObject {

	private final Integer id;

	private final PersonnelBO trxnCreatedBy;

	private final AccountBO account;

	private final Timestamp trxnCreatedDate;

	private final AccountActionEntity activity;

	private final Money amount;

	private final Money balanceAmount;

	protected SavingsActivityEntity() {
		this.id=null;
		this.account=null;
		this.trxnCreatedBy =null;
		this.activity = null;
		this.amount = null;
		this.balanceAmount = null;
		this.trxnCreatedDate = null;

	}

	public SavingsActivityEntity(PersonnelBO trxnCreatedBy,
			AccountActionEntity activity, Money amount, Money balanceAmount, Date trxnDate,AccountBO account ) {
		id=null;
		this.trxnCreatedBy = trxnCreatedBy;
		this.activity = activity;
		this.amount = amount;
		this.balanceAmount = balanceAmount;
		this.trxnCreatedDate = new Timestamp(trxnDate.getTime());
		this.account=account;
	}

	public AccountBO getAccount() {
		return account;
	}
	public AccountActionEntity getActivity() {
		return activity;
	}


	public Money getAmount() {
		return amount;
	}


	public Money getBalanceAmount() {
		return balanceAmount;
	}

	public Integer getId() {
		return id;
	}
	
	@SuppressWarnings("unused") // see .hbm.xml file
	private PersonnelBO getTrxnCreatedBy() {
		return trxnCreatedBy;
	}
	public Timestamp getTrxnCreatedDate() {
		return trxnCreatedDate;
	}


}
