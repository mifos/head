/**

 * AccountTypes.java    version: xxx

 

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

package org.mifos.application.accounts.business;

import java.io.Serializable;

import org.mifos.framework.persistence.Persistence;

/**
 * This class depicts the different account types.
 */
public class AccountTypeEntity extends Persistence implements Serializable{

	public AccountTypeEntity() {
		super();

	}

	public AccountTypeEntity(Short accountTypeId) {
		super();
		this.accountTypeId = accountTypeId;
	}

	private java.lang.Short accountTypeId;

	private Integer lookUpId;

	private java.lang.String description;

	public java.lang.Short getAccountTypeId() {
		return accountTypeId;
	}

	public void setAccountTypeId(java.lang.Short accountTypeId) {
		this.accountTypeId = accountTypeId;
	}

	public java.lang.String getDescription() {
		return description;
	}

	public void setDescription(java.lang.String description) {
		this.description = description;
	}

	public Integer getLookUpId() {
		return lookUpId;
	}

	public void setLookUpId(Integer lookUpId) {
		this.lookUpId = lookUpId;
	}

}
