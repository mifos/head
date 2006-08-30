/**

 * PersonnelLevel.java    version: xxx

 

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
 * This represents the master data of defined personnel levels in the system.
 */
public class PersonnelLevel extends ValueObject{

	/**
     * Simple constructor of Personnel instances.
     */
	public PersonnelLevel() {
		super();
		
	}
	/**Denotes the level of the user */
	private Short levelId;
	
	/**Denotes the interaction flag of this level of the personnel */
	private Short interactionFlag;

	/**Denotes the lokkupid of the personnel */
	private Integer lookUpId;
	
	/**Denotes the parent personnel level of this level */
	private PersonnelLevel parent;


	/**
     * Return the value of the address1.
     * @return Short
     */
	public Short getInteractionFlag() {
		return interactionFlag;
	}

	/**
     * Sets the value of interactionFlag
     * @param interactionFlag
     */
	public void setInteractionFlag(Short interactionFlag) {
		this.interactionFlag = interactionFlag;
	}

	/**
     * Return the value of the levelId.
     * @return Short
     */
	public Short getLevelId() {
		return levelId;
	}

	/**
     * Sets the value of levelId
     * @param levelId
     */
	public void setLevelId(Short levelId) {
		this.levelId = levelId;
	}

	/**
     * Return the value of the lookUpId.
     * @return Integer
     */
	public Integer getLookUpId() {
		return lookUpId;
	}

	/**
     * Sets the value of lookUpId
     * @param lookUpId
     */
	public void setLookUpId(Integer lookUpId) {
		this.lookUpId = lookUpId;
	}


	/**
     * Return the parentLevel for this level.
     * @return PersonnelLevel
     */
	public PersonnelLevel getParent() {
		return parent;
	}

	/**
     * Sets the value of parent
     * @param parent
     */
	public void setParent(PersonnelLevel parent) {
		this.parent = parent;
	}
	  
}
