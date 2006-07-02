/**

 * CustomerFlag.java    version: xxx



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
package org.mifos.application.customer.business;

import org.mifos.framework.business.PersistentObject;

/**
 * A class that represents a row in the 'customer flag' table.
 * 
 */
public class CustomerFlagEntity extends PersistentObject {

	private Integer customerFlagId;

	private Integer customerId;

	private Short flagId;

	private Short flagStatus;

	private CustomerBO customer;

	public CustomerFlagEntity() {
	}

	public void setCustomerFlagId(Integer customerFlagId) {
		this.customerFlagId = customerFlagId;
	}

	public Integer getCustomerFlagId() {
		return customerFlagId;
	}

	public void setCustomerId(Integer customerId) {

		this.customerId = customerId;
	}
	public Integer getCustomerId() {

		return customerId;
	}
	public void setFlagId(Short flagId) {
		this.flagId = flagId;
	}

	public Short getFlagId() {
		return flagId;
	}
	public void setFlagStatus(Short flagStatus) {
		this.flagStatus = flagStatus;
	}

	public Short getFlagStatus() {
		return flagStatus;
	}

	public CustomerBO getCustomer() {
		return customer;
	}
	public void setCustomer(CustomerBO customer) {
		this.customer = customer;
	}

}
