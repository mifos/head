/**

 * PrdOfferingMeeting.java    version: xxx

 

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

package org.mifos.application.productdefinition.business;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.business.PersistentObject;

public class PrdOfferingMeetingEntity extends PersistentObject {

	private Short prdOfferingMeetingId;

	private MeetingBO meeting;

	private PrdOfferingBO prdOffering;

	private Short meetingType;

	public PrdOfferingMeetingEntity() {
	}

	public Short getMeetingType() {
		return meetingType;
	}

	public void setMeetingType(Short meetingType) {
		this.meetingType = meetingType;
	}

	public MeetingBO getMeeting() {
		return meeting;
	}

	public void setMeeting(MeetingBO meeting) {
		this.meeting = meeting;
	}

	public PrdOfferingBO getPrdOffering() {
		return prdOffering;
	}

	public void setPrdOffering(PrdOfferingBO prdOffering) {
		this.prdOffering = prdOffering;
	}

	public Short getPrdOfferingMeetingId() {
		return prdOfferingMeetingId;
	}

	public void setPrdOfferingMeetingId(Short prdOfferingMeetingId) {
		this.prdOfferingMeetingId = prdOfferingMeetingId;
	}

	@Override
	public boolean equals(Object object) {
		PrdOfferingMeetingEntity prdOfferingMeeting = null;
		boolean value = false;
		if (object != null) {
			prdOfferingMeeting = (PrdOfferingMeetingEntity) object;
			if (prdOfferingMeeting.getPrdOfferingMeetingId().equals(
					this.prdOfferingMeetingId)) {
				value = true;
			}
		}
		return value;
	}
}
