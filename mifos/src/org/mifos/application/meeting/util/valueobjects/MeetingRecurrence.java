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
package org.mifos.application.meeting.util.valueobjects;

import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * This class encapsulate the MeetingRecurrence details
 * @author rajenders
 *
 */
public class MeetingRecurrence extends ValueObject {
	private static final long serialVersionUID = 55555345353455l;
	/**
	 * This would hold the details id
	 */
	private Integer detailsId;
	/**
	 * This would hold the week day
	 */
	private Short weekDay;
	/**
	 * This would hold the RankOfDay
	 */
	private Short rankOfDays;
	/**
	 * This would hold the day number 
	 */
	private Short dayNumber;
	/**
	 * This would hold the versionNo 
	 */
	private Integer versionNo;
	/**
	 * This function returns the versionNo
	 * @return Returns the versionNo.
	 */
	private MeetingDetails meetingDetails;
	public Integer getVersionNo() {
		return versionNo;
	}
	/**
	 * This function sets the versionNo
	 * @param versionNo the versionNo to set.
	 */
	
	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
	}
	/**
	 * This function returns the dayNumber
	 * @return Returns the dayNumber.
	 */
	
	public Short getDayNumber() {
		return dayNumber;
	}
	/**
	 * This function sets the dayNumber
	 * @param dayNumber the dayNumber to set.
	 */
	
	public void setDayNumber(Short dayNumber) {
		this.dayNumber = dayNumber;
	}
	/**
	 * This function returns the detailsId
	 * @return Returns the detailsId.
	 */
	
	public Integer getDetailsId() {
		return detailsId;
	}
	/**
	 * This function sets the detailsId
	 * @param detailsId the detailsId to set.
	 */
	
	public void setDetailsId(Integer detailsId) {
		this.detailsId = detailsId;
	}
	/**
	 * This function returns the rankOfDays
	 * @return Returns the rankOfDays.
	 */
	
	public Short getRankOfDays() {
		return rankOfDays;
	}
	/**
	 * This function sets the rankOfDays
	 * @param rankOfDays the rankOfDays to set.
	 */
	
	public void setRankOfDays(Short rankOfDays) {
		this.rankOfDays = rankOfDays;
	}
	/**
	 * This function returns the weekDay
	 * @return Returns the weekDay.
	 */
	
	public Short getWeekDay() {
		return weekDay;
	}
	/**
	 * This function sets the weekDay
	 * @param weekDay the weekDay to set.
	 */
	
	public void setWeekDay(Short weekDay) {
		this.weekDay = weekDay;
	}
	/**
	 * Default constructor 
	 */
	public MeetingRecurrence() {

	}
	/**
	 * This function returns the meetingDetails
	 * @return Returns the meetingDetails.
	 */
	
	public MeetingDetails getMeetingDetails() {
		return meetingDetails;
	}
	/**
	 * This function sets the meetingDetails
	 * @param meetingDetails the meetingDetails to set.
	 */
	
	public void setMeetingDetails(MeetingDetails meetingDetails) {
		this.meetingDetails = meetingDetails;
	}
	


}
