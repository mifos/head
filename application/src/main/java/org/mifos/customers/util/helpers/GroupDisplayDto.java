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
public class GroupDisplayDto implements DataTransferObject {

    private final Integer customerId;
    private final String globalCustNum;
    private final String displayName;
    private final String parentCustomerDisplayName;
    private final Short branchId;
    private final String externalId;
    private final String customerFormedByDisplayName;
    private final Date customerActivationDate;
    private final Short customerLevelId;
    private final Short customerStatusId;
    private final String customerStatusName;
    /*
     * johnw - I can't find anywhere 'trained' information can be entered against a group (even if no center hierarchy).
     * In edit group, a read-only training status is shown which has a date which appears to be the customer activation
     * date. However, the viewgroupdetails.jsp does reference 'trained' information.
     */
    private final Boolean trained;
    private final Date trainedDate;
    //
    /*
     * johnw - viewgroupdetails.jsp checks if blacklisted (a CLOSE flag). However, CLOSED and CANCELLED groups are
     * excluded from viewgroupdetails.jsp.
     */
    private final Boolean blackListed;
    private final Short loanOfficerId;
    private final String loanOfficerName;

    public GroupDisplayDto(final Integer customerId, final String globalCustNum, final String displayName,
            final String parentCustomerDisplayName, final Short branchId, final String externalId,
            final String customerFormedByDisplayName, final Date customerActivationDate, final Short customerLevelId,
            final Short customerStatusId, final String customerStatusName, final Boolean trained,
            final Date trainedDate, final Boolean blackListed, final Short loanOfficerId, final String loanOfficerName) {

        this.customerId = customerId;
        this.globalCustNum = globalCustNum;
        this.displayName = displayName;
        this.parentCustomerDisplayName = parentCustomerDisplayName;
        this.branchId = branchId;
        this.externalId = externalId;
        this.customerFormedByDisplayName = customerFormedByDisplayName;
        this.customerActivationDate = customerActivationDate;
        this.customerLevelId = customerLevelId;
        this.customerStatusId = customerStatusId;
        this.customerStatusName = customerStatusName;
        this.trained = trained;
        this.trainedDate = trainedDate;
        this.blackListed = blackListed;
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

    public String getParentCustomerDisplayName() {
        return this.parentCustomerDisplayName;
    }

    public Short getBranchId() {
        return this.branchId;
    }

    public String getExternalId() {
        return this.externalId;
    }

    public String getCustomerFormedByDisplayName() {
        return this.customerFormedByDisplayName;
    }

    public Date getCustomerActivationDate() {
        return this.customerActivationDate;
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

    public Boolean getTrained() {
        return this.trained;
    }

    public Date getTrainedDate() {
        return this.trainedDate;
    }

    public Boolean getBlackListed() {
        return this.blackListed;
    }

    public Short getLoanOfficerId() {
        return this.loanOfficerId;
    }

    public String getLoanOfficerName() {
        return this.loanOfficerName;
    }

}
