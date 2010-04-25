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

import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.application.master.business.CustomFieldDto;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.personnel.business.PersonnelDto;

public class GroupFormCreationDto {

    private final CustomerBO parentCustomer;
    private final boolean centerHierarchyExists;
    private final List<CustomFieldDto> customFieldDtos;
    private final List<PersonnelDto> personnelList;
    private final List<PersonnelDto> formedByPersonnel;
    private final CustomerApplicableFeesDto applicableFees;

    public GroupFormCreationDto(boolean centerHierarchyExists, CustomerBO parentCustomer,
            List<CustomFieldDto> customFieldDtos, List<PersonnelDto> personnelList,
            List<PersonnelDto> formedByPersonnel, CustomerApplicableFeesDto applicableFees) {
        this.centerHierarchyExists = centerHierarchyExists;
        this.parentCustomer = parentCustomer;
        this.customFieldDtos = customFieldDtos;
        this.personnelList = personnelList;
        this.formedByPersonnel = formedByPersonnel;
        this.applicableFees = applicableFees;
    }

    public List<PersonnelDto> getFormedByPersonnel() {
        return this.formedByPersonnel;
    }

    public List<PersonnelDto> getPersonnelList() {
        return this.personnelList;
    }

    public List<CustomFieldDto> getCustomFieldViews() {
        return this.customFieldDtos;
    }

    public boolean isCenterHierarchyExists() {
        return this.centerHierarchyExists;
    }

    public CustomerBO getParentCustomer() {
        return this.parentCustomer;
    }

    public List<FeeDto> getDefaultFees() {
        return this.applicableFees.getDefaultFees();
    }

    public List<FeeDto> getAdditionalFees() {
        return this.applicableFees.getAdditionalFees();
    }

    public String getParentOfficeId() {
        String parentOfficeId = "";
        if (this.getParentCustomer() != null) {
            parentOfficeId = this.parentCustomer.getOffice().getOfficeId().toString();
        }
        return parentOfficeId;
    }

    public String getParentPersonnelId() {
        String parentPersonnelId = "";
        if (this.getParentCustomer() != null) {
            parentPersonnelId = this.parentCustomer.getPersonnel().getPersonnelId().toString();
        }
        return parentPersonnelId;
    }
}