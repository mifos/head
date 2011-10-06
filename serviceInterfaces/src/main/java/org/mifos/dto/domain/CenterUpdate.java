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

public class CenterUpdate {

    private final Integer customerId;
    private final String displayName;
    private final Integer versionNum;
    private final Short loanOfficerId;
    private final String externalId;
    private final String mfiJoiningDate;
    private final AddressDto address;
    private final List<CustomFieldDto> customFields;
    private final List<CustomerPositionDto> customerPositions;

    public CenterUpdate(Integer customerId, String displayName,Integer versionNum, Short loanOfficerId, String externalId, String mfiJoiningDate, AddressDto address,
            List<CustomFieldDto> customFields, List<CustomerPositionDto> customerPositions) {
        this.customerId = customerId;
        this.displayName = displayName;
        this.versionNum = versionNum;
        this.loanOfficerId = loanOfficerId;
        this.externalId = externalId;
        this.mfiJoiningDate = mfiJoiningDate;
        this.address = address;
        this.customFields = customFields;
        this.customerPositions = customerPositions;
    }

    public Short getLoanOfficerId() {
        return this.loanOfficerId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getExternalId() {
        return this.externalId;
    }

    public String getMfiJoiningDate() {
        return this.mfiJoiningDate;
    }

    public AddressDto getAddress() {
        return this.address;
    }

    public List<CustomFieldDto> getCustomFields() {
        return this.customFields;
    }

    public List<CustomerPositionDto> getCustomerPositions() {
        return this.customerPositions;
    }

    public Integer getCustomerId() {
        return this.customerId;
    }

    public Integer getVersionNum() {
        return this.versionNum;
    }
}