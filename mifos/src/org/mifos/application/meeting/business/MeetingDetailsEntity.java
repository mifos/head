/**

 * MeetingDetails.java    version: 1.0

 

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
import org.mifos.application.meeting.util.helpers.MeetingConstants;
import org.mifos.application.meeting.util.helpers.RankType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.framework.business.PersistentObject;

/**
 * This class encapsulate the details about the meeting
 */
public class MeetingDetailsEntity extends PersistentObject {

	private final Integer detailsId;

	private final RecurrenceTypeEntity recurrenceType;

	private Short recurAfter;

	private final MeetingRecurrenceEntity meetingRecurrence;

	private final MeetingBO meeting;

	public MeetingDetailsEntity(RecurrenceTypeEntity recurrenceType, Short dayNumber, WeekDay weekDay, RankType rank, Short recurAfter, MeetingBO meeting)throws MeetingException {
		this.validateFields(recurAfter);
		this.recurrenceType = recurrenceType;
		this.recurAfter = recurAfter;
		this.meeting = meeting;
		if(recurrenceType.isWeekly())
			this.meetingRecurrence = new MeetingRecurrenceEntity(weekDay, this);
		else if(recurrenceType.isMonthly())
			this.meetingRecurrence = new MeetingRecurrenceEntity(dayNumber, weekDay, rank, this);			
		else
			this.meetingRecurrence = new MeetingRecurrenceEntity(this);
			
		detailsId = null;
	}
	
	protected MeetingDetailsEntity() {
		detailsId = null;
		recurrenceType = null;
		meetingRecurrence = null;
		meeting = null;
	}
	
	public Short getRecurAfter() {
		return recurAfter;
	}

	public void setRecurAfter(Short recurAfter) {
		this.recurAfter = recurAfter;
	}
	
	public MeetingBO getMeeting() {
		return meeting;
	}

	public MeetingRecurrenceEntity getMeetingRecurrence() {
		return meetingRecurrence;
	}

	public RecurrenceTypeEntity getRecurrenceType() {
		return recurrenceType;
	}

	public boolean isWeekly(){
		return getRecurrenceType().isWeekly();
	}

	public boolean isMonthly(){
		return getRecurrenceType().isMonthly();
	}
	
	public boolean isMonthlyOnDate(){
		return isMonthly() && getMeetingRecurrence().isOnDate();
	}
	
	public WeekDay getWeekDay(){
		return getMeetingRecurrence().getWeekDayValue();
	}
	
	public RankType getWeekRank(){
		return getMeetingRecurrence().getWeekRank();
	}
	
	public Short getDayNumber(){
		return getMeetingRecurrence().getDayNumber();
	}
	
	private void validateFields(Short recurAfter)throws MeetingException{
		if(recurAfter == null || recurAfter<1)
			throw new MeetingException(MeetingConstants.INVALID_RECURAFTER);
	}

	public Integer getDetailsId() {
		return detailsId;
	}
	
	
		
}
