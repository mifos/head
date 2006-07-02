/**

 * RecurrenceType.java    version: 1.0

 

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
package org.mifos.application.meeting.util.valueobjects;

import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * This class encapsulate the RecurrenceType for a meeting 
 * @author rajenders
 *
 */
public class RecurrenceType extends ValueObject {
	private static final long serialVersionUID = 5555555555l;

	/**
	 * This would hold the recurrencetype id for this recurrence
	 */
	private Short recurrenceId;
	/**
	 * This would hold the recurrenceName for this object
	 */

	private String recurrenceName;
	
	/**
	 * This  would hold the description
	 *
	 */
	
	private String description;
	/**
	 * This function returns the description
	 * @return Returns the description.
	 */
	
	public String getDescription() {
		return description;
	}
	/**
	 * This function sets the description
	 * @param description the description to set.
	 */
	
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * This function returns the recurrenceId
	 * @return Returns the recurrenceId.
	 */
	
	public Short getRecurrenceId() {
		return recurrenceId;
	}
	/**
	 * This function sets the recurrenceId
	 * @param recurrenceId the recurrenceId to set.
	 */
	
	public void setRecurrenceId(Short recurrenceId) {
		this.recurrenceId = recurrenceId;
	}
	/**
	 * This function returns the recurrenceName
	 * @return Returns the recurrenceName.
	 */
	
	public String getRecurrenceName() {
		return recurrenceName;
	}
	/**
	 * This function sets the recurrenceName
	 * @param recurrenceName the recurrenceName to set.
	 */
	
	public void setRecurrenceName(String recurrenceName) {
		this.recurrenceName = recurrenceName;
	}
	public RecurrenceType() {
		
	}



}
