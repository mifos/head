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

package org.mifos.customers.surveys.business;

import java.util.Date;
import java.util.Set;

import org.mifos.accounts.business.AccountBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.surveys.helpers.InstanceStatus;
import org.mifos.framework.business.AbstractEntity;

public class SurveyInstance extends AbstractEntity {

    private int instanceId;

    private Survey survey;

    private Set<SurveyResponse> surveyResponses;

    private CustomerBO customer;

    private AccountBO account;

    private PersonnelBO officer;

    private PersonnelBO creator;

    private Date dateConducted;

    private InstanceStatus completedStatus;

    public SurveyInstance() {
        completedStatus = InstanceStatus.INCOMPLETE;
    }

    public int getCompletedStatus() {
        return completedStatus.getValue();
    }

    public InstanceStatus getCompletedStatusAsEnum() {
        return completedStatus;
    }

    public void setCompletedStatus(int completedStatus) {
        this.completedStatus = InstanceStatus.fromInt(completedStatus);
    }

    public void setCompletedStatus(InstanceStatus completedStatus) {
        this.completedStatus = completedStatus;
    }

    public Date getDateConducted() {
        return dateConducted;
    }

    public void setDateConducted(Date dateConducted) {
        this.dateConducted = dateConducted;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(int instanceId) {
        this.instanceId = instanceId;
    }

    public PersonnelBO getOfficer() {
        return officer;
    }

    public void setOfficer(PersonnelBO officer) {
        this.officer = officer;
    }

    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    public CustomerBO getCustomer() {
        return customer;
    }

    /*
     * note that a survey instance must be associated with either a client or an
     * account, not both... we could include a check against the survey type
     * here, but that would cause needless errors when you set the
     * client/account before the survey
     */
    public void setCustomer(CustomerBO customer) {
        this.customer = customer;
        this.account = null;
    }

    public AccountBO getAccount() {
        return account;
    }

    public void setAccount(AccountBO account) {
        this.account = account;
        this.customer = null;
    }

    public PersonnelBO getCreator() {
        return creator;
    }

    public void setCreator(PersonnelBO creator) {
        this.creator = creator;
    }

    public void setSurveyResponses(Set<SurveyResponse> surveyResponses) {
        this.surveyResponses = surveyResponses;
    }

    public Set<SurveyResponse> getSurveyResponses() {
        return surveyResponses;
    }
}
