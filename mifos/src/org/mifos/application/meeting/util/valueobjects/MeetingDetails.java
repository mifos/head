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
package org.mifos.application.meeting.util.valueobjects;

import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * @author rajenders
 * 
 */
public class MeetingDetails extends ValueObject {
	private static final long serialVersionUID = 55555345353455l;

	/**
	 * This would hold the detailsId
	 */
	private Integer detailsId;

	/**
	 * This would hold the RecurrenceType
	 */

	private RecurrenceType recurrenceType;

	/**
	 * This would hold the recurAfter for the meeting
	 */
	private Short recurAfter;

	/**
	 * This would hold the MeetingRecurrence
	 */
	private MeetingRecurrence meetingRecurrence;

	/**
	 * This would hold the versionNo for the meeting details
	 */

	private Integer versionNo;

	/**
	 * Default constructor for meetingDetails
	 */

	private Meeting meeting;

	/**
	 * This function returns the meeting
	 * 
	 * @return Returns the meeting.
	 */

	public Meeting getMeeting() {
		return meeting;
	}

	/**
	 * This function sets the meeting
	 * 
	 * @param meeting
	 *            the meeting to set.
	 */

	public void setMeeting(Meeting meeting) {
		this.meeting = meeting;
	}

	public MeetingDetails() {

		meetingRecurrence = new MeetingRecurrence();
		recurrenceType = new RecurrenceType();

	}

	/**
	 * This function returns the detailsId
	 * 
	 * @return Returns the detailsId.
	 */

	public Integer getDetailsId() {
		return detailsId;
	}

	/**
	 * This function sets the detailsId
	 * 
	 * @param detailsId
	 *            the detailsId to set.
	 */

	public void setDetailsId(Integer detailsId) {
		this.detailsId = detailsId;
	}

	/**
	 * This function returns the meetingRecurrence
	 * 
	 * @return Returns the meetingRecurrence.
	 */

	public MeetingRecurrence getMeetingRecurrence() {
		return meetingRecurrence;
	}

	/**
	 * This function sets the meetingRecurrence
	 * 
	 * @param meetingRecurrence
	 *            the meetingRecurrence to set.
	 */

	public void setMeetingRecurrence(MeetingRecurrence meetingRecurrence) {
		if (null != meetingRecurrence) {
			meetingRecurrence.setMeetingDetails(this);
		}

		this.meetingRecurrence = meetingRecurrence;
	}

	/**
	 * This function returns the recurAfter
	 * 
	 * @return Returns the recurAfter.
	 */

	public Short getRecurAfter() {
		return recurAfter;
	}

	/**
	 * This function sets the recurAfter
	 * 
	 * @param recurAfter
	 *            the recurAfter to set.
	 */

	public void setRecurAfter(Short recurAfter) {
		this.recurAfter = recurAfter;
	}

	/**
	 * This function returns the recurrenceType
	 * 
	 * @return Returns the recurrenceType.
	 */

	public RecurrenceType getRecurrenceType() {
		return recurrenceType;
	}

	/**
	 * This function sets the recurrenceType
	 * 
	 * @param recurrenceType
	 *            the recurrenceType to set.
	 */

	public void setRecurrenceType(RecurrenceType recurrenceType) {
		this.recurrenceType = recurrenceType;
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

}
