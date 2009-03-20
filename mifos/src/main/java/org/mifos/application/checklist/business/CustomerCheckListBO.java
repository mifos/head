/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
 
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
		if (!this.customerLevel.getId().equals(customerLevel.getId())
				|| !this.customerStatus.getId().equals(customerStatus.getId()))
			validateCheckListState(customerLevel.getId(), customerStatus
					.getId(), true);
		this.customerLevel = customerLevel;
		this.customerStatus = customerStatus;
		try {
			new CheckListPersistence().createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new CheckListException(e);
		}
	}

}