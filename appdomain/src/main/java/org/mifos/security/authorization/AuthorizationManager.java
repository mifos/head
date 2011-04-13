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

package org.mifos.security.authorization;

import static org.mifos.security.authorization.HierarchyManager.BranchLocation.BELOW;
import static org.mifos.security.authorization.HierarchyManager.BranchLocation.SAME;

import java.util.Set;

import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.customers.personnel.util.helpers.PersonnelLevel;
import org.mifos.security.util.ActivityContext;
import org.mifos.security.util.UserContext;
import org.mifos.security.rolesandpermission.business.ActivityEntity;
import org.mifos.security.rolesandpermission.persistence.LegacyRolesPermissionsDao;

/**
 * A singleton authorization service. Tracks a map of activities to roles
 * synchronized with the <code>activity</code> table in the database.
 */
@SuppressWarnings("unchecked")
public class AuthorizationManager {

    private static AuthorizationManager manager = new AuthorizationManager();

    private LegacyRolesPermissionsDao legacyRolesPermissionsDao = ApplicationContextProvider.getBean(LegacyRolesPermissionsDao.class);

    private AuthorizationManager() {
    }

    public static synchronized AuthorizationManager getInstance() {
        return manager;
    }

    public  synchronized boolean isActivityAllowed(UserContext userContext, ActivityContext activityContext) {
        try {
            ActivityEntity activity = legacyRolesPermissionsDao.getActivityById(activityContext.getActivityId());
            if (activity == null) {
                return false;
            }

            Set<Short> activityAllowedRoles = activity.getRoleIds();
            if (activityAllowedRoles == null) {
                return false;
            }

            Set<Short> userRoles = userContext.getRoles();

            activityAllowedRoles.retainAll(userRoles);

            if (activityAllowedRoles.isEmpty()) {
                return false;
            }

            HierarchyManager.BranchLocation where = HierarchyManager.getInstance().compareOfficeInHierarchy(userContext, activityContext.getRecordOfficeId());
            PersonnelLevel personnelLevel = userContext.getLevel();
            short userId = userContext.getId().shortValue();
            if (where == SAME) {
                // 1 check if record belog to him if so let him do
                if (userId == activityContext.getRecordLoanOfficer()) {
                    return true;
                } else if (PersonnelLevel.LOAN_OFFICER == personnelLevel) {
                    return false;
                }

                return true;

            } else if (where == BELOW && PersonnelLevel.LOAN_OFFICER != personnelLevel) {
                return true;
            }

            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
