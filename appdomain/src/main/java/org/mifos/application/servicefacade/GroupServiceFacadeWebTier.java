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

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.accounts.fees.persistence.FeeDao;
import org.mifos.accounts.servicefacade.UserContextFactory;
import org.mifos.application.admin.servicefacade.InvalidDateException;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.persistence.LegacyMasterDao;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.MeetingFactory;
import org.mifos.config.ClientRules;
import org.mifos.core.CurrencyMismatchException;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.api.CustomerLevel;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerHistoricalDataEntity;
import org.mifos.customers.business.CustomerPositionEntity;
import org.mifos.customers.business.PositionEntity;
import org.mifos.customers.business.service.CustomerService;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.group.business.GroupPerformanceHistoryEntity;
import org.mifos.customers.group.util.helpers.GroupConstants;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.dto.domain.AddressDto;
import org.mifos.dto.domain.ApplicableAccountFeeDto;
import org.mifos.dto.domain.CenterCreation;
import org.mifos.dto.domain.CenterDto;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.CustomerAccountSummaryDto;
import org.mifos.dto.domain.CustomerAddressDto;
import org.mifos.dto.domain.CustomerDetailDto;
import org.mifos.dto.domain.CustomerDetailsDto;
import org.mifos.dto.domain.CustomerDto;
import org.mifos.dto.domain.CustomerFlagDto;
import org.mifos.dto.domain.CustomerHistoricalDataUpdateRequest;
import org.mifos.dto.domain.CustomerMeetingDto;
import org.mifos.dto.domain.CustomerNoteDto;
import org.mifos.dto.domain.CustomerPositionDto;
import org.mifos.dto.domain.CustomerPositionOtherDto;
import org.mifos.dto.domain.GroupCreation;
import org.mifos.dto.domain.GroupCreationDetail;
import org.mifos.dto.domain.GroupFormCreationDto;
import org.mifos.dto.domain.GroupUpdate;
import org.mifos.dto.domain.LoanDetailDto;
import org.mifos.dto.domain.MeetingDto;
import org.mifos.dto.domain.PersonnelDto;
import org.mifos.dto.domain.SavingsDetailDto;
import org.mifos.dto.domain.SurveyDto;
import org.mifos.dto.screen.CenterHierarchySearchDto;
import org.mifos.dto.screen.CenterSearchInput;
import org.mifos.dto.screen.CustomerHistoricalDataDto;
import org.mifos.dto.screen.GroupDisplayDto;
import org.mifos.dto.screen.GroupInformationDto;
import org.mifos.dto.screen.GroupPerformanceHistoryDto;
import org.mifos.dto.screen.LoanCycleCounter;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelperForStaticHibernateUtil;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.LocalizationConverter;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.MifosUser;
import org.mifos.security.util.ActivityMapper;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

public class GroupServiceFacadeWebTier implements GroupServiceFacade {

    private final OfficeDao officeDao;
    private final PersonnelDao personnelDao;
    private final CustomerDao customerDao;
    private final CustomerService customerService;
    private HibernateTransactionHelper transactionHelper = new HibernateTransactionHelperForStaticHibernateUtil();

    @Autowired
    private LegacyMasterDao legacyMasterDao;

    @Autowired
    private FeeDao feeDao;

    @Autowired
    public GroupServiceFacadeWebTier(CustomerService customerService, OfficeDao officeDao,
            PersonnelDao personnelDao, CustomerDao customerDao) {
        this.customerService = customerService;
        this.officeDao = officeDao;
        this.personnelDao = personnelDao;
        this.customerDao = customerDao;
    }

    @Override
    public CenterHierarchySearchDto isCenterHierarchyConfigured() {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        boolean isCenterHierarchyExists = ClientRules.getCenterHierarchyExists();
        CenterSearchInput searchInputs = new CenterSearchInput(userContext.getBranchId(), GroupConstants.CREATE_NEW_GROUP);

        return new CenterHierarchySearchDto(isCenterHierarchyExists, searchInputs);
    }

    private UserContext toUserContext(MifosUser user) {
        return new UserContextFactory().create(user);
    }

    @Override
    public GroupFormCreationDto retrieveGroupFormCreationData(GroupCreation groupCreation) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        CustomerBO parentCustomer = null;
        Short parentOfficeId = groupCreation.getOfficeId();
        CustomerApplicableFeesDto applicableFees = CustomerApplicableFeesDto.empty();
        List<PersonnelDto> personnelList = new ArrayList<PersonnelDto>();

        CenterCreation centerCreation;

        boolean isCenterHierarchyExists = ClientRules.getCenterHierarchyExists();
        if (isCenterHierarchyExists) {
            parentCustomer = this.customerDao.findCenterBySystemId(groupCreation.getParentSystemId());

            parentOfficeId = parentCustomer.getOffice().getOfficeId();

            centerCreation = new CenterCreation(parentOfficeId, userContext.getId(), userContext.getLevelId(), userContext.getPreferredLocale());

            MeetingBO customerMeeting = parentCustomer.getCustomerMeetingValue();
            List<FeeBO> fees = customerDao.retrieveFeesApplicableToGroupsRefinedBy(customerMeeting);
            applicableFees = CustomerApplicableFeesDto.toDto(fees, userContext);
        } else {
            centerCreation = new CenterCreation(groupCreation.getOfficeId(), userContext.getId(), userContext.getLevelId(), userContext.getPreferredLocale());
            personnelList = this.personnelDao.findActiveLoanOfficersForOffice(centerCreation);

            List<FeeBO> fees = customerDao.retrieveFeesApplicableToGroups();
            applicableFees = CustomerApplicableFeesDto.toDto(fees, userContext);
        }

        List<ApplicableAccountFeeDto> applicableDefaultAccountFees = new ArrayList<ApplicableAccountFeeDto>();
        for (FeeDto fee : applicableFees.getDefaultFees()) {
            applicableDefaultAccountFees.add(new ApplicableAccountFeeDto(fee.getFeeIdValue().intValue(), fee.getFeeName(), fee.getAmount(), fee.isRemoved(), fee.isWeekly(), fee.isMonthly(), fee.isPeriodic(), fee.getFeeSchedule()));
        }

        List<ApplicableAccountFeeDto> applicableDefaultAdditionalFees = new ArrayList<ApplicableAccountFeeDto>();
        for (FeeDto fee : applicableFees.getAdditionalFees()) {
            applicableDefaultAdditionalFees.add(new ApplicableAccountFeeDto(fee.getFeeIdValue().intValue(), fee.getFeeName(), fee.getAmount(), fee.isRemoved(), fee.isWeekly(), fee.isMonthly(), fee.isPeriodic(), fee.getFeeSchedule()));
        }

        List<PersonnelDto> formedByPersonnel = customerDao.findLoanOfficerThatFormedOffice(centerCreation.getOfficeId());

        return new GroupFormCreationDto(isCenterHierarchyExists, personnelList,
                formedByPersonnel, applicableDefaultAccountFees, applicableDefaultAdditionalFees);
    }

    @Override
    public CustomerDetailsDto createNewGroup(GroupCreationDetail groupCreationDetail, MeetingDto meetingDto) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        OfficeBO userOffice = this.officeDao.findOfficeById(userContext.getBranchId());
        userContext.setBranchGlobalNum(userOffice.getGlobalOfficeNum());

        GroupBO group;

        try {
            List<AccountFeesEntity> feesForCustomerAccount = convertFeeViewsToAccountFeeEntities(groupCreationDetail.getFeesToApply());

            PersonnelBO formedBy = this.personnelDao.findPersonnelById(groupCreationDetail.getLoanOfficerId());

            Short officeId;

            String groupName = groupCreationDetail.getDisplayName();
            CustomerStatus customerStatus = CustomerStatus.fromInt(groupCreationDetail.getCustomerStatus());
            String externalId = groupCreationDetail.getExternalId();
            boolean trained = groupCreationDetail.isTrained();
            DateTime trainedOn = groupCreationDetail.getTrainedOn();
            DateTime mfiJoiningDate = groupCreationDetail.getMfiJoiningDate();
            DateTime activationDate = groupCreationDetail.getActivationDate();

            AddressDto dto = groupCreationDetail.getAddressDto();
            Address address = null;
            if (dto != null) {
                address = new Address(dto.getLine1(), dto.getLine2(), dto.getLine3(), dto.getCity(), dto.getState(), dto.getCountry(), dto.getZip(), dto.getPhoneNumber());
            }

            MeetingBO groupMeeting = null;
            if (meetingDto != null) {
                groupMeeting = new MeetingFactory().create(meetingDto);
                groupMeeting.setUserContext(userContext);
            }

            if (ClientRules.getCenterHierarchyExists()) {

                CenterBO parentCustomer = this.customerDao.findCenterBySystemId(groupCreationDetail.getParentSystemId());
//                loanOfficerId = parentCustomer.getPersonnel().getPersonnelId();
                officeId = parentCustomer.getOffice().getOfficeId();

                groupMeeting = parentCustomer.getCustomerMeetingValue();

                group = GroupBO.createGroupWithCenterAsParent(userContext, groupName, formedBy, parentCustomer,
                        address, externalId, trained, trainedOn, customerStatus, mfiJoiningDate, activationDate);
            } else {

                // create group without center as parent
                Short loanOfficerId = groupCreationDetail.getLoanOfficerId() != null ? groupCreationDetail.getLoanOfficerId() : userContext.getId();
                officeId = groupCreationDetail.getOfficeId();

                OfficeBO office = this.officeDao.findOfficeById(groupCreationDetail.getOfficeId());
                PersonnelBO loanOfficer = this.personnelDao.findPersonnelById(loanOfficerId);

                int numberOfCustomersInOfficeAlready = customerDao.retrieveLastSearchIdValueForNonParentCustomersInOffice(officeId);

                group = GroupBO.createGroupAsTopOfCustomerHierarchy(userContext, groupName, formedBy, groupMeeting,
                        loanOfficer, office, address, externalId, trained, trainedOn,
                        customerStatus, numberOfCustomersInOfficeAlready, mfiJoiningDate, activationDate);
            }

            this.customerService.createGroup(group, groupMeeting, feesForCustomerAccount);

            return new CustomerDetailsDto(group.getCustomerId(), group.getGlobalCustNum());
        } catch (CustomerException e) {
            throw new BusinessRuleException(e.getKey(), e);
        }
    }

    private List<AccountFeesEntity> convertFeeViewsToAccountFeeEntities(List<ApplicableAccountFeeDto> feesToApply) {
        List<AccountFeesEntity> feesForCustomerAccount = new ArrayList<AccountFeesEntity>();
        for (ApplicableAccountFeeDto feeDto : feesToApply) {
            FeeBO fee = feeDao.findById(feeDto.getFeeId().shortValue());
            Double feeAmount = new LocalizationConverter().getDoubleValueForCurrentLocale(feeDto.getAmount());

            AccountBO nullReferenceForNow = null;
            AccountFeesEntity accountFee = new AccountFeesEntity(nullReferenceForNow, fee, feeAmount);
            feesForCustomerAccount.add(accountFee);
        }
        return feesForCustomerAccount;
    }

//    private void checkPermissionForCreate(Short newState, UserContext userContext, Short recordOfficeId,
//            Short recordLoanOfficerId) throws ApplicationException {
//        if (!isPermissionAllowed(newState, userContext, recordOfficeId, recordLoanOfficerId)) {
//            logger.info("permission not allowed: " + userContext.toString() + " officeId: " + recordLoanOfficerId + " loanOfficerId: " + recordLoanOfficerId);
//            throw new AccountException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
//        }
//    }

//    private boolean isPermissionAllowed(Short newState, UserContext userContext, Short recordOfficeId,
//            Short recordLoanOfficerId) {
//        return ActivityMapper.getInstance().isSavePermittedForCustomer(newState.shortValue(), userContext,
//                recordOfficeId, recordLoanOfficerId);
//    }

    @Override
    public GroupInformationDto getGroupInformationDto(String globalCustNum) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        GroupBO group = this.customerDao.findGroupBySystemId(globalCustNum);
        if (group == null) {
            throw new MifosRuntimeException("Group not found for globalCustNum: " + globalCustNum);
        }

        GroupDisplayDto groupDisplay = this.customerDao.getGroupDisplayDto(group.getCustomerId(), userContext);

        Integer groupId = group.getCustomerId();
        String searchId = group.getSearchId();
        Short branchId = groupDisplay.getBranchId();

        CustomerAccountSummaryDto customerAccountSummary = this.customerDao.getCustomerAccountSummaryDto(groupId);

        GroupPerformanceHistoryDto groupPerformanceHistory = assembleGroupPerformanceHistoryDto(group
                .getGroupPerformanceHistory(), searchId, branchId, groupId);

        CustomerAddressDto groupAddress = this.customerDao.getCustomerAddressDto(group);
        List<CustomerDetailDto> clients = this.customerDao.findClientsThatAreNotCancelledOrClosedReturningDetailDto(searchId, branchId);

        List<CustomerNoteDto> recentCustomerNotes = this.customerDao.getRecentCustomerNoteDto(groupId);
        List<CustomerPositionOtherDto> customerPositions = this.customerDao.getCustomerPositionDto(groupId, userContext);
        List<CustomerFlagDto> customerFlags = this.customerDao.getCustomerFlagDto(group.getCustomerFlags());
        List<LoanDetailDto> loanDetail = this.customerDao.getLoanDetailDto(group.getOpenLoanAccounts());
        List<SavingsDetailDto> savingsDetail = this.customerDao.getSavingsDetailDto(groupId, userContext);

        CustomerMeetingDto customerMeeting = this.customerDao.getCustomerMeetingDto(group.getCustomerMeeting(), userContext);

        boolean activeSurveys = false;
//        boolean activeSurveys = new SurveysPersistence().isActiveSurveysForSurveyType(SurveyType.GROUP);

        List<SurveyDto> customerSurveys = new ArrayList<SurveyDto>();
        List<CustomFieldDto> customFields = new ArrayList<CustomFieldDto>();

        return new GroupInformationDto(groupDisplay, customerAccountSummary, groupPerformanceHistory, groupAddress,
                clients, recentCustomerNotes, customerPositions, customerFlags, loanDetail, savingsDetail,
                customerMeeting, activeSurveys, customerSurveys, customFields);
    }

    private GroupPerformanceHistoryDto assembleGroupPerformanceHistoryDto(
            GroupPerformanceHistoryEntity groupPerformanceHistory, String searchId, Short branchId, Integer groupId) {

        Integer activeClientCount = this.customerDao.getActiveAndOnHoldClientCountForGroup(searchId, branchId);

        Money lastGroupLoanAmountMoney = groupPerformanceHistory.getLastGroupLoanAmount();
        String lastGroupLoanAmount = "";
        if (lastGroupLoanAmountMoney != null) {
            lastGroupLoanAmount = lastGroupLoanAmountMoney.toString();
        }

        String avgLoanAmountForMember = this.customerDao.getAvgLoanAmountForMemberInGoodOrBadStanding(searchId, branchId);
        String totalLoanAmountForGroup = this.customerDao.getTotalLoanAmountForGroup(searchId, branchId);

        String portfolioAtRisk;
        String totalSavingsAmount;

        try {
            if (groupPerformanceHistory.getPortfolioAtRisk() == null) {
                portfolioAtRisk = "0";
            } else {
                portfolioAtRisk = groupPerformanceHistory.getPortfolioAtRisk().toString();
            }
        } catch (CurrencyMismatchException e) {
            portfolioAtRisk = localizedMessageLookup("errors.multipleCurrencies");
        }

        totalSavingsAmount = this.customerDao.getTotalSavingsAmountForGroupandClientsOfGroup(searchId, branchId);

        List<LoanCycleCounter> loanCycleCounters = this.customerDao.fetchLoanCycleCounter(groupId, CustomerLevel.GROUP.getValue());

        return new GroupPerformanceHistoryDto(activeClientCount.toString(), lastGroupLoanAmount,
                avgLoanAmountForMember, totalLoanAmountForGroup, portfolioAtRisk, totalSavingsAmount,
                loanCycleCounters);
    }

    private String localizedMessageLookup(String key) {
        return ApplicationContextProvider.getBean(MessageLookup.class).lookup(key);
    }

    @Override
    public CenterDto retrieveGroupDetailsForUpdate(String globalCustNum) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

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

        List<CustomerPositionDto> customerPositionDtos = generateCustomerPositionViews(group, userContext.getLocaleId());

        DateTime mfiJoiningDate = new DateTime();
        String mfiJoiningDateAsString = "";
        if (group.getMfiJoiningDate() != null) {
            mfiJoiningDate = new DateTime(group.getMfiJoiningDate());
            mfiJoiningDateAsString = DateUtils.getUserLocaleDate(userContext.getPreferredLocale(), group
                    .getMfiJoiningDate().toString());
        }

        AddressDto address = null;
        if (group.getAddress() != null) {
            address = Address.toDto(group.getAddress());
        }
        return new CenterDto(loanOfficerId, group.getCustomerId(), group.getGlobalCustNum(), mfiJoiningDate,
                mfiJoiningDateAsString, group.getExternalId(), address, customerPositionDtos,
                customerList, activeLoanOfficersForBranch, isCenterHierarchyExists);
    }

    private List<CustomerPositionDto> generateCustomerPositionViews(CustomerBO customer, Short localeId) {

        List<PositionEntity> customerPositions = new ArrayList<PositionEntity>();

        List<PositionEntity> allCustomerPositions = legacyMasterDao.findMasterDataEntitiesWithLocale(
                PositionEntity.class);
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

    private Short extractLoanOfficerId(CustomerBO customer) {
        Short loanOfficerId = null;
        PersonnelBO loanOfficer = customer.getPersonnel();
        if (loanOfficer != null) {
            loanOfficerId = loanOfficer.getPersonnelId();
        }
        return loanOfficerId;
    }

    @Override
    public void updateGroup(GroupUpdate groupUpdate) {
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);
        try {
            customerService.updateGroup(userContext, groupUpdate);
        } catch (ApplicationException e) {
            throw new BusinessRuleException(e.getKey(), e);
        }
    }

    @Override
    public CustomerDetailDto transferGroupToCenter(String groupSystemId, String centerSystemId, Integer previousGroupVersionNo) {

        try {
            MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserContext userContext = toUserContext(user);

            CenterBO transferToCenter = this.customerDao.findCenterBySystemId(centerSystemId);
            transferToCenter.updateDetails(userContext);

            GroupBO group = this.customerDao.findGroupBySystemId(groupSystemId);
            group.updateDetails(userContext);

            checkVersionMismatch(previousGroupVersionNo, group.getVersionNo());

            String groupGlobalCustNum = this.customerService.transferGroupTo(group, transferToCenter);
            GroupBO transferedGroup = this.customerDao.findGroupBySystemId(groupGlobalCustNum);
            return transferedGroup.toCustomerDetailDto();
        } catch (ApplicationException e) {
            throw new BusinessRuleException(e.getKey(), e);
        }
    }

    private void checkVersionMismatch(Integer oldVersionNum, Integer newVersionNum) throws ApplicationException {
        if (!oldVersionNum.equals(newVersionNum)) {
            throw new ApplicationException(Constants.ERROR_VERSION_MISMATCH);
        }
    }


    @Override
    public CustomerDetailDto transferGroupToBranch(String globalCustNum, Short officeId, Integer previousGroupVersionNo) {

        try {
            MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            UserContext userContext = toUserContext(user);

            GroupBO group = this.customerDao.findGroupBySystemId(globalCustNum);
            group.updateDetails(userContext);

            checkVersionMismatch(previousGroupVersionNo, group.getVersionNo());

            OfficeBO transferToOffice = this.officeDao.findOfficeById(officeId);
            transferToOffice.setUserContext(userContext);

            String groupGlobalCustNum = this.customerService.transferGroupTo(group, transferToOffice);
            GroupBO transferedGroup = this.customerDao.findGroupBySystemId(groupGlobalCustNum);
            return transferedGroup.toCustomerDetailDto();
        } catch (ApplicationException e) {
            throw new BusinessRuleException(e.getKey(), e);
        }
    }

    @Override
    public CustomerHistoricalDataDto retrieveCustomerHistoricalData(String globalCustNum) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        CustomerBO customer = this.customerDao.findCustomerBySystemId(globalCustNum);
        boolean client = false;
        boolean group = false;
        if (customer.isClient()) {
            client = true;
        } else if (customer.isGroup()) {
            group = true;
        }

        CustomerHistoricalDataEntity customerHistoricalDataEntity = customer.getHistoricalData();
        if (customerHistoricalDataEntity == null) {
            customerHistoricalDataEntity = new CustomerHistoricalDataEntity(customer);
        }

        try {
            String currentDate = DateUtils.getCurrentDate(userContext.getPreferredLocale());

            java.sql.Date mfiJoiningDate = null;
            if (customerHistoricalDataEntity.getMfiJoiningDate() == null) {
                mfiJoiningDate = DateUtils.getLocaleDate(userContext.getPreferredLocale(), currentDate);
            } else {
                mfiJoiningDate = new Date(customerHistoricalDataEntity.getMfiJoiningDate().getTime());
            }

            return new CustomerHistoricalDataDto(client, group, mfiJoiningDate);
        } catch (InvalidDateException e) {
            throw new BusinessRuleException(e.getMessage(), e);
        }
    }

    @Override
    public void updateCustomerHistoricalData(String globalCustNum, CustomerHistoricalDataUpdateRequest historicalData) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        CustomerBO customerBO = this.customerDao.findCustomerBySystemId(globalCustNum);
        customerBO.updateDetails(userContext);

        try {
            CustomerHistoricalDataEntity customerHistoricalDataEntity = customerBO.getHistoricalData();
            if (customerBO.getPersonnel() != null) {

                checkPermissionForAddingHistoricalData(customerBO.getLevel(), userContext, customerBO.getOffice()
                        .getOfficeId(), customerBO.getPersonnel().getPersonnelId());
            } else {
                checkPermissionForAddingHistoricalData(customerBO.getLevel(), userContext, customerBO.getOffice()
                        .getOfficeId(), userContext.getId());
            }
            // Integer oldLoanCycleNo = 0;
            if (customerHistoricalDataEntity == null) {
                customerHistoricalDataEntity = new CustomerHistoricalDataEntity(customerBO);
                customerHistoricalDataEntity.setCreatedBy(customerBO.getUserContext().getId());
                customerHistoricalDataEntity.setCreatedDate(new DateTimeService().getCurrentJavaDateTime());
            } else {
                // oldLoanCycleNo =
                // customerHistoricalDataEntity.getLoanCycleNumber();
                customerHistoricalDataEntity.setUpdatedDate(new DateTimeService().getCurrentJavaDateTime());
                customerHistoricalDataEntity.setUpdatedBy(customerBO.getUserContext().getId());
            }

            customerHistoricalDataEntity.setInterestPaid(StringUtils.isBlank(historicalData.getInterestPaid()) ? null
                    : new Money(Money.getDefaultCurrency(), historicalData.getInterestPaid()));
            customerHistoricalDataEntity.setLoanAmount(StringUtils.isBlank(historicalData.getLoanAmount()) ? null
                    : new Money(Money.getDefaultCurrency(), historicalData.getLoanAmount()));
            customerHistoricalDataEntity.setLoanCycleNumber(historicalData.getLoanCycleNumber());
            customerHistoricalDataEntity.setMissedPaymentsCount(historicalData.getMissedPaymentsCount());
            customerHistoricalDataEntity.setNotes(historicalData.getNotes());
            customerHistoricalDataEntity.setProductName(historicalData.getProductName());
            customerHistoricalDataEntity
                    .setTotalAmountPaid(StringUtils.isBlank(historicalData.getTotalAmountPaid()) ? null : new Money(
                            Money.getDefaultCurrency(), historicalData.getTotalAmountPaid()));
            customerHistoricalDataEntity.setTotalPaymentsCount(historicalData.getTotalPaymentsCount());
            customerHistoricalDataEntity.setMfiJoiningDate(historicalData.getMfiJoiningDate());

            this.transactionHelper.startTransaction();
            this.transactionHelper.beginAuditLoggingFor(customerBO);

            customerBO.updateHistoricalData(customerHistoricalDataEntity);
            this.customerDao.save(customerBO);
            this.transactionHelper.commitTransaction();
        } catch (ApplicationException e) {
            this.transactionHelper.rollbackTransaction();
            throw new BusinessRuleException(e.getKey(), e);
        }
    }

    private void checkPermissionForAddingHistoricalData(CustomerLevel customerLevel, UserContext userContext,
            Short recordOfficeId, Short recordLoanOfficerId) throws ApplicationException {
        if (!isPermissionAllowed(customerLevel, userContext, recordOfficeId, recordLoanOfficerId)) {
            throw new CustomerException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
        }
    }

    private boolean isPermissionAllowed(CustomerLevel customerLevel, UserContext userContext, Short recordOfficeId,
            Short recordLoanOfficerId) {
        return ActivityMapper.getInstance().isAddingHistoricaldataPermittedForCustomers(customerLevel, userContext,
                recordOfficeId, recordLoanOfficerId);
    }

    public void setLegacyMasterDao(LegacyMasterDao legacyMasterDao) {
        this.legacyMasterDao = legacyMasterDao;
    }

    public void setTransactionHelper(HibernateTransactionHelper transactionHelper) {
        this.transactionHelper = transactionHelper;
    }
}