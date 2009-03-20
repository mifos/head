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
 
package org.mifos.application.master.business;

import java.util.Set;

import junit.framework.TestCase;

import org.hibernate.classic.Session;
import org.mifos.framework.persistence.TestDatabase;

public class LookUpValueEntityTest extends TestCase {
	
	public void testReadFromMasterData() throws Exception {
		TestDatabase database = TestDatabase.makeStandard();
		Session reader = database.openSession();
		LookUpValueEntity readEntity = (LookUpValueEntity) 
			reader.get(LookUpValueEntity.class, 404);
		assertEquals(" ", readEntity.getLookUpName());
		assertEquals(87, (int)readEntity.getLookUpEntity().getEntityId());

		Set<LookUpValueLocaleEntity> locales = 
			readEntity.getLookUpValueLocales();
		assertEquals(1, locales.size());
		assertEquals("Can make payments to Client accounts", 
			locales.iterator().next().getLookUpValue());

		reader.close();
	}
	
	public void testWriteAndRead() throws Exception {
		TestDatabase database = TestDatabase.makeStandard();

		Session writer = database.openSession();

		LookUpValueEntity entity = new LookUpValueEntity();
		entity.setLookUpName("my entity");
		MifosLookUpEntity mifosLookUpEntity = new MifosLookUpEntity();
		mifosLookUpEntity.setEntityId((short)87);
		entity.setLookUpEntity(mifosLookUpEntity);

		writer.save(entity);
		int writtenId = entity.getLookUpId();
		writer.close();

		Session reader = database.openSession();
		LookUpValueEntity readEntity = (LookUpValueEntity)
			reader.get(LookUpValueEntity.class, writtenId);
		assertEquals("my entity", readEntity.getLookUpName());
		assertEquals(87, (int)readEntity.getLookUpEntity().getEntityId());
	}

}
