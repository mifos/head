/**

 * Meeting.java    version: 1.0

 

 * Copyright © 2005-2006 Grameen Foundation USA

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

import java.util.Calendar;

import org.mifos.application.meeting.util.resources.MeetingConstants;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * This class encapsulate the meeting
 * 
 * @author rajenders
 * 
 */
public class Meeting extends ValueObject {
	private static final long serialVersionUID = 44l;

	/**
	 * This would hold the meeting id
	 */
	private Integer meetingId;

	/**
	 * This would hold the MeetingDetails for this meeting
	 */

	private MeetingDetails meetingDetails;

	/**
	 * This would hold the MeetingType for this meeting
	 */
	private MeetingType meetingType;

	/**
	 * This would hold the meeting palace
	 */
	private String meetingPlace;

	/**
	 * This would hold the MeetingStartDate
	 */
	private Calendar meetingStartDate;

	/**
	 * This would hold the MeetingEndDate
	 */
	private Calendar meetingEndDate;

	/**
	 * This would hold the MeetingStartTime
	 */
	private Calendar meetingStartTime;

	/**
	 * This would hold the meeting end time
	 */
	private Calendar meetingEndTime;

	/**
	 * This would hold the versionNo
	 */
	private Integer versionNo;

	/**
	 * Default constructor for Meeting
	 */
	public Meeting() {
		meetingDetails = new MeetingDetails();
		meetingType = new MeetingType();
	}

	/**
	 * This function returns the meetingDetails
	 * 
	 * @return Returns the meetingDetails.
	 */

	public MeetingDetails getMeetingDetails() {
		return meetingDetails;
	}

	/**
	 * This function sets the meetingDetails
	 * 
	 * @param meetingDetails
	 *            the meetingDetails to set.
	 */

	public void setMeetingDetails(MeetingDetails meetingDetails) {

		if (null != meetingDetails) {
			meetingDetails.setMeeting(this);
		}
		this.meetingDetails = meetingDetails;
	}

	/**
	 * This function returns the meetingEndDate
	 * 
	 * @return Returns the meetingEndDate.
	 */

	public Calendar getMeetingEndDate() {
		return meetingEndDate;
	}

	/**
	 * This function sets the meetingEndDate
	 * 
	 * @param meetingEndDate
	 *            the meetingEndDate to set.
	 */

	public void setMeetingEndDate(Calendar meetingEndDate) {
		this.meetingEndDate = meetingEndDate;
	}

	/**
	 * This function returns the meetingId
	 * 
	 * @return Returns the meetingId.
	 */

	public Integer getMeetingId() {
		return meetingId;
	}

	/**
	 * This function sets the meetingId
	 * 
	 * @param meetingId
	 *            the meetingId to set.
	 */

	public void setMeetingId(Integer meetingId) {
		this.meetingId = meetingId;
	}

	/**
	 * This function returns the meetingPlace
	 * 
	 * @return Returns the meetingPlace.
	 */

	public String getMeetingPlace() {
		return meetingPlace;
	}

	/**
	 * This function sets the meetingPlace
	 * 
	 * @param meetingPlace
	 *            the meetingPlace to set.
	 */

	public void setMeetingPlace(String meetingPlace) {
		this.meetingPlace = meetingPlace;
	}

	/**
	 * This function returns the meetingStartDate
	 * 
	 * @return Returns the meetingStartDate.
	 */

	public Calendar getMeetingStartDate() {
		return meetingStartDate;
	}

	/**
	 * This function sets the meetingStartDate
	 * 
	 * @param meetingStartDate
	 *            the meetingStartDate to set.
	 */

	public void setMeetingStartDate(Calendar meetingStartDate) {
		this.meetingStartDate = meetingStartDate;
	}

	/**
	 * This function returns the meetingStartTime
	 * 
	 * @return Returns the meetingStartTime.
	 */

	public Calendar getMeetingStartTime() {
		return meetingStartTime;
	}

	/**
	 * This function sets the meetingStartTime
	 * 
	 * @param meetingStartTime
	 *            the meetingStartTime to set.
	 */

	public void setMeetingStartTime(Calendar meetingStartTime) {
		this.meetingStartTime = meetingStartTime;
	}

	/**
	 * This function returns the meetingType
	 * 
	 * @return Returns the meetingType.
	 */

	public MeetingType getMeetingType() {
		return meetingType;
	}

	/**
	 * This function sets the meetingType
	 * 
	 * @param meetingType
	 *            the meetingType to set.
	 */

	public void setMeetingType(MeetingType meetingType) {
		this.meetingType = meetingType;
	}

	/**
	 * This function returns the versionNo
	 * 
	 * @return Returns the versionNo.
	 */

	public Integer getVersionNo() {
		return versionNo;
	}

	/**
	 * This function sets the versionNo
	 * 
	 * @param versionNo
	 *            the versionNo to set.
	 */

	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
	}

	/**
	 * This function returns the meetingEndTime
	 * 
	 * @return Returns the meetingEndTime.
	 */

	public Calendar getMeetingEndTime() {
		return meetingEndTime;
	}

	/**
	 * This function sets the meetingEndTime
	 * 
	 * @param meetingEndTime
	 *            the meetingEndTime to set.
	 */

	public void setMeetingEndTime(Calendar meetingEndTime) {
		this.meetingEndTime = meetingEndTime;
	}

	/**
	 * This function returns the meeting schedule based on the values
	 * set in the meeting object
	 * @return
	 */
	public String getMeetingSchedule() {
		String meeetingInfo = "";
		if (null != this.meetingDetails) {
			RecurrenceType rt = this.meetingDetails.getRecurrenceType();
			if (null != rt) {
				if (null != rt.getRecurrenceId()
						&& rt.getRecurrenceId().shortValue() == MeetingConstants.WEEK) {

					meeetingInfo = "Recurs every "
							+ this.meetingDetails.getRecurAfter()
							+ " Week(s) "
							+ " on "
							+ getDayString(this.meetingDetails
									.getMeetingRecurrence().getWeekDay());

				} else if (null != rt.getRecurrenceId()
						&& rt.getRecurrenceId().shortValue() == MeetingConstants.MONTH) {
					MeetingRecurrence mr = this.meetingDetails
							.getMeetingRecurrence();
					Short dayNumber = mr.getDayNumber();
					Short day = mr.getWeekDay();
					Short rank = mr.getRankOfDays();

					if (null != dayNumber) {
						meeetingInfo = "Recurs on day " + dayNumber.shortValue()
								+ " of every "
								+ this.meetingDetails.getRecurAfter()
								+ " month(s) ";
					} else {
						// bug 27987 -- added months at the end
						meeetingInfo = "Recurs on " + getRankString(rank) + " "
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
	 * This function returns the meeting schedule based on the values
	 * set in the meeting object
	 * @return
	 */
	
	
	public String getSimpleMeetingSchedule() {
		String meeetingInfo = "";
		if (null != this.meetingDetails) {
			RecurrenceType rt = this.meetingDetails.getRecurrenceType();
			if (null != rt) {
				if (null != rt.getRecurrenceId()
						&& rt.getRecurrenceId().shortValue() == MeetingConstants.WEEK) {

					meeetingInfo = "Recurs every "
							+ this.meetingDetails.getRecurAfter() + " Week(s) ";

				} else if (null != rt.getRecurrenceId()
						&& rt.getRecurrenceId().shortValue() == MeetingConstants.MONTH) {
					meeetingInfo = "Recurs  every "
							+ this.meetingDetails.getRecurAfter()
							+ " month(s) ";
				}
			}
		}
		return meeetingInfo;
	}

	/**
	 * This function returns the meeting schedule based on the values
	 * set in the meeting object
	 * @return String
	 */
	public String getFeeMeetingSchedule() {
		String meeetingInfo = "";
		if (null != this.meetingDetails) {
			RecurrenceType rt = this.meetingDetails.getRecurrenceType();
			if (null != rt) {
				if (null != rt.getRecurrenceId()
						&& rt.getRecurrenceId().shortValue() == MeetingConstants.WEEK) {

					meeetingInfo = this.meetingDetails.getRecurAfter() + " Week(s) ";

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
}
