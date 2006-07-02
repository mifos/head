/**

 * EditGrpMemebrshipActionForm.java    version: xxx

 

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

package org.mifos.application.customer.client.struts.actionforms;

import org.mifos.application.office.util.valueobjects.Office;
import org.mifos.framework.struts.actionforms.MifosSearchActionForm;

/**
 * This class acts as an ActionForm for editing group membership of a client.
 * @author ashishsm
 *
 */
public class EditGrpMembershipActionForm extends MifosSearchActionForm {

	/**
	 * 
	 */
	public EditGrpMembershipActionForm() {
		office = new Office();
	}
	
	/**
	 * This field indicates the customer whose group membership is being updated
	 */
	private String customerId;
	
	/**
	 * This indicates the current state of the customer.This is needed to figure out the possible status of the group
	 * eligible for transfer.
	 */
	private String currentCustomerState;
	
	/**
	 * This is the group id of the group to which the client is being assigned.
	 */
	private String parentGroupId;
	
	/** This is the office id of the branch to which the client is being assigned.*/
	private String officeId;
	
	/** This is the office name of the branch to which the client is being assigned.*/
	private String officeName;
	
	/** This is the office  to which the client is being assigned.*/
	private Office office;
/**
	 * This is the group id of the group to which the client is being assigned.
	 */
	private String parentGroupName;
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

	/**
	 * @return Returns the customerId}.
	 */
	public String getCustomerId() {
		return customerId;
	}

	/**
	 * @param customerId The customerId to set.
	 */
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	/**
	 * @return Returns the currentCustomerState}.
	 */
	public String getCurrentCustomerState() {
		return currentCustomerState;
	}

	/**
	 * @param currentCustomerState The currentCustomerState to set.
	 */
	public void setCurrentCustomerState(String currentCustomerState) {
		this.currentCustomerState = currentCustomerState;
	}

	

	/**
	 * Method which returns the parentGroupId	
	 * @return Returns the parentGroupId.
	 */
	public String getParentGroupId() {
		return parentGroupId;
	}

	/**
	 * Method which sets the parentGroupId
	 * @param parentGroupId The parentGroupId to set.
	 */
	public void setParentGroupId(String parentGroupId) {
		this.parentGroupId = parentGroupId;
	}

	/**
	 * Method which returns the officeId	
	 * @return Returns the officeId.
	 */
	public String getOfficeId() {
		return officeId;
	}

	/**
	 * Method which sets the officeId
	 * @param officeId The officeId to set.
	 */
	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	/**
	 * Method which returns the officeName	
	 * @return Returns the officeName.
	 */
	public String getOfficeName() {
		return officeName;
	}

	/**
	 * Method which sets the officeName
	 * @param officeName The officeName to set.
	 */
	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

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

}
