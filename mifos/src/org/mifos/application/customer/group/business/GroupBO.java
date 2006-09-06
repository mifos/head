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

import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.configuration.exceptions.ConfigurationException;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerHierarchyEntity;
import org.mifos.application.customer.business.CustomerPositionView;
import org.mifos.application.customer.business.CustomerStatusEntity;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.group.persistence.GroupPersistence;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.ChildrenStateType;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;

/**
 * This class denotes the Group (row in customer table) object and all
 * attributes associated with it. It has a composition of other objects like
 * Custom fields, fees, personnel etc., since it inherits from Customer
 */
public class GroupBO extends CustomerBO {

	private GroupPerformanceHistoryEntity performanceHistory;

	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.GROUP_LOGGER);

	protected GroupBO() {
		super();
	}

	public GroupBO(UserContext userContext, String displayName,
			CustomerStatus customerStatus, String externalId, boolean trained,
			Date trainedDate, Address address,
			List<CustomFieldView> customFields, List<FeeView> fees,
			Short formedById, CustomerBO parentCustomer)
			throws CustomerException {
		this(userContext, displayName, customerStatus, externalId, trained,
				trainedDate, address, customFields, fees, formedById, null,
				parentCustomer, null, null);
		validateFieldsForGroupUnderCenter(parentCustomer);
		setValues(trained, trainedDate);
	}

	public GroupBO(UserContext userContext, String displayName,
			CustomerStatus customerStatus, String externalId, boolean trained,
			Date trainedDate, Address address,
			List<CustomFieldView> customFields, List<FeeView> fees,
			Short formedById, Short officeId, MeetingBO meeting,
			Short loanOfficerId) throws CustomerException {
		this(userContext, displayName, customerStatus, externalId, trained,
				trainedDate, address, customFields, fees, formedById, officeId,
				null, meeting, loanOfficerId);
		validateFieldsForGroupUnderOffice(loanOfficerId, meeting, officeId);
		setValues(trained, trainedDate);
	}

	private GroupBO(UserContext userContext, String displayName,
			CustomerStatus customerStatus, String externalId, boolean trained,
			Date trainedDate, Address address,
			List<CustomFieldView> customFields, List<FeeView> fees,
			Short formedById, Short officeId, CustomerBO parentCustomer,
			MeetingBO meeting, Short loanOfficerId) throws CustomerException {
		super(userContext, displayName, CustomerLevel.GROUP, customerStatus,
				externalId, null, address, customFields, fees, formedById,
				officeId, parentCustomer, meeting, loanOfficerId);
		validateFields(displayName, formedById, trained, trainedDate);
	}

	@Override
	public boolean isActive() {
		return getCustomerStatus().getId().equals(
				CustomerStatus.GROUP_ACTIVE.getValue());
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
	public void save() throws CustomerException {
		super.save();
		try {
			if (this.getParentCustomer() != null)
				new CustomerPersistence().createOrUpdate(this
						.getParentCustomer());
		} catch (PersistenceException pe) {
			throw new CustomerException(
					CustomerConstants.CREATE_FAILED_EXCEPTION, pe);
		}
	}

	public void generatePortfolioAtRisk() throws PersistenceException,
			ServiceException {
		Money amount = getBalanceForAccountsAtRisk();
		List<CustomerBO> clients = new CustomerPersistence()
				.getAllChildrenForParent(getSearchId(), getOffice()
						.getOfficeId(), CustomerConstants.GROUP_LEVEL_ID);
		if (clients != null) {
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
		List<CustomerBO> clients = new CustomerPersistence()
				.getAllChildrenForParent(getSearchId(), getOffice()
						.getOfficeId(), CustomerConstants.GROUP_LEVEL_ID);
		if (clients != null) {
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
		List<CustomerBO> clients = new CustomerPersistence()
				.getAllChildrenForParent(getSearchId(), getOffice()
						.getOfficeId(), CustomerConstants.GROUP_LEVEL_ID);
		if (clients != null) {
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
		List<CustomerBO> clients = new CustomerPersistence()
				.getAllChildrenForParent(getSearchId(), getOffice()
						.getOfficeId(), CustomerConstants.GROUP_LEVEL_ID);
		if (clients != null) {
			for (CustomerBO client : clients) {
				amount = amount.add(client.getSavingsBalance());
			}
		}
		return amount;
	}

	public Integer getActiveOnHoldChildrenOfGroup()
			throws PersistenceException, ServiceException {
		List<CustomerBO> clients = new CustomerPersistence()
				.getAllChildrenForParent(getSearchId(), getOffice()
						.getOfficeId(), CustomerConstants.GROUP_LEVEL_ID);
		if (clients != null) {
			return Integer.valueOf(clients.size());
		}
		return Integer.valueOf(0);
	}

	public void update(UserContext userContext, String displayName,
			Short loanOfficerId, String externalId, Short trained,
			Date trainedDate, Address address,
			List<CustomFieldView> customFields,
			List<CustomerPositionView> customerPositions)
			throws CustomerException {
		validateFieldsForUpdate(displayName, loanOfficerId);
		if (trained != null)
			setTrained(trained);
		else
			setTrained(YesNoFlag.NO.getValue());
		setTrainedDate(trainedDate);
		if (!Configuration.getInstance().getCustomerConfig(
				userContext.getBranchId()).isCenterHierarchyExists()) {
			updateLoanOfficer(loanOfficerId);
		}
		setDisplayName(displayName);
		super.update(userContext, externalId, address, customFields,
				customerPositions);
	}

	public void transferToBranch(OfficeBO officeToTransfer)throws CustomerException{
		validateNewOffice(officeToTransfer);
		logger.debug("In GroupBO::transferToBranch(), transfering customerId: " + getCustomerId() +  "to branch : "+ officeToTransfer.getOfficeId());
		validateForDuplicateName(getDisplayName(), officeToTransfer.getOfficeId());
		
		if(isActive())
			setCustomerStatus(new CustomerStatusEntity(CustomerStatus.GROUP_HOLD));
		
		makeCustomerMovementEntries(officeToTransfer);
		this.setPersonnel(null);
		this.setSearchId(generateSearchId());
		super.update();
		
		for(CustomerBO client: getChildren()){
			client.setUserContext(getUserContext());
			((ClientBO)client).handleGroupTransfer();
		}
		logger.debug("In GroupBO::transferToBranch(), successfully transfered, customerId :" + getCustomerId());		
	}
	
	public void transferToCenter(CenterBO newParent)throws CustomerException{
		validateNewCenter(newParent);
		logger.debug("In GroupBO::transferToCenter(), transfering customerId: " + getCustomerId() +  "to Center Id : "+ newParent.getCustomerId());

		validateForActiveAccounts();
		
		if(!isSameBranch(newParent.getOffice())){
			makeCustomerMovementEntries(newParent.getOffice());
			if(isActive())
				setCustomerStatus(new CustomerStatusEntity(CustomerStatus.GROUP_HOLD));
		}
		
		changeParentCustomer(newParent);
		CustomerHierarchyEntity currentHierarchy = getActiveCustomerHierarchy();
		currentHierarchy.makeInActive(userContext.getId());
		
		this.addCustomerHierarchy(new CustomerHierarchyEntity(this,newParent));		
		super.update();
		
		for(CustomerBO client: getChildren()){
			client.setUserContext(getUserContext());
			((ClientBO)client).handleGroupTransfer();
		}
	}
	
	@Override
	protected void validateStatusChange(Short newStatusId)
			throws CustomerException {
		logger.debug("In GroupBO::validateStatusChange(), customerId: "
				+ getCustomerId());
		if (newStatusId.equals(CustomerStatus.GROUP_CLOSED.getValue()))
			checkIfGroupCanBeClosed();
		if (newStatusId.equals(CustomerStatus.GROUP_CANCELLED.getValue()))
			checkIfGroupCanBeCancelled();
		if (newStatusId.equals(CustomerStatus.GROUP_ACTIVE.getValue()))
			checkIfGroupCanBeActive(newStatusId);
		if (getCustomerStatus().getId().equals(
				CustomerStatus.GROUP_CANCELLED.getValue())
				&& newStatusId.equals(CustomerStatus.GROUP_PARTIAL.getValue())) {
			handleValidationsForCancelToPartial();
		}
		logger
				.debug("In ClientBO::validateStatusChange(), successfully validated status, customerId: "
						+ getCustomerId());
	}

	@Override
	protected boolean checkNewStatusIsFirstTimeActive(Short oldStatus,
			Short newStatusId) {
		if ((oldStatus.equals(CustomerStatus.GROUP_PARTIAL.getValue()) || oldStatus
				.equals(CustomerStatus.GROUP_PENDING.getValue()))
				&& newStatusId.equals(CustomerStatus.GROUP_ACTIVE.getValue())) {
			this.setCustomerActivationDate(new Date());
			return true;
		}
		return false;
	}

	protected void validateFieldsForUpdate(String displayName,
			Short loanOfficerId) throws CustomerException {
		if (getCustomerStatus().getId().equals(
				CustomerStatus.GROUP_ACTIVE.getValue())
				|| getCustomerStatus().getId().equals(
						CustomerStatus.GROUP_HOLD.getValue())) {
			validateLO(loanOfficerId);
		}
		if (!getDisplayName().equals(displayName))
			validateForDuplicateName(displayName, getOffice().getOfficeId());

	}
	
	private void validateNewCenter(CenterBO toCenter)throws CustomerException{
		if (toCenter == null)
			throw new CustomerException(CustomerConstants.INVALID_PARENT);
		
		if(isSameCenter(toCenter))
			throw new CustomerException(CustomerConstants.ERRORS_SAME_PARENT_TRANSFER);		
	}
	
	private boolean isSameCenter(CenterBO center){
		return getParentCustomer().getCustomerId().equals(center.getCustomerId());
	}
	
	private void validateForActiveAccounts()throws CustomerException{
		if(this.isAnyLoanAccountOpen() || this.isAnySavingsAccountOpen())
			throw new CustomerException(CustomerConstants.ERRORS_HAS_ACTIVE_ACCOUNT);
		for(CustomerBO client: getChildren())
			if(client.isAnyLoanAccountOpen() || client.isAnySavingsAccountOpen())
				throw new CustomerException(CustomerConstants.ERRORS_CHILDREN_HAS_ACTIVE_ACCOUNT);		
	}
	
	private void validateNewOffice(OfficeBO officeToTransfer)throws CustomerException{
		if (officeToTransfer == null)
			throw new CustomerException(CustomerConstants.INVALID_OFFICE);
		
		if(isSameBranch(officeToTransfer))
			throw new CustomerException(CustomerConstants.ERRORS_SAME_BRANCH_TRANSFER);
	}
	
	private void checkIfGroupCanBeActive(Short groupStatusId)
			throws CustomerException {
		if (getParentCustomer() == null
				|| getParentCustomer().getCustomerId() == null) {
			if (getPersonnel() == null
					|| getPersonnel().getPersonnelId() == null) {
				throw new CustomerException(
						GroupConstants.GROUP_LOANOFFICER_NOT_ASSIGNED);
			}
			if (getCustomerMeeting() == null
					|| getCustomerMeeting().getMeeting() == null) {
				throw new CustomerException(GroupConstants.MEETING_NOT_ASSIGNED);
			}
		}
	}

	private void handleValidationsForCancelToPartial() throws CustomerException {
		if (getParentCustomer() != null
				&& getParentCustomer().getCustomerId() != null) {
			checkGroupCanBeChangedFromCancelToPartialIfCenterIsActive();
		} else {
			checkGroupCanBeChangedFromCancelToPartialIfOfficeIsActive();
			if (getPersonnel() != null
					&& getPersonnel().getPersonnelId() != null) {
				checkGroupCanBeChangedFromCancelToPartialIfPersonnelActive();
			}
		}
	}

	private void checkGroupCanBeChangedFromCancelToPartialIfCenterIsActive()
			throws CustomerException {
		if (!getParentCustomer().isActive()) {
			try {
				throw new CustomerException(
						GroupConstants.CENTER_INACTIVE,
						new Object[] { MifosConfiguration.getInstance()
								.getLabel(ConfigurationConstants.GROUP,
										getUserContext().getPereferedLocale()) });
			} catch (ConfigurationException ce) {
				throw new CustomerException(ce);
			}
		}
	}

	private void checkGroupCanBeChangedFromCancelToPartialIfOfficeIsActive()
			throws CustomerException {
		if (new OfficePersistence().isBranchInactive(getOffice().getOfficeId())) {
			try {
				throw new CustomerException(
						GroupConstants.BRANCH_INACTIVE,
						new Object[] { MifosConfiguration.getInstance()
								.getLabel(ConfigurationConstants.GROUP,
										getUserContext().getPereferedLocale()) });
			} catch (ConfigurationException ce) {
				throw new CustomerException(ce);
			}
		}
	}

	private void checkGroupCanBeChangedFromCancelToPartialIfPersonnelActive()
			throws CustomerException {
		if (new OfficePersistence()
				.hasActivePeronnel(getOffice().getOfficeId())) {
			try {
				throw new CustomerException(
						GroupConstants.LOANOFFICER_INACTIVE,
						new Object[] { MifosConfiguration.getInstance()
								.getLabel(ConfigurationConstants.BRANCHOFFICE,
										getUserContext().getPereferedLocale()) });
			} catch (ConfigurationException ce) {
				throw new CustomerException(ce);
			}
		}
	}

	private void checkIfGroupCanBeCancelled() throws CustomerException {
		try {
			if (getChildren(CustomerLevel.CLIENT, ChildrenStateType.OTHER_THAN_CANCELLED_AND_CLOSED)
					.size() > 0)
				throw new CustomerException(
						GroupConstants.GROUP_CLIENTS_ARE_ACTIVE,
						new Object[] { MifosConfiguration.getInstance()
								.getLabel(ConfigurationConstants.GROUP,
										getUserContext().getPereferedLocale()) });
		} catch (ConfigurationException ce) {
			throw new CustomerException(ce);
		}
	}

	private void checkIfGroupCanBeClosed() throws CustomerException {
		if (getActiveLoanAccounts().size() > 0
				|| getActiveSavingsAccounts().size() > 0) {
			throw new CustomerException(
					CustomerConstants.CUSTOMER_HAS_ACTIVE_ACCOUNTS_EXCEPTION);
		}
		try {
			if (getChildren(CustomerLevel.CLIENT, ChildrenStateType.OTHER_THAN_CANCELLED_AND_CLOSED)
					.size() > 0)
				throw new CustomerException(
						CustomerConstants.ERROR_STATE_CHANGE_EXCEPTION,
						new Object[] { MifosConfiguration.getInstance()
								.getLabel(
										ConfigurationConstants.CLIENT,
										this.getUserContext()
												.getPereferedLocale()) });
		} catch (ConfigurationException ce) {
			throw new CustomerException(ce);
		}
	}

	private String generateSearchId() throws CustomerException {
		String searchId = null;
		if (getParentCustomer() != null) {
			getParentCustomer().incrementChildCount();
			searchId = getParentCustomer().getSearchId() + "."
					+ getParentCustomer().getMaxChildCount();
		} else {
			try {
				int customerCount = new CustomerPersistence()
						.getCustomerCountForOffice(CustomerLevel.GROUP,
								getOffice().getOfficeId());
				searchId = GroupConstants.PREFIX_SEARCH_STRING
						+ String.valueOf(customerCount + 1);
			} catch (PersistenceException pe) {
				throw new CustomerException(pe);
			}
		}
		return searchId;
	}

	private void validateFields(String displayName, Short formedBy,
			boolean trained, Date trainedDate) throws CustomerException {
		validateFormedBy(formedBy);
		if ((trained && trainedDate == null)
				|| (!trained && trainedDate != null))
			throw new CustomerException(
					CustomerConstants.INVALID_TRAINED_OR_TRAINEDDATE);
		if(getOffice()!=null)
			validateForDuplicateName(displayName, getOffice().getOfficeId());
	}

	private void validateFormedBy(Short formedBy) throws CustomerException {
		if (formedBy == null)
			throw new CustomerException(CustomerConstants.INVALID_FORMED_BY);

	}

	private void validateForDuplicateName(String displayName, Short officeId)
			throws CustomerException {
		if (new GroupPersistence().isGroupExists(displayName, officeId))
			throw new CustomerException(
					CustomerConstants.ERRORS_DUPLICATE_CUSTOMER);
	}

	private void validateFieldsForGroupUnderCenter(CustomerBO parentCustomer)
			throws CustomerException {
		if (parentCustomer == null)
			throw new CustomerException(CustomerConstants.INVALID_PARENT);
	}

	private void validateFieldsForGroupUnderOffice(Short loanOfficerId,
			MeetingBO meeting, Short officeId) throws CustomerException {
		validateOffice(officeId);
		if (isActive()) {
			validateLO(loanOfficerId);
			validateMeeting(meeting);
		}
	}

	private void setValues(boolean trained, Date trainedDate)
			throws CustomerException {
		this.setSearchId(generateSearchId());
		this.setTrained(trained);
		if (trained)
			this.setTrainedDate(trainedDate);
		if (getStatus().equals(CustomerStatus.GROUP_ACTIVE))
			this.setCustomerActivationDate(this.getCreatedDate());
	}

	
}
