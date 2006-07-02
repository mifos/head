/**

 * CustomerState.java    version: xxx



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

import java.util.Set;

import org.mifos.application.master.util.valueobjects.LookUpEntity;
import org.mifos.framework.business.PersistentObject;

/**
 * State of customer e.g pending partial etc
 * 
 * @author ashishsm
 * 
 */
public class CustomerStateEntity extends PersistentObject {

	private Short statusId;

	private LookUpEntity lookUpEntity;

	private CustomerLevelEntity customerLevel;

	private Set customerChecklistSet;

	private String description;

	private Short optional;
	
	public CustomerStateEntity() {
	}

	public Short getStatusId() {
		return statusId;
	}

	public void setStatusId(java.lang.Short statusId) {

		this.statusId = statusId;
	}

	public CustomerLevelEntity getCustomerLevel() {
		return this.customerLevel;
	}

	public void setCustomerLevel(CustomerLevelEntity customerLevel) {
		this.customerLevel = customerLevel;
	}

	public LookUpEntity getLookUpEntity() {
		return this.lookUpEntity;
	}

	public void setLookUpEntity(LookUpEntity lookUpEntity) {
		this.lookUpEntity = lookUpEntity;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(java.lang.String description) {
		this.description = description;
	}

	public Set getCustomerChecklistSet() {
		return this.customerChecklistSet;
	}

	public void setCustomerChecklistSet(java.util.Set customerChecklistSet) {
		this.customerChecklistSet = customerChecklistSet;
	}
	
	public Short getOptional() {
		return optional;
	}

	public void setOptional(Short optional) {
		this.optional = optional;
	}
}
