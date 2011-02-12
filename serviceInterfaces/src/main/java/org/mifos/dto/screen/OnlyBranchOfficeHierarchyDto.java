/*
 * Copyright Grameen Foundation USA
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

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import org.mifos.dto.domain.OfficeDetailsDto;
import org.mifos.dto.domain.OfficeHierarchyDto;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_NO_SERIALVERSIONID", justification="should disable at filter level and also for pmd - not important for us")
public class OnlyBranchOfficeHierarchyDto implements Serializable {

    public static final String IDENTIFIER = "branchOnlyOffices";

    private final List<OfficeDetailsDto> levels;
    private final String loggedInOfficeSearchId;
    private final Locale preferredLocaleOfUser;
    private final List<OfficeHierarchyDto> branchOnlyOfficeHierarchy;

    public OnlyBranchOfficeHierarchyDto(Locale preferredLocaleOfUser, List<OfficeDetailsDto> levels,
            String loggedInOfficeSearchId, List<OfficeHierarchyDto> branchOnlyOfficeHierarchy) {
        this.preferredLocaleOfUser = preferredLocaleOfUser;
        this.levels = levels;
        this.loggedInOfficeSearchId = loggedInOfficeSearchId;
        this.branchOnlyOfficeHierarchy = branchOnlyOfficeHierarchy;
    }

    public List<OfficeDetailsDto> getLevels() {
        return this.levels;
    }

    public Locale getPreferredLocaleOfUser() {
        return this.preferredLocaleOfUser;
    }

    public String getLoggedInOfficeSearchId() {
        return this.loggedInOfficeSearchId;
    }

    public List<OfficeHierarchyDto> getBranchOnlyOfficeHierarchy() {
        return this.branchOnlyOfficeHierarchy;
    }
}
