/**

 * RecurrenceType.java    version: 1.0

 

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
package org.mifos.application.meeting.business;

import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.framework.business.PersistentObject;

/**
 * This class encapsulate the RecurrenceType for a meeting
 * See also {@link RecurrenceType}.
 */
public class RecurrenceTypeEntity extends PersistentObject {
	
	private Short recurrenceId;

	private String recurrenceName;

	private String description;

	public RecurrenceTypeEntity(RecurrenceType recurrence){
		this.recurrenceId = recurrence.getValue();
	}
	
	public RecurrenceTypeEntity() {
	}
	
	public String getDescription() {
		return description;
	}

	public Short getRecurrenceId() {
		return recurrenceId;
	}

	public String getRecurrenceName() {
		return recurrenceName;
	}

	public boolean isWeekly(){
		return recurrenceId.equals(RecurrenceType.WEEKLY.getValue());
	}

	public boolean isMonthly(){
		return recurrenceId.equals(RecurrenceType.MONTHLY.getValue());
	}

	public RecurrenceType asEnum() {
		return RecurrenceType.fromInt(recurrenceId);
	}
}
