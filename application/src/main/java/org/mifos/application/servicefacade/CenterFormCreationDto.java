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
import org.mifos.customers.personnel.business.PersonnelDto;

public class CenterFormCreationDto {

    private final List<PersonnelDto> activeLoanOfficersForBranch;
    private final List<CustomFieldDto> customFieldDtos;
    private final List<FeeDto> additionalFees;
    private final List<FeeDto> defaultFees;

    public CenterFormCreationDto(List<PersonnelDto> activeLoanOfficersForBranch,
            List<CustomFieldDto> customFieldDtos, List<FeeDto> additionalFees, List<FeeDto> defaultFees) {
        this.activeLoanOfficersForBranch = activeLoanOfficersForBranch;
        this.customFieldDtos = customFieldDtos;
        this.additionalFees = additionalFees;
        this.defaultFees = defaultFees;
    }

    public List<PersonnelDto> getActiveLoanOfficersForBranch() {
        return this.activeLoanOfficersForBranch;
    }

    public List<CustomFieldDto> getCustomFieldViews() {
        return this.customFieldDtos;
    }

    public List<FeeDto> getAdditionalFees() {
        return this.additionalFees;
    }

    public List<FeeDto> getDefaultFees() {
        return this.defaultFees;
    }
}