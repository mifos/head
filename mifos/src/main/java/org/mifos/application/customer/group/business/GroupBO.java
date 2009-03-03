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

import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerHierarchyEntity;
import org.mifos.application.customer.business.CustomerPositionView;
import org.mifos.application.customer.business.CustomerStatusEntity;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.group.persistence.GroupPersistence;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.util.helpers.ChildrenStateType;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.Money;

/**
 * This class denotes the Group (row in customer table) object and all
 * attributes associated with it. It has a composition of other objects like
 * Custom fields, fees, personnel etc., since it inherits from Customer
 */
public class GroupBO extends CustomerBO {

	private GroupPerformanceHistoryEntity groupPerformanceHistory;

    /*
     * Injected Persistence classes
     * 
     * DO NOT ACCESS THESE MEMBERS DIRECTLY!  ALWAYS USE THE GETTER!
     * 
     * The Persistence classes below are used by this class
     * and can be injected via a setter for testing purposes.
     * In order for this mechanism to work correctly, the getter
     * must be used to access them because the getter will 
     * initialize the Persistence class if it has not been injected.
     * 
     * Long term these references to Persistence classes should 
     * probably be eliminated. 
     */
	private GroupPersistence groupPersistence = null;
	private OfficePersistence officePersistence = null;

	public GroupPersistence getGroupPersistence() {
	    if (null == groupPersistence) {
	        groupPersistence = new GroupPersistence();
	    }
        return groupPersistence;
    }

    public void setGroupPersistence(GroupPersistence groupPersistence) {
        this.groupPersistence = groupPersistence;
    }
    
    public OfficePersistence getOfficePersistence() {
        if (null == officePersistence) {
            officePersistence = new OfficePersistence();
        }
        return officePersistence;
    }

    public void setOfficePersistence(OfficePersistence officePersistence) {
        this.officePersistence = officePersistence;
    }


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

	public void update(UserContext userContext, String displayName,
			Short loanOfficerId, String externalId, Short trained,
			Date trainedDate, Address address,
			List<CustomFieldView> customFields,
			List<CustomerPositionView> customerPositions)
			throws Exception {
		validateFieldsForUpdate(displayName, loanOfficerId);
		if (trained != null)
			setTrained(trained);
		else
			setTrained(YesNoFlag.NO.getValue());
		setTrainedDate(trainedDate);
		updateLoanOfficer(loanOfficerId);
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
		setPersonnel(null);
		setSearchId(generateSearchId());
		update();
		if(getChildren()!=null){
			for(CustomerBO client: getChildren()){
				client.setUserContext(getUserContext());
				((ClientBO)client).handleGroupTransfer();
			}
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
		makeInactive(newParent);
		
		addCustomerHierarchy(new CustomerHierarchyEntity(this,newParent));		
		update();
		if(getChildren()!=null){
			for(CustomerBO client: getChildren()){
				client.setUserContext(getUserContext());
				((ClientBO)client).handleGroupTransfer();
			}
		}
	}
	
	protected void saveUpdatedMeeting(MeetingBO meeting) throws CustomerException{
		logger.debug("In GroupBO::saveUpdatedMeeting(), customerId: "
				+ getCustomerId());
		MeetingBO newMeeting = getCustomerMeeting().getUpdatedMeeting();
		super.saveUpdatedMeeting(meeting);
		if(getParentCustomer()==null)
			deleteMeeting(newMeeting);
	}
	
	@Override
	public void updateMeeting(MeetingBO meeting) throws CustomerException{
		logger.debug("In GroupBO::updateMeeting(), customerId: "
				+ getCustomerId());
		if(getParentCustomer()==null){			
			if(getCustomerMeeting()==null){
				this.setCustomerMeeting(createCustomerMeeting(meeting));
				updateMeetingForClients(meeting);
			}
			else
				saveUpdatedMeeting(meeting);
		}else
			saveUpdatedMeeting(meeting);
		update();
	}
	
	private void updateMeetingForClients(MeetingBO meeting) throws CustomerException{
		Set<CustomerBO> clients = getChildren();			
		if(clients!=null){
			for(CustomerBO client : clients){
				client.setUserContext(getUserContext());
				client.updateMeeting(meeting);
			}
		}
	}
	
	@Override
	public void changeStatus(Short newStatusId, Short flagId, String comment)
	throws CustomerException {
		Short oldStatusId = getCustomerStatus().getId();
		super.changeStatus(newStatusId, flagId, comment);
		if(oldStatusId.equals(CustomerStatus.GROUP_PENDING.getValue()) && newStatusId.equals(CustomerStatus.GROUP_CANCELLED.getValue()) && getChildren()!=null){
			for(CustomerBO client: getChildren()){
				if(client.getCustomerStatus().getId().equals(CustomerStatus.CLIENT_PENDING.getValue())){
					client.setUserContext(getUserContext());
					client.changeStatus(CustomerStatus.CLIENT_PARTIAL.getValue(), null, comment);
				}
			}
		}
	}
	
	@Override
	protected void validateStatusChange(Short newStatusId)
			throws CustomerException {
		logger.debug("In GroupBO::validateStatusChange(), customerId: "
				+ getCustomerId());
		if (newStatusId.equals(CustomerStatus.GROUP_CLOSED.getValue()))
			checkIfGroupCanBeClosed();
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

	@Override
	protected void handleActiveForFirstTime(Short oldStatusId, Short newStatusId) throws CustomerException{
		super.handleActiveForFirstTime(oldStatusId, newStatusId);
		if (isActiveForFirstTime(oldStatusId, newStatusId))
			this.setCustomerActivationDate(new DateTimeService().getCurrentJavaDateTime());
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
			if (getOfficePersistence().isBranchInactive(getOffice().getOfficeId())) {
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
			if (getOfficePersistence()
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

	private void checkIfGroupCanBeClosed() throws CustomerException {
		if (isAnyLoanAccountOpen() || isAnySavingsAccountOpen()) {
			throw new CustomerException(
					CustomerConstants.CUSTOMER_HAS_ACTIVE_ACCOUNTS_EXCEPTION);
		}
		if (getChildren(CustomerLevel.CLIENT,
		        ChildrenStateType.OTHER_THAN_CANCELLED_AND_CLOSED)
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
				int customerCount = getCustomerPersistence()
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
			if (getGroupPersistence().isGroupExists(displayName, officeId))
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
	
	public void update() throws CustomerException {
		try {
			setUpdateDetails();
			getCustomerPersistence().createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new CustomerException(
					CustomerConstants.UPDATE_FAILED_EXCEPTION, e);
		}
	}
}
