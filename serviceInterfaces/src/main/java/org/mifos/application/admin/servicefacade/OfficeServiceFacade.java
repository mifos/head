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

import org.mifos.dto.domain.OfficeDetailsDto;
import org.mifos.dto.domain.OfficeDto;
import org.mifos.dto.domain.OfficeHierarchyDto;
import org.mifos.dto.domain.OfficeUpdateRequest;
import org.mifos.dto.screen.ListElement;
import org.mifos.dto.screen.OfficeDetailsForEdit;
import org.mifos.dto.screen.OfficeFormDto;
import org.mifos.dto.screen.OfficeHierarchyByLevelDto;
import org.mifos.dto.screen.OnlyBranchOfficeHierarchyDto;
import org.mifos.framework.exceptions.ApplicationException;

public interface OfficeServiceFacade {

    OfficeHierarchyDto headOfficeHierarchy();

    OfficeHierarchyByLevelDto retrieveAllOffices();

    OfficeFormDto retrieveOfficeFormInformation(Short officeLevelId);

    OfficeDto retrieveOfficeById(Short id);

    ListElement createOffice(Short operationMode, OfficeDto officeDto);

    OnlyBranchOfficeHierarchyDto retrieveBranchOnlyOfficeHierarchy();

    List<OfficeDto> retrieveAllNonBranchOfficesApplicableToLoggedInUser();

    List<OfficeDetailsDto> retrieveActiveParentOffices(Short officeLevelId);

    OfficeDetailsForEdit retrieveOfficeDetailsForEdit(String officeLevel);

    boolean updateOffice(Short officeId, Integer versionNum, OfficeUpdateRequest officeUpdateRequest);
    
    List<OfficeDto> retrieveActiveBranchesUnderUser(Short userId) throws ApplicationException;
}
