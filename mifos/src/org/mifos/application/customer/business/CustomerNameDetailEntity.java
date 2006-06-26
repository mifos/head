/**

 * CustomerNameDetail.java    version: xxx



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

package org.mifos.application.customer.business;

import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.business.util.Name;

public class CustomerNameDetailEntity extends PersistentObject {

	private Integer customerNameId;

	private CustomerBO customer;

	private Short nameType;

	private Integer salutation;

	private String secondMiddleName;

	private String displayName;
	
	private Name name;
	
	
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Integer getSalutation() {
		return salutation;
	}

	public void setSalutation(Integer salutation) {
		this.salutation = salutation;
	}

	public String getSecondMiddleName() {
		return secondMiddleName;
	}

	public void setSecondMiddleName(String secondMiddleName) {
		this.secondMiddleName = secondMiddleName;
	}

	public CustomerNameDetailEntity() {
		this.customer = new CustomerBO();
		this.name = new Name();
	}

	public CustomerBO getCustomer() {
		return customer;
	}

	public void setCustomer(CustomerBO customer) {
		this.customer = customer;
	}

	public Short getNameType() {
		return this.nameType;
	}

	public void setNameType(Short nameType) {
		this.nameType = nameType;
	}

	public Integer getCustomerNameId() {
		return customerNameId;
	}

	public void setCustomerNameId(Integer customerNameId) {
		this.customerNameId = customerNameId;
	}

	public Name getName() {
		return name;
	}

	public void setName(Name name) {
		this.name = name;
	}


}
