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

package org.mifos.application.master.business;

import java.util.Set;

import org.mifos.application.master.MessageLookup;
import org.mifos.customers.client.util.helpers.ClientConstants;

public class SpouseFatherLookupEntity extends MasterDataEntity {

    /** The composite primary key value */
    private Short id;

    private Short localeId;

    /** The value of the lookupValue association. */
    private LookUpValueEntity lookUpValue;

    protected SpouseFatherLookupEntity() {
        super();
    }
    public SpouseFatherLookupEntity(Short id) {
        this.id = id;
    }

    public Short getId() {
        return id;
    }

    public Short getLocaleId() {
        return localeId;
    }

    public LookUpValueEntity getLookUpValue() {
        return lookUpValue;
    }

    public String getName() {
        return MessageLookup.getInstance().lookup(getLookUpValue());
    }

    public Set<LookUpValueLocaleEntity> getNames() {
        return getLookUpValue().getLookUpValueLocales();
    }

    public boolean isOneTime() {
        return getId().equals(ClientConstants.FATHER_VALUE);
    }

    public boolean isSpouse() {
        return getId().equals(ClientConstants.SPOUSE_VALUE);
    }

    protected void setId(Short id) {
        this.id = id;
    }

    public void setLocaleId(Short localeId) {
        this.localeId = localeId;
    }

    protected void setLookUpValue(LookUpValueEntity lookUpValue) {
        this.lookUpValue = lookUpValue;
    }

    protected void setName(String name) {
        MessageLookup.getInstance().updateLookupValue(getLookUpValue(), name);
    }
}
