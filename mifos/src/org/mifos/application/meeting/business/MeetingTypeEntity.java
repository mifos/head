/**

 * MeetingTypeEntity.java    version: 1.0

 

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

import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.framework.business.PersistentObject;

/**
 * This class encapsulate the type of the meeting e.g loan meeting
 */
public class MeetingTypeEntity extends PersistentObject {

	private Short meetingTypeId;

	private String meetingPurpose;

	private String description;

	protected MeetingTypeEntity() {}

	public MeetingTypeEntity(MeetingType meetingType) {
		this.meetingTypeId = meetingType.getValue();
	}

	public String getDescription() {
		return description;
	}

	public String getMeetingPurpose() {
		return meetingPurpose;
	}

	public Short getMeetingTypeId() {
		return meetingTypeId;
	}	
}
