/**

 * PrdState.java    version: xxx

 

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

package org.mifos.application.productdefinition.util.valueobjects;

import org.mifos.application.master.util.valueobjects.LookUpValue;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * @author ashishsm
 *
 */
public class PrdState extends ValueObject{

	/**
	 * Default Constructor 
	 */
	public PrdState() {
	}
	/**
	 * serial version UID for serialization
	 */
	private static final long serialVersionUID = 4534552345435787871L;
	/** The composite primary key value. */
    private java.lang.Short prdStateId;

    /** The value of the lookupEntity association. */
    private LookUpValue lookUpValue;

   /**
     * Return the simple primary key value that identifies this object.
     * @return java.lang.Short
     */
    public java.lang.Short getPrdStateId()
    {
        return prdStateId;
    }

    /**
     * Set the simple primary key value that identifies this object.
     * @param prdStateId
     */
    public void setPrdStateId(java.lang.Short prdStateId)
    {
        this.prdStateId = prdStateId;
    }

	/**
	 * @return Returns the lookUpValue.
	 */
	public LookUpValue getLookUpValue() {
		return lookUpValue;
	}

	/**
	 * @param lookUpValue The lookUpValue to set.
	 */
	public void setLookUpValue(LookUpValue lookUpValue) {
		this.lookUpValue = lookUpValue;
	}
}
