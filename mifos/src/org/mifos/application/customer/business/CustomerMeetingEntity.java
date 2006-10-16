/**

 * CustomerMeetingEntity.java    version: 1.0

 

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
package org.mifos.application.customer.business;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.MeetingTypeEntity;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.business.PersistentObject;

/**
 * This class encapsulate the customer meeting
 */
public class CustomerMeetingEntity extends PersistentObject {

	@SuppressWarnings("unused") // see .hbm.xml file
	private final Integer custMeetingId;

	private MeetingBO meeting;

	private final CustomerBO customer;
	
	private Short updatedFlag;

	private MeetingBO updatedMeeting;
	
	public CustomerMeetingEntity(CustomerBO customer, MeetingBO meeting){
		meeting.setMeetingType(new MeetingTypeEntity(MeetingType.CUSTOMERMEETING));
		this.customer = customer;
		this.meeting = meeting;
		this.custMeetingId = null;
		this.updatedFlag  = YesNoFlag.NO.getValue();
	}
	
	/*
	 * Adding a default constructor is hibernate's requirement and should not be
	 * used to create a valid Object.
	 */
	protected CustomerMeetingEntity(){
		this.custMeetingId = null;
		this.customer = null;
	}
	
	public CustomerBO getCustomer() {
		return customer;
	}

	public MeetingBO getMeeting() {
		return meeting;
	}

	void setMeeting(MeetingBO meeting) {
		this.meeting = meeting;
	}

	public Short getUpdatedFlag() {
		return updatedFlag;
	}

	void setUpdatedFlag(Short updatedFlag) {
		this.updatedFlag = updatedFlag;
	}

	public MeetingBO getUpdatedMeeting() {
		return updatedMeeting;
	}

	void setUpdatedMeeting(MeetingBO updatedMeeting) {
		this.updatedMeeting = updatedMeeting;
	}	
}
