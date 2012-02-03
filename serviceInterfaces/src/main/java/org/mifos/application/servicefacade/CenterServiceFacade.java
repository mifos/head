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

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mifos.dto.domain.AuditLogDto;
import org.mifos.dto.domain.CenterCreation;
import org.mifos.dto.domain.CenterCreationDetail;
import org.mifos.dto.domain.CenterDto;
import org.mifos.dto.domain.CenterInformationDto;
import org.mifos.dto.domain.CenterUpdate;
import org.mifos.dto.domain.CustomerChargesDetailsDto;
import org.mifos.dto.domain.CustomerDetailDto;
import org.mifos.dto.domain.CustomerDetailsDto;
import org.mifos.dto.domain.MeetingDto;
import org.mifos.dto.domain.UserDetailDto;
import org.mifos.dto.screen.CenterFormCreationDto;
import org.mifos.dto.screen.ClosedAccountDto;
import org.mifos.dto.screen.CustomerNoteFormDto;
import org.mifos.dto.screen.CustomerRecentActivityDto;
import org.mifos.dto.screen.CustomerStatusDetailDto;
import org.mifos.dto.screen.TransactionHistoryDto;
import org.springframework.security.access.prepost.PreAuthorize;

public interface CenterServiceFacade {

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_CREATE_NEW_CENTER')")
    CenterFormCreationDto retrieveCenterFormCreationData(CenterCreation centerCreation);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_CREATE_NEW_CENTER')")
    CustomerDetailsDto createNewCenter(CenterCreationDetail centerCreationDetail, MeetingDto meeting);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_MODIFY_CENTER_INFORMATION_AND_CHANGE_CENTER_STATUS')")
    CenterDto retrieveCenterDetailsForUpdate(Integer centerId);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_MODIFY_CENTER_INFORMATION_AND_CHANGE_CENTER_STATUS')")
    void updateCenter(CenterUpdate centerUpdate);

    @PreAuthorize("isFullyAuthenticated()")
    CenterInformationDto getCenterInformationDto(String globalCustNum);

    @PreAuthorize("isFullyAuthenticated()")
    void initializeCenterStates(String centerGlobalNum);

    @PreAuthorize("isFullyAuthenticated()")
    List<AuditLogDto> retrieveChangeLogs(Integer centerId);

    // General
    @PreAuthorize("isFullyAuthenticated()")
    CustomerStatusDetailDto retrieveCustomerStatusDetails(Short newStatusId, Short flagIdValue, Short value);

    @PreAuthorize("isFullyAuthenticated()")
    CustomerChargesDetailsDto retrieveChargesDetails(Integer customerId);

    @PreAuthorize("isFullyAuthenticated()")
    List<CustomerRecentActivityDto> retrieveRecentActivities(Integer customerId, Integer countOfActivities);

    @PreAuthorize("isFullyAuthenticated()")
    void updateCustomerStatus(Integer customerId, Integer versionNo, String flagId, String newStatusId, String notes);

    // ALL BELOW ARE NOT FOR CENTER
    @PreAuthorize("isFullyAuthenticated()")
    void initializeGroupStates(String globalCustNum);

    @PreAuthorize("isFullyAuthenticated()")
    void initializeClientStates(String globalCustNum);

    @PreAuthorize("isFullyAuthenticated()")
    CustomerNoteFormDto retrieveCustomerNote(String globalCustNum);

    @PreAuthorize("isFullyAuthenticated()")
    void createCustomerNote(CustomerNoteFormDto customerNoteForm);

    @PreAuthorize("isFullyAuthenticated()")
    List<ClosedAccountDto> retrieveAllClosedAccounts(Integer customerId);

    @PreAuthorize("isFullyAuthenticated()")
    List<TransactionHistoryDto> retrieveAccountTransactionHistory(String globalAccountNum);

    @PreAuthorize("isFullyAuthenticated()")
    List<CustomerRecentActivityDto> retrieveAllAccountActivity(String globalCustNum);

    @PreAuthorize("isFullyAuthenticated()")
    void waiveChargesDue(Integer accountId, Integer waiveType);

    @PreAuthorize("isFullyAuthenticated()")
    void waiveChargesOverDue(Integer accountId, Integer waiveType);

    @PreAuthorize("isFullyAuthenticated()")
    void removeAccountFee(Integer accountId, Short feeId);

    @PreAuthorize("isFullyAuthenticated()")
    void revertLastChargesPayment(String globalCustNum, String adjustmentNote);

    @PreAuthorize("isFullyAuthenticated()")
    List<CustomerDetailDto> retrieveCustomersUnderUser(Short loanOfficerId);

    @PreAuthorize("isFullyAuthenticated()")
    String retrieveOfficeName(Short officeId);

    @PreAuthorize("isFullyAuthenticated()")
    UserDetailDto retrieveUsersDetails(Short userId);

    @PreAuthorize("isFullyAuthenticated()")
    void addNoteToPersonnel(Short personnelId, String comment);
    
    void putCenterBusinessKeyInSession(String globalCustNum, HttpServletRequest request);
}