/**

 * PersonnelStatus.java    version: 1.0

 

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

import java.util.Set;

import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * This obect denotes status of the personnel.  
 */
public class PersonnelStatus extends ValueObject{
	
	/**Denotes the primary key. It is running , auto generated number */
	private Short personnelStatusId;
	
	/**Denotes the lookUpId of the status represented by this object */
	private Integer lookUpId;
	
	/**Denotes the set of values of the status in different locales */
	private Set lookUpValueLocale;
	
	/**
     * Simple constructor of PersonnelStatus instances.
     */
	public PersonnelStatus() {
		super();
		
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
     * Return the value of the personnelStatusId.
     * @return Short
     */
	public Short getPersonnelStatusId() {
		return personnelStatusId;
	}
	
	/**
     * Sets the value of the personnelStatusId.
     * @param personnel
     */
	public void setPersonnelStatusId(Short personnelStatusId) {
		this.personnelStatusId = personnelStatusId;
	}
	
	/**
     * Sets the value of the lookUpValueLocale.
     * @param lookUpValueLocale
     */
	public void setLookUpValueLocale(Set lookUpValueLocale){
		  this.lookUpValueLocale = lookUpValueLocale;
	}
	
	/**
     * Return the value of the lookUpValueLocale.
     * @return Set
     */
    public Set getLookUpValueLocale(){
		  return lookUpValueLocale;
    }
}
