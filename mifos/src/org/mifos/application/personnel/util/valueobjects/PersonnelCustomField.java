/**

 * PersonnelCustomField.java    version: 1.0

 

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
 * This object has the values for a personnel's custom fields.
 * This has an association with the personnel object.  
 */
public class PersonnelCustomField extends ValueObject{

	/** Simple constructor of PersonnelCustomField instances.
	 */
	public PersonnelCustomField() {
		super();
		
	}
	/**Denotes the the field id.*/
	 private Short fieldId;
	 
	 /**Denotes the the field value entered by user.*/
	 private String fieldValue;
	 
	 /**Denotes the the personnel to which this field value is related.*/
	 private Short personnelId;


	/**
     * Return the value of the fieldValue.
     * @return String
     */
	public String getFieldValue() {
		return fieldValue;
	}

	/**
     * Sets the value of fieldValue 
     * @param fieldValue
     */
	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}

	/**
     * Return the value of the fieldId.
     * @return Short
     */
	public Short getFieldId() {
		return fieldId;
	}


	/**
     * Sets the value of fieldId 
     * @param fieldId
     */
	public void setFieldId(Short fieldId) {
		this.fieldId = fieldId;
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
	  

}
