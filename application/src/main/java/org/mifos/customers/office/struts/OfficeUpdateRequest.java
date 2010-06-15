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

package org.mifos.customers.office.struts;

import java.util.List;

import org.mifos.application.master.business.CustomFieldDto;
import org.mifos.customers.office.util.helpers.OfficeLevel;
import org.mifos.customers.office.util.helpers.OfficeStatus;
import org.mifos.framework.business.util.Address;

public class OfficeUpdateRequest {

    private final String officeName;
    private final String shortName;
    private final OfficeStatus newStatus;
    private final OfficeLevel newlevel;
    private final Short parentOfficeId;
    private final Address address;
    private final List<CustomFieldDto> customFields;

    public OfficeUpdateRequest(String officeName, String shortName, OfficeStatus newStatus, OfficeLevel newlevel,
            Short parentOfficeId, Address address, List<CustomFieldDto> customFields) {
        this.officeName = officeName;
        this.shortName = shortName;
        this.newStatus = newStatus;
        this.newlevel = newlevel;
        this.parentOfficeId = parentOfficeId;
        this.address = address;
        this.customFields = customFields;
    }

    public String getOfficeName() {
        return this.officeName;
    }

    public String getShortName() {
        return this.shortName;
    }

    public OfficeStatus getNewStatus() {
        return this.newStatus;
    }

    public OfficeLevel getNewlevel() {
        return this.newlevel;
    }

    public Short getParentOfficeId() {
        return this.parentOfficeId;
    }

    public Address getAddress() {
        return this.address;
    }

    public List<CustomFieldDto> getCustomFields() {
        return this.customFields;
    }
}