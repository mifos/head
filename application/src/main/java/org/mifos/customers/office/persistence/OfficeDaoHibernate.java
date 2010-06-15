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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.master.MessageLookup;
import org.mifos.config.util.helpers.ConfigurationConstants;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.center.struts.action.OfficeHierarchyDto;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.util.helpers.GroupConstants;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.business.OfficeDetailsDto;
import org.mifos.customers.office.exceptions.OfficeException;
import org.mifos.customers.office.util.helpers.OfficeConstants;
import org.mifos.customers.office.util.helpers.OfficeLevel;
import org.mifos.customers.office.util.helpers.OfficeStatus;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.security.util.UserContext;

import edu.emory.mathcs.backport.java.util.Collections;

public class OfficeDaoHibernate implements OfficeDao {

    private final GenericDao genericDao;

    public OfficeDaoHibernate(GenericDao genericDao) {
        this.genericDao = genericDao;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<OfficeBO> findBranchsOnlyWithParentsMatching(String searchId) {

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("LEVEL_ID", OfficeLevel.BRANCHOFFICE.getValue());
        queryParameters.put("SEARCH_ID", searchId + "%");
        queryParameters.put("STATUS_ID", OfficeStatus.ACTIVE.getValue());
        List<OfficeBO> queryResult = (List<OfficeBO>) genericDao.executeNamedQuery(
                NamedQueryConstants.GET_BRANCH_PARENTS, queryParameters);

        if (queryResult == null) {
            queryResult = new ArrayList<OfficeBO>();
        }

        return queryResult;
    }

    @Override
    public OfficeBO findOfficeById(Short officeId) {

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("OFFICE_ID", officeId);

        return (OfficeBO) genericDao.executeUniqueResultNamedQuery("findOfficeById", queryParameters);
    }

    @Override
    public OfficeDto findOfficeDtoById(Short officeId) {

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("OFFICE_ID", officeId);

        return (OfficeDto) genericDao.executeUniqueResultNamedQuery("findOfficeDtoById", queryParameters);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<OfficeDetailsDto> findActiveOfficeLevels() {

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        List<OfficeDetailsDto> queryResult = (List<OfficeDetailsDto>) genericDao.executeNamedQuery(
                NamedQueryConstants.GETACTIVELEVELS, queryParameters);
        if (queryResult == null) {
            queryResult = new ArrayList<OfficeDetailsDto>();
        }

        return queryResult;
    }

    @Override
    public OfficeHierarchyDto headOfficeHierarchy() {
        OfficeBO headOffice = getHeadOffice();
        return officeHierarchy(headOffice);
    }

    @SuppressWarnings("unchecked")
    private OfficeBO getHeadOffice() {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("LEVEL_ID", OfficeConstants.HEADOFFICE);

        List<OfficeBO> queryResult = (List<OfficeBO>) this.genericDao.executeNamedQuery(NamedQueryConstants.OFFICE_GET_HEADOFFICE, queryParameters);

        if (queryResult != null && queryResult.size() != 0) {
            return queryResult.get(0);
        }
        throw new MifosRuntimeException("No head office found: ");
    }

    private OfficeHierarchyDto officeHierarchy(OfficeBO office) {
        List<OfficeHierarchyDto> childOfficeList = new LinkedList<OfficeHierarchyDto>();
        Set<OfficeBO> children = office.getChildren();
        for (OfficeBO child : children) {
            childOfficeList.add(officeHierarchy(child));
        }
        Collections.sort(childOfficeList);
        OfficeHierarchyDto hierarchy = new OfficeHierarchyDto(office.getOfficeId(), office.getOfficeName().trim(), office.getSearchId(), office.isActive(), childOfficeList);
        return hierarchy;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> topLevelOfficeNames(Collection<Short> officeIds) {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("OFFICE_IDS", officeIds);

        return (List<String>) this.genericDao.executeNamedQuery(NamedQueryConstants.GET_TOP_LEVEL_OFFICE_NAMES, queryParameters);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void validateNoActiveChildrenExist(Short officeId) throws OfficeException {

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("OFFICE_ID", officeId);
        List queryResult = this.genericDao.executeNamedQuery(NamedQueryConstants.GETCOUNTOFACTIVECHILDERN, queryParameters);
        int activeChildren = ((Number) queryResult.get(0)).intValue();

        if (activeChildren > 0) {
            throw new OfficeException(OfficeConstants.KEYHASACTIVECHILDREN);
        }
    }

    @Override
    public void validateBranchIsActiveWithNoActivePersonnel(Short officeId, UserContext userContext) throws CustomerException {

        OfficeBO office = findOfficeById(officeId);

        if (!office.isActive()) {
            throw new CustomerException(GroupConstants.BRANCH_INACTIVE, new Object[] { MessageLookup.getInstance()
                    .lookupLabel(ConfigurationConstants.GROUP, userContext) });
        }

        if (hasActivePeronnel(office.getOfficeId())) {
            throw new CustomerException(GroupConstants.LOANOFFICER_INACTIVE, new Object[] { MessageLookup.getInstance()
                    .lookup(ConfigurationConstants.BRANCHOFFICE, userContext) });
        }
    }

    @SuppressWarnings("unchecked")
    private boolean hasActivePeronnel(Short officeId) {

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("OFFICE_ID", officeId);
        queryParameters.put("STATUS_ID", PersonnelConstants.ACTIVE);
        List queryResult = this.genericDao.executeNamedQuery(NamedQueryConstants.GETOFFICEACTIVEPERSONNEL, queryParameters);

        return ((Number) queryResult.get(0)).longValue() > 0;
    }

    @Override
    public void validateNoActivePeronnelExist(Short officeId) throws OfficeException {

        if (hasActivePeronnel(officeId)) {
            throw new OfficeException(OfficeConstants.KEYHASACTIVEPERSONNEL);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void validateOfficeNameIsNotTaken(String officeName) throws OfficeException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("OFFICE_NAME", officeName);
        List queryResult = this.genericDao.executeNamedQuery(NamedQueryConstants.CHECKOFFICENAMEUNIQUENESS, queryParameters);

        int officeCount = ((Number) queryResult.get(0)).intValue();

        if (officeCount > 0) {
            throw new OfficeException(OfficeConstants.OFFICENAMEEXIST);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void validateOfficeShortNameIsNotTaken(String shortName) throws OfficeException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("SHORT_NAME", shortName);
        List queryResult = this.genericDao.executeNamedQuery(NamedQueryConstants.CHECKOFFICESHORTNAMEUNIQUENESS, queryParameters);
        int officeCount = ((Number) queryResult.get(0)).intValue();

        if (officeCount > 0) {
            throw new OfficeException(OfficeConstants.OFFICESHORTNAMEEXIST);
        }
    }
}