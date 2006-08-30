/**

 * RolesAndPermissionHelper.java    version: 1.0

 

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
package org.mifos.application.rolesandpermission.util.helpers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mifos.application.rolesandpermission.util.valueobjects.Role;
import org.mifos.application.rolesandpermission.util.valueobjects.RoleActivitiesKey;
import org.mifos.application.rolesandpermission.util.valueobjects.RoleActivity;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.SearchResults;

/**
 * This class contails the utility function for role and permission module
 */
public class RolesAndPermissionHelper {
	

	/**
	 * This function returns the copy of the passed role object
	 * 
	 * @param source
	 *            role object copy of which you want to make
	 * @return the new cloned object of the passed object
	 */
	public static Role makeCopy(Role source) {

		Role clone = new Role();
		clone.setDescription(source.getDescription());
		clone.setId(source.getId());
		clone.setName(source.getName());
		clone.setVersionNo(source.getVersionNo());
		Set activitiesClone = new HashSet();
		for (Iterator iter = source.getActivities().iterator(); iter.hasNext();) {
			RoleActivity element = (RoleActivity) iter.next();
			RoleActivitiesKey key = element.getCompositeKey();
			RoleActivitiesKey keyClone = new RoleActivitiesKey(key.getRoleId(),
					key.getActivityId());
			RoleActivity elementCone = new RoleActivity();
			elementCone.setCompositeKey(keyClone);
			activitiesClone.add(elementCone);
		}
		clone.setActivities(activitiesClone);
		return clone;

	}

	/**
	 * This function would return the search results based on the passed name
	 * and value
	 * 
	 * @param name
	 *            name of the search result
	 * @param value
	 *            value of the search result
	 * @return
	 */
	public static SearchResults getSearchResutls(String name, Object value) {
		SearchResults sr = new SearchResults();
		sr.setResultName(name);
		sr.setValue(value);
		return sr;
	}

	/**
	 * This function is helper function used to build the templete for the ui
	 * 
	 * @param l
	 *            list of activities in the system
	 * @return StringBuilder object containing the string representation of the
	 *         templete
	 */
	public static StringBuilder getTempleteBuffer(List l, Short localeId) {
		StringBuilder sb = new StringBuilder();
		RoleTempleteBuilder builder = new RoleTempleteBuilder();
		builder.setLocaleId(localeId);
		sb = builder.getRolesTemplete(l);

		return sb;
	}

	/**
	 * This function covert the set of RoleActivity objects to the set of id's
	 * 
	 * @param roleActivities
	 *            set of RoleActivity objects
	 * @return Set of role id's
	 */
	private static Set convertToIdSet(Set roleActivities) {
		Set<Short> activities = new HashSet<Short>();
		for (Iterator iter = roleActivities.iterator(); iter.hasNext();) {
			RoleActivity element = (RoleActivity) iter.next();
			activities.add(((RoleActivitiesKey) element.getCompositeKey())
					.getActivityId());
		}
		return activities;

	}

	/**
	 * This function build the templete for perticular seleted role in ui by the
	 * user
	 * 
	 * @param l
	 *            list of all the activities
	 * @param roleActivities
	 *            set of activities id's with this set
	 * @return StringBuilder for this role
	 */
	public static StringBuilder getTempleteBuffer(List l, Set roleActivities , Short localeId) {
		StringBuilder sb = new StringBuilder();
		RoleTempleteBuilder builder = new RoleTempleteBuilder();
		builder.setLocaleId(localeId);
		Set activitySet = convertToIdSet(roleActivities);
		builder.setCurrentActivites(activitySet);
		sb = builder.getRolesTemplete(l);

		return sb;
	}

	/**
	 * This function will save the specified object into the context 
	 * @param name name of the object we want to save 
	 * @param obj object that we want to save 
	 * @param context context object
	 */
	public static void saveInContext(String name, Object obj, Context context) {
		

		SearchResults oldResults = context
				.getSearchResultBasedOnName(name);
		if (null == oldResults) {
			SearchResults result = RolesAndPermissionHelper.getSearchResutls(
					name, obj);

			context.addAttribute(result);
		} else {
			oldResults.setValue(obj);

		}

	}
	/**
	 * This function would convert the passed activitySet to the arryList 
	 * @param activitySet set of RoleActivity objects
	 * @return ArrayList of activity id's
	 */
	public static ArrayList convertSetToArrayList( Set activitySet)
	{
		
		ArrayList al = new ArrayList();
		// making the list of id' for the role which is being updated from
		// the UI
		for (Iterator iter = activitySet.iterator(); iter.hasNext();) {
			RoleActivity roleActivity = (RoleActivity) iter.next();
			RoleActivitiesKey activityKey = roleActivity.getCompositeKey();
			al.add(activityKey.getActivityId());
		}
		return al;
	}

}
