/**

 * ClosedAccSearchActionForm.java    version: xxx

 

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
package org.mifos.application.accounts.struts.actionforms;

import org.mifos.framework.struts.actionforms.MifosSearchActionForm;

/**
 * @author mohammedn
 *
 */
public class ClosedAccSearchActionForm extends MifosSearchActionForm {
	/**
	 * default constructor
	 */
	public ClosedAccSearchActionForm() {
	}
	
	/**
	 * serial version UID for serialization
	 */
	private static final long serialVersionUID = 4699203569749596713L;
	
	/**
	 * Id of the Customer
	 */
	private String customerId;
	
	/**
	 * Name of the Customer
	 */
	private String customerName;
	
	/**
	 * Id of the Account
	 */
	private String accountId;
	
	private String entityTypeId;
	
	
	/**
	 * @return Returns the entityTypeId.
	 */
	public String getEntityTypeId() {
		return entityTypeId;
	}

	/**
	 * @param entityTypeId The entityTypeId to set.
	 */
	public void setEntityTypeId(String entityTypeId) {
		this.entityTypeId = entityTypeId;
	}

	/**
	 * @return Returns the customerId.
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
	 * @return Returns the customerName.
	 */
	public String getCustomerName() {
		return customerName;
	}

	/**
	 * @param customerName The customerName to set.
	 */
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	/**
	 * @return Returns the accountId.
	 */
	public String getAccountId() {
		return accountId;
	}

	/**
	 * @param accountId The accountId to set.
	 */
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	
	
	
}
