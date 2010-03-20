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

import java.util.HashSet;
import java.util.Set;

import org.mifos.accounts.productdefinition.business.ProductTypeEntity;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.master.business.StateEntity;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.config.LocalizedTextLookup;

/**
 * Should be replaced by {@link AccountState}
 */
public class AccountStateEntity extends StateEntity implements LocalizedTextLookup {

    private ProductTypeEntity prdType;

    private Set<AccountStateFlagEntity> flagSet;

    private Short optional;

    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    protected AccountStateEntity() {
    }

    public AccountStateEntity(AccountState accountState) {
        super(accountState.getValue());
        this.flagSet = new HashSet<AccountStateFlagEntity>();
    }

    public ProductTypeEntity getPrdType() {
        return prdType;
    }

    public Set<AccountStateFlagEntity> getFlagSet() {
        return flagSet;
    }

    public void setFlagSet(Set<AccountStateFlagEntity> flagSet) {
        this.flagSet = flagSet;
    }

    public Short getOptional() {
        return optional;
    }

    /**
     * Safer, preferred method for getting the "optional" property. Can't be
     * "isOptional()" because hibernate gets confused.
     */
    public boolean getIsOptional() {
        return optional.equals(YesNoFlag.YES.getValue());
    }

    public void setOptional(Short optional) {
        this.optional = optional;
    }

    /**
     * Safer, preferred method for setting the "optional" property. Can't be
     * "setOptional(boolean)" because hibernate gets confused.
     */
    public void setIsOptional(boolean optional) {
        if (optional) {
            setOptional(YesNoFlag.YES.getValue());
        } else {
            setOptional(YesNoFlag.NO.getValue());
        }
    }

    public String getPropertiesKey() {
        return getLookUpValue().getLookUpName();
    }

    public boolean isInState(AccountState state) {
        return state.getValue().equals(getId());
    }
}
