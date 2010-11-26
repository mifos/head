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

import org.mifos.framework.business.AbstractEntity;

public class ClientFamilyDetailEntity extends AbstractEntity {

    private final Integer customerFamilyId;
    private final ClientBO client;
    private final ClientNameDetailEntity clientName;
    private Short relationship;
    private Short gender;
    private Short livingStatus;
    private Date dateOfBirth;

    public ClientFamilyDetailEntity(ClientBO client,ClientNameDetailEntity clientName, ClientFamilyDetailDto clientFamily){
        this.customerFamilyId=null;
        this.client=client;
        this.clientName=clientName;
        this.relationship=clientFamily.getRelationship();
        this.gender=clientFamily.getGender();
        this.livingStatus=clientFamily.getLivingStatus();
        this.dateOfBirth=clientFamily.getDateOfBirth();
    }

    protected ClientFamilyDetailEntity() {
        super();
        this.customerFamilyId=null;
        this.client=null;
        this.clientName=null;
        this.relationship= null;
        this.gender=null;
        this.livingStatus=null;
        this.dateOfBirth=null;
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

    public void setGender(short gender) {
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

    public Integer getCustomerFamilyId() {
        return this.customerFamilyId;
    }

    public ClientBO getClient() {
        return this.client;
    }

    public ClientNameDetailEntity getClientName() {
        return this.clientName;
    }

    public void updateClientFamilyDetails(ClientFamilyDetailDto familyDetails){
        this.setRelationship(familyDetails.getRelationship());
        this.setGender(familyDetails.getGender());
        this.setLivingStatus(familyDetails.getLivingStatus());
        this.setDateOfBirth(familyDetails.getDateOfBirth());
    }

    public ClientFamilyDetailDto toDto() {
        return new ClientFamilyDetailDto(relationship, gender, livingStatus, dateOfBirth);
    }
}