/**

 * ApplyCharges.java    version: xxx

 

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

package org.mifos.application.customer.util.valueobjects;

import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * This class is used as ValueObject for Applying Charges for customers.
 * @author ashishsm
 *
 */
public class ApplyCharges extends ValueObject {

	/**
	 * 
	 */
	public ApplyCharges() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 * This indicates the level of the customer for which charges are being applied.
	 * It gets its value when action form to value object conversion happens.
	 */
	private Short customerLevel;

	/**
	 * @return Returns the customerLevel}.
	 */
	public Short getCustomerLevel() {
		return customerLevel;
	}

	/**
	 * @param customerLevel The customerLevel to set.
	 */
	public void setCustomerLevel(Short customerLevel) {
		this.customerLevel = customerLevel;
	}
	
	/**
	 * This indicates the customerId for which the fees is being added.
	 */
	private Integer customerId;

	/**
	 * @return Returns the customerId}.
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


}
