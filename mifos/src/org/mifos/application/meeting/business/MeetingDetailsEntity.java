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

import org.mifos.framework.business.PersistentObject;

/**
 * This class encapsulate the details about the meeting
 * 
 * @author rajenders
 * 
 */
public class MeetingDetailsEntity extends PersistentObject {

	private Integer detailsId;

	private RecurrenceTypeEntity recurrenceType;

	private Short recurAfter;

	private MeetingRecurrenceEntity meetingRecurrence;

	private MeetingBO meeting;

	public MeetingDetailsEntity() {
		meetingRecurrence = new MeetingRecurrenceEntity();
		recurrenceType = new RecurrenceTypeEntity();
	}
	
	public MeetingDetailsEntity(RecurrenceTypeEntity recurrenceType, Short recurAfter, MeetingBO meeting) {
		this.recurrenceType = recurrenceType;
		this.recurAfter = recurAfter;
		this.meeting = meeting;
		this.meetingRecurrence = new MeetingRecurrenceEntity(this);
	}
	
	public MeetingDetailsEntity(RecurrenceTypeEntity recurrenceType, Short recurAfter, Short weekDay, Short dayRank, MeetingBO meeting) {
		this.recurrenceType = recurrenceType;
		this.recurAfter = recurAfter;
		this.meeting = meeting;
		this.meetingRecurrence = new MeetingRecurrenceEntity(weekDay, dayRank, this);
	}
	
	public MeetingBO getMeeting() {
		return meeting;
	}

	public void setMeeting(MeetingBO meeting) {
		this.meeting = meeting;
	}

	public Integer getDetailsId() {
		return detailsId;
	}

	public void setDetailsId(Integer detailsId) {
		this.detailsId = detailsId;
	}

	public MeetingRecurrenceEntity getMeetingRecurrence() {
		return meetingRecurrence;
	}

	public void setMeetingRecurrence(MeetingRecurrenceEntity meetingRecurrence) {
		if (null != meetingRecurrence) {
			meetingRecurrence.setMeetingDetails(this);
		}

		this.meetingRecurrence = meetingRecurrence;
	}

	public Short getRecurAfter() {
		return recurAfter;
	}

	public void setRecurAfter(Short recurAfter) {
		this.recurAfter = recurAfter;
	}

	public RecurrenceTypeEntity getRecurrenceType() {
		return recurrenceType;
	}

	public void setRecurrenceType(RecurrenceTypeEntity recurrenceType) {
		this.recurrenceType = recurrenceType;
	}

	public boolean isWeekly(){
		return getRecurrenceType().isWeekly();
	}

	public boolean isMonthly(){
		return getRecurrenceType().isMonthly();
	}
}
