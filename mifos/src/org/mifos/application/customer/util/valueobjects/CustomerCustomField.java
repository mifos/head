/**

 * CustomerCustomField.java    version: xxx



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

package org.mifos.application.customer.util.valueobjects;

import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * @author ashishsm
 *
 */
public class CustomerCustomField extends ValueObject {

	/**
	 *
	 */
	public CustomerCustomField() {
		super();
		
	}
	private Integer customerCustomFieldId;
	private Customer customer;
	private Short fieldId;
	private String fieldValue;
	
	public Integer getCustomerCustomFieldId() {
		return customerCustomFieldId;
	}
	public void setCustomerCustomFieldId(Integer customerCustomFieldId) {
		this.customerCustomFieldId = customerCustomFieldId;
	}
	
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public Short getFieldId() {
		return fieldId;
	}
	public void setFieldId(Short fieldId) {
		this.fieldId = fieldId;
	}
	public String getFieldValue() {
		return fieldValue;
	}
	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}

	/*public boolean equals(Object obj){
		CustomerCustomField customerCustomField = (CustomerCustomField)obj;
		if(this.getCustomer().getCustomerId().equals(customerCustomField.getCustomer().getCustomerId()) && this.fieldId.equals(customerCustomField.getFieldId())){
			return true;
		}else{
			return false;
		}
	}*/

	/*public int hashCode(){
		//if(fieldId != null)
			return fieldId.hashCode();
	}*/



}
