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

package org.mifos.customers.office.business.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.mifos.customers.center.struts.action.OfficeHierarchyDto;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.business.OfficeDetailsDto;
import org.mifos.customers.office.persistence.OfficeDto;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.customers.office.util.helpers.OfficeLevel;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.framework.business.AbstractBusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.security.util.UserContext;

import edu.emory.mathcs.backport.java.util.Collections;

public class OfficeBusinessService implements BusinessService {

    private OfficePersistence officePersistence = new OfficePersistence();

    @Override
    public AbstractBusinessObject getBusinessObject(UserContext userContext) {
        return null;
    }

    public List<OfficeDetailsDto> getActiveParents(OfficeLevel level, Short localeId) throws ServiceException {
        try {
            return officePersistence.getActiveParents(level, localeId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<OfficeDetailsDto> getConfiguredLevels(Short localeId) throws ServiceException {
        try {
            return officePersistence.getActiveLevels(localeId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }

    }

    public OfficeBO getOffice(Short officeId) throws ServiceException {
        try {
            return officePersistence.getOffice(officeId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<OfficeDetailsDto> getStatusList(Short localeId) throws ServiceException {
        try {
            return officePersistence.getStatusList(localeId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<OfficeBO> getOfficesTillBranchOffice() throws ServiceException {
        try {
            return officePersistence.getOfficesTillBranchOffice();
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<OfficeBO> getBranchOffices() throws ServiceException {
        try {
            return officePersistence.getBranchOffices();
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<OfficeDetailsDto> getChildOffices(String searchId) throws ServiceException {
        try {
            return officePersistence.getChildOffices(searchId);
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<OfficeBO> getActiveBranchesUnderUser(PersonnelBO personnel) throws ServiceException {
        try {
            return officePersistence.getActiveBranchesUnderUser(personnel.getOfficeSearchId());
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<OfficeBO> getAllofficesForCustomFIeld() throws ServiceException {
        try {
            return officePersistence.getAllofficesForCustomFIeld();
        } catch (PersistenceException pe) {
            throw new ServiceException(pe);
        }
    }

    public OfficeHierarchyDto headOfficeHierarchy() throws ServiceException {
        OfficeBO headOffice = getHeadOffice();
        return officeHierarchy(headOffice);
    }

    private OfficeHierarchyDto officeHierarchy(OfficeBO office) {
        List<OfficeHierarchyDto> childOfficeList = new LinkedList<OfficeHierarchyDto>();
        Set<OfficeBO> children = office.getChildren();
        for (OfficeBO child : children) {
            childOfficeList.add(officeHierarchy(child));
        }
        Collections.sort(childOfficeList);
        OfficeHierarchyDto hierarchy = new OfficeHierarchyDto(office.getOfficeId(), office.getOfficeName(), office
                .getSearchId(), office.isActive(), childOfficeList);
        return hierarchy;
    }

    public OfficeBO getHeadOffice() throws ServiceException {
        try {
            return officePersistence.getHeadOffice();
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public List<String> topLevelOfficeNames(List<Short> ids) {
        return officePersistence.topLevelOfficeName(ids);
    }
}
