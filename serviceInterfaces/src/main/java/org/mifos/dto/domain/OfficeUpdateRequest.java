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

package org.mifos.dto.domain;


public class OfficeUpdateRequest {

    private final String officeName;
    private final String shortName;
    private final Short newStatus;
    private final Short newlevel;
    private final Short parentOfficeId;
    private final AddressDto address;

    public OfficeUpdateRequest(String officeName, String shortName, Short newStatus, Short newlevel,
            Short parentOfficeId, AddressDto address) {
        this.officeName = officeName;
        this.shortName = shortName;
        this.newStatus = newStatus;
        this.newlevel = newlevel;
        this.parentOfficeId = parentOfficeId;
        this.address = address;
    }

    public String getOfficeName() {
        return this.officeName;
    }

    public String getShortName() {
        return this.shortName;
    }

    public Short getNewStatus() {
        return this.newStatus;
    }

    public Short getNewlevel() {
        return this.newlevel;
    }

    public Short getParentOfficeId() {
        return this.parentOfficeId;
    }

    public AddressDto getAddress() {
        return this.address;
    }
}