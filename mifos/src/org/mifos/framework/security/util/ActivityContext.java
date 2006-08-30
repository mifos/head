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
	 * This would hold the id of the activity which user want to perform
	 */

	private short activityId;
	/**
	 * This would hold the office id of the record which user want to modify
	 */
	private short recordOfficeId;
	/**
	 * This would hold the record loan officer
	 */
	private short recordLoanOfficer;
	
	private ActionForward lastForward;
	


	/**
	 * @return Returns the lastForward.
	 */
	public ActionForward getLastForward() {
		return lastForward;
	}


	/**
	 * @param lastForward The lastForward to set.
	 */
	public void setLastForward(ActionForward lastForward) {
		this.lastForward = lastForward;
	}


	/**
	 * This function returns the activityId
	 * @return Returns the activityId.
	 */
	public short getActivityId() {
		return activityId;
	}
	
	
	/**
	 * This function sets the activityId
	 * @param activityId The activityId to set.
	 */
	public void setActivityId(short activityId) {
		this.activityId = activityId;
	}
	/**
	 * This function returns the recordLoanOfficer
	 * @return Returns the recordLoanOfficer.
	 */
	public short getRecordLoanOfficer() {
		return recordLoanOfficer;
	}
	/**
	 * This function sets the recordLoanOfficer
	 * @param recordLoanOfficer The recordLoanOfficer to set.
	 */
	public void setRecordLoanOfficer(short recordLoanOfficer) {
		this.recordLoanOfficer = recordLoanOfficer;
	}
	/**
	 * This function returns the recordOfficeId
	 * @return Returns the recordOfficeId.
	 */
	public short getRecordOfficeId() {
		return recordOfficeId;
	}
	/**
	 * This function sets the recordOfficeId
	 * @param recordOfficeId The recordOfficeId to set.
	 */
	public void setRecordOfficeId(short recordOfficeId) {
		this.recordOfficeId = recordOfficeId;
	}
	/**
	 * Constructor with three parameters
	 * @param activityId
	 * @param recordOfficeId
	 * @param recordLoanOfficer
	 */
	public ActivityContext(short activityId, short recordOfficeId, short recordLoanOfficer) {
		// TODO Auto-generated constructor stub
		this.activityId = activityId;
		this.recordOfficeId = recordOfficeId;
		this.recordLoanOfficer = recordLoanOfficer;
	}
	/**
	 * Constructor with two parameters
	 * @param activityId
	 * @param recordOfficeId
	 */
	public ActivityContext(short activityId, short recordOfficeId) {
		// TODO Auto-generated constructor stub
		this.activityId = activityId;
		this.recordOfficeId = recordOfficeId;
	}
	
}
