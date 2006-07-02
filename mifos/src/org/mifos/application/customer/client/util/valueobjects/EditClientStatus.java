/**

 * EditClientStatus.java    version: xxx

 

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
import org.mifos.application.customer.util.valueobjects.CustomerNote;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * This class acts as valueobject for edit client status module.
 * @author ashishsm
 *
 */
public class EditClientStatus extends ValueObject {

	public EditClientStatus(){
		customerNote = new CustomerNote();
	}
	 private Integer customerId;
	
	/**The display name of the customer*/
	private String displayName;
	/**
	 * This field denotes the current customerState and comes on actionform to valueobject conversion.
	 */
	private Short currentStatusId;
	/**
	 * This field denotes the current customerState and comes on actionform to valueobject conversion.
	 */
	private Short statusId;
	/**
	 * This field denotes the current customerState and comes on actionform to valueobject conversion.
	 */
	private Short flagId;
	/**Denotes the note object for the client.*/
	private CustomerNote customerNote;

	
	/**
	 * Method which returns the flagId	
	 * @return Returns the flagId.
	 */
	public Short getFlagId() {
		return flagId;
	}

	/**
	 * Method which sets the flagId
	 * @param flagId The flagId to set.
	 */
	public void setFlagId(Short flagId) {
		this.flagId = flagId;
	}

	/**
	 * Method which returns the statusId	
	 * @return Returns the statusId.
	 */
	public Short getStatusId() {
		return statusId;
	}

	/**
	 * Method which sets the statusId
	 * @param statusId The statusId to set.
	 */
	public void setStatusId(Short statusId) {
		this.statusId = statusId;
	}
	public String getResultName(){
		return ClientConstants.CLIENTSTATUSVO;
	}

	/**
	 * Method which returns the currentStatusId	
	 * @return Returns the currentStatusId.
	 */
	public Short getCurrentStatusId() {
		return currentStatusId;
	}

	/**
	 * Method which sets the currentStatusId
	 * @param currentStatusId The currentStatusId to set.
	 */
	public void setCurrentStatusId(Short currentStatusId) {
		this.currentStatusId = currentStatusId;
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
	 * Method which returns the displayName	
	 * @return Returns the displayName.
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Method which sets the displayName
	 * @param displayName The displayName to set.
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * Method which returns the customerNote	
	 * @return Returns the customerNote.
	 */
	public CustomerNote getCustomerNote() {
		return customerNote;
	}

	/**
	 * Method which sets the customerNote
	 * @param customerNote The customerNote to set.
	 */
	public void setCustomerNote(CustomerNote customerNote) {
		this.customerNote = customerNote;
	}

}
