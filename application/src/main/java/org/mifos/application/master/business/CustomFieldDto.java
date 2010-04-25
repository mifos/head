/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.mifos.framework.business.service.DataTransferObject;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.util.helpers.DateUtils;

public class CustomFieldDto implements DataTransferObject {

    private Short fieldId;

    private String fieldValue;

    private Short fieldType;

    private boolean mandatory;
    private String lookUpEntityType;
    private String mandatoryString;

    public CustomFieldDto() {
        super();
    }

    public CustomFieldDto(Short fieldId, String fieldValue, Short fieldType) {
        this.fieldId = fieldId;
        this.fieldValue = fieldValue;
        this.fieldType = fieldType;
    }

    public CustomFieldDto(Short fieldId, String value, CustomFieldType type) {
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
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CustomFieldDto other = (CustomFieldDto) obj;
        if (fieldId == null) {
            if (other.fieldId != null) {
                return false;
            }
        } else if (!fieldId.equals(other.fieldId)) {
            return false;
        }
        return true;
    }

    public void convertDateToUniformPattern(Locale currentLocale) throws InvalidDateException {
        SimpleDateFormat format = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, currentLocale);
        String userfmt = DateUtils.convertToCurrentDateFormat(format.toPattern());
        setFieldValue(DateUtils.convertUserToDbFmt(getFieldValue(), userfmt));
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public boolean isMandatory() {
        return this.mandatory;
    }

    public String getLookUpEntityType() {
        return this.lookUpEntityType;
    }

    public void setLookUpEntityType(String lookUpEntityType) {
        this.lookUpEntityType = lookUpEntityType;
    }

    public String getMandatoryString() {
        return this.mandatoryString;
    }

    public void setMandatoryString(String mandatoryString) {
        this.mandatoryString = mandatoryString;
    }

    public static void convertCustomFieldDateToUniformPattern(List<CustomFieldDto> customFields, Locale locale)
            throws InvalidDateException {
        for (CustomFieldDto customField : customFields) {
            if (customField.getFieldType().equals(CustomFieldType.DATE.getValue())
                    && StringUtils.isNotBlank(customField.getFieldValue())) {
                customField.convertDateToUniformPattern(locale);
            }
        }
    }
}
