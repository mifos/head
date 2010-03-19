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
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerPositionView;
import org.mifos.customers.business.CustomerView;
import org.mifos.customers.personnel.business.PersonnelView;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.util.Address;

/**
 * I extend {@link BusinessObject} to keep custom tags in jsp working for now.
 */
public class CenterDto extends BusinessObject {

    private final Short loanOfficerId;
    private final Integer customerId;
    private final String globalCustNum;
    private final DateTime mfiJoiningDate;
    private final String mfiJoiningDateAsString;
    private final Address address;
    private final List<CustomerPositionView> customerPositionViews;
    private final List<CustomFieldView> customFieldViews;
    private final List<CustomerView> clientList;
    private final String externalId;
    private final List<PersonnelView> activeLoanOfficersForBranch;
    private final CustomerBO center;
    private final boolean centerHierarchyExists;

    public CenterDto(Short loanOfficerId, Integer customerId, String globalCustNum, DateTime mfiJoiningDate,
            String mfiJoiningDateAsString, String externalId, Address address,
            List<CustomerPositionView> customerPositionViews, List<CustomFieldView> customFieldViews,
            List<CustomerView> customerList, CustomerBO center, List<PersonnelView> activeLoanOfficersForBranch, boolean centerHierarchyExists) {
        this.loanOfficerId = loanOfficerId;
        this.customerId = customerId;
        this.globalCustNum = globalCustNum;
        this.mfiJoiningDate = mfiJoiningDate;
        this.mfiJoiningDateAsString = mfiJoiningDateAsString;
        this.externalId = externalId;
        this.address = address;
        this.customerPositionViews = customerPositionViews;
        this.customFieldViews = customFieldViews;
        this.clientList = customerList;
        this.center = center;
        this.activeLoanOfficersForBranch = activeLoanOfficersForBranch;
        this.centerHierarchyExists = centerHierarchyExists;
    }

    public boolean isCenterHierarchyExists() {
        return this.centerHierarchyExists;
    }

    public Short getLoanOfficerId() {
        return this.loanOfficerId;
    }

    public Integer getCustomerId() {
        return this.customerId;
    }

    public String getGlobalCustNum() {
        return this.globalCustNum;
    }

    public DateTime getMfiJoiningDate() {
        return this.mfiJoiningDate;
    }

    public String getMfiJoiningDateAsString() {
        return this.mfiJoiningDateAsString;
    }

    public Address getAddress() {
        return this.address;
    }

    public List<CustomerPositionView> getCustomerPositionViews() {
        return this.customerPositionViews;
    }

    public List<CustomFieldView> getCustomFieldViews() {
        return this.customFieldViews;
    }

    public List<CustomerView> getClientList() {
        return this.clientList;
    }

    public String getLoanOfficerIdAsString() {
        String loanOfficerId = "";
        if (this.loanOfficerId != null) {
            loanOfficerId = this.loanOfficerId.toString();
        }
        return loanOfficerId;
    }

    public String getCustomerIdAsString() {
        String customerId = "";
        if (this.customerId != null) {
            customerId = this.customerId.toString();
        }
        return customerId;
    }

    public String getExternalId() {
        return this.externalId;
    }

    public List<PersonnelView> getActiveLoanOfficersForBranch() {
        return this.activeLoanOfficersForBranch;
    }

    public CustomerBO getCenter() {
        return this.center;
    }
}
