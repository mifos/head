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

package org.mifos.framework.components.fieldConfiguration.business;

import java.io.IOException;
import java.sql.SQLException;

import junit.framework.TestCase;

import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.persistence.Upgrade;

public class AddFieldTest extends TestCase {

    public void testStartFromStandardStore() throws Exception {
        TestDatabase database = TestDatabase.makeStandard();
        upgradeAndCheck(database);
    }

    /*
     * INSERT INTO
     * FIELD_CONFIGURATION(FIELD_CONFIG_ID,FIELD_NAME,ENTITY_ID,MANDATORY_FLAG
     * ,HIDDEN_FLAG) VALUES(74,'AssignClients',1,0,0);
     */
    private Upgrade upgradeAndCheck(TestDatabase database) throws IOException, SQLException, ApplicationException {
        int newId = 203;
        AddField upgrade = new AddField(DatabaseVersionPersistence.APPLICATION_VERSION + 1, newId, "AssignClients",
                EntityType.CLIENT, false, false);
        upgrade.upgrade(database.openConnection(), null);
        FieldConfigurationEntity fetched = (FieldConfigurationEntity) database.openSession().get(
                FieldConfigurationEntity.class, newId);
        assertEquals(newId, (int) fetched.getFieldConfigId());
        assertFalse(fetched.isHidden());
        assertFalse(fetched.isMandatory());
        assertEquals(EntityType.CLIENT, fetched.getEntityType());
        assertEquals("AssignClients", fetched.getFieldName());

        /*
         * This upgrade doesn't yet have the ability to set the parent. Looks
         * like we'll probably need that some day (not for 106 upgrade).
         */
        assertEquals(null, fetched.getParentFieldConfig());

        return upgrade;
    }
}
