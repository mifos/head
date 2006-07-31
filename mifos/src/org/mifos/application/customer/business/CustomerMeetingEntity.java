/**

 * CustomerMeeting.java    version: 1.0

 

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
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.business.PersistentObject;

/**
 * This class encapsulate the customer meeting
 * 
 * @author rajenders
 * 
 */
public class CustomerMeetingEntity extends PersistentObject {

	private Integer custMeetingId;

	private MeetingBO meeting;

	private CustomerBO customer;
	
	private Short updatedFlag;

	protected CustomerMeetingEntity(){
		
	}
	
	public CustomerMeetingEntity(CustomerBO customer, MeetingBO meeting){
		this.customer = customer;
		this.meeting = meeting;
		this.custMeetingId = null;
		this.updatedFlag  = YesNoFlag.NO.getValue();
	}
	
	public Integer getCustMeetingId() {
		return custMeetingId;
	}

	public void setCustMeetingId(Integer custMeetingId) {
		this.custMeetingId = custMeetingId;
	}

	public CustomerBO getCustomer() {
		return customer;
	}

	public void setCustomer(CustomerBO customer) {
		this.customer = customer;
	}

	public MeetingBO getMeeting() {
		return meeting;
	}

	public void setMeeting(MeetingBO meeting) {
		this.meeting = meeting;
	}

	public Short getUpdatedFlag() {
		return updatedFlag;
	}

	public void setUpdatedFlag(Short updatedFlag) {
		this.updatedFlag = updatedFlag;
	}
	
}
