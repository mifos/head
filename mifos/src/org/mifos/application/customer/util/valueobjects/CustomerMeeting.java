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
package org.mifos.application.customer.util.valueobjects;

import org.mifos.application.customer.util.valueobjects.Customer;
import org.mifos.application.meeting.util.valueobjects.Meeting;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * This class encapsulate the customer meeting
 * @author rajenders
 *
 */
public class CustomerMeeting extends ValueObject {
	private static final long serialVersionUID = 444444444444444l;
	
	/**
	 * This would hold the customer meetingId
	 */
	private Integer custMeetingId;
	
	/**
	 * This would hold the customer meeting 
	 */
	private Meeting meeting;
	
	/**
	 * This would hold the customer for this meeting 
	 */
	private Customer customer;

	/**
	 * This function returns the custMeetingId
	 * @return Returns the custMeetingId.
	 */
	
	private Short updatedFlag;
	
	public Integer getCustMeetingId() {
		return custMeetingId;
	}

	/**
	 * This function sets the custMeetingId
	 * @param custMeetingId the custMeetingId to set.
	 */
	
	public void setCustMeetingId(Integer custMeetingId) {
		this.custMeetingId = custMeetingId;
	}

	/**
	 * This function returns the customer
	 * @return Returns the customer.
	 */
	
	public Customer getCustomer() {
		return customer;
	}

	/**
	 * This function sets the customer
	 * @param customer the customer to set.
	 */
	
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	/**
	 * This function returns the meeting
	 * @return Returns the meeting.
	 */
	
	public Meeting getMeeting() {
		return meeting;
	}

	/**
	 * This function sets the meeting
	 * @param meeting the meeting to set.
	 */
	
	public void setMeeting(Meeting meeting) {
		this.meeting = meeting;
	}

	public Short getUpdatedFlag() {
		return updatedFlag;
	}

	public void setUpdatedFlag(Short updatedFlag) {
		this.updatedFlag = updatedFlag;
	}
	
	
}
