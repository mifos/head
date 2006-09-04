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
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.exceptions.PropertyNotFoundException;

public class PrdOfferingMeetingEntity extends PersistentObject {

	private final Short prdOfferingMeetingId;

	private MeetingBO meeting;

	private final PrdOfferingBO prdOffering;

	private final Short meetingType;

	protected PrdOfferingMeetingEntity() {
		prdOfferingMeetingId = null;
		prdOffering = null;
		meetingType = null;
	}

	public PrdOfferingMeetingEntity(MeetingBO meeting,
			PrdOfferingBO prdOffering, MeetingType meetingType) {
		prdOfferingMeetingId = null;
		this.meeting = meeting;
		this.prdOffering = prdOffering;
		this.meetingType = meetingType.getValue();
	}

	private Short getPrdOfferingMeetingId() {
		return prdOfferingMeetingId;
	}

	public MeetingType getprdOfferingMeetingType()
			throws ProductDefinitionException {
		try {
			return MeetingType.getMeetingType(meetingType);
		} catch (PropertyNotFoundException e) {
			throw new ProductDefinitionException(e);
		}
	}

	public PrdOfferingBO getPrdOffering() {
		return prdOffering;
	}

	public MeetingBO getMeeting() {
		return meeting;
	}

	public void setMeeting(MeetingBO meeting) {
		this.meeting = meeting;
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
