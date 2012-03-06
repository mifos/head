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

import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifos.dto.screen.ClientFamilyDetailDto;
import org.mifos.dto.screen.ClientNameDetailDto;
import org.mifos.dto.screen.ClientPersonalDetailDto;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID", "EI_EXPOSE_REP", "EI_EXPOSE_REP2", "SE_TRANSIENT_FIELD_NOT_RESTORED"}, 
    justification="should disable at filter level and also for pmd - not important for us")
public class ClientCreationDetail implements Serializable{

    private static final long serialVersionUID = 824884718450478093L;
	
    private final List<Short> selectedSavingProducts;
    private final String clientName;
    private final Short clientStatus;
    private final Date mfiJoiningDate;
    private final String externalId;
    private final AddressDto address;
    private final Short formedBy;
    private final Date dateOfBirth;
    private final String governmentId;
    private final boolean trained;
    private final Date trainedDate;
    private final Short groupFlag;
    private final ClientNameDetailDto clientNameDetailDto;
    private final ClientPersonalDetailDto clientPersonalDetailDto;
    private final transient InputStream picture;
    private final List<ApplicableAccountFeeDto> feesToApply;
    private final String parentGroupId;
    private final List<ClientNameDetailDto> familyNames;
    private final List<ClientFamilyDetailDto> familyDetails;
    private final Short loanOfficerId;
    private final Short officeId;
    private final ClientNameDetailDto spouseFatherName;
    private final LocalDate activationDate;

    public ClientCreationDetail(List<Short> selectedSavingProducts, String clientName, Short clientStatus,
            Date mfiJoiningDate, String externalId, AddressDto address, Short formedBy, Date dateOfBirth,
            String governmentId, boolean trained, Date trainedDate, Short groupFlag,
            ClientNameDetailDto clientNameDetailDto, ClientPersonalDetailDto clientPersonalDetailDto,
            ClientNameDetailDto spouseFatherName, InputStream picture, List<ApplicableAccountFeeDto> feesToApply, String parentGroupId,
            List<ClientNameDetailDto> familyNames, List<ClientFamilyDetailDto> familyDetails,
            Short loanOfficerId, Short officeId, LocalDate activationDate) {
        this.selectedSavingProducts = selectedSavingProducts;
        this.clientName = clientName;
        this.clientStatus = clientStatus;
        this.mfiJoiningDate = mfiJoiningDate;
        this.externalId = externalId;
        this.address = address;
        this.formedBy = formedBy;
        this.dateOfBirth = dateOfBirth;
        this.governmentId = governmentId;
        this.trained = trained;
        this.trainedDate = trainedDate;
        this.groupFlag = groupFlag;
        this.clientNameDetailDto = clientNameDetailDto;
        this.clientPersonalDetailDto = clientPersonalDetailDto;
        this.spouseFatherName = spouseFatherName;
        this.picture = picture;
        this.feesToApply = feesToApply;
        this.parentGroupId = parentGroupId;
        this.familyNames = familyNames;
        this.familyDetails = familyDetails;
        this.loanOfficerId = loanOfficerId;
        this.officeId = officeId;
        this.activationDate = activationDate;
    }

    public List<Short> getSelectedSavingProducts() {
        return this.selectedSavingProducts;
    }

    public String getClientName() {
        return this.clientName;
    }

    public Short getClientStatus() {
        return this.clientStatus;
    }

    public Date getMfiJoiningDate() {
        return this.mfiJoiningDate;
    }

    public String getExternalId() {
        return this.externalId;
    }

    public AddressDto getAddress() {
        return this.address;
    }

    public Short getFormedBy() {
        return this.formedBy;
    }

    public Date getDateOfBirth() {
        return this.dateOfBirth;
    }

    public String getGovernmentId() {
        return this.governmentId;
    }

    public boolean isTrained() {
        return this.trained;
    }

    public Date getTrainedDate() {
        return this.trainedDate;
    }

    public Short getGroupFlag() {
        return this.groupFlag;
    }

    public ClientNameDetailDto getClientNameDetailDto() {
        return this.clientNameDetailDto;
    }

    public ClientPersonalDetailDto getClientPersonalDetailDto() {
        return this.clientPersonalDetailDto;
    }

    public InputStream getPicture() {
        return this.picture;
    }

    public List<ApplicableAccountFeeDto> getFeesToApply() {
        return this.feesToApply;
    }

    public String getParentGroupId() {
        return this.parentGroupId;
    }

    public List<ClientNameDetailDto> getFamilyNames() {
        return this.familyNames;
    }

    public List<ClientFamilyDetailDto> getFamilyDetails() {
        return this.familyDetails;
    }

    public Short getLoanOfficerId() {
        return this.loanOfficerId;
    }

    public Short getOfficeId() {
        return this.officeId;
    }

    public ClientNameDetailDto getSpouseFatherName() {
        return this.spouseFatherName;
    }

    public LocalDate getActivationDate() {
        return this.activationDate;
    }
}