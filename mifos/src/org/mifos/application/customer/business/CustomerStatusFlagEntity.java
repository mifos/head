package org.mifos.application.customer.business;

import org.mifos.application.customer.util.helpers.CustomerStatusFlag;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.util.helpers.YesNoFlag;

public class CustomerStatusFlagEntity extends MasterDataEntity {
	private Short blacklisted;
	private String flagDescription;
	private CustomerStatusEntity customerStatus;

	/*
	 * Adding a default constructor is hibernate's requirement and should not be
	 * used to create a valid Object.
	 */
	protected CustomerStatusFlagEntity() {
		super();
	}

	protected CustomerStatusFlagEntity(CustomerStatusFlag statusFlag) {
		super(statusFlag.getValue());
	}

	public CustomerStatusEntity getCustomerStatus() {
		return customerStatus;
	}

	public boolean isBlackListed() {
		return (blacklisted.equals(YesNoFlag.YES.getValue()));
	}

	public String getFlagDescription() {
		return flagDescription;
	}

	public void setFlagDescription(String flagDescription) {
		this.flagDescription = flagDescription;
	}
	
}
