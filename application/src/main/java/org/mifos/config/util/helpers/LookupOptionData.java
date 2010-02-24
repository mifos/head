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

package org.mifos.config.util.helpers;

import java.io.Serializable;

public class LookupOptionData implements Serializable {
    private Short valueListId;
    private String lookupValue;
    private Integer lookupId;

    public Integer getLookupId() {
        return lookupId;
    }

    public void setLookupId(Integer lookupId) {
        this.lookupId = lookupId;
    }

    public String getLookupValue() {
        return lookupValue;
    }

    public void setLookupValue(String lookupValue) {
        this.lookupValue = lookupValue;
    }

    public Short getValueListId() {
        return valueListId;
    }

    public void setValueListId(Short valueListId) {
        this.valueListId = valueListId;
    }

}
