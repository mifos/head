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

import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.personnel.business.PersonnelView;
import org.mifos.customers.util.helpers.SavingsDetailDto;

public class ClientFormCreationDto {

    private final List<CustomFieldView> customFieldViews;
    private final Short officeId;
    private final Short formedByPersonnelId;
    private final List<PersonnelView> personnelList;
    private final CustomerApplicableFeesDto applicableFees;
    private final ClientRulesDto clientRules;
    private final List<SavingsDetailDto> savingsOfferings;
    private final List<PersonnelView> formedByPersonnelList;
    private final MeetingBO parentCustomerMeeting;
    private final String centerDisplayName;
    private final ClientDropdownsDto clientDropdowns;

    public ClientFormCreationDto(ClientDropdownsDto clientDropdowns, List<CustomFieldView> customFieldViews, ClientRulesDto clientRules, Short officeId,
            Short formedByPersonnelId, List<PersonnelView> personnelList, CustomerApplicableFeesDto applicableFees,
            List<PersonnelView> formedByPersonnelList, List<SavingsDetailDto> savingsOfferings,
            MeetingBO parentCustomerMeeting, String centerDisplayName) {

        this.clientDropdowns = clientDropdowns;
        this.customFieldViews = customFieldViews;
        this.clientRules = clientRules;
        this.officeId = officeId;
        this.formedByPersonnelId = formedByPersonnelId;
        this.personnelList = personnelList;
        this.applicableFees = applicableFees;
        this.formedByPersonnelList = formedByPersonnelList;
        this.savingsOfferings = savingsOfferings;
        this.parentCustomerMeeting = parentCustomerMeeting;
        this.centerDisplayName = centerDisplayName;
    }

    public List<CustomFieldView> getCustomFieldViews() {
        return this.customFieldViews;
    }

    public Short getOfficeId() {
        return this.officeId;
    }

    public Short getFormedByPersonnelId() {
        return this.formedByPersonnelId;
    }

    public List<PersonnelView> getPersonnelList() {
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

    public List<PersonnelView> getFormedByPersonnelList() {
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
}