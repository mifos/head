/**

 * InterestTypes.java    version: xxx

 

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

import org.mifos.application.productdefinition.util.valueobjects.ProductType;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * @author ashishsm
 *
 */
public class InterestTypes extends ValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4234324231L;



	/**
	 * 
	 */
	public InterestTypes() {
		super();
		// TODO Auto-generated constructor stub
	}
	
 /** The composite primary key value. */
    private java.lang.Short interestTypeId;

    /** The value of the categoryType association. */
    private ProductType productType;

    /** The value of the lookupValue association. */
   // private LookUpValue lookUpValue;
      private Integer lookUpId;
    /** The value of the simple descripton property. */
    private java.lang.String descripton;

	

	public ProductType getProductType() {
		return productType;
	}

	public void setProductType(ProductType productType) {
		this.productType = productType;
	}

	/**
	 * @return Returns the descripton}.
	 */
	public java.lang.String getDescripton() {
		return descripton;
	}

	/**
	 * @param descripton The descripton to set.
	 */
	public void setDescripton(java.lang.String descripton) {
		this.descripton = descripton;
	}

	/**
	 * @return Returns the interestTypeId}.
	 */
	public java.lang.Short getInterestTypeId() {
		return interestTypeId;
	}

	/**
	 * @param interestTypeId The interestTypeId to set.
	 */
	public void setInterestTypeId(java.lang.Short interestTypeId) {
		this.interestTypeId = interestTypeId;
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
