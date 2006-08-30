/**

 * Meeting.java    version: 1.0

 

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

import java.util.Calendar;

import org.mifos.application.meeting.util.helpers.MeetingFrequency;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.resources.MeetingConstants;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.struts.plugin.helper.EntityMasterConstants;

/**
 * This class encapsulate the meeting
 */
public class MeetingBO extends BusinessObject {
	
	private Integer meetingId;

	private MeetingDetailsEntity meetingDetails;

	private MeetingTypeEntity meetingType;

	private String meetingPlace;

	private Calendar meetingStartDate;

	private Calendar meetingEndDate;

	private Calendar meetingStartTime;

	private Calendar meetingEndTime;

	public MeetingBO() {
		meetingDetails = new MeetingDetailsEntity();
		meetingType = new MeetingTypeEntity();
	}

	public MeetingBO(MeetingFrequency meetingRecurrence, Short recurAfter, MeetingType meetingType){
		RecurrenceTypeEntity recurrenceType = new RecurrenceTypeEntity(meetingRecurrence);
		this.meetingDetails =  new MeetingDetailsEntity(recurrenceType, recurAfter, this);
		this.meetingType = new MeetingTypeEntity(meetingType);
	}
	
	public MeetingBO(MeetingFrequency meetingRecurrence, Short recurAfter, MeetingType meetingType, Short weekDay, Short dayRank){
		RecurrenceTypeEntity recurrenceType = new RecurrenceTypeEntity(meetingRecurrence);
		this.meetingDetails =  new MeetingDetailsEntity(recurrenceType, recurAfter, weekDay, dayRank, this);
		this.meetingType = new MeetingTypeEntity(meetingType);
	}
	
	public MeetingDetailsEntity getMeetingDetails() {
		return meetingDetails;
	}

	public void setMeetingDetails(MeetingDetailsEntity meetingDetails) {

		if (null != meetingDetails) {
			meetingDetails.setMeeting(this);
		}
		this.meetingDetails = meetingDetails;
	}

	public Calendar getMeetingEndDate() {
		return meetingEndDate;
	}

	public void setMeetingEndDate(Calendar meetingEndDate) {
		this.meetingEndDate = meetingEndDate;
	}

	public Integer getMeetingId() {
		return meetingId;
	}

	public void setMeetingId(Integer meetingId) {
		this.meetingId = meetingId;
	}

	public String getMeetingPlace() {
		return meetingPlace;
	}

	public void setMeetingPlace(String meetingPlace) {
		this.meetingPlace = meetingPlace;
	}

	public Calendar getMeetingStartDate() {
		return meetingStartDate;
	}

	public void setMeetingStartDate(Calendar meetingStartDate) {
		this.meetingStartDate = meetingStartDate;
	}

	public Calendar getMeetingStartTime() {
		return meetingStartTime;
	}

	public void setMeetingStartTime(Calendar meetingStartTime) {
		this.meetingStartTime = meetingStartTime;
	}

	public MeetingTypeEntity getMeetingType() {
		return meetingType;
	}

	public void setMeetingType(MeetingTypeEntity meetingType) {
		this.meetingType = meetingType;
	}

	public Calendar getMeetingEndTime() {
		return meetingEndTime;
	}

	public void setMeetingEndTime(Calendar meetingEndTime) {
		this.meetingEndTime = meetingEndTime;
	}

	/**
	 * This function returns the meeting schedule based on the values set in the
	 * meeting object
	 * 
	 * @return
	 */
	public String getMeetingSchedule() {
		String meeetingInfo = "";
		if (null != this.meetingDetails) {
			RecurrenceTypeEntity rt = this.meetingDetails.getRecurrenceType();
			if (null != rt) {
				if (null != rt.getRecurrenceId()
						&& rt.getRecurrenceId().shortValue() == MeetingConstants.WEEK) {

					meeetingInfo = "Recur every "
							+ this.meetingDetails.getRecurAfter()
							+ " Week(s) "
							+ " on "
							+ getDayString(this.meetingDetails
									.getMeetingRecurrence().getWeekDay().getWeekDayId());

				} else if (null != rt.getRecurrenceId()
						&& rt.getRecurrenceId().shortValue() == MeetingConstants.MONTH) {
					MeetingRecurrenceEntity mr = this.meetingDetails
							.getMeetingRecurrence();
					Short dayNumber = mr.getDayNumber();
					Short day = null;
					Short rank = null;
					if(mr.getWeekDay()!=null)
						day = mr.getWeekDay().getWeekDayId();
					if(mr.getRankOfDays()!=null)
						rank = mr.getRankOfDays().getRankOfDayId();

					if (null != dayNumber) {
						meeetingInfo = "Recur on day " + dayNumber.shortValue()
								+ " of every "
								+ this.meetingDetails.getRecurAfter()
								+ " month(s) ";
					} else {
						// bug 27987 -- added months at the end
						meeetingInfo = "Recur on " + getRankString(rank) + " "
								+ getDayString(day) + " of every "
								+ this.meetingDetails.getRecurAfter()
								+ " month(s) ";
					}

				}
			}
		}
		return meeetingInfo;
	}

	/**
	 * This function returns the meeting schedule based on the values set in the
	 * meeting object
	 * 
	 * @return
	 */

	public String getSimpleMeetingSchedule() {
		String meeetingInfo = "";
		if (null != this.meetingDetails) {
			RecurrenceTypeEntity rt = this.meetingDetails.getRecurrenceType();
			if (null != rt) {
				if (null != rt.getRecurrenceId()
						&& rt.getRecurrenceId().shortValue() == MeetingConstants.WEEK) {

					meeetingInfo = "Periodicity: Recur every "
							+ this.meetingDetails.getRecurAfter() + " Week(s) ";

				} else if (null != rt.getRecurrenceId()
						&& rt.getRecurrenceId().shortValue() == MeetingConstants.MONTH) {
					meeetingInfo = "Periodicity: Recur  every "
							+ this.meetingDetails.getRecurAfter()
							+ " month(s) ";
				}
			}
		}
		return meeetingInfo;
	}

	/**
	 * This function returns the meeting schedule based on the values set in the
	 * meeting object
	 * 
	 * @return String
	 */
	public String getShortMeetingSchedule() {
		String meeetingInfo = "";
		if (null != this.meetingDetails) {
			RecurrenceTypeEntity rt = this.meetingDetails.getRecurrenceType();
			if (null != rt) {
				if (null != rt.getRecurrenceId()
						&& rt.getRecurrenceId().shortValue() == MeetingConstants.WEEK) {

					meeetingInfo = this.meetingDetails.getRecurAfter()
							+ " week(s) ";

				} else if (null != rt.getRecurrenceId()
						&& rt.getRecurrenceId().shortValue() == MeetingConstants.MONTH) {
					meeetingInfo = this.meetingDetails.getRecurAfter()
							+ " month(s) ";
				}
			}
		}
		return meeetingInfo;
	}

	/**
	 * This function returns the day of week based on the passed value
	 * 
	 * @param day
	 * @return
	 */
	private String getDayString(Short day) {
		String weekDay = "";
		switch (day.shortValue()) {
		case 1:
			weekDay = "Sunday";
			break;
		case 2:
			weekDay = "Monday";
			break;
		case 3:
			weekDay = "Tuesday";
			break;
		case 4:
			weekDay = "Wednesday";
			break;
		case 5:
			weekDay = "Thrusday";
			break;
		case 6:
			weekDay = "Friday";
			break;
		case 7:
			weekDay = "Saturday";
			break;

		}
		return weekDay;
	}

	/**
	 * This function returns the rank string for the day
	 * 
	 * @param rank
	 * @return
	 */
	private String getRankString(Short rank) {
		String rankString = "";
		switch (rank.shortValue()) {
		case 1:
			rankString = "First";
			break;
		case 2:
			rankString = "Second";
			break;

		case 3:
			rankString = "Third";
			break;
		case 4:
			rankString = "Forth";
			break;
		case 5:
			rankString = "Last";
			break;

		}

		return rankString;

	}


	
	public boolean isWeekly(){
		return getMeetingDetails().isWeekly();
	}

	public boolean isMonthly(){
		return getMeetingDetails().isMonthly();
	}

}
