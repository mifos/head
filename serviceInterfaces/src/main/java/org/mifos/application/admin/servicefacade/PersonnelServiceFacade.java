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

package org.mifos.application.admin.servicefacade;

import java.util.List;
import java.util.Locale;

import org.joda.time.DateTime;
import org.mifos.dto.domain.AddressDto;
import org.mifos.dto.domain.CreateOrUpdatePersonnelInformation;
import org.mifos.dto.domain.CustomerHierarchyDto;
import org.mifos.dto.domain.UserDetailDto;
import org.mifos.dto.domain.UserSearchDto;
import org.mifos.dto.domain.ValueListElement;
import org.mifos.dto.screen.DefinePersonnelDto;
import org.mifos.dto.screen.PersonnelInformationDto;
import org.mifos.dto.screen.SystemUserSearchResultsDto;
import org.mifos.dto.screen.UserSettingsDto;
import org.mifos.framework.business.util.Name;
import org.springframework.security.access.prepost.PreAuthorize;

public interface PersonnelServiceFacade {

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CREATE_USER')")
    DefinePersonnelDto retrieveInfoForNewUserDefinition(Short officeId);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CREATE_USER')")
    UserDetailDto createPersonnelInformation(CreateOrUpdatePersonnelInformation personnel);

    @PreAuthorize("isFullyAuthenticated()")
    PersonnelInformationDto getPersonnelInformationDto(Long userId, String globalPersonnelNum);

    @PreAuthorize("isFullyAuthenticated()")
    SystemUserSearchResultsDto searchUser(UserSearchDto searchDto);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_MODIFY_USER')")
    UserDetailDto updatePersonnel(CreateOrUpdatePersonnelInformation personnel);

    @PreAuthorize("isFullyAuthenticated()")
    UserSettingsDto retrieveUserSettings();

    @PreAuthorize("isFullyAuthenticated()")
    UserSettingsDto retrieveUserSettings(Integer genderValue, Integer maritalStatusValue, Integer prefeeredLocaleId);

    @PreAuthorize("isFullyAuthenticated()")
    UserDetailDto retrieveUserInformation(Short personnelId);

    @PreAuthorize("isFullyAuthenticated()")
    void updateUserSettings(Short personnelId, String emailId, Name name, Integer maritalStatusId, Integer genderId,
            AddressDto address, Short preferredLocaleId);

    @PreAuthorize("isFullyAuthenticated()")
    void unLockUserAccount(String globalAccountNum);

    @PreAuthorize("isFullyAuthenticated()")
    Short changeUserLocale(Short id);

    Locale getUserPreferredLocale();

    List<ValueListElement> getDisplayLocaleList();

    @PreAuthorize("isFullyAuthenticated()")
    CustomerHierarchyDto getLoanOfficerCustomersHierarchyForDay(Short loanOfficerId, DateTime day);
}