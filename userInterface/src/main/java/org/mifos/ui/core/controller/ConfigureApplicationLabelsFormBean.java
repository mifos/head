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

package org.mifos.ui.core.controller;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value="EQ_UNUSUAL", justification="using commons equals builder")
public class ConfigureApplicationLabelsFormBean {

    private String headOffice;
    private String regionalOffice;
    private String subRegionalOffice;
    private String areaOffice;
    private String branchOffice;
    private String client;
    private String group;
    private String center;
    private String loans;
    private String savings;
    private String state;
    private String postalCode;
    private String ethnicity;
    private String citizenship;
    private String handicapped;
    private String govtId;
    private String address1;
    private String address2;
    private String address3;
    private String partialApplication;
    private String pendingApproval;
    private String approved;
    private String cancel;
    private String closed;
    private String onhold;
    private String active;
    private String inActive;
    private String activeInGoodStanding;
    private String activeInBadStanding;
    private String closedObligationMet;
    private String closedRescheduled;
    private String closedWrittenOff;
    private String none;
    private String graceOnAllRepayments;
    private String principalOnlyGrace;
    private String interest;
    private String externalId;
    private String bulkEntry;

    public String getHeadOffice() {
        return this.headOffice;
    }

    public void setHeadOffice(String headOffice) {
        this.headOffice = headOffice;
    }

    public String getRegionalOffice() {
        return this.regionalOffice;
    }

    public void setRegionalOffice(String regionalOffice) {
        this.regionalOffice = regionalOffice;
    }

    public String getSubRegionalOffice() {
        return this.subRegionalOffice;
    }

    public void setSubRegionalOffice(String subRegionalOffice) {
        this.subRegionalOffice = subRegionalOffice;
    }

    public String getAreaOffice() {
        return this.areaOffice;
    }

    public void setAreaOffice(String areaOffice) {
        this.areaOffice = areaOffice;
    }

    public String getBranchOffice() {
        return this.branchOffice;
    }

    public void setBranchOffice(String branchOffice) {
        this.branchOffice = branchOffice;
    }

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

    public String getPartialApplication() {
        return this.partialApplication;
    }

    public void setPartialApplication(String partialApplication) {
        this.partialApplication = partialApplication;
    }

    public String getPendingApproval() {
        return this.pendingApproval;
    }

    public void setPendingApproval(String pendingApproval) {
        this.pendingApproval = pendingApproval;
    }

    public String getApproved() {
        return this.approved;
    }

    public void setApproved(String approved) {
        this.approved = approved;
    }

    public String getCancel() {
        return this.cancel;
    }

    public void setCancel(String cancel) {
        this.cancel = cancel;
    }

    public String getClosed() {
        return this.closed;
    }

    public void setClosed(String closed) {
        this.closed = closed;
    }

    public String getOnhold() {
        return this.onhold;
    }

    public void setOnhold(String onhold) {
        this.onhold = onhold;
    }

    public String getActive() {
        return this.active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getInActive() {
        return this.inActive;
    }

    public void setInActive(String inActive) {
        this.inActive = inActive;
    }

    public String getActiveInGoodStanding() {
        return this.activeInGoodStanding;
    }

    public void setActiveInGoodStanding(String activeInGoodStanding) {
        this.activeInGoodStanding = activeInGoodStanding;
    }

    public String getActiveInBadStanding() {
        return this.activeInBadStanding;
    }

    public void setActiveInBadStanding(String activeInBadStanding) {
        this.activeInBadStanding = activeInBadStanding;
    }

    public String getClosedObligationMet() {
        return this.closedObligationMet;
    }

    public void setClosedObligationMet(String closedObligationMet) {
        this.closedObligationMet = closedObligationMet;
    }

    public String getClosedRescheduled() {
        return this.closedRescheduled;
    }

    public void setClosedRescheduled(String closedRescheduled) {
        this.closedRescheduled = closedRescheduled;
    }

    public String getClosedWrittenOff() {
        return this.closedWrittenOff;
    }

    public void setClosedWrittenOff(String closedWrittenOff) {
        this.closedWrittenOff = closedWrittenOff;
    }

    public String getNone() {
        return this.none;
    }

    public void setNone(String none) {
        this.none = none;
    }

    public String getGraceOnAllRepayments() {
        return this.graceOnAllRepayments;
    }

    public void setGraceOnAllRepayments(String graceOnAllRepayments) {
        this.graceOnAllRepayments = graceOnAllRepayments;
    }

    public String getPrincipalOnlyGrace() {
        return this.principalOnlyGrace;
    }

    public void setPrincipalOnlyGrace(String principalOnlyGrace) {
        this.principalOnlyGrace = principalOnlyGrace;
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

    @Override
    public boolean equals(Object obj) {
        return new EqualsBuilder().reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().reflectionHashCode(this);
    }
}