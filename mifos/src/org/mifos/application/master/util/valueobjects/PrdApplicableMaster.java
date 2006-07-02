/**

 * PrdApplicableMaster.java    version: xxx

 

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
 * @author ashishsm
 *
 */
public class PrdApplicableMaster extends ValueObject {
	/**
	 * 
	 */
	public PrdApplicableMaster() {
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 633657567657864781L;
	
/** The composite primary key value. */
    private java.lang.Short prdApplicableMasterId;

    /** The value of the lookupValue association. */
    private Integer lookUpId;

	/**
	 * @return Returns the lookUpId.
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
	 * @return Returns the prdApplicableMasterId}.
	 */
	public java.lang.Short getPrdApplicableMasterId() {
		return prdApplicableMasterId;
	}

	/**
	 * @param prdApplicableMasterId The prdApplicableMasterId to set.
	 */
	public void setPrdApplicableMasterId(java.lang.Short prdApplicableMasterId) {
		this.prdApplicableMasterId = prdApplicableMasterId;
	}

}
