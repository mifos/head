/**

 * CustomerPosition.java    version: xxx



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

import org.mifos.application.master.util.valueobjects.Position;
import org.mifos.framework.business.PersistentObject;

/**
 * A Customer can hold various positions like president ,vice president etc.
 * This class holds the relationship between the customer and the postion he
 * holds
 */
public class CustomerPositionEntity extends PersistentObject {

	private final Integer customerPositionId;

	private final Position position;

	private CustomerBO customer;

	protected CustomerPositionEntity() {
		this.customerPositionId = null;
		this.position = null;
	}

	public CustomerPositionEntity(Position position, CustomerBO customer) {
		this.position = position;
		this.customer = customer;
		this.customerPositionId = null;
	}

	public CustomerBO getCustomer() {
		return customer;
	}

	public void setCustomer(CustomerBO customer) {
		this.customer = customer;
	}

	public Position getPosition() {
		return position;
	}

	public Integer getCustomerPositionId() {
		return customerPositionId;
	}
}
