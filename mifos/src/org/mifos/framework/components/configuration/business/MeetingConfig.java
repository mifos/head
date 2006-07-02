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

import org.mifos.framework.components.configuration.cache.CacheRepository;
import org.mifos.framework.components.configuration.util.helpers.ConfigConstants;
import org.mifos.framework.components.configuration.util.helpers.OfficeConfigConstants;

/**
 *  This class defines methods that tells configuration details about Meeting. 
 *  It has specific methods for specific type of configuration items related to Meeting. 
 *  This class restricts the interface of configuration items specific to Meeting only.  
 *  e.g.  getWeekOffDays(), getSchTypeForMeetingOnHoliday() etc.
 */
public class MeetingConfig extends BaseConfig{
	  
	  public MeetingConfig(CacheRepository cacheRepo,OfficeConfig officeConf) {
		  super(cacheRepo,officeConf);
	  }

	  public Short getFiscalWeekStartDay() {
		  return getShortValueFromCache(ConfigConstants.FISCAL_START_OF_WEEK,ConfigConstants.FISCAL_START_OF_WEEK_DEFAULT);
	  }

	  public List<Short> getWeekOffDays() {
		  	return (List<Short>) getValueFromCache(ConfigConstants.WEEK_OFF_LIST);
	  }
	  
	  /**
	   * This method returns the schedule type for the meetings that lie on holiday
	   * @return schedule type can have either of (sameday, nextworkingday or nextmeetingday)
	   */
	  public String getSchTypeForMeetingOnHoliday() {
		  return getStringValueFromCache(OfficeConfigConstants.SCHEDULE_TYPE_FOR_MEETING_ON_HOLIDAY, ConfigConstants.MEETING_SCHEDULE_TYPE_DEFAULT);
	  }
	   
	  /**
	   * This method returns the number of days before the end of current year when calender definition for
	   * next year can be entered.
	   */
	  public Short getDaysForCalDefinition() {
		  return getShortValueFromCache(OfficeConfigConstants.DAYS_FOR_CAL_DEFINITION,ConfigConstants.DAYS_FOR_CAL_DEFINITION_DEFAULT);
	  }
}
