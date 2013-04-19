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

package org.mifos.application.servicefacade;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.accounts.fees.persistence.FeeDao;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.persistence.SavingsProductDao;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.servicefacade.UserContextFactory;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.MeetingFactory;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.config.ClientRules;
import org.mifos.config.ProcessFlowRules;
import org.mifos.core.CurrencyMismatchException;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.api.CustomerLevel;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerNoteEntity;
import org.mifos.customers.business.service.CustomerService;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.client.business.ClientDetailEntity;
import org.mifos.customers.client.business.ClientFamilyDetailEntity;
import org.mifos.customers.client.business.ClientInitialSavingsOfferingEntity;
import org.mifos.customers.client.business.ClientNameDetailEntity;
import org.mifos.customers.client.business.ClientPerformanceHistoryEntity;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.dto.domain.AddressDto;
import org.mifos.dto.domain.ApplicableAccountFeeDto;
import org.mifos.dto.domain.CenterCreation;
import org.mifos.dto.domain.ClientCreationDetail;
import org.mifos.dto.domain.ClientFamilyDetailsDto;
import org.mifos.dto.domain.ClientFamilyInfoUpdate;
import org.mifos.dto.domain.ClientMfiInfoUpdate;
import org.mifos.dto.domain.ClientPersonalInfoUpdate;
import org.mifos.dto.domain.ClientRulesDto;
import org.mifos.dto.domain.CustomerAccountSummaryDto;
import org.mifos.dto.domain.CustomerAddressDto;
import org.mifos.dto.domain.CustomerDetailDto;
import org.mifos.dto.domain.CustomerDetailsDto;
import org.mifos.dto.domain.CustomerFlagDto;
import org.mifos.dto.domain.CustomerMeetingDto;
import org.mifos.dto.domain.CustomerNoteDto;
import org.mifos.dto.domain.FamilyDetailDto;
import org.mifos.dto.domain.LoanDetailDto;
import org.mifos.dto.domain.MeetingDto;
import org.mifos.dto.domain.OfficeDetailsDto;
import org.mifos.dto.domain.PersonnelDto;
import org.mifos.dto.domain.ProcessRulesDto;
import org.mifos.dto.domain.SavingsDetailDto;
import org.mifos.dto.domain.SurveyDto;
import org.mifos.dto.domain.ValueListElement;
import org.mifos.dto.screen.ClientDetailDto;
import org.mifos.dto.screen.ClientDisplayDto;
import org.mifos.dto.screen.ClientDropdownsDto;
import org.mifos.dto.screen.ClientFamilyDetailDto;
import org.mifos.dto.screen.ClientFamilyInfoDto;
import org.mifos.dto.screen.ClientFormCreationDto;
import org.mifos.dto.screen.ClientInformationDto;
import org.mifos.dto.screen.ClientMfiInfoDto;
import org.mifos.dto.screen.ClientNameDetailDto;
import org.mifos.dto.screen.ClientPerformanceHistoryDto;
import org.mifos.dto.screen.ClientPersonalInfoDto;
import org.mifos.dto.screen.ClientPhotoDto;
import org.mifos.dto.screen.ClientRemovalFromGroupDto;
import org.mifos.dto.screen.LoanCycleCounter;
import org.mifos.dto.screen.UploadedFileDto;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.fileupload.service.ClientFileService;
import org.mifos.framework.image.domain.ClientPhoto;
import org.mifos.framework.image.service.ClientPhotoService;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.LocalizationConverter;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.security.MifosUser;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

public class ClientServiceFacadeWebTier implements ClientServiceFacade {

    private final OfficeDao officeDao;
    private final PersonnelDao personnelDao;
    private final CustomerDao customerDao;
    private final CustomerService customerService;
    private final FeeDao feeDao;

    @Autowired
    private ClientPhotoService clientPhotoService;

    @Autowired
    private SavingsProductDao savingsProductDao;
    
    @Autowired
    private ClientFileService clientFileService;

    @Autowired
    public ClientServiceFacadeWebTier(CustomerService customerService, OfficeDao officeDao,
            PersonnelDao personnelDao, CustomerDao customerDao, FeeDao feeDao) {
        this.customerService = customerService;
        this.officeDao = officeDao;
        this.personnelDao = personnelDao;
        this.customerDao = customerDao;
        this.feeDao = feeDao;
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

        Short applicableOfficeId = officeId;
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

            applicableOfficeId = parentCustomer.getOffice().getOfficeId();
            officeName = parentCustomer.getOffice().getOfficeName();

            if (parentCustomer.getCustomerMeeting() != null) {
                parentCustomerMeeting = parentCustomer.getCustomerMeetingValue();
                fees = this.customerDao.retrieveFeesApplicableToClientsRefinedBy(parentCustomer
                        .getCustomerMeetingValue());
            } else {
                fees = this.customerDao.retrieveFeesApplicableToClients();
            }

        } else if (YesNoFlag.NO.getValue().equals(groupFlag)) {

            CenterCreation centerCreation = new CenterCreation(applicableOfficeId, userContext.getId(), userContext.getLevelId(), userContext.getPreferredLocale());
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

        List<SavingsDetailDto> savingsOfferings = this.customerDao.retrieveSavingOfferingsApplicableToClient();

        ClientRulesDto clientRules = retrieveClientRules();

        ClientDropdownsDto clientDropdowns = retrieveClientDropdownData();

        List<PersonnelDto> formedByPersonnel = this.customerDao.findLoanOfficerThatFormedOffice(applicableOfficeId);

        MeetingDto parentMeeting = null;
        if (parentCustomerMeeting != null) {
            parentMeeting = parentCustomerMeeting.toDto();
        }

        return new ClientFormCreationDto(clientDropdowns, clientRules, applicableOfficeId, officeName,
                formedByPersonnelId, formedByPersonnelName, personnelList, formedByPersonnel, savingsOfferings,
                parentMeeting, centerDisplayName, groupDisplayName, additionalFees, defaultFees);
    }

    private UserContext toUserContext(MifosUser user) {
        return new UserContextFactory().create(user);
    }

    private ClientDropdownsDto retrieveClientDropdownData() {
        List<ValueListElement> salutations = this.customerDao.retrieveSalutations();
        List<ValueListElement> genders = this.customerDao.retrieveGenders();
        List<ValueListElement> maritalStatuses = this.customerDao.retrieveMaritalStatuses();
        List<ValueListElement> citizenship = this.customerDao.retrieveCitizenship();
        List<ValueListElement> ethnicity = this.customerDao.retrieveEthnicity();
        List<ValueListElement> educationLevels = this.customerDao.retrieveEducationLevels();
        List<ValueListElement> businessActivity = this.customerDao.retrieveBusinessActivities();
        List<ValueListElement> poverty = this.customerDao.retrievePoverty();
        List<ValueListElement> handicapped = this.customerDao.retrieveHandicapped();
        List<ValueListElement> livingStatus = this.customerDao.retrieveLivingStatus();

        ClientDropdownsDto clientDropdowns = new ClientDropdownsDto(salutations, genders, maritalStatuses, citizenship,
                ethnicity, educationLevels, businessActivity, poverty, handicapped, livingStatus);
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
            boolean governmentIdValidationUnclosedFailing = false;
            boolean duplicateNameOnClient = false;
            ClientBO matchedClient = null;

            if (defaultFeeRemoval) {
                customerDao.checkPermissionForDefaultFeeRemoval(userContext, officeId, loanOfficerId);
            }

            if (StringUtils.isNotBlank(governmentId)) {
            	matchedClient = this.customerDao.validateGovernmentIdForUnclosedClient(governmentId);
                governmentIdValidationUnclosedFailing = matchedClient != null;
                if (!governmentIdValidationUnclosedFailing ) {
                	matchedClient = this.customerDao.validateGovernmentIdForClient(governmentId);
                    governmentIdValidationFailing = matchedClient != null;
                }
            }
            if (!governmentIdValidationFailing && !governmentIdValidationUnclosedFailing) {
            	matchedClient = this.customerDao.validateForBlackListedClientsOnNameAndDob(clientName,
                        dateOfBirth);
                duplicateNameOnBlackListedClient = matchedClient != null;
                if (!duplicateNameOnBlackListedClient) {
                	matchedClient = this.customerDao.validateForClosedClientsOnNameAndDob(clientName,
                            dateOfBirth);
                    duplicateNameOnClosedClient = matchedClient != null;
                    if(!duplicateNameOnClosedClient){
                    	matchedClient = this.customerDao.validateForClientsOnName(clientName);
                        duplicateNameOnClient = matchedClient != null;
                    }
                }
            }

            return matchedClient != null ?  
            	new ProcessRulesDto(clientPendingApprovalStateEnabled, governmentIdValidationFailing,
                    duplicateNameOnClosedClient, duplicateNameOnBlackListedClient, governmentIdValidationUnclosedFailing, duplicateNameOnClient, 
                    matchedClient.getGlobalCustNum(), matchedClient.getDisplayName(), matchedClient.getAddress().getPhoneNumber(),
                    matchedClient.getGovernmentId(), matchedClient.getAddress().getDisplayAddress(), matchedClient.getOffice().getOfficeName(), matchedClient.getOffice().getGlobalOfficeNum()) :
            	new ProcessRulesDto(clientPendingApprovalStateEnabled, governmentIdValidationFailing,
                        duplicateNameOnClosedClient, duplicateNameOnBlackListedClient, governmentIdValidationUnclosedFailing, duplicateNameOnClient, 
                        null, null, null, null, null, null, null);
                    	
        } catch (CustomerException e) {
            throw new BusinessRuleException(e.getKey(), e);
        }
    }

    @Override
    public CustomerDetailsDto createNewClient(ClientCreationDetail clientCreationDetail, MeetingDto meetingDto, List<SavingsDetailDto> allowedSavingProducts) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        OfficeBO userOffice = this.officeDao.findOfficeById(userContext.getBranchId());
        userContext.setBranchGlobalNum(userOffice.getGlobalOfficeNum());

        try {

            ClientBO client = null;

            List<AccountFeesEntity> feesForCustomerAccount = convertFeeViewsToAccountFeeEntities(clientCreationDetail.getFeesToApply());
            List<SavingsOfferingBO> selectedOfferings = new ArrayList<SavingsOfferingBO>();
            for (Short productId : clientCreationDetail.getSelectedSavingProducts()) {
                if (productId != null) {
                    for (SavingsDetailDto savingsOffering : allowedSavingProducts) {
                        if (productId.equals(savingsOffering.getPrdOfferingId())) {

                            SavingsOfferingBO savingsProduct = savingsProductDao.findById(productId.intValue());
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
                spouseNameDetailView = clientCreationDetail.getSpouseFatherName();
            }

            String secondMiddleName = null;
            ClientNameDetailEntity clientNameDetailEntity = new ClientNameDetailEntity(null, secondMiddleName, clientCreationDetail.getClientNameDetailDto());

            ClientNameDetailEntity spouseFatherNameDetailEntity = null;
            if (spouseNameDetailView != null) {
                spouseFatherNameDetailEntity = new ClientNameDetailEntity(null, secondMiddleName, spouseNameDetailView);
            }

            ClientDetailEntity clientDetailEntity = new ClientDetailEntity();
            clientDetailEntity.updateClientDetails(clientCreationDetail.getClientPersonalDetailDto());

            DateTime dob = new DateTime(clientCreationDetail.getDateOfBirth());
            boolean trainedBool = clientCreationDetail.isTrained();
            DateTime trainedDateTime = null;
            if (clientCreationDetail.getTrainedDate() != null) {
                trainedDateTime = new DateTime(clientCreationDetail.getTrainedDate());
            }
            String clientFirstName = clientCreationDetail.getClientNameDetailDto().getFirstName();
            String clientLastName = clientCreationDetail.getClientNameDetailDto().getLastName();
            String secondLastName = clientCreationDetail.getClientNameDetailDto().getSecondLastName();

            CustomerStatus clientStatus = CustomerStatus.fromInt(clientCreationDetail.getClientStatus());
            PersonnelBO formedBy = this.personnelDao.findPersonnelById(clientCreationDetail.getFormedBy());
            Address address = null;
            if (clientCreationDetail.getAddress() != null) {
                AddressDto dto = clientCreationDetail.getAddress();
                address = new Address(dto.getLine1(), dto.getLine2(), dto.getLine3(), dto.getCity(), dto.getState(), dto.getCountry(), dto.getZip(), dto.getPhoneNumber());
            }

            if (YesNoFlag.YES.getValue().equals(clientCreationDetail.getGroupFlag())) {

                Integer parentGroupId = Integer.parseInt(clientCreationDetail.getParentGroupId());
                CustomerBO group = this.customerDao.findCustomerById(parentGroupId);

                if (group.getPersonnel() != null) {
                    personnelId = group.getPersonnel().getPersonnelId();
                }

                officeId = group.getOffice().getOfficeId();

                client = ClientBO.createNewInGroupHierarchy(userContext, clientCreationDetail.getClientName(), clientStatus, new DateTime(
                       clientCreationDetail.getMfiJoiningDate()), group, formedBy, clientNameDetailEntity, dob,
                       clientCreationDetail.getGovernmentId(), trainedBool, trainedDateTime, clientCreationDetail.getGroupFlag(), clientFirstName, clientLastName,
                       secondLastName, spouseFatherNameDetailEntity, clientDetailEntity, offeringsAssociatedInCreate,
                       clientCreationDetail.getExternalId(), address, clientCreationDetail.getActivationDate());

                if (ClientRules.isFamilyDetailsRequired()) {
                    client.setFamilyAndNameDetailSets(clientCreationDetail.getFamilyNames(), clientCreationDetail.getFamilyDetails());
                }

                this.customerService.createClient(client, client.getCustomerMeetingValue(), feesForCustomerAccount,selectedOfferings);

            } else {
                personnelId = clientCreationDetail.getLoanOfficerId();
                officeId = clientCreationDetail.getOfficeId();

                PersonnelBO loanOfficer = this.personnelDao.findPersonnelById(personnelId);
                OfficeBO office = this.officeDao.findOfficeById(officeId);

                int lastSearchIdCustomerValue = customerDao.retrieveLastSearchIdValueForNonParentCustomersInOffice(officeId);

                MeetingBO clientMeeting = null;
                if (meetingDto != null) {
                    clientMeeting = new MeetingFactory().create(meetingDto);
                    clientMeeting.setUserContext(userContext);
                }


                client = ClientBO.createNewOutOfGroupHierarchy(userContext, clientCreationDetail.getClientName(), clientStatus, new DateTime(
                        clientCreationDetail.getMfiJoiningDate()), office, loanOfficer, clientMeeting, formedBy,
                        clientNameDetailEntity, dob, clientCreationDetail.getGovernmentId(), trainedBool, trainedDateTime, clientCreationDetail.getGroupFlag(),
                        clientFirstName, clientLastName, secondLastName, spouseFatherNameDetailEntity,
                        clientDetailEntity, offeringsAssociatedInCreate, clientCreationDetail.getExternalId(), address,
                        lastSearchIdCustomerValue);

                if (ClientRules.isFamilyDetailsRequired()) {
                    client.setFamilyAndNameDetailSets(clientCreationDetail.getFamilyNames(), clientCreationDetail.getFamilyDetails());
                }
                
                try {
                    personnelDao.checkAccessPermission(userContext, client.getOfficeId(), client.getLoanOfficerId());
                } catch (AccountException e) {
                    throw new MifosRuntimeException("Access denied!", e);
                }
                this.customerService.createClient(client, clientMeeting, feesForCustomerAccount, selectedOfferings);
            }

                        
            clientPhotoService.create(client.getCustomerId().longValue(), clientCreationDetail.getPicture());

            return new CustomerDetailsDto(client.getCustomerId(), client.getGlobalCustNum());
        } catch (CustomerException e) {
            throw new BusinessRuleException(e.getKey(), e.getValues(), e);
        }
    }

    private List<AccountFeesEntity> convertFeeViewsToAccountFeeEntities(List<ApplicableAccountFeeDto> feesToApply) {
        List<AccountFeesEntity> feesForCustomerAccount = new ArrayList<AccountFeesEntity>();
        for (ApplicableAccountFeeDto feeDto : feesToApply) {
            FeeBO fee = this.feeDao.findById(feeDto.getFeeId().shortValue());
            Double feeAmount = new LocalizationConverter().getDoubleValueForCurrentLocale(feeDto.getAmount());

            AccountBO nullReferenceForNow = null;
            AccountFeesEntity accountFee = new AccountFeesEntity(nullReferenceForNow, fee, feeAmount);
            feesForCustomerAccount.add(accountFee);
        }
        return feesForCustomerAccount;
    }

    @Override
    public ClientInformationDto getClientInformationDto(String globalCustNum) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        ClientBO client = customerDao.findClientBySystemId(globalCustNum);
        if (client == null) {
            throw new MifosRuntimeException("Client not found for globalCustNum, levelId: " + globalCustNum);
        }

        try {
            personnelDao.checkAccessPermission(userContext, client.getOfficeId(), client.getLoanOfficerId());
        } catch (AccountException e) {
            throw new MifosRuntimeException("Access denied!", e);
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
        
        List<LoanDetailDto> groupLoanDetail = customerDao.getLoanDetailDto(client.getOpenGroupLoanAccounts());

        List<SavingsDetailDto> savingsDetail = customerDao.getSavingsDetailDto(clientId, userContext);

        CustomerMeetingDto customerMeeting = customerDao.getCustomerMeetingDto(client.getCustomerMeeting(), userContext);

        List<AccountBO> allClosedLoanAndSavingsAccounts = customerDao.retrieveAllClosedLoanAndSavingsAccounts(clientId);
        List<LoanDetailDto> closedLoanAccounts = new ArrayList<LoanDetailDto>();
        List<SavingsDetailDto> closedSavingsAccounts = new ArrayList<SavingsDetailDto>();
        for (AccountBO closedAccount : allClosedLoanAndSavingsAccounts){
        	if ( closedAccount.getAccountType().getAccountTypeId() == AccountTypes.LOAN_ACCOUNT.getValue().intValue()){      		
        		closedLoanAccounts.add(new LoanDetailDto(closedAccount.getGlobalAccountNum(), 
        				((LoanBO)closedAccount).getLoanOffering().getPrdOfferingName(), closedAccount.getAccountState().getId(), 
        				closedAccount.getAccountState().getName(), ((LoanBO)closedAccount).getLoanSummary().getOutstandingBalance().toString(), 
        				closedAccount.getTotalAmountDue().toString()));
        		
        	} else {
        		closedSavingsAccounts.add(new SavingsDetailDto(closedAccount.getGlobalAccountNum(), 
        				((SavingsBO)closedAccount).getSavingsOffering().getPrdOfferingName(), 
        				closedAccount.getAccountState().getId(), closedAccount.getAccountState().getName(), 
                        ((SavingsBO)closedAccount).getSavingsBalance().toString()));
        	}
        }
        
        Boolean activeSurveys = Boolean.FALSE;
//        Boolean activeSurveys = new SurveysPersistence().isActiveSurveysForSurveyType(SurveyType.CLIENT);

        List<SurveyDto> customerSurveys = new ArrayList<SurveyDto>();

        return new ClientInformationDto(clientDisplay, customerAccountSummary, clientPerformanceHistory, clientAddress,
                recentCustomerNotes, customerFlags, loanDetail, groupLoanDetail, savingsDetail, customerMeeting, activeSurveys, customerSurveys,
                closedLoanAccounts, closedSavingsAccounts);
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
        return ApplicationContextProvider.getBean(MessageLookup.class).lookup(key);
    }

    @Override
    public ClientPersonalInfoDto retrieveClientPersonalInfoForUpdate(String clientSystemId, String clientStatus, short loanOfficerId) {

        ClientDropdownsDto clientDropdowns = retrieveClientDropdownData();

        ClientRulesDto clientRules = retrieveClientRules();

        ClientBO client = this.customerDao.findClientBySystemId(clientSystemId);

        CustomerDetailDto customerDetailDto = client.toCustomerDetailDto();
        ClientDetailDto clientDetailDto = client.toClientDetailDto(clientRules.isFamilyDetailsRequired());

        return new ClientPersonalInfoDto(clientDropdowns, clientRules, customerDetailDto, clientDetailDto);
    }

    @Override
    public ClientRulesDto retrieveClientDetailsForPreviewingEditOfPersonalInfo() {
        return retrieveClientRules();
    }

    @Override
    public void updateClientPersonalInfo(ClientPersonalInfoUpdate personalInfo, String clientStatus, short loanOfficerId) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        try {
            this.customerService.updateClientPersonalInfo(userContext, personalInfo);
        } catch (CustomerException e) {
            throw new BusinessRuleException(e.getKey(), e);
        }
    }
    
    public void removeFromBlacklist(Integer customerId) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        this.customerService.removeFromBlacklist(userContext, customerId);
    }

    @Override
    public ClientFamilyInfoDto retrieveFamilyInfoForEdit(String clientGlobalCustNum) {

        ClientBO client = this.customerDao.findClientBySystemId(clientGlobalCustNum);

        ClientDropdownsDto clientDropdowns = retrieveClientDropdownData();

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

        return new ClientFamilyInfoDto(clientDropdowns, customerDetail, clientDetail, familyMembers, clientFamilyDetails);
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
    public void updateFamilyInfo(ClientFamilyInfoUpdate clientFamilyInfoUpdate) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        try {
            this.customerService.updateClientFamilyInfo(userContext, clientFamilyInfoUpdate);
        } catch (CustomerException e) {
            throw new BusinessRuleException(e.getKey(), e);
        }
    }


    @Override
    public ClientMfiInfoDto retrieveMfiInfoForEdit(String clientSystemId) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

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
    public void updateClientMfiInfo(ClientMfiInfoUpdate clientMfiInfoUpdate) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        try {
            this.customerService.updateClientMfiInfo(userContext, clientMfiInfoUpdate);
        } catch (CustomerException e) {
            throw new BusinessRuleException(e.getKey(), e);
        }
    }

    @Override
    public ClientRemovalFromGroupDto retreiveClientDetailsForRemovalFromGroup(String globalCustNum) {

        try {
            MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserContext userContext = toUserContext(user);

            ClientBO client = this.customerDao.findClientBySystemId(globalCustNum);
            client.updateDetails(userContext);
            client.checkIfClientIsATitleHolder();

            List<OfficeDetailsDto> activeBranches = this.officeDao.findActiveBranches(userContext.getBranchId());
            boolean isCenterHierarchyExists = ClientRules.getCenterHierarchyExists();
            boolean isActive = client.isActive();
            Short loanOfficerId = client.getPersonnel().getPersonnelId();

            CenterCreation clientDetails = new CenterCreation(client.getOfficeId(), userContext.getId(), userContext.getLevelId(), userContext.getPreferredLocale());
            List<PersonnelDto> loanOfficers = this.personnelDao.findActiveLoanOfficersForOffice(clientDetails);

            return new ClientRemovalFromGroupDto(client.getGlobalCustNum(), isActive, isCenterHierarchyExists, loanOfficerId, loanOfficers, activeBranches);
        } catch (CustomerException e) {
            throw new BusinessRuleException(e.getKey(), e);
        }
    }

    @Override
    public void removeGroupMembership(String globalCustNum, Short loanOfficerId, String comment) {

        MifosUser mifosUser = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(mifosUser);

        ClientBO client = this.customerDao.findClientBySystemId(globalCustNum);
        client.updateDetails(userContext);

        PersonnelBO loanOfficer = null;
        if (loanOfficerId != null) {
            loanOfficer = this.personnelDao.findPersonnelById(loanOfficerId);
        }

        PersonnelBO loggedInUser = this.personnelDao.findPersonnelById(userContext.getId());
        java.sql.Date commentDate = new DateTimeService().getCurrentJavaSqlDate();
        CustomerNoteEntity accountNotesEntity = new CustomerNoteEntity(comment, commentDate, loggedInUser, client);

        customerService.removeGroupMembership(client, loanOfficer, accountNotesEntity, userContext.getLocaleId());
    }


    @Override
    public String transferClientToGroup(Integer groupId, String clientGlobalCustNum, Integer previousClientVersionNo) {
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        ClientBO client;
        try {
            client = this.customerService.transferClientTo(userContext, groupId, clientGlobalCustNum, previousClientVersionNo);
            return client.getGlobalCustNum();
        } catch (CustomerException e) {
            throw new BusinessRuleException(e.getKey(), e);
        }
    }

    @Override
    public String transferClientToBranch(String globalCustNum, Short officeId) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        OfficeBO receivingBranch = this.officeDao.findOfficeById(officeId);
        ClientBO client = this.customerDao.findClientBySystemId(globalCustNum);
        client.updateDetails(userContext);

        this.customerService.transferClientTo(client, receivingBranch);

        return client.getGlobalCustNum();
    }

    @Override
    public ClientPhotoDto getClientPhoto(Long clientId) {
        ClientPhoto clientPhoto = clientPhotoService.read(clientId);
        if(clientPhoto == null) {
            return null;
        }
        String contentType = clientPhoto.getImageInfo().getContentType();
        Long contentLength = clientPhoto.getImageInfo().getLength();
        byte[] out = clientPhotoService.getData(clientPhoto);
        return new ClientPhotoDto(contentType, contentLength, out);
    }
    
    public void uploadFile(Integer clientId, InputStream in, UploadedFileDto uploadedFileDto) {
        clientFileService.create(clientId, in, uploadedFileDto);
    }

    /**
     * Workaround for NullPointerException when accessing some struts/jsp page from spring/ftl.
     */
    @Override
    public void putClientBusinessKeyInSession(String globalCustNum, HttpServletRequest request) {
        ClientBO clientBO = customerDao.findClientBySystemId(globalCustNum);
        try {
            SessionUtils.removeThenSetAttribute(Constants.BUSINESS_KEY, clientBO, request);
        } catch (PageExpiredException e) {
            throw new MifosRuntimeException(e);
        }
    }
    
    @Override
    public List<SavingsDetailDto> retrieveSavingsInUseForClient(Integer clientId) {
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        return this.customerDao.getSavingsDetailDto(clientId, userContext);
    }
    
    @Override
    public List<ValueListElement> getClientGenders() {
        return this.customerDao.retrieveGenders();
    }
}
