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

import org.mifos.dto.domain.ApplicableAccountFeeDto;
import org.mifos.dto.domain.PersonnelDto;

public class CenterFormCreationDto {

    private final List<PersonnelDto> activeLoanOfficersForBranch;
    private final List<ApplicableAccountFeeDto> additionalFees;
    private final List<ApplicableAccountFeeDto> defaultFees;

    public CenterFormCreationDto(List<PersonnelDto> activeLoanOfficersForBranch,
                                 List<ApplicableAccountFeeDto> additionalFees, List<ApplicableAccountFeeDto> defaultFees) {
        this.activeLoanOfficersForBranch = activeLoanOfficersForBranch;
        this.additionalFees = additionalFees;
        this.defaultFees = defaultFees;
    }

    public List<PersonnelDto> getActiveLoanOfficersForBranch() {
        return this.activeLoanOfficersForBranch;
    }

    public List<ApplicableAccountFeeDto> getAdditionalFees() {
        return this.additionalFees;
    }

    public List<ApplicableAccountFeeDto> getDefaultFees() {
        return this.defaultFees;
    }
}