package org.mifos.framework.hibernate;

import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.HibernateStartUpException;
import org.mifos.framework.hibernate.factory.HibernateSessionFactory;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class HibernateTest extends MifosTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testInitializeHibernateStartUpForInvalidPath() {
		try {
			HibernateStartUp.initialize("");
			fail();
		} catch (HibernateStartUpException he) {
			assertTrue(true);
		}
	}

	public void testHibernateSessionFactoryForNullConfig() {
		try {
			HibernateSessionFactory.setConfiguration(null);
			fail();
		} catch (HibernateStartUpException he) {
			assertTrue(true);
		}
	}

	public void testHibernateUtilIsSessionOpen() {
		HibernateUtil.getSessionTL();
		assertTrue(HibernateUtil.isSessionOpen());
		HibernateUtil.closeSession();
	}

	public void testHibernateUtilIsSessionOpenForClosedSession() {
		HibernateUtil.closeSession();
		assertFalse(HibernateUtil.isSessionOpen());
	}


}
