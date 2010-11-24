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

package org.mifos.dto.domain;

import java.util.List;

import org.joda.time.DateTime;

public class GroupCreationDetail {

    private final String displayName;
    private final String externalId;
    private final AddressDto addressDto;
    private final Short loanOfficerId;
    private final List<ApplicableAccountFeeDto> feesToApply;
    private final Short customerStatus;
    private final boolean trained;
    private final DateTime trainedOn;
    private final String parentSystemId;
    private final Short officeId;

    @SuppressWarnings("PMD")
    public GroupCreationDetail(String displayName, String externalId, AddressDto addressDto, Short loanOfficerId,
            List<ApplicableAccountFeeDto> feesToApply, Short customerStatus, boolean trained, DateTime trainedOn, String parentSystemId, Short officeId) {
        this.displayName = displayName;
        this.externalId = externalId;
        this.addressDto = addressDto;
        this.loanOfficerId = loanOfficerId;
        this.feesToApply = feesToApply;
        this.customerStatus = customerStatus;
        this.trained = trained;
        this.trainedOn = trainedOn;
        this.parentSystemId = parentSystemId;
        this.officeId = officeId;
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

    public List<ApplicableAccountFeeDto> getFeesToApply() {
        return this.feesToApply;
    }

    public Short getCustomerStatus() {
        return this.customerStatus;
    }

    public boolean isTrained() {
        return this.trained;
    }

    public DateTime getTrainedOn() {
        return this.trainedOn;
    }

    public String getParentSystemId() {
        return this.parentSystemId;
    }

    public Short getOfficeId() {
        return this.officeId;
    }
}