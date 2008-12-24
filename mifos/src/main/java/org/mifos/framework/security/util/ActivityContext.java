/**

 * ActivityContext.java    version: 1.0

 

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
package org.mifos.framework.security.util;

import java.io.Serializable;

import org.apache.struts.action.ActionForward;

/**
 * This class will hold the attributes for the current activity
 * which user want to perform in the system .User has to set the attributes
 * and the pass the same to the Authorization manager for verification
 */
public class ActivityContext implements Serializable {

	/**
	 * This would hold the id of the activity which user want to perform.
	 * The activities are set up in latest-data; see
	 * for example {@link ActivityMapper#SAVING_CANCHANGESTATETO_APPROVED}.
	 */
	private short activityId;

	/**
	 * This would hold the office id of the record which user want to modify
	 */
	private short recordOfficeId;

	private short recordLoanOfficer;
	
	private ActionForward lastForward;
	

	public ActionForward getLastForward() {
		return lastForward;
	}

	public void setLastForward(ActionForward lastForward) {
		this.lastForward = lastForward;
	}

	public short getActivityId() {
		return activityId;
	}
	
	public void setActivityId(short activityId) {
		this.activityId = activityId;
	}

	public short getRecordLoanOfficer() {
		return recordLoanOfficer;
	}

	public void setRecordLoanOfficer(short recordLoanOfficer) {
		this.recordLoanOfficer = recordLoanOfficer;
	}

	public short getRecordOfficeId() {
		return recordOfficeId;
	}

	public void setRecordOfficeId(short recordOfficeId) {
		this.recordOfficeId = recordOfficeId;
	}

	public ActivityContext(short activityId, 
			short recordOfficeId, short recordLoanOfficer) {
		this.activityId = activityId;
		this.recordOfficeId = recordOfficeId;
		this.recordLoanOfficer = recordLoanOfficer;
	}

	public ActivityContext(short activityId, short recordOfficeId) {
		this.activityId = activityId;
		this.recordOfficeId = recordOfficeId;
	}
	
}
