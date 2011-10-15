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

/**
 *
 */
package org.mifos.security.authorization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.office.exceptions.OfficeException;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.security.util.Observer;
import org.mifos.security.util.OfficeCacheDto;
import org.mifos.security.util.OfficeSearch;
import org.mifos.security.util.SecurityEvent;
import org.mifos.security.util.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public class HierarchyManager implements Observer {

    private static final Logger logger = LoggerFactory.getLogger(HierarchyManager.class);

    private static Map<Short, OfficeCacheDto> hierarchyMap;
    private static HierarchyManager hierarchyManager;

    private HierarchyManager() {
        hierarchyMap = new HashMap<Short, OfficeCacheDto>();
    }

    public static HierarchyManager getInstance() {
        if (hierarchyManager == null) {
            hierarchyManager = new HierarchyManager();
        }
        return hierarchyManager;
    }

    @Override
	public void handleEvent(SecurityEvent e) {
        logger.info("Map before" + hierarchyMap);
        List<OfficeCacheDto> officeList = convertToOfficeCacheList((List<OfficeSearch>) e.getObject());
        if (e.getEventType().equals(Constants.CREATE)) {
            updateMapForCreateEvent(officeList);
        } else if (e.getEventType().equals(Constants.UPDATE)) {
            updateMapForUpdateEvent(officeList);
        }

        logger.info("Map after " + hierarchyMap);
    }

    private void updateMapForCreateEvent(List<OfficeCacheDto> officeList) {
        addToMap(officeList.get(0));
    }

    private void updateMapForUpdateEvent(List<OfficeCacheDto> officeList) {
        for (int i = 0; i < officeList.size(); i++) {
            synchronized (hierarchyMap) {
                hierarchyMap.remove(officeList.get(i));
                addToMap(officeList.get(i));
            }
        }
    }

    private void addToMap(OfficeCacheDto cacheView) {
        hierarchyMap.put(cacheView.getOfficeId(), cacheView);
    }

    private List<OfficeCacheDto> convertToOfficeCacheList(List<OfficeSearch> officeList) {
        List<OfficeCacheDto> officeCacheList = new ArrayList<OfficeCacheDto>();
        for (int i = 0; i < officeList.size(); i++) {
            OfficeCacheDto cacheView = new OfficeCacheDto(officeList.get(i).getOfficeId(), officeList.get(i)
                    .getParentOfficeId(), officeList.get(i).getSearchId());
            officeCacheList.add(cacheView);
        }
        return officeCacheList;
    }

    public void init() throws SystemException, OfficeException {
        List<OfficeCacheDto> officeList;
        try {
            officeList = new OfficePersistence().getAllOffices();
        } catch (PersistenceException e) {
            throw new OfficeException(e);
        }
        hierarchyMap.clear();
        for (int i = 0; i < officeList.size(); i++) {
            addToMap(officeList.get(i));
        }
    }

    public BranchLocation compareOfficeInHierarchy(UserContext user, Short officeId) {

        Assert.notNull(officeId, "officeId should not be null");
        Assert.notNull(user, "userContext should not be null");

        short userBranch = user.getBranchId().shortValue();
        if (userBranch == officeId) {
            return BranchLocation.SAME;
        }

        /*
         * Look into the map now if the passed officeid's searchid on which user wants to perform action starts with the
         * user's office searchid it means that office falls under that user hiererchy
         */
        String userOfficeSearchId = hierarchyMap.get(user.getBranchId()).getSearchId();

        OfficeCacheDto cachedOffice = hierarchyMap.get(officeId);
        if (cachedOffice == null) {
            try {
                init(); // repopulate cachedmap
                cachedOffice = hierarchyMap.get(officeId);
            } catch (SystemException e) {
                throw new MifosRuntimeException(e);
            } catch (OfficeException e) {
                throw new MifosRuntimeException(e);
            }
            if (cachedOffice == null) {
                throw new IllegalArgumentException("office with id [" + officeId + "] does not exist");
            }
        }
        String operatedOfficeSearchId = cachedOffice.getSearchId();

        if (operatedOfficeSearchId.startsWith(userOfficeSearchId)) {
            return BranchLocation.BELOW;
        }

        return BranchLocation.ABOVE_OR_DIFFERENT;
    }

    public String getSearchId(short branchId) {
        return hierarchyMap.get(Short.valueOf(branchId)).getSearchId();
    }

    public Short getParentOfficeId(Short officeId) {
        return hierarchyMap.get(officeId).getParentOfficeId();
    }

    public enum BranchLocation {
        SAME, BELOW, ABOVE_OR_DIFFERENT
    };
}