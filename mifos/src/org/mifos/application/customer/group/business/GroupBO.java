/**

 * Group.java    version: 1.0



 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.



 * Apache License
 * Copyright (c) 2005-2006 Grameen Foundation USA
 *

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the

 * License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license

 * and how it is applied.

 *

 */

package org.mifos.application.customer.group.business;

import java.util.Date;
import java.util.List;

import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;

/**
 * This class denotes the Group (row in customer table) object and all
 * attributes associated with it. It has a composition of other objects like
 * Custom fields, fees, personnel etc., since it inherits from Customer
 * 
 * @author navitas
 */
public class GroupBO extends CustomerBO {

	private GroupPerformanceHistoryEntity performanceHistory;

	protected GroupBO() {
		super();
	}

	// TODO: removed searchId from parameter and generate internally
	public GroupBO(UserContext userContext, String displayName,
			CustomerStatus customerStatus, String externalId, Date mfiJoiningDate, Address address,
			List<CustomFieldView> customFields, List<FeeView> fees,
			Short formedById, Short officeId, CustomerBO parentCustomer,
			String searchId) throws CustomerException {
		this(userContext, displayName, customerStatus, externalId, mfiJoiningDate, address, customFields,
				fees, formedById, officeId, parentCustomer, null, null, searchId);
	}

	public GroupBO(UserContext userContext, String displayName,
			CustomerStatus customerStatus, String externalId, Date mfiJoiningDate, Address address,
			List<CustomFieldView> customFields, List<FeeView> fees,
			Short formedById, Short officeId, MeetingBO meeting,
			Short loanOfficerId, String searchId) throws CustomerException{
		this(userContext, displayName, customerStatus, externalId, mfiJoiningDate, address, customFields,
				fees, formedById, officeId, null, meeting, loanOfficerId, searchId);
	}

	private GroupBO(UserContext userContext, String displayName,
			CustomerStatus customerStatus, String externalId, Date mfiJoiningDate, Address address,
			List<CustomFieldView> customFields, List<FeeView> fees,
			Short formedById, Short officeId, CustomerBO parentCustomer,
			MeetingBO meeting, Short loanOfficerId, String searchId)
			throws CustomerException{
		super(userContext, displayName, CustomerLevel.GROUP, customerStatus, externalId, mfiJoiningDate,
				address, customFields, fees, formedById, officeId, parentCustomer,
				meeting, loanOfficerId);
		this.setSearchId(searchId);
		if (customerStatus.equals(CustomerStatus.GROUP_ACTIVE.getValue()))
			this.setCustomerActivationDate(this.getCreatedDate());
	}

	@Override
	public boolean isActive() {
		return getCustomerStatus().getId().equals(CustomerStatus.GROUP_ACTIVE.getValue());
	}

	@Override
	public GroupPerformanceHistoryEntity getPerformanceHistory() {
		return performanceHistory;
	}

	public void setPerformanceHistory(
			GroupPerformanceHistoryEntity performanceHistory) {
		if (performanceHistory != null)
			performanceHistory.setGroup(this);
		this.performanceHistory = performanceHistory;
	}

	@Override
	public void generatePortfolioAtRisk() throws PersistenceException,
			ServiceException {
		Money amount = getBalanceForAccountsAtRisk();
		List<CustomerBO> clients = new CustomerPersistence().getAllChildrenForParent(
				getSearchId(), getOffice().getOfficeId(),
				CustomerConstants.GROUP_LEVEL_ID);
		if (clients != null && !clients.isEmpty()) {
			for (CustomerBO client : clients) {
				amount = amount.add(client.getBalanceForAccountsAtRisk());
			}
		}
		if (getPerformanceHistory() != null
				&& getPerformanceHistory().getTotalOutStandingLoanAmount()
						.getAmountDoubleValue() != 0.0)
			getPerformanceHistory().setPortfolioAtRisk(
					new Money(String.valueOf(amount.getAmountDoubleValue()
							/ getPerformanceHistory()
									.getTotalOutStandingLoanAmount()
									.getAmountDoubleValue())));
		new CustomerPersistence().createOrUpdate(this);
	}

	public Money getTotalOutStandingLoanAmount() throws PersistenceException,
			ServiceException {
		Money amount = getOutstandingLoanAmount();
		List<CustomerBO> clients = new CustomerPersistence().getAllChildrenForParent(
				getSearchId(), getOffice().getOfficeId(),
				CustomerConstants.GROUP_LEVEL_ID);
		if (clients != null && !clients.isEmpty()) {
			for (CustomerBO client : clients) {
				amount = amount.add(client.getOutstandingLoanAmount());
			}
		}
		return amount;
	}

	public Money getAverageLoanAmount() throws PersistenceException,
			ServiceException {
		Money amountForActiveAccount = new Money();
		Integer countOfActiveLoans = 0;
		List<CustomerBO> clients = new CustomerPersistence().getAllChildrenForParent(
				getSearchId(), getOffice().getOfficeId(),
				CustomerConstants.GROUP_LEVEL_ID);
		if (clients != null && !clients.isEmpty()) {
			for (CustomerBO client : clients) {
				amountForActiveAccount = amountForActiveAccount.add(client
						.getOutstandingLoanAmount());
				countOfActiveLoans += client.getActiveLoanCounts();
			}
		}
		if (countOfActiveLoans.intValue() > 0)
			return new Money(String.valueOf(amountForActiveAccount
					.getAmountDoubleValue()
					/ countOfActiveLoans.intValue()));
		return new Money();
	}

	public Money getTotalSavingsBalance() throws PersistenceException,
			ServiceException {
		Money amount = getSavingsBalance();
		List<CustomerBO> clients = new CustomerPersistence().getAllChildrenForParent(
				getSearchId(), getOffice().getOfficeId(),
				CustomerConstants.GROUP_LEVEL_ID);
		if (clients != null && !clients.isEmpty()) {
			for (CustomerBO client : clients) {
				amount = amount.add(client.getSavingsBalance());
			}
		}
		return amount;
	}

	public Integer getActiveOnHoldChildrenOfGroup()
			throws PersistenceException, ServiceException {
		List<CustomerBO> clients = new CustomerPersistence().getAllChildrenForParent(
				getSearchId(), getOffice().getOfficeId(),
				CustomerConstants.GROUP_LEVEL_ID);
		if (clients != null && !clients.isEmpty()) {
			return Integer.valueOf(clients.size());
		}
		return Integer.valueOf(0);
	}
	
	@Override
	protected void validateStatusChange(Short newStatusId) throws CustomerException{
		
	}
	
	@Override
	protected boolean checkNewStatusIsFirstTimeActive(Short oldStatus,
			Short newStatusId) {
		return false;
	}
}
