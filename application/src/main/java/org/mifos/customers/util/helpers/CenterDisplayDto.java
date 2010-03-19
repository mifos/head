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
package org.mifos.customers.util.helpers;

import java.util.Date;

import org.mifos.framework.business.service.DataTransferObject;

/**
 *
 */
public class CenterDisplayDto implements DataTransferObject {

    private final Integer customerId;
    private final String globalCustNum;
    private final String displayName;
    private final Short branchId;
    private final Date mfiJoiningDate;
    private final Date createdDate;
    private final Integer versionNo;
    private final String externalId;
    private final Short customerLevelId;
    private final Short customerStatusId;
    private final String customerStatusName;
    private final Short loanOfficerId;
    private final String loanOfficerName;

    public CenterDisplayDto(final Integer customerId, final String globalCustNum, final String displayName,
            final Short branchId, final Date mfiJoiningDate, final Date createdDate, final Integer versionNo,
            final String externalId, final Short customerLevelId, final Short customerStatusId,
            final String customerStatusName, final Short loanOfficerId, final String loanOfficerName) {

        this.customerId = customerId;
        this.globalCustNum = globalCustNum;
        this.displayName = displayName;
        this.branchId = branchId;
        this.mfiJoiningDate = mfiJoiningDate;
        this.createdDate = createdDate;
        this.versionNo = versionNo;
        this.externalId = externalId;
        this.customerLevelId = customerLevelId;
        this.customerStatusId = customerStatusId;
        this.customerStatusName = customerStatusName;
        this.loanOfficerId = loanOfficerId;
        this.loanOfficerName = loanOfficerName;
    }

    public Integer getCustomerId() {
        return this.customerId;
    }

    public String getGlobalCustNum() {
        return this.globalCustNum;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public Short getBranchId() {
        return this.branchId;
    }

    public Date getMfiJoiningDate() {
        return this.mfiJoiningDate;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public Integer getVersionNo() {
        return this.versionNo;
    }

    public String getExternalId() {
        return this.externalId;
    }

    public Short getCustomerLevelId() {
        return this.customerLevelId;
    }

    public Short getCustomerStatusId() {
        return this.customerStatusId;
    }

    public String getCustomerStatusName() {
        return this.customerStatusName;
    }

    public Short getLoanOfficerId() {
        return this.loanOfficerId;
    }

    public String getLoanOfficerName() {
        return this.loanOfficerName;
    }

}
