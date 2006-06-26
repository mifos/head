/**

 * CustomFieldDefinition.java    version: xxx



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

import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.master.util.valueobjects.LookUpEntity;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.util.helpers.Constants;

/**
 * This class represent custome field defination entity
 * 
 * @author ashishsm
 * 
 */

public class CustomFieldDefinitionEntity extends PersistentObject {

	private Short fieldId;

	private LookUpEntity lookUpEntity;

	private Short levelId;

	private Short fieldType;

	private Short entityType;
	
	private String defaultValue;
	
	private Short mandatoryFlag;

	public CustomFieldDefinitionEntity() {
	}

	public Short getFieldId() {
		return fieldId;
	}

	public void setFieldId(Short fieldId) {

		this.fieldId = fieldId;
	}

	public LookUpEntity getLookUpEntity() {
		return this.lookUpEntity;
	}

	public void setLookUpEntity(LookUpEntity lookUpEntity) {
		this.lookUpEntity = lookUpEntity;
	}

	public Short getLevelId() {
		return this.levelId;
	}

	public void setLevelId(Short levelId) {
		this.levelId = levelId;
	}

	public Short getFieldType() {
		return this.fieldType;
	}

	public void setFieldType(Short fieldType) {
		this.fieldType = fieldType;
	}

	public Short getEntityType() {
		return this.entityType;
	}

	public void setEntityType(Short entityType) {
		this.entityType = entityType;
	}

	private Short getMandatoryFlag() {
		return this.mandatoryFlag;
	}
	 public String getDefaultValue() {
			return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	private void setMandatoryFlag(Short mandatoryFlag) {
		this.mandatoryFlag = mandatoryFlag;
	}
   public boolean isMandatoryFlag(){
	   return this.mandatoryFlag>0;
   }
	public boolean equals(Object obj) {
		CustomFieldDefinitionEntity customFieldDefinition = (CustomFieldDefinitionEntity) obj;
		if (this.entityType.equals(customFieldDefinition.getEntityType())
				&& this.levelId.equals(customFieldDefinition.getLevelId())
				&& this.lookUpEntity.equals(customFieldDefinition
						.getLookUpEntity())
				&& this.fieldType.equals(customFieldDefinition.getFieldType())) {
			return true;
		} else {
			return false;
		}
	}
	public String getMandatoryStringValue() {
		return (mandatoryFlag.shortValue() == Constants.YES ? CustomerConstants.YES_SMALL : CustomerConstants.NO_SMALL);
		
	}
	public int hashCode() {
		return entityType.hashCode() * levelId.hashCode()
				* fieldType.hashCode();
	}

}
