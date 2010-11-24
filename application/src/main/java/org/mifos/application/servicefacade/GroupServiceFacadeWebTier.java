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

import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.config.ClientRules;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.service.CustomerService;
import org.mifos.customers.group.util.helpers.GroupConstants;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.dto.domain.ApplicableAccountFeeDto;
import org.mifos.dto.domain.CenterCreation;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.GroupCreation;
import org.mifos.dto.domain.GroupFormCreationDto;
import org.mifos.dto.domain.PersonnelDto;
import org.mifos.dto.screen.CenterHierarchySearchDto;
import org.mifos.dto.screen.CenterSearchInput;
import org.mifos.security.MifosUser;
import org.mifos.security.util.UserContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class GroupServiceFacadeWebTier implements GroupServiceFacade {

    private final OfficeDao officeDao;
    private final PersonnelDao personnelDao;
    private final CustomerDao customerDao;
    private final CustomerService customerService;

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
        UserContext userContext = new UserContext();
        userContext.setBranchId(user.getBranchId());
        userContext.setId(Short.valueOf((short) user.getUserId()));
        userContext.setName(user.getUsername());
        userContext.setLevelId(user.getLevelId());
        userContext.setRoles(new HashSet<Short>(user.getRoleIds()));
        return userContext;
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

        List<CustomFieldDefinitionEntity> customFieldDefinitions = customerDao.retrieveCustomFieldEntitiesForGroup();
        List<CustomFieldDto> customFieldDtos = CustomFieldDefinitionEntity.toDto(customFieldDefinitions, userContext.getPreferredLocale());
        List<PersonnelDto> formedByPersonnel = customerDao.findLoanOfficerThatFormedOffice(centerCreation.getOfficeId());

        return new GroupFormCreationDto(isCenterHierarchyExists, customFieldDtos,
                personnelList, formedByPersonnel, applicableDefaultAccountFees, applicableDefaultAdditionalFees);
    }
}