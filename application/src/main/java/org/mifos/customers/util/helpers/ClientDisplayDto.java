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
import java.util.List;

import org.mifos.framework.business.service.DataTransferObject;
import org.mifos.framework.util.helpers.DateUtils;

/**
 *
 */
public class ClientDisplayDto implements DataTransferObject {

    private final Integer customerId;
    private final String globalCustNum;
    private final String displayName;
    private final String parentCustomerDisplayName;
    private final String branchName;
    private final String externalId;
    private final String customerFormedByDisplayName;
    private final Date customerActivationDate;
    private final Short customerLevelId;
    private final Short customerStatusId;
    private final String customerStatusName;
    private final Date trainedDate;
    private final Date dateOfBirth;
    private final Integer age;
    private final String governmentId;
    private final Boolean clientUnderGroup;
    private final Boolean blackListed;
    private final Short loanOfficerId;
    private final String loanOfficerName;
    private final String businessActivities;
    private final String handicapped;
    private final String maritalStatus;
    private final String citizenship;
    private final String ethnicity;
    private final String educationLevel;
    private final String povertyStatus;
    private final Short numChildren;
    private final Boolean isCustomerPicture;
    private final Boolean areFamilyDetailsRequired;

    // if areFamilyDetailsRequired = false
    private final String spouseFatherValue;
    private final String spouseFatherName;

    // if areFamilyDetailsRequired = true
    private final List<ClientFamilyDetailDto> familyDetails;

    public ClientDisplayDto(final Integer customerId, final String globalCustNum, final String displayName,
            final String parentCustomerDisplayName, final String branchName, final String externalId,
            final String customerFormedByDisplayName, final Date customerActivationDate, final Short customerLevelId,
            final Short customerStatusId, final String customerStatusName, final Date trainedDate,
            final Date dateOfBirth, final String governmentId, final Boolean clientUnderGroup,
            final Boolean blackListed, final Short loanOfficerId, final String loanOfficerName,
            final String businessActivities, final String handicapped, final String maritalStatus,
            final String citizenship, final String ethnicity, final String educationLevel, final String povertyStatus,
            final Short numChildren, final Boolean isCustomerPicture, final Boolean areFamilyDetailsRequired,
            final String spouseFatherValue, final String spouseFatherName,
            final List<ClientFamilyDetailDto> familyDetails) {

        this.customerId = customerId;
        this.globalCustNum = globalCustNum;
        this.displayName = displayName;
        this.parentCustomerDisplayName = parentCustomerDisplayName;
        this.branchName = branchName;
        this.externalId = externalId;
        this.customerFormedByDisplayName = customerFormedByDisplayName;
        this.customerActivationDate = customerActivationDate;
        this.customerLevelId = customerLevelId;
        this.customerStatusId = customerStatusId;
        this.customerStatusName = customerStatusName;
        this.trainedDate = trainedDate;
        this.dateOfBirth = dateOfBirth;
        if (dateOfBirth == null) {
            this.age = null;
        } else {
            this.age = DateUtils.DateDiffInYears(new java.sql.Date(dateOfBirth.getTime()));
        }
        this.governmentId = governmentId;
        this.clientUnderGroup = clientUnderGroup;
        this.blackListed = blackListed;
        this.loanOfficerId = loanOfficerId;
        this.loanOfficerName = loanOfficerName;
        this.businessActivities = businessActivities;
        this.handicapped = handicapped;
        this.maritalStatus = maritalStatus;
        this.citizenship = citizenship;
        this.ethnicity = ethnicity;
        this.educationLevel = educationLevel;
        this.povertyStatus = povertyStatus;
        this.numChildren = numChildren;
        this.isCustomerPicture = isCustomerPicture;
        this.areFamilyDetailsRequired = areFamilyDetailsRequired;

        this.spouseFatherValue = spouseFatherValue;
        this.spouseFatherName = spouseFatherName;

        this.familyDetails = familyDetails;
    }

    public Integer getCustomerId() {
        return this.customerId;
    }

    public String getGlobalCustNum() {
        return this.globalCustNum;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getParentCustomerDisplayName() {
        return this.parentCustomerDisplayName;
    }

    public String getBranchName() {
        return this.branchName;
    }

    public String getExternalId() {
        return this.externalId;
    }

    public String getCustomerFormedByDisplayName() {
        return this.customerFormedByDisplayName;
    }

    public Date getCustomerActivationDate() {
        return this.customerActivationDate;
    }

    public Short getCustomerLevelId() {
        return this.customerLevelId;
    }

    public Short getCustomerStatusId() {
        return this.customerStatusId;
    }

    public String getCustomerStatusName() {
        return this.customerStatusName;
    }

    public Date getTrainedDate() {
        return this.trainedDate;
    }

    public Date getDateOfBirth() {
        return this.dateOfBirth;
    }

    public Integer getAge() {
        return this.age;
    }

    public String getGovernmentId() {
        return this.governmentId;
    }

    public Boolean getClientUnderGroup() {
        return this.clientUnderGroup;
    }

    public Boolean getBlackListed() {
        return this.blackListed;
    }

    public Short getLoanOfficerId() {
        return this.loanOfficerId;
    }

    public String getLoanOfficerName() {
        return this.loanOfficerName;
    }

    public String getBusinessActivities() {
        return this.businessActivities;
    }

    public String getHandicapped() {
        return this.handicapped;
    }

    public String getMaritalStatus() {
        return this.maritalStatus;
    }

    public String getCitizenship() {
        return this.citizenship;
    }

    public String getEthnicity() {
        return this.ethnicity;
    }

    public String getEducationLevel() {
        return this.educationLevel;
    }

    public String getPovertyStatus() {
        return this.povertyStatus;
    }

    public Short getNumChildren() {
        return this.numChildren;
    }

    public Boolean getIsCustomerPicture() {
        return this.isCustomerPicture;
    }

    public Boolean getAreFamilyDetailsRequired() {
        return this.areFamilyDetailsRequired;
    }

    public String getSpouseFatherValue() {
        return this.spouseFatherValue;
    }

    public String getSpouseFatherName() {
        return this.spouseFatherName;
    }

    public List<ClientFamilyDetailDto> getFamilyDetails() {
        return this.familyDetails;
    }

}
