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
 
package org.mifos.application.customer.business;

import java.io.Serializable;

public class CustomerPerformanceHistoryView implements Serializable {

	private Integer meetingsAttended;

	private Integer meetingsMissed;

	private String lastLoanAmount;

	//TODO: remove this constructor and change references
	public CustomerPerformanceHistoryView(){};
	
	public CustomerPerformanceHistoryView(Integer meetingAttended,
			Integer meetingsMissed, String lastLoanAmount) {
		this.meetingsAttended = meetingAttended;
		this.meetingsMissed = meetingsMissed;
		this.lastLoanAmount = lastLoanAmount;
	}

	public Integer getMeetingsAttended() {
		return meetingsAttended;
	}

	public void setMeetingsAttended(Integer meetingsAttended) {
		this.meetingsAttended = meetingsAttended;
	}

	public Integer getMeetingsMissed() {
		return meetingsMissed;
	}

	public void setMeetingsMissed(Integer meetingsMissed) {
		this.meetingsMissed = meetingsMissed;
	}

	public String getLastLoanAmount() {
		return lastLoanAmount;
	}

	public void setLastLoanAmount(String lastLoanAmount) {
		this.lastLoanAmount = lastLoanAmount;
	}

}
