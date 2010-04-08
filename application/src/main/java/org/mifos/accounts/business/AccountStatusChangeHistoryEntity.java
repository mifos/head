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
import java.util.Locale;

import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.framework.business.AbstractEntity;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.DateUtils;

public class AccountStatusChangeHistoryEntity extends AbstractEntity {
    private final Integer accountStatusChangeId;

    private final AccountBO account;

    private final AccountStateEntity oldStatus;

    private final AccountStateEntity newStatus;

    private final PersonnelBO personnel;

    private Locale locale = null;

    private Date createdDate;

    protected AccountStatusChangeHistoryEntity() {
        accountStatusChangeId = null;
        this.oldStatus = null;
        this.newStatus = null;
        this.personnel = null;
        this.account = null;
    }

    public AccountStatusChangeHistoryEntity(AccountStateEntity oldStatus, AccountStateEntity newStatus,
            PersonnelBO personnel, AccountBO account) {
        accountStatusChangeId = null;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.personnel = personnel;
        this.account = account;
        this.setCreatedDate(new DateTimeService().getCurrentJavaDateTime());
    }

    public AccountBO getAccount() {
        return account;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getAccountStatusChangeId() {
        return accountStatusChangeId;
    }

    public AccountStateEntity getNewStatus() {
        return newStatus;
    }

    public AccountStateEntity getOldStatus() {
        return oldStatus;
    }

    public PersonnelBO getPersonnel() {
        return personnel;
    }

    public String getPersonnelName() {
        return personnel.getDisplayName();
    }

    public String getOldStatusName() {
        if (oldStatus.getId().equals(newStatus.getId())) {
            return "-";
        }
        return oldStatus.getName();
    }

    public String getNewStatusName() {
        return newStatus.getName();
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getUserPrefferedTransactionDate() {
        return DateUtils.getUserLocaleDate(getLocale(), getCreatedDate().toString());
    }

}
