/**

 * GroupSearchResults.java    version: 1.0



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
package org.mifos.application.customer.group.util.valueobjects;

import java.io.Serializable;

/**
 * This class holds tha data related to Group Search
 * @author navita
 *
 */
public class GroupSearchResults implements Serializable{
	/**Denotes the office name to whicht he center belongs*/
	private String officeName;
	/**Denotes the center name on which the search is conducted*/
	private String centerName;
	/**Denotes the id of the group*/
	private int groupId;
	/**Denotes the name of the group*/
	private String groupName;
	/**
	 * Method which returns the centerName	
	 * @return Returns the centerName.
	 */
	public String getCenterName() {
		return centerName;
	}
	/**
	 * Method which sets the centerName
	 * @param centerName The centerName to set.
	 */
	public void setCenterName(String centerName) {
		this.centerName = centerName;
	}
	
	/**
	 * Method which returns the officeName	
	 * @return Returns the officeName.
	 */
	public String getOfficeName() {
		return officeName;
	}
	/**
	 * Method which sets the officeName
	 * @param officeName The officeName to set.
	 */
	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}
	/**
	 * Method which returns the groupId	
	 * @return Returns the groupId.
	 */
	public int getGroupId() {
		return groupId;
	}
	/**
	 * Method which sets the groupId
	 * @param groupId The groupId to set.
	 */
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	/**
	 * Method which returns the groupName	
	 * @return Returns the groupName.
	 */
	public String getGroupName() {
		return groupName;
	}
	/**
	 * Method which sets the groupName
	 * @param groupName The groupName to set.
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	
	
}
