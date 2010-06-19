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
import java.sql.Blob;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.accounts.fees.persistence.FeePersistence;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.persistence.SavingsPrdPersistence;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldDto;
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
import org.mifos.customers.business.CustomerDto;
import org.mifos.customers.business.CustomerPositionDto;
import org.mifos.customers.business.CustomerPositionEntity;
import org.mifos.customers.business.PositionEntity;
import org.mifos.customers.business.service.CustomerService;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.center.struts.action.OfficeHierarchyDto;
import org.mifos.customers.center.struts.actionforms.CenterCustActionForm;
import org.mifos.customers.center.util.helpers.CenterConstants;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.client.business.ClientDetailEntity;
import org.mifos.customers.client.business.ClientFamilyDetailDto;
import org.mifos.customers.client.business.ClientFamilyDetailEntity;
import org.mifos.customers.client.business.ClientInitialSavingsOfferingEntity;
import org.mifos.customers.client.business.ClientNameDetailDto;
import org.mifos.customers.client.business.ClientNameDetailEntity;
import org.mifos.customers.client.business.ClientPersonalDetailDto;
import org.mifos.customers.client.business.FamilyDetailDTO;
import org.mifos.customers.client.persistence.ClientPersistence;
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
import org.mifos.customers.office.business.OfficeDetailsDto;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.office.persistence.OfficeDto;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.business.PersonnelDto;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.customers.util.helpers.CustomerDetailDto;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.customers.util.helpers.CustomerStatusFlag;
import org.mifos.customers.util.helpers.SavingsDetailDto;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.QueryResult;
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
        List<OfficeDetailsDto> levels = officeDao.findActiveOfficeLevels();

        List<OfficeHierarchyDto> branchOnlyOfficeHierarchy = OfficeBO
                .convertToBranchOnlyHierarchyWithParentsOfficeHierarchy(branchParents);

        return new OnlyBranchOfficeHierarchyDto(userContext.getPreferredLocale(), levels, office.getSearchId(),
                branchOnlyOfficeHierarchy);
    }

    @Override
    public CenterFormCreationDto retrieveCenterFormCreationData(CenterCreation centerCreation, UserContext userContext) {

        List<PersonnelDto> activeLoanOfficersForBranch = personnelDao.findActiveLoanOfficersForOffice(centerCreation);

        List<CustomFieldDefinitionEntity> customFieldDefinitions = customerDao.retrieveCustomFieldEntitiesForCenter();
        List<CustomFieldDto> customFieldDtos = CustomFieldDefinitionEntity.toDto(customFieldDefinitions, userContext
                .getPreferredLocale());

        List<FeeBO> fees = customerDao.retrieveFeesApplicableToCenters();
        CustomerApplicableFeesDto applicableFees = CustomerApplicableFeesDto.toDto(fees, userContext);

        return new CenterFormCreationDto(activeLoanOfficersForBranch, customFieldDtos, applicableFees
                .getAdditionalFees(), applicableFees.getDefaultFees());
    }

    @Override
    public GroupFormCreationDto retrieveGroupFormCreationData(GroupCreation groupCreation) {

        CustomerBO parentCustomer = null;
        Short parentOfficeId = groupCreation.getOfficeId();
        CustomerApplicableFeesDto applicableFees = CustomerApplicableFeesDto.empty();
        List<PersonnelDto> personnelList = new ArrayList<PersonnelDto>();

        CenterCreation centerCreation;

        boolean isCenterHierarchyExists = ClientRules.getCenterHierarchyExists();
        if (isCenterHierarchyExists) {
            parentCustomer = this.customerDao.findCenterBySystemId(groupCreation.getParentSystemId());

            parentOfficeId = parentCustomer.getOffice().getOfficeId();

            centerCreation = new CenterCreation(parentOfficeId, groupCreation.getUserId(), groupCreation
                    .getUserLevelId(), groupCreation.getPreferredLocale());

            MeetingBO customerMeeting = parentCustomer.getCustomerMeetingValue();
            List<FeeBO> fees = customerDao.retrieveFeesApplicableToGroupsRefinedBy(customerMeeting);
            applicableFees = CustomerApplicableFeesDto.toDto(fees, groupCreation.getUserContext());
        } else {
            centerCreation = new CenterCreation(groupCreation.getOfficeId(), groupCreation.getUserId(), groupCreation
                    .getUserLevelId(), groupCreation.getPreferredLocale());
            personnelList = this.personnelDao.findActiveLoanOfficersForOffice(centerCreation);

            List<FeeBO> fees = customerDao.retrieveFeesApplicableToGroups();
            applicableFees = CustomerApplicableFeesDto.toDto(fees, groupCreation.getUserContext());
        }

        List<CustomFieldDefinitionEntity> customFieldDefinitions = customerDao.retrieveCustomFieldEntitiesForGroup();
        List<CustomFieldDto> customFieldDtos = CustomFieldDefinitionEntity.toDto(customFieldDefinitions,
                groupCreation.getPreferredLocale());
        List<PersonnelDto> formedByPersonnel = customerDao.findLoanOfficerThatFormedOffice(centerCreation
                .getOfficeId());

        return new GroupFormCreationDto(isCenterHierarchyExists, parentCustomer, parentOfficeId, customFieldDtos, personnelList,
                formedByPersonnel, applicableFees);
    }

    @Override
    public ClientFormCreationDto retrieveClientFormCreationData(UserContext userContext, Short groupFlag,
            Short officeId, String parentGroupId) {

        List<PersonnelDto> personnelList = new ArrayList<PersonnelDto>();
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
                fees = this.customerDao.retrieveFeesApplicableToClientsRefinedBy(parentCustomer
                        .getCustomerMeetingValue());
            } else {
                fees = this.customerDao.retrieveFeesApplicableToClients();
            }

        } else if (YesNoFlag.NO.getValue().equals(groupFlag)) {

            CenterCreation centerCreation = new CenterCreation(officeId, userContext.getId(), userContext.getLevelId(),
                    userContext.getPreferredLocale());
            personnelList = this.personnelDao.findActiveLoanOfficersForOffice(centerCreation);
            fees = this.customerDao.retrieveFeesApplicableToClients();
        }

        CustomerApplicableFeesDto applicableFees = CustomerApplicableFeesDto.toDto(fees, userContext);

        List<CustomFieldDefinitionEntity> customFieldDefinitions = customerDao.retrieveCustomFieldEntitiesForClient();
        List<CustomFieldDto> customFieldDtos = CustomFieldDefinitionEntity.toDto(customFieldDefinitions, userContext
                .getPreferredLocale());

        List<SavingsDetailDto> savingsOfferings = this.customerDao.retrieveSavingOfferingsApplicableToClient();

        try {
            ClientRulesDto clientRules = retrieveClientRules();

            ClientDropdownsDto clientDropdowns = retrieveClientDropdownData(userContext);

            List<PersonnelDto> formedByPersonnel = this.customerDao.findLoanOfficerThatFormedOffice(officeId);

            return new ClientFormCreationDto(clientDropdowns, customFieldDtos, clientRules, officeId, officeName,
                    formedByPersonnelId, formedByPersonnelName, personnelList, applicableFees, formedByPersonnel,
                    savingsOfferings, parentCustomerMeeting, centerDisplayName, groupDisplayName);
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    private ClientDropdownsDto retrieveClientDropdownData(UserContext userContext) throws PersistenceException {
        List<SpouseFatherLookupEntity> spouseFather = new MasterPersistence().retrieveMasterEntities(
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

        ClientDropdownsDto clientDropdowns = new ClientDropdownsDto(salutations, genders, maritalStatuses, citizenship,
                ethinicity, educationLevels, businessActivity, poverty, handicapped, spouseFather, livingStatus);
        return clientDropdowns;
    }

    @Override
    public ClientPersonalInfoDto retrieveClientPersonalInfoForUpdate(String clientSystemId, UserContext userContext) {

        try {
            List<CustomFieldDefinitionEntity> customFieldDefinitions = customerDao
                    .retrieveCustomFieldEntitiesForClient();
            List<CustomFieldDto> customFieldDtos = CustomFieldDefinitionEntity.toDto(customFieldDefinitions,
                    userContext.getPreferredLocale());

            ClientDropdownsDto clientDropdowns = retrieveClientDropdownData(userContext);

            ClientRulesDto clientRules = retrieveClientRules();

            ClientBO client = this.customerDao.findClientBySystemId(clientSystemId);

            CustomerDetailDto customerDetailDto = client.toCustomerDetailDto();
            ClientDetailDto clientDetailDto = client.toClientDetailDto(clientRules.isFamilyDetailsRequired());

            return new ClientPersonalInfoDto(clientDropdowns, customFieldDtos, clientRules, customerDetailDto,
                    clientDetailDto);
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public CustomerDetailsDto createNewCenter(CenterCustActionForm actionForm, MeetingBO meeting, UserContext userContext, List<CustomerCustomFieldEntity> customerCustomFields)
        throws ApplicationException {

        try {
            CustomerCustomFieldEntity.convertCustomFieldDateToUniformPattern(customerCustomFields, userContext.getPreferredLocale());
            DateTime mfiJoiningDate = CalendarUtils.getDateFromString(actionForm.getMfiJoiningDate(), userContext
                    .getPreferredLocale());

            String centerName = actionForm.getDisplayName();
            String externalId = actionForm.getExternalId();
            Address centerAddress = actionForm.getAddress();
            PersonnelBO loanOfficer = this.personnelDao.findPersonnelById(actionForm.getLoanOfficerIdValue());
            OfficeBO centerOffice = this.officeDao.findOfficeById(actionForm.getOfficeIdValue());

            int numberOfCustomersInOfficeAlready = customerDao.retrieveLastSearchIdValueForNonParentCustomersInOffice(actionForm.getOfficeIdValue());

            List<AccountFeesEntity> feesForCustomerAccount = convertFeeViewsToAccountFeeEntities(actionForm
                    .getFeesToApply());

            CenterBO center = CenterBO.createNew(userContext, centerName, mfiJoiningDate, meeting, loanOfficer,
                    centerOffice, numberOfCustomersInOfficeAlready, customerCustomFields, centerAddress, externalId, new DateMidnight().toDateTime());

            this.customerService.createCenter(center, meeting, feesForCustomerAccount);

            return new CustomerDetailsDto(center.getCustomerId(), center.getGlobalCustNum());
        } catch (InvalidDateException e) {
            throw new IllegalStateException(e);
        } catch (PersistenceException e) {
            throw new IllegalStateException(e);
        }
    }

    private List<AccountFeesEntity> convertFeeViewsToAccountFeeEntities(List<FeeDto> feesToApply) {
        List<AccountFeesEntity> feesForCustomerAccount = new ArrayList<AccountFeesEntity>();
        for (FeeDto feeDto : feesToApply) {
            FeeBO fee = new FeePersistence().getFee(feeDto.getFeeIdValue());
            Double feeAmount = new LocalizationConverter().getDoubleValueForCurrentLocale(feeDto.getAmount());

            AccountBO nullReferenceForNow = null;
            AccountFeesEntity accountFee = new AccountFeesEntity(nullReferenceForNow, fee, feeAmount);
            feesForCustomerAccount.add(accountFee);
        }
        return feesForCustomerAccount;
    }

    @Override
    public CustomerDetailsDto createNewGroup(GroupCustActionForm actionForm, MeetingBO meeting, UserContext userContext, List<CustomerCustomFieldEntity> customerCustomFields)
            throws ApplicationException {

        GroupBO group;

        try {
            List<AccountFeesEntity> feesForCustomerAccount = convertFeeViewsToAccountFeeEntities(actionForm
                    .getFeesToApply());

            CustomerCustomFieldEntity.convertCustomFieldDateToUniformPattern(customerCustomFields, userContext.getPreferredLocale());

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

                loanOfficerId = actionForm.getLoanOfficerIdValue() != null ? actionForm.getLoanOfficerIdValue()
                        : userContext.getId();
                officeId = actionForm.getOfficeIdValue();

                checkPermissionForCreate(customerStatusId, userContext, officeId, loanOfficerId);

                OfficeBO office = this.officeDao.findOfficeById(actionForm.getOfficeIdValue());
                PersonnelBO loanOfficer = this.personnelDao.findPersonnelById(actionForm.getLoanOfficerIdValue());

                int numberOfCustomersInOfficeAlready = customerDao.retrieveLastSearchIdValueForNonParentCustomersInOffice(officeId);

                group = GroupBO.createGroupAsTopOfCustomerHierarchy(userContext, groupName, formedBy, groupMeeting,
                        loanOfficer, office, customerCustomFields, address, externalId, trained, trainedOn,
                        customerStatus, numberOfCustomersInOfficeAlready);
            }

            this.customerService.createGroup(group, groupMeeting, feesForCustomerAccount);

            return new CustomerDetailsDto(group.getCustomerId(), group.getGlobalCustNum());
        } catch (InvalidDateException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public CustomerDetailsDto createNewClient(ClientCustActionForm actionForm, MeetingBO meeting,
            UserContext userContext, List<SavingsDetailDto> allowedSavingProducts, List<CustomerCustomFieldEntity> customerCustomFields) throws ApplicationException {

        try {
            List<Short> selectedSavingProducts = actionForm.getSelectedOfferings();
            String clientName = clientName(actionForm);
            CustomerStatus clientStatus = clientStatus(actionForm);
            java.sql.Date mfiJoiningDate = mfiJoiningDate(actionForm);
            String externalId = externalId(actionForm);
            Address address = address(actionForm);
            PersonnelBO formedBy = formedByPersonnel(actionForm);
            java.sql.Date dateOfBirth = dateOfBirth(actionForm);
            String governmentId = governmentId(actionForm);
            Short trained = trainedValue(actionForm);
            java.sql.Date trainedDate = trainedDate(actionForm);
            Short groupFlagValue = groupFlagValue(actionForm);
            ClientNameDetailDto clientNameDetailDto = clientNameDetailName(actionForm);
            ClientPersonalDetailDto clientPersonalDetailDto = clientPersonalDetailDto(actionForm);
            InputStream picture = picture(actionForm);

            ClientBO client = null;
            CustomerCustomFieldEntity.convertCustomFieldDateToUniformPattern(customerCustomFields, userContext.getPreferredLocale());

            List<AccountFeesEntity> feesForCustomerAccount = convertFeeViewsToAccountFeeEntities(clientFees(actionForm));
            List<SavingsOfferingBO> selectedOfferings = new ArrayList<SavingsOfferingBO>();
            for (Short productId : selectedSavingProducts) {
                if (productId != null) {
                    for (SavingsDetailDto savingsOffering : allowedSavingProducts) {
                        if (productId.equals(savingsOffering.getPrdOfferingId())) {

                            SavingsOfferingBO savingsProduct = new SavingsPrdPersistence().getSavingsProduct(productId);
                            selectedOfferings.add(savingsProduct);
                        }
                    }
                }
            }

            List<ClientInitialSavingsOfferingEntity> offeringsAssociatedInCreate = new ArrayList<ClientInitialSavingsOfferingEntity>();
            for (SavingsOfferingBO offering : selectedOfferings) {
                offeringsAssociatedInCreate.add(new ClientInitialSavingsOfferingEntity(null, offering));
            }

            Short personnelId = null;
            Short officeId = null;

            ClientNameDetailDto spouseNameDetailView = null;
            if (ClientRules.isFamilyDetailsRequired()) {
                actionForm.setFamilyDateOfBirth();
                actionForm.constructFamilyDetails();
            } else {
                spouseNameDetailView = spouseFatherName(actionForm);
            }

            String secondMiddleName = null;
            ClientNameDetailEntity clientNameDetailEntity = new ClientNameDetailEntity(null, secondMiddleName,
                    clientNameDetailDto);

            ClientNameDetailEntity spouseFatherNameDetailEntity = null;
            if (spouseNameDetailView != null) {
                spouseFatherNameDetailEntity = new ClientNameDetailEntity(null, secondMiddleName, spouseNameDetailView);
            }

            ClientDetailEntity clientDetailEntity = new ClientDetailEntity();
            clientDetailEntity.updateClientDetails(clientPersonalDetailDto);

            DateTime dob = new DateTime(dateOfBirth);
            boolean trainedBool = isTrained(trained);
            DateTime trainedDateTime = new DateTime(trainedDate);
            String clientFirstName = clientNameDetailDto.getFirstName();
            String clientLastName = clientNameDetailDto.getLastName();
            String secondLastName = clientNameDetailDto.getSecondLastName();

            Blob pictureAsBlob = null;
            if (picture != null) {
                pictureAsBlob = new ClientPersistence().createBlob(picture);
            }

            if (YesNoFlag.YES.getValue().equals(groupFlagValue(actionForm))) {

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

                client = ClientBO.createNewInGroupHierarchy(userContext, clientName, clientStatus, new DateTime(
                        mfiJoiningDate), group, formedBy, customerCustomFields, clientNameDetailEntity, dob,
                        governmentId, trainedBool, trainedDateTime, groupFlagValue, clientFirstName, clientLastName,
                        secondLastName, spouseFatherNameDetailEntity, clientDetailEntity, pictureAsBlob,
                        offeringsAssociatedInCreate, externalId, address);

                if (ClientRules.isFamilyDetailsRequired()) {
                    client.setFamilyAndNameDetailSets(actionForm.getFamilyNames(), actionForm.getFamilyDetails());
                }

                this.customerService.createClient(client, client.getCustomerMeetingValue(), feesForCustomerAccount,
                        selectedOfferings);

            } else {
                personnelId = actionForm.getLoanOfficerIdValue();
                officeId = actionForm.getOfficeIdValue();

                checkPermissionForCreate(actionForm.getStatusValue().getValue(), userContext, officeId, personnelId);

                PersonnelBO loanOfficer = this.personnelDao.findPersonnelById(personnelId);
                OfficeBO office = this.officeDao.findOfficeById(officeId);

                int lastSearchIdCustomerValue = customerDao.retrieveLastSearchIdValueForNonParentCustomersInOffice(officeId);

                client = ClientBO.createNewOutOfGroupHierarchy(userContext, clientName, clientStatus, new DateTime(
                        mfiJoiningDate), office, loanOfficer, meeting, formedBy, customerCustomFields,
                        clientNameDetailEntity, dob, governmentId, trainedBool, trainedDateTime, groupFlagValue,
                        clientFirstName, clientLastName, secondLastName, spouseFatherNameDetailEntity,
                        clientDetailEntity, pictureAsBlob, offeringsAssociatedInCreate, externalId, address, lastSearchIdCustomerValue);

                if (ClientRules.isFamilyDetailsRequired()) {
                    client.setFamilyAndNameDetailSets(actionForm.getFamilyNames(), actionForm.getFamilyDetails());
                }

                this.customerService.createClient(client, meeting, feesForCustomerAccount, selectedOfferings);
            }

            return new CustomerDetailsDto(client.getCustomerId(), client.getGlobalCustNum());
        } catch (InvalidDateException e) {
            throw new MifosRuntimeException(e);
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    private boolean isTrained(Short trainedValue) {
        return Short.valueOf("1").equals(trainedValue);
    }

    private ClientNameDetailDto spouseFatherName(ClientCustActionForm actionForm) {
        return actionForm.getSpouseName();
    }

    private InputStream picture(ClientCustActionForm actionForm) {
        return actionForm.getCustomerPicture();
    }

    private ClientPersonalDetailDto clientPersonalDetailDto(ClientCustActionForm actionForm) {
        return actionForm.getClientDetailView();
    }

    private ClientNameDetailDto clientNameDetailName(ClientCustActionForm actionForm) {
        return actionForm.getClientName();
    }

    private Short groupFlagValue(ClientCustActionForm actionForm) {
        return actionForm.getGroupFlagValue();
    }

    private Date trainedDate(ClientCustActionForm actionForm) throws InvalidDateException {
        return DateUtils.getDateAsSentFromBrowser(actionForm.getTrainedDate());
    }

    private Short trainedValue(ClientCustActionForm actionForm) {
        return actionForm.getTrainedValue();
    }

    private String governmentId(ClientCustActionForm actionForm) {
        return actionForm.getGovernmentId();
    }

    private Date dateOfBirth(ClientCustActionForm actionForm) throws InvalidDateException {
        return DateUtils.getDateAsSentFromBrowser(actionForm.getDateOfBirth());
    }

    private PersonnelBO formedByPersonnel(ClientCustActionForm actionForm) throws PersistenceException {
        return new PersonnelPersistence().getPersonnel(actionForm.getFormedByPersonnelValue());
    }

    private List<FeeDto> clientFees(ClientCustActionForm actionForm) {
        return actionForm.getFeesToApply();
    }

    private Address address(ClientCustActionForm actionForm) {
        return actionForm.getAddress();
    }

    private String externalId(ClientCustActionForm actionForm) {
        return actionForm.getExternalId();
    }

    private Date mfiJoiningDate(ClientCustActionForm actionForm) throws InvalidDateException {
        return DateUtils.getDateAsSentFromBrowser(actionForm.getMfiJoiningDate());
    }

    private CustomerStatus clientStatus(ClientCustActionForm actionForm) {
        return actionForm.getStatusValue();
    }

    private String clientName(ClientCustActionForm actionForm) {
        return clientNameDetailName(actionForm).getDisplayName();
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
        List<PersonnelDto> activeLoanOfficersForBranch = personnelDao.findActiveLoanOfficersForOffice(centerCreation);

        List<CustomerDto> customerList = customerDao.findClientsThatAreNotCancelledOrClosed(searchId, officeId);

        List<CustomerPositionDto> customerPositionDtos = generateCustomerPositionViews(center, userContext.getLocaleId());

        List<CustomFieldDefinitionEntity> fieldDefinitions = customerDao.retrieveCustomFieldEntitiesForCenter();
        List<CustomFieldDto> customFieldDtos = CustomerCustomFieldEntity.toDto(center.getCustomFields(),
                fieldDefinitions, userContext);

        DateTime mfiJoiningDate = new DateTime();
        String mfiJoiningDateAsString = "";
        if (center.getMfiJoiningDate() != null) {
            mfiJoiningDate = new DateTime(center.getMfiJoiningDate());
            mfiJoiningDateAsString = DateUtils.getUserLocaleDate(userContext.getPreferredLocale(), center
                    .getMfiJoiningDate().toString());
        }

        return new CenterDto(loanOfficerId, center.getCustomerId(), center.getGlobalCustNum(), mfiJoiningDate,
                mfiJoiningDateAsString, center.getExternalId(), center.getAddress(), customerPositionDtos,
                customFieldDtos, customerList, center, activeLoanOfficersForBranch, true);
    }

    @Override
    public CenterDto retrieveGroupDetailsForUpdate(String globalCustNum, UserContext userContext) {

        List<PersonnelDto> activeLoanOfficersForBranch = new ArrayList<PersonnelDto>();

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

        List<CustomerDto> customerList = customerDao.findClientsThatAreNotCancelledOrClosed(searchId, officeId);

        List<CustomerPositionDto> customerPositionDtos = generateCustomerPositionViews(group, userContext
                .getLocaleId());

        List<CustomFieldDefinitionEntity> fieldDefinitions = customerDao.retrieveCustomFieldEntitiesForGroup();
        List<CustomFieldDto> customFieldDtos = CustomerCustomFieldEntity.toDto(group.getCustomFields(),
                fieldDefinitions, userContext);

        DateTime mfiJoiningDate = new DateTime();
        String mfiJoiningDateAsString = "";
        if (group.getMfiJoiningDate() != null) {
            mfiJoiningDate = new DateTime(group.getMfiJoiningDate());
            mfiJoiningDateAsString = DateUtils.getUserLocaleDate(userContext.getPreferredLocale(), group
                    .getMfiJoiningDate().toString());
        }

        return new CenterDto(loanOfficerId, group.getCustomerId(), group.getGlobalCustNum(), mfiJoiningDate,
                mfiJoiningDateAsString, group.getExternalId(), group.getAddress(), customerPositionDtos,
                customFieldDtos, customerList, group, activeLoanOfficersForBranch, isCenterHierarchyExists);
    }

    private List<CustomerPositionDto> generateCustomerPositionViews(CustomerBO customer, Short localeId) {

        try {
            List<PositionEntity> customerPositions = new ArrayList<PositionEntity>();

            List<PositionEntity> allCustomerPositions = new MasterPersistence().retrieveMasterEntities(PositionEntity.class, localeId);
            if (!new ClientRules().getCenterHierarchyExists()) {
                customerPositions = populateWithNonCenterRelatedPositions(allCustomerPositions);
            } else {
                customerPositions.addAll(allCustomerPositions);
            }

            List<CustomerPositionDto> customerPositionDtos = new ArrayList<CustomerPositionDto>();
            generatePositionsFromExistingCustomerPositions(customer, customerPositions, customerPositionDtos);

            if (customerPositionDtos.isEmpty()) {
                generateNewListOfPositions(customer, customerPositions, customerPositionDtos);
            }

            return customerPositionDtos;
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    private List<PositionEntity> populateWithNonCenterRelatedPositions(List<PositionEntity> allCustomerPositions) {
        List<PositionEntity> nonCenterRelatedPositions = new ArrayList<PositionEntity>();
        for (PositionEntity positionEntity : allCustomerPositions) {
            if (!(positionEntity.getId().equals(Short.valueOf("1")) || positionEntity.getId().equals(Short.valueOf("2")))) {
               nonCenterRelatedPositions.add(positionEntity);
            }
        }
        return nonCenterRelatedPositions;
    }

    private void generatePositionsFromExistingCustomerPositions(CustomerBO customer,
            List<PositionEntity> customerPositions, List<CustomerPositionDto> customerPositionDtos) {
        for (PositionEntity position : customerPositions) {
            for (CustomerPositionEntity entity : customer.getCustomerPositions()) {
                if (position.getId().equals(entity.getPosition().getId())) {

                    CustomerPositionDto customerPosition;
                    if (entity.getCustomer() != null) {
                        customerPosition = new CustomerPositionDto(entity.getCustomer().getCustomerId(), entity.getPosition().getId(), entity.getPosition().getName());
                    } else {
                        customerPosition = new CustomerPositionDto(customer.getCustomerId(), entity.getPosition().getId(), entity.getPosition().getName());
                    }

                    customerPositionDtos.add(customerPosition);
                }
            }
        }
    }

    private void generateNewListOfPositions(CustomerBO customer, List<PositionEntity> customerPositions,
            List<CustomerPositionDto> customerPositionDtos) {
        for (PositionEntity position : customerPositions) {
            CustomerPositionDto customerPosition = new CustomerPositionDto(customer.getCustomerId(), position
                    .getId(), position.getName());
            customerPositionDtos.add(customerPosition);
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
    public void updateCenter(UserContext userContext, CenterUpdate centerUpdate) throws ApplicationException {

        customerService.updateCenter(userContext, centerUpdate);
    }

    @Override
    public void updateGroup(UserContext userContext, GroupUpdate groupUpdate) throws ApplicationException {

        customerService.updateGroup(userContext, groupUpdate);
    }

    private void checkVersionMismatch(Integer oldVersionNum, Integer newVersionNum) throws ApplicationException {
        if (!oldVersionNum.equals(newVersionNum)) {
            throw new ApplicationException(Constants.ERROR_VERSION_MISMATCH);
        }
    }

    @Override
    public CustomerSearch search(String searchString, UserContext userContext) throws ApplicationException {

        if (searchString == null) {
            throw new CustomerException(CenterConstants.NO_SEARCH_STRING);
        }

        String normalisedSearchString = org.mifos.framework.util.helpers.SearchUtils
                .normalizeSearchString(searchString);

        if (normalisedSearchString.equals("")) {
            throw new CustomerException(CenterConstants.NO_SEARCH_STRING);
        }

        String officeId = userContext.getBranchId().toString();
        String officeName = this.officeDao.findOfficeDtoById(userContext.getBranchId()).getName();

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
            Integer previousGroupVersionNo) throws ApplicationException {

        CenterBO transferToCenter = this.customerDao.findCenterBySystemId(centerSystemId);
        transferToCenter.setUserContext(userContext);

        GroupBO group = this.customerDao.findGroupBySystemId(groupSystemId);

        checkVersionMismatch(previousGroupVersionNo, group.getVersionNo());
        group.setUserContext(userContext);

        GroupBO transferedGroup = this.customerService.transferGroupTo(group, transferToCenter);

        return transferedGroup;
    }

    @Override
    public GroupBO transferGroupToBranch(String globalCustNum, Short officeId, UserContext userContext,
            Integer previousGroupVersionNo) throws ApplicationException {

        OfficeBO transferToOffice = this.officeDao.findOfficeById(officeId);
        transferToOffice.setUserContext(userContext);

        GroupBO group = this.customerDao.findGroupBySystemId(globalCustNum);

        checkVersionMismatch(previousGroupVersionNo, group.getVersionNo());
        group.setUserContext(userContext);

        GroupBO transferedGroup = this.customerService.transferGroupTo(group, transferToOffice);

        return transferedGroup;
    }

    @Override
    public ClientBO transferClientToGroup(UserContext userContext, Integer groupId, String clientGlobalCustNum, Integer previousClientVersionNo) throws ApplicationException {
        return this.customerService.transferClientTo(userContext, groupId, clientGlobalCustNum, previousClientVersionNo);
    }

    @Override
    public void updateCustomerStatus(Integer customerId, Integer previousCustomerVersionNo, String flagIdAsString,
            String newStatusIdAsString, String notes, UserContext userContext) throws ApplicationException {

        Short flagId = null;
        Short newStatusId = null;
        if (StringUtils.isNotBlank(flagIdAsString)) {
            flagId = Short.valueOf(flagIdAsString);
        }
        if (StringUtils.isNotBlank(newStatusIdAsString)) {
            newStatusId = Short.valueOf(newStatusIdAsString);
        }

        CustomerStatusFlag customerStatusFlag = null;
        if (flagId != null) {
            customerStatusFlag = CustomerStatusFlag.fromInt(flagId);
        }

        CustomerStatus newStatus = CustomerStatus.fromInt(newStatusId);

        CustomerStatusUpdate customerStatusUpdate = new CustomerStatusUpdate(customerId, previousCustomerVersionNo, customerStatusFlag, newStatus, notes);

        this.customerService.updateCustomerStatus(userContext, customerStatusUpdate);
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
    public GroupSearchResultsDto searchGroups(boolean searchForAddingClientsToGroup, String normalizedSearchString,
            Short loggedInUserId) {

        try {
            // FIXME - #000001 - keithw - move search logic off group business service over to customerDAO
            QueryResult searchResults = new GroupBusinessService().search(normalizedSearchString, loggedInUserId);
            QueryResult searchForAddingClientToGroupResults = null;
            if (searchForAddingClientsToGroup) {
                searchForAddingClientToGroupResults = new GroupBusinessService().searchForAddingClientToGroup(
                        normalizedSearchString, loggedInUserId);
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
        boolean duplicateNameOnClosedClient = false;
        boolean duplicateNameOnBlackListedClient = false;

        if (StringUtils.isNotBlank(governmentId)) {
            governmentIdValidationFailing = this.customerDao.validateGovernmentIdForClient(governmentId);
            if (!governmentIdValidationFailing) {
                duplicateNameOnClosedClient = this.customerDao.validateForClosedClientsOnNameAndDob(clientName, dateOfBirth);
                if (!duplicateNameOnClosedClient) {
                    duplicateNameOnBlackListedClient = this.customerDao.validateForBlackListedClientsOnNameAndDob(clientName, dateOfBirth);
                }
            }
        }

        return new ProcessRulesDto(clientPendingApprovalStateEnabled, governmentIdValidationFailing, duplicateNameOnClosedClient, duplicateNameOnBlackListedClient);
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
        return retrieveClientRules();
    }

    @Override
    public void updateClientPersonalInfo(UserContext userContext, Integer oldClientVersionNumber, Integer customerId,
            ClientCustActionForm actionForm) throws ApplicationException {

        List<CustomFieldDto> customFields = actionForm.getCustomFields();

        ClientNameDetailDto spouseFather = null;
        if (!ClientRules.isFamilyDetailsRequired()) {
            spouseFather = spouseFatherName(actionForm);
        }

        InputStream picture = null;
        if (actionForm.getPicture() != null && StringUtils.isNotBlank(actionForm.getPicture().getFileName())) {
            picture = picture(actionForm);
        }

        Address address = address(actionForm);
        ClientNameDetailDto clientNameDetails = clientNameDetailName(actionForm);
        ClientPersonalDetailDto clientDetail = clientPersonalDetailDto(actionForm);

        String governmentId = governmentId(actionForm);
        String clientDisplayName = clientName(actionForm);
        String dateOfBirth = actionForm.getDateOfBirth();

        ClientPersonalInfoUpdate personalInfo = new ClientPersonalInfoUpdate(customerId, oldClientVersionNumber, customFields, address, clientDetail,
                clientNameDetails, spouseFather, picture, governmentId, clientDisplayName, dateOfBirth);

        this.customerService.updateClientPersonalInfo(userContext, personalInfo);
    }

    @Override
    public ClientFamilyInfoDto retrieveFamilyInfoForEdit(String clientGlobalCustNum, UserContext userContext) {

        try {
            ClientBO client = this.customerDao.findClientBySystemId(clientGlobalCustNum);

            List<CustomFieldDefinitionEntity> fieldDefinitions = customerDao.retrieveCustomFieldEntitiesForClient();
            List<CustomFieldDto> customFieldDtos = CustomerCustomFieldEntity.toDto(client.getCustomFields(),
                    fieldDefinitions, userContext);

            ClientDropdownsDto clientDropdowns = retrieveClientDropdownData(userContext);

            ClientRulesDto clientRules = retrieveClientRules();

            CustomerDetailDto customerDetail = client.toCustomerDetailDto();
            ClientDetailDto clientDetail = client.toClientDetailDto(clientRules.isFamilyDetailsRequired());

            List<ClientNameDetailDto> familyMembers = new ArrayList<ClientNameDetailDto>();
            Map<Integer, List<ClientFamilyDetailDto>> clientFamilyDetails = new HashMap<Integer, List<ClientFamilyDetailDto>>();
            int familyMemberCount = 0;
            for (ClientNameDetailEntity clientNameDetailEntity : client.getNameDetailSet()) {

                if (clientNameDetailEntity.isNotClientNameType()) {

                    ClientNameDetailDto nameView1 = clientNameDetailEntity.toDto();
                    familyMembers.add(nameView1);

                    for (ClientFamilyDetailEntity clientFamilyDetailEntity : client.getFamilyDetailSet()) {

                        if (clientNameDetailEntity.matchesCustomerId(clientFamilyDetailEntity.getClientName()
                                .getCustomerNameId())) {
                            ClientFamilyDetailDto clientFamilyDetail = clientFamilyDetailEntity.toDto();

                            addFamilyDetailsDtoToMap(clientFamilyDetails, familyMemberCount, clientFamilyDetail);
                        }
                    }
                    familyMemberCount++;
                }
            }

            return new ClientFamilyInfoDto(clientDropdowns, customFieldDtos, customerDetail, clientDetail,
                    familyMembers, clientFamilyDetails);
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    private void addFamilyDetailsDtoToMap(Map<Integer, List<ClientFamilyDetailDto>> clientFamilyDetails,
            int familyMemberCount, ClientFamilyDetailDto clientFamilyDetail) {
        final Integer mapKey = Integer.valueOf(familyMemberCount);
        if (clientFamilyDetails.containsKey(mapKey)) {
            clientFamilyDetails.get(mapKey).add(clientFamilyDetail);
        } else {
            List<ClientFamilyDetailDto> clientFamilyDetailsList = new ArrayList<ClientFamilyDetailDto>();
            clientFamilyDetailsList.add(clientFamilyDetail);
            clientFamilyDetails.put(mapKey, clientFamilyDetailsList);
        }
    }

    @Override
    public void updateFamilyInfo(Integer customerId, UserContext userContext, Integer oldVersionNum,
            ClientCustActionForm actionForm) throws ApplicationException {

        ClientFamilyInfoUpdate clientFamilyInfoUpdate = new ClientFamilyInfoUpdate(customerId, oldVersionNum, actionForm.getFamilyPrimaryKey(),
                actionForm.getFamilyNames(), actionForm.getFamilyDetails());

        this.customerService.updateClientFamilyInfo(userContext, clientFamilyInfoUpdate);
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

        List<PersonnelDto> loanOfficersList = new ArrayList<PersonnelDto>();
        if (!client.isClientUnderGroup()) {
            CenterCreation centerCreation = new CenterCreation(client.getOffice().getOfficeId(), userContext.getId(),
                    userContext.getLevelId(), userContext.getPreferredLocale());
            loanOfficersList = this.personnelDao.findActiveLoanOfficersForOffice(centerCreation);
        }

        CustomerDetailDto customerDetail = client.toCustomerDetailDto();
        ClientDetailDto clientDetail = client.toClientDetailDto(ClientRules.isFamilyDetailsRequired());
        return new ClientMfiInfoDto(groupDisplayName, centerDisplayName, loanOfficersList, customerDetail, clientDetail);
    }

    @Override
    public void updateClientMfiInfo(Integer clientId, Integer oldVersionNumber, UserContext userContext, ClientCustActionForm actionForm) throws CustomerException {

            boolean trained = false;
            if (trainedValue(actionForm) != null && trainedValue(actionForm).equals(YesNoFlag.YES.getValue())) {
                trained = true;
            }

            DateTime trainedDate;
            try {
                trainedDate = new DateTime(trainedDate(actionForm));
            } catch (InvalidDateException e) {
                throw new CustomerException(ClientConstants.TRAINED_DATE_MANDATORY);
            }

            Short personnelId = Short.valueOf("-1");
            if (groupFlagValue(actionForm).equals(YesNoFlag.NO.getValue())) {
                if (actionForm.getLoanOfficerIdValue() != null) {
                    personnelId = actionForm.getLoanOfficerIdValue();
                }
            } else if (groupFlagValue(actionForm).equals(YesNoFlag.YES.getValue())) {
                personnelId = null;
            }

            ClientMfiInfoUpdate clientMfiInfoUpdate = new ClientMfiInfoUpdate(clientId, oldVersionNumber, personnelId, externalId(actionForm), trained, trainedDate);

            this.customerService.updateClientMfiInfo(userContext, clientMfiInfoUpdate);
    }
}