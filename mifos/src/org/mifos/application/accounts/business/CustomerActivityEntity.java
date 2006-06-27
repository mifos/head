package org.mifos.application.accounts.business;

import java.sql.Date;

import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.util.helpers.Money;

public class CustomerActivityEntity extends PersistentObject {

	private Integer customerActivityId;

	private Money amount;

	private CustomerAccountBO customerAccount;

	private String description;

	private PersonnelBO personnel;

	protected CustomerActivityEntity() {
	}

	public CustomerActivityEntity(PersonnelBO personnel, String description,
			Money amount) {
		this.personnel = personnel;
		this.amount = amount;
		this.description = description;
		this.createdDate = new Date(System.currentTimeMillis());
	}

	public Money getAmount() {
		return amount;
	}

	private void setAmount(Money amount) {
		this.amount = amount;
	}

	private CustomerAccountBO getCustomerAccount() {
		return customerAccount;
	}

	public void setCustomerAccount(CustomerAccountBO customerAccount) {
		this.customerAccount = customerAccount;
	}

	private Integer getCustomerActivityId() {
		return customerActivityId;
	}

	private void setCustomerActivityId(Integer customerActivityId) {
		this.customerActivityId = customerActivityId;
	}

	public String getDescription() {
		return description;
	}

	private void setDescription(String description) {
		this.description = description;
	}

	public PersonnelBO getPersonnel() {
		return personnel;
	}

	private void setPersonnel(PersonnelBO personnel) {
		this.personnel = personnel;
	}

}
