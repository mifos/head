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

package org.mifos.application.servicefacade;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeeView;
import org.mifos.accounts.fees.persistence.FeePersistence;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.persistence.SavingsPersistence;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.SpouseFatherLookupEntity;
import org.mifos.application.master.business.ValueListElement;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.calendar.CalendarUtils;
import org.mifos.config.ClientRules;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.config.util.helpers.ConfigurationConstants;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerCustomFieldEntity;
import org.mifos.customers.business.CustomerNoteEntity;
import org.mifos.customers.business.CustomerPositionEntity;
import org.mifos.customers.business.CustomerPositionView;
import org.mifos.customers.business.CustomerStatusEntity;
import org.mifos.customers.business.CustomerStatusFlagEntity;
import org.mifos.customers.business.CustomerView;
import org.mifos.customers.business.PositionEntity;
import org.mifos.customers.business.service.CustomerBusinessService;
import org.mifos.customers.business.service.CustomerService;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.center.struts.action.OfficeHierarchyDto;
import org.mifos.customers.center.struts.actionforms.CenterCustActionForm;
import org.mifos.customers.center.util.helpers.CenterConstants;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.client.business.ClientInitialSavingsOfferingEntity;
import org.mifos.customers.client.util.helpers.ClientConstants;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.group.struts.actionforms.GroupCustActionForm;
import org.mifos.customers.group.util.helpers.CenterSearchInput;
import org.mifos.customers.group.util.helpers.GroupConstants;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.business.OfficeView;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.office.persistence.OfficeDto;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.business.PersonnelView;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.LocalizationConverter;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.security.util.ActivityMapper;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;

public class CustomerServiceFacadeWebTier implements CustomerServiceFacade {

    private final OfficeDao officeDao;
    private final PersonnelDao personnelDao;
    private final CustomerDao customerDao;
    private final CustomerService customerService;

    public CustomerServiceFacadeWebTier(CustomerService customerService, OfficeDao officeDao,
            PersonnelDao personnelDao, CustomerDao customerDao) {
        this.customerService = customerService;
        this.officeDao = officeDao;
        this.personnelDao = personnelDao;
        this.customerDao = customerDao;
    }

    @Override
    public OnlyBranchOfficeHierarchyDto retrieveBranchOnlyOfficeHierarchy(UserContext userContext) {

        OfficeDto office = officeDao.findOfficeDtoById(userContext.getBranchId());
        List<OfficeBO> branchParents = officeDao.findBranchsOnlyWithParentsMatching(office.getSearchId());
        List<OfficeView> levels = officeDao.findActiveOfficeLevels();

        List<OfficeHierarchyDto> branchOnlyOfficeHierarchy = OfficeBO
                .convertToBranchOnlyHierarchyWithParentsOfficeHierarchy(branchParents);

        return new OnlyBranchOfficeHierarchyDto(userContext.getPreferredLocale(), levels, office.getSearchId(),
                branchOnlyOfficeHierarchy);
    }

    @Override
    public CenterFormCreationDto retrieveCenterFormCreationData(CenterCreation centerCreation, UserContext userContext) {

        List<PersonnelView> activeLoanOfficersForBranch = personnelDao.findActiveLoanOfficersForOffice(centerCreation);

        List<CustomFieldDefinitionEntity> customFieldDefinitions = customerDao.retrieveCustomFieldEntitiesForCenter();
        List<CustomFieldView> customFieldViews = CustomFieldDefinitionEntity.toDto(customFieldDefinitions, userContext
                .getPreferredLocale());

        List<FeeBO> fees = customerDao.retrieveFeesApplicableToCenters();
        CustomerApplicableFeesDto applicableFees = CustomerApplicableFeesDto.toDto(fees, userContext);

        return new CenterFormCreationDto(activeLoanOfficersForBranch, customFieldViews, applicableFees
                .getAdditionalFees(), applicableFees.getDefaultFees());
    }

    @Override
    public GroupFormCreationDto retrieveGroupFormCreationData(GroupCreation groupCreation) {

        CustomerBO parentCustomer = null;
        CustomerApplicableFeesDto applicableFees = CustomerApplicableFeesDto.empty();
        List<PersonnelView> personnelList = new ArrayList<PersonnelView>();

        boolean isCenterHierarchyExists = ClientRules.getCenterHierarchyExists();
        if (isCenterHierarchyExists) {
            parentCustomer = this.customerDao.findCenterBySystemId(groupCreation.getParentSystemId());

            MeetingBO customerMeeting = parentCustomer.getCustomerMeetingValue();
            List<FeeBO> fees = customerDao.retrieveFeesApplicableToGroupsRefinedBy(customerMeeting);
            applicableFees = CustomerApplicableFeesDto.toDto(fees, groupCreation.getUserContext());
        } else {
            CenterCreation centerCreation = new CenterCreation(groupCreation.getOfficeId(), groupCreation.getUserId(),
                    groupCreation.getUserLevelId(), groupCreation.getPreferredLocale());
            personnelList = this.personnelDao.findActiveLoanOfficersForOffice(centerCreation);

            List<FeeBO> fees = customerDao.retrieveFeesApplicableToGroups();
            applicableFees = CustomerApplicableFeesDto.toDto(fees, groupCreation.getUserContext());
        }

        List<CustomFieldDefinitionEntity> customFieldDefinitions = customerDao.retrieveCustomFieldEntitiesForGroup();
        List<CustomFieldView> customFieldViews = CustomFieldDefinitionEntity.toDto(customFieldDefinitions,
                groupCreation.getPreferredLocale());
        List<PersonnelView> formedByPersonnel = customerDao
                .findLoanOfficerThatFormedOffice(groupCreation.getOfficeId());

        return new GroupFormCreationDto(isCenterHierarchyExists, parentCustomer, customFieldViews, personnelList,
                formedByPersonnel, applicableFees);
    }

    @Override
    public ClientFormCreationDto retrieveClientFormCreationData(UserContext userContext) {

        List<ValueListElement> salutations = this.customerDao.retrieveSalutations();
        List<ValueListElement> genders = this.customerDao.retrieveGenders();
        List<ValueListElement> maritalStatuses = this.customerDao.retrieveMaritalStatuses();
        List<ValueListElement> citizenship = this.customerDao.retrieveCitizenship();
        List<ValueListElement> ethinicity = this.customerDao.retrieveEthinicity();
        List<ValueListElement> educationLevels = this.customerDao.retrieveEducationLevels();
        List<ValueListElement> businessActivity = this.customerDao.retrieveBusinessActivities();
        List<ValueListElement> poverty = this.customerDao.retrievePoverty();
        List<ValueListElement> handicapped = this.customerDao.retrieveHandicapped();

        List<CustomFieldDefinitionEntity> customFieldDefinitions = customerDao.retrieveCustomFieldEntitiesForClient();
        List<CustomFieldView> customFieldViews = CustomFieldDefinitionEntity.toDto(customFieldDefinitions, userContext
                .getPreferredLocale());

        try {
            List<MasterDataEntity> spouseFather = new MasterPersistence().retrieveMasterEntities(
                    SpouseFatherLookupEntity.class, userContext.getLocaleId());

            return new ClientFormCreationDto(salutations, genders, maritalStatuses, citizenship, ethinicity,
                    educationLevels, businessActivity, poverty, handicapped, spouseFather, customFieldViews);
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public CenterDetailsDto createNewCenter(CenterCustActionForm actionForm, MeetingBO meeting, UserContext userContext) {

        try {
            List<CustomFieldView> customFields = actionForm.getCustomFields();
            CustomFieldView.convertCustomFieldDateToUniformPattern(customFields, userContext.getPreferredLocale());
            DateTime mfiJoiningDate = CalendarUtils.getDateFromString(actionForm.getMfiJoiningDate(), userContext
                    .getPreferredLocale());

            String centerName = actionForm.getDisplayName();
            String externalId = actionForm.getExternalId();
            Address centerAddress = actionForm.getAddress();
            PersonnelBO loanOfficer = this.personnelDao.findPersonnelById(actionForm.getLoanOfficerIdValue());
            OfficeBO centerOffice = this.officeDao.findOfficeById(actionForm.getOfficeIdValue());

            int numberOfCustomersInOfficeAlready = new CustomerPersistence().getCustomerCountForOffice(
                    CustomerLevel.CENTER, actionForm.getOfficeIdValue());

            List<CustomerCustomFieldEntity> customerCustomFields = CustomerCustomFieldEntity
                    .fromDto(customFields, null);

            List<AccountFeesEntity> feesForCustomerAccount = new ArrayList<AccountFeesEntity>();
            List<FeeView> feesToApply = actionForm.getFeesToApply();
            for (FeeView feeView : feesToApply) {
                FeeBO fee = new FeePersistence().getFee(feeView.getFeeIdValue());
                Double feeAmount = new LocalizationConverter().getDoubleValueForCurrentLocale(feeView.getAmount());
                AccountFeesEntity accountFee = new AccountFeesEntity(null, fee, feeAmount);
                feesForCustomerAccount.add(accountFee);
            }

            CenterBO center = CenterBO.createNew(userContext, centerName, mfiJoiningDate, meeting, loanOfficer,
                    centerOffice, numberOfCustomersInOfficeAlready, customerCustomFields, centerAddress, externalId);

            this.customerService.create(center, meeting, feesForCustomerAccount);

            return new CenterDetailsDto(center.getCustomerId(), center.getGlobalCustNum());
        } catch (InvalidDateException e) {
            throw new IllegalStateException(e);
        } catch (PersistenceException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public CenterDetailsDto createNewGroup(GroupCustActionForm actionForm, MeetingBO meeting, UserContext userContext)
            throws CustomerException {

        GroupBO group;

        try {

            List<AccountFeesEntity> feesForCustomerAccount = new ArrayList<AccountFeesEntity>();
            List<FeeView> feesToApply = actionForm.getFeesToApply();
            for (FeeView feeView : feesToApply) {
                FeeBO fee = new FeePersistence().getFee(feeView.getFeeIdValue());
                Double feeAmount = new LocalizationConverter().getDoubleValueForCurrentLocale(feeView.getAmount());
                AccountFeesEntity accountFee = new AccountFeesEntity(null, fee, feeAmount);
                feesForCustomerAccount.add(accountFee);
            }

            List<CustomFieldView> customFields = actionForm.getCustomFields();
            CustomFieldView.convertCustomFieldDateToUniformPattern(customFields, userContext.getPreferredLocale());

            List<CustomerCustomFieldEntity> customerCustomFields = CustomerCustomFieldEntity
                    .fromDto(customFields, null);

            PersonnelBO formedBy = this.personnelDao.findPersonnelById(actionForm.getFormedByPersonnelValue());

            Short loanOfficerId;
            Short officeId;
            Short customerStatusId = actionForm.getStatusValue().getValue();

            String groupName = actionForm.getDisplayName();
            CustomerStatus customerStatus = actionForm.getStatusValue();
            String externalId = actionForm.getExternalId();
            boolean trained = actionForm.isCustomerTrained();
            DateTime trainedOn = new DateTime(actionForm.getTrainedDateValue(userContext.getPreferredLocale()));
            Address address = actionForm.getAddress();
            MeetingBO groupMeeting = meeting;

            if (ClientRules.getCenterHierarchyExists()) {

                // create group without center as parent
                CustomerBO parentCustomer = actionForm.getParentCustomer();
                loanOfficerId = parentCustomer.getPersonnel().getPersonnelId();
                officeId = parentCustomer.getOffice().getOfficeId();

                checkPermissionForCreate(actionForm.getStatusValue().getValue(), userContext, officeId, loanOfficerId);
                groupMeeting = parentCustomer.getCustomerMeetingValue();

                group = GroupBO.createGroupWithCenterAsParent(userContext, groupName, formedBy, parentCustomer,
                        customerCustomFields, address, externalId, trained, trainedOn, customerStatus);
            } else {

                // create group with center
                loanOfficerId = actionForm.getLoanOfficerIdValue() != null ? actionForm.getLoanOfficerIdValue()
                        : userContext.getId();
                officeId = actionForm.getOfficeIdValue();

                checkPermissionForCreate(customerStatusId, userContext, officeId, loanOfficerId);

                OfficeBO office = this.officeDao.findOfficeById(actionForm.getOfficeIdValue());
                PersonnelBO loanOfficer = this.personnelDao.findPersonnelById(actionForm.getLoanOfficerIdValue());

                int numberOfCustomersInOfficeAlready = new CustomerPersistence().getCustomerCountForOffice(
                        CustomerLevel.GROUP, officeId);
                String searchId = GroupConstants.PREFIX_SEARCH_STRING
                        + String.valueOf(numberOfCustomersInOfficeAlready + 1);

                group = GroupBO.createGroupAsTopOfCustomerHierarchy(userContext, groupName, formedBy, groupMeeting,
                        loanOfficer, office, customerCustomFields, address, externalId, trained, trainedOn,
                        customerStatus, searchId);
            }

            this.customerService.createGroup(group, groupMeeting, feesForCustomerAccount);

            return new CenterDetailsDto(group.getCustomerId(), group.getGlobalCustNum());
        } catch (InvalidDateException e) {
            throw new MifosRuntimeException(e);
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    private void checkPermissionForCreate(Short newState, UserContext userContext, Short recordOfficeId,
            Short recordLoanOfficerId) {
        if (!isPermissionAllowed(newState, userContext, recordOfficeId, recordLoanOfficerId)) {
            throw new MifosRuntimeException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
        }
    }

    private boolean isPermissionAllowed(Short newState, UserContext userContext, Short recordOfficeId,
            Short recordLoanOfficerId) {
        return ActivityMapper.getInstance().isSavePermittedForCustomer(newState.shortValue(), userContext,
                recordOfficeId, recordLoanOfficerId);
    }

    @Override
    public CenterDto retrieveCenterDetailsForUpdate(Integer centerId, UserContext userContext) {

        CustomerBO center = customerDao.findCustomerById(centerId);

        Short officeId = center.getOffice().getOfficeId();
        String searchId = center.getSearchId();
        Short loanOfficerId = extractLoanOfficerId(center);

        CenterCreation centerCreation = new CenterCreation(officeId, userContext.getId(), userContext.getLevelId(),
                userContext.getPreferredLocale());
        List<PersonnelView> activeLoanOfficersForBranch = personnelDao.findActiveLoanOfficersForOffice(centerCreation);

        List<CustomerView> customerList = customerDao.findClientsThatAreNotCancelledOrClosed(searchId, officeId);

        List<CustomerPositionView> customerPositionViews = generateCustomerPositionViews(center, userContext
                .getLocaleId());

        List<CustomFieldDefinitionEntity> fieldDefinitions = customerDao.retrieveCustomFieldEntitiesForCenter();
        List<CustomFieldView> customFieldViews = CustomerCustomFieldEntity.toDto(center.getCustomFields(),
                fieldDefinitions, userContext);

        DateTime mfiJoiningDate = new DateTime();
        String mfiJoiningDateAsString = "";
        if (center.getMfiJoiningDate() != null) {
            mfiJoiningDate = new DateTime(center.getMfiJoiningDate());
            mfiJoiningDateAsString = DateUtils.getUserLocaleDate(userContext.getPreferredLocale(), center
                    .getMfiJoiningDate().toString());
        }

        return new CenterDto(loanOfficerId, center.getCustomerId(), center.getGlobalCustNum(), mfiJoiningDate,
                mfiJoiningDateAsString, center.getExternalId(), center.getAddress(), customerPositionViews,
                customFieldViews, customerList, center, activeLoanOfficersForBranch, true);
    }

    @Override
    public CenterDto retrieveGroupDetailsForUpdate(String globalCustNum, UserContext userContext) {

        List<PersonnelView> activeLoanOfficersForBranch = new ArrayList<PersonnelView>();

        GroupBO group = this.customerDao.findGroupBySystemId(globalCustNum);

        Short officeId = group.getOffice().getOfficeId();
        String searchId = group.getSearchId();
        Short loanOfficerId = extractLoanOfficerId(group);

        boolean isCenterHierarchyExists = ClientRules.getCenterHierarchyExists();
        if (!isCenterHierarchyExists) {

            CenterCreation centerCreation = new CenterCreation(officeId, userContext.getId(), userContext.getLevelId(),
                    userContext.getPreferredLocale());
            activeLoanOfficersForBranch = personnelDao.findActiveLoanOfficersForOffice(centerCreation);
        }

        List<CustomerView> customerList = customerDao.findClientsThatAreNotCancelledOrClosed(searchId, officeId);

        List<CustomerPositionView> customerPositionViews = generateCustomerPositionViews(group, userContext
                .getLocaleId());

        List<CustomFieldDefinitionEntity> fieldDefinitions = customerDao.retrieveCustomFieldEntitiesForGroup();
        List<CustomFieldView> customFieldViews = CustomerCustomFieldEntity.toDto(group.getCustomFields(),
                fieldDefinitions, userContext);

        DateTime mfiJoiningDate = new DateTime();
        String mfiJoiningDateAsString = "";
        if (group.getMfiJoiningDate() != null) {
            mfiJoiningDate = new DateTime(group.getMfiJoiningDate());
            mfiJoiningDateAsString = DateUtils.getUserLocaleDate(userContext.getPreferredLocale(), group
                    .getMfiJoiningDate().toString());
        }

        return new CenterDto(loanOfficerId, group.getCustomerId(), group.getGlobalCustNum(), mfiJoiningDate,
                mfiJoiningDateAsString, group.getExternalId(), group.getAddress(), customerPositionViews,
                customFieldViews, customerList, group, activeLoanOfficersForBranch, isCenterHierarchyExists);
    }

    private List<CustomerPositionView> generateCustomerPositionViews(CustomerBO customer, Short localeId) {

        try {
            List<MasterDataEntity> customerPositions = new MasterPersistence().retrieveMasterEntities(
                    PositionEntity.class, localeId);

            List<CustomerPositionView> customerPositionViews = new ArrayList<CustomerPositionView>();
            for (MasterDataEntity position : customerPositions) {
                for (CustomerPositionEntity entity : customer.getCustomerPositions()) {
                    if (position.getId().equals(entity.getPosition().getId())) {
                        if (entity.getCustomer() != null) {
                            customerPositionViews.add(new CustomerPositionView(entity.getCustomer().getCustomerId(),
                                    entity.getPosition().getId()));
                        } else {
                            customerPositionViews.add(new CustomerPositionView(null, entity.getPosition().getId()));
                        }
                    }
                }
            }

            return customerPositionViews;
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    private Short extractLoanOfficerId(CustomerBO customer) {
        Short loanOfficerId = null;
        PersonnelBO loanOfficer = customer.getPersonnel();
        if (loanOfficer != null) {
            loanOfficerId = loanOfficer.getPersonnelId();
        }
        return loanOfficerId;
    }

    @Override
    public void updateCenter(UserContext userContext, CenterUpdate centerUpdate) {

        try {
            DateTime mfiJoiningDate = null;
            if (centerUpdate.getMfiJoiningDate() != null) {
                mfiJoiningDate = CalendarUtils.getDateFromString(centerUpdate.getMfiJoiningDate(), userContext
                        .getPreferredLocale());
                centerUpdate.setMfiJoiningDateAsDateTime(mfiJoiningDate);
            }

            CustomerBO customer = customerDao.findCustomerById(centerUpdate.getCustomerId());

            checkVersionMismatch(centerUpdate.getVersionNum(), customer.getVersionNo());

            CenterBO center = (CenterBO) customer;
            customerService.updateCenter(userContext, centerUpdate, center);
        } catch (Exception e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public void updateGroup(UserContext userContext, GroupUpdate groupUpdate) {

        try {
            GroupBO groupBO = this.customerDao.findGroupBySystemId(groupUpdate.getGlobalCustNum());

            checkVersionMismatch(groupUpdate.getVersionNo(), groupBO.getVersionNo());

            customerService.updateGroup(userContext, groupUpdate, groupBO);
        } catch (Exception e) {
            throw new MifosRuntimeException(e);
        }
    }

    private void checkVersionMismatch(Integer oldVersionNum, Integer newVersionNum) throws ApplicationException {
        if (!oldVersionNum.equals(newVersionNum)) {
            throw new ApplicationException(Constants.ERROR_VERSION_MISMATCH);
        }
    }

    @Override
    public CustomerSearch search(String searchString, UserContext userContext) {

        if (searchString == null) {
            throw new MifosRuntimeException(CenterConstants.NO_SEARCH_STRING);
        }

        String officeId = userContext.getBranchId().toString();
        String officeName = this.officeDao.findOfficeDtoById(userContext.getBranchId()).getName();

        String normalisedSearchString = org.mifos.framework.util.helpers.SearchUtils
                .normalizeSearchString(searchString);

        if (normalisedSearchString.equals("")) {
            throw new MifosRuntimeException(CenterConstants.NO_SEARCH_STRING);
        }

        PersonnelBO user = personnelDao.findPersonnelById(userContext.getId());

        QueryResult searchResult = customerDao.search(normalisedSearchString, user);

        return new CustomerSearch(searchResult, searchString, officeId, officeName);
    }

    @Override
    public CenterHierarchySearchDto isCenterHierarchyConfigured(Short loggedInUserBranch) {

        boolean isCenterHierarchyExists = ClientRules.getCenterHierarchyExists();
        CenterSearchInput searchInputs = new CenterSearchInput(loggedInUserBranch, GroupConstants.CREATE_NEW_GROUP);

        return new CenterHierarchySearchDto(isCenterHierarchyExists, searchInputs);
    }

    @Override
    public GroupBO transferGroupToCenter(String groupSystemId, String centerSystemId, UserContext userContext,
            Integer previousGroupVersionNo) {

        try {
            CenterBO transferToCenter = this.customerDao.findCenterBySystemId(centerSystemId);
            transferToCenter.setUserContext(userContext);

            GroupBO group = this.customerDao.findGroupBySystemId(groupSystemId);

            checkVersionMismatch(previousGroupVersionNo, group.getVersionNo());
            group.setUserContext(userContext);

            GroupBO transferedGroup = this.customerService.transferGroupTo(group, transferToCenter);

            return transferedGroup;
        } catch (Exception e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public GroupBO transferGroupToBranch(String globalCustNum, Short officeId, UserContext userContext,
            Integer previousGroupVersionNo) {
        try {
            OfficeBO transferToOffice = this.officeDao.findOfficeById(officeId);
            transferToOffice.setUserContext(userContext);

            GroupBO group = this.customerDao.findGroupBySystemId(globalCustNum);

            checkVersionMismatch(previousGroupVersionNo, group.getVersionNo());
            group.setUserContext(userContext);

            GroupBO transferedGroup = this.customerService.transferGroupTo(group, transferToOffice);

            return transferedGroup;
        } catch (Exception e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public void updateCustomerStatus(Integer customerId, Integer previousCustomerVersionNo, String flagIdAsString,
            String newStatusIdAsString, String notes, UserContext userContext) throws CustomerException {

        CustomerBO customerBO = this.customerDao.findCustomerById(customerId);

        try {
            checkVersionMismatch(previousCustomerVersionNo, customerBO.getVersionNo());
        } catch (ApplicationException e) {
            throw new MifosRuntimeException(e);
        }

        customerBO.setUserContext(userContext);
        Short flagId = null;
        Short newStatusId = null;
        if (StringUtils.isNotBlank(flagIdAsString)) {
            flagId = Short.valueOf(flagIdAsString);
        }
        if (StringUtils.isNotBlank(newStatusIdAsString)) {
            newStatusId = Short.valueOf(newStatusIdAsString);
        }
        checkPermission(customerBO, userContext, newStatusId, flagId);

        // pull out all commom stuff here...
        if (customerBO.isGroup()) {

            GroupBO group = (GroupBO) customerBO;

            Short oldStatusId = group.getCustomerStatus().getId();
            changeStatus(group, newStatusId, flagId, notes, userContext);

            Set<CustomerBO> groupChildren = group.getChildren();

            // if group transitions from GROUP_PENDING to GROUP_CANCELLED then downgrade status of all children/clients
            if (oldStatusId.equals(CustomerStatus.GROUP_PENDING.getValue())
                    && newStatusId.equals(CustomerStatus.GROUP_CANCELLED.getValue()) && groupChildren != null) {

                for (CustomerBO child : groupChildren) {

                    ClientBO client = (ClientBO) child;

                    if (client.getCustomerStatus().getId().equals(CustomerStatus.CLIENT_PENDING.getValue())) {
                        client.setUserContext(userContext);
                     // FIXME - ensure clients statuses are changed as well.
//                        client.changeStatus(CustomerStatus.CLIENT_PARTIAL.getValue(), null, notes);
                    }
                }
            }

        } else if (customerBO.isClient()) {

            ClientBO client = (ClientBO) customerBO;

            changeStatus(client, newStatusId, flagId, notes, userContext);

            if (newStatusId.equals(CustomerStatus.CLIENT_CLOSED.getValue())
                    || newStatusId.equals(CustomerStatus.CLIENT_CANCELLED.getValue())) {

                if (client.isClientUnderGroup()) {

                    CustomerBO parentCustomer = client.getParentCustomer();

                    client.resetPositions(parentCustomer);
                    parentCustomer.setUserContext(userContext);
                    // parentCustomer.update();
                    CustomerBO center = parentCustomer.getParentCustomer();
                    if (center != null) {
                        parentCustomer.resetPositions(center);
                        center.setUserContext(userContext);
                        // center.update();
                        // center = null;
                    }
                }
                // close customer account - #MIFOS-1504
                for (AccountBO account : client.getAccounts()) {
                    if (account.isOfType(AccountTypes.CUSTOMER_ACCOUNT) && account.isOpen()) {
                        try {
                            account.setUserContext(userContext);

                            // FIXME - figure out taking this out of domain model to remove persistence
                            account.changeStatus(AccountState.CUSTOMER_ACCOUNT_INACTIVE, flagId, notes);
                            // account.update();
                        } catch (AccountException e) {
                            throw new CustomerException(e);
                        }
                    }
                }
            }

        } else {
            // center?
            changeStatus(customerBO, newStatusId, flagId, notes, userContext);
        }

        this.customerDao.save(customerBO);
    }

    private void changeStatus(CustomerBO customer, Short newStatusId, Short flagId, String notes,
            UserContext userContext) throws CustomerException {
        Short oldStatusId = customer.getCustomerStatus().getId();

        if (customer.isGroup()) {

            GroupBO group = (GroupBO) customer;

            if (newStatusId.equals(CustomerStatus.GROUP_CLOSED.getValue())) {
                // checkIfGroupCanBeClosed
                if (group.isAnyLoanAccountOpen() || group.isAnySavingsAccountOpen()) {
                    throw new CustomerException(CustomerConstants.CUSTOMER_HAS_ACTIVE_ACCOUNTS_EXCEPTION);
                }

                List<CustomerView> clientsThatAreNotClosedOrCanceled = this.customerDao.findClientsThatAreNotCancelledOrClosed(group.getSearchId(), group.getOffice().getOfficeId());

                if (clientsThatAreNotClosedOrCanceled.size() > 0) {
                    throw new CustomerException(CustomerConstants.ERROR_STATE_CHANGE_EXCEPTION, new Object[] { MessageLookup
                            .getInstance().lookupLabel(ConfigurationConstants.CLIENT, group.getUserContext()) });
                }
            }

            if (newStatusId.equals(CustomerStatus.GROUP_ACTIVE.getValue())) {
                // checkIfGroupCanBeActive
                if (group.getParentCustomer() == null || group.getParentCustomer().getCustomerId() == null) {
                    if (group.getPersonnel() == null || group.getPersonnel().getPersonnelId() == null) {
                        throw new CustomerException(GroupConstants.GROUP_LOANOFFICER_NOT_ASSIGNED);
                    }
                    if (group.getCustomerMeeting() == null || group.getCustomerMeeting().getMeeting() == null) {
                        throw new CustomerException(GroupConstants.MEETING_NOT_ASSIGNED);
                    }
                }
            }

            if (group.getCustomerStatus().getId().equals(CustomerStatus.GROUP_CANCELLED.getValue())
                    && newStatusId.equals(CustomerStatus.GROUP_PARTIAL.getValue())) {
                // handleValidationsForCancelToPartial
                if (group.getParentCustomer() != null && group.getParentCustomer().getCustomerId() != null) {
                    // checkGroupCanBeChangedFromCancelToPartialIfCenterIsActive
                    if (!group.getParentCustomer().isActive()) {
                        throw new CustomerException(GroupConstants.CENTER_INACTIVE, new Object[] { MessageLookup.getInstance()
                                .lookupLabel(ConfigurationConstants.CENTER, group.getUserContext()) });
                    }
                } else {
                    // checkGroupCanBeChangedFromCancelToPartialIfOfficeIsActive
                    try {
                        if (new OfficePersistence().isBranchInactive(group.getOffice().getOfficeId())) {
                            throw new CustomerException(GroupConstants.BRANCH_INACTIVE, new Object[] { MessageLookup.getInstance()
                                    .lookupLabel(ConfigurationConstants.GROUP, group.getUserContext()) });
                        }
                    } catch (PersistenceException e) {
                        throw new CustomerException(e);
                    }
                    if (group.getPersonnel() != null && group.getPersonnel().getPersonnelId() != null) {
                        // checkGroupCanBeChangedFromCancelToPartialIfPersonnelActive
                        try {
                            if (new OfficePersistence().hasActivePeronnel(group.getOffice().getOfficeId())) {
                                throw new CustomerException(GroupConstants.LOANOFFICER_INACTIVE, new Object[] { MessageLookup
                                        .getInstance().lookup(ConfigurationConstants.BRANCHOFFICE, group.getUserContext()) });
                            }
                        } catch (PersistenceException e) {
                            throw new CustomerException(e);
                        }
                    }
                }
            }

        } else if (customer.isClient()) {

            ClientBO client = (ClientBO) customer;

            if (client.getParentCustomer() != null) {
                Short groupStatus = client.getParentCustomer().getCustomerStatus().getId();
                if ((newStatusId.equals(CustomerStatus.CLIENT_ACTIVE.getValue()) || newStatusId
                        .equals(CustomerStatus.CLIENT_PENDING.getValue()))
                        && client.isClientUnderGroup()) {
                    if (groupStatus.equals(CustomerStatus.GROUP_CANCELLED.getValue())) {
                        throw new CustomerException(ClientConstants.ERRORS_GROUP_CANCELLED,
                                new Object[] { MessageLookup.getInstance().lookupLabel(ConfigurationConstants.GROUP,
                                        client.getUserContext()) });
                    }

                    if (client.isGroupStatusLower(newStatusId, groupStatus)) {

                        throw new CustomerException(ClientConstants.INVALID_CLIENT_STATUS_EXCEPTION, new Object[] {
                                MessageLookup.getInstance().lookupLabel(ConfigurationConstants.GROUP,
                                        client.getUserContext()),
                                MessageLookup.getInstance().lookupLabel(ConfigurationConstants.CLIENT,
                                        client.getUserContext()) });
                    }
                }
            }

            if (newStatusId.equals(CustomerStatus.CLIENT_CLOSED.getValue())) {
                if (client.isAnyLoanAccountOpen() || client.isAnySavingsAccountOpen()) {
                    throw new CustomerException(CustomerConstants.CUSTOMER_HAS_ACTIVE_ACCOUNTS_EXCEPTION);
                }
            }

            if (newStatusId.equals(CustomerStatus.CLIENT_ACTIVE.getValue())) {
                boolean loanOfficerActive = false;
                boolean branchInactive = false;
                short officeId = client.getOffice().getOfficeId();
                if (client.getPersonnel() == null || client.getPersonnel().getPersonnelId() == null) {
                    throw new CustomerException(ClientConstants.CLIENT_LOANOFFICER_NOT_ASSIGNED);
                }
                if (client.getCustomerMeeting() == null || client.getCustomerMeeting().getMeeting() == null) {
                    throw new CustomerException(GroupConstants.MEETING_NOT_ASSIGNED);
                }
                if (client.getPersonnel() != null) {
                    try {
                        loanOfficerActive = new OfficePersistence().hasActivePeronnel(officeId);
                    } catch (PersistenceException e) {
                        throw new CustomerException(e);
                    }
                }

                try {
                    branchInactive = new OfficePersistence().isBranchInactive(officeId);
                } catch (PersistenceException e) {
                    throw new CustomerException(e);
                }
                if (loanOfficerActive == false) {
                    throw new CustomerException(CustomerConstants.CUSTOMER_LOAN_OFFICER_INACTIVE_EXCEPTION,
                            new Object[] { MessageLookup.getInstance().lookup(ConfigurationConstants.BRANCHOFFICE,
                                    client.getUserContext()) });
                }
                if (branchInactive == true) {
                    throw new CustomerException(CustomerConstants.CUSTOMER_BRANCH_INACTIVE_EXCEPTION,
                            new Object[] { MessageLookup.getInstance().lookup(ConfigurationConstants.BRANCHOFFICE,
                                    client.getUserContext()) });
                }
            }
        } else {
            if (newStatusId.equals(CustomerStatus.CENTER_INACTIVE.getValue())) {

                if (customer.isAnySavingsAccountOpen()) {
                    throw new CustomerException(CustomerConstants.CENTER_STATE_CHANGE_EXCEPTION);
                }

                List<CustomerView> groupsThatAreNotClosedOrCanceled = this.customerDao
                        .findClientsThatAreNotCancelledOrClosed(customer.getSearchId(), customer.getOffice()
                                .getOfficeId());

                if (groupsThatAreNotClosedOrCanceled.size() > 0) {
                    throw new CustomerException(CustomerConstants.ERROR_STATE_CHANGE_EXCEPTION,
                            new Object[] { MessageLookup.getInstance().lookupLabel(ConfigurationConstants.GROUP,
                                    userContext) });
                }

            } else if (newStatusId.equals(CustomerStatus.CENTER_ACTIVE.getValue())) {

                if (customer.getPersonnel() == null || customer.getPersonnel().getPersonnelId() == null) {
                    throw new CustomerException(ClientConstants.CLIENT_LOANOFFICER_NOT_ASSIGNED);
                }
            }
        }

        PersonnelBO loanOfficer = customer.getPersonnel();
        if (loanOfficer != null) {
            if (!loanOfficer.isActive()
                    || !(loanOfficer.getOffice().getOfficeId().equals(customer.getOffice().getOfficeId()) || !loanOfficer
                            .isLoanOfficer())) {
                throw new CustomerException(CustomerConstants.CUSTOMER_LOAN_OFFICER_INACTIVE_EXCEPTION);
            }
        }
        if (customer.checkStatusChangeCancelToPartial(CustomerStatus.fromInt(oldStatusId), CustomerStatus
                .fromInt(newStatusId))) {
            if (!customer.isBlackListed()) {
                customer.getCustomerFlags().clear();
            }
        }
        CustomerStatusEntity customerStatus;
        try {
            customerStatus = (CustomerStatusEntity) new MasterPersistence().getPersistentObject(
                    CustomerStatusEntity.class, newStatusId);
        } catch (PersistenceException e) {
            throw new CustomerException(e);
        }

        Short localeId = userContext.getLocaleId();

        customerStatus.setLocaleId(localeId);
        CustomerStatusFlagEntity customerStatusFlagEntity = null;
        if (flagId != null) {
            try {
                customerStatusFlagEntity = (CustomerStatusFlagEntity) new MasterPersistence().getPersistentObject(
                        CustomerStatusFlagEntity.class, flagId);
            } catch (PersistenceException e) {
                throw new CustomerException(e);
            }
        }

        CustomerNoteEntity customerNote = new CustomerNoteEntity(notes, new DateTimeService().getCurrentJavaSqlDate(),
                this.personnelDao.findPersonnelById(userContext.getId()), customer);

        customer.setCustomerStatus(customerStatus);
        customer.addCustomerNotes(customerNote);
        if (customerStatusFlagEntity != null) {
            customerStatusFlagEntity.setLocaleId(localeId);
            customer.addCustomerFlag(customerStatusFlagEntity);
            if (customerStatusFlagEntity.isBlackListed()) {
                customer.blackListed = YesNoFlag.YES.getValue();
            }
        }

        if (customer.isGroup()) {
            handleActiveForFirstTime(customer, oldStatusId, newStatusId);

            if (customer.isActiveForFirstTime(oldStatusId, newStatusId)) {
                customer.setCustomerActivationDate(new DateTimeService().getCurrentJavaDateTime());
            }
        } else if (customer.isClient()) {

            ClientBO client = (ClientBO) customer;

            handleActiveForFirstTime(client, oldStatusId, newStatusId);
            if (client.isActiveForFirstTime(oldStatusId, newStatusId)) {
                client.setCustomerActivationDate(new DateTimeService().getCurrentJavaDateTime());

                if (client.getOfferingsAssociatedInCreate() != null) {
                    for (ClientInitialSavingsOfferingEntity clientOffering : client.getOfferingsAssociatedInCreate()) {
                        try {
                            SavingsOfferingBO savingsOffering = client.getSavingsPrdPersistence().getSavingsProduct(
                                    clientOffering.getSavingsOffering().getPrdOfferingId());

                            if (savingsOffering.isActive()) {

                                List<CustomFieldDefinitionEntity> customFieldDefs = client.getSavingsPersistence()
                                        .retrieveCustomFieldsDefinition(EntityType.SAVINGS.getValue());

                                List<CustomFieldView> customerFieldsForSavings = CustomFieldDefinitionEntity.toDto(
                                        customFieldDefs, userContext.getPreferredLocale());

                                client.addAccount(new SavingsBO(client.getUserContext(), savingsOffering, client,
                                        AccountState.SAVINGS_ACTIVE, savingsOffering.getRecommendedAmount(),
                                        customerFieldsForSavings));
                            }
                        } catch (PersistenceException pe) {
                            throw new CustomerException(pe);
                        } catch (AccountException pe) {
                            throw new CustomerException(pe);
                        }
                    }
                }

                new SavingsPersistence().persistSavingAccounts(client);

                List<Days> workingDays = new FiscalCalendarRules().getWorkingDaysAsJodaTimeDays();
                List<Holiday> holidays = DependencyInjectedServiceLocator.locateHolidayDao()
                        .findAllHolidaysThisYearAndNext();

                // createDepositSchedule
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

                            // generateAndUpdateDepositActionsForClient
                            if (client.getCustomerMeeting().getMeeting() != null) {
                                if (!(savings.getCustomer().getLevel() == CustomerLevel.GROUP && savings
                                        .getRecommendedAmntUnit().getId().equals(
                                                RecommendedAmountUnit.COMPLETE_GROUP.getValue()))) {

                                    savings.generateDepositAccountActions(client, client.getCustomerMeeting()
                                            .getMeeting(), workingDays, holidays);

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
        } else {
            // center
            handleActiveForFirstTime(customer, oldStatusId, newStatusId);
        }
    }

    private void handleActiveForFirstTime(CustomerBO customer, final Short oldStatusId, final Short newStatusId)
            throws CustomerException {

        if (customer.isActiveForFirstTime(oldStatusId, newStatusId)) {
            try {
                customer.getCustomerAccount().generateCustomerFeeSchedule();
            } catch (AccountException ae) {
                throw new CustomerException(ae);
            }
        }
    }

    private void checkPermission(CustomerBO customerBO, UserContext userContext, Short newStatusId, Short flagId) {

        try {
            if (null != customerBO.getPersonnel()) {
                new CustomerBusinessService().checkPermissionForStatusChange(newStatusId, userContext, flagId,
                        customerBO.getOffice().getOfficeId(), customerBO.getPersonnel().getPersonnelId());
            } else {
                new CustomerBusinessService().checkPermissionForStatusChange(newStatusId, userContext, flagId,
                        customerBO.getOffice().getOfficeId(), userContext.getId());
            }
        } catch (ServiceException e) {
            throw new MifosRuntimeException(e);
        }
    }
}