/**
 
 * PersonnelMaster.java    version: xxx
 
 
 
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

import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * This represents a lightweight personnel object which could be used in places where entire personnel object would be an overhead .
 * for e.g. displaying a list of personnels in a combo box in the UI. 
 */
public class PersonnelMaster extends ValueObject {
	
	/**
     * Simple constructor of PersonnelMaster instances.
     */
	public PersonnelMaster() {
		super();
		
	}
	
	/**
     * Constructor that takes personnelId as parameter
     * @param personnelId.
     */
	public PersonnelMaster(Short personnelId) {
		this.personnelId = personnelId;
		
	}
	
	/**
     * Constructor 
     * @param personnelId.
     * @param displayName.
     */
	public PersonnelMaster(Short personnelId,String displayName) {
		this.personnelId = personnelId;
		this.displayName = displayName;
		
	}
	
	/**
     * Constructor 
     * @param personnelId.
     * @param displayName.
     * @param versionNo.
     */
	public PersonnelMaster(Short personnelId,String displayName , Integer versionNo) {
		this.personnelId = personnelId;
		this.displayName = displayName;
		this.versionNo = versionNo;
		
	}
	/** denotes the id of the personnel*/
	private Short personnelId;
	
	/** denotes the system id of the personnel*/
	private String globalPersonnelNum;
	
	/** denotes the display name of the personnel. It is concatenation of first, second , second last and last names*/
	private String displayName;
	
	/** denotes the version no of the personnel*/
	private Integer versionNo;
	
	
	/**
     * Return the value of the displayName.
     * @return String
     */
	public String getDisplayName() {
		return displayName;
	}
	
	
	/**
     * Sets the value of displayName 
     * @param displayName
     */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
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
     * Return the value of the personnelId.
     * @return Short
     */
	public Short getPersonnelId() {
		return personnelId;
	}
	
	/**
     * Sets the value of personnelId 
     * @param personnelId
     */
	public void setPersonnelId(Short personnelId) {
		this.personnelId = personnelId;
	}

	/**
     * Return the value of the versionNo.
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
	
	
}
