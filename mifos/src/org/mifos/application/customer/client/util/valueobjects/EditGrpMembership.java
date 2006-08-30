/**

 * EditGrpMemebership.java    version: xxx

 

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

package org.mifos.application.customer.client.util.valueobjects;

import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.office.util.valueobjects.Office;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * This class acts as a value object for editing group membership of a client.
 */
public class EditGrpMembership extends ValueObject {

	public EditGrpMembership(){
		office=new Office();
	}
	
	/**
	 * This field indicates the customer whose group membership is being updated
	 */
	private Integer customerId;
	
	/**
	 * This indicates the current state of the customer.This is needed to figure out the possible status of the group
	 * eligible for transfer.
	 */
	private Short currentCustomerState;
	
	/**
	 * This is the group id of the group to which the client is being assigned.
	 */
	private Integer parentGroupId;
	
	/**
	 * This is the group id of the group to which the client is being assigned.
	 */
	private String parentGroupName;
	
	/** This is the office  to which the client is being assigned.*/
	private Office office;

	/**
	 * Method which returns the office	
	 * @return Returns the office.
	 */
	public Office getOffice() {
		return office;
	}
	
	/**
	 * Method which sets the office
	 * @param office The office to set.
	 */
	public void setOffice(Office office) {
		this.office = office;
	}

	/**
	 * Method which returns the currentCustomerState	
	 * @return Returns the currentCustomerState.
	 */
	public Short getCurrentCustomerState() {
		return currentCustomerState;
	}

	/**
	 * Method which sets the currentCustomerState
	 * @param currentCustomerState The currentCustomerState to set.
	 */
	public void setCurrentCustomerState(Short currentCustomerState) {
		this.currentCustomerState = currentCustomerState;
	}

	/**
	 * Method which returns the customerId	
	 * @return Returns the customerId.
	 */
	public Integer getCustomerId() {
		return customerId;
	}

	/**
	 * Method which sets the customerId
	 * @param customerId The customerId to set.
	 */
	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	

	/**
	 * Method which returns the parentGroupId	
	 * @return Returns the parentGroupId.
	 */
	public Integer getParentGroupId() {
		return parentGroupId;
	}

	/**
	 * Method which sets the parentGroupId
	 * @param parentGroupId The parentGroupId to set.
	 */
	public void setParentGroupId(Integer parentGroupId) {
		this.parentGroupId = parentGroupId;
	}

	
	/**
	 * Method which returns the parentGroupName	
	 * @return Returns the parentGroupName.
	 */
	public String getParentGroupName() {
		return parentGroupName;
	}

	/**
	 * Method which sets the parentGroupName
	 * @param parentGroupName The parentGroupName to set.
	 */
	public void setParentGroupName(String parentGroupName) {
		this.parentGroupName = parentGroupName;
	}
	public String getResultName(){
		return ClientConstants.CLIENT_TRANSFERVO;
	}

	

}
