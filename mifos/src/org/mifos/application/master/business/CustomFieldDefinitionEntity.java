/**

 * CustomFieldDefinition.java    version: xxx



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

package org.mifos.application.master.business;

import java.util.Locale;

import org.mifos.application.master.MessageLookup;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.business.PersistentObject;
import java.util.Set;
import java.util.HashSet;

/**
 * Represents a named custom field of a given type {@link CustomFieldType}
 * defined for a particular entity {@link EntityType}. 
 */

public class CustomFieldDefinitionEntity extends PersistentObject {

	private final Short fieldId;

	/*
	 * The name of the custom field
	 */
	private final MifosLookUpEntity lookUpEntity;

	private final Short levelId;

	/*
	 * The data type {@link CustomFieldType}
	 */
	private final Short fieldType;

	/*
	 * see {@link EntityType}
	 */
	private final Short entityType;

	private  String defaultValue;

	private  Short mandatoryFlag;

	/*
	 * Adding a default constructor is hibernate's requirement and should not be
	 * used to create a valid Object.
	 */
	protected CustomFieldDefinitionEntity() {
		this.fieldId = null;
		this.lookUpEntity = null;
		this.levelId = null;
		this.fieldType = null;
		this.entityType = null;
		this.defaultValue = null;
		this.mandatoryFlag = null;
	}
	
	public CustomFieldDefinitionEntity(MifosLookUpEntity name, Short fieldIndex, CustomFieldType fieldType, 
			EntityType entityType, String defaultValue, YesNoFlag mandatory) {
		this.fieldId = null;       // this should be assigned when persisted to the database 
		this.lookUpEntity = name;  
		this.levelId = fieldIndex;
		this.fieldType = fieldType.value;
		this.entityType = entityType.getValue();
		this.defaultValue = defaultValue;
		this.mandatoryFlag = mandatory.getValue();
		
	}
	
	/*
	 * This constructor is used to create a custom field and after that it will be saved to the 
	 * database using addCustomField of ApplicationConfigurationPersistence
	 */
	public CustomFieldDefinitionEntity(String label, Short levelId, CustomFieldType fieldType, 
		EntityType entityType, String defaultValue, YesNoFlag mandatory, Short localeId) {
		
		MifosLookUpEntity lookupEntity = new MifosLookUpEntity();
		lookupEntity.setEntityType(label);
		
		Set<LookUpLabelEntity> lookUpLabels = new HashSet<LookUpLabelEntity>();
		LookUpLabelEntity lookupLabel = new LookUpLabelEntity();
		lookupLabel.setLabelName(label);
		lookupLabel.setLookUpEntity(lookupEntity);
		lookupLabel.setLocaleId(localeId);
		lookUpLabels.add(lookupLabel);
		lookupEntity.setLookUpLabels(lookUpLabels);
		
		Set<LookUpValueEntity> lookUpValues = new HashSet<LookUpValueEntity>();
		LookUpValueEntity lookupValue = new LookUpValueEntity();
		lookupValue.setLookUpName("");
		lookupValue.setLookUpEntity(lookupEntity);
		lookUpValues.add(lookupValue);
		lookupEntity.setLookUpValues(lookUpValues);
		this.lookUpEntity = lookupEntity;
		this.fieldType = fieldType.value;
		this.levelId = levelId;
		this.defaultValue = defaultValue;
		this.mandatoryFlag = mandatory.getValue();
		this.entityType = entityType.getValue();
		this.fieldId = null;
		
	}
	
	
	public Short getFieldId() {
		return fieldId;
	}

	public MifosLookUpEntity getLookUpEntity() {
		return this.lookUpEntity;
	}

	public Short getLevelId() {
		return this.levelId;
	}

	public Short getFieldType() {
		return this.fieldType;
	}

	public Short getEntityType() {
		return this.entityType;
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

	public boolean isMandatory() {
		return mandatoryFlag.equals(YesNoFlag.YES.getValue());
	}
	
	public void setMandatoryFlag(Short mandatoryFlag) {
		this.mandatoryFlag = mandatoryFlag;
	}

	@Override
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
	
	public static String getMandatoryStringValue(Locale locale, Short flag) {
		return MessageLookup.getInstance().lookup(
				YesNoFlag.fromInt(flag), locale);
	}

	public String getMandatoryStringValue(Locale locale) {
		return MessageLookup.getInstance().lookup(
				YesNoFlag.fromInt(mandatoryFlag), locale);
	}
	
	public void setLabel(String label, Short localeId)
	{
		for (LookUpLabelEntity entity : lookUpEntity.getLookUpLabels()) {
			if (entity.getLocaleId().equals(localeId))
			{
				entity.setLabelName(label);
				break;
			}
		}
	}
	
	public String getLabel(Short localeId)
	{
		/*String label = null;
		for (LookUpLabelEntity entity : lookUpEntity.getLookUpLabels()) {
			if (entity.getLocaleId().equals(localeId))
			{
				label = entity.getLabelName();
				break;
			}
		}*/
		return lookUpEntity.getLabelForLocale(localeId);
	}

	/*
	 * This method is currently used as a property getter in various JSP pages.
	 * Access to this value will need to be updated to support localization.
	 */
	public String getMandatoryStringValue() {
		return getMandatoryStringValue(Locale.US).toLowerCase();
	}
	
	@Override
	public int hashCode() {
		return entityType.hashCode() * levelId.hashCode()
				* fieldType.hashCode();
	}
}
