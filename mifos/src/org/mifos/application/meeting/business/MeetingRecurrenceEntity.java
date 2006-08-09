/**

 * MeetingRecurrence.java    version: 1.0

 

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

import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * This class encapsulate the MeetingRecurrence details
 * 
 * @author rajenders
 * 
 */
public class MeetingRecurrenceEntity extends PersistentObject {

	private Integer detailsId;

	private WeekDaysEntity weekDay;

	private RankOfDaysEntity rankOfDays;

	private Short dayNumber;

	private MeetingDetailsEntity meetingDetails;

	public MeetingRecurrenceEntity() {
	}

	public MeetingRecurrenceEntity(MeetingDetailsEntity meetingDetails) {
		this.meetingDetails = meetingDetails;
		this.weekDay = null;
		this.rankOfDays = null;
		this.dayNumber = null;
	}
	
	public MeetingRecurrenceEntity(Short weekDay, Short dayRank, MeetingDetailsEntity meetingDetails) {
		this.meetingDetails = meetingDetails;
		this.weekDay = new WeekDaysEntity(weekDay);
		this.rankOfDays = new RankOfDaysEntity(dayRank);
		this.dayNumber = null;
	}
	
	public Short getDayNumber() {
		return dayNumber;
	}

	public void setDayNumber(Short dayNumber) {
		this.dayNumber = dayNumber;
	}

	public Integer getDetailsId() {
		return detailsId;
	}

	public void setDetailsId(Integer detailsId) {
		this.detailsId = detailsId;
	}

	public MeetingDetailsEntity getMeetingDetails() {
		return meetingDetails;
	}

	public void setMeetingDetails(MeetingDetailsEntity meetingDetails) {
		this.meetingDetails = meetingDetails;
	}

	public RankOfDaysEntity getRankOfDays() {
		return rankOfDays;
	}

	public void setRankOfDays(RankOfDaysEntity rankOfDays) {
		this.rankOfDays = rankOfDays;
	}

	public WeekDaysEntity getWeekDay() {
		return weekDay;
	}

	public void setWeekDay(WeekDaysEntity weekDay) {
		this.weekDay = weekDay;
	}
}
