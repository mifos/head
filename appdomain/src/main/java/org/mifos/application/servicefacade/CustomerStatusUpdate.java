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

import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.customers.util.helpers.CustomerStatusFlag;

public class CustomerStatusUpdate {

    private final Integer customerId;
    private final Integer versionNum;
    private final CustomerStatusFlag customerStatusFlag;
    private final CustomerStatus newStatus;
    private final String notes;

    public CustomerStatusUpdate(Integer customerId, Integer versionNum, CustomerStatusFlag customerStatusFlag, CustomerStatus newStatus, String notes) {
        this.customerId = customerId;
        this.versionNum = versionNum;
        this.customerStatusFlag = customerStatusFlag;
        this.newStatus = newStatus;
        this.notes = notes;
    }

    public Integer getCustomerId() {
        return this.customerId;
    }

    public Integer getVersionNum() {
        return this.versionNum;
    }

    public CustomerStatusFlag getCustomerStatusFlag() {
        return this.customerStatusFlag;
    }

    public CustomerStatus getNewStatus() {
        return this.newStatus;
    }

    public String getNotes() {
        return this.notes;
    }
}