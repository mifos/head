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

import org.mifos.application.master.business.CustomFieldDto;
import org.mifos.customers.business.CustomerPositionDto;
import org.mifos.framework.business.util.Address;

public class GroupUpdate {

    private final Integer customerId;
    private final Integer versionNo;
    private final Short loanOfficerId;
    private final String externalId;
    private final boolean trained;
    private final String trainedDateAsString;
    private final Address address;
    private final List<CustomFieldDto> customFields;
    private final List<CustomerPositionDto> customerPositions;
    private final String globalCustNum;
    private final String displayName;

    public GroupUpdate(Integer customerId, String globalCustNum, Integer versionNo, String displayName,
            Short loanOfficerId, String externalId, boolean trained, String trainedDateAsString, Address address,
            List<CustomFieldDto> customFields, List<CustomerPositionDto> customerPositions) {
        this.customerId = customerId;
        this.globalCustNum = globalCustNum;
        this.versionNo = versionNo;
        this.displayName = displayName;
        this.loanOfficerId = loanOfficerId;
        this.externalId = externalId;
        this.trained = trained;
        this.trainedDateAsString = trainedDateAsString;
        this.address = address;
        this.customFields = customFields;
        this.customerPositions = customerPositions;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getGlobalCustNum() {
        return this.globalCustNum;
    }

    public Integer getCustomerId() {
        return this.customerId;
    }

    public Integer getVersionNo() {
        return this.versionNo;
    }

    public Short getLoanOfficerId() {
        return this.loanOfficerId;
    }

    public String getExternalId() {
        return this.externalId;
    }

    public boolean isTrained() {
        return this.trained;
    }

    public String getTrainedDateAsString() {
        return this.trainedDateAsString;
    }

    public Address getAddress() {
        return this.address;
    }

    public List<CustomFieldDto> getCustomFields() {
        return this.customFields;
    }

    public List<CustomerPositionDto> getCustomerPositions() {
        return this.customerPositions;
    }
}