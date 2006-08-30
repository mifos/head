/**

 * UserSearchResults.java    version: 1.0

 

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
package org.mifos.application.personnel.util.valueobjects;

import java.io.Serializable;

/**
 * This obect denotes instance of search result, that will be obtained 
 * after user search.  
 */
public class UserSearchResults implements Serializable{
	
	/**Denotes the officeId of the user */
	short officeId;
	
	/**Denotes the office name of the user */
	String officeName;
	
	/**Denotes the id of the user */
	short personnelId;
	
	/**Denotes the system of the user */
	String globalPersonnelNum;
	
	/**Denotes the name of the user */
	String personnelName;
	
	/**
     * Return the value of the globalPersonnelNum.
     * @return String
     */
	public String getGlobalPersonnelNum() {
		return globalPersonnelNum;
	}
	
	/**
     * Sets the value of globalPersonnelNum
     * @param globalPersonnelNum
     */
	public void setGlobalPersonnelNum(String globalPersonnelNum) {
		this.globalPersonnelNum = globalPersonnelNum;
	}
	
	/**
     * Return the value of the officeId.
     * @return short
     */
	public short getOfficeId() {
		return officeId;
	}
	
	/**
     * Sets the value of officeId
     * @param officeId
     */
	public void setOfficeId(short officeId) {
		this.officeId = officeId;
	}
	
	/**
     * Return the value of the officeName.
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
     * Return the value of the personnelId.
     * @return short
     */
	public short getPersonnelId() {
		return personnelId;
	}
	
	/**
     * Sets the value of personnelId
     * @param personnelId
     */
	public void setPersonnelId(short personnelId) {
		this.personnelId = personnelId;
	}
	
	/**
     * Return the value of the personnelName.
     * @return String
     */
	public String getPersonnelName() {
		return personnelName;
	}
	
	/**
     * Sets the value of personnelName
     * @param personnelName
     */
	public void setPersonnelName(String personnelName) {
		this.personnelName = personnelName;
	}
}
