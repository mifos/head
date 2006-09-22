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

import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.RankType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.meeting.util.resources.MeetingConstants;
import org.mifos.framework.business.PersistentObject;

/**
 * This class encapsulate the MeetingRecurrence details
 */
public class MeetingRecurrenceEntity extends PersistentObject {

	private final Integer detailsId;

	private WeekDaysEntity weekDay;

	private RankOfDaysEntity rankOfDays;

	private Short dayNumber;

	private final MeetingDetailsEntity meetingDetails;

	public MeetingRecurrenceEntity(MeetingDetailsEntity meetingDetails) {
		this.meetingDetails = meetingDetails;
		this.weekDay = null;
		this.rankOfDays = null;
		this.dayNumber = null;
		this.detailsId = null;
	}
	
	public MeetingRecurrenceEntity(Short dayNumber, WeekDay weekDay, RankType rank, MeetingDetailsEntity meetingDetails) throws MeetingException {
		validateFields(dayNumber, weekDay, rank);
		if(dayNumber!=null)
			this.dayNumber = dayNumber;
		else{
			this.weekDay = new WeekDaysEntity(weekDay);
			this.rankOfDays = new RankOfDaysEntity(rank);	
		}
		this.meetingDetails = meetingDetails;
		this.detailsId = null;
	}
	
	public MeetingRecurrenceEntity(WeekDay weekDay, MeetingDetailsEntity meetingDetails)throws MeetingException {
		validateWeekDay(weekDay);
		this.weekDay = new WeekDaysEntity(weekDay);
		this.meetingDetails = meetingDetails;
		this.detailsId = null;
	}
	
	protected MeetingRecurrenceEntity() {
		detailsId = null;
		meetingDetails = null;
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
	
	public MeetingDetailsEntity getMeetingDetails() {
		return meetingDetails;
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
	
	public boolean isOnDate(){
		return getDayNumber()!=null;
	}
	
	public WeekDay getWeekDayValue() {
		return weekDay!=null ? WeekDay.getWeekDay(weekDay.getId()) : null;
	}
	
		
	public RankType getWeekRank() {
		return rankOfDays!=null ? RankType.getRankType(rankOfDays.getId()) : null;
	}
	
	public void updateDayNumber(Short dayNumber)throws MeetingException {
		validateDayNumber(dayNumber);
		this.dayNumber = dayNumber;
	}
	
	public void updateWeekDay(WeekDay weekDay)throws MeetingException {
		validateWeekDay(weekDay);
		this.weekDay = new WeekDaysEntity(weekDay);
	}
	
	public void update(WeekDay weekDay, RankType rank)throws MeetingException {
		if(weekDay==null || rank==null)
			throw new MeetingException(MeetingConstants.INVALID_WEEKDAY_OR_WEEKRANK);
		this.weekDay = new WeekDaysEntity(weekDay);
		this.rankOfDays = new RankOfDaysEntity(rank);
	}
	
	private void validateWeekDay(WeekDay weekDay) throws MeetingException{
		if(weekDay == null)
			throw new MeetingException(MeetingConstants.INVALID_WEEKDAY);
	}
	
	private void validateFields(Short dayNumber, WeekDay weekDay, RankType rank) throws MeetingException{
		validateDayNumber(dayNumber);
		
		if(dayNumber==null && weekDay==null && rank==null)
			throw new MeetingException(MeetingConstants.INVALID_DAYNUMBER_OR_WEEK);

		if(dayNumber==null && (weekDay==null || rank==null))
			throw new MeetingException(MeetingConstants.INVALID_WEEKDAY_OR_WEEKRANK);
	}
	
	private void validateDayNumber(Short dayNumber)throws MeetingException{
		if(dayNumber!=null && (dayNumber<1 || dayNumber>31))
			throw new MeetingException(MeetingConstants.INVALID_DAYNUMBER);
	}
}
