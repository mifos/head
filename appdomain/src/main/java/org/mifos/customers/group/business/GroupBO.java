/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.application.admin.servicefacade.InvalidDateException;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.calendar.CalendarEvent;
import org.mifos.calendar.CalendarUtils;
import org.mifos.config.util.helpers.ConfigurationConstants;
import org.mifos.customers.api.CustomerLevel;
import org.mifos.customers.business.CustomerAccountBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerHierarchyEntity;
import org.mifos.customers.business.CustomerMeetingEntity;
import org.mifos.customers.business.CustomerNoteEntity;
import org.mifos.customers.business.CustomerStatusEntity;
import org.mifos.customers.business.CustomerStatusFlagEntity;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.util.helpers.GroupConstants;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.GroupUpdate;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;
import org.springframework.util.Assert;

/**
 * This class denotes the Group (row in customer table) object and all attributes associated with it. It has a
 * composition of other objects like Custom fields, fees, personnel etc., since it inherits from Customer
 */
public class GroupBO extends CustomerBO {

    private GroupPerformanceHistoryEntity groupPerformanceHistory;

    public static GroupBO createGroupWithCenterAsParent(UserContext userContext, String groupName,
            PersonnelBO formedBy, CustomerBO parentCustomer,
            Address address, String externalId, boolean trained,
            DateTime trainedOn, CustomerStatus customerStatus, DateTime mfiJoiningDate, DateTime activationDate) {

        Assert.notNull(customerStatus, "customerStatus cannot be null");
        Assert.notNull(parentCustomer, "parentCustomer cannot be null");

        PersonnelBO loanOfficer = parentCustomer.getPersonnel();
        OfficeBO office = parentCustomer.getOffice();
        MeetingBO meeting = parentCustomer.getCustomerMeetingValue();

        GroupBO group = new GroupBO(userContext, groupName, formedBy, meeting, loanOfficer, office, customerStatus,
                mfiJoiningDate, activationDate);

        group.setParentCustomer(parentCustomer);

        if (customerStatus.isGroupActive()) {
            CustomerHierarchyEntity hierarchy = new CustomerHierarchyEntity(group, parentCustomer);
            group.addCustomerHierarchy(hierarchy);
        }

        group.generateSearchId();
        group.setExternalId(externalId);
        group.updateAddress(address);
        group.setTrained(trained);
        if (trained) {
            if (trainedOn != null) {
                group.setTrainedDate(trainedOn.toDate());
            }
        }

        return group;
    }

    public static GroupBO createGroupAsTopOfCustomerHierarchy(UserContext userContext, String groupName,
            PersonnelBO formedBy, MeetingBO meeting, PersonnelBO loanOfficer, OfficeBO office,
            Address address, String externalId, boolean trained,
            DateTime trainedOn, CustomerStatus customerStatus, int numberOfCustomersInOfficeAlready, DateTime mfiJoiningDate, DateTime activationDate) {

        Assert.notNull(customerStatus, "customerStatus cannot be null");

        if (customerStatus.isGroupActive()) {
            if (meeting == null) {
                throw new BusinessRuleException(GroupConstants.MEETING_REQUIRED);
            }
        }

        GroupBO group = new GroupBO(userContext, groupName, formedBy, meeting, loanOfficer, office, customerStatus, mfiJoiningDate, activationDate);

        group.generateSearchId();
        group.setExternalId(externalId);
        group.updateAddress(address);
        group.setTrained(trained);
        if (trained) {
            group.setTrainedDate(trainedOn.toDate());
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
            PersonnelBO loanOfficer, OfficeBO office, CustomerStatus customerStatus, DateTime mfiJoiningDate, DateTime activationDate) {
        super(userContext, groupName, CustomerLevel.GROUP, customerStatus, mfiJoiningDate, office, meeting, loanOfficer, formedBy);
        this.groupPerformanceHistory = new GroupPerformanceHistoryEntity(this);
        if (isActive()) {
            this.setCustomerActivationDate(activationDate.toDate());
            this.updateCustomerHierarchy();
        }
    }

    /**
     * @deprecated - use static factory methods..
     */
    @Deprecated
    public GroupBO(final UserContext userContext, final String displayName, final CustomerStatus customerStatus,
            final String externalId, final boolean trained, final Date trainedDate, final Address address,
            final List<CustomFieldDto> customFields, final List<FeeDto> fees, final PersonnelBO formedBy,
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
            final List<CustomFieldDto> customFields, final List<FeeDto> fees, final PersonnelBO formedBy,
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
            final List<CustomFieldDto> customFields, final List<FeeDto> fees, final PersonnelBO formedBy,
            final OfficeBO office, final CustomerBO parentCustomer, final MeetingBO meeting,
            final PersonnelBO loanOfficer) throws CustomerException {
        super(userContext, displayName, CustomerLevel.GROUP, customerStatus, externalId, null, address, customFields,
                fees, formedBy, office, parentCustomer, meeting, loanOfficer);
        validateFields(formedBy, trained, trainedDate);
    }

    @Override
    public boolean isActive() {
        return getStatus().equals(CustomerStatus.GROUP_ACTIVE);
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
    public boolean isActiveForFirstTime(final Short oldStatusId, final Short newStatusId) {

        CustomerStatus oldStatus = CustomerStatus.fromInt(oldStatusId);
        CustomerStatus newStatus = CustomerStatus.fromInt(newStatusId);

        return oldStatus.isGroupPartialOrGroupPending() && newStatus.isGroupActive();
    }

    public void validateReceivingCenter(final CenterBO toCenter) throws CustomerException {
        if (toCenter == null) {
            throw new CustomerException(CustomerConstants.INVALID_PARENT);
        }

        if (isSameCenter(toCenter)) {
            throw new CustomerException(CustomerConstants.ERRORS_SAME_CENTER_TRANSFER);
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
            return parent.hasSameIdentityAs(center);
        }
        return isSame;
    }
    /**
     * Checks if group and its members have active loan/savings account.
    * @throws CustomerException
    */
    public void validateNoActiveAccountsExist() throws CustomerException {
        if (this.isAnyLoanAccountOpen() || this.isAnySavingsAccountOpen()) {
            throw new CustomerException(CustomerConstants.ERRORS_HAS_ACTIVE_ACCOUNT);
        }
        for (CustomerBO client : getChildren()) {
            if (client.isAnyLoanAccountOpen() || client.isAnySavingsAccountOpen()) {
                throw new CustomerException(CustomerConstants.ERRORS_CHILDREN_HAS_ACTIVE_ACCOUNT);
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
        validateFormedBy();
        validateTrained();
    }

    private void validateFields(final PersonnelBO formedBy, final boolean trained,
            final Date trainedDate) throws CustomerException {
        validateFormedBy(formedBy);
        if (trained && trainedDate == null || !trained && trainedDate != null) {
            throw new CustomerException(CustomerConstants.INVALID_TRAINED_OR_TRAINEDDATE);
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

    /**
     * called from deprecated constructors so not used in production
     */
    @Deprecated
    private void setValues(final boolean trained, final Date trainedDate) throws CustomerException {

        this.generateSearchId();
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
            performanceHistory.updateOnFullRepayment(loan);
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

        if (this.getPersonnel() == null) {
            throw new CustomerException(GroupConstants.GROUP_LOANOFFICER_NOT_ASSIGNED);
        }

        if (this.getCustomerMeeting() == null || this.getCustomerMeeting().getMeeting() == null) {
            throw new CustomerException(GroupConstants.MEETING_NOT_ASSIGNED);
        }
    }

    public boolean isCancelled() {
        return getStatus().isGroupCancelled();
    }

    public void validateTransitionFromCancelledToPartialIsAllowedBasedOnCenter() throws CustomerException {

        if (!this.getParentCustomer().isActive()) {
            throw new CustomerException(GroupConstants.CENTER_INACTIVE,
                    new Object[] { ApplicationContextProvider.getBean(MessageLookup.class).lookupLabel(ConfigurationConstants.CENTER) });
        }
    }

    /*
     * This methed is used to regenerate the searchId for a group
     * which has a bad searchId due to previous bugs (such as MIFOS-2737)
     */
    public void updateSearchId() throws CustomerException {
        generateSearchId();
        update();
        if (getChildren() != null) {
            for (CustomerBO client : getChildren()) {
                client.setUserContext(getUserContext());
                ((ClientBO) client).handleGroupTransfer();
            }
        }
    }

    public void updateTrainedDetails(GroupUpdate groupUpdate) throws CustomerException {

        try {
            if (groupUpdate.getTrainedDateAsString() != null && !StringUtils.isBlank(groupUpdate.getTrainedDateAsString())) {
                DateTime trainedDate = CalendarUtils.getDateFromString(groupUpdate.getTrainedDateAsString(), userContext
                        .getPreferredLocale());

                if (trainedDate != null) {
                    this.setTrainedDate(trainedDate.toDate());
                }
            }

            if (groupUpdate.isTrained()) {
                this.setTrained(groupUpdate.isTrained());
            } else {
                this.setTrained(false);
            }

            this.setUpdateDetails();
        } catch (InvalidDateException e) {
            throw new CustomerException(CustomerConstants.MFI_JOINING_DATE_MANDATORY, e);
        }
    }

    public final void updateCustomerHierarchy() {
        if (this.getParentCustomer() != null) {
            CustomerHierarchyEntity hierarchy = new CustomerHierarchyEntity(this, this.getParentCustomer());
            this.addCustomerHierarchy(hierarchy);
        }
    }

    public void regenerateCustomerFeeSchedule(CalendarEvent applicableCalendarEvents) {
        CustomerAccountBO customerAccount = this.getCustomerAccount();
        if (customerAccount != null) {
            List<AccountFeesEntity> accountFees = new ArrayList<AccountFeesEntity>(customerAccount.getAccountFees());
            customerAccount.createSchedulesAndFeeSchedulesForFirstTimeActiveCustomer(this, accountFees, this.getCustomerMeetingValue(), applicableCalendarEvents, new DateMidnight().toDateTime());
        }
    }

    @Override
    public void updateCustomerStatus(CustomerStatus newStatus, CustomerNoteEntity customerNote, CustomerStatusFlagEntity customerStatusFlagEntity) {
        this.clearCustomerFlagsIfApplicable(getStatus(), newStatus);
        super.updateCustomerStatus(newStatus, customerNote, customerStatusFlagEntity);
    }

    public boolean transferTo(CenterBO receivingCenter) {

        boolean regenerateGroupSchedules = false;

        this.setParentCustomer(receivingCenter);
        this.setPersonnel(receivingCenter.getPersonnel());

        CustomerHierarchyEntity currentHierarchy = this.getActiveCustomerHierarchy();
        if (null != currentHierarchy) {
            currentHierarchy.makeInactive(this.getUserContext().getId());
        }
        this.addCustomerHierarchy(new CustomerHierarchyEntity(this, receivingCenter));

        MeetingBO centerMeeting = receivingCenter.getCustomerMeetingValue();
        MeetingBO groupMeeting = this.getCustomerMeetingValue();
        if (centerMeeting != null) {
            if (groupMeeting != null) {
                regenerateGroupSchedules = receivingCenter.hasMeetingDifferentTo(groupMeeting);

                CustomerMeetingEntity groupMeetingEntity = this.getCustomerMeeting();
                groupMeetingEntity.setMeeting(receivingCenter.getCustomerMeetingValue());
            } else {
                CustomerMeetingEntity customerMeeting = this.createCustomerMeeting(centerMeeting);
                this.setCustomerMeeting(customerMeeting);
            }
        } else if (groupMeeting != null) {
            this.setCustomerMeeting(null);
        }

        OfficeBO centerOffice = receivingCenter.getOffice();
        if (this.isDifferentBranch(centerOffice)) {
            this.makeCustomerMovementEntries(centerOffice);
            if (this.isActive()) {
                this.setCustomerStatus(new CustomerStatusEntity(CustomerStatus.GROUP_HOLD));
            }
            regenerateGroupSchedules = true;
        }

        this.generateSearchId();

        return regenerateGroupSchedules;
    }
}