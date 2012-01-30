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
package org.mifos.config;

public enum SitePreferenceType {
    AUTO(1),
    MOBILE(2),
    NORMAL(3);
    
    private final Short value;

    private SitePreferenceType(Integer value) {
        this.value = value.shortValue();
    }

    public Short getValue() {
        return value;
    }
    
    public static SitePreferenceType getSitePreference(Short value){
        SitePreferenceType sitePreferenceType = null;
        if ( value == null ){
            sitePreferenceType = AUTO;
        } else {
            for (SitePreferenceType sitePreference : SitePreferenceType.values()){
                if ( sitePreference.getValue().equals(value)){
                    sitePreferenceType = sitePreference;
                    break;
                }
            }    
        }
        return sitePreferenceType;
    }
}
