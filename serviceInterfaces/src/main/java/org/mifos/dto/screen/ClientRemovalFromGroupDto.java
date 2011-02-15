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

package org.mifos.dto.screen;

import java.util.List;

import org.mifos.dto.domain.OfficeDetailsDto;
import org.mifos.dto.domain.PersonnelDto;

public class ClientRemovalFromGroupDto {

    private final String globalCustNum;
    private final boolean active;
    private final boolean centerHierarchyExists;
    private final Short loanOfficerId;
    private final List<PersonnelDto> loanOfficers;
    private final List<OfficeDetailsDto> activeBranches;

    public ClientRemovalFromGroupDto(String globalCustNum, boolean isActive, boolean isCenterHierarchyExists,
            Short loanOfficerId, List<PersonnelDto> loanOfficers, List<OfficeDetailsDto> activeBranches) {
        this.globalCustNum = globalCustNum;
        this.active = isActive;
        this.centerHierarchyExists = isCenterHierarchyExists;
        this.loanOfficerId = loanOfficerId;
        this.loanOfficers = loanOfficers;
        this.activeBranches = activeBranches;
    }

    public String getGlobalCustNum() {
        return this.globalCustNum;
    }

    public boolean isActive() {
        return this.active;
    }

    public boolean isCenterHierarchyExists() {
        return this.centerHierarchyExists;
    }

    public Short getLoanOfficerId() {
        return this.loanOfficerId;
    }

    public List<PersonnelDto> getLoanOfficers() {
        return this.loanOfficers;
    }

    public List<OfficeDetailsDto> getActiveBranches() {
        return this.activeBranches;
    }
}