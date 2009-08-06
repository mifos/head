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

package org.mifos.framework.components.fieldConfiguration.util.helpers;

import java.util.List;
import java.util.Map;

import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.components.fieldConfiguration.business.EntityMaster;
import org.mifos.framework.components.fieldConfiguration.business.FieldConfigurationEntity;
import org.mifos.framework.components.fieldConfiguration.persistence.FieldConfigurationPersistence;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.struts.plugin.helper.EntityMasterData;

public class FieldConfigIntegrationTest extends MifosIntegrationTestCase {

    public FieldConfigIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    private FieldConfigurationPersistence persistence = new FieldConfigurationPersistence();

    private FieldConfig fieldConfig = FieldConfig.getInstance();

    public void testIsFieldHidden() throws HibernateProcessException, PersistenceException {
        EntityMasterData.getInstance().init();
        List<EntityMaster> entityMasterList = persistence.getEntityMasterList();
        for (EntityMaster entityMaster : entityMasterList) {
            fieldConfig.getEntityFieldMap()
                    .put(entityMaster.getId(), persistence.getListOfFields(entityMaster.getId()));
        }
        assertEquals(fieldConfig.isFieldHidden("Loan.PurposeOfLoan"), false);
        assertEquals(fieldConfig.isFieldHidden("Group.City"), true);
        fieldConfig.getEntityFieldMap().clear();
    }

    public void testIsFieldMandatory() throws HibernateProcessException, PersistenceException {
        EntityMasterData.getInstance().init();
        List<EntityMaster> entityMasterList = persistence.getEntityMasterList();
        for (EntityMaster entityMaster : entityMasterList) {
            fieldConfig.getEntityFieldMap()
                    .put(entityMaster.getId(), persistence.getListOfFields(entityMaster.getId()));
        }
        assertEquals(fieldConfig.isFieldManadatory("Loan.PurposeOfLoan"), true);
        fieldConfig.getEntityFieldMap().clear();
    }

    public void testInit() throws HibernateProcessException, ApplicationException {
        EntityMasterData.getInstance().init();
        fieldConfig.init();
        Map<Short, List<FieldConfigurationEntity>> entityMandatoryMap = fieldConfig.getEntityMandatoryFieldMap();
        assertEquals(22, entityMandatoryMap.size());

        List<FieldConfigurationEntity> listOfMandatoryFields = entityMandatoryMap.get(Short.valueOf("22"));
        assertEquals(1, listOfMandatoryFields.size());

        List<FieldConfigurationEntity> listOfFields = fieldConfig.getEntityFieldMap().get(Short.valueOf("22"));
        assertEquals(5, listOfFields.size());

        for (FieldConfigurationEntity fieldConfigurationEntity : listOfMandatoryFields) {
            assertEquals(fieldConfigurationEntity.getFieldName(), "PurposeOfLoan");
        }

        assertEquals(fieldConfig.isFieldHidden("Loan.PurposeOfLoan"), false);
        assertEquals(fieldConfig.isFieldManadatory("Loan.PurposeOfLoan"), true);

        fieldConfig.getEntityFieldMap().clear();
        fieldConfig.getEntityMandatoryFieldMap().clear();
    }

}
