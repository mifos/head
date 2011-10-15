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

package org.mifos.customers.personnel.business;

import junit.framework.Assert;

import org.junit.Test;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.persistence.LegacyMasterDao;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.framework.MifosIntegrationTestCase;
import org.springframework.beans.factory.annotation.Autowired;

public class PersonnelStatusEntityIntegrationTest extends MifosIntegrationTestCase {

    @Autowired
    LegacyMasterDao legacyMasterDao;

    @Test
    public void testGetPersonnelStatusEntity() throws Exception {
        PersonnelStatusEntity personnelStatusEntity = legacyMasterDao
                .getPersistentObject(PersonnelStatusEntity.class, (short) 1);

        String name = ApplicationContextProvider.getBean(MessageLookup.class).lookup(personnelStatusEntity.getLookUpValue());
        (personnelStatusEntity).setName(name);

        Assert.assertEquals(Short.valueOf("1"), personnelStatusEntity.getId());
        Assert.assertEquals("Active", personnelStatusEntity.getName());
    }
}