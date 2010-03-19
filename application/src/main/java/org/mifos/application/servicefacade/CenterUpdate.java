/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

import org.joda.time.DateTime;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.customers.business.CustomerPositionView;
import org.mifos.framework.business.util.Address;

public class CenterUpdate {

    private final Integer customerId;
    private final Integer versionNum;
    private final Short loanOfficerId;
    private final String externalId;
    private final String mfiJoiningDate;
    private final Address address;
    private final List<CustomFieldView> customFields;
    private final List<CustomerPositionView> customerPositions;
    private DateTime mfiJoiningDateTime;

    public CenterUpdate(Integer customerId, Integer versionNum, Short loanOfficerId, String externalId, String mfiJoiningDate, Address address,
            List<CustomFieldView> customFields, List<CustomerPositionView> customerPositions) {
        this.customerId = customerId;
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

    public String getExternalId() {
        return this.externalId;
    }

    public String getMfiJoiningDate() {
        return this.mfiJoiningDate;
    }

    public Address getAddress() {
        return this.address;
    }

    public List<CustomFieldView> getCustomFields() {
        return this.customFields;
    }

    public List<CustomerPositionView> getCustomerPositions() {
        return this.customerPositions;
    }

    public Integer getCustomerId() {
        return this.customerId;
    }

    public Integer getVersionNum() {
        return this.versionNum;
    }

    public void setMfiJoiningDateAsDateTime(DateTime mfiJoiningDateTime) {
        this.mfiJoiningDateTime = mfiJoiningDateTime;
    }

    public DateTime getMfiJoiningDateTime() {
        return this.mfiJoiningDateTime;
    }

    public void setMfiJoiningDateTime(DateTime mfiJoiningDateTime) {
        this.mfiJoiningDateTime = mfiJoiningDateTime;
    }
}
