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
package org.mifos.application.meeting.business;

import java.util.Set;

import org.mifos.application.master.util.valueobjects.LookUpValueLocale;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * This class encapsulate the weekDay
 */
public class WeekDaysEntity extends PersistentObject {
	
	private Short weekDayId;

	private Short lookUpId;

	private Set<LookUpValueLocale> lookUpValueLocale;

	private Short workingDay;
	
	private Short startOfFiscalWeek;
	
	public WeekDaysEntity() {
	}

	public WeekDaysEntity(Short weekDay) {
		this.weekDayId = weekDay;
	}
	
	public Short getLookUpId() {
		return lookUpId;
	}

	public void setLookUpId(Short lookUpId) {
		this.lookUpId = lookUpId;
	}

	public Set<LookUpValueLocale> getLookUpValueLocale() {
		return lookUpValueLocale;
	}

	public void setLookUpValueLocale(Set<LookUpValueLocale> lookUpValueLocale) {
		this.lookUpValueLocale = lookUpValueLocale;
	}

	public Short getWeekDayId() {
		return weekDayId;
	}

	public void setWeekDayId(Short weekDayId) {
		this.weekDayId = weekDayId;
	}

	public Short getStartOfFiscalWeek() {
		return startOfFiscalWeek;
	}

	public void setStartOfFiscalWeek(Short startOfFiscalWeek) {
		this.startOfFiscalWeek = startOfFiscalWeek;
	}

	public Short getWorkingDay() {
		return workingDay;
	}

	public void setWorkingDay(Short workingDay) {
		this.workingDay = workingDay;
	}
	
	public boolean isWorkingDay(){
		return (workingDay!=null&& workingDay.equals(Constants.NO)) ? false : true;
	}
	
	public boolean isStartOfFiscalWeek(){
		return (startOfFiscalWeek!=null&& startOfFiscalWeek.equals(Constants.YES)) ? true : false;
	}
}
