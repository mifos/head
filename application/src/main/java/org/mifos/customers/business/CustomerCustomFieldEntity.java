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

package org.mifos.customers.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.framework.business.AbstractEntity;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.security.util.UserContext;

/**
 * This class encpsulate the custom field for the customer
 */
public class CustomerCustomFieldEntity extends AbstractEntity {

    @SuppressWarnings("unused")
    private final Integer customFieldId;

    /*
     * Reference to a {@link CustomFieldDefinitionEntity}
     */
    private final Short fieldId;

    private String fieldValue;

    @SuppressWarnings("unused")
    private final CustomerBO customer;

    public CustomerCustomFieldEntity(Short fieldId, String fieldValue, CustomerBO customer) {
        this.fieldId = fieldId;
        this.fieldValue = fieldValue;
        this.customer = customer;
        this.customFieldId = null;
    }

    /*
     * Adding a default constructor is hibernate's requirement and should not be
     * used to create a valid Object.
     */
    protected CustomerCustomFieldEntity() {
        super();
        this.customFieldId = null;
        this.fieldId = null;
        this.customer = null;
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

    public static List<CustomerCustomFieldEntity> fromDto(List<CustomFieldView> customFields, CustomerBO customer) {
        List<CustomerCustomFieldEntity> customerCustomFields = new ArrayList<CustomerCustomFieldEntity>();
        for (CustomFieldView customField : customFields) {
            customerCustomFields.add(new CustomerCustomFieldEntity(customField.getFieldId(), customField
                    .getFieldValue(), customer));
        }
        return customerCustomFields;
    }
    
    public static List<CustomerCustomFieldEntity> fromCustomerCustomFieldEntity(List<CustomerCustomFieldEntity> customFields, CustomerBO customer) {
        List<CustomerCustomFieldEntity> customerCustomFields = new ArrayList<CustomerCustomFieldEntity>();
        for (CustomerCustomFieldEntity customField : customFields) {
            customerCustomFields.add(new CustomerCustomFieldEntity(customField.getFieldId(), customField.getFieldValue(), customer));
        }
        return customerCustomFields;
    }

    public static List<CustomFieldView> toDto(Set<CustomerCustomFieldEntity> customFieldEntities,
            List<CustomFieldDefinitionEntity> customFieldDefs, UserContext userContext) {

        Locale locale = userContext.getPreferredLocale();

        List<CustomFieldView> customFields = new ArrayList<CustomFieldView>();

        for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {

            for (CustomerCustomFieldEntity customFieldEntity : customFieldEntities) {

                if (customFieldDef.getFieldId().equals(customFieldEntity.getFieldId())) {
                    CustomFieldView customFieldView;
                    if (customFieldDef.getFieldType().equals(CustomFieldType.DATE.getValue())) {

                        customFieldView = new CustomFieldView(customFieldEntity.getFieldId(), DateUtils
                                .getUserLocaleDate(locale, customFieldEntity.getFieldValue()), customFieldDef
                                .getFieldType());
                    } else {
                        customFieldView = new CustomFieldView(customFieldEntity.getFieldId(),
                                customFieldEntity.getFieldValue(), customFieldDef.getFieldType());
                    }
                    customFieldView.setMandatory(customFieldDef.isMandatory());
                    customFieldView.setMandatoryString(customFieldDef.getMandatoryStringValue());
                    customFieldView.setLookUpEntityType(customFieldDef.getEntityName());

                    customFields.add(customFieldView);
                }
            }
        }
        return customFields;
    }
}
