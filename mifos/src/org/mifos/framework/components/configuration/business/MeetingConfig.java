/**

 * MeetingConfig.java    version: 1.0



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

package org.mifos.framework.components.configuration.business;

import java.util.List;

import org.mifos.config.FiscalCalendarRules;

/**
 *  This class defines methods that tells configuration details about Meeting. 
 *  It has specific methods for specific type of configuration items related to Meeting. 
 *  This class restricts the interface of configuration items specific to Meeting only.  
 *  e.g.  getWeekOffDays(), getSchTypeForMeetingOnHoliday() etc.
 *  <p>
 * This class is a remnant of per-office configuration, which <a
 * href="http://article.gmane.org/gmane.comp.finance.mifos.devel/3498">is
 * deprecated and may be removed</a>.
 */
public class MeetingConfig {

	  public Short getFiscalWeekStartDay() {
		  return FiscalCalendarRules.getStartOfWeek();
	  }

	  public List<Short> getWeekOffDays() {
		  	return FiscalCalendarRules.getWeekDayOffList();
	  }
	  
	  /**
	   * This method returns the schedule type for the meetings that lie on holiday
	   * @return schedule type can have either of (sameday, nextworkingday or nextmeetingday)
	   */
	  public String getSchTypeForMeetingOnHoliday() {
		  return FiscalCalendarRules.getScheduleTypeForMeetingOnHoliday();
	  }
	   
	  /**
	   * This method returns the number of days before the end of current year when calender definition for
	   * next year can be entered.
	   */
	  public Short getDaysForCalDefinition() {
		  return FiscalCalendarRules.getDaysForCalendarDefinition();
	  }
}
