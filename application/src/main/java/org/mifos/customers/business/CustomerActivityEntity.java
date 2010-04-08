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

package org.mifos.customers.business;

import java.util.Date;

import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.util.helpers.Money;

public class CustomerActivityEntity extends PersistentObject {

    private final Integer customerActivityId;

    private final Money amount;

    private final CustomerAccountBO customerAccount;

    private final String description;

    private final PersonnelBO personnel;

    private Date createdDate;

    private Short createdBy;

    protected CustomerActivityEntity() {
        customerActivityId = null;
        this.customerAccount = null;
        this.personnel = null;
        this.amount = null;
        this.description = null;
        this.createdDate = null;
    }

    public CustomerActivityEntity(CustomerAccountBO customerAccount, PersonnelBO personnel, Money amount,
            String description, Date trxnDate) {
        customerActivityId = null;
        this.customerAccount = customerAccount;
        this.personnel = personnel;
        this.amount = amount;
        this.description = description;
        this.createdDate = trxnDate;
    }

    public Short getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Short createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Money getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public PersonnelBO getPersonnel() {
        return personnel;
    }

}
