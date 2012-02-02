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

import javax.servlet.http.HttpServletRequest;

import org.mifos.dto.domain.CenterDto;
import org.mifos.dto.domain.CustomerDetailDto;
import org.mifos.dto.domain.CustomerDetailsDto;
import org.mifos.dto.domain.CustomerHistoricalDataUpdateRequest;
import org.mifos.dto.domain.GroupCreation;
import org.mifos.dto.domain.GroupCreationDetail;
import org.mifos.dto.domain.GroupFormCreationDto;
import org.mifos.dto.domain.GroupUpdate;
import org.mifos.dto.domain.MeetingDto;
import org.mifos.dto.screen.CenterHierarchySearchDto;
import org.mifos.dto.screen.CustomerHistoricalDataDto;
import org.mifos.dto.screen.GroupInformationDto;
import org.springframework.security.access.prepost.PreAuthorize;

public interface GroupServiceFacade {

    @PreAuthorize("isFullyAuthenticated()")
    CenterHierarchySearchDto isCenterHierarchyConfigured();

    @PreAuthorize("isFullyAuthenticated() and hasAnyRole('ROLE_CREATE_GROUP_IN_SAVE_FOR_LATER_STATE', 'ROLE_CREATE_GROUP_IN_SUBMIT_FOR_APPROVAL_STATE')")
    GroupFormCreationDto retrieveGroupFormCreationData(GroupCreation groupCreation);

    @PreAuthorize("isFullyAuthenticated() and hasAnyRole('ROLE_CREATE_GROUP_IN_SAVE_FOR_LATER_STATE', 'ROLE_CREATE_GROUP_IN_SUBMIT_FOR_APPROVAL_STATE')")
    CustomerDetailsDto createNewGroup(GroupCreationDetail groupCenterDetail, MeetingDto meetingDto);

    @PreAuthorize("isFullyAuthenticated()")
    GroupInformationDto getGroupInformationDto(String globalCustNum);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_EDIT_GROUP')")
    CenterDto retrieveGroupDetailsForUpdate(String globalCustNum);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_EDIT_GROUP')")
    void updateGroup(GroupUpdate groupUpdate);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_CHANGE_CENTER_MEMBERSHIP_OF_GROUP')")
    CustomerDetailDto transferGroupToCenter(String globalCustNum, String centerSystemId, Integer previousGroupVersionNo);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_CHANGE_CENTER_MEMBERSHIP_OF_GROUP')")
    CustomerDetailDto transferGroupToBranch(String globalCustNum, Short officeIdValue, Integer previousGroupVersionNo);

    @PreAuthorize("isFullyAuthenticated()")
    CustomerHistoricalDataDto retrieveCustomerHistoricalData(String globalCustNum);

    @PreAuthorize("isFullyAuthenticated()")
    void updateCustomerHistoricalData(String globalCustNum, CustomerHistoricalDataUpdateRequest historicalData);
    
    void putGroupBusinessKeyInSession(String globalCustNum, HttpServletRequest request);
}