package org.mifos.application.accounts.business;

import java.util.Date;

import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.util.helpers.Money;

public class CustomerActivityEntity extends PersistentObject {

	private final Integer customerActivityId;

	private final Money amount;

	private final CustomerAccountBO customerAccount;

	private final String description;

	private final PersonnelBO personnel;

	protected CustomerActivityEntity() {
		customerActivityId = null;
		this.customerAccount = null;
		this.personnel = null;
		this.amount = null;
		this.description = null;
		this.createdDate = null;
	}

	public CustomerActivityEntity(CustomerAccountBO customerAccount,
			PersonnelBO personnel, Money amount, String description,
			Date trxnDate) {
		customerActivityId = null;
		this.customerAccount = customerAccount;
		this.personnel = personnel;
		this.amount = amount;
		this.description = description;
		this.createdDate = trxnDate;
	}

	public Money getAmount() {
		return amount;
	}

	public String getDescription() {
		return description;
	}

	public PersonnelBO getPersonnel() {
		return personnel;
	}

}
