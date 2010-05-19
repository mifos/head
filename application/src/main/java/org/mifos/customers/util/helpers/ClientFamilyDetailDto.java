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

package org.mifos.customers.util.helpers;

import java.util.Date;

import org.mifos.framework.util.helpers.DateUtils;

public class ClientFamilyDetailDto {

    private final String relationship;
    private final String displayName;
    private final Date dateOfBirth;
    private final String gender;
    private final String livingStatus;

    public ClientFamilyDetailDto(final String relationship, final String displayName, final Date dateOfBirth,
            final String gender, final String livingStatus) {
        this.relationship = relationship;
        this.displayName = displayName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.livingStatus = livingStatus;
    }

    public String getRelationship() {
        return this.relationship;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public Date getDateOfBirth() {
        return this.dateOfBirth;
    }

    public String getGender() {
        return this.gender;
    }

    public String getLivingStatus() {
        return this.livingStatus;
    }

    public String getDateOfBirthForBrowser() {
        if (getDateOfBirth() != null) {
            return DateUtils.makeDateAsSentFromBrowser(getDateOfBirth());
        }
        return null;
    }
}