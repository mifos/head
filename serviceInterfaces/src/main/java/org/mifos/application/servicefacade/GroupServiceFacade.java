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

package org.mifos.application.servicefacade;

import org.mifos.dto.domain.CenterDto;
import org.mifos.dto.domain.CustomerDetailsDto;
import org.mifos.dto.domain.GroupCreation;
import org.mifos.dto.domain.GroupCreationDetail;
import org.mifos.dto.domain.GroupFormCreationDto;
import org.mifos.dto.domain.GroupUpdate;
import org.mifos.dto.domain.MeetingDto;
import org.mifos.dto.screen.CenterHierarchySearchDto;
import org.mifos.dto.screen.GroupInformationDto;
import org.springframework.security.access.prepost.PreAuthorize;

public interface GroupServiceFacade {

    @PreAuthorize("isFullyAuthenticated()")
    CenterHierarchySearchDto isCenterHierarchyConfigured();

    @PreAuthorize("isFullyAuthenticated()")
    GroupFormCreationDto retrieveGroupFormCreationData(GroupCreation groupCreation);

    @PreAuthorize("isFullyAuthenticated()")
    CustomerDetailsDto createNewGroup(GroupCreationDetail groupCenterDetail, MeetingDto meetingDto);

    @PreAuthorize("isFullyAuthenticated()")
    GroupInformationDto getGroupInformationDto(String globalCustNum);

    @PreAuthorize("isFullyAuthenticated()")
    CenterDto retrieveGroupDetailsForUpdate(String globalCustNum);

    @PreAuthorize("isFullyAuthenticated()")
    void updateGroup(GroupUpdate groupUpdate);
}