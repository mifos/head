/**

 * GracePeriodType.java    version: xxx

 

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

package org.mifos.application.productdefinition.util.valueobjects;

import org.mifos.framework.util.valueobjects.ValueObject;


/**
 * @author ashishsm
 *
 */
public class GracePeriodType extends ValueObject{

	/**
	 * default constructor
	 */
	public GracePeriodType() {
		super();
	}
	/**
	 * serial version UID for serialization
	 */
	private static final long serialVersionUID = 6546767657563453451L;
	
	/** The composite primary key value. */
    private java.lang.Short gracePeriodTypeId;

    /** The value of the lookupEntity association. */
   // private LookUpValue lookUpValue;
    	private Integer lookUpId;
	/**
	 * @return Returns the gracePeriodTypeId}.
	 */
	public java.lang.Short getGracePeriodTypeId() {
		return gracePeriodTypeId;
	}

	/**
	 * @param gracePeriodTypeId The gracePeriodTypeId to set.
	 */
	public void setGracePeriodTypeId(java.lang.Short gracePeriodTypeId) {
		this.gracePeriodTypeId = gracePeriodTypeId;
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
