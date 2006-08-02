package org.mifos.application.customer.business;

import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.util.helpers.YesNoFlag;

public class CustomerStatusEntity extends MasterDataEntity {

	private String description;

	private CustomerLevelEntity customerLevel;

	private Short optional;

	/*
	 * Adding a default constructor is hibernate's requirement and should not be
	 * used to create a valid Object.
	 */
	protected CustomerStatusEntity() {
	}

	public CustomerStatusEntity(CustomerStatus customerStatus) {
		super(customerStatus.getValue());
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
}
