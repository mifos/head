/**

 * MeetingType.java    version: 1.0

 

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

import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * @author rajenders
 *
 */
public class MeetingType extends ValueObject {
	private static final long serialVersionUID = 5555555555l;
	/**
	 * This would hold the meeting typeId
	 */
	private Short meetingTypeId;
	
	/**
	 * This would hold the meeting purpose
	 */

	private String meetingPurpose;
	
	/**
	 * This would hold the description
	 */
	private String description;
	/**
	 * This function returns the description
	 * @return Returns the description.
	 */
	
	public String getDescription() {
		return description;
	}
	/**
	 * This function sets the description
	 * @param description the description to set.
	 */
	
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * This function returns the meetingPurpose
	 * @return Returns the meetingPurpose.
	 */
	
	public String getMeetingPurpose() {
		return meetingPurpose;
	}
	/**
	 * This function sets the meetingPurpose
	 * @param meetingPurpose the meetingPurpose to set.
	 */
	
	public void setMeetingPurpose(String meetingPurpose) {
		this.meetingPurpose = meetingPurpose;
	}
	/**
	 * This function returns the meetingTypeId
	 * @return Returns the meetingTypeId.
	 */
	
	public Short getMeetingTypeId() {
		return meetingTypeId;
	}
	/**
	 * This function sets the meetingTypeId
	 * @param meetingTypeId the meetingTypeId to set.
	 */
	
	public void setMeetingTypeId(Short meetingTypeId) {
		this.meetingTypeId = meetingTypeId;
	}
	/**
	 * Default constructor for the  MeetingType
	 */
	public MeetingType() {

		}


}
