/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

import java.sql.Connection;

import junit.framework.Assert;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

public class AddFieldIntegrationTest extends MifosIntegrationTestCase {

    private Session session;

    @Before
    public void setUp() {
        session = StaticHibernateUtil.getSessionTL();
    }

    /*
     * INSERT INTO
     * FIELD_CONFIGURATION(FIELD_CONFIG_ID,FIELD_NAME,ENTITY_ID,MANDATORY_FLAG
     * ,HIDDEN_FLAG) VALUES(74,'AssignClients',1,0,0);
     */
    @Test
    public void testStartFromStandardStore() throws Exception {
        int newId = 203;
        AddField upgrade = new AddField(newId, "AssignClients",
                EntityType.CLIENT, false, false);
        Connection connection = dataSource.getConnection();
        upgrade.upgrade(connection);
        connection.close();
        FieldConfigurationEntity fetched = (FieldConfigurationEntity) session.get(FieldConfigurationEntity.class, newId);
       Assert.assertEquals(newId, (int) fetched.getFieldConfigId());
        Assert.assertFalse(fetched.isHidden());
        Assert.assertFalse(fetched.isMandatory());
       Assert.assertEquals(EntityType.CLIENT, fetched.getEntityType());
       Assert.assertEquals("AssignClients", fetched.getFieldName());

        /*
         * This upgrade doesn't yet have the ability to set the parent. Looks
         * like we'll probably need that some day (not for 106 upgrade).
         */
       Assert.assertEquals(null, fetched.getParentFieldConfig());
    }
}
