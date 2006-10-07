package org.mifos.application.checklist.business;

import java.util.List;

import org.mifos.application.checklist.exceptions.CheckListException;
import org.mifos.application.customer.business.CustomerLevelEntity;
import org.mifos.application.customer.business.CustomerStatusEntity;

public class CustomerCheckListBO extends CheckListBO {

	private CustomerLevelEntity customerLevel;

	private CustomerStatusEntity customerStatus;

	public CustomerCheckListBO() {
	}

	public CustomerCheckListBO(CustomerLevelEntity customerLevel,
			CustomerStatusEntity customerStatus, String name,
			Short checkListStatus, List<String> details, Short prefferedLocale,
			Short userId) throws CheckListException {
		super(name, checkListStatus, details, prefferedLocale, userId);
		this.customerLevel = customerLevel;
		this.customerStatus = customerStatus;
	}

	public CustomerLevelEntity getCustomerLevel() {
		return customerLevel;
	}

	public void setCustomerLevel(CustomerLevelEntity customerLevelEntity) {
		this.customerLevel = customerLevelEntity;
	}

	public CustomerStatusEntity getCustomerStatus() {
		return customerStatus;
	}

	public void setCustomerStatus(CustomerStatusEntity customerStatus) {
		this.customerStatus = customerStatus;
	}

}