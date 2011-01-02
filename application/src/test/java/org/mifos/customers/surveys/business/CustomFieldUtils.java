/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 *  explanation of the license and how it is applied.
 */

package org.mifos.customers.surveys.business;

import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountCustomFieldEntity;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.customers.api.CustomerLevel;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerCustomFieldEntity;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.business.OfficeCustomFieldEntity;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.business.PersonnelCustomFieldEntity;

public class CustomFieldUtils {
    public static CustomFieldDefinitionEntity getCustomerCustomField(String label, CustomFieldType customFieldType) {
        return new CustomFieldDefinitionEntity(label, Short.valueOf("1"), customFieldType, EntityType.CLIENT, "", YesNoFlag.YES);
    }

    public static CustomFieldDefinitionEntity getCustomFieldDef(Integer fieldId, String label, CustomerLevel customerLevel, CustomFieldType customFieldType, EntityType entityType) {
        CustomFieldDefinitionEntity customField1 = new CustomFieldDefinitionEntity(label, customerLevel.getValue(),
                customFieldType, entityType, "Default", YesNoFlag.YES);
        customField1.setFieldId(Short.valueOf(fieldId.toString()));
        return customField1;
    }

    public static CustomerCustomFieldEntity getCustomerCustomField(Integer fieldId, String fieldValue, CustomerBO clientBO) {
        return new CustomerCustomFieldEntity(Short.valueOf(fieldId.toString()), fieldValue, null, clientBO);
    }

    public static AccountCustomFieldEntity getLoanCustomField(Integer fieldId, String fieldValue, AccountBO accountBO) {
        return new AccountCustomFieldEntity(accountBO, Short.valueOf(fieldId.toString()), fieldValue);
    }

    public static OfficeCustomFieldEntity getOfficeCustomField(Integer fieldId, String fieldValue, OfficeBO officeBO) {
        return new OfficeCustomFieldEntity(fieldValue, Short.valueOf(fieldId.toString()), officeBO);
    }

    public static PersonnelCustomFieldEntity getPersonnelCustomField(Integer fieldId, String fieldValue, PersonnelBO personnelBO) {
        return new PersonnelCustomFieldEntity(fieldValue, Short.valueOf(fieldId.toString()), personnelBO);
    }
}
