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
import java.util.HashSet;
import java.util.List;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.accounts.fees.persistence.FeePersistence;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RankOfDay;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.config.ClientRules;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerCustomFieldEntity;
import org.mifos.customers.business.CustomerPositionEntity;
import org.mifos.customers.business.PositionEntity;
import org.mifos.customers.business.service.CustomerService;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.dto.domain.AddressDto;
import org.mifos.dto.domain.ApplicableAccountFeeDto;
import org.mifos.dto.domain.CenterCreation;
import org.mifos.dto.domain.CenterCreationDetail;
import org.mifos.dto.domain.CenterDto;
import org.mifos.dto.domain.CreateAccountFeeDto;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.CustomerDetailsDto;
import org.mifos.dto.domain.CustomerDto;
import org.mifos.dto.domain.CustomerPositionDto;
import org.mifos.dto.domain.MeetingDetailsDto;
import org.mifos.dto.domain.MeetingDto;
import org.mifos.dto.domain.PersonnelDto;
import org.mifos.dto.screen.CenterFormCreationDto;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.LocalizationConverter;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.security.MifosUser;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;
import org.springframework.security.core.context.SecurityContextHolder;

public class CenterServiceFacadeWebTier implements CenterServiceFacade {

    private final OfficeDao officeDao;
    private final PersonnelDao personnelDao;
    private final CustomerDao customerDao;
    private final CustomerService customerService;

    public CenterServiceFacadeWebTier(CustomerService customerService, OfficeDao officeDao,
            PersonnelDao personnelDao, CustomerDao customerDao) {
        this.customerService = customerService;
        this.officeDao = officeDao;
        this.personnelDao = personnelDao;
        this.customerDao = customerDao;
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

    @Override
    public CenterFormCreationDto retrieveCenterFormCreationData(CenterCreation centerCreation) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        List<PersonnelDto> activeLoanOfficersForBranch = personnelDao.findActiveLoanOfficersForOffice(centerCreation);

        List<CustomFieldDefinitionEntity> customFieldDefinitions = customerDao.retrieveCustomFieldEntitiesForCenter();
        List<CustomFieldDto> customFieldDtos = CustomFieldDefinitionEntity.toDto(customFieldDefinitions, userContext.getPreferredLocale());

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

        return new CenterFormCreationDto(activeLoanOfficersForBranch, customFieldDtos, applicableDefaultAdditionalFees, applicableDefaultAccountFees);
    }

    @Override
    public CustomerDetailsDto createNewCenter(CenterCreationDetail createCenterDetail, MeetingDto meetingDto) {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        String centerName = createCenterDetail.getDisplayName();
        String externalId = createCenterDetail.getExternalId();
        AddressDto addressDto = createCenterDetail.getAddressDto();
        Address centerAddress = new Address(addressDto.getLine1(), addressDto.getLine2(), addressDto.getLine3(),
                addressDto.getCity(), addressDto.getState(), addressDto.getCountry(), addressDto.getZip(), addressDto.getPhoneNumber());
        PersonnelBO loanOfficer = this.personnelDao.findPersonnelById(createCenterDetail.getLoanOfficerId());
        OfficeBO centerOffice = this.officeDao.findOfficeById(createCenterDetail.getOfficeId());

        int numberOfCustomersInOfficeAlready = customerDao.retrieveLastSearchIdValueForNonParentCustomersInOffice(createCenterDetail.getOfficeId());

        List<AccountFeesEntity> feesForCustomerAccount = createAccountFeeEntities(createCenterDetail.getFeesToApply());

        DateTime mfiJoiningDate = null;
        if (createCenterDetail.getMfiJoiningDate() != null) {
            mfiJoiningDate = createCenterDetail.getMfiJoiningDate().toDateMidnight().toDateTime();
        }

        try {
            MeetingDetailsDto meetingDetailsDto = meetingDto.getMeetingDetailsDto();
            MeetingBO meeting = new MeetingBO(RecurrenceType.fromInt(meetingDetailsDto.getRecurrenceTypeId().shortValue()),
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
                meeting = new MeetingBO(weekDay, rank, meetingDetailsDto.getEvery().shortValue(), meetingDto.getMeetingStartDate().toDateMidnight().toDate(), MeetingType.CUSTOMER_MEETING, meetingDto.getMeetingPlace());
            }
            meeting.setUserContext(userContext);

            CenterBO center = CenterBO.createNew(userContext, centerName, mfiJoiningDate, meeting, loanOfficer,
                    centerOffice, numberOfCustomersInOfficeAlready, centerAddress, externalId, new DateMidnight().toDateTime());

            this.customerService.createCenter(center, meeting, feesForCustomerAccount);

            return new CustomerDetailsDto(center.getCustomerId(), center.getGlobalCustNum());
        } catch (MeetingException e) {
            throw new BusinessRuleException(e.getKey(), e);
        }
    }

    private List<AccountFeesEntity> createAccountFeeEntities(List<CreateAccountFeeDto> feesToApply) {
        List<AccountFeesEntity> feesForCustomerAccount = new ArrayList<AccountFeesEntity>();
        for (CreateAccountFeeDto feeDto : feesToApply) {
            FeeBO fee = new FeePersistence().getFee(feeDto.getFeeId().shortValue());
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

        AddressDto address = Address.toDto(center.getAddress());
        return new CenterDto(loanOfficerId, center.getCustomerId(), center.getGlobalCustNum(), mfiJoiningDate,
                mfiJoiningDateAsString, center.getExternalId(), address, customerPositionDtos,
                customFieldDtos, customerList, activeLoanOfficersForBranch, true);
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
}