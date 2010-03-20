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

package org.mifos.customers.business.service;

import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.servicefacade.CenterUpdate;
import org.mifos.application.servicefacade.DependencyInjectedServiceLocator;
import org.mifos.application.servicefacade.GroupUpdate;
import org.mifos.calendar.CalendarUtils;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.config.util.helpers.ConfigurationConstants;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.business.CustomerAccountBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerHierarchyEntity;
import org.mifos.customers.business.CustomerMeetingEntity;
import org.mifos.customers.business.CustomerStatusEntity;
import org.mifos.customers.business.CustomerView;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.group.util.helpers.GroupConstants;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.schedule.ScheduledDateGeneration;
import org.mifos.schedule.ScheduledEvent;
import org.mifos.schedule.ScheduledEventFactory;
import org.mifos.schedule.internal.HolidayAndWorkingDaysScheduledDateGeneration;
import org.mifos.security.util.UserContext;

public class CustomerServiceImpl implements CustomerService {

    private final CustomerDao customerDao;
    private final PersonnelDao personnelDao;
    private final OfficeDao officeDao;

    public CustomerServiceImpl(CustomerDao customerDao, PersonnelDao personnelDao, OfficeDao officeDao) {
        this.customerDao = customerDao;
        this.personnelDao = personnelDao;
        this.officeDao = officeDao;
    }

    @Override
    public void updateCenter(UserContext userContext, CenterUpdate centerUpdate, CenterBO center) {

        Short loanOfficerId = centerUpdate.getLoanOfficerId();
        PersonnelBO loanOfficer = personnelDao.findPersonnelById(loanOfficerId);

        try {
            if (center.isActive()) {
                center.validateLO(loanOfficer);
            }

            center.setMfiJoiningDate(centerUpdate.getMfiJoiningDateTime().toDate());

            if (center.isLOChanged(loanOfficerId)) {
                // If a new loan officer has been assigned, then propagate this
                // change to the customer's children and to their associated
                // accounts.
                new CustomerPersistence().updateLOsForAllChildren(loanOfficerId, center.getSearchId(), center
                        .getOffice().getOfficeId());

                new CustomerPersistence().updateLOsForAllChildrenAccounts(loanOfficerId, center.getSearchId(), center
                        .getOffice().getOfficeId());

                center.setLoanOfficer(loanOfficer);
            }

            center.setExternalId(centerUpdate.getExternalId());
            center.setUserContext(userContext);
            center.updateAddress(centerUpdate.getAddress());
            center.updateCustomFields(centerUpdate.getCustomFields());
            center.updateCustomerPositions(centerUpdate.getCustomerPositions());
            center.setUpdatedBy(userContext.getId());
            center.setUpdatedDate(new DateTime().toDate());

            StaticHibernateUtil.startTransaction();

            customerDao.save(center);

            StaticHibernateUtil.commitTransaction();

        } catch (CustomerException e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } catch (InvalidDateException e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } catch (Exception e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    @Override
    public void create(CustomerBO customer, MeetingBO meeting, List<AccountFeesEntity> accountFees) {

        try {
            StaticHibernateUtil.startTransaction();
            this.customerDao.save(customer);

            // CREATE CUSTOMER FEE SCHEDULE AND MEETING SCHEDULE FROM CUSTOMER
            // generate meeting schedule based on customer meeting!!!

            List<Days> workingDays = new FiscalCalendarRules().getWorkingDaysAsJodaTimeDays();
            HolidayDao holidayDao = DependencyInjectedServiceLocator.locateHolidayDao();
            List<Holiday> thisAndNextYearsHolidays = holidayDao.findAllHolidaysThisYearAndNext();

            DateTime startFromMeetingDate = new DateTime(meeting.getMeetingStartDate());
            ScheduledEvent scheduledEvent = ScheduledEventFactory.createScheduledEventFrom(meeting);

            ScheduledDateGeneration dateGeneration = new HolidayAndWorkingDaysScheduledDateGeneration(workingDays,
                    thisAndNextYearsHolidays);
            List<DateTime> installmentDates = dateGeneration.generateScheduledDates(10, startFromMeetingDate,
                    scheduledEvent);

            CustomerAccountBO customerAccount = CustomerAccountBO.createNew(customer, accountFees, installmentDates);
            customer.addAccount(customerAccount);
            this.customerDao.save(customer);
            StaticHibernateUtil.commitTransaction();

            StaticHibernateUtil.startTransaction();
            customer.generateGlobalCustomerNumber();
            this.customerDao.save(customer);
            StaticHibernateUtil.commitTransaction();
        } catch (Exception e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    @Override
    public void createGroup(GroupBO group, MeetingBO meeting, List<AccountFeesEntity> accountFees)
            throws CustomerException {

        group.validate();

        customerDao.validateGroupNameIsNotTakenForOffice(group.getDisplayName(), group.getOffice().getOfficeId());

        try {
            StaticHibernateUtil.startTransaction();

            this.customerDao.save(group);

            // CREATE CUSTOMER FEE SCHEDULE AND MEETING SCHEDULE FROM CUSTOMER
            // generate meeting schedule based on customer meeting!!!

            List<Days> workingDays = new FiscalCalendarRules().getWorkingDaysAsJodaTimeDays();
            HolidayDao holidayDao = DependencyInjectedServiceLocator.locateHolidayDao();
            List<Holiday> thisAndNextYearsHolidays = holidayDao.findAllHolidaysThisYearAndNext();

            DateTime startFromMeetingDate = new DateTime(meeting.getMeetingStartDate());
            ScheduledEvent scheduledEvent = ScheduledEventFactory.createScheduledEventFrom(meeting);

            ScheduledDateGeneration dateGeneration = new HolidayAndWorkingDaysScheduledDateGeneration(workingDays,
                    thisAndNextYearsHolidays);
            List<DateTime> installmentDates = dateGeneration.generateScheduledDates(10, startFromMeetingDate,
                    scheduledEvent);

            CustomerAccountBO customerAccount = CustomerAccountBO.createNew(group, accountFees, installmentDates);
            group.addAccount(customerAccount);
            this.customerDao.save(group);
            StaticHibernateUtil.commitTransaction();

            // FIXME - keith may need to update parent also...
            // try {
            // if (groupBo.getParentCustomer() != null) {
            // customerPersistence.createOrUpdate(groupBo.getParentCustomer());
            // }
            // } catch (PersistenceException pe) {
            // throw new CustomerException(CustomerConstants.CREATE_FAILED_EXCEPTION, pe);
            // }

            StaticHibernateUtil.startTransaction();
            group.generateGlobalCustomerNumber();
            this.customerDao.save(group);
            StaticHibernateUtil.commitTransaction();
        } catch (Exception e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    @Override
    public void updateGroup(UserContext userContext, GroupUpdate groupUpdate, GroupBO group) throws CustomerException {

        group.validate();

        if (!group.getDisplayName().equals(groupUpdate.getDisplayName())) {
            // customerDao.validateGroupNameIsNotTakenForOffice(groupUpdate.getDisplayName(),
            // group.getOffice().getOfficeId());
        }

        Short loanOfficerId = groupUpdate.getLoanOfficerId();
        PersonnelBO loanOfficer = personnelDao.findPersonnelById(loanOfficerId);

        try {
            DateTime trainedDate = null;

            if (groupUpdate.getTrainedDateAsString() != null) {
                trainedDate = CalendarUtils.getDateFromString(groupUpdate.getTrainedDateAsString(), userContext
                        .getPreferredLocale());
            }

            if (groupUpdate.isTrained()) {
                group.setTrained(groupUpdate.isTrained());
                group.setTrainedDate(trainedDate.toDate());
            } else {
                group.setTrained(false);
            }

            if (group.isActive()) {
                group.validateLO(loanOfficer);
            }

            if (group.isLOChanged(loanOfficerId)) {
                // If a new loan officer has been assigned, then propagate this
                // change to the customer's children and to their associated
                // accounts.
                new CustomerPersistence().updateLOsForAllChildren(loanOfficerId, group.getSearchId(), group.getOffice()
                        .getOfficeId());

                new CustomerPersistence().updateLOsForAllChildrenAccounts(loanOfficerId, group.getSearchId(), group
                        .getOffice().getOfficeId());

                group.setLoanOfficer(loanOfficer);
            }

            group.setExternalId(groupUpdate.getExternalId());
            group.setUserContext(userContext);
            group.updateAddress(groupUpdate.getAddress());
            group.updateCustomFields(groupUpdate.getCustomFields());
            group.updateCustomerPositions(groupUpdate.getCustomerPositions());
            group.setUpdatedBy(userContext.getId());
            group.setUpdatedDate(new DateTime().toDate());
            group.setDisplayName(groupUpdate.getDisplayName());

            StaticHibernateUtil.startTransaction();

            customerDao.save(group);

            StaticHibernateUtil.commitTransaction();

        } catch (CustomerException e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } catch (InvalidDateException e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } catch (Exception e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    @Override
    public GroupBO transferGroupTo(GroupBO group, CenterBO transferToCenter) throws CustomerException {

        // FIXME - keithw - moved out transfer logic into service as first step in cleaning up.
        group.validateNewCenter(transferToCenter);
        group.validateForActiveAccounts();

        OfficeBO centerOffice = transferToCenter.getOffice();
        if (group.isDifferentBranch(centerOffice)) {
            group.makeCustomerMovementEntries(centerOffice);
            if (group.isActive()) {
                group.setCustomerStatus(new CustomerStatusEntity(CustomerStatus.GROUP_HOLD));
            }
        }

        CustomerBO oldParent = group.getParentCustomer();
        group.setParentCustomer(transferToCenter);

        CustomerHierarchyEntity currentHierarchy = group.getActiveCustomerHierarchy();
        if (null != currentHierarchy) {
            currentHierarchy.makeInactive(group.getUserContext().getId());
        }
        group.addCustomerHierarchy(new CustomerHierarchyEntity(group, transferToCenter));

        // handle parent
        group.setPersonnel(transferToCenter.getPersonnel());

        MeetingBO centerMeeting = transferToCenter.getCustomerMeetingValue();
        MeetingBO groupMeeting = group.getCustomerMeetingValue();
        if (centerMeeting != null) {
            if (groupMeeting != null) {
                if (!groupMeeting.getMeetingId().equals(centerMeeting.getMeetingId())) {
                    group.setUpdatedMeeting(centerMeeting);
                }
            } else {
                CustomerMeetingEntity customerMeeting = group.createCustomerMeeting(centerMeeting);
                group.setCustomerMeeting(customerMeeting);
            }
        } else if (groupMeeting != null) {
            // try {
            // new CustomerPersistence().deleteCustomerMeeting(group);
            group.setCustomerMeeting(null);
            // } catch (PersistenceException e) {
            // throw new MifosRuntimeException(e);
            // }
        }

        if (oldParent != null) {
            oldParent.decrementChildCount();
            oldParent.setUserContext(group.getUserContext());
        }

        transferToCenter.incrementChildCount();
        group.setSearchId(transferToCenter.getSearchId() + "." + String.valueOf(transferToCenter.getMaxChildCount()));

        transferToCenter.setUserContext(group.getUserContext());

        // CustomerHierarchyEntity currentHierarchy1 = getActiveCustomerHierarchy();
        // currentHierarchy1.makeInactive(userContext.getId());
        // this.addCustomerHierarchy(new CustomerHierarchyEntity(this, newParent));
        //
        // addCustomerHierarchy(new CustomerHierarchyEntity(this, newParent));

        try {
            StaticHibernateUtil.startTransaction();

            // group.transferToCenter(transferToCenter);
            group.setUpdateDetails();

            if (oldParent != null) {
                customerDao.save(oldParent);
            }
            customerDao.save(transferToCenter);

            customerDao.save(group);

            Set<CustomerBO> clients = group.getChildren();

            if (clients != null) {
                for (CustomerBO client : clients) {
                    client.setUserContext(group.getUserContext());
                    ((ClientBO) client).handleGroupTransfer();
                    client.setUpdateDetails();
                    customerDao.save(client);
                }
            }
            StaticHibernateUtil.commitTransaction();

            return group;
        } catch (Exception e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            StaticHibernateUtil.closeSession();
        }

    }

    @Override
    public GroupBO transferGroupTo(GroupBO group, OfficeBO transferToOffice) throws CustomerException {

        group.validateNewOffice(transferToOffice);

        if (group.isActive()) {
            group.setCustomerStatus(new CustomerStatusEntity(CustomerStatus.GROUP_HOLD));
        }

        group.makeCustomerMovementEntries(transferToOffice);
        group.setPersonnel(null);

        CustomerBO oldParentOfGroup = group.getParentCustomer();

        String searchId = null;
        if (oldParentOfGroup != null) {
            oldParentOfGroup.incrementChildCount();
            searchId = oldParentOfGroup.getSearchId() + "." + oldParentOfGroup.getMaxChildCount();
        } else {
            try {
                int customerCount = new CustomerPersistence().getCustomerCountForOffice(CustomerLevel.GROUP, group
                        .getOffice().getOfficeId());
                searchId = GroupConstants.PREFIX_SEARCH_STRING + String.valueOf(customerCount + 1);
            } catch (PersistenceException pe) {
                throw new CustomerException(pe);
            }
        }
        group.setSearchId(searchId);

        try {
            StaticHibernateUtil.startTransaction();

            group.setUpdateDetails();

            if (oldParentOfGroup != null) {
                customerDao.save(oldParentOfGroup);
            }

            customerDao.save(group);

            Set<CustomerBO> clients = group.getChildren();

            if (clients != null) {
                for (CustomerBO client : clients) {
                    client.setUserContext(group.getUserContext());
                    ((ClientBO) client).handleGroupTransfer();
                    client.setUpdateDetails();
                    customerDao.save(client);
                }
            }
            StaticHibernateUtil.commitTransaction();

            return group;
        } catch (Exception e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    @Override
    public void updateCenterStatus(CenterBO center, CustomerStatus newStatus) throws CustomerException {

        if (newStatus.equals(CustomerStatus.CENTER_INACTIVE.getValue())) {

            center.validateChangeToInActive();

            List<CustomerView> groupsThatAreNotClosedOrCanceled = this.customerDao
                    .findClientsThatAreNotCancelledOrClosed(center.getSearchId(), center.getOffice().getOfficeId());

            if (groupsThatAreNotClosedOrCanceled.size() > 0) {
                throw new CustomerException(CustomerConstants.ERROR_STATE_CHANGE_EXCEPTION,
                        new Object[] { MessageLookup.getInstance().lookupLabel(ConfigurationConstants.GROUP,
                                center.getUserContext()) });
            }

        } else if (newStatus.equals(CustomerStatus.CENTER_ACTIVE.getValue())) {
            center.validateChangeToActive();
        }

        try {
            StaticHibernateUtil.startTransaction();

            customerDao.save(center);
            StaticHibernateUtil.commitTransaction();

        } catch (Exception e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    @Override
    public void updateGroupStatus(GroupBO group, CustomerStatus oldStatus, CustomerStatus newStatus)
            throws CustomerException {

        validateChangeOfStatusForGroup(group, oldStatus, newStatus);

        try {
            StaticHibernateUtil.startTransaction();

            if (group.isActiveForFirstTime(oldStatus.getValue(), newStatus.getValue())) {
                group.setCustomerActivationDate(new DateTime().toDate());

                group.getCustomerAccount().generateCustomerFeeSchedule();
            }

            Set<CustomerBO> groupChildren = group.getChildren();

            if (oldStatus.isGroupPending() && newStatus.isGroupCancelled() && groupChildren != null) {

                for (CustomerBO child : groupChildren) {

                    ClientBO client = (ClientBO) child;

                    if (client.isPending()) {
                        client.setUserContext(group.getUserContext());
                        changeClientStatus(client, CustomerStatus.CLIENT_PARTIAL);
                    }
                }
            }

            customerDao.save(group);
            StaticHibernateUtil.commitTransaction();

        } catch (Exception e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    private void changeClientStatus(ClientBO client, CustomerStatus clientPartial) {
        // FIXME - ensure clients statuses are changed as well.
    }

    private void validateChangeOfStatusForGroup(GroupBO group, CustomerStatus oldStatus, CustomerStatus newStatus) throws CustomerException {

        if (newStatus.isGroupClosed()) {
            group.validateNoActiveAccountExist();

            List<CustomerView> clientsThatAreNotClosedOrCanceled = this.customerDao
                    .findClientsThatAreNotCancelledOrClosed(group.getSearchId(), group.getOffice().getOfficeId());

            if (clientsThatAreNotClosedOrCanceled.size() > 0) {
                throw new CustomerException(CustomerConstants.ERROR_STATE_CHANGE_EXCEPTION,
                        new Object[] { MessageLookup.getInstance().lookupLabel(ConfigurationConstants.CLIENT,
                                group.getUserContext()) });
            }
        }

        if (newStatus.isGroupActive()) {
            group.validateGroupCanBeActive();
        }

        if (oldStatus.isGroupCancelled() && newStatus.isGroupPartial()) {

            // handleValidationsForCancelToPartial
            if (group.getParentCustomer() != null && group.getParentCustomer().getCustomerId() != null) {
                group.validateTransitionFromCancelledToPartialIsAllowedBasedOnCenter();
            } else {
                this.officeDao.validateBranchIsActiveWithNoActivePersonnel(group.getOffice().getOfficeId(), group
                        .getUserContext());
            }
        }
    }

    @Override
    public void updateClientStatus(ClientBO client, UserContext userContext) {
        try {
            StaticHibernateUtil.startTransaction();

            if (client.isClosedOrCancelled()) {

                if (client.isClientUnderGroup()) {

                    CustomerBO parentCustomer = client.getParentCustomer();

                    client.resetPositions(parentCustomer);
                    parentCustomer.setUserContext(userContext);
                    CustomerBO center = parentCustomer.getParentCustomer();
                    if (center != null) {
                        parentCustomer.resetPositions(center);
                        center.setUserContext(userContext);
                    }
                }

                // close customer account - #MIFOS-1504
                for (AccountBO account : client.getAccounts()) {
                    if (account.isOfType(AccountTypes.CUSTOMER_ACCOUNT) && account.isOpen()) {
                        // try {
                        account.setUserContext(userContext);

                        // FIXME - figure out taking this out of domain model to remove persistence
                        // FIXME - put back in commented out code
                        // account.changeStatus(AccountState.CUSTOMER_ACCOUNT_INACTIVE, flagId, notes);
                        // account.update();
                        // } catch (AccountException e) {
                        // throw new CustomerException(e);
                        // }
                    }
                }
            }

            customerDao.save(client);
            StaticHibernateUtil.commitTransaction();

        } catch (Exception e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }
}