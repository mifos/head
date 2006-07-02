/**

 * WeekDays.java    version: 1.0

 

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

import java.util.Set;

import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * This class encapsulate the weekDay
 * @author rajenders
 *
 */
public class WeekDays extends ValueObject {
	private static final long serialVersionUID = 5555555555l;
	
	/**
	 * This would hold the weekdayId
	 */
	private Short weekDayId;
	
	/**
	 * This would hold the lookupId
	 */
	private Short lookUpId;
	/**
	 * This would hold the lookupValueLocale for this object
	 */
	private Set lookUpValueLocale;


	private Short workingDay;

	public Short getWorkingDay() {
		return workingDay;
	}

	public void setWorkingDay(Short workingDay) {
		this.workingDay = workingDay;
	}

	/**
	 * This function returns the lookUpId
	 * @return Returns the lookUpId.
	 */
	
	public Short getLookUpId() {
		return lookUpId;
	}

	/**
	 * This function sets the lookUpId
	 * @param lookUpId the lookUpId to set.
	 */
	
	public void setLookUpId(Short lookUpId) {
		this.lookUpId = lookUpId;
	}

	/**
	 * This function returns the lookUpValueLocale
	 * @return Returns the lookUpValueLocale.
	 */
	
	public Set getLookUpValueLocale() {
		return lookUpValueLocale;
	}

	/**
	 * This function sets the lookUpValueLocale
	 * @param lookUpValueLocale the lookUpValueLocale to set.
	 */
	
	public void setLookUpValueLocale(Set lookUpValueLocale) {
		this.lookUpValueLocale = lookUpValueLocale;
	}

	/**
	 * This function returns the weekDayId
	 * @return Returns the weekDayId.
	 */
	
	public Short getWeekDayId() {
		return weekDayId;
	}

	/**
	 * This function sets the weekDayId
	 * @param weekDayId the weekDayId to set.
	 */
	
	public void setWeekDayId(Short weekDayId) {
		this.weekDayId = weekDayId;
	}

	/**
	 * This is default constructor 
	 */
	public WeekDays() {
			}



}
