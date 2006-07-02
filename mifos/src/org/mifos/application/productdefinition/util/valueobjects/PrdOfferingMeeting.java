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

package org.mifos.application.productdefinition.util.valueobjects;

import org.mifos.application.meeting.util.valueobjects.Meeting;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * @author mohammedn
 *
 */
public class PrdOfferingMeeting extends ValueObject {
	/**
	 * Default Constructor 
	 */
	public PrdOfferingMeeting() {
	}
	/**
	 * serial version UID for serialization
	 */
	private static final long serialVersionUID = 67865878766781L;
	 /** The value of the simple prdOfferingMeetingId property. */
	private Short prdOfferingMeetingId;
	 /** The value of the Meeting association. */
	private Meeting meeting;
	 /** The value of the PrdOffering association. */
	private PrdOffering prdOffering;
	 /** The value of the simple meetingType property. */
	private Short meetingType;

	/**
	 * @return Returns the meetingType.
	 */
	public Short getMeetingType() {
		return meetingType;
	}

	/**
	 * @param meetingType The meetingType to set.
	 */
	public void setMeetingType(Short meetingType) {
		this.meetingType = meetingType;
	}

	/**
	 * @return Returns the meeting.
	 */
	public Meeting getMeeting() {
		return meeting;
	}

	/**
	 * @param meeting The meeting to set.
	 */
	public void setMeeting(Meeting meeting) {
		this.meeting = meeting;
	}

	/**
	 * @return Returns the prdOffering.
	 */
	public PrdOffering getPrdOffering() {
		return prdOffering;
	}

	/**
	 * @param prdOffering The prdOffering to set.
	 */
	public void setPrdOffering(PrdOffering prdOffering) {
		this.prdOffering = prdOffering;
	}

	/**
	 * @return Returns the prdOfferingMeetingId.
	 */
	public Short getPrdOfferingMeetingId() {
		return prdOfferingMeetingId;
	}

	/**
	 * @param prdOfferingMeetingId The prdOfferingMeetingId to set.
	 */
	public void setPrdOfferingMeetingId(Short prdOfferingMeetingId) {
		this.prdOfferingMeetingId = prdOfferingMeetingId;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object object) {
		PrdOfferingMeeting prdOfferingMeeting=null;
		boolean value=false;
		if(object !=null) {
			prdOfferingMeeting=(PrdOfferingMeeting)object;
			if(prdOfferingMeeting.getPrdOfferingMeetingId().equals(this.prdOfferingMeetingId)) {
				value= true;
			}
		}
		return value;
	}
}
