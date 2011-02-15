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

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_NO_SERIALVERSIONID", justification="should disable at filter level and also for pmd - not important for us")
public class DefinePersonnelDto implements Serializable {

    private final String officeName;
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_BAD_FIELD")
    private final List<ListElement> titleList;
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_BAD_FIELD")
    private final List<ListElement> personnelLevelList;
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_BAD_FIELD")
    private final List<ListElement> genderList;
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_BAD_FIELD")
    private final List<ListElement> maritalStatusList;
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_BAD_FIELD")
    private final List<ListElement> languageList;
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_BAD_FIELD")
    private final List<ListElement> rolesList;

    public DefinePersonnelDto(String officeName, List<ListElement> titleList,
            List<ListElement> personnelLevelList, List<ListElement> genderList,
            List<ListElement> maritalStatusList, List<ListElement> languageList,
            List<ListElement> rolesList) {
        super();
        this.officeName = officeName;
        this.titleList = titleList;
        this.personnelLevelList = personnelLevelList;
        this.genderList = genderList;
        this.maritalStatusList = maritalStatusList;
        this.languageList = languageList;
        this.rolesList = rolesList;
    }

    public String getOfficeName() {
        return this.officeName;
    }

    public List<ListElement> getTitleList() {
        return this.titleList;
    }

    public List<ListElement> getPersonnelLevelList() {
        return this.personnelLevelList;
    }

    public List<ListElement> getGenderList() {
        return this.genderList;
    }

    public List<ListElement> getMaritalStatusList() {
        return this.maritalStatusList;
    }

    public List<ListElement> getLanguageList() {
        return this.languageList;
    }

    public List<ListElement> getRolesList() {
        return this.rolesList;
    }
}
