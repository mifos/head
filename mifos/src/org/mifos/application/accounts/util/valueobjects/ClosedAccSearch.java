/**

 * ClosedAccSearch.java    version: xxx

 

 * Copyright © 2005-2006 Grameen Foundation USA

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
package org.mifos.application.accounts.util.valueobjects;

import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * @author mohammedn
 *
 */
public class ClosedAccSearch extends ValueObject {
	/**
	 * default constructor
	 */
	public ClosedAccSearch() {
	}
	
	/**
	 * serial version UID for serialization
	 */
	private static final long serialVersionUID = 542532532254536541L;
	
	private Integer customerId;
	
	private String customerName;
	
	private Integer accountId;
	
	private Short entityTypeId;
	
	/**
	 * @return Returns the entityTypeId.
	 */
	public Short getEntityTypeId() {
		return entityTypeId;
	}

	/**
	 * @param entityTypeId The entityTypeId to set.
	 */
	public void setEntityTypeId(Short entityTypeId) {
		this.entityTypeId = entityTypeId;
	}

	/**
	 * @return Returns the customerId.
	 */
	public Integer getCustomerId() {
		return customerId;
	}

	/**
	 * @param customerId The customerId to set.
	 */
	public void setCustomerId(Integer customerId) {
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
	public Integer getAccountId() {
		return accountId;
	}

	/**
	 * @param accountId The accountId to set.
	 */
	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}
	
}
