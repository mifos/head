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

package org.mifos.dto.screen;

import java.util.List;

import org.mifos.dto.domain.OfficeDetailsDto;
import org.mifos.dto.domain.PersonnelDto;

public class ChangeAccountStatusDto {

    private final List<OfficeDetailsDto> activeBranches;
    private final List<PersonnelDto> loanOfficers;
    private final boolean loanPendingApprovalStateEnabled;
    private final Short accountState;
    private final boolean centerHierarchyExists;

    public ChangeAccountStatusDto(List<OfficeDetailsDto> activeBranches, List<PersonnelDto> loanOfficers, boolean loanPendingApprovalStateEnabled, Short accountState, boolean centerHierarchyExists) {
        this.activeBranches = activeBranches;
        this.loanOfficers = loanOfficers;
        this.loanPendingApprovalStateEnabled = loanPendingApprovalStateEnabled;
        this.accountState = accountState;
        this.centerHierarchyExists = centerHierarchyExists;
    }

    public List<OfficeDetailsDto> getActiveBranches() {
        return this.activeBranches;
    }

    public List<PersonnelDto> getLoanOfficers() {
        return this.loanOfficers;
    }

    public boolean isLoanPendingApprovalStateEnabled() {
        return this.loanPendingApprovalStateEnabled;
    }

    public Short getAccountState() {
        return this.accountState;
    }

    public boolean isCenterHierarchyExists() {
        return this.centerHierarchyExists;
    }
}