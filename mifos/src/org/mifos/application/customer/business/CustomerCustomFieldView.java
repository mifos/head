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

package org.mifos.application.customer.business;

import org.mifos.framework.business.View;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * This class encpsulate the custome field for the customer
 * 
 * @author ashishsm
 * 
 */
public class CustomerCustomFieldView extends View {


	private Integer customerId;

	private Short fieldId;

	private String fieldValue;
	
	public CustomerCustomFieldView() {
		super();
	}


	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
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

	public boolean equals(Object obj) {
		CustomerCustomFieldView customerCustomField = (CustomerCustomFieldView) obj;
		if (this.customerId.equals(customerCustomField.getCustomerId())
				&& this.fieldId.equals(customerCustomField.getFieldId())) {
			return true;
		} else {
			return false;
		}
	}

}
