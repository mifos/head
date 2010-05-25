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

import java.util.List;

import junit.framework.Assert;

import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.components.fieldConfiguration.business.EntityMaster;
import org.mifos.framework.components.fieldConfiguration.business.FieldConfigurationEntity;
import org.mifos.framework.exceptions.PersistenceException;

public class FieldConfigurationPersistenceIntegrationTest extends MifosIntegrationTestCase {

    public FieldConfigurationPersistenceIntegrationTest() throws Exception {
        super();
    }

    private FieldConfigurationPersistence fieldConfigurationPersistence = new FieldConfigurationPersistence();

    public void testGetEntityMasterList() throws PersistenceException {
        List<EntityMaster> entityMasterList = fieldConfigurationPersistence.getEntityMasterList();
       Assert.assertEquals(22, entityMasterList.size());
    }

    public void testGetListOfFields() throws NumberFormatException, PersistenceException {
        List<FieldConfigurationEntity> fieldList = fieldConfigurationPersistence.getListOfFields(EntityType.LOAN
                .getValue());
       Assert.assertEquals(7, fieldList.size());
    }

}
