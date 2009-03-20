/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
 
package org.mifos.application.master.business;

import org.mifos.application.customer.client.business.AttendanceType;
import org.mifos.framework.persistence.Persistence;

/**
 * Use {@link AttendanceType} instead.
 */
public class CustomerAttendanceType extends Persistence {

	public CustomerAttendanceType() {
	    super();
	}

	private Short attendanceId;

	private Integer lookUpId;

	private String desciption;

	public Short getAttendanceId() {
		return attendanceId;
	}

	public void setAttendanceId(Short attendanceId) {
		this.attendanceId = attendanceId;
	}

	public String getDesciption() {
		return desciption;
	}

	public void setDesciption(String desciption) {
		this.desciption = desciption;
	}

	public Integer getLookUpId() {
		return lookUpId;
	}

	public void setLookUpId(Integer lookUpId) {
		this.lookUpId = lookUpId;
	}

}
