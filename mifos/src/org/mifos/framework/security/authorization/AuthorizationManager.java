/**

 * AuthrizationManager.java    version: 1.0



 * Copyright © 2005-2006 Grameen Foundation USA

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mifos.application.rolesandpermission.util.valueobjects.Role;
import org.mifos.application.rolesandpermission.util.valueobjects.RoleActivitiesKey;
import org.mifos.application.rolesandpermission.util.valueobjects.RoleActivity;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SecurityException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.ActivityRoles;
import org.mifos.framework.security.util.Observer;
import org.mifos.framework.security.util.RoleChangeEvent;
import org.mifos.framework.security.util.SecurityEvent;
import org.mifos.framework.security.util.SecurityHelper;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.util.helpers.Constants;

/**
 * @author rajenders
 *
 * AuthrizationManager Acts as authorization Service it is singleton class and
 * it's object got created during the initilization phase .This object keep a
 * Hashmap of activities to roles and keep this hashmap synchrozinised with the
 * database
 */

public class AuthorizationManager implements Observer {

	/** ********************************************Fields************** */
	/**
	 * This would keep mapping of activites to roles
	 */
	private HashMap<Short, Set> activityToRolesCacheMap = null;

	/**
	 * This Would hold the singleton instance of this class
	 */
	private static AuthorizationManager am = new AuthorizationManager();

	/** ********************** Constructor ****************** */
	/**
	 * The default constructor
	 */
	private AuthorizationManager() {
		activityToRolesCacheMap = new HashMap<Short, Set>();
	}

	/** **************************** Methods ***************** */
	/**
	 * Thsi method create/return the single instance of AuthrizationManager
	 */
	public static AuthorizationManager getInstance() {
		return am;
	}

	/**
	 * This method monitors for the database changes for synchronization
	 *
	 * @see org.mifos.framework.security.Util.Observer#update(org.mifos.framework.security.Util.Subject)
	 */
	public void handleEvent(SecurityEvent e) {
		if (e instanceof RoleChangeEvent) {

			// we need to convert it to a list of id's
			Role role = (Role) e.getObject();
			Set currentActivitySet = role.getActivities();
			List activitySet = new ArrayList();
			for (Iterator iter = currentActivitySet.iterator(); iter.hasNext();) {
				RoleActivity roleActivity = (RoleActivity) iter.next();
				RoleActivitiesKey activityKey = roleActivity.getCompositeKey();
				activitySet.add(activityKey.getActivityId());
			}

			if (e.getEventType().toLowerCase() == Constants.CREATE) {
				Set<Short> keys = activityToRolesCacheMap.keySet();

				/*
				 * iterate the cache based on the activities and whatever
				 * activities role has update that
				 */
				for (Iterator iter = keys.iterator(); iter.hasNext();) {
					Short cacheActivity = (Short) iter.next();

					/*
					 * see if for this activity role activitySet has anything in
					 * it If there is any add it to the cache
					 */
					if (activitySet.contains(cacheActivity)) {
						Set roleSet = activityToRolesCacheMap
								.get(cacheActivity);
						roleSet.add(role.getId());
					}

				}

			} else if (e.getEventType().toLowerCase() == Constants.UPDATE) {

				// During update we may have to remove some role_id's and add
				// other role_id's
				Set<Short> keys = activityToRolesCacheMap.keySet();
				for (Iterator iter = keys.iterator(); iter.hasNext();) {
					Short cacheActivity = (Short) iter.next();

					/*
					 * see if for this activity role activitySet has anything in
					 * it If there is not any remove it from the cache
					 */
					Set roleSet = activityToRolesCacheMap.get(cacheActivity);
					if (activitySet.contains(cacheActivity)) {

						roleSet.add(role.getId());

					} else {
						roleSet.remove(role.getId());
					}

				}

			} else if (e.getEventType().toLowerCase() == Constants.DELETE) {
				Set<Short> keys = activityToRolesCacheMap.keySet();

				/*
				 * iterate the cache based on the activities and whatever
				 * activities role has update that
				 */
				for (Iterator iter = keys.iterator(); iter.hasNext();) {
					Short cacheActivity = (Short) iter.next();

					/*
					 * see if for this activity role activitySet has anything in
					 * it If there is any remove it from the cache
					 */
					if (activitySet.contains(cacheActivity)) {
						Set roleSet = activityToRolesCacheMap
								.get(cacheActivity);
						roleSet.remove(role.getId());
					}

				}

			}

		}

	}

	/**
	 * This function initialize the cache from the database.It takes the help of
	 * getActivities() and list of all the ActivityRoles objects in the system
	 * .Which in turn composed of activity description and the all the role id's
	 * associated with that activity id
	 *
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void init() throws SystemException ,ApplicationException {

		try {
		List al = SecurityHelper.getActivities();
		activityToRolesCacheMap.clear();
		List leafs = SecurityHelper.getLeafActivities();
		for (int i = 0; i < al.size(); i++) {
			ActivityRoles act = ((ActivityRoles) al.get(i));
			if (leafs.contains(act.getId())) {
				activityToRolesCacheMap
						.put(act.getId(), act.getActivityRoles());
			}
		}

		}catch(SystemException se )
		{
			throw se;
		}
		catch ( ApplicationException ae)
		{
			throw ae;
		}
		catch(Exception e)
		{
			throw new SecurityException(SecurityConstants.INITIALIZATIONFAILED);
		}

	}

	/**
	 * This method create the userroles set based on the userid
	 *
	 * @param uid
	 *            user id
	 * @return set of roles associated with the user
	 */
	public Set createUserRoles(short uid) throws SystemException ,ApplicationException {

		try
		{

		return SecurityHelper.getUserRoles(uid);
		}catch(SystemException se )
		{
			throw se;
		}
		catch ( ApplicationException ae)
		{
			throw ae;
		}
		catch(Exception e)
		{
			throw new SecurityException(SecurityConstants.GENERALERROR);
		}

	}

	/**
	 * This is main method which checks whether user is allowed to perform the
	 * action he is doing
	 *
	 * @param uc
	 *            UserContext object associated with user
	 * @param aid
	 *            Acitivity id of the operation
	 * @param officeid
	 *            id if office on which he wants to perform operation
	 * @return whether user is allowed to perform the action
	 */
	public boolean isActivityAllowed(UserContext userContext, ActivityContext activityContext) {
		// System.out.println("activityContext.getActivityId()------------------"+activityContext.getActivityId());
		// System.out.println("activityToRolesCacheMap------------------"+activityToRolesCacheMap.size());
		// System.out.println("activityToRolesCacheMap.get(Short.valueOf(activityContext.getActivityId()))  ---"+				activityToRolesCacheMap.get(activityContext.getActivityId()));
		try {
		Set roles = new HashSet(activityToRolesCacheMap.get(activityContext.getActivityId()));
		roles.retainAll(userContext.getRoles());
		if (roles.isEmpty()) {
			return false;
		} else {

			HierarchyManager hm = HierarchyManager.getInstance();
			short ol = hm.getOfficeLevel(userContext, activityContext.getRecordOfficeId());
			// System.out.println("office level for this record is "+ol);

			short personnelLevel= userContext.getLevelId().shortValue();
			short userId = userContext.getId().shortValue();
			if (ol == Constants.BRANCH_SAME) {

				// System.out.println("record belong to login user branch");

				// 1 check if record belog to him if so let him do
				if (userId == activityContext.getRecordLoanOfficer()) {

					// System.out.println("record belong to login user itself");

					return true;

				}
				// TODO assume 1 is loan officer check whether he is loan
				// officer
				if (Constants.LOANOFFICER == personnelLevel) {
					// System.out.println("user is loan officer and record does not belog to him");


					return false;
				} else {

					// System.out.println("user is not loan officer and record does not belog to him");


					return true;
				}
			} else if (ol == Constants.BRANCH_BELOW
					&& Constants.LOANOFFICER != personnelLevel) {
				// System.out.println("user is not loan officer and record does fall in his data scope");

				return true;
			} else {
				return false;
			}
		}
		}catch(Exception e) {
			// System.out.println("exception in Auth Man-------");
			e.printStackTrace();
		}
		return false;


	}
	/**
	 * This function gets the list if all the activities user is allowed to
	 * oerform in the system
	 *
	 * @param uc
	 *            UserContext object associated with user
	 * @return array of activity id associated with the user
	 */
	public ArrayList getAcivitiesAllowed(UserContext uc) {

		ArrayList al = new ArrayList();

		Set keys = activityToRolesCacheMap.keySet();
		Iterator itr = keys.iterator();
		while (itr.hasNext()) {
			Short key = new Short((Short) itr.next());
			Set roles = (Set) activityToRolesCacheMap.get(key);
			Set userRoles = new HashSet(uc.getRoles());
			userRoles.retainAll(roles);
			if (!userRoles.isEmpty()) {
				al.add(key);
			}
		}
		return al;
	}
}
