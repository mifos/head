/**

 * AccountState.java    version: xxx

 

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

package org.mifos.application.master.util.valueobjects;

import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * This class depicts the different state an account can be.
 * @author ashishsm
 *
 */
public class AccountState extends ValueObject {

  /** The composite primary key value. */
    private java.lang.Short accountStateId;

    /** The value of the lookupValue association. */
    private Integer lookUpId;

    /** The value of the simple prdTypeId property. */
    private java.lang.Short prdTypeId;
    
    /**
     * This indicates if the state is currently in use.
     * Some optional states might not be in use.
     */
    private Short currentlyInUse;

	public AccountState() {
		super();
		
	}
	
	public AccountState(Short accountStateId) {
		super();
		this.accountStateId=accountStateId;
	}

	/**
	 * @return Returns the accountStateId}.
	 */
	public java.lang.Short getAccountStateId() {
		return accountStateId;
	}

	/**
	 * @param accountStateId The accountStateId to set.
	 */
	public void setAccountStateId(java.lang.Short accountStateId) {
		this.accountStateId = accountStateId;
	}

	

	/**
	 * @return Returns the prdTypeId}.
	 */
	public java.lang.Short getPrdTypeId() {
		return prdTypeId;
	}

	/**
	 * @param prdTypeId The prdTypeId to set.
	 */
	public void setPrdTypeId(java.lang.Short prdTypeId) {
		this.prdTypeId = prdTypeId;
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

	/**
	 * @return Returns the currentlyInUse}.
	 */
	public Short getCurrentlyInUse() {
		return currentlyInUse;
	}

	/**
	 * @param currentlyInUse The currentlyInUse to set.
	 */
	public void setCurrentlyInUse(Short currentlyInUse) {
		this.currentlyInUse = currentlyInUse;
	}

	

}
