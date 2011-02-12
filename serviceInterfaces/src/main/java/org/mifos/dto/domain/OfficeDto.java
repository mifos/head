/*
 * Copyright Grameen Foundation USA
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

package org.mifos.dto.domain;

import java.io.Serializable;
import java.util.List;


@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_NO_SERIALVERSIONID", justification="should disable at filter level and also for pmd - not important for us")
public class OfficeDto implements Serializable {

    private final Short id;
    private final String name;
    private final String searchId;
    private final String officeShortName;
    private final String globalNum;
    private final Short parentId;
    private final Short statusId;
    private final Short levelId;
    private final String parentOfficeName;
    private Integer versionNum = Integer.valueOf(0);
    private final String lookupNameKey;

    private final String officeStatusName;
    private String officeLevelName;
    private final AddressDto address;
    private final List<CustomFieldDto> customFields;

    public OfficeDto(final Short officeId, String officeName, String searchId, String officeShortName, String globalNum, Short parentId, Short statusId, Short levelId) {
        this.id = officeId;
        this.name = officeName;
        this.searchId = searchId;
        this.officeShortName = officeShortName;
        this.globalNum = globalNum;
        this.parentId = parentId;
        this.statusId = statusId;
        this.levelId = levelId;
        this.parentOfficeName = "";
        this.lookupNameKey = "";
        this.officeStatusName = "";
        this.officeLevelName = "";
        this.address = null;
        this.customFields = null;
    }

    public OfficeDto(final Short officeId, String officeName, String searchId, String officeShortName, String globalNum, Short parentId, Short statusId, Short levelId, String parentOfficeName) {
        this.id = officeId;
        this.name = officeName;
        this.searchId = searchId;
        this.officeShortName = officeShortName;
        this.globalNum = globalNum;
        this.parentId = parentId;
        this.statusId = statusId;
        this.levelId = levelId;
        this.parentOfficeName = parentOfficeName;
        this.lookupNameKey = "";
        this.officeStatusName = "";
        this.officeLevelName = "";
        this.address = null;
        this.customFields = null;
    }

    public OfficeDto(final Short officeId, String officeName, String searchId, String officeShortName, String globalNum, Integer versionNum, Short statusId, Short levelId, String lookupNameKey) {
        this.id = officeId;
        this.name = officeName;
        this.searchId = searchId;
        this.officeShortName = officeShortName;
        this.globalNum = globalNum;
        this.parentId = null;
        this.statusId = statusId;
        this.levelId = levelId;
        this.parentOfficeName = "";
        this.versionNum = versionNum;
        this.lookupNameKey = lookupNameKey;
        this.officeStatusName = "";
        this.officeLevelName = "";
        this.address = null;
        this.customFields = null;
    }

    public OfficeDto(Short id, String name, String searchId, String officeShortName, String globalNum, Short parentId,
            Short statusId, Short levelId, String parentOfficeName, Integer versionNum, String officeStatusName, String officeLevelName,
            AddressDto address, List<CustomFieldDto> customFields) {
        super();
        this.id = id;
        this.name = name;
        this.searchId = searchId;
        this.officeShortName = officeShortName;
        this.globalNum = globalNum;
        this.parentId = parentId;
        this.statusId = statusId;
        this.levelId = levelId;
        this.parentOfficeName = parentOfficeName;
        this.versionNum = versionNum;
        this.lookupNameKey = "";
        this.officeStatusName = officeStatusName;
        this.officeLevelName = officeLevelName;
        this.address = address;
        this.customFields = customFields;
    }

    public OfficeDto(Short levelId, Short parentId, List<CustomFieldDto> customFields, String name, String officeShortName, AddressDto address) {
        this.id = null;
        this.name = name;
        this.searchId = "";
        this.officeShortName = officeShortName;
        this.globalNum = null;
        this.parentId = parentId;
        this.statusId = null;
        this.levelId = levelId;
        this.parentOfficeName = "";
        this.versionNum = null;
        this.lookupNameKey = "";
        this.officeStatusName = null;
        this.officeLevelName = null;
        this.address = address;
        this.customFields = customFields;
    }

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

    public Short getParentId() {
        return this.parentId;
    }

    public Short getStatusId() {
        return this.statusId;
    }

    public Short getLevelId() {
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

    public AddressDto getAddress() {
        return this.address;
    }

    public List<CustomFieldDto> getCustomFields() {
        return this.customFields;
    }

    // MIFOS-3780: added necessary setters/getters needed for UI
    public Short getOfficeId() {
        return id;
    }

    public String getDisplayName() {
        return getOfficeLevelName() + "(" + name + ")";
    }

    public void setLevelName(String levelName) {
        this.officeLevelName = levelName;
    }
}
