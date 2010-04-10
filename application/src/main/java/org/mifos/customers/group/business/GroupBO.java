/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.customers.group.business;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.fees.business.FeeView;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.config.util.helpers.ConfigurationConstants;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerHierarchyEntity;
import org.mifos.customers.business.CustomerMeetingEntity;
import org.mifos.customers.business.CustomerPositionView;
import org.mifos.customers.business.CustomerStatusEntity;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.persistence.GroupPersistence;
import org.mifos.customers.group.util.helpers.GroupConstants;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.util.helpers.ChildrenStateType;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.util.UserContext;

/**
 * This class denotes the Group (row in customer table) object and all
 * attributes associated with it. It has a composition of other objects like
 * Custom fields, fees, personnel etc., since it inherits from Customer
 */
public class GroupBO extends CustomerBO {

    private static final MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.GROUP_LOGGER);
    private GroupPerformanceHistoryEntity groupPerformanceHistory;

    /*
     * Injected Persistence classes
     *
     * DO NOT ACCESS THESE MEMBERS DIRECTLY! ALWAYS USE THE GETTER!
     *
     * The Persistence classes below are used by this class and can be injected
     * via a setter for testing purposes. In order for this mechanism to work
     * correctly, the getter must be used to access them because the getter will
     * initialize the Persistence class if it has not been injected.
     *
     * Long term these references to Persistence classes should probably be
     * eliminated.
     */
    private GroupPersistence groupPersistence = null;
    private OfficePersistence officePersistence = null;

    public GroupPersistence getGroupPersistence() {
        if (null == groupPersistence) {
            groupPersistence = new GroupPersistence();
        }
        return groupPersistence;
    }

    public void setGroupPersistence(final GroupPersistence groupPersistence) {
        this.groupPersistence = groupPersistence;
    }

    public OfficePersistence getOfficePersistence() {
        if (null == officePersistence) {
            officePersistence = new OfficePersistence();
        }
        return officePersistence;
    }

    public void setOfficePersistence(final OfficePersistence officePersistence) {
        this.officePersistence = officePersistence;
    }

    /**
     * default constructor for hibernate usage
     */
    protected GroupBO() {
        super();
    }

    /**
     * TODO - keithw - work in progress
     *
     * minimal constructor
     *
     * @param parentCustomer
     */
    public GroupBO(final CustomerLevel customerLevel, final CustomerStatus customerStatus, final String name,
            final OfficeBO office, final PersonnelBO loanOfficer, final CustomerMeetingEntity customerMeeting,
            final String searchId, final CustomerBO parentCustomer) {
        super(customerLevel, customerStatus, name, office, loanOfficer, customerMeeting, parentCustomer);
        this.setSearchId(searchId);
    }

    public GroupBO(final UserContext userContext, final String displayName, final CustomerStatus customerStatus, final String externalId,
            final boolean trained, final Date trainedDate, final Address address, final List<CustomFieldView> customFields, final List<FeeView> fees,
            final PersonnelBO formedBy, final CustomerBO parentCustomer, final GroupPersistence groupPersistence,
            final OfficePersistence officePersistence) throws CustomerException {
        this(userContext, displayName, customerStatus, externalId, trained, trainedDate, address, customFields, fees,
                formedBy, null, parentCustomer, null, null, groupPersistence, officePersistence);
        validateFieldsForGroupUnderCenter(parentCustomer);
        setValues(trained, trainedDate);
        this.groupPerformanceHistory = new GroupPerformanceHistoryEntity(this);
    }

    public GroupBO(final UserContext userContext, final String displayName, final CustomerStatus customerStatus, final String externalId,
            final boolean trained, final Date trainedDate, final Address address, final List<CustomFieldView> customFields, final List<FeeView> fees,
            final PersonnelBO formedBy, final OfficeBO office, final MeetingBO meeting, final PersonnelBO loanOfficer,
            final GroupPersistence groupPersistence, final OfficePersistence officePersistence) throws CustomerException {
        this(userContext, displayName, customerStatus, externalId, trained, trainedDate, address, customFields, fees,
                formedBy, office, null, meeting, loanOfficer, groupPersistence, officePersistence);
        validateFieldsForGroupUnderOffice(loanOfficer, meeting, office);
        setValues(trained, trainedDate);
        this.groupPerformanceHistory = new GroupPerformanceHistoryEntity(this);
    }

    private GroupBO(final UserContext userContext, final String displayName, final CustomerStatus customerStatus, final String externalId,
            final boolean trained, final Date trainedDate, final Address address, final List<CustomFieldView> customFields, final List<FeeView> fees,
            final PersonnelBO formedBy, final OfficeBO office, final CustomerBO parentCustomer, final MeetingBO meeting,
            final PersonnelBO loanOfficer, final GroupPersistence groupPersistence, final OfficePersistence officePersistence)
            throws CustomerException {
        super(userContext, displayName, CustomerLevel.GROUP, customerStatus, externalId, null, address, customFields,
                fees, formedBy, office, parentCustomer, meeting, loanOfficer);
        this.setGroupPersistence(groupPersistence);
        this.setOfficePersistence(officePersistence);
        validateFields(displayName, formedBy, trained, trainedDate);
    }

    @Override
    public boolean isActive() {
        return getStatus() == CustomerStatus.GROUP_ACTIVE;
    }

    public GroupPerformanceHistoryEntity getGroupPerformanceHistory() {
        return groupPerformanceHistory;
    }

    public void setGroupPerformanceHistory(final GroupPerformanceHistoryEntity groupPerformanceHistory) {
        if (groupPerformanceHistory != null) {
            groupPerformanceHistory.setGroup(this);
        }
        this.groupPerformanceHistory = groupPerformanceHistory;
    }

    public void update(final UserContext userContext, final String displayName, final Short loanOfficerId, final String externalId,
            final Short trained, final Date trainedDate, final Address address, final List<CustomFieldView> customFields,
            final List<CustomerPositionView> customerPositions) throws Exception {
        validateFieldsForUpdate(displayName, loanOfficerId);
        if (trained != null) {
            setTrained(trained);
        } else {
            setTrained(YesNoFlag.NO.getValue());
        }
        setTrainedDate(trainedDate);
        updateLoanOfficer(loanOfficerId);
        setDisplayName(displayName);
        super.update(userContext, externalId, address, customFields, customerPositions);
    }

    public void transferToBranch(final OfficeBO officeToTransfer) throws CustomerException {
        validateNewOffice(officeToTransfer);
        validateForActiveAccounts();
        logger.debug("In GroupBO::transferToBranch(), transfering customerId: " + getCustomerId() + "to branch : "
                + officeToTransfer.getOfficeId());
        validateForDuplicateName(getDisplayName(), officeToTransfer.getOfficeId());

        if (isActive()) {
            setCustomerStatus(new CustomerStatusEntity(CustomerStatus.GROUP_HOLD));
        }

        makeCustomerMovementEntries(officeToTransfer);
        setPersonnel(null);
        setSearchId(generateSearchId());
        update();
        if (getChildren() != null) {
            for (CustomerBO client : getChildren()) {
                client.setUserContext(getUserContext());
                ((ClientBO) client).handleGroupTransfer();
            }
        }
        logger.debug("In GroupBO::transferToBranch(), successfully transfered, customerId :" + getCustomerId());
    }

    public void transferToCenter(final CenterBO newParent) throws CustomerException {
        validateNewCenter(newParent);
        logger.debug("In GroupBO::transferToCenter(), transfering customerId: " + getCustomerId() + "to Center Id : "
                + newParent.getCustomerId());

        validateForActiveAccounts();

        if (!isSameBranch(newParent.getOffice())) {
            makeCustomerMovementEntries(newParent.getOffice());
            if (isActive()) {
                setCustomerStatus(new CustomerStatusEntity(CustomerStatus.GROUP_HOLD));
            }
        }

        changeParentCustomer(newParent);
        makeInactive(newParent);

        addCustomerHierarchy(new CustomerHierarchyEntity(this, newParent));
        update();
        if (getChildren() != null) {
            for (CustomerBO client : getChildren()) {
                client.setUserContext(getUserContext());
                ((ClientBO) client).handleGroupTransfer();
            }
        }
    }

    @Override
    protected void saveUpdatedMeeting(final MeetingBO meeting) throws CustomerException {
        logger.debug("In GroupBO::saveUpdatedMeeting(), customerId: " + getCustomerId());
        MeetingBO newMeeting = getCustomerMeeting().getUpdatedMeeting();
        super.saveUpdatedMeeting(meeting);
        if (getParentCustomer() == null) {
            deleteMeeting(newMeeting);
        }
    }

    @Override
    public void updateMeeting(final MeetingBO meeting) throws CustomerException {
        logger.debug("In GroupBO::updateMeeting(), customerId: " + getCustomerId());
        if (getParentCustomer() == null) {
            if (getCustomerMeeting() == null) {
                this.setCustomerMeeting(createCustomerMeeting(meeting));
                updateMeetingForClients(meeting);
            } else {
                saveUpdatedMeeting(meeting);
            }
        } else {
            saveUpdatedMeeting(meeting);
        }
        update();
    }

    private void updateMeetingForClients(final MeetingBO meeting) throws CustomerException {
        Set<CustomerBO> clients = getChildren();
        if (clients != null) {
            for (CustomerBO client : clients) {
                client.setUserContext(getUserContext());
                client.updateMeeting(meeting);
            }
        }
    }

    @Override
    public void changeStatus(final Short newStatusId, final Short flagId, final String comment) throws CustomerException {
        Short oldStatusId = getCustomerStatus().getId();
        super.changeStatus(newStatusId, flagId, comment);
        if (oldStatusId.equals(CustomerStatus.GROUP_PENDING.getValue())
                && newStatusId.equals(CustomerStatus.GROUP_CANCELLED.getValue()) && getChildren() != null) {
            for (CustomerBO client : getChildren()) {
                if (client.getCustomerStatus().getId().equals(CustomerStatus.CLIENT_PENDING.getValue())) {
                    client.setUserContext(getUserContext());
                    client.changeStatus(CustomerStatus.CLIENT_PARTIAL.getValue(), null, comment);
                }
            }
        }
    }

    @Override
    protected void validateStatusChange(final Short newStatusId) throws CustomerException {
        logger.debug("In GroupBO::validateStatusChange(), customerId: " + getCustomerId());
        if (newStatusId.equals(CustomerStatus.GROUP_CLOSED.getValue())) {
            checkIfGroupCanBeClosed();
        }
        if (newStatusId.equals(CustomerStatus.GROUP_ACTIVE.getValue())) {
            checkIfGroupCanBeActive(newStatusId);
        }
        if (getCustomerStatus().getId().equals(CustomerStatus.GROUP_CANCELLED.getValue())
                && newStatusId.equals(CustomerStatus.GROUP_PARTIAL.getValue())) {
            handleValidationsForCancelToPartial();
        }
        logger.debug("In ClientBO::validateStatusChange(), successfully validated status, customerId: "
                + getCustomerId());
    }

    @Override
    protected boolean isActiveForFirstTime(final Short oldStatus, final Short newStatusId) {
        return (oldStatus.equals(CustomerStatus.GROUP_PARTIAL.getValue()) || oldStatus
                .equals(CustomerStatus.GROUP_PENDING.getValue())) && newStatusId.equals(CustomerStatus.GROUP_ACTIVE
                .getValue());
    }

    @Override
    protected void handleActiveForFirstTime(final Short oldStatusId, final Short newStatusId) throws CustomerException {
        super.handleActiveForFirstTime(oldStatusId, newStatusId);
        if (isActiveForFirstTime(oldStatusId, newStatusId)) {
            this.setCustomerActivationDate(new DateTimeService().getCurrentJavaDateTime());
        }
    }

    protected void validateFieldsForUpdate(final String displayName, final Short loanOfficerId) throws CustomerException {
        if (getCustomerStatus().getId().equals(CustomerStatus.GROUP_ACTIVE.getValue())
                || getCustomerStatus().getId().equals(CustomerStatus.GROUP_HOLD.getValue())) {
            validateLO(loanOfficerId);
        }
        if (!getDisplayName().equals(displayName)) {
            validateForDuplicateName(displayName, getOffice().getOfficeId());
        }

    }

    private void validateNewCenter(final CenterBO toCenter) throws CustomerException {
        if (toCenter == null) {
            throw new CustomerException(CustomerConstants.INVALID_PARENT);
        }

        if (isSameCenter(toCenter)) {
            throw new CustomerException(CustomerConstants.ERRORS_SAME_PARENT_TRANSFER);
        }

        if (!toCenter.isActive()) {
            throw new CustomerException(CustomerConstants.ERRORS_INTRANSFER_PARENT_INACTIVE);
        }

        validateMeetingRecurrenceForTransfer(getCustomerMeeting().getMeeting(), toCenter.getCustomerMeeting()
                .getMeeting());
    }

    private boolean isSameCenter(final CenterBO center) {
        return getParentCustomer().getCustomerId().equals(center.getCustomerId());
    }

    private void validateForActiveAccounts() throws CustomerException {
        if (this.isAnyLoanAccountOpen() || this.isAnySavingsAccountOpen()) {
            throw new CustomerException(CustomerConstants.ERRORS_HAS_ACTIVE_ACCOUNT);
        }
        if (getChildren() != null) {
            for (CustomerBO client : getChildren()) {
                if (client.isAnyLoanAccountOpen() || client.isAnySavingsAccountOpen()) {
                    throw new CustomerException(CustomerConstants.ERRORS_CHILDREN_HAS_ACTIVE_ACCOUNT);
                }
            }
        }
    }

    private void validateNewOffice(final OfficeBO officeToTransfer) throws CustomerException {
        if (officeToTransfer == null) {
            throw new CustomerException(CustomerConstants.INVALID_OFFICE);
        }

        if (isSameBranch(officeToTransfer)) {
            throw new CustomerException(CustomerConstants.ERRORS_SAME_BRANCH_TRANSFER);
        }

        if (!officeToTransfer.isActive()) {
            throw new CustomerException(CustomerConstants.ERRORS_TRANSFER_IN_INACTIVE_OFFICE);
        }
    }

    private void checkIfGroupCanBeActive(final Short groupStatusId) throws CustomerException {
        if (getParentCustomer() == null || getParentCustomer().getCustomerId() == null) {
            if (getPersonnel() == null || getPersonnel().getPersonnelId() == null) {
                throw new CustomerException(GroupConstants.GROUP_LOANOFFICER_NOT_ASSIGNED);
            }
            if (getCustomerMeeting() == null || getCustomerMeeting().getMeeting() == null) {
                throw new CustomerException(GroupConstants.MEETING_NOT_ASSIGNED);
            }
        }
    }

    private void handleValidationsForCancelToPartial() throws CustomerException {
        if (getParentCustomer() != null && getParentCustomer().getCustomerId() != null) {
            checkGroupCanBeChangedFromCancelToPartialIfCenterIsActive();
        } else {
            checkGroupCanBeChangedFromCancelToPartialIfOfficeIsActive();
            if (getPersonnel() != null && getPersonnel().getPersonnelId() != null) {
                checkGroupCanBeChangedFromCancelToPartialIfPersonnelActive();
            }
        }
    }

    private void checkGroupCanBeChangedFromCancelToPartialIfCenterIsActive() throws CustomerException {
        if (!getParentCustomer().isActive()) {
            throw new CustomerException(GroupConstants.CENTER_INACTIVE, new Object[] { MessageLookup.getInstance()
                    .lookupLabel(ConfigurationConstants.CENTER, getUserContext()) });
        }
    }

    private void checkGroupCanBeChangedFromCancelToPartialIfOfficeIsActive() throws CustomerException {
        try {
            if (getOfficePersistence().isBranchInactive(getOffice().getOfficeId())) {
                throw new CustomerException(GroupConstants.BRANCH_INACTIVE, new Object[] { MessageLookup.getInstance()
                        .lookupLabel(ConfigurationConstants.GROUP, getUserContext()) });
            }
        } catch (PersistenceException e) {
            throw new CustomerException(e);
        }
    }

    private void checkGroupCanBeChangedFromCancelToPartialIfPersonnelActive() throws CustomerException {
        try {
            if (getOfficePersistence().hasActivePeronnel(getOffice().getOfficeId())) {
                throw new CustomerException(GroupConstants.LOANOFFICER_INACTIVE, new Object[] { MessageLookup
                        .getInstance().lookup(ConfigurationConstants.BRANCHOFFICE, getUserContext()) });
            }
        } catch (PersistenceException e) {
            throw new CustomerException(e);
        }
    }

    private void checkIfGroupCanBeClosed() throws CustomerException {
        if (isAnyLoanAccountOpen() || isAnySavingsAccountOpen()) {
            throw new CustomerException(CustomerConstants.CUSTOMER_HAS_ACTIVE_ACCOUNTS_EXCEPTION);
        }
        if (getChildren(CustomerLevel.CLIENT, ChildrenStateType.OTHER_THAN_CANCELLED_AND_CLOSED).size() > 0) {
            throw new CustomerException(CustomerConstants.ERROR_STATE_CHANGE_EXCEPTION, new Object[] { MessageLookup
                    .getInstance().lookupLabel(ConfigurationConstants.CLIENT, this.getUserContext()) });
        }
    }

    private String generateSearchId() throws CustomerException {
        String searchId = null;
        if (getParentCustomer() != null) {
            // for a group that is under a center
            childAddedForParent(getParentCustomer());
            searchId = getParentCustomer().getSearchId() + "." + getParentCustomer().getMaxChildCount();
        } else {
            // for a group that is under an office/branch
            try {
                int newSearchIdSuffix = getCustomerPersistence().getMaxSearchIdSuffix(CustomerLevel.GROUP, getOffice().getOfficeId()) + 1;
                searchId = GroupConstants.PREFIX_SEARCH_STRING + newSearchIdSuffix;
            } catch (PersistenceException pe) {
                throw new CustomerException(pe);
            }
        }
        return searchId;
    }

    private void validateFields(final String displayName, final PersonnelBO formedBy, final boolean trained, final Date trainedDate)
            throws CustomerException {
        validateFormedBy(formedBy);
        if (trained && trainedDate == null || !trained && trainedDate != null) {
            throw new CustomerException(CustomerConstants.INVALID_TRAINED_OR_TRAINEDDATE);
        }
        if (getOffice() != null) {
            validateForDuplicateName(displayName, getOffice().getOfficeId());
        }
    }

    private void validateFormedBy(final PersonnelBO formedBy) throws CustomerException {
        if (formedBy == null) {
            throw new CustomerException(CustomerConstants.INVALID_FORMED_BY);
        }

    }

    private void validateForDuplicateName(final String displayName, final Short officeId) throws CustomerException {
        try {
            if (getGroupPersistence().isGroupExists(displayName, officeId)) {
                throw new CustomerException(CustomerConstants.ERRORS_DUPLICATE_CUSTOMER);
            }
        } catch (PersistenceException e) {
            throw new CustomerException(e);
        }
    }

    private void validateFieldsForGroupUnderCenter(final CustomerBO parentCustomer) throws CustomerException {
        if (parentCustomer == null) {
            throw new CustomerException(CustomerConstants.INVALID_PARENT);
        }
    }

    private void validateFieldsForGroupUnderOffice(final PersonnelBO loanOfficer, final MeetingBO meeting, final OfficeBO office)
            throws CustomerException {
        validateOffice(office);
        if (isActive()) {
            validateLO(loanOfficer);
            validateMeeting(meeting);
        }
    }

    private void setValues(final boolean trained, final Date trainedDate) throws CustomerException {
        this.setSearchId(generateSearchId());
        this.setTrained(trained);
        if (trained) {
            this.setTrainedDate(trainedDate);
        }
        if (getStatus().equals(CustomerStatus.GROUP_ACTIVE)) {
            this.setCustomerActivationDate(this.getCreatedDate());
        }
    }

    public void checkIfGroupHasActiveLoan() throws CustomerException {
        if (isAnyLoanAccountOpen()) {
            throw new CustomerException(CustomerConstants.GROUP_HAS_ACTIVE_ACCOUNTS_EXCEPTION);
        }
    }

    @Override
    public void updatePerformanceHistoryOnDisbursement(final LoanBO loan, final Money disburseAmount) throws CustomerException {
        try {
            ((GroupPerformanceHistoryEntity) getPerformanceHistory()).updateOnDisbursement(loan, disburseAmount);
        } catch (AccountException e) {
            throw new CustomerException(e);
        }
    }

    @Override
    public void updatePerformanceHistoryOnWriteOff(final LoanBO loan) throws CustomerException {
        GroupPerformanceHistoryEntity performanceHistory = (GroupPerformanceHistoryEntity) getPerformanceHistory();
        try {
            performanceHistory.updateOnWriteOff(loan);
        } catch (AccountException e) {
            throw new CustomerException(e);
        }
    }

    @Override
    public void updatePerformanceHistoryOnReversal(final LoanBO loan, final Money lastLoanAmount) throws CustomerException {
        try {
            GroupPerformanceHistoryEntity groupPerformanceHistoryEntity = (GroupPerformanceHistoryEntity) getPerformanceHistory();
            groupPerformanceHistoryEntity.updateOnReversal(loan, lastLoanAmount);
        } catch (AccountException e) {
            throw new CustomerException(e);
        }
    }

    @Override
    public void updatePerformanceHistoryOnRepayment(final LoanBO loan, final Money totalAmount) throws CustomerException {
        GroupPerformanceHistoryEntity performanceHistory = (GroupPerformanceHistoryEntity) getPerformanceHistory();
        try {
            performanceHistory.updateOnRepayment(loan, totalAmount);
        } catch (AccountException e) {
            throw new CustomerException(e);
        }
    }

    @Override
    public void updatePerformanceHistoryOnLastInstlPayment(final LoanBO loan, final Money totalAmount) throws CustomerException {
        updatePerformanceHistoryOnRepayment(loan, totalAmount);
    }

    @Override
    public void update() throws CustomerException {
        try {
            setUpdateDetails();
            getCustomerPersistence().createOrUpdate(this);
        } catch (PersistenceException e) {
            throw new CustomerException(CustomerConstants.UPDATE_FAILED_EXCEPTION, e);
        }
    }

    /*
     * This methed is used to regenerate the searchId for a group
     * which has a bad searchId due to previous bugs (such as MIFOS-2737)
     */
    public void updateSearchId() throws CustomerException {
        setSearchId(generateSearchId());
        update();
        if (getChildren() != null) {
            for (CustomerBO client : getChildren()) {
                client.setUserContext(getUserContext());
                ((ClientBO) client).handleGroupTransfer();
            }
        }
    }
}
