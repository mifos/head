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

public class OfficeDetailsForEdit {

    private final List<OfficeDetailsDto> parents;
    private final List<OfficeDetailsDto> statusList;
    private final List<OfficeDetailsDto> configuredOfficeLevels;

    public OfficeDetailsForEdit(List<OfficeDetailsDto> parents, List<OfficeDetailsDto> statusList, List<OfficeDetailsDto> configuredOfficeLevels) {
        this.parents = parents;
        this.statusList = statusList;
        this.configuredOfficeLevels = configuredOfficeLevels;
    }

    public List<OfficeDetailsDto> getParents() {
        return this.parents;
    }

    public List<OfficeDetailsDto> getStatusList() {
        return this.statusList;
    }

    public List<OfficeDetailsDto> getConfiguredOfficeLevels() {
        return this.configuredOfficeLevels;
    }
}