/**

 * OfficeSubObject.java    version: 1.0

 

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
package org.mifos.application.office.util.helpers;

/**
 *  Helper Object for keeping some fields backup
 * @author rajenders
 *
 */
public class OfficeSubObject {
	
	private String officeName;
	private String ShortName;
	private Short status;
	private Short parentId;
	private Short officeType;
	/**
	 * @param officeName
	 * @param shortName
	 * @param status
	 * @param parentId
	 * @param officeType
	 */
	public OfficeSubObject(String officeName, String shortName, Short status, Short parentId, Short officeType) {
		// TODO Auto-generated constructor stub
		this.officeName = officeName;
		ShortName = shortName;
		this.status = status;
		this.parentId = parentId;
		this.officeType = officeType;
	}
	/**
	 * This function returns the officeType
	 * @return Returns the officeType.
	 */
	public Short getOfficeType() {
		return officeType;
	}
	/**
	 * This function sets the officeType
	 * @param officeType The officeType to set.
	 */
	public void setOfficeType(Short officeType) {
		this.officeType = officeType;
	}
	/**
	 * @param officeName
	 * @param shortName
	 * @param status
	 * @param parentId
	 */
	public OfficeSubObject(String officeName, String shortName, Short status, Short parentId) {
		// TODO Auto-generated constructor stub
		this.officeName = officeName;
		ShortName = shortName;
		this.status = status;
		this.parentId = parentId;
	}
	/**
	 * This function returns the officeName
	 * @return Returns the officeName.
	 */
	public String getOfficeName() {
		return officeName;
	}
	/**
	 * This function sets the officeName
	 * @param officeName The officeName to set.
	 */
	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}
	/**
	 * This function returns the parentId
	 * @return Returns the parentId.
	 */
	public Short getParentId() {
		return parentId;
	}
	/**
	 * This function sets the parentId
	 * @param parentId The parentId to set.
	 */
	public void setParentId(Short parentId) {
		this.parentId = parentId;
	}
	/**
	 * This function returns the shortName
	 * @return Returns the shortName.
	 */
	public String getShortName() {
		return ShortName;
	}
	/**
	 * This function sets the shortName
	 * @param shortName The shortName to set.
	 */
	public void setShortName(String shortName) {
		ShortName = shortName;
	}
	/**
	 * This function returns the status
	 * @return Returns the status.
	 */
	public Short getStatus() {
		return status;
	}
	/**
	 * This function sets the status
	 * @param status The status to set.
	 */
	public void setStatus(Short status) {
		this.status = status;
	}
	/**
	 * @param status
	 * @param officeType
	 */
	public OfficeSubObject(Short status, Short officeType) {
		// TODO Auto-generated constructor stub
		this.status = status;
		this.officeType = officeType;
	}
	

}
