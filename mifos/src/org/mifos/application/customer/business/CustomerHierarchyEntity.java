/**

 * CustomerHierarchy.java    version: xxx



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

import java.util.Date;

import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.PersistentObject;

/**
 * This class encapsulate the customer hierarchy
 * 
 * @author ashishsm
 */
public class CustomerHierarchyEntity extends PersistentObject {

	private Integer hierarchyId;

	private CustomerBO parentCustomer;

	private CustomerBO customer;

	private Integer parentId;

	private Short status;

	private Date endDate;


	public CustomerHierarchyEntity() {
	}

	public Integer getHierarchyId() {
		return hierarchyId;
	}

	public void setHierarchyId(Integer hierarchyId) {

		this.hierarchyId = hierarchyId;
	}

	public Integer getParentId() {
		return this.parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Short getStatus() {
		return this.status;
	}

	public void setStatus(Short status) {
		this.status = status;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}


	public CustomerBO getCustomer() {
		return customer;
	}

	public void setCustomer(CustomerBO customer) {
		this.customer = customer;
	}

	public CustomerBO getParentCustomer() {
		return parentCustomer;
	}

	public void setParentCustomer(CustomerBO parentCustomer) {
		this.parentCustomer = parentCustomer;
	}

}
