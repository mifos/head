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

package org.mifos.dto.domain;

import java.io.Serializable;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_NO_SERIALVERSIONID", justification="should disable at filter level and also for pmd - not important for us")
public class OfficeDetailsDto implements Serializable {

    private final Short officeId;
    private final String officeName;
    private final Short levelId;
    private final String levelNameKey;
    private final Integer versionNo;
    private String levelName;

    /**
     * used by hibernate queries
     */
    public OfficeDetailsDto(final Short levelId, final String levelNameKey) {
        this.levelId = levelId;
        this.levelNameKey = levelNameKey;
        this.officeId = null;
        this.officeName = null;
        this.versionNo = null;
    }

    /**
     * used by hibernate queries
     */
    public OfficeDetailsDto(final Short officeId, final String officeName, final Integer versionNo) {
        this.officeId = officeId;
        this.officeName = officeName;
        this.levelId = null;
        this.levelNameKey = null;
        this.versionNo = versionNo;
    }

    public OfficeDetailsDto(final Short officeId, final String officeName, final Short levelId, final Integer versionNo) {
        this.officeId = officeId;
        this.officeName = officeName;
        this.levelId = levelId;
        this.levelNameKey = null;
        this.versionNo = versionNo;
    }

    public OfficeDetailsDto(final Short officeId, final String officeName, final Short levelId, final String levelNameKey, final Integer versionNo) {
        this.officeId = officeId;
        this.officeName = officeName;
        this.levelId = levelId;
        this.levelNameKey = levelNameKey;
        this.versionNo = versionNo;
    }

    public Short getLevelId() {
        return levelId;
    }

    public Short getOfficeId() {
        return officeId;
    }

    public String getOfficeName() {
        return officeName;
    }

    public Integer getVersionNo() {
        return versionNo;
    }

    public String getDisplayName() {
        return getLevelName() + "(" + officeName + ")";
    }

    public String getLevelName() {
        return this.levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getLevelNameKey() {
        return this.levelNameKey;
    }
}
