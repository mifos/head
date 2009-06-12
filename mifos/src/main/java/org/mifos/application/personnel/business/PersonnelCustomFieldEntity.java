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

package org.mifos.application.personnel.business;

import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.exceptions.ApplicationException;

public class PersonnelCustomFieldEntity extends PersistentObject {

    private final Integer personnelCustomFieldId;

    private String fieldValue;

    /*
     * Reference to a {@link CustomFieldDefinitionEntity}
     */
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

    public void save(PersonnelCustomFieldEntity personnelCustomFieldEntity) throws ApplicationException {
        new PersonnelPersistence().createOrUpdate(personnelCustomFieldEntity);
    }
}
