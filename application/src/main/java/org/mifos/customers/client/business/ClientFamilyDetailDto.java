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

package org.mifos.customers.client.business;

import java.sql.Date;

import org.mifos.framework.business.service.DataTransferObject;
import org.mifos.framework.util.helpers.DateUtils;

public class ClientFamilyDetailDto implements DataTransferObject  {

    private Short relationship;
    private Short gender;
    private Short livingStatus;
    private Date dateOfBirth;
    private String displayName;

    public ClientFamilyDetailDto(Short relationship, Short gender, Short livingStatus, Date dateOfBirth) {
        super();
        this.relationship = relationship;
        this.gender = gender;
        this.livingStatus = livingStatus;
        this.dateOfBirth = dateOfBirth;
    }

    public Short getRelationship() {
        return this.relationship;
    }

    public void setRelationship(Short relationship) {
        this.relationship = relationship;
    }

    public Short getGender() {
        return this.gender;
    }

    public void setGender(Short gender) {
        this.gender = gender;
    }

    public Short getLivingStatus() {
        return this.livingStatus;
    }

    public void setLivingStatus(Short livingStatus) {
        this.livingStatus = livingStatus;
    }

    public Date getDateOfBirth() {
        return this.dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getDateOfBirthForBrowser() {
        if(getDateOfBirth()!=null) {
            return DateUtils.makeDateAsSentFromBrowser(getDateOfBirth());
        }
        return null;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}