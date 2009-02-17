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

package org.mifos.application.customer.group.business;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.savings.persistence.SavingsPersistence;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerHierarchyEntity;
import org.mifos.application.customer.business.CustomerLevelEntity;
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
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.productdefinition.persistence.SavingsPrdPersistence;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;

/**
 * This class denotes the Group (row in customer table) object and all
 * attributes associated with it. It has a composition of other objects like
 * Custom fields, fees, personnel etc., since it inherits from Customer
 */
public class GroupBO extends CustomerBO {

	private GroupPerformanceHistoryEntity groupPerformanceHistory;

	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.GROUP_LOGGER);

	
	public GroupBO(UserContext userContext, String displayName,
			CustomerStatus customerStatus, String externalId, boolean trained,
			Date trainedDate, Address address,
			List<CustomFieldView> customFields, List<FeeView> fees,
			PersonnelBO formedBy, CustomerBO parentCustomer)
			throws CustomerException {
		this(userContext, displayName, customerStatus, externalId, trained,
				trainedDate, address, customFields, fees, formedBy, null,
				parentCustomer, null, null);
		validateFieldsForGroupUnderCenter(parentCustomer);
		setValues(trained, trainedDate);
		this.groupPerformanceHistory = new GroupPerformanceHistoryEntity(this);
	}

	public GroupBO(UserContext userContext, String displayName,
			CustomerStatus customerStatus, String externalId, boolean trained,
			Date trainedDate, Address address,
			List<CustomFieldView> customFields, List<FeeView> fees,
			PersonnelBO formedBy, OfficeBO office, MeetingBO meeting,
			PersonnelBO loanOfficer) throws CustomerException {
		this(userContext, displayName, customerStatus, externalId, trained,
				trainedDate, address, customFields, fees, formedBy, office,
				null, meeting, loanOfficer);
		validateFieldsForGroupUnderOffice(loanOfficer, meeting, office);
		setValues(trained, trainedDate);
		this.groupPerformanceHistory = new GroupPerformanceHistoryEntity(this);
	}

	protected GroupBO() {
		super();
		this.groupPerformanceHistory = null;
	}
	
	private GroupBO(UserContext userContext, String displayName,
			CustomerStatus customerStatus, String externalId, boolean trained,
			Date trainedDate, Address address,
			List<CustomFieldView> customFields, List<FeeView> fees,
			PersonnelBO formedBy, OfficeBO office, CustomerBO parentCustomer,
			MeetingBO meeting, PersonnelBO loanOfficer) throws CustomerException {
		super(userContext, displayName, CustomerLevel.GROUP, customerStatus,
				externalId, null, address, customFields, fees, formedBy,
				office, parentCustomer, meeting, loanOfficer);
		validateFields(displayName, formedBy, trained, trainedDate);
	}

	@Override
	public boolean isActive() {
		return getStatus() == CustomerStatus.GROUP_ACTIVE;
	}

	public GroupPerformanceHistoryEntity getGroupPerformanceHistory() {
		return groupPerformanceHistory;
	}

	public void setGroupPerformanceHistory(
			GroupPerformanceHistoryEntity groupPerformanceHistory) {
		if (groupPerformanceHistory != null)
			groupPerformanceHistory.setGroup(this);
		this.groupPerformanceHistory = groupPerformanceHistory;
	}

    // NOTE: Injected Persistence
	public void update(UserContext userContext, String displayName,
			Short loanOfficerId, String externalId, Short trained,
			Date trainedDate, Address address,
			List<CustomFieldView> customFields,
			List<CustomerPositionView> customerPositions,
			CustomerPersistence customerPersistence, 
			PersonnelPersistence personnelPersistence)
			throws Exception {
		validateFieldsForUpdate(displayName, loanOfficerId);
		if (trained != null)
			setTrained(trained);
		else
			setTrained(YesNoFlag.NO.getValue());
		setTrainedDate(trainedDate);
		updateLoanOfficer(loanOfficerId, customerPersistence, personnelPersistence);
		setDisplayName(displayName);
		super.update(userContext, externalId, address, customFields,
				customerPositions, customerPersistence);
	}

    // NOTE: Injected Persistence
	public void transferToBranch(OfficeBO officeToTransfer, CustomerPersistence customerPersistence)throws CustomerException{
		validateNewOffice(officeToTransfer);
		logger.debug("In GroupBO::transferToBranch(), transfering customerId: " + getCustomerId() +  "to branch : "+ officeToTransfer.getOfficeId());
		validateForDuplicateName(getDisplayName(), officeToTransfer.getOfficeId());
		
		if(isActive())
			setCustomerStatus(new CustomerStatusEntity(CustomerStatus.GROUP_HOLD));
		
		makeCustomerMovementEntries(officeToTransfer);
		setPersonnel(null);
		setSearchId(generateSearchId());
		update(customerPersistence);
		if(getChildren()!=null){
			for(CustomerBO client: getChildren()){
				client.setUserContext(getUserContext());
				((ClientBO)client).handleGroupTransfer(customerPersistence);
			}
		}
		logger.debug("In GroupBO::transferToBranch(), successfully transfered, customerId :" + getCustomerId());		
	}
	
    // NOTE: Injected Persistence
	public void transferToCenter(CenterBO newParent, CustomerPersistence customerPersistence)throws CustomerException{
		validateNewCenter(newParent);
		logger.debug("In GroupBO::transferToCenter(), transfering customerId: " + getCustomerId() +  "to Center Id : "+ newParent.getCustomerId());

		validateForActiveAccounts();
		
		if(!isSameBranch(newParent.getOffice())){
			makeCustomerMovementEntries(newParent.getOffice());
			if(isActive())
				setCustomerStatus(new CustomerStatusEntity(CustomerStatus.GROUP_HOLD));
		}
		
		changeParentCustomer(newParent, customerPersistence);
		makeInactive(newParent);
		
		addCustomerHierarchy(new CustomerHierarchyEntity(this,newParent));		
		update(customerPersistence);
		if(getChildren()!=null){
			for(CustomerBO client: getChildren()){
				client.setUserContext(getUserContext());
				((ClientBO)client).handleGroupTransfer(customerPersistence);
			}
		}
	}
	
    // NOTE: Injected Persistence
	@Override
	protected void saveUpdatedMeeting(MeetingBO meeting,
	        CustomerPersistence customerPersistence) throws CustomerException{
		logger.debug("In GroupBO::saveUpdatedMeeting(), customerId: "
				+ getCustomerId());
		MeetingBO newMeeting = getCustomerMeeting().getUpdatedMeeting();
		super.saveUpdatedMeeting(meeting, customerPersistence);
		if(getParentCustomer()==null)
			deleteMeeting(newMeeting, customerPersistence);
	}
	
    // NOTE: Injected Persistence
	@Override
	public void updateMeeting(MeetingBO meeting,
	        CustomerPersistence customerPersistence) throws CustomerException{
		logger.debug("In GroupBO::updateMeeting(), customerId: "
				+ getCustomerId());
		if(getParentCustomer()==null){			
			if(getCustomerMeeting()==null){
				this.setCustomerMeeting(createCustomerMeeting(meeting));
				updateMeetingForClients(meeting, customerPersistence);
			}
			else
				saveUpdatedMeeting(meeting, customerPersistence);
		}else
			saveUpdatedMeeting(meeting, customerPersistence);
		update(customerPersistence);
	}
	
    // NOTE: Injected Persistence
	private void updateMeetingForClients(MeetingBO meeting,
	        CustomerPersistence customerPersistence) throws CustomerException{
		Set<CustomerBO> clients = getChildren();			
		if(clients!=null){
			for(CustomerBO client : clients){
				client.setUserContext(getUserContext());
				client.updateMeeting(meeting, customerPersistence);
			}
		}
	}
	
    // NOTE: Injected Persistence
	@Override
	public void changeStatus(Short newStatusId, Short flagId, String comment, 
	        CustomerPersistence customerPersistence, PersonnelPersistence personnelPersistence,
	        MasterPersistence masterPersistence, SavingsPersistence savingsPersistence,
	        SavingsPrdPersistence savingsPrdPersistence, OfficePersistence officePersistence)
	throws CustomerException {
		Short oldStatusId = getCustomerStatus().getId();
		super.changeStatus(newStatusId, flagId, comment, customerPersistence, personnelPersistence,
		        masterPersistence, savingsPersistence, savingsPrdPersistence, officePersistence);
		if(oldStatusId.equals(CustomerStatus.GROUP_PENDING.getValue()) && newStatusId.equals(CustomerStatus.GROUP_CANCELLED.getValue()) && getChildren()!=null){
			for(CustomerBO client: getChildren()){
				if(client.getCustomerStatus().getId().equals(CustomerStatus.CLIENT_PENDING.getValue())){
					client.setUserContext(getUserContext());
					client.changeStatus(CustomerStatus.CLIENT_PARTIAL.getValue(), null, comment,
					        customerPersistence, personnelPersistence, masterPersistence, savingsPersistence,
					        savingsPrdPersistence, officePersistence);
				}
			}
		}
	}
	
    // NOTE: Injected Persistence
	@Override
	protected void validateStatusChange(Short newStatusId,
	        CustomerPersistence customerPersistence, OfficePersistence officePersistence)
			throws CustomerException {
		logger.debug("In GroupBO::validateStatusChange(), customerId: "
				+ getCustomerId());
		if (newStatusId.equals(CustomerStatus.GROUP_CLOSED.getValue()))
			checkIfGroupCanBeClosed(customerPersistence);
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
	protected boolean isActiveForFirstTime(Short oldStatus,
			Short newStatusId) {
		return ((oldStatus.equals(CustomerStatus.GROUP_PARTIAL.getValue()) || oldStatus
				.equals(CustomerStatus.GROUP_PENDING.getValue()))
				&& newStatusId.equals(CustomerStatus.GROUP_ACTIVE.getValue()));
	}

    // NOTE: Injected Persistence
	@Override
	protected void handleActiveForFirstTime(Short oldStatusId, Short newStatusId,
	        CustomerPersistence customerPersistence, SavingsPersistence savingsPersistence,
	        SavingsPrdPersistence savingsPrdPersistence) throws CustomerException{
		super.handleActiveForFirstTime(oldStatusId, newStatusId, customerPersistence, savingsPersistence,
		        savingsPrdPersistence);
		if (isActiveForFirstTime(oldStatusId, newStatusId))
			this.setCustomerActivationDate(new Date());
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
		
		if(!toCenter.isActive())
			throw new CustomerException(CustomerConstants.ERRORS_INTRANSFER_PARENT_INACTIVE);
		
		validateMeetingRecurrenceForTransfer(getCustomerMeeting().getMeeting(), toCenter.getCustomerMeeting().getMeeting());
	}
	
	

	private boolean isSameCenter(CenterBO center){
		return getParentCustomer().getCustomerId().equals(center.getCustomerId());
	}
	
	private void validateForActiveAccounts()throws CustomerException{
		if(this.isAnyLoanAccountOpen() || this.isAnySavingsAccountOpen())
			throw new CustomerException(CustomerConstants.ERRORS_HAS_ACTIVE_ACCOUNT);
		if(getChildren()!=null){
			for(CustomerBO client: getChildren())
				if(client.isAnyLoanAccountOpen() || client.isAnySavingsAccountOpen())
					throw new CustomerException(CustomerConstants.ERRORS_CHILDREN_HAS_ACTIVE_ACCOUNT);
		}
	}
	
	private void validateNewOffice(OfficeBO officeToTransfer)throws CustomerException{
		if (officeToTransfer == null)
			throw new CustomerException(CustomerConstants.INVALID_OFFICE);
		
		if(isSameBranch(officeToTransfer))
			throw new CustomerException(CustomerConstants.ERRORS_SAME_BRANCH_TRANSFER);
		
		if(!officeToTransfer.isActive())
			throw new CustomerException(CustomerConstants.ERRORS_TRANSFER_IN_INACTIVE_OFFICE);
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
			throw new CustomerException(
					GroupConstants.CENTER_INACTIVE,
					new Object[] { MessageLookup.getInstance().lookupLabel(
							ConfigurationConstants.CENTER,
							getUserContext()) });
		}
	}

	private void checkGroupCanBeChangedFromCancelToPartialIfOfficeIsActive()
	throws CustomerException {
		try {
			if (new OfficePersistence().isBranchInactive(getOffice().getOfficeId())) {
				throw new CustomerException(
						GroupConstants.BRANCH_INACTIVE,
						new Object[] { MessageLookup.getInstance().lookupLabel(ConfigurationConstants.GROUP,
								getUserContext()) });
			}
		} catch (PersistenceException e) {
			throw new CustomerException(e);
		} 
	}

	private void checkGroupCanBeChangedFromCancelToPartialIfPersonnelActive()
	throws CustomerException {
		try {
			if (new OfficePersistence()
			.hasActivePeronnel(getOffice().getOfficeId())) {
				throw new CustomerException(
						GroupConstants.LOANOFFICER_INACTIVE,
						new Object[] { MessageLookup.getInstance().lookup(
								ConfigurationConstants.BRANCHOFFICE,
								getUserContext()) });
			}
		} catch (PersistenceException e) {
			throw new CustomerException(e);
		}
	}

    // NOTE: Injected Persistence
	private void checkIfGroupCanBeClosed(
	        CustomerPersistence customerPersistence) throws CustomerException {
		if (isAnyLoanAccountOpen() || isAnySavingsAccountOpen()) {
			throw new CustomerException(
					CustomerConstants.CUSTOMER_HAS_ACTIVE_ACCOUNTS_EXCEPTION);
		}
		if (getChildren(CustomerLevel.CLIENT,
		        ChildrenStateType.OTHER_THAN_CANCELLED_AND_CLOSED,
		        customerPersistence)
				.size() > 0)
			throw new CustomerException(
					CustomerConstants.ERROR_STATE_CHANGE_EXCEPTION,
					new Object[] { MessageLookup.getInstance().lookupLabel(
							ConfigurationConstants.CLIENT,
							this.getUserContext()) });
	}

	private String generateSearchId() throws CustomerException {
		String searchId = null;
		if (getParentCustomer() != null) {
			childAddedForParent(getParentCustomer());
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

	private void validateFields(String displayName, PersonnelBO formedBy,
			boolean trained, Date trainedDate) throws CustomerException {
		validateFormedBy(formedBy);
		if ((trained && trainedDate == null)
				|| (!trained && trainedDate != null))
			throw new CustomerException(
					CustomerConstants.INVALID_TRAINED_OR_TRAINEDDATE);
		if(getOffice()!=null)
			validateForDuplicateName(displayName, getOffice().getOfficeId());
	}

	private void validateFormedBy(PersonnelBO formedBy) throws CustomerException {
		if (formedBy == null)
			throw new CustomerException(CustomerConstants.INVALID_FORMED_BY);

	}

	private void validateForDuplicateName(String displayName, Short officeId)
			throws CustomerException {
		try {
			if (new GroupPersistence().isGroupExists(displayName, officeId))
				throw new CustomerException(
						CustomerConstants.ERRORS_DUPLICATE_CUSTOMER);
		} catch (PersistenceException e) {
			throw new CustomerException(e);
		}
	}

	private void validateFieldsForGroupUnderCenter(CustomerBO parentCustomer)
			throws CustomerException {
		if (parentCustomer == null)
			throw new CustomerException(CustomerConstants.INVALID_PARENT);
	}

	private void validateFieldsForGroupUnderOffice(PersonnelBO loanOfficer,
			MeetingBO meeting, OfficeBO office) throws CustomerException {
		validateOffice(office);
		if (isActive()) {
			validateLO(loanOfficer);
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
	
	public void checkIfGroupHasActiveLoan() throws CustomerException {
		if (isAnyLoanAccountOpen()) {
			throw new CustomerException(
					CustomerConstants.GROUP_HAS_ACTIVE_ACCOUNTS_EXCEPTION);
		}
	}

	@Override
	public void updatePerformanceHistoryOnDisbursement(LoanBO loan, Money disburseAmount) throws CustomerException {
		try {
			((GroupPerformanceHistoryEntity) getPerformanceHistory())
					.updateOnDisbursement(loan, disburseAmount);
		}catch (AccountException e) {
			throw new CustomerException(e);
		}
	}

	@Override
	public void updatePerformanceHistoryOnWriteOff(LoanBO loan) throws CustomerException {
		GroupPerformanceHistoryEntity performanceHistory = (GroupPerformanceHistoryEntity) getPerformanceHistory();
		try {
			performanceHistory.updateOnWriteOff(loan);
		}catch (AccountException e) {
			throw new CustomerException(e);
		}
	}

	@Override
	public void updatePerformanceHistoryOnReversal(LoanBO loan,
			Money lastLoanAmount) throws CustomerException {
		try {
			GroupPerformanceHistoryEntity groupPerformanceHistoryEntity = (GroupPerformanceHistoryEntity) getPerformanceHistory();
			groupPerformanceHistoryEntity
					.updateOnReversal(loan, lastLoanAmount);
		}
		catch (AccountException e) {
			throw new CustomerException(e);
		}
	}

	@Override
	public void updatePerformanceHistoryOnRepayment(LoanBO loan, Money totalAmount) throws CustomerException {
		GroupPerformanceHistoryEntity performanceHistory = (GroupPerformanceHistoryEntity) getPerformanceHistory();
		try {
			performanceHistory.updateOnRepayment(loan, totalAmount);
		}
		catch (AccountException e) {
			throw new CustomerException(e);
		}
	}	
	
	
	@Override
	public void updatePerformanceHistoryOnLastInstlPayment(LoanBO loan, Money totalAmount) throws CustomerException {
		updatePerformanceHistoryOnRepayment(loan, totalAmount);
	}
	
    // NOTE: Injected Persistence
	@Override
	public void update(CustomerPersistence customerPersistence) throws CustomerException {
		try {
			setUpdateDetails();
			customerPersistence.createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new CustomerException(
					CustomerConstants.UPDATE_FAILED_EXCEPTION, e);
		}
	}
}
