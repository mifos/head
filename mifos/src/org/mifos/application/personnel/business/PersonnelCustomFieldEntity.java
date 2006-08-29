package org.mifos.application.personnel.business;

import org.mifos.framework.business.PersistentObject;

public class PersonnelCustomFieldEntity extends PersistentObject {
	
	private final Integer personnelCustomFieldId;

	private String fieldValue;

	private final Short fieldId;

	private final PersonnelBO personnel;

	public PersonnelCustomFieldEntity(String fieldValue, Short fieldId, PersonnelBO personnel) {
		super();
		this.fieldValue = fieldValue;
		this.fieldId = fieldId;
		this.personnel = personnel;
		personnelCustomFieldId = null;

	}

	protected PersonnelCustomFieldEntity() {
		personnelCustomFieldId = null;
		personnel = null;
		fieldId = null;
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

}
