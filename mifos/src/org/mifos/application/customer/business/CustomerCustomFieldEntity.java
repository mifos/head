/**

 * CustomerCustomFieldEntity.java    version: xxx



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
 * This class encpsulate the custom field for the customer
 * @author ashishsm
 */
public class CustomerCustomFieldEntity extends PersistentObject {

	private final Integer customFieldId;

	private final Short fieldId;

	private String fieldValue;

	private final CustomerBO customer;

	/*
	 * Adding a default constructor is hibernate's requirement and should not be
	 * used to create a valid Object.
	 */
	protected CustomerCustomFieldEntity() {
		super();
		this.customFieldId = null;
		this.fieldId = null;
		this.customer = null;
	}

	public CustomerCustomFieldEntity(Short fieldId, String fieldValue,
			CustomerBO customer) {
		this.fieldId = fieldId;
		this.fieldValue = fieldValue;
		this.customer = customer;
		this.customFieldId = null;
	}

	public Integer getCustomFieldId() {
		return customFieldId;
	}
	
	public Short getFieldId() {
		return fieldId;
	}

	public String getFieldValue() {
		return fieldValue;
	}

	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}

	private CustomerBO getCustomer (){
		return this.customer;
	}
	
	@Override
	public boolean equals(Object obj) {
		CustomerCustomFieldEntity customerCustomField = (CustomerCustomFieldEntity) obj;
		return this.customer.getCustomerId().equals(
				customerCustomField.getCustomer().getCustomerId())
				&& this.fieldId.equals(customerCustomField.getFieldId());
	}
	
	@Override
	public int hashCode() {
		return this.customer.getCustomerId().hashCode() + fieldId.hashCode();
	}
}
