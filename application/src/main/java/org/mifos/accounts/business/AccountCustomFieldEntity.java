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

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.mifos.accounts.savings.persistence.SavingsPersistence;
import org.mifos.framework.business.AbstractEntity;
import org.mifos.framework.exceptions.ApplicationException;

@Entity
@Table(name = "account_custom_field")
public class AccountCustomFieldEntity extends AbstractEntity {

    @Id
    @GeneratedValue
    private Integer accountCustomFieldId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", unique = true)
    private AccountBO account;

    /*
     * Reference to a {@link CustomFieldDefinitionEntity}
     */
    private Short fieldId;

    private String fieldValue;

    public AccountCustomFieldEntity() {
    }

    public AccountCustomFieldEntity(AccountBO account, Short fieldId, String fieldValue) {
        super();
        this.account = account;
        this.fieldId = fieldId;
        this.fieldValue = fieldValue;

    }

    public Integer getAccountCustomFieldId() {
        return accountCustomFieldId;
    }

    public void setAccountCustomFieldId(Integer accountCustomFieldId) {
        this.accountCustomFieldId = accountCustomFieldId;
    }

    public AccountBO getAccount() {
        return account;
    }

    public void setAccount(AccountBO account) {
        this.account = account;
    }

    public Short getFieldId() {
        return fieldId;
    }

    public void setFieldId(Short fieldId) {
        this.fieldId = fieldId;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public void save(AccountCustomFieldEntity accountCustomFieldEntity) throws ApplicationException {
        new SavingsPersistence().createOrUpdate(accountCustomFieldEntity);
    }
}
