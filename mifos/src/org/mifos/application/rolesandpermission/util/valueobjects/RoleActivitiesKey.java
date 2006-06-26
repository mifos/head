/**

 * RoleActivities.java    version: 1.0

 

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
package org.mifos.application.rolesandpermission.util.valueobjects;

import java.io.Serializable;

/**
 * Key for roleActivity class
 * @author rajenders
 * 
 */
public class RoleActivitiesKey implements Serializable {

	
	private static final long serialVersionUID=99214235l;

	

	/**
	 * This would hold the roles id 
	 */
	private Short roleId;

	/**
	 * This would hold the activity id
	 */
	private Short activityId;
	

	/**
	 * @param id
	 * @param id2
	 */
	public RoleActivitiesKey(Short id, Short id2) {
		super();
		// TODO Auto-generated constructor stub
		roleId = id;
		activityId = id2;
	}

	/**
	 * This Function returns the activityId
	 * 
	 * @return Returns the activityId.
	 */
	public Short getActivityId() {
		return activityId;
	}

	/**
	 * This function set the activityId
	 * 
	 * @param activityId
	 *            The activityId to set.
	 */
	public void setActivityId(Short activityId) {
		this.activityId = activityId;
	}

	/**
	 * This Function returns the roleId
	 * 
	 * @return Returns the roleId.
	 */
	public Short getRoleId() {
		return roleId;
	}

	/**
	 * This function set the roleId
	 * 
	 * @param roleId
	 *            The roleId to set.
	 */
	public void setRoleId(Short roleId) {
		this.roleId = roleId;
	}
	


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object arg0) {
		// TODO Auto-generated method stub
		return super.equals(arg0);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	/**
	 * Default constructor
	 */
	public RoleActivitiesKey() {
		super();
		// TODO Auto-generated constructor stub
	}


}
