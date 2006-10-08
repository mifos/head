package org.mifos.application.checklist.business;

import java.util.List;

import org.mifos.application.checklist.exceptions.CheckListException;
import org.mifos.application.checklist.persistence.CheckListPersistence;
import org.mifos.application.checklist.util.helpers.CheckListType;
import org.mifos.application.customer.business.CustomerLevelEntity;
import org.mifos.application.customer.business.CustomerStatusEntity;
import org.mifos.framework.exceptions.PersistenceException;

public class CustomerCheckListBO extends CheckListBO {

	private CustomerLevelEntity customerLevel;

	private CustomerStatusEntity customerStatus;

	protected CustomerCheckListBO() {
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

	@Override
	public CheckListType getCheckListType() {
		return CheckListType.CUSTOMER_CHECKLIST;
	}

	public void update(CustomerLevelEntity customerLevel,
			CustomerStatusEntity customerStatus, String name,
			Short checkListStatus, List<String> details, Short prefferedLocale,
			Short userId) throws CheckListException {
		super.update(name, checkListStatus, details, prefferedLocale, userId);
		this.customerLevel = customerLevel;
		this.customerStatus = customerStatus;
		try {
			new CheckListPersistence().createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new CheckListException(e);
		}
	}
}