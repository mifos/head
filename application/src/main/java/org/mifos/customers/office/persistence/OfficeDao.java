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

package org.mifos.customers.office.persistence;

import java.util.Collection;
import java.util.List;

import org.mifos.customers.center.struts.action.OfficeHierarchyDto;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.business.OfficeDetailsDto;
import org.mifos.customers.office.exceptions.OfficeException;
import org.mifos.security.util.UserContext;

public interface OfficeDao {

    OfficeBO findOfficeById(Short officeIdValue);

    OfficeDto findOfficeDtoById(Short valueOf);

    List<OfficeBO> findBranchsOnlyWithParentsMatching(String searchId);

    List<OfficeDetailsDto> findActiveOfficeLevels();

    OfficeHierarchyDto headOfficeHierarchy();

    List<String> topLevelOfficeNames(Collection<Short> idList);

    void validateOfficeNameIsNotTaken(String officeName) throws OfficeException;

    void validateOfficeShortNameIsNotTaken(String shortName) throws OfficeException;

    void validateNoActiveChildrenExist(Short officeId) throws OfficeException;

    void validateNoActivePeronnelExist(Short officeId) throws OfficeException;

    void validateBranchIsActiveWithNoActivePersonnel(Short officeId, UserContext userContext) throws CustomerException;
}
