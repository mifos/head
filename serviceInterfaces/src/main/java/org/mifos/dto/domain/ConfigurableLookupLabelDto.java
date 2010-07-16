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

public class ConfigurableLookupLabelDto {

    private String clientKey;
    private String groupKey;
    private String centerKey;
    private String loansKey;
    private String savingsKey;
    private String stateKey;
    private String postalCodeKey;

    private String ethnicityKey;
    private String citizenshipKey;
    private String handicappedKey;
    private String govtIdKey;
    private String address1Key;
    private String address2Key;
    private String address3Key;

    private String interestKey;
    private String externalIdKey;
    private String bulkEntryKey;

    // private String partialApplication;
    // private String pendingApproval;
    //
    // private String approved;
    // private String cancel;
    // private String closed;
    // private String onhold;
    //
    // private String active;
    // private String inActive;
    //
    // private String activeInGoodStanding;
    // private String activeInBadStanding;
    // private String closedObligationMet;
    // private String closedRescheduled;
    // private String closedWrittenOff;
    //

    public String getClientKey() {
        return this.clientKey;
    }

    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }

    public String getGroupKey() {
        return this.groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    public String getCenterKey() {
        return this.centerKey;
    }

    public void setCenterKey(String centerKey) {
        this.centerKey = centerKey;
    }

    public String getLoansKey() {
        return this.loansKey;
    }

    public void setLoansKey(String loansKey) {
        this.loansKey = loansKey;
    }

    public String getSavingsKey() {
        return this.savingsKey;
    }

    public void setSavingsKey(String savingsKey) {
        this.savingsKey = savingsKey;
    }

    public String getStateKey() {
        return this.stateKey;
    }

    public void setStateKey(String stateKey) {
        this.stateKey = stateKey;
    }

    public String getPostalCodeKey() {
        return this.postalCodeKey;
    }

    public void setPostalCodeKey(String postalCodeKey) {
        this.postalCodeKey = postalCodeKey;
    }

    public String getEthnicityKey() {
        return this.ethnicityKey;
    }

    public void setEthnicityKey(String ethnicityKey) {
        this.ethnicityKey = ethnicityKey;
    }

    public String getCitizenshipKey() {
        return this.citizenshipKey;
    }

    public void setCitizenshipKey(String citizenshipKey) {
        this.citizenshipKey = citizenshipKey;
    }

    public String getHandicappedKey() {
        return this.handicappedKey;
    }

    public void setHandicappedKey(String handicappedKey) {
        this.handicappedKey = handicappedKey;
    }

    public String getGovtIdKey() {
        return this.govtIdKey;
    }

    public void setGovtIdKey(String govtIdKey) {
        this.govtIdKey = govtIdKey;
    }

    public String getAddress1Key() {
        return this.address1Key;
    }

    public void setAddress1Key(String address1Key) {
        this.address1Key = address1Key;
    }

    public String getAddress2Key() {
        return this.address2Key;
    }

    public void setAddress2Key(String address2Key) {
        this.address2Key = address2Key;
    }

    public String getAddress3Key() {
        return this.address3Key;
    }

    public void setAddress3Key(String address3Key) {
        this.address3Key = address3Key;
    }

    public String getExternalIdKey() {
        return this.externalIdKey;
    }

    public void setExternalIdKey(String externalIdKey) {
        this.externalIdKey = externalIdKey;
    }

    public String getBulkEntryKey() {
        return this.bulkEntryKey;
    }

    public void setBulkEntryKey(String bulkEntryKey) {
        this.bulkEntryKey = bulkEntryKey;
    }

    public String getInterestKey() {
        return this.interestKey;
    }

    public void setInterestKey(String interestKey) {
        this.interestKey = interestKey;
    }
}
