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

package org.mifos.application.servicefacade;

import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.mifos.dto.domain.ClientCreationDetail;
import org.mifos.dto.domain.ClientFamilyDetailsDto;
import org.mifos.dto.domain.ClientFamilyInfoUpdate;
import org.mifos.dto.domain.ClientMfiInfoUpdate;
import org.mifos.dto.domain.ClientPersonalInfoUpdate;
import org.mifos.dto.domain.ClientRulesDto;
import org.mifos.dto.domain.CustomerDetailsDto;
import org.mifos.dto.domain.MeetingDto;
import org.mifos.dto.domain.ProcessRulesDto;
import org.mifos.dto.domain.SavingsDetailDto;
import org.mifos.dto.screen.ClientFamilyInfoDto;
import org.mifos.dto.screen.ClientFormCreationDto;
import org.mifos.dto.screen.ClientInformationDto;
import org.mifos.dto.screen.ClientMfiInfoDto;
import org.mifos.dto.screen.ClientPersonalInfoDto;
import org.mifos.dto.screen.ClientPhotoDto;
import org.mifos.dto.screen.ClientRemovalFromGroupDto;
import org.mifos.dto.screen.UploadedFileDto;
import org.springframework.security.access.prepost.PreAuthorize;

public interface ClientServiceFacade {

    @PreAuthorize("isFullyAuthenticated() and hasAnyRole('ROLE_CREATE_CLIENT_IN_SAVE_FOR_LATER_STATE', 'ROLE_CREATE_CLIENT_IN_SUBMIT_FOR_APPROVAL_STATE')")
    ClientFormCreationDto retrieveClientFormCreationData(Short groupFlag, Short officeId, String parentGroupId);

    @PreAuthorize("isFullyAuthenticated()")
    ClientFamilyDetailsDto retrieveClientFamilyDetails();

    @PreAuthorize("isFullyAuthenticated()")
    ProcessRulesDto previewClient(String governmentId, DateTime dateOfBirth, String clientName, boolean defaultFeesRemoval, Short officeId, Short loanOfficerId);

    @PreAuthorize("isFullyAuthenticated() and hasAnyRole('ROLE_CREATE_CLIENT_IN_SAVE_FOR_LATER_STATE', 'ROLE_CREATE_CLIENT_IN_SUBMIT_FOR_APPROVAL_STATE')")
    CustomerDetailsDto createNewClient(ClientCreationDetail clientCreationDetail, MeetingDto meeting, List<SavingsDetailDto> allowedSavingProducts);

    @PreAuthorize("isFullyAuthenticated()")
    ClientInformationDto getClientInformationDto(String globalCustNum);

    @PreAuthorize("isFullyAuthenticated() and (hasRole('ROLE_EDIT_CLIENT_PERSONAL_INFO') or (hasRole('ROLE_CREATE_CLIENT_IN_SAVE_FOR_LATER_STATE') and hasPermission(#clientStatus, 'CLIENT_STATUS') and hasPermission(#loanOfficerId, 'LOAN_OFFICER_ID')))")
    ClientPersonalInfoDto retrieveClientPersonalInfoForUpdate(String clientSystemId, @SuppressWarnings("PMD") String clientStatus, @SuppressWarnings("PMD") short loanOfficerId);

    @PreAuthorize("isFullyAuthenticated()")
    ClientPhotoDto getClientPhoto(Long clientId);

    @PreAuthorize("isFullyAuthenticated()")
    ClientRulesDto retrieveClientDetailsForPreviewingEditOfPersonalInfo();

    @PreAuthorize("isFullyAuthenticated() and (hasRole('ROLE_EDIT_CLIENT_PERSONAL_INFO') or (hasRole('ROLE_CREATE_CLIENT_IN_SAVE_FOR_LATER_STATE') and hasPermission(#clientStatus, 'CLIENT_STATUS') and hasPermission(#loanOfficerId, 'LOAN_OFFICER_ID')))")
    void updateClientPersonalInfo(ClientPersonalInfoUpdate personalInfo, @SuppressWarnings("PMD") String clientStatus, @SuppressWarnings("PMD") short loanOfficerId);
    
    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_UNBLACKLIST_CLIENT') and hasRole('ROLE_CHANGE_CLIENT_ACTIVE_STATE')")
    void removeFromBlacklist(Integer customerId);

    @PreAuthorize("isFullyAuthenticated()")
    void uploadFile(Integer clientId, InputStream in, UploadedFileDto uploadedFileDto);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_EDIT_CLIENT_PERSONAL_INFO')")
    ClientFamilyInfoDto retrieveFamilyInfoForEdit(String globalCustNum);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_EDIT_CLIENT_PERSONAL_INFO')")
    void updateFamilyInfo(ClientFamilyInfoUpdate clientFamilyInfoUpdate);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_EDIT_CLIENT_MFI_INFO')")
    ClientMfiInfoDto retrieveMfiInfoForEdit(String clientSystemId);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_EDIT_CLIENT_MFI_INFO')")
    void updateClientMfiInfo(ClientMfiInfoUpdate clientMfiInfoUpdate);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_REMOVE_CLIENT_FROM_GROUP')")
    ClientRemovalFromGroupDto retreiveClientDetailsForRemovalFromGroup(String globalCustNum);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_REMOVE_CLIENT_FROM_GROUP')")
    void removeGroupMembership(String globalCustNum, Short loanOfficerId, String comment);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_UPDATE_GROUP_MEMBERSHIP_OF_CLIENT')")
    String transferClientToGroup(Integer parentGroupIdValue, String clientGlobalCustNum, Integer previousClientVersionNo);

    @PreAuthorize("isFullyAuthenticated()")
    List<SavingsDetailDto> retrieveSavingsInUseForClient(Integer clientId);

    String transferClientToBranch(String globalCustNum, Short officeId);
    
    void putClientBusinessKeyInSession(String globalCustNum, HttpServletRequest request);
}