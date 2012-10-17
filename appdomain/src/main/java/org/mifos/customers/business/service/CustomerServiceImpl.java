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

package org.mifos.customers.business.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.persistence.SavingsProductDao;
import org.mifos.accounts.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.persistence.SavingsPersistence;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.admin.servicefacade.InvalidDateException;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.persistence.LegacyMasterDao;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.RankOfDay;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.application.servicefacade.CustomerStatusUpdate;
import org.mifos.calendar.CalendarEvent;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.config.util.helpers.ConfigurationConstants;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.api.CustomerLevel;
import org.mifos.customers.business.CustomerAccountBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerHierarchyEntity;
import org.mifos.customers.business.CustomerMeetingEntity;
import org.mifos.customers.business.CustomerNoteEntity;
import org.mifos.customers.business.CustomerPositionEntity;
import org.mifos.customers.business.CustomerStatusEntity;
import org.mifos.customers.business.CustomerStatusFlagEntity;
import org.mifos.customers.business.PositionEntity;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.client.business.ClientInitialSavingsOfferingEntity;
import org.mifos.customers.client.util.helpers.ClientConstants;
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
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.customers.util.helpers.CustomerStatusFlag;
import org.mifos.dto.domain.CenterUpdate;
import org.mifos.dto.domain.ClientFamilyInfoUpdate;
import org.mifos.dto.domain.ClientMfiInfoUpdate;
import org.mifos.dto.domain.ClientPersonalInfoUpdate;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.CustomerDto;
import org.mifos.dto.domain.CustomerPositionDto;
import org.mifos.dto.domain.GroupUpdate;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.image.service.ClientPhotoService;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Default implementation of {@link CustomerService}.
 */
public class CustomerServiceImpl implements CustomerService {

    private final CustomerDao customerDao;
    private final PersonnelDao personnelDao;
    private final OfficeDao officeDao;
    private final HolidayDao holidayDao;
    private final HibernateTransactionHelper hibernateTransactionHelper;
    private CustomerAccountFactory customerAccountFactory = DefaultCustomerAccountFactory.createNew();
    private MessageLookupHelper messageLookupHelper = DefaultMessageLookupHelper.createNew();

    private ConfigurationPersistence configurationPersistence;

    @Autowired
    private LegacyMasterDao legacyMasterDao;

    @Autowired
    private SavingsProductDao savingsProductDao;

    @Autowired
    private ClientPhotoService clientPhotoService;

    private MifosConfigurationHelper configurationHelper = new DefaultMifosConfigurationHelper();

    @Autowired
    public CustomerServiceImpl(CustomerDao customerDao, PersonnelDao personnelDao, OfficeDao officeDao,
            HolidayDao holidayDao, final HibernateTransactionHelper hibernateTransactionHelper) {
        this.customerDao = customerDao;
        this.personnelDao = personnelDao;
        this.officeDao = officeDao;
        this.holidayDao = holidayDao;
        this.hibernateTransactionHelper = hibernateTransactionHelper;
    }

    public void setCustomerAccountFactory(CustomerAccountFactory customerAccountFactory) {
        this.customerAccountFactory = customerAccountFactory;
    }

    public void setMessageLookupHelper(MessageLookupHelper messageLookupHelper) {
        this.messageLookupHelper = messageLookupHelper;
    }

    private ConfigurationPersistence getConfigurationPersistence() {
        if (configurationPersistence == null) {
            configurationPersistence = new ConfigurationPersistence();
        }
        return configurationPersistence;
    }

    public void setConfigurationPersistence(ConfigurationPersistence configurationPersistence) {
        this.configurationPersistence = configurationPersistence;
    }
    @Override
    public final void createCenter(CenterBO customer, MeetingBO meeting, List<AccountFeesEntity> accountFees) {

        try {
            customer.validate();
            customer.validateMeetingAndFees(accountFees);

            customerDao.validateCenterNameIsNotTakenForOffice(customer.getDisplayName(), customer.getOfficeId());

            createCustomer(customer, meeting, accountFees);
        } catch (CustomerException e) {
            throw new BusinessRuleException(e.getKey(), new Object[] { customer.getDisplayName()});
        }
    }

    @Override
    public final void createGroup(GroupBO group, MeetingBO meeting, List<AccountFeesEntity> accountFees)
            throws CustomerException {

        group.validate();

        customerDao.validateGroupNameIsNotTakenForOffice(group.getDisplayName(), group.getOffice().getOfficeId());

        createCustomer(group, meeting, accountFees);
    }

    @Override
    public final void createClient(ClientBO client, MeetingBO meeting, List<AccountFeesEntity> accountFees,
            List<SavingsOfferingBO> savingProducts) throws CustomerException {

        client.validate();
        client.validateNoDuplicateSavings(savingProducts);

        customerDao.validateClientForDuplicateNameOrGovtId(client.getDisplayName(), client.getDateOfBirth(), client.getGovernmentId());

        if (client.isActive()) {
            client.validateFieldsForActiveClient();

            createSavingsAccountsForActiveSavingProducts(client, savingProducts);

            generateSavingSchedulesForGroupAndCenterSavingAccounts(client);
        }

        createCustomer(client, meeting, accountFees);
    }

    private void generateSavingSchedulesForGroupAndCenterSavingAccounts(ClientBO client) {

        try {
            List<Days> workingDays = new FiscalCalendarRules().getWorkingDaysAsJodaTimeDays();
            List<Holiday> holidays = new ArrayList<Holiday>();

            UserContext userContext = client.getUserContext();
            CustomerBO group = client.getParentCustomer();

            if (group != null) {

                List<SavingsBO> groupSavingAccounts = new CustomerPersistence().retrieveSavingsAccountForCustomer(group.getCustomerId());

                CustomerBO center = group.getParentCustomer();
                if (center != null) {
                    List<SavingsBO> centerSavingAccounts = new CustomerPersistence().retrieveSavingsAccountForCustomer(center.getCustomerId());
                    groupSavingAccounts.addAll(centerSavingAccounts);
                }

                for (SavingsBO savings : groupSavingAccounts) {
                    savings.setUserContext(userContext);

                    if (client.getCustomerMeetingValue() != null) {

                        if (!(savings.getCustomer().getLevel() == CustomerLevel.GROUP && savings.getRecommendedAmntUnit().getId().equals(RecommendedAmountUnit.COMPLETE_GROUP.getValue()))) {
                            DateTime today = new DateTime().toDateMidnight().toDateTime();
                            savings.generateDepositAccountActions(client, client.getCustomerMeeting().getMeeting(), workingDays, holidays, today);
                        }
                    }
                }
            }
        } catch (PersistenceException pe) {
            throw new MifosRuntimeException(pe);
        }
    }

    private void createSavingsAccountsForActiveSavingProducts(ClientBO client, List<SavingsOfferingBO> savingProducts) {

        UserContext userContext = client.getUserContext();
        List<SavingsBO> savingsAccounts = new ArrayList<SavingsBO>();
        for (SavingsOfferingBO clientSavingsProduct : savingProducts) {

            try {
                if (clientSavingsProduct.isActive()) {

                    List<CustomFieldDto> savingCustomFieldViews = new ArrayList<CustomFieldDto>();

                    SavingsBO savingsAccount = new SavingsBO(userContext, clientSavingsProduct, client,
                            AccountState.SAVINGS_ACTIVE, clientSavingsProduct.getRecommendedAmount(),
                            savingCustomFieldViews);
                    savingsAccounts.add(savingsAccount);
                }
            } catch (AccountException pe) {
                throw new MifosRuntimeException(pe);
            }
        }

        client.addSavingsAccounts(savingsAccounts);
    }

    private void createCustomer(CustomerBO customer, MeetingBO meeting, List<AccountFeesEntity> accountFees) {
        try {
            this.hibernateTransactionHelper.startTransaction();
            this.customerDao.save(customer);
            this.hibernateTransactionHelper.flushSession();

            // generate globalids for savings accounts, as they require accouont_id the savings accounts
            // must first be saved via the customer save.
            for (AccountBO account: customer.getAccounts()) {
                if (account.isSavingsAccount()) {
                    SavingsBO savingsAccount = (SavingsBO)account;
                    savingsAccount.setUserContext(customer.getUserContext());
                    savingsAccount.generateSystemId(customer.getOffice().getGlobalOfficeNum());
                }
            }
            this.customerDao.save(customer);
            this.hibernateTransactionHelper.flushSession();

            CalendarEvent applicableCalendarEvents = this.holidayDao.findCalendarEventsForThisYearAndNext(customer.getOfficeId());
            CustomerAccountBO customerAccount = this.customerAccountFactory.create(customer, accountFees, meeting, applicableCalendarEvents);
            customer.addAccount(customerAccount);

            this.customerDao.save(customer);
            this.hibernateTransactionHelper.flushSession();
            if (customer.getParentCustomer() != null) {
                this.customerDao.save(customer.getParentCustomer());
            }

            customer.generateGlobalCustomerNumber();
            customer.generateSearchId();
            this.customerDao.save(customer);

            if (customer.getParentCustomer() != null) {
                this.customerDao.save(customer.getParentCustomer());
            }

            this.hibernateTransactionHelper.commitTransaction();
        } catch (Exception e) {
            this.hibernateTransactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public final void updateCenter(UserContext userContext, CenterUpdate centerUpdate) throws ApplicationException {
        CustomerBO center = customerDao.findCustomerById(centerUpdate.getCustomerId());
        center.validateVersion(centerUpdate.getVersionNum());
        center.setUserContext(userContext);

        if(!centerUpdate.getDisplayName().equals(center.getDisplayName())) {
            customerDao.validateCenterNameIsNotTakenForOffice(centerUpdate.getDisplayName(), center.getOfficeId());
        }

        assembleCustomerPostionsFromDto(centerUpdate.getCustomerPositions(), center);

        try {
            hibernateTransactionHelper.startTransaction();
            hibernateTransactionHelper.beginAuditLoggingFor(center);

            center.setDisplayName(centerUpdate.getDisplayName());
            updateLoanOfficerAndValidate(centerUpdate.getLoanOfficerId(), center);

            center.updateCenterDetails(userContext, centerUpdate);

            customerDao.save(center);

            hibernateTransactionHelper.commitTransaction();
        } catch (ApplicationException e) {
            hibernateTransactionHelper.rollbackTransaction();
            throw e;
        } catch (Exception e) {
            hibernateTransactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            hibernateTransactionHelper.closeSession();
        }
    }

    @Override
    public final void updateGroup(UserContext userContext, GroupUpdate groupUpdate) throws ApplicationException {

        GroupBO group = customerDao.findGroupBySystemId(groupUpdate.getGlobalCustNum());
        group.validateVersion(groupUpdate.getVersionNo());
        group.setUserContext(userContext);

        assembleCustomerPostionsFromDto(groupUpdate.getCustomerPositions(), group);

        try {
            hibernateTransactionHelper.startTransaction();
            hibernateTransactionHelper.beginAuditLoggingFor(group);

            group.updateTrainedDetails(groupUpdate);
            group.setExternalId(groupUpdate.getExternalId());

            Address address = null;
            if (groupUpdate.getAddress() != null) {
                address = new Address(groupUpdate.getAddress().getLine1(), groupUpdate.getAddress().getLine2(), groupUpdate.getAddress().getLine3(),
                        groupUpdate.getAddress().getCity(), groupUpdate.getAddress().getState(), groupUpdate.getAddress().getCountry(),
                        groupUpdate.getAddress().getZip(), groupUpdate.getAddress().getPhoneNumber());
            }

            group.updateAddress(address);

            if (group.isNameDifferent(groupUpdate.getDisplayName())) {
                customerDao.validateGroupNameIsNotTakenForOffice(groupUpdate.getDisplayName(), group.getOffice().getOfficeId());
                group.setDisplayName(groupUpdate.getDisplayName());
            }

            updateLoanOfficerAndValidate(groupUpdate.getLoanOfficerId(), group);

            customerDao.save(group);

            hibernateTransactionHelper.commitTransaction();
        } catch (ApplicationException e) {
            hibernateTransactionHelper.rollbackTransaction();
            throw e;
        } catch (Exception e) {
            hibernateTransactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            hibernateTransactionHelper.closeSession();
        }
    }

    @Override
    public final void updateClientPersonalInfo(UserContext userContext, ClientPersonalInfoUpdate personalInfo) throws CustomerException {

        ClientBO client = (ClientBO) this.customerDao.findCustomerById(personalInfo.getCustomerId());
        client.validateVersion(personalInfo.getOriginalClientVersionNumber());
        client.updateDetails(userContext);
        LocalDate currentDOB = new LocalDate(client.getDateOfBirth());
        LocalDate newDOB = currentDOB;
        try {
            // updating Date of birth
            // doesn''t sound normal but it can be required in certain cases
            // see http://mifosforge.jira.com/browse/MIFOS-4368
            newDOB = new LocalDate(DateUtils.getDateAsSentFromBrowser(personalInfo.getDateOfBirth()));
        } catch (InvalidDateException e) {
            throw new MifosRuntimeException(e);
        }

        if(!currentDOB.isEqual(newDOB)) {
            customerDao.validateClientForDuplicateNameOrGovtId(personalInfo.getClientDisplayName(), newDOB.toDateMidnight().toDate(), personalInfo.getGovernmentId());
        }

        try {
            hibernateTransactionHelper.startTransaction();
            hibernateTransactionHelper.beginAuditLoggingFor(client);
            client.updatePersonalInfo(personalInfo);

            clientPhotoService.update(personalInfo.getCustomerId().longValue(), personalInfo.getPicture());

            customerDao.save(client);

            hibernateTransactionHelper.commitTransaction();
        } catch (ApplicationException e) {
            hibernateTransactionHelper.rollbackTransaction();
            throw new CustomerException(e.getKey(), e);
        } catch (Exception e) {
            hibernateTransactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            hibernateTransactionHelper.commitTransaction();
        }
    }

    @Override
    public void updateClientFamilyInfo(UserContext userContext, ClientFamilyInfoUpdate clientFamilyInfoUpdate)
            throws CustomerException {

        ClientBO client = (ClientBO) this.customerDao.findCustomerById(clientFamilyInfoUpdate.getCustomerId());
        client.validateVersion(clientFamilyInfoUpdate.getOldVersionNum());
        client.updateDetails(userContext);

        try {
            hibernateTransactionHelper.startTransaction();
            hibernateTransactionHelper.beginAuditLoggingFor(client);

            client.updateFamilyInfo(clientFamilyInfoUpdate);

            customerDao.save(client);

            hibernateTransactionHelper.commitTransaction();
        } catch (Exception e) {
            hibernateTransactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            hibernateTransactionHelper.closeSession();
        }
    }

    @Override
    public final void updateClientMfiInfo(UserContext userContext, ClientMfiInfoUpdate clientMfiInfoUpdate) throws CustomerException {

        ClientBO client = (ClientBO) this.customerDao.findCustomerById(clientMfiInfoUpdate.getClientId());
        client.validateVersion(clientMfiInfoUpdate.getOrginalClientVersionNumber());
        client.updateDetails(userContext);

        try {
            hibernateTransactionHelper.startTransaction();
            hibernateTransactionHelper.beginAuditLoggingFor(client);

            PersonnelBO personnel = this.personnelDao.findPersonnelById(clientMfiInfoUpdate.getPersonnelId());
            client.updateMfiInfo(personnel, clientMfiInfoUpdate);

            customerDao.save(client);

            hibernateTransactionHelper.commitTransaction();
        } catch (CustomerException e) {
            hibernateTransactionHelper.rollbackTransaction();
            throw e;
        } catch (Exception e) {
            hibernateTransactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            hibernateTransactionHelper.closeSession();
        }
    }

    private void updateLoanOfficerAndValidate(Short loanOfficerId, CustomerBO customer) throws CustomerException {
        PersonnelBO loanOfficer = personnelDao.findPersonnelById(loanOfficerId);

        if (customer.isLoanOfficerChanged(loanOfficer)) {
            customer.setLoanOfficer(loanOfficer);
            customer.validate();

            customerDao.updateLoanOfficersForAllChildrenAndAccounts(loanOfficerId, customer.getSearchId(), customer.getOffice().getOfficeId());
        } else {
            customer.validate();
        }
    }

    private void assembleCustomerPostionsFromDto(List<CustomerPositionDto> customerPositions, CustomerBO customer) {

        for (CustomerPositionDto positionView : customerPositions) {
            boolean isPositionFound = false;

            for (CustomerPositionEntity positionEntity : customer.getCustomerPositions()) {

                if (positionView.getPositionId().equals(positionEntity.getPosition().getId())) {

                    CustomerBO customerInPosition = null;
                    if (positionView.getCustomerId() != null) {
                        customerInPosition = customerDao.findCustomerById(positionView.getCustomerId());
                    }
                    positionEntity.setCustomer(customerInPosition);
                    isPositionFound = true;
                    break;
                }
            }
            if (!isPositionFound) {

                CustomerBO customerInPosition = null;
                if (positionView.getCustomerId() != null) {
                    customerInPosition = customerDao.findCustomerById(positionView.getCustomerId());
                }
                CustomerPositionEntity customerPosition = new CustomerPositionEntity(new PositionEntity(positionView.getPositionId()), customerInPosition, customer);
                customer.addCustomerPosition(customerPosition);
            }
        }
    }

    @Override
    public final void updateCustomerStatus(UserContext userContext, CustomerStatusUpdate customerStatusUpdate) throws CustomerException {

        CustomerBO customer = this.customerDao.findCustomerById(customerStatusUpdate.getCustomerId());
        customer.validateVersion(customerStatusUpdate.getVersionNum());
        customer.updateDetails(userContext);

        checkPermission(customer, userContext, customerStatusUpdate.getNewStatus(), customerStatusUpdate.getCustomerStatusFlag());

        Short oldStatusId = customer.getCustomerStatus().getId();
        CustomerStatus oldStatus = CustomerStatus.fromInt(oldStatusId);

        PersonnelBO loggedInUser = this.personnelDao.findPersonnelById(userContext.getId());

        CustomerNoteEntity customerNote = new CustomerNoteEntity(customerStatusUpdate.getNotes(), new Date(), loggedInUser, customer);

        if (customer.isGroup()) {
            GroupBO group = (GroupBO) customer;
            updateGroupStatus(group, oldStatus, customerStatusUpdate.getNewStatus(), customerStatusUpdate.getCustomerStatusFlag(), customerNote);
        } else if (customer.isClient()) {
            ClientBO client = (ClientBO) customer;
            updateClientStatus(client, oldStatus, customerStatusUpdate.getNewStatus(), customerStatusUpdate.getCustomerStatusFlag(), customerNote);
        } else {
            CenterBO center = (CenterBO) customer;
            updateCenterStatus(center, customerStatusUpdate.getNewStatus(), customerStatusUpdate.getCustomerStatusFlag(), customerNote);
        }
    }

    private void checkPermission(CustomerBO customerBO, UserContext userContext, CustomerStatus newStatus,
            CustomerStatusFlag statusFlag) throws CustomerException {

        Short statusFlagId = null;
        if (statusFlag != null) {
            statusFlagId = statusFlag.getValue();
        }

        if (null != customerBO.getPersonnel()) {
            this.customerDao.checkPermissionForStatusChange(newStatus.getValue(), userContext, statusFlagId, customerBO.getOfficeId(), customerBO.getPersonnel().getPersonnelId());
        } else {

            this.customerDao.checkPermissionForStatusChange(newStatus.getValue(), userContext, statusFlagId, customerBO.getOfficeId(), userContext.getId());
        }
    }

    @Override
    public final void updateCenterStatus(CenterBO center, CustomerStatus newStatus, CustomerStatusFlag customerStatusFlag,
            CustomerNoteEntity customerNote) throws CustomerException {

        if (newStatus.isCenterInActive()) {

            center.validateChangeToInActive();

            List<CustomerDto> clientsThatAreNotClosedOrCanceled = this.customerDao
                    .findClientsThatAreNotCancelledOrClosed(center.getSearchId(), center.getOffice().getOfficeId());

            List<CustomerDto> groupsThatAreNotClosedOrCancelled = this.customerDao
                    .findGroupsThatAreNotCancelledOrClosed(center.getSearchId(), center.getOffice().getOfficeId());

            if (clientsThatAreNotClosedOrCanceled.size() > 0 || groupsThatAreNotClosedOrCancelled.size() > 0) {
                final String errorMessage = messageLookupHelper.lookupLabel(ConfigurationConstants.GROUP);
                throw new CustomerException(CustomerConstants.ERROR_STATE_CHANGE_EXCEPTION, new Object[] {errorMessage});
            }

        } else if (newStatus.isCenterActive()) {
            center.validateChangeToActive();
            center.validateLoanOfficerIsActive();
        }

        CustomerStatusFlagEntity customerStatusFlagEntity = populateCustomerStatusFlag(customerStatusFlag);

        try {
            hibernateTransactionHelper.startTransaction();
            hibernateTransactionHelper.beginAuditLoggingFor(center);

            center.updateCustomerStatus(newStatus, customerNote, customerStatusFlagEntity);

            customerDao.save(center);

            hibernateTransactionHelper.commitTransaction();
        } catch (Exception e) {
            this.hibernateTransactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            this.hibernateTransactionHelper.closeSession();
        }
    }

    @Override
    public final void updateGroupStatus(GroupBO group, CustomerStatus oldStatus, CustomerStatus newStatus,
            CustomerStatusFlag customerStatusFlag, CustomerNoteEntity customerNote) throws CustomerException {

        validateChangeOfStatusForGroup(group, oldStatus, newStatus);

        CustomerStatusFlagEntity customerStatusFlagEntity = populateCustomerStatusFlag(customerStatusFlag);

        try {
            hibernateTransactionHelper.startTransaction();
            hibernateTransactionHelper.beginAuditLoggingFor(group);

            if (group.isActiveForFirstTime(oldStatus.getValue(), newStatus.getValue())) {
                group.setCustomerActivationDate(new DateTime().toDate());
                group.updateCustomerHierarchy();

                CalendarEvent applicableCalendarEvents = this.holidayDao.findCalendarEventsForThisYearAndNext(group.getOfficeId());
                group.regenerateCustomerFeeSchedule(applicableCalendarEvents);
            }

            Set<CustomerBO> groupChildren = group.getChildren();

            if (oldStatus.isGroupPending() && newStatus.isGroupCancelled() && groupChildren != null) {

                for (CustomerBO child : groupChildren) {

                    ClientBO client = (ClientBO) child;

                    if (client.isPending()) {
                        client.setUserContext(group.getUserContext());
                        hibernateTransactionHelper.beginAuditLoggingFor(client);
                        client.updateCustomerStatus(CustomerStatus.CLIENT_PARTIAL);
                        customerDao.save(client);
                    }
                }
            }

            group.updateCustomerStatus(newStatus, customerNote, customerStatusFlagEntity);

            customerDao.save(group);
            hibernateTransactionHelper.commitTransaction();
        } catch (Exception e) {
            this.hibernateTransactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            this.hibernateTransactionHelper.closeSession();
        }
    }

    private CustomerStatusFlagEntity populateCustomerStatusFlag(CustomerStatusFlag customerStatusFlag)
            throws CustomerException {
        CustomerStatusFlagEntity customerStatusFlagEntity = null;
        if (customerStatusFlag != null) {
            try {
                customerStatusFlagEntity = legacyMasterDao.getPersistentObject(
                        CustomerStatusFlagEntity.class, customerStatusFlag.getValue());
            } catch (PersistenceException e) {
                throw new CustomerException(e);
            }
        }
        return customerStatusFlagEntity;
    }

    private void validateChangeOfStatusForGroup(GroupBO group, CustomerStatus oldStatus, CustomerStatus newStatus)
            throws CustomerException {

        if (newStatus.isGroupActive()) {
            group.validateGroupCanBeActive();
        }

        if (newStatus.isGroupClosed()) {
            group.validateNoActiveAccountExist();

            List<CustomerDto> clientsThatAreNotClosedOrCanceled = this.customerDao
                    .findClientsThatAreNotCancelledOrClosed(group.getSearchId(), group.getOffice().getOfficeId());

            if (clientsThatAreNotClosedOrCanceled.size() > 0) {
                throw new CustomerException(CustomerConstants.ERROR_STATE_CHANGE_EXCEPTION,
                        new Object[] { ApplicationContextProvider.getBean(MessageLookup.class).lookupLabel(ConfigurationConstants.CLIENT) });
            }
        }

        if (oldStatus.isGroupCancelled() && newStatus.isGroupPartial()) {
            if (group.getParentCustomer() != null && group.getParentCustomer().getCustomerId() != null) {
                group.validateTransitionFromCancelledToPartialIsAllowedBasedOnCenter();
            } else {
                this.officeDao.validateBranchIsActiveWithNoActivePersonnel(group.getOffice().getOfficeId(), group
                        .getUserContext());
            }
        }
    }

    @Override
    public final void updateClientStatus(ClientBO client, CustomerStatus oldStatus, CustomerStatus newStatus,
            CustomerStatusFlag customerStatusFlag, CustomerNoteEntity customerNote) throws CustomerException {

        PersonnelBO loggedInUser = this.personnelDao.findPersonnelById(client.getUserContext().getId());
        handeClientChangeOfStatus(client, newStatus);

        CustomerStatusFlagEntity customerStatusFlagEntity = populateCustomerStatusFlag(customerStatusFlag);

        try {
            hibernateTransactionHelper.startTransaction();
            hibernateTransactionHelper.beginAuditLoggingFor(client);

            client.clearCustomerFlagsIfApplicable(oldStatus, newStatus);

            client.updateCustomerStatus(newStatus);
            changeStatus(client, oldStatus, newStatus);

            if (customerStatusFlagEntity != null) {
                client.addCustomerFlag(customerStatusFlagEntity);
            }
            client.addCustomerNotes(customerNote);
            this.handleChangeOfClientStatusToClosedOrCancelled(client, customerStatusFlag, customerNote, loggedInUser);

            customerDao.save(client);

            hibernateTransactionHelper.commitTransaction();
        } catch (ApplicationException e) {
            this.hibernateTransactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getKey(), e);
        } catch (Exception e) {
            this.hibernateTransactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            this.hibernateTransactionHelper.closeSession();
        }
    }

    private void handleChangeOfClientStatusToClosedOrCancelled(ClientBO client, CustomerStatusFlag customerStatusFlag,
            CustomerNoteEntity customerNote, PersonnelBO loggedInUser) throws AccountException {
        if (client.isClosedOrCancelled()) {

            if (client.isClientUnderGroup()) {

                CustomerBO parentCustomer = client.getParentCustomer();

                client.resetPositions(parentCustomer);
                parentCustomer.setUserContext(client.getUserContext());
                CustomerBO center = parentCustomer.getParentCustomer();
                if (center != null) {
                    parentCustomer.resetPositions(center);
                    center.setUserContext(client.getUserContext());
                }
            }

            CustomerAccountBO customerAccount = client.getCustomerAccount();
            if (customerAccount.isOpen()) {
                customerAccount.setUserContext(client.getUserContext());

                customerAccount.changeStatus(AccountState.CUSTOMER_ACCOUNT_INACTIVE, customerStatusFlag.getValue(),
                        customerNote.getComment(), loggedInUser);
                customerAccount.update();
            }
        }
    }

    private void changeStatus(CustomerBO customer, CustomerStatus oldStatus, CustomerStatus newStatus) throws CustomerException {
        Short oldStatusId = oldStatus.getValue();
        Short newStatusId = newStatus.getValue();

        if (customer.isClient()) {

            ClientBO client = (ClientBO) customer;

            if (client.isActiveForFirstTime(oldStatusId, newStatusId)) {
                if (client.getParentCustomer() != null) {
                    CustomerHierarchyEntity hierarchy = new CustomerHierarchyEntity(client, client.getParentCustomer());
                    client.addCustomerHierarchy(hierarchy);
                }

                CalendarEvent applicableCalendarEvents = holidayDao.findCalendarEventsForThisYearAndNext(customer.getOfficeId());

                List<AccountFeesEntity> accountFees = new ArrayList<AccountFeesEntity>(customer.getCustomerAccount().getAccountFees());
                client.getCustomerAccount().createSchedulesAndFeeSchedulesForFirstTimeActiveCustomer(customer, accountFees, customer.getCustomerMeetingValue(), applicableCalendarEvents, new DateMidnight().toDateTime());

                client.setCustomerActivationDate(new DateTimeService().getCurrentJavaDateTime());

                if (client.getOfferingsAssociatedInCreate() != null) {
                    for (ClientInitialSavingsOfferingEntity clientOffering : client.getOfferingsAssociatedInCreate()) {
                        try {
                            SavingsOfferingBO savingsOffering = savingsProductDao.findById(
                                    clientOffering.getSavingsOffering().getPrdOfferingId().intValue());

                            if (savingsOffering.isActive()) {

                                List<CustomFieldDto> customerFieldsForSavings = new ArrayList<CustomFieldDto>();

                                client.addAccount(new SavingsBO(client.getUserContext(), savingsOffering, client,
                                        AccountState.SAVINGS_ACTIVE, savingsOffering.getRecommendedAmount(),
                                        customerFieldsForSavings));
                            }
                        } catch (AccountException pe) {
                            throw new CustomerException(pe);
                        }
                    }
                }

                new SavingsPersistence().persistSavingAccounts(client);

                try {
                    if (client.getParentCustomer() != null) {
                        List<SavingsBO> savingsList = new CustomerPersistence()
                                .retrieveSavingsAccountForCustomer(client.getParentCustomer().getCustomerId());

                        if (client.getParentCustomer().getParentCustomer() != null) {
                            savingsList.addAll(new CustomerPersistence().retrieveSavingsAccountForCustomer(client
                                    .getParentCustomer().getParentCustomer().getCustomerId()));
                        }
                        for (SavingsBO savings : savingsList) {
                            savings.setUserContext(client.getUserContext());

                            if (client.getCustomerMeeting().getMeeting() != null) {
                                if (!(savings.getCustomer().getLevel() == CustomerLevel.GROUP && savings
                                        .getRecommendedAmntUnit().getId().equals(
                                                RecommendedAmountUnit.COMPLETE_GROUP.getValue()))) {

                                    DateTime today = new DateTime().toDateMidnight().toDateTime();
                                    savings.generateDepositAccountActions(client, client.getCustomerMeeting()
                                            .getMeeting(), applicableCalendarEvents.getWorkingDays(), applicableCalendarEvents.getHolidays(), today);

                                    savings.update();
                                }
                            }
                        }
                    }

                } catch (PersistenceException pe) {
                    throw new CustomerException(pe);
                } catch (AccountException ae) {
                    throw new CustomerException(ae);
                }
            }
        }
    }

    private void handeClientChangeOfStatus(ClientBO client, CustomerStatus newStatus) throws CustomerException {
        if (client.getParentCustomer() != null) {

            CustomerStatus groupStatus = client.getParentCustomer().getStatus();

            if ((newStatus.isClientActive() || newStatus.isClientPending()) && client.isClientUnderGroup()) {

                if (groupStatus.isGroupCancelled()) {
                    throw new CustomerException(ClientConstants.ERRORS_GROUP_CANCELLED,
                            new Object[] { ApplicationContextProvider.getBean(MessageLookup.class).lookupLabel(ConfigurationConstants.GROUP) });
                }

                if (client.isGroupStatusLower(newStatus.getValue(), groupStatus.getValue())) {

                    throw new CustomerException(ClientConstants.INVALID_CLIENT_STATUS_EXCEPTION);
                }
            }
        }

        if (newStatus.isClientClosed()) {
            if (client.isAnyLoanAccountOpen() || client.isAnySavingsAccountOpen()) {
                throw new CustomerException(CustomerConstants.CUSTOMER_HAS_ACTIVE_ACCOUNTS_EXCEPTION);
            }
        }

        if (newStatus.isClientActive()) {
            if (client.getPersonnel() == null || client.getPersonnel().getPersonnelId() == null) {
                throw new CustomerException(ClientConstants.CLIENT_LOANOFFICER_NOT_ASSIGNED);
            }

            if (client.getCustomerMeeting() == null || client.getCustomerMeeting().getMeeting() == null) {
                throw new CustomerException(GroupConstants.MEETING_NOT_ASSIGNED);
            }
        }
    }

    @Override
    public final String transferGroupTo(GroupBO group, CenterBO receivingCenter) throws CustomerException {

        group.validateReceivingCenter(receivingCenter);
        group.validateNoActiveAccountsExist();

        if (group.isDifferentBranch(receivingCenter.getOffice())) {
            customerDao.validateGroupNameIsNotTakenForOffice(group.getDisplayName(), receivingCenter.getOfficeId());
        }

        CustomerBO oldParent = group.getParentCustomer();

        try {
            hibernateTransactionHelper.startTransaction();
            hibernateTransactionHelper.beginAuditLoggingFor(group);

            boolean regenerateSchedules = group.transferTo(receivingCenter);

            if (oldParent != null) {
                oldParent.updateDetails(group.getUserContext());
                customerDao.save(oldParent);
            }
            receivingCenter.updateDetails(group.getUserContext());
            customerDao.save(receivingCenter);

            group.updateDetails(group.getUserContext());
            customerDao.save(group);

            Set<CustomerBO> clients = group.getChildren();

            for (CustomerBO client : clients) {
                client.setUserContext(group.getUserContext());
                ((ClientBO) client).handleGroupTransfer();
                client.setUpdateDetails();
                customerDao.save(client);
            }
            hibernateTransactionHelper.flushSession();

            GroupBO groupInitialised = group;
            if (regenerateSchedules) {
                CalendarEvent calendarEvents = holidayDao.findCalendarEventsForThisYearAndNext(group.getOfficeId());
                groupInitialised = customerDao.findGroupBySystemId(group.getGlobalCustNum());
                handleChangeInMeetingSchedule(groupInitialised, calendarEvents.getWorkingDays(), calendarEvents.getHolidays());
            }
            hibernateTransactionHelper.commitTransaction();
            return groupInitialised.getGlobalCustNum();
        } catch (ApplicationException e) {
            this.hibernateTransactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getKey(), e);
        } catch (Exception e) {
            this.hibernateTransactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            this.hibernateTransactionHelper.closeSession();
        }
    }

    @Override
    public ClientBO transferClientTo(UserContext userContext, Integer groupId, String clientGlobalCustNum, Integer previousClientVersionNo) throws CustomerException {

        ClientBO client = customerDao.findClientBySystemId(clientGlobalCustNum);
        client.validateVersion(previousClientVersionNo);
        client.validateIsSameGroup(groupId);
        client.updateDetails(userContext);

        GroupBO receivingGroup = (GroupBO) customerDao.findCustomerById(groupId);
        client.validateReceivingGroup(receivingGroup);
        client.validateForActiveAccounts();
        client.validateForPeriodicFees();

        	
        CustomerBO oldParent = client.getParentCustomer();

        try {
            hibernateTransactionHelper.startTransaction();
            hibernateTransactionHelper.beginAuditLoggingFor(client);

            boolean regenerateSchedules = client.transferTo(receivingGroup);

            if (oldParent != null) {
                client.resetPositions(oldParent);
                oldParent.updateDetails(client.getUserContext());

                if (oldParent.getParentCustomer() != null) {
                    CustomerBO center = oldParent.getParentCustomer();
                    client.resetPositions(center);
                    center.setUserContext(client.getUserContext());
                }

                customerDao.save(oldParent);
            }

            receivingGroup.updateDetails(client.getUserContext());
            customerDao.save(receivingGroup);

            client.updateDetails(userContext);
            customerDao.save(client);

            hibernateTransactionHelper.flushAndClearSession();

            if (regenerateSchedules) {
                client = customerDao.findClientBySystemId(clientGlobalCustNum);
                CalendarEvent calendarEvents = holidayDao.findCalendarEventsForThisYearAndNext(client.getOfficeId());
                handleChangeInMeetingSchedule(client, calendarEvents.getWorkingDays(), calendarEvents.getHolidays());
            }
            hibernateTransactionHelper.commitTransaction();
            return client;
        } catch (ApplicationException e) {
            this.hibernateTransactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getKey(), e);
        } catch (Exception e) {
            this.hibernateTransactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            this.hibernateTransactionHelper.closeSession();
        }
    }

    @Override
    public void transferClientTo(ClientBO client, OfficeBO receivingBranch) {

        try {
            this.hibernateTransactionHelper.startTransaction();
            this.hibernateTransactionHelper.beginAuditLoggingFor(client);
            client.transferToBranch(receivingBranch);

            if (client.getParentCustomer() != null) {
                CustomerBO parent = client.getParentCustomer();
                parent.incrementChildCount();
            }

            this.hibernateTransactionHelper.flushSession();
            client.generateSearchId();
            this.customerDao.save(client);

            if (client.getParentCustomer() != null) {
                this.customerDao.save(client.getParentCustomer());
            }
            this.hibernateTransactionHelper.commitTransaction();
        } catch (ApplicationException e) {
            this.hibernateTransactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getKey(), e);
        } catch (Exception e) {
            this.hibernateTransactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            this.hibernateTransactionHelper.closeSession();
        }
    }

    @Override
    public final String transferGroupTo(GroupBO group, OfficeBO transferToOffice) throws CustomerException {

        group.validateNewOffice(transferToOffice);
        group.validateNoActiveAccountsExist();

        customerDao.validateGroupNameIsNotTakenForOffice(group.getDisplayName(), transferToOffice.getOfficeId());

        try {
            hibernateTransactionHelper.startTransaction();
            hibernateTransactionHelper.beginAuditLoggingFor(group);

            group.makeCustomerMovementEntries(transferToOffice);
            group.setPersonnel(null);
            if (group.isActive()) {
                group.setCustomerStatus(new CustomerStatusEntity(CustomerStatus.GROUP_HOLD));
            }

            CustomerBO oldParentOfGroup = group.getParentCustomer();
            if (oldParentOfGroup != null) {
                oldParentOfGroup.incrementChildCount();
                customerDao.save(oldParentOfGroup);
            }

            this.hibernateTransactionHelper.flushSession();
            group.generateSearchId();
            group.setUpdateDetails();
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

            hibernateTransactionHelper.commitTransaction();

            return group.getGlobalCustNum();
        } catch (Exception e) {
            hibernateTransactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            hibernateTransactionHelper.closeSession();
        }
    }

    @Override
    public void updateCustomerMeetingSchedule(MeetingBO updatedMeeting, CustomerBO customer) {

        try {
            customer.validateIsTopOfHierarchy();
            customerDao.checkPermissionForEditMeetingSchedule(updatedMeeting.getUserContext(), customer);

            CalendarEvent calendarEvents = holidayDao.findCalendarEventsForThisYearAndNext(customer.getOfficeId());

            this.hibernateTransactionHelper.startTransaction();

            boolean scheduleUpdateRequired = false;
            CustomerMeetingEntity meetingEntity = customer.getCustomerMeeting();
            if (meetingEntity != null) {
                MeetingBO meeting = customer.getCustomerMeetingValue();
                scheduleUpdateRequired = updateMeeting(meeting, updatedMeeting);
            } else {
                CustomerMeetingEntity newMeetingEntity = customer.createCustomerMeeting(updatedMeeting);
                customer.setCustomerMeeting(newMeetingEntity);
            }
            customerDao.save(customer);

            if (scheduleUpdateRequired) {
                handleChangeInMeetingSchedule(customer, calendarEvents.getWorkingDays(), calendarEvents.getHolidays());
            }

            this.hibernateTransactionHelper.commitTransaction();
        } catch (CustomerException e) {
            this.hibernateTransactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getKey(), e);
        } catch (AccountException e) {
            this.hibernateTransactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getKey(), e);
        } finally {
            this.hibernateTransactionHelper.closeSession();
        }
    }

    private boolean updateMeeting(final MeetingBO oldMeeting, final MeetingBO updatedDetails) throws CustomerException {
        boolean isRegenerationOfSchedulesRequired = false;

        try {
            if (oldMeeting.isWeekly()) {
                oldMeeting.setMeetingStartDate(updatedDetails.getMeetingStartDate());
                WeekDay dayOfWeek = updatedDetails.getMeetingDetails().getWeekDay();
                isRegenerationOfSchedulesRequired = oldMeeting.isDayOfWeekDifferent(dayOfWeek);
                oldMeeting.update(dayOfWeek.getValue(), updatedDetails.getMeetingPlace());
                oldMeeting.update(dayOfWeek, updatedDetails.getMeetingPlace());
            } else if (oldMeeting.isMonthlyOnDate()) {
                isRegenerationOfSchedulesRequired = oldMeeting.isDayOfMonthDifferent(updatedDetails.getMeetingDetails().getDayNumber());
                oldMeeting.update(updatedDetails.getMeetingDetails().getDayNumber(), updatedDetails.getMeetingPlace());
            } else if (oldMeeting.isMonthly()) {
                RankOfDay rankOfday = updatedDetails.getMeetingDetails().getWeekRank();
//                WeekDay weekOfMonth = WeekDay.getWeekDay(updatedDetails.getMonthWeek());
                WeekDay weekOfMonth = updatedDetails.getMeetingDetails().getWeekDay();
                isRegenerationOfSchedulesRequired = oldMeeting.isWeekOfMonthDifferent(rankOfday, weekOfMonth);
                oldMeeting.update(weekOfMonth, rankOfday, updatedDetails.getMeetingPlace());
            }
        } catch (MeetingException me) {
            throw new CustomerException(me);
        }

        return isRegenerationOfSchedulesRequired;
    }

    private void handleChangeInMeetingSchedule(CustomerBO customer, final List<Days> workingDays, final List<Holiday> orderedUpcomingHolidays) throws AccountException {

        boolean lsimEnabled = this.configurationHelper.isLoanScheduleRepaymentIndependentOfCustomerMeetingEnabled();

        Set<AccountBO> accounts = customer.getAccounts();
        for (AccountBO account : accounts) {
            if (account instanceof LoanBO && lsimEnabled) {
                // do not change schedules when LSIm is on for loan accounts
            } else {
                account.handleChangeInMeetingSchedule(workingDays, orderedUpcomingHolidays, customer.isTopOfHierarchy());
                customerDao.save(account);
            }
        }

        for (CustomerBO child : customer.getChildren()) {
            handleChangeInMeetingSchedule(child, workingDays, orderedUpcomingHolidays);
        }
    }

    @Override
    public void removeGroupMembership(ClientBO client, PersonnelBO loanOfficer, CustomerNoteEntity accountNotesEntity, Short localeId) {

        if (client.hasActiveLoanAccounts()) {
            throw new BusinessRuleException(CustomerConstants.CLIENT_HAS_ACTIVE_ACCOUNTS_EXCEPTION);
        }    

        if (client.getParentCustomer() != null) {

            boolean glimEnabled = getConfigurationPersistence().isGlimEnabled();

            if (glimEnabled) {
                if (customerIsMemberOfAnyExistingGlimLoanAccount(client, client.getParentCustomer())) {
                    throw new BusinessRuleException(CustomerConstants.GROUP_HAS_ACTIVE_ACCOUNTS_EXCEPTION);
                }
            } else if (client.getParentCustomer().hasActiveLoanAccounts()) {
                // not glim - then disallow removing client from group with active account
                throw new BusinessRuleException(CustomerConstants.GROUP_HAS_ACTIVE_ACCOUNTS_EXCEPTION);
            }
        }

        try {
            this.hibernateTransactionHelper.startTransaction();
            this.hibernateTransactionHelper.beginAuditLoggingFor(client);

            client.addCustomerNotes(accountNotesEntity);

            client.resetPositions(client.getParentCustomer());
            client.getParentCustomer().updateDetails(client.getUserContext());
            this.customerDao.save(client.getParentCustomer());

            this.hibernateTransactionHelper.flushSession();

            client.setPersonnel(loanOfficer);
            client.setParentCustomer(null);
            client.removeGroupMembership();
            client.generateSearchId();
            this.customerDao.save(client);
            this.hibernateTransactionHelper.commitTransaction();
        } catch (Exception e) {
            this.hibernateTransactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            this.hibernateTransactionHelper.closeSession();
        }
    }



    private boolean customerIsMemberOfAnyExistingGlimLoanAccount(CustomerBO customerToRemoveFromGroup, CustomerBO customerWithActiveAccounts) {

        List<AccountBO> activeLoanAccounts = customerDao.findGLIMLoanAccountsApplicableTo(customerToRemoveFromGroup.getCustomerId(), customerWithActiveAccounts.getCustomerId());
        
        
        for (int i = activeLoanAccounts.size() -1 ; i >=0; i--) {
            AccountBO glim = activeLoanAccounts.get(i);
            if (glim.getAccountState().isLoanClosedObligationsMet() || 
                    glim.getAccountState().isLoanClosedWrittenOff() ||
                    glim.getAccountState().isLoanClosedReschedule() ||
                    glim.getAccountState().isLoanCanceled()) {
                activeLoanAccounts.remove(i);
            }
        }
        return !activeLoanAccounts.isEmpty();
    }

    public void setConfigurationHelper(MifosConfigurationHelper configurationHelper) {
        this.configurationHelper = configurationHelper;
    }
}