package org.mifos.application.accounts.business;

import org.hibernate.Session;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestAccountActionEntity extends MifosTestCase {	
	
	private Session session;
	private AccountActionEntity accountActionEntity;
	private CustomerBO center=null;
	private CustomerBO group=null;
	private AccountPersistence accountPersistence;

	@Override
	protected void setUp() throws Exception {
		session = HibernateUtil.getSessionTL();
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		session=null;
	}

	public void testGetAccountAction(){
		accountActionEntity=getAccountActionEntityObject(Short.valueOf("1"));
		assertEquals("Loan Repayment",accountActionEntity.getName(Short.valueOf("1")));
	}

	private AccountActionEntity getAccountActionEntityObject(Short id) {
		return (AccountActionEntity)session.get(AccountActionEntity.class,id);
	}
	
}
