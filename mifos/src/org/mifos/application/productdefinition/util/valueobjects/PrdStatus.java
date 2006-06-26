/**

 * PrdStatus.java    version: xxx

 

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
public class PrdStatus extends ValueObject {

	/**
	 * Default Constructor 
	 */
	public PrdStatus() {
	}
	/**
	 * serial version UID for serialization
	 */
	private static final long serialVersionUID = 86587867865787871L;
	/** The composite primary key value. */
    private java.lang.Short offeringStatusId;
    /** The value of the prdType association. */
    private ProductType prdType;
    /** The value of the simple prdStateId property. */
    private PrdState prdStateId;
    /** The value of the simple status property. */
    private java.lang.Short status;
    /** The value of the simple versionNo property. */
    private Integer versionNo;

    /**
     * Return the simple primary key value that identifies this object.
     * @return java.lang.Short
     */
    public java.lang.Short getOfferingStatusId()
    {
        return offeringStatusId;
    }

    /**
     * Set the simple primary key value that identifies this object.
     * @param offeringStatusId
     */
    public void setOfferingStatusId(java.lang.Short offeringStatusId) {
        this.offeringStatusId = offeringStatusId;
    }
    /**
	 * @return Returns the prdStateId.
	 */
	public PrdState getPrdStateId() {
		return prdStateId;
	}
	/**
	 * @param prdStateId The prdStateId to set.
	 */
	public void setPrdStateId(PrdState prdStateId) {
		this.prdStateId = prdStateId;
	}
	/**
     * Return the value of the STATUS column.
     * @return java.lang.Short
     */
    public java.lang.Short getStatus()
    {
        return this.status;
    }

    /**
     * Set the value of the STATUS column.
     * @param status
     */
    public void setStatus(java.lang.Short status) {
        this.status = status;
    }
	/**
	 * @return Returns the prdType.
	 */
	public ProductType getPrdType() {
		return prdType;
	}
	/**
	 * @param prdType The prdType to set.
	 */
	public void setPrdType(ProductType prdType) {
		this.prdType = prdType;
	}
	/**
	 * @return Returns the versionNo}.
	 */
	public Integer getVersionNo() {
		return versionNo;
	}
	/**
	 * @param versionNo The versionNo to set.
	 */
	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
	}
}
