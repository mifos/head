/**

 * PaymentType.java    version: xxx

 

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

import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * @author ashishsm
 *
 */
public class PaymentType extends ValueObject{

	/**
	 * 
	 */
	public PaymentType() {
		super();
	}

	/** The composite primary key value. */
	private Short paymentTypeId;

	/** The value of the lookupEntity association. */
	private Integer lookUpId;

	/**
	 * Return the simple primary key value that identifies this object.
	 * @return java.lang.Short
	 */
	public Short getPaymentTypeId() {
		return paymentTypeId;
	}

	/**
	 * Set the simple primary key value that identifies this object.
	 * @param paymentTypeId
	 */
	public void setPaymentTypeId(Short paymentTypeId) {

		this.paymentTypeId = paymentTypeId;
	}

	public Integer getLookUpId() {
		return lookUpId;
	}

	public void setLookUpId(Integer lookUpId) {
		this.lookUpId = lookUpId;
	}


}
