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

import java.util.List;

import org.mifos.accounts.servicefacade.UserContextFactory;
import org.mifos.config.ClientRules;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.business.service.CustomerService;
import org.mifos.customers.center.util.helpers.CenterConstants;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.service.GroupBusinessService;
import org.mifos.customers.group.struts.action.GroupSearchResultsDto;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.dto.domain.OfficeDetailsDto;
import org.mifos.dto.domain.OfficeDto;
import org.mifos.dto.domain.OfficeHierarchyDto;
import org.mifos.dto.screen.OnlyBranchOfficeHierarchyDto;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.security.MifosUser;
import org.mifos.security.util.UserContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class CustomerServiceFacadeWebTier implements CustomerServiceFacade {

    private final OfficeDao officeDao;
    private final PersonnelDao personnelDao;
    private final CustomerDao customerDao;
    private final CustomerService customerService;

    public CustomerServiceFacadeWebTier(CustomerService customerService, OfficeDao officeDao,
            PersonnelDao personnelDao, CustomerDao customerDao) {
        this.customerService = customerService;
        this.officeDao = officeDao;
        this.personnelDao = personnelDao;
        this.customerDao = customerDao;
    }

    @Override
    public OnlyBranchOfficeHierarchyDto retrieveBranchOnlyOfficeHierarchy() {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        OfficeDto office = officeDao.findOfficeDtoById(userContext.getBranchId());
        List<OfficeBO> branchParents = officeDao.findBranchsOnlyWithParentsMatching(office.getSearchId());
        List<OfficeDetailsDto> levels = officeDao.findActiveOfficeLevels();

        List<OfficeHierarchyDto> branchOnlyOfficeHierarchy = OfficeBO.convertToBranchOnlyHierarchyWithParentsOfficeHierarchy(branchParents);

        return new OnlyBranchOfficeHierarchyDto(userContext.getPreferredLocale(), levels, office.getSearchId(), branchOnlyOfficeHierarchy);
    }

    private UserContext toUserContext(MifosUser user) {
        return new UserContextFactory().create(user);
    }

    private void checkVersionMismatch(Integer oldVersionNum, Integer newVersionNum) throws ApplicationException {
        if (!oldVersionNum.equals(newVersionNum)) {
            throw new ApplicationException(Constants.ERROR_VERSION_MISMATCH);
        }
    }

    @Override
    public CustomerSearch search(String searchString) throws ApplicationException {

        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = toUserContext(user);

        if (searchString == null) {
            throw new CustomerException(CenterConstants.NO_SEARCH_STRING);
        }

        String normalisedSearchString = org.mifos.framework.util.helpers.SearchUtils
                .normalizeSearchString(searchString);

        if (normalisedSearchString.equals("")) {
            throw new CustomerException(CenterConstants.NO_SEARCH_STRING);
        }

        String officeId = userContext.getBranchId().toString();
        String officeName = this.officeDao.findOfficeDtoById(userContext.getBranchId()).getName();

        PersonnelBO loggedInUser = personnelDao.findPersonnelById(userContext.getId());

        QueryResult searchResult = customerDao.search(normalisedSearchString, loggedInUser);

        return new CustomerSearch(searchResult, searchString, officeId, officeName);
    }

    @Override
    public boolean isGroupHierarchyRequired() {
        return ClientRules.getClientCanExistOutsideGroup();
    }

    @Override
    public GroupSearchResultsDto searchGroups(boolean searchForAddingClientsToGroup, String normalizedSearchString,
            Short loggedInUserId) {

        try {
            // FIXME - #000001 - keithw - move search logic off group business service over to customerDAO
            QueryResult searchResults = new GroupBusinessService().search(normalizedSearchString, loggedInUserId);
            QueryResult searchForAddingClientToGroupResults = null;
            if (searchForAddingClientsToGroup) {
                searchForAddingClientToGroupResults = new GroupBusinessService().searchForAddingClientToGroup(
                        normalizedSearchString, loggedInUserId);
            }

            return new GroupSearchResultsDto(searchResults, searchForAddingClientToGroupResults);
        } catch (ServiceException e) {
            throw new MifosRuntimeException(e);
        }
    }
}