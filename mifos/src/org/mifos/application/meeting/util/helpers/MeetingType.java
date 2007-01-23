/**

 * MeetingType.java    version: 1.0

 

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
package org.mifos.application.meeting.util.helpers;

import org.mifos.application.meeting.business.MeetingTypeEntity;

/**
 * When ever we require a frequency(recurrence pattern) to be stored, 
 * we create a "meeting" (this is not necessarily a meeting in the
 * sense we present to the users).
 * 
 * See also {@link MeetingTypeEntity}.
 */
public enum MeetingType {
	/* Schedule of loan installment payments for an account */
	/* LOAN_INSTALLMENT */
	LOANFREQUENCYOFINSTALLMENTS((short) 1), 
	
	/* This is not to generate a schedule, but to calculate the 
	 * interest after the specified time period.*/
	/* SAVINGS_INTEREST_CALCULATION_TIME_PERIOD */
	SAVINGSTIMEPERFORINTCALC((short) 2), 

	/* Schedule for posting interest to an account */
	/* SAVINGS_INTEREST_POSTING */
	SAVINGSFRQINTPOSTACC((short) 3), 
	
	/* This is what we call a "meeting" in the user interface. */
	/* CUSTOMER_MEETING */
	CUSTOMERMEETING((short) 4), 
	
	/* Recurrence of a fee. Can be for either a loan or a customer. */
	/* PERIODIC_FEE */
	FEEMEETING((short) 5);

	Short value;

	MeetingType(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}

	public static MeetingType getMeetingType(Short value) {
		for (MeetingType meetingType : MeetingType.values())
			if (meetingType.getValue().equals(value))
				return meetingType;
		return null;
	}
}
