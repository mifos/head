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

package org.mifos.application.holiday.business;

import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.framework.business.AbstractEntity;

public class RepaymentRuleEntity extends AbstractEntity {

    private LookUpValueEntity lookUpValue;

    private String lookUpValueKey;

    private Short id;

    protected RepaymentRuleEntity() {
        lookUpValueKey = null;
        lookUpValue = null;
    }

    public RepaymentRuleEntity(Short id, String lookUpValueKey) {
        this.id = id;
        this.lookUpValueKey = lookUpValueKey;
    }

    public Short getId() {
        return this.id;
    }

    public LookUpValueEntity getLookUpObject() {
        return this.lookUpValue;
    }

    public String getLookUpValue() {
        return MessageLookup.getInstance().lookup(lookUpValueKey);
    }

    @SuppressWarnings("unused")
    private void setId(Short Id) {
        this.id = Id;
    }

    @SuppressWarnings("unused")
    private void setLookUpValueKey(String lookUpValueKey) {
        this.lookUpValueKey = lookUpValueKey;
    }

    @SuppressWarnings("unused")
    private void setLookUpValue(LookUpValueEntity lookUpValue) {
        this.lookUpValue = lookUpValue;
    }
}
