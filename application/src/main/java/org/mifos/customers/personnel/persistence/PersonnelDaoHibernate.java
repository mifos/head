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

package org.mifos.customers.personnel.persistence;

import java.util.HashMap;
import java.util.List;

import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.servicefacade.CenterCreation;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.business.PersonnelDto;
import org.mifos.customers.personnel.util.helpers.PersonnelLevel;
import org.mifos.customers.personnel.util.helpers.PersonnelStatus;

public class PersonnelDaoHibernate implements PersonnelDao {

    private final GenericDao genericDao;

    public PersonnelDaoHibernate(GenericDao genericDao) {
        this.genericDao = genericDao;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PersonnelDto> findActiveLoanOfficersForOffice(CenterCreation centerCreationDto) {

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("levelId", PersonnelLevel.LOAN_OFFICER.getValue());
        queryParameters.put("userId", centerCreationDto.getLoggedInUserId());
        queryParameters.put("userLevelId", centerCreationDto.getLoggedInUserLevelId());
        queryParameters.put("officeId", centerCreationDto.getOfficeId());
        queryParameters.put("statusId", PersonnelStatus.ACTIVE.getValue());

        List<PersonnelDto> queryResult = (List<PersonnelDto>) genericDao.executeNamedQuery(
                NamedQueryConstants.MASTERDATA_ACTIVE_LOANOFFICERS_INBRANCH, queryParameters);

        return queryResult;
    }

    @Override
    public PersonnelBO findPersonnelById(Short id) {

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("PERSONNEL_ID", id);

        return (PersonnelBO) this.genericDao.executeUniqueResultNamedQuery("findPersonnelById", queryParameters);
    }
}
