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

package org.mifos.customers.office.business;

import org.mifos.framework.business.AbstractEntity;

public class OfficeCustomFieldEntity extends AbstractEntity {

    private final Integer officecustomFieldId;

    private String fieldValue;

    /*
     * Reference to a {@link CustomFieldDefinitionEntity}
     */
    private final Short fieldId;

    private final OfficeBO office;

    public OfficeCustomFieldEntity(String fieldValue, Short fieldId, OfficeBO office) {
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
