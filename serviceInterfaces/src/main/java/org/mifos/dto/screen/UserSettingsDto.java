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

package org.mifos.dto.screen;

import java.util.List;

import org.mifos.dto.domain.ValueListElement;

public class UserSettingsDto {

    private final String gender;
    private final String martialStatus;
    private final String language;
    private final String sitePreference;
    private final int age;
    private final List<ValueListElement> genders;
    private final List<ValueListElement> martialStatuses;
    private final List<ValueListElement> languages;
    private final List<ValueListElement> sitePreferenceTypes;

    public UserSettingsDto(String gender, String martialStatus, String language, int age, String sitePreference,
            List<ValueListElement> genders, List<ValueListElement> martialStatuses, List<ValueListElement> languages,
            List<ValueListElement> sitePreferenceTypes) {
        super();
        this.gender = gender;
        this.martialStatus = martialStatus;
        this.language = language;
        this.sitePreference = sitePreference;
        this.age = age;
        this.genders = genders;
        this.martialStatuses = martialStatuses;
        this.languages = languages;
        this.sitePreferenceTypes = sitePreferenceTypes;
    }

    public String getGender() {
        return this.gender;
    }

    public String getMartialStatus() {
        return this.martialStatus;
    }

    public String getLanguage() {
        return this.language;
    }

    public int getAge() {
        return this.age;
    }

    public List<ValueListElement> getGenders() {
        return this.genders;
    }

    public List<ValueListElement> getMartialStatuses() {
        return this.martialStatuses;
    }

    public List<ValueListElement> getLanguages() {
        return this.languages;
    }

    public String getSitePreference() {
        return sitePreference;
    }

    public List<ValueListElement> getSitePreferenceTypes() {
        return sitePreferenceTypes;
    }

}
