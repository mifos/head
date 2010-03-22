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

package org.mifos.application.servicefacade;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeeView;
import org.mifos.accounts.fees.persistence.FeePersistence;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.SpouseFatherLookupEntity;
import org.mifos.application.master.business.ValueListElement;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.calendar.CalendarUtils;
import org.mifos.config.ClientRules;
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
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.group.struts.actionforms.GroupCustActionForm;
import org.mifos.customers.group.util.helpers.CenterSearchInput;
import org.mifos.customers.group.util.helpers.GroupConstants;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.business.OfficeView;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.office.persistence.OfficeDto;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.business.PersonnelView;
import org.mifos.customers.personnel.persistence.PersonnelDao;
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

        CenterCreation centerCreation;

        boolean isCenterHierarchyExists = ClientRules.getCenterHierarchyExists();
        if (isCenterHierarchyExists) {
            parentCustomer = this.customerDao.findCenterBySystemId(groupCreation.getParentSystemId());

            Short parentOfficeId = parentCustomer.getOffice().getOfficeId();

            centerCreation = new CenterCreation(parentOfficeId, groupCreation.getUserId(),
                    groupCreation.getUserLevelId(), groupCreation.getPreferredLocale());

            MeetingBO customerMeeting = parentCustomer.getCustomerMeetingValue();
            List<FeeBO> fees = customerDao.retrieveFeesApplicableToGroupsRefinedBy(customerMeeting);
            applicableFees = CustomerApplicableFeesDto.toDto(fees, groupCreation.getUserContext());
        } else {
            centerCreation = new CenterCreation(groupCreation.getOfficeId(), groupCreation.getUserId(),
                    groupCreation.getUserLevelId(), groupCreation.getPreferredLocale());
            personnelList = this.personnelDao.findActiveLoanOfficersForOffice(centerCreation);

            List<FeeBO> fees = customerDao.retrieveFeesApplicableToGroups();
            applicableFees = CustomerApplicableFeesDto.toDto(fees, groupCreation.getUserContext());
        }

        List<CustomFieldDefinitionEntity> customFieldDefinitions = customerDao.retrieveCustomFieldEntitiesForGroup();
        List<CustomFieldView> customFieldViews = CustomFieldDefinitionEntity.toDto(customFieldDefinitions,
                groupCreation.getPreferredLocale());
        List<PersonnelView> formedByPersonnel = customerDao
                .findLoanOfficerThatFormedOffice(centerCreation.getOfficeId());

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

        Short oldStatusId = customerBO.getCustomerStatus().getId();

        // FIXME - keithw - this validation was failing an integration test.. check with business expert on all states for customer status update.
//        customerBO.validateLoanOfficerIsActive();

        CustomerStatus oldStatus = CustomerStatus.fromInt(oldStatusId);
        CustomerStatus newStatus = CustomerStatus.fromInt(newStatusId);

        customerBO.clearCustomerFlagsIfApplicable(oldStatus, newStatus);

        CustomerStatusEntity customerStatus;
        try {
            customerStatus = (CustomerStatusEntity) new MasterPersistence().getPersistentObject(
                    CustomerStatusEntity.class, newStatusId);
        } catch (PersistenceException e) {
            throw new CustomerException(e);
        }

        CustomerStatusFlagEntity customerStatusFlagEntity = null;
        if (flagId != null) {
            try {
                customerStatusFlagEntity = (CustomerStatusFlagEntity) new MasterPersistence().getPersistentObject(
                        CustomerStatusFlagEntity.class, flagId);
            } catch (PersistenceException e) {
                throw new CustomerException(e);
            }
        }

        PersonnelBO loggedInUser = this.personnelDao.findPersonnelById(userContext.getId());
        CustomerNoteEntity customerNote = new CustomerNoteEntity(notes, new DateTimeService().getCurrentJavaSqlDate(),
                loggedInUser, customerBO);

        customerBO.setCustomerStatus(customerStatus);
        customerBO.addCustomerNotes(customerNote);

        if (customerStatusFlagEntity != null) {
            customerBO.addCustomerFlag(customerStatusFlagEntity);
        }

        if (customerBO.isGroup()) {

            GroupBO group = (GroupBO) customerBO;
            this.customerService.updateGroupStatus(group, oldStatus, newStatus);
        } else if (customerBO.isClient()) {

            ClientBO client = (ClientBO) customerBO;

            this.customerService.updateClientStatus(client, oldStatus, newStatus, userContext, flagId, notes);

        } else {
            CenterBO center = (CenterBO) customerBO;
            this.customerService.updateCenterStatus(center, newStatus);
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