package org.mifos.application.master.business;

import java.util.Set;

import junit.framework.TestCase;

import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.mifos.framework.util.helpers.DatabaseSetup;

public class LookUpValueEntityTest extends TestCase {
	
	public void testBasics() throws Exception {
		SessionFactory factory = DatabaseSetup.mayflySessionFactory();

		/* Hmm, we don't seem to have the ability to write a LookUpValueEntity
		 * (Hibernate mapping written for auto-increment column but the
		 * column isn't really auto-increment).
		 * 
		 * The following code doesn't clean up the shared database, so it would
		 * just be a first step anyway.
		 */
//		Session writer = factory.openSession();
//		LookUpValueEntity entity = new LookUpValueEntity();
//		entity.setLookUpId(5);
//		entity.setLookUpName("my entity");
//		writer.save(entity);
//		writer.close();

		Session reader = factory.openSession();
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

}
