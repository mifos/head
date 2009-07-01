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

package org.mifos.framework.components.audit.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.LookUpLabelEntity;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.application.master.business.MifosLookUpEntity;
import org.mifos.application.meeting.business.RecurrenceTypeEntity;
import org.mifos.application.productdefinition.business.PrdStatusEntity;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.Persistence;

public class AuditConfigurationPersistence extends Persistence {

    public Map<String, String> retrieveAllCustomFieldsDefinition(Short localeId) throws PersistenceException {
        Map<String, String> valueMap = new HashMap<String, String>();
        try {
            List<CustomFieldDefinitionEntity> customFields = executeNamedQuery(
                    NamedQueryConstants.RETRIEVE_ALL_CUSTOM_FIELDS, null);
            for (CustomFieldDefinitionEntity field : customFields) {
                MifosLookUpEntity lookUpEntity = field.getLookUpEntity();
                Set<LookUpLabelEntity> lookUpLabelSet = lookUpEntity.getLookUpLabels();
                for (LookUpLabelEntity lookUpLabel : lookUpLabelSet) {
                    if (lookUpLabel.getLocaleId().equals(localeId)) {
                        valueMap.put(field.getFieldId().toString(), lookUpLabel.getLabelText());
                    }
                }
            }
        } catch (HibernateException e) {
            throw new PersistenceException(e);
        }
        return valueMap;
    }

    public Map<String, String> retrieveProductStatus(Short localeId) throws PersistenceException {
        Map<String, String> valueMap = new HashMap<String, String>();
        try {
            List<PrdStatusEntity> productStates = executeNamedQuery(NamedQueryConstants.ALL_PRD_STATES, null);
            for (PrdStatusEntity prdStatusEntity : productStates) {
                valueMap.put(prdStatusEntity.getOfferingStatusId().toString(), MessageLookup.getInstance().lookup(
                        prdStatusEntity.getPrdState().getLookUpValue()));
            }
        } catch (HibernateException e) {
            throw new PersistenceException(e);
        }
        return valueMap;
    }

    public Map<String, String> retrieveRecurrenceTypes(Short localeId) throws PersistenceException {
        Map<String, String> valueMap = new HashMap<String, String>();
        try {
            List<RecurrenceTypeEntity> recurrenceTypes = executeNamedQuery(
                    NamedQueryConstants.FETCH_ALL_RECURRENCE_TYPES, null);
            for (RecurrenceTypeEntity recurrence : recurrenceTypes) {
                valueMap.put(recurrence.getRecurrenceId().toString(), recurrence.getRecurrenceName());
            }
        } catch (HibernateException e) {
            throw new PersistenceException(e);
        }
        return valueMap;
    }

}
