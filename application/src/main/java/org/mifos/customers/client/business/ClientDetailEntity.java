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

import java.util.Date;

import org.mifos.customers.business.CustomerBO;
import org.mifos.framework.business.PersistentObject;

public class ClientDetailEntity extends PersistentObject {

    private final Integer customerId;
    private final ClientBO client;
    private Integer ethinicity;
    private Integer citizenship;
    private Integer handicapped;
    private Integer businessActivities;
    private Integer maritalStatus;
    private Integer educationLevel;
    private Short numChildren;
    private Short gender;
    private Date dateStarted;
    private String handicappedDetails;
    private Short povertyStatus;
    private Double povertyLikelihoodPercent;

    /**
     * Create a new instance based on a view.
     *
     */
    @Deprecated
    public ClientDetailEntity(ClientBO client, ClientDetailView view) {
        super();
        this.customerId = null;
        this.client = client;
        this.ethinicity = view.getEthinicity();
        this.citizenship = view.getCitizenship();
        this.handicapped = view.getHandicapped();
        this.businessActivities = view.getBusinessActivities();
        this.maritalStatus = view.getMaritalStatus();
        this.educationLevel = view.getEducationLevel();
        this.numChildren = view.getNumChildren();
        this.gender = view.getGender();
        this.dateStarted = null;
        this.handicappedDetails = null;
        this.povertyStatus = view.getPovertyStatus();
        this.povertyLikelihoodPercent = null;
    }

    protected ClientDetailEntity() {
        super();
        this.customerId = null;
        this.client = null;
        this.ethinicity = null;
        this.citizenship = null;
        this.handicapped = null;
        this.businessActivities = null;
        this.maritalStatus = null;
        this.educationLevel = null;
        this.numChildren = null;
        this.gender = null;
        this.dateStarted = null;
        this.handicappedDetails = null;
        this.povertyLikelihoodPercent = null;
    }

    public Integer getEthinicity() {
        return this.ethinicity;
    }

    public void setEthinicity(Integer ethinicity) {
        this.ethinicity = ethinicity;
    }

    public Integer getCitizenship() {
        return this.citizenship;
    }

    public void setCitizenship(Integer citizenship) {
        this.citizenship = citizenship;
    }

    public Integer getHandicapped() {
        return this.handicapped;
    }

    public void setHandicapped(Integer handicapped) {
        this.handicapped = handicapped;
    }

    public Integer getBusinessActivities() {
        return this.businessActivities;
    }

    public void setBusinessActivities(Integer businessActivities) {
        this.businessActivities = businessActivities;
    }

    public Integer getMaritalStatus() {
        return this.maritalStatus;
    }

    public void setMaritalStatus(Integer maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public Integer getEducationLevel() {
        return this.educationLevel;
    }

    public void setEducationLevel(Integer educationLevel) {
        this.educationLevel = educationLevel;
    }

    public Short getNumChildren() {
        return this.numChildren;
    }

    public void setNumChildren(Short numChildren) {
        this.numChildren = numChildren;
    }

    public Short getGender() {
        return this.gender;
    }

    public void setGender(Short gender) {
        this.gender = gender;
    }

    public java.util.Date getDateStarted() {
        return this.dateStarted;
    }

    public void setDateStarted(java.util.Date dateStarted) {
        this.dateStarted = dateStarted;
    }

    public String getHandicappedDetails() {
        return this.handicappedDetails;
    }

    public void setHandicappedDetails(String handicappedDetails) {
        this.handicappedDetails = handicappedDetails;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public CustomerBO getClient() {
        return client;
    }

    public Short getPovertyStatus() {
        return povertyStatus;
    }

    public void setPovertyStatus(Short povertyStatus) {
        this.povertyStatus = povertyStatus;
    }

    public Double getPovertyLikelihoodPercent() {
        return this.povertyLikelihoodPercent;
    }

    public void setPovertyLikelihoodPercent(Double povertyLikelihoodPercent) {
        this.povertyLikelihoodPercent = povertyLikelihoodPercent;
    }

    public void updateClientDetails(ClientDetailView view) {
        this.ethinicity = view.getEthinicity();
        this.citizenship = view.getCitizenship();
        this.handicapped = view.getHandicapped();
        this.businessActivities = view.getBusinessActivities();
        this.maritalStatus = view.getMaritalStatus();
        this.educationLevel = view.getEducationLevel();
        this.numChildren = view.getNumChildren();
        this.gender = view.getGender();
        this.povertyStatus = view.getPovertyStatus();
        this.dateStarted = null;
        this.handicappedDetails = null;
    }

    public ClientDetailView toDto() {
        return new ClientDetailView(this.ethinicity, this.citizenship, this.handicapped, this.businessActivities,
                this.maritalStatus, this.educationLevel, this.numChildren, this.gender, this.povertyStatus);
    }
}