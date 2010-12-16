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

package org.mifos.application.questionnaire.migration;

import java.util.Date;

public class CustomFieldForMigrationDto {
    private Integer customFieldId;
    private Short fieldId;
    private String fieldValue;
    private Date createdDate;
    private Date updatedDate;
    private Short createdBy;
    private Short updatedBy;
    private Integer entityId;

    public CustomFieldForMigrationDto(Object[] customFieldResponse) {
        customFieldId = (Integer) customFieldResponse[0];
        fieldId = (Short) customFieldResponse[1];
        fieldValue = (String) customFieldResponse[2];
        if (fieldValue == null) { // special handling for null responses
            fieldValue = "";
        }
        createdDate = (Date) customFieldResponse[3];
        updatedDate = (Date) customFieldResponse[4];
        createdBy = (Short) customFieldResponse[5];
        updatedBy = (Short) customFieldResponse[6];
        entityId = (Integer) customFieldResponse[7];
    }

    public Integer getCustomFieldId() {
        return customFieldId;
    }

    public Short getFieldId() {
        return fieldId;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public Short getCreatedBy() {
        return createdBy;
    }

    public Short getUpdatedBy() {
        return updatedBy;
    }

    public Integer getEntityId() {
        return entityId;
    }
}
