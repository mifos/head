/**

 * AccountType.java    version: xxx

 

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

package org.mifos.application.master.util.valueobjects;

import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * This class depicts the different account types.
 * @author ashishsm
 *
 */
public class AccountType extends ValueObject {

	/**
	 * 
	 */
	public AccountType() {
		super();
		
	}
	public AccountType(Short accountTypeId) {
		super();
		this.accountTypeId=accountTypeId;		
	}
	/** The composite primary key value. */
    private java.lang.Short accountTypeId;

    /** The value of the lookupValue association. */
    private Integer lookUpId;

    /** The value of the simple description property. */
    private java.lang.String description;

	/**
	 * @return Returns the accountTypeId}.
	 */
	public java.lang.Short getAccountTypeId() {
		return accountTypeId;
	}

	/**
	 * @param accountTypeId The accountTypeId to set.
	 */
	public void setAccountTypeId(java.lang.Short accountTypeId) {
		this.accountTypeId = accountTypeId;
	}

	/**
	 * @return Returns the description}.
	 */
	public java.lang.String getDescription() {
		return description;
	}

	/**
	 * @param description The description to set.
	 */
	public void setDescription(java.lang.String description) {
		this.description = description;
	}

	/**
	 * @return Returns the lookUpId}.
	 */
	public Integer getLookUpId() {
		return lookUpId;
	}

	/**
	 * @param lookUpId The lookUpId to set.
	 */
	public void setLookUpId(Integer lookUpId) {
		this.lookUpId = lookUpId;
	}

	


}
