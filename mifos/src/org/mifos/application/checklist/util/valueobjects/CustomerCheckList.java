/**

 * CustomerCheckList.java    version: xxx

 

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

package org.mifos.application.checklist.util.valueobjects;

import org.mifos.application.customer.util.valueobjects.CustomerState;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * A class that represents a row in the 'customer_checklist' table. This class
 * may be customized as it is never re-generated after being created.
 */
public class CustomerCheckList extends ValueObject {
	/**
	 * Simple constructor of CustomerCheckList instances.
	 */
	public CustomerCheckList() {
	}

	private Short checklistId;

	/**
	 * the value of checkList object
	 */
	private CheckList checkList;

	/** The value of the customerState association. */
	private CustomerState customerState;

	/** The value of the simple levelId property. */
	private Short levelId;

	/**
	 * Return the simple primary key value that identifies this object.
	 * 
	 * @return Checklist
	 */
	public CheckList getCheckList() {
		return checkList;
	}

	/**
	 * Set the simple primary key value that identifies this object.
	 * 
	 * @param checklist
	 */
	public void setCheckList(CheckList checkList) {
		this.checkList = checkList;
	}

	/**
	 * Return the value of the LEVEL_ID column.
	 * 
	 * @return Short
	 */
	public Short getLevelId() {
		return this.levelId;
	}

	/**
	 * Set the value of the LEVEL_ID column.
	 * 
	 * @param levelId
	 */
	public void setLevelId(Short levelId) {
		this.levelId = levelId;
	}

	/**
	 * Return the value of the CUSTOMER_STATUS_ID column.
	 * 
	 * @return CustomerState
	 */
	public CustomerState getCustomerState() {
		return this.customerState;
	}

	/**
	 * Set the value of the CUSTOMER_STATUS_ID column.
	 * 
	 * @param customerState
	 */
	public void setCustomerState(CustomerState customerState) {
		this.customerState = customerState;
	}

	public Short getChecklistId() {
		return checklistId;
	}

	public void setChecklistId(Short checklistId) {
		this.checklistId = checklistId;
	}

	

}
