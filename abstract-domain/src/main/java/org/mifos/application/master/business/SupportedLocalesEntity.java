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

import org.mifos.framework.business.AbstractEntity;

public class SupportedLocalesEntity extends AbstractEntity {

    private CountryEntity country;

    private Short defaultLocale;

    /** The composite primary key value */
    private Short id;

    private LanguageEntity language;
    private Short localeId;

    private String localeName;

    /** The value of the lookupValue association. */
    private LookUpValueEntity lookUpValue;

    public SupportedLocalesEntity() {
        super();

    }

    public SupportedLocalesEntity(Short localeId) {
        super();
        this.localeId = localeId;
    }

    public CountryEntity getCountry() {
        return country;
    }

    public String getCountryCode() {
        return country.getCountryShortName();
    }

    public String getCountryName() {
        return country.getCountryName();
    }

    public Short getDefaultLocale() {
        return defaultLocale;
    }

    public Short getId() {
        return id;
    }

    public LanguageEntity getLanguage() {
        return language;
    }

    public String getLanguageCode() {
        return language.getLanguageShortName();
    }

    public String getLanguageName() {
        return language.getLanguageName();
    }

    public Short getLocaleId() {
        return localeId;
    }

    public String getLocaleName() {
        return localeName;
    }

    public LookUpValueEntity getLookUpValue() {
        return lookUpValue;
    }

//    public String getName() {
//        String name = MessageLookup.getInstance().lookup(getLookUpValue());
//        return name;
//    }

    public Set<LookUpValueLocaleEntity> getNames() {
        return getLookUpValue().getLookUpValueLocales();
    }

    public void setCountry(CountryEntity country) {
        this.country = country;
    }

    public void setDefaultLocale(Short defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    protected void setId(Short id) {
        this.id = id;
    }

    public void setLanguage(LanguageEntity language) {
        this.language = language;
    }

    public void setLocaleId(Short localeId) {
        this.localeId = localeId;
    }

    public void setLocaleName(String localeName) {
        this.localeName = localeName;
    }

    protected void setLookUpValue(LookUpValueEntity lookUpValue) {
        this.lookUpValue = lookUpValue;
    }
}
