/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
 
package org.mifos.application.master.business;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.mifos.framework.business.View;
import org.mifos.framework.util.helpers.DateUtils;

public class CustomFieldView extends View {

	private Short fieldId;

	private String fieldValue;
	
	private Short fieldType;

	public CustomFieldView() {
		super();
	}

	public CustomFieldView(Short fieldId, String fieldValue, Short fieldType) {
		this.fieldId = fieldId;
		this.fieldValue = fieldValue;
		this.fieldType = fieldType;
	}
	
	public CustomFieldView(Short fieldId, String value, CustomFieldType type) {
		this(fieldId, value, type == null ? null : type.getValue());
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

	public Short getFieldType() {
		return fieldType;
	}
	
	public CustomFieldType getFieldTypeAsEnum() {
		return CustomFieldType.fromInt(fieldType);
	}

	public void setFieldType(Short fieldType) {
		this.fieldType = fieldType;
	}
	
	public void setFieldType(CustomFieldType type) {
		this.fieldType = type.getValue();
	}

	@Override
	public int hashCode() {
		return fieldId == null ? 0 : fieldId.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final CustomFieldView other = (CustomFieldView) obj;
		if (fieldId == null) {
			if (other.fieldId != null)
				return false;
		} else if (!fieldId.equals(other.fieldId))
			return false;
		return true;
	}

	public void convertDateToUniformPattern(Locale currentLocale) {
		SimpleDateFormat format = (SimpleDateFormat) DateFormat.getDateInstance(
				DateFormat.SHORT, currentLocale);
		String userfmt = DateUtils.convertToCurrentDateFormat(
			format.toPattern());
		setFieldValue(DateUtils.convertUserToDbFmt(getFieldValue(), userfmt));
	}

}
