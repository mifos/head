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

package org.mifos.application.customer.business;

import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.exceptions.ApplicationException;

/**
 * This class encpsulate the custom field for the customer
 */
public class CustomerCustomFieldEntity extends PersistentObject {

    private final Integer customFieldId;

    /*
     * Reference to a {@link CustomFieldDefinitionEntity}
     */
    private final Integer fieldId;

    private String fieldValue;

    private final CustomerBO customer;

    public CustomerCustomFieldEntity(Integer fieldId, String fieldValue, CustomerBO customer) {
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

    public Integer getFieldId() {
        return fieldId;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public void save(CustomerCustomFieldEntity customerCustomFieldEntity) throws ApplicationException {
        new CustomerPersistence().createOrUpdate(customerCustomFieldEntity);
    }
}
