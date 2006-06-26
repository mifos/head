/**

 * GroupTransferInput.java    version: 1.0



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

package org.mifos.application.customer.group.util.helpers;

import java.io.Serializable;

/**
 * This class is helper class that keeps data when group is transferred across office.
 * @author navitas
 *
 */
public class GroupTransferInput implements Serializable{
	
	/** Denotes the global customer number of the group being transferred*/
	private String globalCustNum;
	
	/** Denotes the name of the group being transferred*/
	private String groupName;
	
	/** Denotes the name of the center(if center hierarchy exists) under which group is being transferred*/
	private String centerName;
	
	/** Denotes the id of the center(if center hierarchy exists) under which group is being transferred*/
	private String centerId;
	
	/** Denotes the office name under which group is being transferred*/
	private String officeName;
	
	/** Denotes the office id under which group is being transferred*/
	private Short  officeId;
	
	/** Denotes the version no of the group  being transferred*/
	private Integer versionNo;

	
	/**
     * Return the value of the versionNo attribute.
     * @return Integer
     */
	public Integer getVersionNo() {
		return versionNo;
	}
	
	/**
     * Sets the value of versionNo
     * @param versionNo
     */	
	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
	}
	
	/**
     * Return the value of the centerId attribute.
     * @return String
     */	
	public String getCenterId() {
		return centerId;
	}

	/**
     * Sets the value of centerId
     * @param centerId
     */
	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}

	/**
     * Return the value of the centerName attribute.
     * @return String
     */
	public String getCenterName() {
		return centerName;
	}
	
	/**
     * Sets the value of centerName
     * @param centerName
     */	
	public void setCenterName(String centerName) {
		this.centerName = centerName;
	}
	/**
     * Return the value of the officeId attribute.
     * @return Short
     */	
	public Short getOfficeId() {
		return officeId;
	}
	
	/**
     * Sets the value of officeId
     * @param officeId
     */	
	public void setOfficeId(Short officeId) {
		this.officeId = officeId;
	}
	/**
     * Return the value of the officeName attribute.
     * @return String
     */	
	public String getOfficeName() {
		return officeName;
	}

	/**
     * Sets the value of officeName
     * @param officeName
     */
	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}
	/**
     * Return the value of the globalCustNum attribute.
     * @return String
     */	
	public String getGlobalCustNum() {
		return globalCustNum;
	}

	/**
     * Sets the value of globalCustNum
     * @param groupInput
     */
	public void setGlobalCustNum(String globalCustNum) {
		this.globalCustNum = globalCustNum;
	}
	/**
     * Return the value of the groupName attribute.
     * @return String
     */	
	public String getGroupName() {
		return groupName;
	}

	/**
     * Sets the value of groupName
     * @param groupName
     */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	}
