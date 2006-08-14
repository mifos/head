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
package org.mifos.application.office.business;

import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.business.View;

/**
 * This class represent the custom fields for the office
 * @author rajenders
 */
public class OfficeCustomFieldEntity extends PersistentObject {
	
	private final Integer officecustomFieldId;
	
	private String fieldValue;

	private final Short fieldId;

	private final  OfficeBO office;
	
	protected  OfficeCustomFieldEntity() {
		officecustomFieldId=null;
		office=null;
		fieldId=null;
	}

	public OfficeCustomFieldEntity(String fieldValue, Short fieldId, OfficeBO office) {
		this.fieldValue = fieldValue;
		this.fieldId = fieldId;
		this.office = office;
		this.officecustomFieldId=null;
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

	public OfficeBO getOffice() {
		return office;
	}


	public Integer getOfficecustomFieldId() {
		return officecustomFieldId;
	}


}
