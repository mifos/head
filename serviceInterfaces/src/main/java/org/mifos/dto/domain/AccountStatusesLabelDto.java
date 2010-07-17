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

public class AccountStatusesLabelDto {

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

}
