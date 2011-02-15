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

package org.mifos.dto.domain;

import java.util.List;

import org.joda.time.LocalDate;

public class CenterCreationDetail {

    private final LocalDate mfiJoiningDate;
    private final String displayName;
    private final String externalId;
    private final AddressDto addressDto;
    private final Short loanOfficerId;
    private final Short officeId;
    private final List<CreateAccountFeeDto> feesToApply;

    public CenterCreationDetail(LocalDate mfiJoiningDate, String displayName, String externalId, AddressDto addressDto,
            Short loanOfficerId, Short officeId, List<CreateAccountFeeDto> feesToApply) {
        this.mfiJoiningDate = mfiJoiningDate;
        this.displayName = displayName;
        this.externalId = externalId;
        this.addressDto = addressDto;
        this.loanOfficerId = loanOfficerId;
        this.officeId = officeId;
        this.feesToApply = feesToApply;
    }

    public LocalDate getMfiJoiningDate() {
        return this.mfiJoiningDate;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getExternalId() {
        return this.externalId;
    }

    public AddressDto getAddressDto() {
        return this.addressDto;
    }

    public Short getLoanOfficerId() {
        return this.loanOfficerId;
    }

    public Short getOfficeId() {
        return this.officeId;
    }

    public List<CreateAccountFeeDto> getFeesToApply() {
        return this.feesToApply;
    }
}