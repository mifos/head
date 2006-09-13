/**

 * CustomerAddressDetail.java    version: xxx



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
import org.mifos.framework.business.util.Address;

/**
 * This class encapsulate the details of the customer address
 */
public class CustomerAddressDetailEntity extends PersistentObject {

	@SuppressWarnings("unused") // see .hbm.xml file
	private final Integer customerAddressId;

	@SuppressWarnings("unused") // see .hbm.xml file
	private final CustomerBO customer;

	private Address address;
	
	/*
	 * Adding a default constructor is hibernate's requirement and should not be
	 * used to create a valid Object.
	 */
	protected CustomerAddressDetailEntity(){
		this.customerAddressId = null;
		this.customer = null;
		this.address = null;
	}
	
	public CustomerAddressDetailEntity(CustomerBO customer, Address address){
		this.customer = customer;
		this.address = address;
		this.customerAddressId = null;
	}
	
	public Address getAddress() {
		return address;
	}
	
	public void setAddress(Address address) {
		this.address = address;
	}
	
	public String getDisplayAddress(){
		return getAddress().getDisplayAddress();
	}
}
