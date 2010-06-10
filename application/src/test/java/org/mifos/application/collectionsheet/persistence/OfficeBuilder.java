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
package org.mifos.application.collectionsheet.persistence;

import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.util.helpers.OfficeLevel;
import org.mifos.customers.office.util.helpers.OfficeStatus;

/**
 *
 */
public class OfficeBuilder {

    private String name = "builder-branch-office";
    private String globalOfficeNum = "xxx-0000001";
    private OfficeBO parentOffice = null;
    private OfficeLevel officeLevel = OfficeLevel.BRANCHOFFICE;
    private String searchId = "1.1.1.1.";
    private String shortName = "bf1";
    private OfficeStatus status = OfficeStatus.ACTIVE;
    private Short officeId;

    public OfficeBO build() {
        final OfficeBO office = new OfficeBO(officeId, name, shortName, globalOfficeNum, parentOffice, officeLevel, searchId, status);
        return office;
    }

    public OfficeBuilder withName(final String withName) {
        this.name = withName;
        return this;
    }

    public OfficeBuilder withOfficeId(final Short officeId) {
        this.officeId = officeId;
        return this;
    }

    public OfficeBuilder withGlobalOfficeNum(final String withGlobalOfficeNum) {
        this.globalOfficeNum = withGlobalOfficeNum;
        return this;
    }

    public OfficeBuilder withSearchId(String withSearchId) {
        this.searchId = withSearchId;
        return this;
    }

    public OfficeBuilder headOffice() {
        this.officeLevel = OfficeLevel.HEADOFFICE;
        return this;
    }

    public OfficeBuilder regionalOffice() {
        this.officeLevel = OfficeLevel.REGIONALOFFICE;
        return this;
    }

    public OfficeBuilder subRegionalOffice() {
        this.officeLevel = OfficeLevel.SUBREGIONALOFFICE;
        return this;
    }

    public OfficeBuilder areaOffice() {
        this.officeLevel = OfficeLevel.AREAOFFICE;
        return this;
    }

    public OfficeBuilder branchOffice() {
        this.officeLevel = OfficeLevel.BRANCHOFFICE;
        return this;
    }

    public OfficeBuilder withParentOffice(OfficeBO withParentOffice) {
        this.parentOffice = withParentOffice;
        return this;
    }

    public OfficeBuilder withShortName(String withShortName) {
        this.shortName = withShortName;
        return this;
    }

    public OfficeBuilder inActive() {
        this.status = OfficeStatus.INACTIVE;
        return this;
    }
}