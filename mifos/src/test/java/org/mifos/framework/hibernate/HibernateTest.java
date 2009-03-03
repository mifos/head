package org.mifos.framework.hibernate;

import java.io.FileNotFoundException;

import junitx.framework.ObjectAssert;

import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateStartUpException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.factory.HibernateSessionFactory;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class HibernateTest extends MifosIntegrationTest {

	public HibernateTest() throws SystemException, ApplicationException {
        super();
    }

    public void testInitializeHibernateStartUpForInvalidPath() {
		try {
			HibernateStartUp.initialize("");
			fail();
		} catch (HibernateStartUpException outer) {
			// What do we want the user to see?
//			assertEquals(HibernateConstants.STARTUPEXCEPTION, outer.getKey());
			assertEquals("errors.hibernatepropnotfound",
					outer.getKey());
			ObjectAssert.assertInstanceOf(
				FileNotFoundException.class, 
				outer.getCause());
		}
	}

	public void testHibernateSessionFactoryForNullConfig() {
		try {
			HibernateSessionFactory.setConfiguration(null);
			fail();
		} catch (HibernateStartUpException outer) {
			assertEquals("exception.framework.SystemException",
					outer.getKey());
			ObjectAssert.assertInstanceOf(
				NullPointerException.class, 
				outer.getCause());
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
