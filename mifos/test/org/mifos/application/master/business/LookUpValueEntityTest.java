package org.mifos.application.master.business;

import java.util.Set;

import junit.framework.TestCase;

import org.hibernate.classic.Session;
import org.mifos.framework.TestDatabase;

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
