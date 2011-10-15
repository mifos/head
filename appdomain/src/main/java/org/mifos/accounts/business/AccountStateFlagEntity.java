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

package org.mifos.accounts.business;

import java.util.Set;

import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.servicefacade.ApplicationContextProvider;

public class AccountStateFlagEntity extends MasterDataEntity {

    /** The composite primary key value */
    private Short id;
    private String flagDescription;
    private Short retained;
    private Short statusId;
    private String statusFlagMessageText = "";

    /** The value of the lookupValue association. */
    private LookUpValueEntity lookUpValue;

    protected AccountStateFlagEntity() {
    }

    public String getFlagDescription() {
        return flagDescription;
    }

    @Override
    public Short getId() {
        return id;
    }

    @Override
    public LookUpValueEntity getLookUpValue() {
        return lookUpValue;
    }
    @Override
    public String getName() {
        String name = ApplicationContextProvider.getBean(MessageLookup.class).lookup(getLookUpValue());
        return name;

    }

    @Override
    public Set<LookUpValueLocaleEntity> getNames() {
        return getLookUpValue().getLookUpValueLocales();
    }

    Short getRetained() {
        return this.retained;
    }

    public Short getStatusId() {
        return statusId;
    }

    public boolean isFlagRetained() {
        return this.getRetained() == 1;
    }

    public void setFlagDescription(String flagDescription) {
        this.flagDescription = flagDescription;
    }

    protected void setId(Short id) {
        this.id = id;
    }

    protected void setLookUpValue(LookUpValueEntity lookUpValue) {
        this.lookUpValue = lookUpValue;
    }

    void setRetained(Short retained) {
        this.retained = retained;
    }

    public void setStatusId(Short statusId) {
        this.statusId = statusId;
    }

    public String getStatusFlagMessageText() {
        return this.statusFlagMessageText;
    }

    public void setStatusFlagMessageText(String statusFlagMessageText) {
        this.statusFlagMessageText = statusFlagMessageText;
    }
}