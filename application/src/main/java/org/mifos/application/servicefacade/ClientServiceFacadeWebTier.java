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

import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.accounts.fees.persistence.FeePersistence;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.persistence.SavingsPrdPersistence;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RankOfDay;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.config.ClientRules;
import org.mifos.config.ProcessFlowRules;
import org.mifos.core.CurrencyMismatchException;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.api.CustomerLevel;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerCustomFieldEntity;
import org.mifos.customers.business.service.CustomerService;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.client.business.ClientDetailEntity;
import org.mifos.customers.client.business.ClientInitialSavingsOfferingEntity;
import org.mifos.customers.client.business.ClientNameDetailEntity;
import org.mifos.customers.client.business.ClientPerformanceHistoryEntity;
import org.mifos.customers.client.persistence.ClientPersistence;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.customers.surveys.helpers.SurveyType;
import org.mifos.customers.surveys.persistence.SurveysPersistence;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.dto.domain.AddressDto;
import org.mifos.dto.domain.ApplicableAccountFeeDto;
import org.mifos.dto.domain.CenterCreation;
import org.mifos.dto.domain.ClientCreationDetail;
import org.mifos.dto.domain.ClientFamilyDetailsDto;
import org.mifos.dto.domain.ClientRulesDto;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.CustomerAccountSummaryDto;
import org.mifos.dto.domain.CustomerAddressDto;
import org.mifos.dto.domain.CustomerDetailsDto;
import org.mifos.dto.domain.CustomerFlagDto;
import org.mifos.dto.domain.CustomerMeetingDto;
import org.mifos.dto.domain.CustomerNoteDto;
import org.mifos.dto.domain.FamilyDetailDto;
import org.mifos.dto.domain.LoanDetailDto;
import org.mifos.dto.domain.MeetingDetailsDto;
import org.mifos.dto.domain.MeetingDto;
import org.mifos.dto.domain.PersonnelDto;
import org.mifos.dto.domain.ProcessRulesDto;
import org.mifos.dto.domain.SavingsDetailDto;
import org.mifos.dto.domain.SurveyDto;
import org.mifos.dto.domain.ValueListElement;
import org.mifos.dto.screen.ClientDisplayDto;
import org.mifos.dto.screen.ClientDropdownsDto;
import org.mifos.dto.screen.ClientFormCreationDto;
import org.mifos.dto.screen.ClientInformationDto;
import org.mifos.dto.screen.ClientNameDetailDto;
import org.mifos.dto.screen.ClientPerformanceHistoryDto;
import org.mifos.dto.screen.LoanCycleCounter;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.LocalizationConverter;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.MifosUser;
import org.mifos.security.util.ActivityMapper;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;
import org.springframework.security.core.context.SecurityContextHolder;

public class ClientServiceFacadeWebTier implements ClientServiceFacade {

    private final OfficeDao officeDao;
    private final PersonnelDao personnelDao;
    private final CustomerDao customerDao;
    private final CustomerService customerService;

    public ClientServiceFacadeWebTier(CustomerService customerService, OfficeDao officeDao,
            PersonnelDao personnelDao, CustomerDao customerDao) {
        this.customerService = customerService;
        this.officeDao = officeDao;
        this.personnelDao = personnelDao;
        this.customerDao = customerDao;
    }

    @Override
    public ClientFormCreationDto retrieveClientFormCreationData(Short groupFlag, Short officeId, String parentGroupId) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

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
        List<ApplicableAccountFeeDto> defaultFees = new ArrayList<ApplicableAccountFeeDto>();
        for (FeeDto fee : applicableFees.getDefaultFees()) {
            defaultFees.add(new ApplicableAccountFeeDto(fee.getFeeIdValue().intValue(), fee.getFeeName(), fee.getAmount(), fee.isRemoved(), fee.isWeekly(), fee.isMonthly(), fee.isPeriodic(), fee.getFeeSchedule()));
        }

        List<ApplicableAccountFeeDto> additionalFees = new ArrayList<ApplicableAccountFeeDto>();
        for (FeeDto fee : applicableFees.getAdditionalFees()) {
            additionalFees.add(new ApplicableAccountFeeDto(fee.getFeeIdValue().intValue(), fee.getFeeName(), fee.getAmount(), fee.isRemoved(), fee.isWeekly(), fee.isMonthly(), fee.isPeriodic(), fee.getFeeSchedule()));
        }

        List<CustomFieldDefinitionEntity> customFieldDefinitions = customerDao.retrieveCustomFieldEntitiesForClient();
        List<CustomFieldDto> customFieldDtos = CustomFieldDefinitionEntity.toDto(customFieldDefinitions, userContext
                .getPreferredLocale());

        List<SavingsDetailDto> savingsOfferings = this.customerDao.retrieveSavingOfferingsApplicableToClient();

            ClientRulesDto clientRules = retrieveClientRules();

            ClientDropdownsDto clientDropdowns = retrieveClientDropdownData();

            List<PersonnelDto> formedByPersonnel = this.customerDao.findLoanOfficerThatFormedOffice(officeId);

            MeetingDto parentMeeting = null;
            if (parentCustomerMeeting != null) {
                parentMeeting = parentCustomerMeeting.toDto();
            }
            return new ClientFormCreationDto(clientDropdowns, customFieldDtos, clientRules, officeId, officeName,
                    formedByPersonnelId, formedByPersonnelName, personnelList, formedByPersonnel,
                    savingsOfferings, parentMeeting, centerDisplayName, groupDisplayName,
                    additionalFees, defaultFees);
    }

    private UserContext toUserContext(MifosUser user) {
        UserContext userContext = new UserContext();
        userContext.setBranchId(user.getBranchId());
        userContext.setId(Short.valueOf((short) user.getUserId()));
        userContext.setName(user.getUsername());
        userContext.setLevelId(user.getLevelId());
        userContext.setRoles(new HashSet<Short>(user.getRoleIds()));
        return userContext;
    }

    private ClientDropdownsDto retrieveClientDropdownData() {
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
                ethinicity, educationLevels, businessActivity, poverty, handicapped, livingStatus);
        return clientDropdowns;
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
    public ClientFamilyDetailsDto retrieveClientFamilyDetails() {

        List<ValueListElement> genders = new ArrayList<ValueListElement>();
        List<ValueListElement> livingStatus = new ArrayList<ValueListElement>();
        List<FamilyDetailDto> familyDetails = new ArrayList<FamilyDetailDto>();
        boolean familyDetailsRequired = ClientRules.isFamilyDetailsRequired();

        if (familyDetailsRequired) {

            genders = this.customerDao.retrieveGenders();
            livingStatus = this.customerDao.retrieveLivingStatus();

            familyDetails.add(new FamilyDetailDto());
        }

        return new ClientFamilyDetailsDto(familyDetailsRequired, familyDetails, genders, livingStatus);
    }

    @Override
    public ProcessRulesDto previewClient(String governmentId, DateTime dateOfBirth, String clientName, boolean defaultFeeRemoval, Short officeId, Short loanOfficerId) {

        try {
            MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserContext userContext = toUserContext(user);

            boolean clientPendingApprovalStateEnabled = ProcessFlowRules.isClientPendingApprovalStateEnabled();
            boolean governmentIdValidationFailing = false;
            boolean duplicateNameOnClosedClient = false;
            boolean duplicateNameOnBlackListedClient = false;

            if (defaultFeeRemoval) {
                customerDao.checkPermissionForDefaultFeeRemoval(userContext, officeId, loanOfficerId);
            }

            if (StringUtils.isNotBlank(governmentId)) {
                governmentIdValidationFailing = this.customerDao.validateGovernmentIdForClient(governmentId);
            }
            if (!governmentIdValidationFailing) {
                duplicateNameOnBlackListedClient = this.customerDao.validateForBlackListedClientsOnNameAndDob(clientName,
                        dateOfBirth);
                if (!duplicateNameOnBlackListedClient) {
                    duplicateNameOnClosedClient = this.customerDao.validateForClosedClientsOnNameAndDob(clientName,
                            dateOfBirth);
                }
            }

            return new ProcessRulesDto(clientPendingApprovalStateEnabled, governmentIdValidationFailing,
                    duplicateNameOnClosedClient, duplicateNameOnBlackListedClient);
        } catch (CustomerException e) {
            throw new BusinessRuleException(e.getKey(), e);
        }
    }

    @Override
    public CustomerDetailsDto createNewClient(ClientCreationDetail actionForm, MeetingDto meetingDto, List<SavingsDetailDto> allowedSavingProducts) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        try {

            ClientBO client = null;
//            CustomerCustomFieldEntity.convertCustomFieldDateToUniformPattern(customerCustomFields, userContext.getPreferredLocale());

            List<AccountFeesEntity> feesForCustomerAccount = convertFeeViewsToAccountFeeEntities(actionForm.getFeesToApply());
            List<SavingsOfferingBO> selectedOfferings = new ArrayList<SavingsOfferingBO>();
            for (Short productId : actionForm.getSelectedSavingProducts()) {
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
//                actionForm.setFamilyDateOfBirth();
//                actionForm.constructFamilyDetails();
            } else {
                spouseNameDetailView = actionForm.getSpouseFatherName();
            }

            String secondMiddleName = null;
            ClientNameDetailEntity clientNameDetailEntity = new ClientNameDetailEntity(null, secondMiddleName, actionForm.getClientNameDetailDto());

            ClientNameDetailEntity spouseFatherNameDetailEntity = null;
            if (spouseNameDetailView != null) {
                spouseFatherNameDetailEntity = new ClientNameDetailEntity(null, secondMiddleName, spouseNameDetailView);
            }

            ClientDetailEntity clientDetailEntity = new ClientDetailEntity();
            clientDetailEntity.updateClientDetails(actionForm.getClientPersonalDetailDto());

            DateTime dob = new DateTime(actionForm.getDateOfBirth());
            boolean trainedBool = actionForm.isTrained();
            DateTime trainedDateTime = null;
            if (actionForm.getTrainedDate() != null) {
                trainedDateTime = new DateTime(actionForm.getTrainedDate());
            }
            String clientFirstName = actionForm.getClientNameDetailDto().getFirstName();
            String clientLastName = actionForm.getClientNameDetailDto().getLastName();
            String secondLastName = actionForm.getClientNameDetailDto().getSecondLastName();

            Blob pictureAsBlob = null;
            if (actionForm.getPicture() != null) {
                pictureAsBlob = new ClientPersistence().createBlob(actionForm.getPicture());
            }

            CustomerStatus clientStatus = CustomerStatus.fromInt(actionForm.getClientStatus());
            PersonnelBO formedBy = this.personnelDao.findPersonnelById(actionForm.getFormedBy());
            Address address = null;
            if (actionForm.getAddress() != null) {
                AddressDto dto = actionForm.getAddress();
                address = new Address(dto.getLine1(), dto.getLine2(), dto.getLine3(), dto.getCity(), dto.getState(), dto.getCountry(), dto.getZip(), dto.getPhoneNumber());
            }

            if (YesNoFlag.YES.getValue().equals(actionForm.getGroupFlag())) {

                Integer parentGroupId = Integer.parseInt(actionForm.getParentGroupId());
                CustomerBO group = this.customerDao.findCustomerById(parentGroupId);

                if (group.getPersonnel() != null) {
                    personnelId = group.getPersonnel().getPersonnelId();
                }

                if (group.getParentCustomer() != null) {
//                    actionForm.setGroupDisplayName(group.getDisplayName());
                    if (group.getParentCustomer() != null) {
//                        actionForm.setCenterDisplayName(group.getParentCustomer().getDisplayName());
                    }
                }

                officeId = group.getOffice().getOfficeId();

                List<CustomerCustomFieldEntity> customerCustomFields = new ArrayList<CustomerCustomFieldEntity>();
                client = ClientBO.createNewInGroupHierarchy(userContext, actionForm.getClientName(), clientStatus, new DateTime(
                       actionForm.getDateOfBirth()), group, formedBy, customerCustomFields, clientNameDetailEntity, dob,
                       actionForm.getGovernmentId(), trainedBool, trainedDateTime, actionForm.getGroupFlag(), clientFirstName, clientLastName,
                        secondLastName, spouseFatherNameDetailEntity, clientDetailEntity, pictureAsBlob,
                        offeringsAssociatedInCreate, actionForm.getExternalId(), address);

                if (ClientRules.isFamilyDetailsRequired()) {
                    client.setFamilyAndNameDetailSets(actionForm.getFamilyNames(), actionForm.getFamilyDetails());
                }

                this.customerService.createClient(client, client.getCustomerMeetingValue(), feesForCustomerAccount,selectedOfferings);

            } else {
                personnelId = actionForm.getLoanOfficerId();
                officeId = actionForm.getOfficeId();

                checkPermissionForCreate(actionForm.getClientStatus(), userContext, officeId, personnelId);

                PersonnelBO loanOfficer = this.personnelDao.findPersonnelById(personnelId);
                OfficeBO office = this.officeDao.findOfficeById(officeId);

                int lastSearchIdCustomerValue = customerDao.retrieveLastSearchIdValueForNonParentCustomersInOffice(officeId);

                MeetingBO clientMeeting = null;
                if (meetingDto != null) {
                    // FIXME - pull out assembly code from dto to domain object for meeting (also in center service facade)
                    MeetingDetailsDto meetingDetailsDto = meetingDto.getMeetingDetailsDto();
                    clientMeeting = new MeetingBO(RecurrenceType.fromInt(meetingDetailsDto.getRecurrenceTypeId().shortValue()),
                                                      meetingDetailsDto.getEvery().shortValue(),
                                                      meetingDto.getMeetingStartDate().toDateMidnight().toDate(),
                                                      MeetingType.CUSTOMER_MEETING);

                    RankOfDay rank = null;
                    Integer weekOfMonth = meetingDetailsDto.getRecurrenceDetails().getWeekOfMonth();
                    if (weekOfMonth != null && weekOfMonth > 0) {
                        rank = RankOfDay.getRankOfDay(meetingDetailsDto.getRecurrenceDetails().getWeekOfMonth()+1);
                    }

                    WeekDay weekDay = null;
                    Integer weekDayNum = meetingDetailsDto.getRecurrenceDetails().getWeekOfMonth();
                    if (weekDayNum != null && weekDayNum > 0) {
                        weekDay = WeekDay.getWeekDay(meetingDetailsDto.getRecurrenceDetails().getDayOfWeek());
                    }

                    if (rank != null && weekDay != null) {
                        clientMeeting = new MeetingBO(weekDay, rank, meetingDetailsDto.getEvery().shortValue(), meetingDto.getMeetingStartDate().toDateMidnight().toDate(), MeetingType.CUSTOMER_MEETING, meetingDto.getMeetingPlace());
                    }
                    clientMeeting.setUserContext(userContext);
                }


                List<CustomerCustomFieldEntity> customerCustomFields = new ArrayList<CustomerCustomFieldEntity>();
                client = ClientBO.createNewOutOfGroupHierarchy(userContext, actionForm.getClientName(), clientStatus, new DateTime(
                        actionForm.getMfiJoiningDate()), office, loanOfficer, clientMeeting, formedBy, customerCustomFields,
                        clientNameDetailEntity, dob, actionForm.getGovernmentId(), trainedBool, trainedDateTime, actionForm.getGroupFlag(),
                        clientFirstName, clientLastName, secondLastName, spouseFatherNameDetailEntity,
                        clientDetailEntity, pictureAsBlob, offeringsAssociatedInCreate, actionForm.getExternalId(), address,
                        lastSearchIdCustomerValue);

                if (ClientRules.isFamilyDetailsRequired()) {
                    client.setFamilyAndNameDetailSets(actionForm.getFamilyNames(), actionForm.getFamilyDetails());
                }

                this.customerService.createClient(client, clientMeeting, feesForCustomerAccount, selectedOfferings);
            }

            return new CustomerDetailsDto(client.getCustomerId(), client.getGlobalCustNum());
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        } catch (CustomerException e) {
            throw new BusinessRuleException(e.getKey(), e);
        } catch (ApplicationException e) {
            throw new BusinessRuleException(e.getKey(), e);
        }
    }

    private List<AccountFeesEntity> convertFeeViewsToAccountFeeEntities(List<ApplicableAccountFeeDto> feesToApply) {
        List<AccountFeesEntity> feesForCustomerAccount = new ArrayList<AccountFeesEntity>();
        for (ApplicableAccountFeeDto feeDto : feesToApply) {
            FeeBO fee = new FeePersistence().getFee(feeDto.getFeeId().shortValue());
            Double feeAmount = new LocalizationConverter().getDoubleValueForCurrentLocale(feeDto.getAmount());

            AccountBO nullReferenceForNow = null;
            AccountFeesEntity accountFee = new AccountFeesEntity(nullReferenceForNow, fee, feeAmount);
            feesForCustomerAccount.add(accountFee);
        }
        return feesForCustomerAccount;
    }

    private void checkPermissionForCreate(Short newState, UserContext userContext, Short recordOfficeId,
            Short recordLoanOfficerId) throws ApplicationException {
        if (!isPermissionAllowed(newState, userContext, recordOfficeId, recordLoanOfficerId)) {
            throw new AccountException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
        }
    }

    private boolean isPermissionAllowed(Short newState, UserContext userContext, Short recordOfficeId,
            Short recordLoanOfficerId) {
        return ActivityMapper.getInstance().isSavePermittedForCustomer(newState.shortValue(), userContext,
                recordOfficeId, recordLoanOfficerId);
    }

    @Override
    public ClientInformationDto getClientInformationDto(String globalCustNum) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        ClientBO client = customerDao.findClientBySystemId(globalCustNum);
        if (client == null) {
            throw new MifosRuntimeException("Client not found for globalCustNum, levelId: " + globalCustNum);
        }

        ClientDisplayDto clientDisplay = this.customerDao.getClientDisplayDto(client.getCustomerId(), userContext);

        Integer clientId = client.getCustomerId();

        CustomerAccountSummaryDto customerAccountSummary = this.customerDao.getCustomerAccountSummaryDto(
                clientId);

        ClientPerformanceHistoryDto clientPerformanceHistory = assembleClientPerformanceHistoryDto(client.getClientPerformanceHistory(), clientId);

        CustomerAddressDto clientAddress = this.customerDao.getCustomerAddressDto(client);

        List<CustomerNoteDto> recentCustomerNotes = customerDao.getRecentCustomerNoteDto(clientId);

        List<CustomerFlagDto> customerFlags = customerDao.getCustomerFlagDto(client.getCustomerFlags());

        List<LoanDetailDto> loanDetail = customerDao.getLoanDetailDto(client.getOpenLoanAccounts());

        List<SavingsDetailDto> savingsDetail = customerDao.getSavingsDetailDto(clientId, userContext);

        CustomerMeetingDto customerMeeting = customerDao.getCustomerMeetingDto(client.getCustomerMeeting(), userContext);

        Boolean activeSurveys = new SurveysPersistence().isActiveSurveysForSurveyType(SurveyType.CLIENT);

        List<SurveyDto> customerSurveys = customerDao.getCustomerSurveyDto(clientId);

        List<CustomFieldDto> customFields = customerDao.getCustomFieldViewForCustomers(clientId,
                EntityType.CLIENT.getValue(), userContext);

        return new ClientInformationDto(clientDisplay, customerAccountSummary, clientPerformanceHistory, clientAddress,
                recentCustomerNotes, customerFlags, loanDetail, savingsDetail, customerMeeting, activeSurveys, customerSurveys, customFields);
    }

    private ClientPerformanceHistoryDto assembleClientPerformanceHistoryDto(
            ClientPerformanceHistoryEntity clientPerformanceHistory, Integer clientId) {

        Integer loanCycleNumber = clientPerformanceHistory.getLoanCycleNumber();

        Money lastLoanAmount = clientPerformanceHistory.getLastLoanAmount();

        Integer noOfActiveLoans = clientPerformanceHistory.getNoOfActiveLoans();

        String delinquentPortfolioAmountString;
        try {
            Money delinquentPortfolioAmount = clientPerformanceHistory.getDelinquentPortfolioAmount();
            delinquentPortfolioAmountString = delinquentPortfolioAmount.toString();
        } catch (CurrencyMismatchException e) {
            delinquentPortfolioAmountString = localizedMessageLookup("errors.multipleCurrencies");
        }

        // TODO currency mismatch check
        Money totalSavingsAmount = clientPerformanceHistory.getTotalSavingsAmount();

        Integer meetingsAttended = this.customerDao.numberOfMeetings(true, clientId).getMeetingsAttended();
        Integer meetingsMissed = customerDao.numberOfMeetings(false, clientId).getMeetingsMissed();

        List<LoanCycleCounter> loanCycleCounters = this.customerDao.fetchLoanCycleCounter(clientId,CustomerLevel.CLIENT.getValue());

        return new ClientPerformanceHistoryDto(loanCycleNumber, lastLoanAmount.toString(), noOfActiveLoans,
                delinquentPortfolioAmountString, totalSavingsAmount.toString(), meetingsAttended, meetingsMissed,
                loanCycleCounters);
    }

    protected String localizedMessageLookup(String key) {
        return MessageLookup.getInstance().lookup(key);
    }
}