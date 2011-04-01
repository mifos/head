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

package org.mifos.accounts.util.helpers;

import org.mifos.customers.api.DataTransferObject;

public class AccountSearchResultsDto implements DataTransferObject {

    private static final long serialVersionUID = -2925583671044915036L;

    private String officeName;

    private short customerLevelId;

    private String parentOfParentCustomerName;

    private String parentCustomerName;

    private String customerName;

    private int customerId;

    private String globelNo;

    public String getParentOfParentCustomerName() {
        return this.parentOfParentCustomerName;
    }

    public void setParentOfParentCustomerName(String parentOfParentCustomerName) {
        this.parentOfParentCustomerName = parentOfParentCustomerName;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public short getCustomerLevelId() {
        return customerLevelId;
    }

    public void setCustomerLevelId(short customerLevelId) {
        this.customerLevelId = customerLevelId;
    }

    public String getCustomerName() {

        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getParentCustomerName() {
        return parentCustomerName;
    }

    public void setParentCustomerName(String parentCustomerName) {
        this.parentCustomerName = parentCustomerName;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public String getGlobelNo() {
        return globelNo;
    }

    public void setGlobelNo(String globelNo) {
        this.globelNo = globelNo;
    }
}
