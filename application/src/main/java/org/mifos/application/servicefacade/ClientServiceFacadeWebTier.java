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
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.config.ClientRules;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.service.CustomerService;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.dto.domain.ApplicableAccountFeeDto;
import org.mifos.dto.domain.CenterCreation;
import org.mifos.dto.domain.ClientRulesDto;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.MeetingDto;
import org.mifos.dto.domain.PersonnelDto;
import org.mifos.dto.domain.SavingsDetailDto;
import org.mifos.dto.domain.ValueListElement;
import org.mifos.dto.screen.ClientDropdownsDto;
import org.mifos.dto.screen.ClientFormCreationDto;
import org.mifos.security.MifosUser;
import org.mifos.security.util.UserContext;
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
}