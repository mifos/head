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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.mifos.customers.center.struts.action.OfficeHierarchyDto;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.exceptions.OfficeException;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.office.struts.OfficeUpdateRequest;
import org.mifos.customers.office.util.helpers.OfficeConstants;
import org.mifos.customers.office.util.helpers.OfficeStatus;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.security.util.UserContext;

public class OfficeServiceFacadeWebTier implements OfficeServiceFacade {

    private final OfficeDao officeDao;

    public OfficeServiceFacadeWebTier(OfficeDao officeDao) {
        this.officeDao = officeDao;
    }

    @Override
    public OfficeHierarchyDto headOfficeHierarchy() {
        return officeDao.headOfficeHierarchy();
    }

    @Override
    public String topLevelOfficeNames(String ids) {
        String[] idArray = ids.split(",");
        List<Short> idList = new LinkedList<Short>();
        for (String id : idArray) {
            idList.add(new Short(id));
        }
        List<String> topLevelOffices = officeDao.topLevelOfficeNames(idList);
        StringBuffer stringBuffer = new StringBuffer();
        for (Iterator<String> iterator = topLevelOffices.iterator(); iterator.hasNext();) {
            stringBuffer.append(iterator.next());
            if (iterator.hasNext()) {
                stringBuffer.append(", ");
            }
        }
        return stringBuffer.toString();
    }

    @Override
    public boolean updateOffice(UserContext userContext, Short officeId, Integer versionNum, OfficeUpdateRequest officeUpdateRequest) throws ApplicationException {

        boolean isParentOfficeChanged = false;

        OfficeBO office = officeDao.findOfficeById(officeId);
        office.validateVersion(versionNum);

        OfficeBO parentOffice = null;
        if (officeUpdateRequest.getParentOfficeId() != null) {
            parentOffice = officeDao.findOfficeById(officeUpdateRequest.getParentOfficeId());
        }

        if (office.isNameDifferent(officeUpdateRequest.getOfficeName())) {
            officeDao.validateOfficeNameIsNotTaken(officeUpdateRequest.getOfficeName());
        }

        if (office.isShortNameDifferent(officeUpdateRequest.getShortName())) {
            officeDao.validateOfficeShortNameIsNotTaken(officeUpdateRequest.getShortName());
        }

        if (!office.isStatusDifferent(officeUpdateRequest.getNewStatus())) {

            if (OfficeStatus.INACTIVE.equals(officeUpdateRequest.getNewStatus())) {
                officeDao.validateNoActiveChildrenExist(office.getOfficeId());
                officeDao.validateNoActivePeronnelExist(office.getOfficeId());
            }

            if (parentOffice.isInActive()) {
                throw new OfficeException(OfficeConstants.KEYPARENTNOTACTIVE);
            }
        }

        office.update(userContext, officeUpdateRequest, parentOffice);

        return isParentOfficeChanged;
    }
}