package org.mifos.application.office.business;

import org.mifos.framework.business.PersistentObject;

public class OfficeCustomFieldEntity extends PersistentObject {

	private final Integer officecustomFieldId;

	private String fieldValue;

	/*
	 * Reference to a {@link CustomFieldDefinitionEntity}
	 */
	private final Short fieldId;

	private final OfficeBO office;

	public OfficeCustomFieldEntity(String fieldValue, Short fieldId,
			OfficeBO office) {
		this.fieldValue = fieldValue;
		this.fieldId = fieldId;
		this.office = office;
		this.officecustomFieldId = null;
	}

	protected OfficeCustomFieldEntity() {
		officecustomFieldId = null;
		office = null;
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

	public OfficeBO getOffice() {
		return office;
	}

	public Integer getOfficecustomFieldId() {
		return officecustomFieldId;
	}

}
