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

package org.mifos.application.master.business;

import java.util.Set;

import junit.framework.Assert;

import org.hibernate.Session;
import org.junit.Ignore;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

@Ignore
public class LookUpValueEntityIntegrationTest extends MifosIntegrationTestCase {

    public LookUpValueEntityIntegrationTest() throws Exception {
        super();
    }

    private Session session;

    @Override
    public void setUp() {
        session = StaticHibernateUtil.getSessionTL();
    }

    public void testReadFromMasterData() throws Exception {
        LookUpValueEntity readEntity = (LookUpValueEntity) session.get(LookUpValueEntity.class, 404);
       Assert.assertEquals(" ", readEntity.getLookUpName());
       Assert.assertEquals(87, (int) readEntity.getLookUpEntity().getEntityId());

        Set<LookUpValueLocaleEntity> locales = readEntity.getLookUpValueLocales();
       Assert.assertEquals(1, locales.size());
       Assert.assertEquals("Can make payments to Client accounts", locales.iterator().next().getLookUpValue());
    }

    public void testWriteAndRead() throws Exception {

        LookUpValueEntity entity = new LookUpValueEntity();
        entity.setLookUpName("my entity");
        MifosLookUpEntity mifosLookUpEntity = new MifosLookUpEntity();
        mifosLookUpEntity.setEntityId((short) 87);
        entity.setLookUpEntity(mifosLookUpEntity);

        session.save(entity);
        int writtenId = entity.getLookUpId();

       LookUpValueEntity readEntity = (LookUpValueEntity) session.get(LookUpValueEntity.class, writtenId);
       Assert.assertEquals("my entity", readEntity.getLookUpName());
       Assert.assertEquals(87, (int) readEntity.getLookUpEntity().getEntityId());
    }
}
