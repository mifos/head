/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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
package org.mifos.framework.security.authorization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.customers.office.exceptions.OfficeException;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.Observer;
import org.mifos.framework.security.util.OfficeCacheView;
import org.mifos.framework.security.util.OfficeSearch;
import org.mifos.framework.security.util.SecurityEvent;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;

public class HierarchyManager implements Observer {
    private static Map<Short, OfficeCacheView> hierarchyMap;
    private static HierarchyManager hierarchyManager;

    private HierarchyManager() {
        hierarchyMap = new HashMap<Short, OfficeCacheView>();
    }

    public static HierarchyManager getInstance() {
        if (hierarchyManager == null)
            hierarchyManager = new HierarchyManager();
        return hierarchyManager;
    }

    public void handleEvent(SecurityEvent e) {
        MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("Map before" + hierarchyMap);
        List<OfficeCacheView> officeList = convertToOfficeCacheList((List<OfficeSearch>) e.getObject());
        if (e.getEventType().equals(Constants.CREATE))
            updateMapForCreateEvent(officeList);
        else if (e.getEventType().equals(Constants.UPDATE))
            updateMapForUpdateEvent(officeList);

        MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info("Map after " + hierarchyMap);
    }

    private void updateMapForCreateEvent(List<OfficeCacheView> officeList) {
        addToMap(officeList.get(0));
    }

    private void updateMapForUpdateEvent(List<OfficeCacheView> officeList) {
        for (int i = 0; i < officeList.size(); i++) {
            synchronized (hierarchyMap) {
                hierarchyMap.remove(officeList.get(i));
                addToMap(officeList.get(i));
            }
        }
    }

    private void addToMap(OfficeCacheView cacheView) {
        hierarchyMap.put(cacheView.getOfficeId(), cacheView);
    }

    private List<OfficeCacheView> convertToOfficeCacheList(List<OfficeSearch> officeList) {
        List<OfficeCacheView> officeCacheList = new ArrayList<OfficeCacheView>();
        for (int i = 0; i < officeList.size(); i++) {
            OfficeCacheView cacheView = new OfficeCacheView(officeList.get(i).getOfficeId(), officeList.get(i)
                    .getParentOfficeId(), officeList.get(i).getSearchId());
            officeCacheList.add(cacheView);
        }
        return officeCacheList;
    }

    public void init() throws SystemException, OfficeException {
        List<OfficeCacheView> officeList;
        try {
            officeList = new OfficePersistence().getAllOffices();
        } catch (PersistenceException e) {
            throw new OfficeException(e);
        }
        hierarchyMap.clear();
        for (int i = 0; i < officeList.size(); i++)
            addToMap(officeList.get(i));
    }

    public BranchLocation compareOfficeInHierarchy(UserContext user, short officeId) {
        short userBranch = user.getBranchId().shortValue();
        if (userBranch == officeId) {
            return BranchLocation.SAME;
        } else {
            /*
             * Look into the map now if the passed officeid's searchid on which
             * user wants to perform action starts with the user's office
             * searchid it means that office falls under that user hiererchy
             */
            String userOfficeSearchId = hierarchyMap.get(user.getBranchId()).getSearchId();
            String operatedOfficeSearchId = hierarchyMap.get(Short.valueOf(officeId)).getSearchId();

            if (operatedOfficeSearchId.startsWith(userOfficeSearchId)) {
                return BranchLocation.BELOW;
            } else {
                return BranchLocation.ABOVE_OR_DIFFERENT;
            }
        }
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
