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



package org.mifos.ui.core.controller;
import java.util.List;

//import org.mifos.dto.domain.AddressDto;
import org.hibernate.validator.constraints.NotEmpty;
import org.mifos.dto.domain.CustomFieldDto;


@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_NO_SERIALVERSIONID", justification="should disable at filter level and also for pmd - not important for us")
public class OfficeFormBean{

    private Short id;

    @NotEmpty
    private String name;

    private String searchId;

    @NotEmpty
    private String officeShortName;
    private String globalNum;

    private String parentId;
    private String statusId;

    @NotEmpty
    private String levelId;
    private String parentOfficeName;
    private Integer versionNum = Integer.valueOf(0);
    private String lookupNameKey;

    private String officeStatusName;
    private String officeLevelName;
    private List<CustomFieldDto> customFields;
/* * Address fields * */
    private String line1;
    private String line2;
    private String line3;
    private String city;
    private String state;
    private String country;
    private String zip;
    private String phoneNumber;
/* * Address fields * */
    public String getSearchId() {
        return this.searchId;
    }

    public Short getId() {
        return this.id;
    }

    public String getName() {
        return this.name.trim();
    }

    public String getText() {
        return this.name.trim();
    }

    public String getOfficeShortName() {
        return this.officeShortName;
    }

    public String getGlobalNum() {
        return this.globalNum;
    }

    public String getParentId() {
        return this.parentId;
    }

    public String getStatusId() {
        return this.statusId;
    }

    public String getLevelId() {
        return this.levelId;
    }

    public String getParentOfficeName() {
        return this.parentOfficeName;
    }

    public Integer getVersionNum() {
        return this.versionNum;
    }

    public String getLookupNameKey() {
        return this.lookupNameKey;
    }

    public String getOfficeStatusName() {
        return this.officeStatusName;
    }

    public String getOfficeLevelName() {
        return this.officeLevelName;
    }



    public List<CustomFieldDto> getCustomFields() {
        return this.customFields;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSearchId(String searchId) {
        this.searchId = searchId;
    }

    public void setOfficeShortName(String officeShortName) {
        this.officeShortName = officeShortName;
    }

    public void setGlobalNum(String globalNum) {
        this.globalNum = globalNum;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public void setLevelId(String levelId) {
        this.levelId = levelId;
    }

    public void setParentOfficeName(String parentOfficeName) {
        this.parentOfficeName = parentOfficeName;
    }

    public void setVersionNum(Integer versionNum) {
        this.versionNum = versionNum;
    }

    public void setLookupNameKey(String lookupNameKey) {
        this.lookupNameKey = lookupNameKey;
    }

    public void setOfficeStatusName(String officeStatusName) {
        this.officeStatusName = officeStatusName;
    }

    public void setOfficeLevelName(String officeLevelName) {
        this.officeLevelName = officeLevelName;
    }



    public void setCustomFields(List<CustomFieldDto> customFields) {
        this.customFields = customFields;
    }

    public String getLine1() {
        return this.line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return this.line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getLine3() {
        return this.line3;
    }

    public void setLine3(String line3) {
        this.line3 = line3;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZip() {
        return this.zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}