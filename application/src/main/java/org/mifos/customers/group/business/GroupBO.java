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

import org.joda.time.DateTime;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.fees.business.FeeView;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.config.util.helpers.ConfigurationConstants;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerCustomFieldEntity;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.util.helpers.GroupConstants;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.util.UserContext;
import org.springframework.util.Assert;

/**
 * This class denotes the Group (row in customer table) object and all attributes associated with it. It has a
 * composition of other objects like Custom fields, fees, personnel etc., since it inherits from Customer
 */
public class GroupBO extends CustomerBO {

    private static final MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.GROUP_LOGGER);

    private GroupPerformanceHistoryEntity groupPerformanceHistory;

    public static GroupBO createGroupWithCenterAsParent(UserContext userContext, String groupName,
            PersonnelBO formedBy, CustomerBO parentCustomer,
            List<CustomerCustomFieldEntity> customerCustomFields, Address address, String externalId, boolean trained,
            DateTime trainedOn, CustomerStatus customerStatus) {

        Assert.notNull(customerStatus, "customerStatus cannot be null");
        Assert.notNull(parentCustomer, "parentCustomer cannot be null");

        DateTime mfiJoiningDate = new DateTime().toDateMidnight().toDateTime();
        PersonnelBO loanOfficer = parentCustomer.getPersonnel();
        OfficeBO office = parentCustomer.getOffice();
        MeetingBO meeting = parentCustomer.getCustomerMeetingValue();

        GroupBO group = new GroupBO(userContext, groupName, formedBy, meeting, loanOfficer, office, customerStatus,
                mfiJoiningDate);

        group.childAddedForParent(parentCustomer);
        group.setParentCustomer(parentCustomer);
        String searchId = parentCustomer.getSearchId() + "." + parentCustomer.getMaxChildCount();

        group.setSearchId(searchId);
        group.setExternalId(externalId);
        group.updateAddress(address);
        group.setTrained(trained);
        if (trained) {
            if (trainedOn != null) {
                group.setTrainedDate(trainedOn.toDate());
            }
        }

        List<CustomerCustomFieldEntity> populatedWithCustomerReference = CustomerCustomFieldEntity
                .fromCustomerCustomFieldEntity(customerCustomFields, group);
        for (CustomerCustomFieldEntity customerCustomFieldEntity : populatedWithCustomerReference) {
            group.addCustomField(customerCustomFieldEntity);
        }

        return group;
    }

    public static GroupBO createGroupAsTopOfCustomerHierarchy(UserContext userContext, String groupName,
            PersonnelBO formedBy, MeetingBO meeting, PersonnelBO loanOfficer, OfficeBO office,
            List<CustomerCustomFieldEntity> customerCustomFields, Address address, String externalId, boolean trained,
            DateTime trainedOn, CustomerStatus customerStatus, int numberOfCustomersInOfficeAlready) {

        Assert.notNull(customerStatus, "customerStatus cannot be null");
        Assert.notNull(meeting, "meeting cannot be null");

        DateTime mfiJoiningDate = new DateTime().toDateMidnight().toDateTime();
        GroupBO group = new GroupBO(userContext, groupName, formedBy, meeting, loanOfficer, office, customerStatus,
                mfiJoiningDate);

        final String searchId = GroupConstants.PREFIX_SEARCH_STRING + String.valueOf(numberOfCustomersInOfficeAlready + 1);
        group.setSearchId(searchId);
        group.setExternalId(externalId);
        group.updateAddress(address);
        group.setTrained(trained);
        if (trained) {
            group.setTrainedDate(trainedOn.toDate());
        }

        List<CustomerCustomFieldEntity> populatedWithCustomerReference = CustomerCustomFieldEntity
                .fromCustomerCustomFieldEntity(customerCustomFields, group);
        for (CustomerCustomFieldEntity customerCustomFieldEntity : populatedWithCustomerReference) {
            group.addCustomField(customerCustomFieldEntity);
        }

        return group;
    }

    /**
     * default constructor for hibernate usage
     */
    protected GroupBO() {
        super();
    }

    /**
     * minimal legal constructor
     */
    private GroupBO(UserContext userContext, String groupName, PersonnelBO formedBy, MeetingBO meeting,
            PersonnelBO loanOfficer, OfficeBO office, CustomerStatus customerStatus, DateTime mfiJoiningDate) {
        super(userContext, groupName, CustomerLevel.GROUP, customerStatus, mfiJoiningDate, office, meeting, loanOfficer, formedBy);
        this.groupPerformanceHistory = new GroupPerformanceHistoryEntity(this);
        if (getStatus().equals(CustomerStatus.GROUP_ACTIVE)) {
            this.setCustomerActivationDate(this.getCreatedDate());
        }
    }

    /**
     * @deprecated - use static factory methods..
     */
    @Deprecated
    public GroupBO(final UserContext userContext, final String displayName, final CustomerStatus customerStatus,
            final String externalId, final boolean trained, final Date trainedDate, final Address address,
            final List<CustomFieldView> customFields, final List<FeeView> fees, final PersonnelBO formedBy,
            final CustomerBO parentCustomer) throws CustomerException {
        this(userContext, displayName, customerStatus, externalId, trained, trainedDate, address, customFields, fees,
                formedBy, null, parentCustomer, null, null);
        validateFieldsForGroupUnderCenter(parentCustomer);
        setValues(trained, trainedDate);
        this.groupPerformanceHistory = new GroupPerformanceHistoryEntity(this);
    }

    /**
     * @deprecated - use static factory methods..
     */
    @Deprecated
    public GroupBO(final UserContext userContext, final String displayName, final CustomerStatus customerStatus,
            final String externalId, final boolean trained, final Date trainedDate, final Address address,
            final List<CustomFieldView> customFields, final List<FeeView> fees, final PersonnelBO formedBy,
            final OfficeBO office, final MeetingBO meeting, final PersonnelBO loanOfficer)
            throws CustomerException {
        this(userContext, displayName, customerStatus, externalId, trained, trainedDate, address, customFields, fees,
                formedBy, office, null, meeting, loanOfficer);
        validateFieldsForGroupUnderOffice(loanOfficer, meeting, office);
        setValues(trained, trainedDate);
        this.groupPerformanceHistory = new GroupPerformanceHistoryEntity(this);
    }

    /**
     * @deprecated - use static factory methods..
     */
    @Deprecated
    private GroupBO(final UserContext userContext, final String displayName, final CustomerStatus customerStatus,
            final String externalId, final boolean trained, final Date trainedDate, final Address address,
            final List<CustomFieldView> customFields, final List<FeeView> fees, final PersonnelBO formedBy,
            final OfficeBO office, final CustomerBO parentCustomer, final MeetingBO meeting,
            final PersonnelBO loanOfficer) throws CustomerException {
        super(userContext, displayName, CustomerLevel.GROUP, customerStatus, externalId, null, address, customFields,
                fees, formedBy, office, parentCustomer, meeting, loanOfficer);
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
    public boolean isActiveForFirstTime(final Short oldStatusId, final Short newStatusId) {

        CustomerStatus oldStatus = CustomerStatus.fromInt(oldStatusId);
        CustomerStatus newStatus = CustomerStatus.fromInt(newStatusId);

        return oldStatus.isGroupPartialOrGroupPending() && newStatus.isGroupActive();
    }

    protected void validateFieldsForUpdate(final String displayName, final Short loanOfficerId)
            throws CustomerException {
        if (getCustomerStatus().getId().equals(CustomerStatus.GROUP_ACTIVE.getValue())
                || getCustomerStatus().getId().equals(CustomerStatus.GROUP_HOLD.getValue())) {
            validateLO(loanOfficerId);
        }
        if (!getDisplayName().equals(displayName)) {
//            validateForDuplicateName(displayName, getOffice().getOfficeId());
        }

    }

    public void validateNewCenter(final CenterBO toCenter) throws CustomerException {
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
        boolean isSame = false;
        CustomerBO parent = getParentCustomer();
        if (parent != null) {
            return parent.getCustomerId().equals(center.getCustomerId());
        }
        return isSame;
    }

    public void validateForActiveAccounts() throws CustomerException {
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

    public void validateNewOffice(final OfficeBO officeToTransfer) throws CustomerException {
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

    @Override
    public void validate() throws CustomerException {
        super.validate();
    }

    private void validateFields(final String displayName, final PersonnelBO formedBy, final boolean trained,
            final Date trainedDate) throws CustomerException {
        validateFormedBy(formedBy);
        if (trained && trainedDate == null || !trained && trainedDate != null) {
            throw new CustomerException(CustomerConstants.INVALID_TRAINED_OR_TRAINEDDATE);
        }
        if (getOffice() != null) {
//            validateForDuplicateName(displayName, getOffice().getOfficeId());
        }
    }

    private void validateFormedBy(final PersonnelBO formedBy) throws CustomerException {
        if (formedBy == null) {
            throw new CustomerException(CustomerConstants.INVALID_FORMED_BY);
        }
    }

    private void validateFieldsForGroupUnderCenter(final CustomerBO parentCustomer) throws CustomerException {
        if (parentCustomer == null) {
            throw new CustomerException(CustomerConstants.INVALID_PARENT);
        }
    }

    private void validateFieldsForGroupUnderOffice(final PersonnelBO loanOfficer, final MeetingBO meeting,
            final OfficeBO office) throws CustomerException {
        validateOffice(office);
        if (isActive()) {
            validateLO(loanOfficer);
            validateMeeting(meeting);
        }
    }

    private void setValues(final boolean trained, final Date trainedDate) throws CustomerException {

        String searchId = null;
        if (getParentCustomer() != null) {
            childAddedForParent(getParentCustomer());
            searchId = getParentCustomer().getSearchId() + "." + getParentCustomer().getMaxChildCount();
        } else {
            try {
                int customerCount = getCustomerPersistence().getCustomerCountForOffice(CustomerLevel.GROUP,
                        getOffice().getOfficeId());
                searchId = GroupConstants.PREFIX_SEARCH_STRING + String.valueOf(customerCount + 1);
            } catch (PersistenceException pe) {
                throw new CustomerException(pe);
            }
        }

        this.setSearchId(searchId);
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
    public void updatePerformanceHistoryOnDisbursement(final LoanBO loan, final Money disburseAmount)
            throws CustomerException {
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
    public void updatePerformanceHistoryOnReversal(final LoanBO loan, final Money lastLoanAmount)
            throws CustomerException {
        try {
            GroupPerformanceHistoryEntity groupPerformanceHistoryEntity = (GroupPerformanceHistoryEntity) getPerformanceHistory();
            groupPerformanceHistoryEntity.updateOnReversal(loan, lastLoanAmount);
        } catch (AccountException e) {
            throw new CustomerException(e);
        }
    }

    @Override
    public void updatePerformanceHistoryOnRepayment(final LoanBO loan, final Money totalAmount)
            throws CustomerException {
        GroupPerformanceHistoryEntity performanceHistory = (GroupPerformanceHistoryEntity) getPerformanceHistory();
        try {
            performanceHistory.updateOnRepayment(loan, totalAmount);
        } catch (AccountException e) {
            throw new CustomerException(e);
        }
    }

    @Override
    public void updatePerformanceHistoryOnLastInstlPayment(final LoanBO loan, final Money totalAmount)
            throws CustomerException {
        updatePerformanceHistoryOnRepayment(loan, totalAmount);
    }

    /**
     * @deprecated - use DAOs to do CRUD operations
     */
    @Deprecated
    @Override
    public void update() throws CustomerException {
        try {
            setUpdateDetails();
            getCustomerPersistence().createOrUpdate(this);
        } catch (PersistenceException e) {
            throw new CustomerException(CustomerConstants.UPDATE_FAILED_EXCEPTION, e);
        }
    }

    public void validateGroupCanBeActive() throws CustomerException {

        if (this.getParentCustomer() == null || this.getParentCustomer().getCustomerId() == null) {
            if (this.getPersonnel() == null || this.getPersonnel().getPersonnelId() == null) {
                throw new CustomerException(GroupConstants.GROUP_LOANOFFICER_NOT_ASSIGNED);
            }
            if (this.getCustomerMeeting() == null || this.getCustomerMeeting().getMeeting() == null) {
                throw new CustomerException(GroupConstants.MEETING_NOT_ASSIGNED);
            }
        }
    }

    public boolean isCancelled() {
        return getStatus().isGroupCancelled();
    }

    public void validateTransitionFromCancelledToPartialIsAllowedBasedOnCenter() throws CustomerException {

        if (!this.getParentCustomer().isActive()) {
            throw new CustomerException(GroupConstants.CENTER_INACTIVE, new Object[] { MessageLookup
                    .getInstance().lookupLabel(ConfigurationConstants.CENTER, this.getUserContext()) });
        }
    }
}