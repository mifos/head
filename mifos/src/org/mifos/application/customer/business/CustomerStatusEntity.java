package org.mifos.application.customer.business;

import java.util.HashSet;
import java.util.Set;

import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.business.AccountStateFlagEntity;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.master.business.StateEntity;
import org.mifos.application.util.helpers.YesNoFlag;

public class CustomerStatusEntity extends StateEntity {

	private String description;

	private CustomerLevelEntity customerLevel;

	private Short optional;
	
	private Set<CustomerStatusFlagEntity> flagSet;
	/*
	 * Adding a default constructor is hibernate's requirement and should not be
	 * used to create a valid Object.
	 */
	protected CustomerStatusEntity() {
	}

	public CustomerStatusEntity(CustomerStatus customerStatus) {
		super(customerStatus.getValue());
		this.flagSet = new HashSet<CustomerStatusFlagEntity>();
	}
	
	public CustomerStatusEntity(Short customerStateId) {
		super(customerStateId);
		this.flagSet = new HashSet<CustomerStatusFlagEntity>();
		
	}

	public CustomerLevelEntity getCustomerLevel() {
		return customerLevel;
	}

	public String getDescription() {
		return description;
	}
	
	public boolean isOptional(){
		return optional.equals(YesNoFlag.YES.getValue());
	}

	public Set<CustomerStatusFlagEntity> getFlagSet() {
		return flagSet;
	}

	public void setFlagSet(Set<CustomerStatusFlagEntity> flagSet) {
		this.flagSet = flagSet;
	}
		
}
