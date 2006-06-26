/**

 * RolesandPermissionActionForm.java    version: 1.0



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

/**
 *
 */
package org.mifos.application.rolesandpermission.struts.actionforms;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.rolesandpermission.util.valueobjects.RoleActivitiesKey;
import org.mifos.application.rolesandpermission.util.valueobjects.RoleActivity;
import org.mifos.framework.struts.actionforms.MifosActionForm;
import org.mifos.framework.util.helpers.Constants;

/**
 * This class represents the actionform for the role and permission group of
 * actions
 *
 * @author rajenders
 */
public class RolesandPermissionActionForm extends MifosActionForm {

	private static final long serialVersionUID = 99l;

	/**
	 * This would hold the id of the role we are currently operating
	 */
	private String id;

	/**
	 * This would hold the name of the role we are currently operating
	 */
	private String name;

	/**
	 * This would hold the version no of the current record to maintain the
	 * concurrency
	 */
	private String versionNo;

	/**
	 * This would hold the list of checked activities from the ui
	 */
	private Map<String, String> activities = new HashMap<String, String>();

	/**
	 * This Function returns the id
	 *
	 * @return Returns the id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * This function set the id
	 *
	 * @param id
	 *            The id to set.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * This Function returns the name
	 *
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * This function set the name
	 *
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {

		this.name = name;
	}

	/**
	 * Default constructor for RolesandPermissionActionForm
	 */
	public RolesandPermissionActionForm() {
		super();

	}

	/**
	 * This Function returns the set of activities<RoleActivity>
	 *
	 * @return Returns the activities.
	 */
	public Set<RoleActivity> getActivities() {

		/*
		 * we are converting the map of activities into the set of role
		 * activities so that when we call convert action form to value object
		 * is can convert it transparently
		 */
		Set<RoleActivity> act = new HashSet<RoleActivity>();
		Set<String> keys = activities.keySet();
		for (String string : keys) {

			/*
			 * We need to collect the id's of all the checked activities when we
			 * created the ui he have given unique name to leaf activities and
			 * "chekbox" to non leaf activities .Now we are trying to get the
			 * id's of checked leafs only
			 */
			try {
				Short activityId = Short.parseShort(activities.get(string));
				RoleActivitiesKey rak = new RoleActivitiesKey();
				rak.setActivityId(activityId);
				RoleActivity ra = new RoleActivity();
				ra.setCompositeKey(rak);
				act.add(ra);

			} catch (Exception e) {
				// ignore it
			}

		}

		activities.clear();

		return act;

	}

	/**
	 * This function set the activities
	 *
	 * @param activities
	 *            The activities to set.
	 */
	public void setActivities(Map<String, String> activities) {
		this.activities = activities;
	}

	/**
	 * This function gets the activity from the map based on the key
	 *
	 * @param key
	 *            key for the map
	 * @return value associated with the key
	 */
	public String getActivity(String key) {

		return this.activities.get(key);
	}

	/**
	 * This function sets the value in the map based on the key
	 *
	 * @param key
	 *            Key for value
	 * @param value
	 *            Value to be stored in the map
	 */
	public void setActivity(String key, String value) {

		// TODO remove CODE for debugging
		// // System.out.println("key--------"+key+"value--------"+value);
		this.activities.put(key, value);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping,
	 *      javax.servlet.ServletRequest)
	 */
	@Override
	public void reset(ActionMapping mapping, ServletRequest request) {
		// TODO Auto-generated method stub
		id = null;
		name = null;
		activities.clear();
		activities = null;

		super.reset(mapping, request);
	}

	/**
	 * This Function returns the version
	 *
	 * @return Returns the version.
	 */
	public String getVersionNo() {
		return versionNo;
	}

	/**
	 * This function set the version
	 *
	 * @param version
	 *            The version to set.
	 */
	public void setVersionNo(String versionNo) {
		this.versionNo = versionNo;
	}

	/**
	 * Thsi method is called before validate method is called in the base action
	 * class . In this method we are trying to restore the page to original
	 * state if the validation fail
	 *
	 * @param mapping
	 * @param request
	 * @return
	 */

	public ActionErrors customValidate(ActionMapping mapping,
			HttpServletRequest request) {

		String methodCalled = request.getParameter("method");

		if (null != methodCalled) {

			if ("cancel".equals(methodCalled) || "get".equals(methodCalled)
					|| "load".equals(methodCalled)
					|| "manage".equals(methodCalled)
					|| "previous".equals(methodCalled)) {
				request.setAttribute(Constants.SKIPVALIDATION, Boolean
						.valueOf(true));
			}

		} else {

		}
		return null;

	}

}
