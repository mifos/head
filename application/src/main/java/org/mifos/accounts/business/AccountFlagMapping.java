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

package org.mifos.accounts.business;

import java.util.Date;

import org.mifos.framework.business.AbstractEntity;

public class AccountFlagMapping extends AbstractEntity {

    private Integer accountFlagId;
    private AccountStateFlagEntity flag;
    private Date createdDate;
    private Short createdBy;

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

    public Integer getAccountFlagId() {
        return accountFlagId;
    }

    @SuppressWarnings("unused")
    // see .hbm.xml file
    private void setAccountFlagId(Integer accountFlagId) {
        this.accountFlagId = accountFlagId;
    }

    public AccountStateFlagEntity getFlag() {
        return flag;
    }

    public void setFlag(AccountStateFlagEntity flag) {
        this.flag = flag;
    }

}
