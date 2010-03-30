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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.joda.time.DateTime;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeeView;
import org.mifos.accounts.fees.persistence.FeePersistence;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.SpouseFatherLookupEntity;
import org.mifos.application.master.business.ValueListElement;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.calendar.CalendarUtils;
import org.mifos.config.ClientRules;
import org.mifos.config.ProcessFlowRules;
import org.mifos.config.exceptions.ConfigurationException;
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
import org.mifos.customers.client.business.ClientDetailView;
import org.mifos.customers.client.business.ClientFamilyDetailEntity;
import org.mifos.customers.client.business.ClientFamilyDetailView;
import org.mifos.customers.client.business.ClientNameDetailEntity;
import org.mifos.customers.client.business.ClientNameDetailView;
import org.mifos.customers.client.business.FamilyDetailDTO;
import org.mifos.customers.client.struts.actionforms.ClientCustActionForm;
import org.mifos.customers.client.util.helpers.ClientConstants;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.group.business.service.GroupBusinessService;
import org.mifos.customers.group.struts.action.GroupSearchResultsDto;
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
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.customers.surveys.persistence.SurveysPersistence;
import org.mifos.customers.util.helpers.CustomerDetailDto;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.customers.util.helpers.SavingsDetailDto;
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
    public ClientFormCreationDto retrieveClientFormCreationData(UserContext userContext, Short groupFlag, Short officeId, String parentGroupId) {

        List<PersonnelView> personnelList = new ArrayList<PersonnelView>();
        MeetingBO parentCustomerMeeting = null;
        Short formedByPersonnelId = null;
        String formedByPersonnelName = "";
        String centerDisplayName = "";
        String groupDisplayName = "";
        String officeName = "";
        List<FeeBO> fees = new ArrayList<FeeBO>();

        if (YesNoFlag.YES.getValue().equals(groupFlag)) {

            Integer parentCustomerId = Integer.valueOf(parentGroupId);
            CustomerBO parentCustomer = this.customerDao.findCustomerById(parentCustomerId);

            groupDisplayName = parentCustomer.getDisplayName();

            if (parentCustomer.getPersonnel() != null) {
                formedByPersonnelId = parentCustomer.getPersonnel().getPersonnelId();
                formedByPersonnelName = parentCustomer.getPersonnel().getDisplayName();
            }

            if (parentCustomer.getParentCustomer() != null) {
                centerDisplayName = parentCustomer.getParentCustomer().getDisplayName();
            }

            officeId = parentCustomer.getOffice().getOfficeId();
            officeName = parentCustomer.getOffice().getOfficeName();

            if (parentCustomer.getCustomerMeeting() != null) {
                parentCustomerMeeting = parentCustomer.getCustomerMeetingValue();
                fees = this.customerDao.retrieveFeesApplicableToClientsRefinedBy(parentCustomer.getCustomerMeetingValue());
            } else {
                fees = this.customerDao.retrieveFeesApplicableToClients();
            }

        } else if (YesNoFlag.NO.getValue().equals(groupFlag)) {

            CenterCreation centerCreation = new CenterCreation(officeId, userContext.getId(), userContext.getLevelId(), userContext.getPreferredLocale());
            personnelList = this.personnelDao.findActiveLoanOfficersForOffice(centerCreation);
            fees = this.customerDao.retrieveFeesApplicableToClients();
        }

        CustomerApplicableFeesDto applicableFees = CustomerApplicableFeesDto.toDto(fees, userContext);

        List<CustomFieldDefinitionEntity> customFieldDefinitions = customerDao.retrieveCustomFieldEntitiesForClient();
        List<CustomFieldView> customFieldViews = CustomFieldDefinitionEntity.toDto(customFieldDefinitions, userContext
                .getPreferredLocale());

        List<SavingsDetailDto> savingsOfferings = this.customerDao.retrieveSavingOfferingsApplicableToClient();

        try {
            ClientRulesDto clientRules = retrieveClientRules();

            ClientDropdownsDto clientDropdowns = retrieveClientDropdownData(userContext);

            List<PersonnelView> formedByPersonnel = this.customerDao.findLoanOfficerThatFormedOffice(officeId);

            return new ClientFormCreationDto(clientDropdowns, customFieldViews,
                    clientRules, officeId, officeName, formedByPersonnelId, formedByPersonnelName, personnelList, applicableFees, formedByPersonnel,
                    savingsOfferings, parentCustomerMeeting, centerDisplayName, groupDisplayName);
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    private ClientDropdownsDto retrieveClientDropdownData(UserContext userContext) throws PersistenceException {
        List<MasterDataEntity> spouseFather = new MasterPersistence().retrieveMasterEntities(
                SpouseFatherLookupEntity.class, userContext.getLocaleId());

        List<ValueListElement> salutations = this.customerDao.retrieveSalutations();
        List<ValueListElement> genders = this.customerDao.retrieveGenders();
        List<ValueListElement> maritalStatuses = this.customerDao.retrieveMaritalStatuses();
        List<ValueListElement> citizenship = this.customerDao.retrieveCitizenship();
        List<ValueListElement> ethinicity = this.customerDao.retrieveEthinicity();
        List<ValueListElement> educationLevels = this.customerDao.retrieveEducationLevels();
        List<ValueListElement> businessActivity = this.customerDao.retrieveBusinessActivities();
        List<ValueListElement> poverty = this.customerDao.retrievePoverty();
        List<ValueListElement> handicapped = this.customerDao.retrieveHandicapped();
        List<ValueListElement> livingStatus = this.customerDao.retrieveLivingStatus();

        ClientDropdownsDto clientDropdowns = new ClientDropdownsDto(salutations, genders, maritalStatuses, citizenship, ethinicity, educationLevels, businessActivity, poverty, handicapped, spouseFather, livingStatus);
        return clientDropdowns;
    }

    @Override
    public ClientPersonalInfoDto retrieveClientPersonalInfoForUpdate(String clientSystemId, UserContext userContext) {

        try {
            List<CustomFieldDefinitionEntity> customFieldDefinitions = customerDao.retrieveCustomFieldEntitiesForClient();
            List<CustomFieldView> customFieldViews = CustomFieldDefinitionEntity.toDto(customFieldDefinitions, userContext.getPreferredLocale());

            ClientDropdownsDto clientDropdowns = retrieveClientDropdownData(userContext);

            ClientRulesDto clientRules = retrieveClientRules();

            ClientBO client = this.customerDao.findClientBySystemId(clientSystemId);

            CustomerDetailDto customerDetailDto = client.toCustomerDetailDto();
            ClientDetailDto clientDetailDto = client.toClientDetailDto(clientRules.isFamilyDetailsRequired());

            return new ClientPersonalInfoDto(clientDropdowns, customFieldViews, clientRules, customerDetailDto, clientDetailDto);
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

            this.customerService.createCenter(center, meeting, feesForCustomerAccount);

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

    @Override
    public CenterDetailsDto createClient(ClientCustActionForm actionForm, MeetingBO meeting, UserContext userContext, List<SavingsDetailDto> offeringsList) {

        try {
            ClientBO client = null;
            List<CustomFieldView> customFields = actionForm.getCustomFields();
            CustomFieldView.convertCustomFieldDateToUniformPattern(customFields, userContext.getPreferredLocale());

            Short personnelId = null;
            Short officeId = null;

            if (actionForm.getGroupFlagValue().equals(YesNoFlag.YES.getValue())) {

                Integer parentGroupId = Integer.parseInt(actionForm.getParentGroupId());
                CustomerBO group = this.customerDao.findCustomerById(parentGroupId);

                if (group.getPersonnel() != null) {
                    personnelId = group.getPersonnel().getPersonnelId();
                }

                if (group.getParentCustomer() != null) {
                    actionForm.setGroupDisplayName(group.getDisplayName());
                    if (group.getParentCustomer() != null) {
                        actionForm.setCenterDisplayName(group.getParentCustomer().getDisplayName());
                    }
                }

                officeId = group.getOffice().getOfficeId();
            } else {
                personnelId = actionForm.getLoanOfficerIdValue();
                officeId = actionForm.getOfficeIdValue();
            }

            if (personnelId != null) {
                if (!ActivityMapper.getInstance().isSavePermittedForCustomer(actionForm.getStatusValue().getValue().shortValue(), userContext, officeId, personnelId)) {
                    throw new CustomerException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
                }
            } else {
                if (!ActivityMapper.getInstance().isSavePermittedForCustomer(actionForm.getStatusValue().getValue().shortValue(), userContext, officeId, userContext.getId())) {
                    throw new CustomerException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
                }
            }

            List<SavingsDetailDto> selectedOfferings1 = new ArrayList<SavingsDetailDto>(ClientConstants.MAX_OFFERINGS_SIZE);

            for (Short offeringId : actionForm.getSelectedOfferings()) {
                if (offeringId != null) {
                    for (SavingsDetailDto savingsOffering : offeringsList) {
                        if (offeringId.equals(savingsOffering.getPrdOfferingId())) {
                            selectedOfferings1.add(savingsOffering);
                        }
                    }
                }
            }

            // FIXME - keithw - translate from savingsDetailsDto to savingsOfferings
            List<SavingsOfferingBO> selectedOfferings = new ArrayList<SavingsOfferingBO>();

            if (actionForm.getGroupFlagValue().equals(YesNoFlag.NO.getValue())) {

                if (ClientRules.isFamilyDetailsRequired()) {
                    actionForm.setFamilyDateOfBirth();
                    actionForm.constructFamilyDetails();

                    client = new ClientBO(userContext, actionForm.getClientName().getDisplayName(), actionForm
                            .getStatusValue(), actionForm.getExternalId(), DateUtils.getDateAsSentFromBrowser(actionForm
                            .getMfiJoiningDate()), actionForm.getAddress(), customFields, actionForm.getFeesToApply(),
                            selectedOfferings, new PersonnelPersistence().getPersonnel(actionForm
                                    .getFormedByPersonnelValue()), new OfficePersistence().getOffice(actionForm
                                    .getOfficeIdValue()), meeting, new PersonnelPersistence().getPersonnel(actionForm
                                    .getLoanOfficerIdValue()), DateUtils.getDateAsSentFromBrowser(actionForm
                                    .getDateOfBirth()), actionForm.getGovernmentId(), actionForm.getTrainedValue(),
                            DateUtils.getDateAsSentFromBrowser(actionForm.getTrainedDate()),
                            actionForm.getGroupFlagValue(), actionForm.getClientName(), null, actionForm
                                    .getClientDetailView(), actionForm.getCustomerPicture());

                    client.setFamilyAndNameDetailSets(actionForm.getFamilyNames(), actionForm.getFamilyDetails());

                } else {

                    client = new ClientBO(userContext, actionForm.getClientName().getDisplayName(), actionForm
                            .getStatusValue(), actionForm.getExternalId(), DateUtils.getDateAsSentFromBrowser(actionForm
                            .getMfiJoiningDate()), actionForm.getAddress(), customFields, actionForm.getFeesToApply(),
                            selectedOfferings, new PersonnelPersistence().getPersonnel(actionForm
                                    .getFormedByPersonnelValue()), new OfficePersistence().getOffice(actionForm
                                    .getOfficeIdValue()), meeting, new PersonnelPersistence().getPersonnel(actionForm
                                    .getLoanOfficerIdValue()), DateUtils.getDateAsSentFromBrowser(actionForm
                                    .getDateOfBirth()), actionForm.getGovernmentId(), actionForm.getTrainedValue(),
                            DateUtils.getDateAsSentFromBrowser(actionForm.getTrainedDate()),
                            actionForm.getGroupFlagValue(), actionForm.getClientName(), actionForm.getSpouseName(),
                            actionForm.getClientDetailView(), actionForm.getCustomerPicture());
                }

            } else {
                Integer parentGroupId = Integer.parseInt(actionForm.getParentGroupId());
                CustomerBO group = this.customerDao.findCustomerById(parentGroupId);

                if (group.getParentCustomer() != null) {
                    actionForm.setGroupDisplayName(group.getDisplayName());
                    if (group.getParentCustomer() != null) {
                        actionForm.setCenterDisplayName(group.getParentCustomer().getDisplayName());
                    }
                }

                if (ClientRules.isFamilyDetailsRequired()) {
                    actionForm.setFamilyDateOfBirth();
                    actionForm.constructFamilyDetails();

                    client = new ClientBO(userContext, actionForm.getClientName().getDisplayName(), actionForm
                            .getStatusValue(), actionForm.getExternalId(), DateUtils.getDateAsSentFromBrowser(actionForm
                            .getMfiJoiningDate()), actionForm.getAddress(), customFields, actionForm.getFeesToApply(),
                            selectedOfferings, new PersonnelPersistence().getPersonnel(actionForm
                                    .getFormedByPersonnelValue()), group.getOffice(), group, DateUtils
                                    .getDateAsSentFromBrowser(actionForm.getDateOfBirth()), actionForm.getGovernmentId(),
                            actionForm.getTrainedValue(), DateUtils.getDateAsSentFromBrowser(actionForm.getTrainedDate()),
                            actionForm.getGroupFlagValue(), actionForm.getClientName(), null, actionForm
                                    .getClientDetailView(), actionForm.getCustomerPicture());

                    client.setFamilyAndNameDetailSets(actionForm.getFamilyNames(), actionForm.getFamilyDetails());
                } else {

                    client = new ClientBO(userContext, actionForm.getClientName().getDisplayName(), actionForm
                            .getStatusValue(), actionForm.getExternalId(), DateUtils.getDateAsSentFromBrowser(actionForm
                            .getMfiJoiningDate()), actionForm.getAddress(), customFields, actionForm.getFeesToApply(),
                            selectedOfferings, new PersonnelPersistence().getPersonnel(actionForm
                                    .getFormedByPersonnelValue()), group.getOffice(), group, DateUtils
                                    .getDateAsSentFromBrowser(actionForm.getDateOfBirth()), actionForm.getGovernmentId(),
                            actionForm.getTrainedValue(), DateUtils.getDateAsSentFromBrowser(actionForm.getTrainedDate()),
                            actionForm.getGroupFlagValue(), actionForm.getClientName(), actionForm.getSpouseName(),
                            actionForm.getClientDetailView(), actionForm.getCustomerPicture());
                }

            }

            new CustomerPersistence().saveCustomer(client);

            return new CenterDetailsDto(client.getCustomerId(), client.getGlobalCustNum());
        } catch (InvalidDateException e) {
            throw new MifosRuntimeException(e);
        } catch (CustomerException e) {
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

        runningTime = System.currentTimeMillis();
        startTime = System.currentTimeMillis();
        markposition("Very Start of updateCustomerStatus");

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

        markposition("before checkPermission");
        checkPermission(customerBO, userContext, newStatusId, flagId);
        markposition("after checkPermission");

        Short oldStatusId = customerBO.getCustomerStatus().getId();

        // FIXME - keithw - this validation was failing an integration test.. check with business expert on all states for customer status update.
//        customerBO.validateLoanOfficerIsActive();

        CustomerStatus oldStatus = CustomerStatus.fromInt(oldStatusId);
        CustomerStatus newStatus = CustomerStatus.fromInt(newStatusId);

        customerBO.clearCustomerFlagsIfApplicable(oldStatus, newStatus);
        markposition("after clearCustomerFlagsIfApplicable");

        CustomerStatusEntity customerStatus;
        try {
            customerStatus = (CustomerStatusEntity) new MasterPersistence().getPersistentObject(
                    CustomerStatusEntity.class, newStatusId);
        } catch (PersistenceException e) {
            throw new CustomerException(e);
        }
        markposition("after customerStatus");

        CustomerStatusFlagEntity customerStatusFlagEntity = null;
        if (flagId != null) {
            try {
                customerStatusFlagEntity = (CustomerStatusFlagEntity) new MasterPersistence().getPersistentObject(
                        CustomerStatusFlagEntity.class, flagId);
            } catch (PersistenceException e) {
                throw new CustomerException(e);
            }
        }
        markposition("after customerStatusFlagEntity");

        PersonnelBO loggedInUser = this.personnelDao.findPersonnelById(userContext.getId());

        markposition("after loggedInUser");
        CustomerNoteEntity customerNote = new CustomerNoteEntity(notes, new DateTimeService().getCurrentJavaSqlDate(),
                loggedInUser, customerBO);
        markposition("after customerNote new");

        customerBO.setCustomerStatus(customerStatus);
        markposition("after setCustomerStatus");

        customerBO.addCustomerNotes(customerNote);

        if (customerStatusFlagEntity != null) {
            customerBO.addCustomerFlag(customerStatusFlagEntity);
        }

        markposition("after notes and addcustomerflag");

        if (customerBO.isGroup()) {

            GroupBO group = (GroupBO) customerBO;
            this.customerService.updateGroupStatus(group, oldStatus, newStatus);
            markposition("after updateGroupStatus");
        } else if (customerBO.isClient()) {

            ClientBO client = (ClientBO) customerBO;

            this.customerService.updateClientStatus(client, oldStatus, newStatus, userContext, flagId, notes);
            markposition("after updateClientStatus");

        } else {
            CenterBO center = (CenterBO) customerBO;
            this.customerService.updateCenterStatus(center, newStatus);
            markposition("after updateCenterStatus");
        }

        markposition("FINISHED the service update");
    }

    Long runningTime = null;
    Long startTime = null;

    private void markposition(String string) {

        Session session = new SurveysPersistence().getSession();
        Long timeTaken = (System.currentTimeMillis() - runningTime);
        session.createSQLQuery("select 'A' from customer where 1=0 and display_name = 'Finished: " + string + "'")
                .list();

        System.out.println(string + ": " + timeTaken);
        runningTime = System.currentTimeMillis();
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

    @Override
    public boolean isGroupHierarchyRequired() {
        try {
            return ClientRules.getClientCanExistOutsideGroup();
        } catch (ConfigurationException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public GroupSearchResultsDto searchGroups(boolean searchForAddingClientsToGroup, String normalizedSearchString, Short loggedInUserId) {

        try {
            // FIXME - #000001 - keithw - move search logic off group business service over to customerDAO
            QueryResult searchResults = new GroupBusinessService().search(normalizedSearchString, loggedInUserId);
            QueryResult searchForAddingClientToGroupResults = null;
            if (searchForAddingClientsToGroup) {
                searchForAddingClientToGroupResults = new GroupBusinessService().searchForAddingClientToGroup(normalizedSearchString, loggedInUserId);
            }

            return new GroupSearchResultsDto(searchResults, searchForAddingClientToGroupResults);
        } catch (ServiceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public ClientFamilyDetailsDto retrieveClientFamilyDetails() {

        List<ValueListElement> genders = new ArrayList<ValueListElement>();
        List<ValueListElement> livingStatus = new ArrayList<ValueListElement>();
        List<FamilyDetailDTO> familyDetails = new ArrayList<FamilyDetailDTO>();
        boolean familyDetailsRequired = ClientRules.isFamilyDetailsRequired();

        if (familyDetailsRequired) {

            genders = this.customerDao.retrieveGenders();
            livingStatus = this.customerDao.retrieveLivingStatus();

            familyDetails.add(new FamilyDetailDTO());
        }

        return new ClientFamilyDetailsDto(familyDetailsRequired, familyDetails, genders, livingStatus);
    }

    @Override
    public ProcessRulesDto previewClient(String governmentId, DateTime dateOfBirth, String clientName) {

        boolean clientPendingApprovalStateEnabled = ProcessFlowRules.isClientPendingApprovalStateEnabled();
        boolean governmentIdValidationFailing = false;

        if (StringUtils.isNotBlank(governmentId)) {
            governmentIdValidationFailing = this.customerDao.validateGovernmentIdForClient(governmentId, clientName, dateOfBirth);
        }

        return new ProcessRulesDto(clientPendingApprovalStateEnabled, governmentIdValidationFailing);
    }

    private ClientRulesDto retrieveClientRules() {
        boolean centerHierarchyExists = ClientRules.getCenterHierarchyExists();
        int maxNumberOfFamilyMembers = ClientRules.getMaximumNumberOfFamilyMembers();
        boolean familyDetailsRequired = ClientRules.isFamilyDetailsRequired();

        ClientRulesDto clientRules = new ClientRulesDto(centerHierarchyExists, maxNumberOfFamilyMembers,
                familyDetailsRequired);
        return clientRules;
    }

    @Override
    public ClientRulesDto retrieveClientDetailsForPreviewingEditOfPersonalInfo(ClientDetailDto clientDetailDto) {

        try {
            DateTime dateOfBirth = new DateTime(DateUtils.getDateAsSentFromBrowser(clientDetailDto.getDateOfBirth()));
            this.customerDao.validateGovernmentIdForClient(clientDetailDto.getGovernmentId(), clientDetailDto.getClientDisplayName(), dateOfBirth);

            return retrieveClientRules();
        } catch (InvalidDateException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public void updateClientPersonalInfo(UserContext userContext, Integer oldClientVersionNumber, Integer customerId,
            ClientCustActionForm actionForm) {

        ClientBO client = (ClientBO) this.customerDao.findCustomerById(customerId);

        try {
            checkVersionMismatch(oldClientVersionNumber, client.getVersionNo());
            client.setUserContext(userContext);

            List<CustomFieldView> customFields = actionForm.getCustomFields();
            CustomFieldView.convertCustomFieldDateToUniformPattern(customFields, userContext.getPreferredLocale());

            List<CustomerCustomFieldEntity> clientCustomFields = CustomerCustomFieldEntity.fromDto(customFields, client);

            ClientNameDetailView spouseFather = null;
            if (!ClientRules.isFamilyDetailsRequired()) {
                spouseFather = actionForm.getSpouseName();
            }

            InputStream picture = null;
            if (actionForm.getPicture() != null && StringUtils.isNotBlank(actionForm.getPicture().getFileName())) {
                picture = actionForm.getCustomerPicture();
            }

            Address address = actionForm.getAddress();
            ClientNameDetailView clientNameDetails = actionForm.getClientName();
            ClientDetailView clientDetail = actionForm.getClientDetailView();

            String governmentId = actionForm.getGovernmentId();
            String clientDisplayName = actionForm.getClientName().getDisplayName();
            String dateOfBirth = actionForm.getDateOfBirth();

            ClientPersonalInfoUpdate personalInfo = new ClientPersonalInfoUpdate(clientCustomFields, address, clientDetail, clientNameDetails, spouseFather, picture, governmentId, clientDisplayName, dateOfBirth);
            this.customerService.updateClientPersonalInfo(client, personalInfo);

        } catch (ApplicationException e) {
            throw new MifosRuntimeException(e);
        } catch (InvalidDateException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public ClientFamilyInfoDto retrieveFamilyInfoForEdit(String clientGlobalCustNum, UserContext userContext) {

        try {
            ClientBO client = this.customerDao.findClientBySystemId(clientGlobalCustNum);

            List<CustomFieldDefinitionEntity> fieldDefinitions = customerDao.retrieveCustomFieldEntitiesForClient();
            List<CustomFieldView> customFieldViews = CustomerCustomFieldEntity.toDto(client.getCustomFields(),
                    fieldDefinitions, userContext);

            ClientDropdownsDto clientDropdowns = retrieveClientDropdownData(userContext);

            ClientRulesDto clientRules = retrieveClientRules();

            CustomerDetailDto customerDetail = client.toCustomerDetailDto();
            ClientDetailDto clientDetail = client.toClientDetailDto(clientRules.isFamilyDetailsRequired());

            List<ClientNameDetailView> familyMembers = new ArrayList<ClientNameDetailView>();
            Map<Integer, List<ClientFamilyDetailView>> clientFamilyDetails = new HashMap<Integer, List<ClientFamilyDetailView>>();
            int familyMemberCount = 0;
            for (ClientNameDetailEntity clientNameDetailEntity : client.getNameDetailSet()) {

                if (clientNameDetailEntity.isNotClientNameType()) {

                    ClientNameDetailView nameView1 = clientNameDetailEntity.toDto();
                    familyMembers.add(nameView1);

                    for (ClientFamilyDetailEntity clientFamilyDetailEntity : client.getFamilyDetailSet()) {

                        if (clientNameDetailEntity.matchesCustomerId(clientFamilyDetailEntity.getClientName().getCustomerNameId())) {
                            ClientFamilyDetailView clientFamilyDetail = clientFamilyDetailEntity.toDto();

                            final Integer mapKey = Integer.valueOf(familyMemberCount);
                            if (clientFamilyDetails.containsKey(mapKey)) {
                                clientFamilyDetails.get(mapKey).add(clientFamilyDetail);
                            } else {
                                List<ClientFamilyDetailView> clientFamilyDetailsList = new ArrayList<ClientFamilyDetailView>();
                                clientFamilyDetailsList.add(clientFamilyDetail);
                                clientFamilyDetails.put(mapKey, clientFamilyDetailsList);
                            }
                        }
                    }
                    familyMemberCount++;
                }
            }

            return new ClientFamilyInfoDto(clientDropdowns, customFieldViews, customerDetail, clientDetail, familyMembers, clientFamilyDetails);
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public void updateFamilyInfo(Integer customerId, UserContext userContext, Integer oldVersionNum, ClientCustActionForm actionForm) {
        ClientBO client = (ClientBO) this.customerDao.findCustomerById(customerId);
        client.setUserContext(userContext);

        try {
            checkVersionMismatch(oldVersionNum, client.getVersionNo());

            ClientFamilyInfoUpdate clientFamilyInfoUpdate = new ClientFamilyInfoUpdate(actionForm.getFamilyPrimaryKey(), actionForm.getFamilyNames(), actionForm.getFamilyDetails());
            this.customerService.updateClientFamilyInfo(client, clientFamilyInfoUpdate);
        } catch (ApplicationException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public ClientMfiInfoDto retrieveMfiInfoForEdit(String clientSystemId, UserContext userContext) {

        ClientBO client = this.customerDao.findClientBySystemId(clientSystemId);

        String groupDisplayName = "";
        String centerDisplayName = "";
        if (client.getParentCustomer() != null) {
            groupDisplayName = client.getParentCustomer().getDisplayName();
            if (client.getParentCustomer().getParentCustomer() != null) {
                centerDisplayName = client.getParentCustomer().getParentCustomer().getDisplayName();
            }
        }

        List<PersonnelView> loanOfficersList = new ArrayList<PersonnelView>();
        if (!client.isClientUnderGroup()) {
            CenterCreation centerCreation = new CenterCreation(client.getOffice().getOfficeId(), userContext.getId(), userContext.getLevelId(), userContext.getPreferredLocale());
            loanOfficersList = this.personnelDao.findActiveLoanOfficersForOffice(centerCreation);
        }

        CustomerDetailDto customerDetail = client.toCustomerDetailDto();
        ClientDetailDto clientDetail = client.toClientDetailDto(ClientRules.isFamilyDetailsRequired());
        return new ClientMfiInfoDto(groupDisplayName, centerDisplayName, loanOfficersList, customerDetail, clientDetail);
    }

    @Override
    public void updateClientMfiInfo(Integer clientId, Integer oldVersionNumber, UserContext userContext, ClientCustActionForm actionForm) {

        try {
            ClientBO client = (ClientBO) this.customerDao.findCustomerById(clientId);
            checkVersionMismatch(oldVersionNumber, client.getVersionNo());

            client.setUserContext(userContext);

            boolean trained = false;
            if (actionForm.getTrainedValue() != null && actionForm.getTrainedValue().equals(YesNoFlag.YES.getValue())) {
                trained = true;
            }

            DateTime trainedDate = new DateTime(DateUtils.getDateAsSentFromBrowser(actionForm.getTrainedDate()));

            Short personnelId = null;
            if (actionForm.getGroupFlagValue().equals(YesNoFlag.NO.getValue())) {
                if (actionForm.getLoanOfficerIdValue() != null) {
                    personnelId = actionForm.getLoanOfficerIdValue();
                }
            } else if (actionForm.getGroupFlagValue().equals(YesNoFlag.YES.getValue())) {
                personnelId = client.getPersonnel().getPersonnelId();
            }

            ClientMfiInfoUpdate clientMfiInfoUpdate = new ClientMfiInfoUpdate(personnelId, actionForm.getExternalId(), trained, trainedDate);
            this.customerService.updateClientMfiInfo(client, clientMfiInfoUpdate);
        } catch (ApplicationException e) {
            throw new MifosRuntimeException(e);
        } catch (InvalidDateException e) {
            throw new MifosRuntimeException(e);
        }
    }
}