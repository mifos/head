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

import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.OfficeDetailsDto;
import org.mifos.dto.domain.OfficeDto;

public class OfficeFormDto {

    private final List<CustomFieldDto> customFields;
    private final List<OfficeDto> parents;
    private final List<OfficeDetailsDto> officeLevels;

    public OfficeFormDto(List<CustomFieldDto> customFields, List<OfficeDto> parents, List<OfficeDetailsDto> officeLevels) {
        this.customFields = customFields;
        this.parents = parents;
        this.officeLevels = officeLevels;
    }

    public List<CustomFieldDto> getCustomFields() {
        return this.customFields;
    }

    public List<OfficeDto> getParents() {
        return this.parents;
    }

    public List<OfficeDetailsDto> getOfficeLevels() {
        return this.officeLevels;
    }
}