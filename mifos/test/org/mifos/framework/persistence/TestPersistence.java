package org.mifos.framework.persistence;

import org.mifos.application.accounts.loan.persistance.LoanPersistance;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class TestPersistence extends MifosTestCase {

	public void testConnection() {
		LoanPersistance loanPersistance = new LoanPersistance();
		assertNotNull(loanPersistance.getConnection());
		HibernateUtil.closeSession();
	}

	public void testOpenSession() throws Exception {
		LoanPersistance loanPersistance = new LoanPersistance();
		loanPersistance.openSession();
		assertTrue(HibernateUtil.isSessionOpen());
		HibernateUtil.closeSession();
	}

	public void testCloseSession() throws Exception {
		LoanPersistance loanPersistance = new LoanPersistance();
		loanPersistance.openSession();
		loanPersistance.closeSession();
		assertFalse(HibernateUtil.isSessionOpen());
		HibernateUtil.closeSession();
	}
}
