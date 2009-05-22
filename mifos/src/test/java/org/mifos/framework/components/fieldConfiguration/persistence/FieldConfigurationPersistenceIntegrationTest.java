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
 
package org.mifos.framework.components.fieldConfiguration.persistence;

import java.util.List;

import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.components.fieldConfiguration.business.EntityMaster;
import org.mifos.framework.components.fieldConfiguration.business.FieldConfigurationEntity;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;

public class FieldConfigurationPersistenceIntegrationTest extends MifosIntegrationTest {
	
	public FieldConfigurationPersistenceIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    private FieldConfigurationPersistence fieldConfigurationPersistence=
		new FieldConfigurationPersistence();
	
	public void testGetEntityMasterList() throws PersistenceException {
		List<EntityMaster> entityMasterList = 
			fieldConfigurationPersistence.getEntityMasterList();
		assertEquals(22, entityMasterList.size());
	}
	
	public void testGetListOfFields() 
	throws NumberFormatException, PersistenceException {
		List<FieldConfigurationEntity> fieldList =
			fieldConfigurationPersistence.getListOfFields(
				EntityType.LOAN.getValue());
		assertEquals(5, fieldList.size());
	}

}
