/**

 * OfficeCustomField.java    version: 1.0



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
package org.mifos.application.office.util.valueobjects;

import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * This class represent the custom fields for the office
 */
public class OfficeCustomField extends ValueObject {
	private static final long serialVersionUID = 66666l;
	
	private Integer officecustomFieldId;
	/**
	 * This would hold the customfield value
	 */
	private String fieldValue;
	/**
	 * This would hold the field id  
	 */
	private Short fieldId;
	/**
	 * this would hold the officeId  
	 */
	private Office office;

	
	/**
	 * This Function returns the fieldId
	 * @return Returns the fieldId.
	 */
	public Short getFieldId() {
		return fieldId;
	}
	/**
	 * This function sets the fieldId
	 * @param fieldId The fieldId to set.
	 */
	public void setFieldId(Short fieldId) {
		this.fieldId = fieldId;
	}
	/**
	 * This Function returns the fieldValue
	 * @return Returns the fieldValue.
	 */
	public String getFieldValue() {
		return fieldValue;
	}
	/**
	 * This function sets the fieldValue
	 * @param fieldValue The fieldValue to set.
	 */
	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}
	
	public Office getOffice() {
		return office;
	}
	public void setOffice(Office office) {
		this.office = office;
	}
	public Integer getOfficecustomFieldId() {
		return officecustomFieldId;
	}
	public void setOfficecustomFieldId(Integer officecustomFieldId) {
		this.officecustomFieldId = officecustomFieldId;
	}
	

	
}
