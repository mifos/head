/**

 * AuthrizationManager.java    version: 1.0



 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.



 * Apache License
 * Copyright (c) 2005-2006 Grameen Foundation USA
 *

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the

 * License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license

 * and how it is applied.

 *

 */

package org.mifos.framework.security.authorization;

import static org.mifos.framework.security.authorization.HierarchyManager.BranchLocation.BELOW;
import static org.mifos.framework.security.authorization.HierarchyManager.BranchLocation.SAME;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.application.rolesandpermission.business.RoleBO;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SecurityException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.ActivityRoles;
import org.mifos.framework.security.util.SecurityHelper;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;

/**
 * AuthrizationManager Acts as authorization Service it is singleton class and
 * it's object got created during the initilization phase .This object keep a
 * Hashmap of activities to roles and keep this hashmap synchrozinised with the
 * database
 */

public class AuthorizationManager {

	private HashMap<Short, Set> activityToRolesCacheMap = null;
	private static AuthorizationManager manager = new AuthorizationManager();
	private AuthorizationManager() {
		activityToRolesCacheMap = new HashMap<Short, Set>();
	}

	public static AuthorizationManager getInstance() {
		return manager;
	}

	public void init() throws SystemException, ApplicationException {

		try {
			List al = SecurityHelper.getActivities();
			activityToRolesCacheMap.clear();
			List leafs = SecurityHelper.getLeafActivities();
			for (int i = 0; i < al.size(); i++) {
				ActivityRoles act = ((ActivityRoles) al.get(i));
				if (leafs.contains(act.getId())) {
					activityToRolesCacheMap.put(act.getId(), act
							.getActivityRoles());
				}
			}

		} catch (SystemException e) {
			throw e;
		} catch (ApplicationException e) {
			throw e;
		} catch (Exception e) {
			throw new SecurityException(
				SecurityConstants.INITIALIZATIONFAILED, e);
		}

	}

	public boolean isActivityAllowed(UserContext userContext,
			ActivityContext activityContext) {
		try {
			Set rolesFromActivity = 
				activityToRolesCacheMap.get(activityContext.getActivityId());
			if (rolesFromActivity == null) {
				return false;
			}

			Set roles = new HashSet(rolesFromActivity);
			roles.retainAll(userContext.getRoles());
			if (roles.isEmpty()) {
				return false;
			} 
			else {
				HierarchyManager.BranchLocation where = 
					HierarchyManager.getInstance()
					.compareOfficeInHierarchy(userContext, 
						activityContext.getRecordOfficeId());
				PersonnelLevel personnelLevel = userContext.getLevel();
				short userId = userContext.getId().shortValue();
				if (where == SAME) {
					// 1 check if record belog to him if so let him do
					if (userId == activityContext.getRecordLoanOfficer()) {
						return true;
					} 
					else if (PersonnelLevel.LOAN_OFFICER == personnelLevel) {
						return false;
					}
					else {
						return true;
					}
				}
				else if (where == BELOW
						&& PersonnelLevel.LOAN_OFFICER != personnelLevel) {
					return true;
				}
				else {
					return false;
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void addRole(RoleBO role) {
		List<Short> activityIds = role.getActivityIds();
		Set<Short> keys = activityToRolesCacheMap.keySet();
		for (Iterator<Short> iter = keys.iterator(); iter.hasNext();) {
			Short activityId = iter.next();
			// see if for role has this activityId assingned. If it is, add it
			// to the cache
			if (activityIds.contains(activityId)) {
				Set<Short> roleSet = activityToRolesCacheMap.get(activityId);
				roleSet.add(role.getId());
			}
		}
	}

	public void updateRole(RoleBO role) {
		List<Short> activityIds = role.getActivityIds();
		Set<Short> keys = activityToRolesCacheMap.keySet();
		synchronized (activityToRolesCacheMap) {
			for (Iterator<Short> iter = keys.iterator(); iter.hasNext();) {
				Short activityId = iter.next();
				// see if for this activity role activitySet has anything in
				// it If there is not any remove it from the cache
				Set<Short> roleSet = activityToRolesCacheMap.get(activityId);
				if (activityIds.contains(activityId)) {
					roleSet.add(role.getId());
				} else {
					roleSet.remove(role.getId());
				}
			}
		}
	}

	public void deleteRole(RoleBO role) {
		List<Short> activityIds = role.getActivityIds();
		Set<Short> keys = activityToRolesCacheMap.keySet();
		synchronized (activityToRolesCacheMap) {
			for (Iterator<Short> iter = keys.iterator(); iter.hasNext();) {
				Short activityId = iter.next();
				// see if for this activity role activitySet has anything in
				// it If there is any remove it from the cache
				if (activityIds.contains(activityId)) {
					Set<Short> roleSet = activityToRolesCacheMap
							.get(activityId);
					roleSet.remove(role.getId());
				}
			}
		}
	}

}
