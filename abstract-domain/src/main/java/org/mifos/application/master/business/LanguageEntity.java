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

public class LanguageEntity extends MasterDataEntity {

    /** The composite primary key value */
    private Short id;

    private final String languageName;

    private final String languageShortName;

    private Short localeId;

    /** The value of the lookupValue association. */
    private LookUpValueEntity lookUpValue;

    public LanguageEntity() {
        super();
        languageName = null;
        languageShortName = null;
    }

    @Override
    public Short getId() {
        return id;
    }

    public String getLanguageName() {
        return languageName;
    }

    public String getLanguageShortName() {
        return languageShortName;
    }

    public Short getLocaleId() {
        return localeId;
    }

//    public String getLookedUpLanguageName() {
//        return this.getName();
//    }

    @Override
    public LookUpValueEntity getLookUpValue() {
        return lookUpValue;
    }

    @Override
    public String getName() {
//        String name = MessageLookup.getInstance().lookup(getLookUpValue());
        return "";

    }

    @Override
    public Set<LookUpValueLocaleEntity> getNames() {
        return getLookUpValue().getLookUpValueLocales();
    }

    protected void setId(Short id) {
        this.id = id;
    }

    @Override
    public void setLocaleId(Short localeId) {
        this.localeId = localeId;
    }

    protected void setLookUpValue(LookUpValueEntity lookUpValue) {
        this.lookUpValue = lookUpValue;
    }
}
