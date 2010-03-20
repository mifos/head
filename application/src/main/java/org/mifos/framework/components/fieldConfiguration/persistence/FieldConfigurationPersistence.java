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

package org.mifos.framework.components.fieldConfiguration.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.application.NamedQueryConstants;
import org.mifos.framework.components.fieldConfiguration.business.EntityMaster;
import org.mifos.framework.components.fieldConfiguration.business.FieldConfigurationEntity;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigurationConstant;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.Persistence;

public class FieldConfigurationPersistence extends Persistence {

    public List<EntityMaster> getEntityMasterList() throws PersistenceException {
        return executeNamedQuery(NamedQueryConstants.GET_ENTITY_MASTER, null);
    }

    public List<FieldConfigurationEntity> getListOfFields(Short entityId) throws PersistenceException {
        Map<String, Object> queryParameter = new HashMap<String, Object>();
        queryParameter.put(FieldConfigurationConstant.ENTITY_ID, entityId);
        return executeNamedQuery(NamedQueryConstants.GET_FIELD_LIST, queryParameter);
    }

    public List<FieldConfigurationEntity> getAllConfigurationFieldList() throws PersistenceException {
        return executeNamedQuery(NamedQueryConstants.GET_ALL_FIELD_CONFIGURATION_LIST, null);
    }

}
