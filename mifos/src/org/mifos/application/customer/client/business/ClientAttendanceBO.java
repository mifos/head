/**

 * ClientAttendanceBO.java    version: 1.0

 

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

package org.mifos.application.customer.client.business;

import java.sql.Date;

import org.mifos.application.customer.business.CustomerBO;
import org.mifos.framework.business.BusinessObject;

/**
 * This class is the value object for storing the attendance details of the
 * customer.
 * 
 * @author mohammedn
 * 
 */
public class ClientAttendanceBO extends BusinessObject {

	private Integer id;

	private java.util.Date meetingDate;

	private CustomerBO customer;

	private Short attendance;

	public Integer getId() {
		return id;
	}

	private void setId(Integer id) {
		this.id = id;
	}

	public Short getAttendance() {
		return attendance;
	}

	public void setAttendance(Short attendance) {
		this.attendance = attendance;
	}

	public java.util.Date getMeetingDate() {
		return meetingDate;
	}

	public void setMeetingDate(java.util.Date meetingDate) {
		this.meetingDate = meetingDate;
	}

	public CustomerBO getCustomer() {
		return customer;
	}

	public void setCustomer(CustomerBO customer) {
		this.customer = customer;
	}

	@Override
	public Short getEntityID() {
		return null;
	}

}
