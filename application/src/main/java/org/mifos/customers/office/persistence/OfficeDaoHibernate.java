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
import java.util.HashMap;
import java.util.List;

import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.master.MessageLookup;
import org.mifos.config.util.helpers.ConfigurationConstants;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.util.helpers.GroupConstants;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.business.OfficeDetailsDto;
import org.mifos.customers.office.util.helpers.OfficeLevel;
import org.mifos.customers.office.util.helpers.OfficeStatus;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.security.util.UserContext;

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
        if (queryResult != null && queryResult.size() != 0) {
            return ((Number) queryResult.get(0)).longValue() > 0;
        }
        return false;
    }
}