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

@SuppressWarnings("PMD.TooManyFields") // is not applicable for data forms
public class ConfigurableLookupLabelDto {

    private String client;
    private String group;
    private String center;

    private String loans;
    private String savings;

    private String city;
    private String state;
    private String postalCode;
    private String ethnicity;
    private String citizenship;
    private String handicapped;
    private String govtId;

    private String address1;
    private String address2;
    private String address3;

    private String interest;
    private String externalId;
    private String bulkEntry;

    public String getClient() {
        return this.client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getGroup() {
        return this.group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getCenter() {
        return this.center;
    }

    public void setCenter(String center) {
        this.center = center;
    }

    public String getLoans() {
        return this.loans;
    }

    public void setLoans(String loans) {
        this.loans = loans;
    }

    public String getSavings() {
        return this.savings;
    }

    public void setSavings(String savings) {
        this.savings = savings;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return this.postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getEthnicity() {
        return this.ethnicity;
    }

    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }

    public String getCitizenship() {
        return this.citizenship;
    }

    public void setCitizenship(String citizenship) {
        this.citizenship = citizenship;
    }

    public String getHandicapped() {
        return this.handicapped;
    }

    public void setHandicapped(String handicapped) {
        this.handicapped = handicapped;
    }

    public String getGovtId() {
        return this.govtId;
    }

    public void setGovtId(String govtId) {
        this.govtId = govtId;
    }

    public String getAddress1() {
        return this.address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return this.address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress3() {
        return this.address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public String getInterest() {
        return this.interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getExternalId() {
        return this.externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getBulkEntry() {
        return this.bulkEntry;
    }

    public void setBulkEntry(String bulkEntry) {
        this.bulkEntry = bulkEntry;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}