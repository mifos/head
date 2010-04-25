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

import java.util.List;

import org.mifos.application.master.business.CustomFieldDto;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.personnel.business.PersonnelDto;
import org.mifos.customers.util.helpers.SavingsDetailDto;

public class ClientFormCreationDto {

    private final List<CustomFieldDto> customFieldDtos;
    private final Short officeId;
    private final Short formedByPersonnelId;
    private final List<PersonnelDto> personnelList;
    private final CustomerApplicableFeesDto applicableFees;
    private final ClientRulesDto clientRules;
    private final List<SavingsDetailDto> savingsOfferings;
    private final List<PersonnelDto> formedByPersonnelList;
    private final MeetingBO parentCustomerMeeting;
    private final String centerDisplayName;
    private final ClientDropdownsDto clientDropdowns;
    private final String formedByPersonnelName;
    private final String groupDisplayName;
    private final String officeName;

    public ClientFormCreationDto(ClientDropdownsDto clientDropdowns, List<CustomFieldDto> customFieldDtos, ClientRulesDto clientRules, Short officeId, String officeName,
            Short formedByPersonnelId, String formedByPersonnelName, List<PersonnelDto> personnelList, CustomerApplicableFeesDto applicableFees,
            List<PersonnelDto> formedByPersonnelList, List<SavingsDetailDto> savingsOfferings,
            MeetingBO parentCustomerMeeting, String centerDisplayName, String groupDisplayName) {

        this.clientDropdowns = clientDropdowns;
        this.customFieldDtos = customFieldDtos;
        this.clientRules = clientRules;
        this.officeId = officeId;
        this.officeName = officeName;
        this.formedByPersonnelId = formedByPersonnelId;
        this.formedByPersonnelName = formedByPersonnelName;
        this.personnelList = personnelList;
        this.applicableFees = applicableFees;
        this.formedByPersonnelList = formedByPersonnelList;
        this.savingsOfferings = savingsOfferings;
        this.parentCustomerMeeting = parentCustomerMeeting;
        this.centerDisplayName = centerDisplayName;
        this.groupDisplayName = groupDisplayName;
    }

    public List<CustomFieldDto> getCustomFieldViews() {
        return this.customFieldDtos;
    }

    public Short getOfficeId() {
        return this.officeId;
    }

    public Short getFormedByPersonnelId() {
        return this.formedByPersonnelId;
    }

    public List<PersonnelDto> getPersonnelList() {
        return this.personnelList;
    }

    public CustomerApplicableFeesDto getApplicableFees() {
        return this.applicableFees;
    }

    public ClientRulesDto getClientRules() {
        return this.clientRules;
    }

    public List<SavingsDetailDto> getSavingsOfferings() {
        return this.savingsOfferings;
    }

    public List<PersonnelDto> getFormedByPersonnelList() {
        return this.formedByPersonnelList;
    }

    public MeetingBO getParentCustomerMeeting() {
        return this.parentCustomerMeeting;
    }

    public String getCenterDisplayName() {
        return this.centerDisplayName;
    }

    public ClientDropdownsDto getClientDropdowns() {
        return this.clientDropdowns;
    }

    public String getFormedByPersonnelName() {
        return this.formedByPersonnelName;
    }

    public String getGroupDisplayName() {
        return this.groupDisplayName;
    }

    public String getOfficeName() {
        return this.officeName;
    }
}