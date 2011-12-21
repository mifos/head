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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.business.AccountStateMachines;
import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.accounts.fees.persistence.FeeDao;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.servicefacade.UserContextFactory;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.accounts.util.helpers.WaiveEnum;
import org.mifos.application.master.persistence.LegacyMasterDao;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.MeetingFactory;
import org.mifos.application.meeting.util.helpers.MeetingHelper;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.config.ClientRules;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.api.CustomerLevel;
import org.mifos.customers.business.CustomerAccountBO;
import org.mifos.customers.business.CustomerActivityEntity;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerNoteEntity;
import org.mifos.customers.business.CustomerPositionEntity;
import org.mifos.customers.business.CustomerScheduleEntity;
import org.mifos.customers.business.CustomerStatusEntity;
import org.mifos.customers.business.PositionEntity;
import org.mifos.customers.business.service.CustomerService;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.business.PersonnelNotesEntity;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.customers.util.helpers.CustomerStatusFlag;
import org.mifos.dto.domain.AccountFeeScheduleDto;
import org.mifos.dto.domain.AddressDto;
import org.mifos.dto.domain.ApplicableAccountFeeDto;
import org.mifos.dto.domain.AuditLogDto;
import org.mifos.dto.domain.CenterCreation;
import org.mifos.dto.domain.CenterCreationDetail;
import org.mifos.dto.domain.CenterDisplayDto;
import org.mifos.dto.domain.CenterDto;
import org.mifos.dto.domain.CenterInformationDto;
import org.mifos.dto.domain.CenterPerformanceHistoryDto;
import org.mifos.dto.domain.CenterUpdate;
import org.mifos.dto.domain.CreateAccountFeeDto;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.CustomerAccountSummaryDto;
import org.mifos.dto.domain.CustomerAddressDto;
import org.mifos.dto.domain.CustomerChargesDetailsDto;
import org.mifos.dto.domain.CustomerDetailDto;
import org.mifos.dto.domain.CustomerDetailsDto;
import org.mifos.dto.domain.CustomerDto;
import org.mifos.dto.domain.CustomerMeetingDto;
import org.mifos.dto.domain.CustomerNoteDto;
import org.mifos.dto.domain.CustomerPositionDto;
import org.mifos.dto.domain.CustomerPositionOtherDto;
import org.mifos.dto.domain.CustomerScheduleDto;
import org.mifos.dto.domain.MeetingDto;
import org.mifos.dto.domain.PersonnelDto;
import org.mifos.dto.domain.SavingsDetailDto;
import org.mifos.dto.domain.SurveyDto;
import org.mifos.dto.domain.UserDetailDto;
import org.mifos.dto.screen.AccountFeesDto;
import org.mifos.dto.screen.CenterFormCreationDto;
import org.mifos.dto.screen.ClosedAccountDto;
import org.mifos.dto.screen.CustomerNoteFormDto;
import org.mifos.dto.screen.CustomerRecentActivityDto;
import org.mifos.dto.screen.CustomerStatusDetailDto;
import org.mifos.dto.screen.ListElement;
import org.mifos.dto.screen.TransactionHistoryDto;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.components.audit.business.service.AuditBusinessService;
import org.mifos.framework.components.audit.util.helpers.AuditLogView;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.StatesInitializationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelperForStaticHibernateUtil;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.LocalizationConverter;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.MifosUser;
import org.mifos.security.util.ActivityMapper;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

public class CenterServiceFacadeWebTier implements CenterServiceFacade {

    private final OfficeDao officeDao;
    private final PersonnelDao personnelDao;
    private final CustomerDao customerDao;
    private final CustomerService customerService;

    @Autowired
    private FeeDao feeDao;

    private HibernateTransactionHelper transactionHelper = new HibernateTransactionHelperForStaticHibernateUtil();

    @Autowired
    LegacyMasterDao legacyMasterDao;

    @Autowired
    public CenterServiceFacadeWebTier(CustomerService customerService, OfficeDao officeDao,
            PersonnelDao personnelDao, CustomerDao customerDao) {
        this.customerService = customerService;
        this.officeDao = officeDao;
        this.personnelDao = personnelDao;
        this.customerDao = customerDao;
    }

    private UserContext toUserContext(MifosUser user) {
        return new UserContextFactory().create(user);
    }

    @Override
    public CenterFormCreationDto retrieveCenterFormCreationData(CenterCreation centerCreation) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        List<PersonnelDto> activeLoanOfficersForBranch = personnelDao.findActiveLoanOfficersForOffice(centerCreation);

        List<FeeBO> fees = customerDao.retrieveFeesApplicableToCenters();
        CustomerApplicableFeesDto applicableFees = CustomerApplicableFeesDto.toDto(fees, userContext);

        List<ApplicableAccountFeeDto> applicableDefaultAccountFees = new ArrayList<ApplicableAccountFeeDto>();
        for (FeeDto fee : applicableFees.getDefaultFees()) {
            applicableDefaultAccountFees.add(new ApplicableAccountFeeDto(fee.getFeeIdValue().intValue(), fee.getFeeName(), fee.getAmount(), fee.isRemoved(), fee.isWeekly(), fee.isMonthly(), fee.isPeriodic(), fee.getFeeSchedule()));
        }

        List<ApplicableAccountFeeDto> applicableDefaultAdditionalFees = new ArrayList<ApplicableAccountFeeDto>();
        for (FeeDto fee : applicableFees.getAdditionalFees()) {
            applicableDefaultAdditionalFees.add(new ApplicableAccountFeeDto(fee.getFeeIdValue().intValue(), fee.getFeeName(), fee.getAmount(), fee.isRemoved(), fee.isWeekly(), fee.isMonthly(), fee.isPeriodic(), fee.getFeeSchedule()));
        }

        return new CenterFormCreationDto(activeLoanOfficersForBranch, applicableDefaultAdditionalFees, applicableDefaultAccountFees);
    }

    @Override
    public CustomerDetailsDto createNewCenter(CenterCreationDetail createCenterDetail, MeetingDto meetingDto) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        OfficeBO userOffice = this.officeDao.findOfficeById(userContext.getBranchId());
        userContext.setBranchGlobalNum(userOffice.getGlobalOfficeNum());

        String centerName = createCenterDetail.getDisplayName();
        String externalId = createCenterDetail.getExternalId();
        AddressDto addressDto = createCenterDetail.getAddressDto();
        Address centerAddress = new Address(addressDto.getLine1(), addressDto.getLine2(), addressDto.getLine3(),
                addressDto.getCity(), addressDto.getState(), addressDto.getCountry(), addressDto.getZip(), addressDto.getPhoneNumber());
        PersonnelBO loanOfficer = this.personnelDao.findPersonnelById(createCenterDetail.getLoanOfficerId());
        OfficeBO centerOffice = this.officeDao.findOfficeById(createCenterDetail.getOfficeId());

        List<AccountFeesEntity> feesForCustomerAccount = createAccountFeeEntities(createCenterDetail.getFeesToApply());

        DateTime mfiJoiningDate = null;
        if (createCenterDetail.getMfiJoiningDate() != null) {
            mfiJoiningDate = createCenterDetail.getMfiJoiningDate().toDateMidnight().toDateTime();
        }

        MeetingBO meeting = new MeetingFactory().create(meetingDto);
        meeting.setUserContext(userContext);

        CenterBO center = CenterBO.createNew(userContext, centerName, mfiJoiningDate, meeting, loanOfficer,
                centerOffice, centerAddress, externalId, new DateMidnight().toDateTime());

        this.customerService.createCenter(center, meeting, feesForCustomerAccount);

        return new CustomerDetailsDto(center.getCustomerId(), center.getGlobalCustNum());
    }

    private List<AccountFeesEntity> createAccountFeeEntities(List<CreateAccountFeeDto> feesToApply) {
        List<AccountFeesEntity> feesForCustomerAccount = new ArrayList<AccountFeesEntity>();
        for (CreateAccountFeeDto feeDto : feesToApply) {
            FeeBO fee = feeDao.findById(feeDto.getFeeId().shortValue());
            Double feeAmount = new LocalizationConverter().getDoubleValueForCurrentLocale(feeDto.getAmount());

            AccountBO nullReferenceForNow = null;
            AccountFeesEntity accountFee = new AccountFeesEntity(nullReferenceForNow, fee, feeAmount);
            feesForCustomerAccount.add(accountFee);
        }
        return feesForCustomerAccount;
    }

    @Override
    public CenterDto retrieveCenterDetailsForUpdate(Integer centerId) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        CustomerBO center = customerDao.findCustomerById(centerId);

        Short officeId = center.getOffice().getOfficeId();
        String searchId = center.getSearchId();
        Short loanOfficerId = extractLoanOfficerId(center);

        CenterCreation centerCreation = new CenterCreation(officeId, userContext.getId(), userContext.getLevelId(),
                userContext.getPreferredLocale());
        List<PersonnelDto> activeLoanOfficersForBranch = personnelDao.findActiveLoanOfficersForOffice(centerCreation);

        List<CustomerDto> customerList = customerDao.findClientsThatAreNotCancelledOrClosed(searchId, officeId);

        List<CustomerPositionDto> customerPositionDtos = generateCustomerPositionViews(center, userContext
                .getLocaleId());

        DateTime mfiJoiningDate = new DateTime();
        String mfiJoiningDateAsString = "";
        if (center.getMfiJoiningDate() != null) {
            mfiJoiningDate = new DateTime(center.getMfiJoiningDate());
            mfiJoiningDateAsString = DateUtils.getUserLocaleDate(userContext.getPreferredLocale(), center
                    .getMfiJoiningDate().toString());
        }

        AddressDto address = null;
        if (center.getAddress() != null) {
            address = Address.toDto(center.getAddress());
        }
        return new CenterDto(loanOfficerId, center.getCustomerId(), center.getGlobalCustNum(), mfiJoiningDate,
                mfiJoiningDateAsString, center.getExternalId(), address, customerPositionDtos,
                customerList, activeLoanOfficersForBranch, true);
    }

    private Short extractLoanOfficerId(CustomerBO customer) {
        Short loanOfficerId = null;
        PersonnelBO loanOfficer = customer.getPersonnel();
        if (loanOfficer != null) {
            loanOfficerId = loanOfficer.getPersonnelId();
        }
        return loanOfficerId;
    }

    private List<CustomerPositionDto> generateCustomerPositionViews(CustomerBO customer, Short localeId) {

        List<PositionEntity> customerPositions = new ArrayList<PositionEntity>();

        List<PositionEntity> allCustomerPositions = legacyMasterDao.findMasterDataEntitiesWithLocale(PositionEntity.class);
        if (!ClientRules.getCenterHierarchyExists()) {
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
    }

    private List<PositionEntity> populateWithNonCenterRelatedPositions(List<PositionEntity> allCustomerPositions) {
        List<PositionEntity> nonCenterRelatedPositions = new ArrayList<PositionEntity>();
        for (PositionEntity positionEntity : allCustomerPositions) {
            if (!(positionEntity.getId().equals(Short.valueOf("1")) || positionEntity.getId()
                    .equals(Short.valueOf("2")))) {
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
                        customerPosition = new CustomerPositionDto(entity.getCustomer().getCustomerId(), entity
                                .getPosition().getId(), entity.getPosition().getName());
                    } else {
                        customerPosition = new CustomerPositionDto(customer.getCustomerId(), entity.getPosition()
                                .getId(), entity.getPosition().getName());
                    }

                    customerPositionDtos.add(customerPosition);
                }
            }
        }
    }

    private void generateNewListOfPositions(CustomerBO customer, List<PositionEntity> customerPositions,
            List<CustomerPositionDto> customerPositionDtos) {
        for (PositionEntity position : customerPositions) {
            CustomerPositionDto customerPosition = new CustomerPositionDto(customer.getCustomerId(), position.getId(),
                    position.getName());
            customerPositionDtos.add(customerPosition);
        }
    }

    @Override
    public void updateCenter(CenterUpdate centerUpdate) {
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        try {
            customerService.updateCenter(userContext, centerUpdate);
        } catch (ApplicationException e) {
            throw new BusinessRuleException(e.getKey(), e);
        }
    }

    @Override
    public CenterInformationDto getCenterInformationDto(String globalCustNum) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        CenterBO center = customerDao.findCenterBySystemId(globalCustNum);
        if (center == null) {
            throw new MifosRuntimeException("Center not found for globalCustNum" + globalCustNum);
        }

        try {
            personnelDao.checkAccessPermission(userContext, center.getOfficeId(), center.getLoanOfficerId());
        } catch (AccountException e) {
            throw new MifosRuntimeException("Access denied!", e);
        }

        CenterDisplayDto centerDisplay = customerDao.getCenterDisplayDto(center.getCustomerId(),
                userContext);

        Integer centerId = center.getCustomerId();
        String searchId = center.getSearchId();
        Short branchId = centerDisplay.getBranchId();

        CustomerAccountSummaryDto customerAccountSummary = customerDao.getCustomerAccountSummaryDto(centerId);

        CenterPerformanceHistoryDto centerPerformanceHistory = customerDao.getCenterPerformanceHistory(searchId, branchId);

        CustomerAddressDto centerAddress = customerDao.getCustomerAddressDto(center);

        List<CustomerDetailDto> groups = customerDao.getGroupsOtherThanClosedAndCancelledForGroup(searchId, branchId);

        List<CustomerNoteDto> recentCustomerNotes = customerDao.getRecentCustomerNoteDto(centerId);

        List<CustomerPositionOtherDto> customerPositions = customerDao.getCustomerPositionDto(centerId, userContext);

        List<SavingsDetailDto> savingsDetail = customerDao.getSavingsDetailDto(centerId, userContext);

        CustomerMeetingDto customerMeeting = customerDao.getCustomerMeetingDto(center.getCustomerMeeting(), userContext);

        Boolean activeSurveys = Boolean.FALSE;//new SurveysPersistence().isActiveSurveysForSurveyType(SurveyType.CENTER);

        List<SurveyDto> customerSurveys = new ArrayList<SurveyDto>();

        List<CustomFieldDto> customFields = new ArrayList<CustomFieldDto>();

        return new CenterInformationDto(centerDisplay, customerAccountSummary, centerPerformanceHistory, centerAddress,
                groups, recentCustomerNotes, customerPositions, savingsDetail, customerMeeting, activeSurveys,
                customerSurveys, customFields);
    }

    @Override
    public void updateCustomerStatus(Integer customerId, Integer previousCustomerVersionNo, String flagIdAsString,
            String newStatusIdAsString, String notes) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

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

        CustomerStatusUpdate customerStatusUpdate = new CustomerStatusUpdate(customerId, previousCustomerVersionNo,
                customerStatusFlag, newStatus, notes);

        try {
            this.customerService.updateCustomerStatus(userContext, customerStatusUpdate);
        } catch (CustomerException e) {
            throw new BusinessRuleException(e.getKey(), e);
        }
    }

    @Override
    public void initializeCenterStates(String centerGlobalNum) {
        CenterBO center = this.customerDao.findCenterBySystemId(centerGlobalNum);

        try {
            List<ListElement> savingsStatesList = new ArrayList<ListElement>();
            AccountStateMachines.getInstance().initializeCenterStates();

            List<CustomerStatusEntity> statusList = AccountStateMachines.getInstance().getCenterStatusList(center.getCustomerStatus());
            for (CustomerStatusEntity customerState : statusList) {
                savingsStatesList.add(new ListElement(customerState.getId().intValue(), customerState.getName()));
            }
        } catch (StatesInitializationException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public void initializeGroupStates(String groupGlobalNum) {
        GroupBO group = this.customerDao.findGroupBySystemId(groupGlobalNum);

        try {
            List<ListElement> savingsStatesList = new ArrayList<ListElement>();
            AccountStateMachines.getInstance().initializeGroupStates();

            List<CustomerStatusEntity> statusList = AccountStateMachines.getInstance().getGroupStatusList(group.getCustomerStatus());
            for (CustomerStatusEntity customerState : statusList) {
                savingsStatesList.add(new ListElement(customerState.getId().intValue(), customerState.getName()));
            }
        } catch (StatesInitializationException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public void initializeClientStates(String clientGlobalNum) {
        ClientBO client = this.customerDao.findClientBySystemId(clientGlobalNum);

        try {
            List<ListElement> savingsStatesList = new ArrayList<ListElement>();
            AccountStateMachines.getInstance().initializeClientStates();

            List<CustomerStatusEntity> statusList = AccountStateMachines.getInstance().getClientStatusList(client.getCustomerStatus());
            for (CustomerStatusEntity customerState : statusList) {
                savingsStatesList.add(new ListElement(customerState.getId().intValue(), customerState.getName()));
            }
        } catch (StatesInitializationException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public CustomerStatusDetailDto retrieveCustomerStatusDetails(Short newStatusId, Short flagIdValue, Short customerLevelId) {

        CustomerLevel customerLevel = CustomerLevel.getLevel(customerLevelId);
        CustomerStatus customerStatus = CustomerStatus.fromInt(newStatusId);

        CustomerStatusFlag statusFlag = null;
        if (customerStatus.isCustomerCancelledOrClosed()) {
            statusFlag = CustomerStatusFlag.getStatusFlag(flagIdValue);
        }

        String statusName = AccountStateMachines.getInstance().getCustomerStatusName(customerStatus, customerLevel);
        String flagName = AccountStateMachines.getInstance().getCustomerFlagName(statusFlag, customerLevel);

        return new CustomerStatusDetailDto(statusName, flagName);
    }

    @Override
    public CustomerChargesDetailsDto retrieveChargesDetails(Integer customerId) {

        MifosUser mifosUser = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(mifosUser);

        CustomerBO customerBO = this.customerDao.findCustomerById(customerId);
        CustomerAccountBO customerAccount = customerBO.getCustomerAccount();

        List<AccountFeesDto> accountFeesDtos = new ArrayList<AccountFeesDto>();
        if(!customerAccount.getAccountFees().isEmpty()) {
            for (AccountFeesEntity accountFeesEntity: customerAccount.getAccountFees()) {
                AccountFeesDto accountFeesDto = new AccountFeesDto(accountFeesEntity.getFees().getFeeFrequency().getFeeFrequencyType().getId(),
                        (accountFeesEntity.getFees().getFeeFrequency().getFeePayment() != null ?
                                accountFeesEntity.getFees().getFeeFrequency().getFeePayment().getId() : null),
                        accountFeesEntity.getFeeStatus(), accountFeesEntity.getFees().getFeeName(),
                        accountFeesEntity.getAccountFeeAmount().toString(),
                        getMeetingRecurrence(accountFeesEntity.getFees().getFeeFrequency().getFeeMeetingFrequency(),
                                userContext),
                        accountFeesEntity.getFees().getFeeId());
                accountFeesDtos.add(accountFeesDto);
            }
        }

        CustomerScheduleDto customerSchedule = null;
        CustomerScheduleEntity scheduleEntity = (CustomerScheduleEntity) customerAccount.getUpcomingInstallment();
        if (scheduleEntity != null) {
            Set<AccountFeesActionDetailEntity> feeEntities =  scheduleEntity.getAccountFeesActionDetails();

            List<AccountFeeScheduleDto> feeDtos = new ArrayList<AccountFeeScheduleDto>();
            for (AccountFeesActionDetailEntity feeEntity : feeEntities) {
                feeDtos.add(convertToDto(feeEntity));
            }

            customerSchedule = new CustomerScheduleDto(scheduleEntity.getMiscFee().toString(),
                    scheduleEntity.getMiscFeePaid().toString(), scheduleEntity.getMiscPenalty().toString(),
                    scheduleEntity.getMiscPenaltyPaid().toString(), feeDtos);
        }

        return new CustomerChargesDetailsDto(customerAccount.getNextDueAmount().toString(),
                customerAccount.getTotalAmountInArrears().toString(),
                customerAccount.getTotalAmountDue().toString(),
                customerAccount.getUpcomingChargesDate(),
                customerSchedule,
                accountFeesDtos);
    }

    @Override
    public List<CustomerRecentActivityDto> retrieveRecentActivities(Integer customerId, Integer countOfActivities) {
        CustomerBO customerBO = this.customerDao.findCustomerById(customerId);
        List<CustomerActivityEntity> customerActivityDetails = customerBO.getCustomerAccount().getCustomerActivitDetails();

        List<CustomerRecentActivityDto> customerActivityViewList = new ArrayList<CustomerRecentActivityDto>();
        int count = 0;
        for (CustomerActivityEntity customerActivityEntity : customerActivityDetails) {
            customerActivityViewList.add(getCustomerActivityView(customerActivityEntity));
            if (++count == countOfActivities) {
                break;
            }
        }
        return customerActivityViewList;
    }

    private CustomerRecentActivityDto getCustomerActivityView(CustomerActivityEntity customerActivityEntity) {

        CustomerRecentActivityDto customerRecentActivityDto = new CustomerRecentActivityDto();
        String preferredDate = DateUtils.getUserLocaleDate(Locale.getDefault(), customerActivityEntity.getCreatedDate().toString());
        customerRecentActivityDto.setUserPrefferedDate(preferredDate);
        customerRecentActivityDto.setActivityDate(customerActivityEntity.getCreatedDate());
        customerRecentActivityDto.setDescription(customerActivityEntity.getDescription());
        Money amount = removeSign(customerActivityEntity.getAmount());
        if (amount.isZero()) {
            customerRecentActivityDto.setAmount("-");
        } else {
            customerRecentActivityDto.setAmount(amount.toString());
        }
        if (customerActivityEntity.getPersonnel() != null) {
            customerRecentActivityDto.setPostedBy(customerActivityEntity.getPersonnel().getDisplayName());
        }
        return customerRecentActivityDto;
    }

    private Money removeSign(Money amount) {
        if (amount != null && amount.isLessThanZero()) {
            return amount.negate();
        }
        return amount;
    }

    @Override
    public CustomerNoteFormDto retrieveCustomerNote(String globalCustNum) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        CustomerBO customer = this.customerDao.findCustomerBySystemId(globalCustNum);
        PersonnelBO loggedInUser = this.personnelDao.findPersonnelById(userContext.getId());

        Integer customerLevel = customer.getCustomerLevel().getId().intValue();
        String globalNum = customer.getGlobalCustNum();
        String displayName = customer.getDisplayName();
        LocalDate commentDate = new LocalDate();
        String commentUser = loggedInUser.getDisplayName();

        return new CustomerNoteFormDto(globalNum, displayName, customerLevel, commentDate, commentUser, "");
    }

    @Override
    public void createCustomerNote(CustomerNoteFormDto customerNoteForm) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        CustomerBO customer = this.customerDao.findCustomerBySystemId(customerNoteForm.getGlobalNum());
        customer.updateDetails(userContext);

        PersonnelBO loggedInUser = this.personnelDao.findPersonnelById(userContext.getId());

        CustomerNoteEntity customerNote = new CustomerNoteEntity(customerNoteForm.getComment(),
                new DateTimeService().getCurrentJavaSqlDate(), loggedInUser, customer);
        customer.addCustomerNotes(customerNote);

        try {
            this.transactionHelper.startTransaction();
            this.customerDao.save(customer);
            this.transactionHelper.commitTransaction();
        } catch (Exception e) {
            this.transactionHelper.rollbackTransaction();
            throw new BusinessRuleException(customer.getCustomerAccount().getAccountId().toString(), e);
        } finally {
            this.transactionHelper.closeSession();
        }
    }

    @Override
    public List<ClosedAccountDto> retrieveAllClosedAccounts(Integer customerId) {
        List<ClosedAccountDto> closedAccounts = new ArrayList<ClosedAccountDto>();

        List<AccountBO> allClosedAccounts = this.customerDao.retrieveAllClosedLoanAndSavingsAccounts(customerId);
        for (AccountBO account : allClosedAccounts) {
            LocalDate closedDate = new LocalDate(account.getClosedDate());
            ClosedAccountDto closedAccount = new ClosedAccountDto(account.getAccountId(), account.getGlobalAccountNum(), account.getType().getValue().intValue(), account.getState().getValue().intValue(), closedDate);
            closedAccounts.add(closedAccount);
        }
        return closedAccounts;
    }

    @Override
    public List<TransactionHistoryDto> retrieveAccountTransactionHistory(String globalAccountNum) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        try {
            AccountBO account = new AccountBusinessService().findBySystemId(globalAccountNum);
            account.updateDetails(userContext);

            List<TransactionHistoryDto> transactionHistories = account.getTransactionHistoryView();
            for (TransactionHistoryDto transactionHistoryDto : transactionHistories) {
                transactionHistoryDto.setUserPrefferedPostedDate(DateUtils.getUserLocaleDate(userContext.getPreferredLocale(), transactionHistoryDto.getPostedDate().toString()));
                transactionHistoryDto.setUserPrefferedTransactionDate(DateUtils.getUserLocaleDate(userContext.getPreferredLocale(), transactionHistoryDto.getTransactionDate().toString()));
            }
            return transactionHistories;
        } catch (ServiceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public List<CustomerRecentActivityDto> retrieveAllAccountActivity(String globalCustNum) {
        List<CustomerRecentActivityDto> customerActivityViewList = new ArrayList<CustomerRecentActivityDto>();

        CustomerBO customerBO = this.customerDao.findCustomerBySystemId(globalCustNum);
        List<CustomerActivityEntity> customerActivityDetails = customerBO.getCustomerAccount().getCustomerActivitDetails();

        for (CustomerActivityEntity customerActivityEntity : customerActivityDetails) {
            customerActivityViewList.add(assembleCustomerActivityDto(customerActivityEntity, Locale.getDefault()));
        }
        return customerActivityViewList;
    }

    private CustomerRecentActivityDto assembleCustomerActivityDto(CustomerActivityEntity customerActivityEntity, Locale locale) {
        CustomerRecentActivityDto customerRecentActivityDto = new CustomerRecentActivityDto();

        String preferredDate = DateUtils.getUserLocaleDate(locale, customerActivityEntity.getCreatedDate().toString());
        customerRecentActivityDto.setActivityDate(customerActivityEntity.getCreatedDate());
        customerRecentActivityDto.setUserPrefferedDate(preferredDate);
        customerRecentActivityDto.setDescription(customerActivityEntity.getDescription());
        Money amount = removeSign(customerActivityEntity.getAmount());
        if (amount.isZero()) {
            customerRecentActivityDto.setAmount("-");
        } else {
            customerRecentActivityDto.setAmount(amount.toString());
        }
        if (customerActivityEntity.getPersonnel() != null) {
            customerRecentActivityDto.setPostedBy(customerActivityEntity.getPersonnel().getDisplayName());
        }
        return customerRecentActivityDto;
    }

    @Override
    public void waiveChargesDue(Integer accountId, Integer waiveType) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        try {
            AccountBO account = new AccountBusinessService().getAccount(accountId);
            account.updateDetails(userContext);
            PersonnelBO loggedInUser = this.personnelDao.findPersonnelById(userContext.getId());

            WaiveEnum waiveEnum = WaiveEnum.fromInt(waiveType);
            if (account.getPersonnel() != null) {
                new AccountBusinessService().checkPermissionForWaiveDue(waiveEnum, account.getType(),
                        account.getCustomer().getLevel(), userContext, account.getOffice().getOfficeId(),
                        account.getPersonnel().getPersonnelId());
            } else {
                new AccountBusinessService().checkPermissionForWaiveDue(waiveEnum, account.getType(),
                        account.getCustomer().getLevel(), userContext, account.getOffice().getOfficeId(), userContext.getId());
            }

            if (account.isLoanAccount()) {
                ((LoanBO)account).waiveAmountDue(waiveEnum);
            } else if (account.isSavingsAccount()) {
                ((SavingsBO)account).waiveNextDepositAmountDue(loggedInUser);
            } else  {
                try {
                    this.transactionHelper.startTransaction();
                    ((CustomerAccountBO)account).waiveAmountDue();
                    this.customerDao.save(account);
                    this.transactionHelper.commitTransaction();
                } catch (Exception e) {
                    this.transactionHelper.rollbackTransaction();
                    throw new BusinessRuleException(account.getAccountId().toString(), e);
                } finally {
                    this.transactionHelper.closeSession();
                }
            }
        } catch (ServiceException e) {
            throw new MifosRuntimeException(e);
        } catch (ApplicationException e) {
            throw new BusinessRuleException(e.getKey(), e);
        }
    }

    @Override
    public void waiveChargesOverDue(Integer accountId, Integer waiveType) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        try {
            AccountBO account = new AccountBusinessService().getAccount(accountId);
            account.updateDetails(userContext);

            WaiveEnum waiveEnum = WaiveEnum.fromInt(waiveType);

            if (account.getPersonnel() != null) {
                new AccountBusinessService().checkPermissionForWaiveDue(waiveEnum, account.getType(), account
                        .getCustomer().getLevel(), userContext, account.getOffice().getOfficeId(), account
                        .getPersonnel().getPersonnelId());
            } else {
                new AccountBusinessService().checkPermissionForWaiveDue(waiveEnum, account.getType(), account
                        .getCustomer().getLevel(), userContext, account.getOffice().getOfficeId(), userContext.getId());
            }

            this.transactionHelper.startTransaction();
            account.waiveAmountOverDue(waiveEnum);
            this.customerDao.save(account);
            this.transactionHelper.commitTransaction();

        } catch (ServiceException e) {
            this.transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } catch (ApplicationException e) {
            this.transactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getKey(), e);
        } finally {
            this.transactionHelper.closeSession();
        }
    }

    @Override
    public void removeAccountFee(Integer accountId, Short feeId) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        try {
            AccountBO account = new AccountBusinessService().getAccount(accountId);

            account.updateDetails(userContext);

            if (account.getPersonnel() != null) {
                new AccountBusinessService().checkPermissionForRemoveFees(account.getType(),
                        account.getCustomer().getLevel(), userContext, account.getOffice().getOfficeId(),
                        account.getPersonnel().getPersonnelId());
            } else {
                new AccountBusinessService().checkPermissionForRemoveFees(account.getType(),
                        account.getCustomer().getLevel(), userContext, account.getOffice().getOfficeId(), userContext.getId());
            }

            this.transactionHelper.startTransaction();
            account.removeFeesAssociatedWithUpcomingAndAllKnownFutureInstallments(feeId, userContext.getId());
            this.customerDao.save(account);
            this.transactionHelper.commitTransaction();
        } catch (ServiceException e) {
            this.transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } catch (AccountException e) {
            this.transactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getKey(), e);
        } catch (ApplicationException e) {
            this.transactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getKey(), e);
        } finally {
            this.transactionHelper.closeSession();
        }
    }

    @Override
    public void revertLastChargesPayment(String globalCustNum, String adjustmentNote) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        PersonnelBO loggedInUser = this.personnelDao.findPersonnelById(userContext.getId());
        CustomerBO customerBO = this.customerDao.findCustomerBySystemId(globalCustNum);
        customerBO.updateDetails(userContext);

        if (customerBO.getCustomerAccount().findMostRecentNonzeroPaymentByPaymentDate() != null) {
            customerBO.getCustomerAccount().updateDetails(userContext);

            try {
                if (customerBO.getPersonnel() != null) {
                    new AccountBusinessService().checkPermissionForAdjustment(AccountTypes.CUSTOMER_ACCOUNT, customerBO
                            .getLevel(), userContext, customerBO.getOffice().getOfficeId(), customerBO.getPersonnel()
                            .getPersonnelId());
                } else {
                    new AccountBusinessService().checkPermissionForAdjustment(AccountTypes.CUSTOMER_ACCOUNT, customerBO
                            .getLevel(), userContext, customerBO.getOffice().getOfficeId(), userContext.getId());
                }

                this.transactionHelper.startTransaction();
                customerBO.adjustPmnt(adjustmentNote, loggedInUser);
                this.customerDao.save(customerBO);
                this.transactionHelper.commitTransaction();
            } catch (SystemException e) {
                this.transactionHelper.rollbackTransaction();
                throw new MifosRuntimeException(e);
            } catch (ApplicationException e) {
                this.transactionHelper.rollbackTransaction();
                throw new BusinessRuleException(e.getKey(), e);
            }
        }
    }

    @Override
    public List<AuditLogDto> retrieveChangeLogs(Integer centerId) {

        try {
            List<AuditLogDto> auditLogs = new ArrayList<AuditLogDto>();

            Short entityType = EntityType.CENTER.getValue();
            AuditBusinessService auditBusinessService = new AuditBusinessService();
            List<AuditLogView> centerAuditLogs = auditBusinessService.getAuditLogRecords(entityType, centerId);
            for (AuditLogView auditLogView : centerAuditLogs) {
                auditLogs.add(auditLogView.toDto());
            }

            return auditLogs;
        } catch (ServiceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    @Override
    public List<CustomerDetailDto> retrieveCustomersUnderUser(Short loanOfficerId) {

        PersonnelBO loanOfficer = this.personnelDao.findPersonnelById(loanOfficerId);

        List<CustomerDetailDto> customerList = new ArrayList<CustomerDetailDto>();

        if (ClientRules.getCenterHierarchyExists()) {
            customerList = this.customerDao.findActiveCentersUnderUser(loanOfficer);
        } else {
            customerList = this.customerDao.findGroupsUnderUser(loanOfficer);
        }

        return customerList;
    }

    @Override
    public String retrieveOfficeName(Short officeId) {
        return this.officeDao.findOfficeById(officeId).getOfficeName();
    }

    @Override
    public UserDetailDto retrieveUsersDetails(Short userId) {
        return this.personnelDao.findPersonnelById(userId).toDto();
    }

    @Override
    public void addNoteToPersonnel(Short personnelId, String comment) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        try {
            PersonnelBO personnel = this.personnelDao.findPersonnelById(personnelId);
            PersonnelBO loggedInUser = this.personnelDao.findPersonnelById(userContext.getId());
            if (personnel != null) {
                checkPermissionForAddingNotesToPersonnel(userContext, personnel.getOffice().getOfficeId(), personnel.getPersonnelId());
            }

            this.transactionHelper.startTransaction();
            PersonnelNotesEntity personnelNote = new PersonnelNotesEntity(comment, loggedInUser, personnel);
            personnel.addNotes(userContext.getId(), personnelNote);
            this.personnelDao.save(personnel);
            this.transactionHelper.commitTransaction();
        } catch (Exception e) {
            this.transactionHelper.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            this.transactionHelper.closeSession();
        }
    }

    private void checkPermissionForAddingNotesToPersonnel(UserContext userContext, Short recordOfficeId,
            Short recordLoanOfficerId) {
        if (!isPermissionAllowed(userContext, recordOfficeId, recordLoanOfficerId)) {
            throw new BusinessRuleException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
        }
    }

    private boolean isPermissionAllowed(UserContext userContext, Short recordOfficeId, Short recordLoanOfficerId) {
        return ActivityMapper.getInstance().isAddingNotesPermittedForPersonnel(userContext, recordOfficeId,
                recordLoanOfficerId);
    }

    private AccountFeeScheduleDto convertToDto(AccountFeesActionDetailEntity feeEntity) {
        return new AccountFeeScheduleDto(feeEntity.getFee().getFeeName(), feeEntity.getFeeAmount().toString(),
                feeEntity.getFeeAmountPaid().toString(), feeEntity.getFeeAllocated().toString());
    }

    private String getMeetingRecurrence(MeetingBO meeting, UserContext userContext) {
        return meeting != null ? new MeetingHelper().getMessageWithFrequency(meeting, userContext) : null;
    }
}