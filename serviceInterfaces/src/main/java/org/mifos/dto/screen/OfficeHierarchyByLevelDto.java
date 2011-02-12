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

import java.util.List;

import org.mifos.dto.domain.OfficeDto;

public class OfficeHierarchyByLevelDto {

    private final List<OfficeDto> headOffices;
    private final List<OfficeDto> regionalOffices;
    private final List<OfficeDto> divisionalOffices;
    private final List<OfficeDto> areaOffices;
    private final List<OfficeDto> branchOffices;

    public OfficeHierarchyByLevelDto(List<OfficeDto> headOffices, List<OfficeDto> regionalOffices, List<OfficeDto> divisionalOffices, List<OfficeDto> areaOffices, List<OfficeDto> branchOffices) {
        this.headOffices = headOffices;
        this.regionalOffices = regionalOffices;
        this.divisionalOffices = divisionalOffices;
        this.areaOffices = areaOffices;
        this.branchOffices = branchOffices;
    }

    public List<OfficeDto> getHeadOffices() {
        return this.headOffices;
    }

    public List<OfficeDto> getRegionalOffices() {
        return this.regionalOffices;
    }

    public List<OfficeDto> getDivisionalOffices() {
        return this.divisionalOffices;
    }

    public List<OfficeDto> getAreaOffices() {
        return this.areaOffices;
    }

    public List<OfficeDto> getBranchOffices() {
        return this.branchOffices;
    }
}