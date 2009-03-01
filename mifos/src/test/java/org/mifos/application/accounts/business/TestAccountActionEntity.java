package org.mifos.application.accounts.business;

import org.hibernate.Session;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;

/**
 * Also see {@link AccountActionEntityTest}.
 */
public class TestAccountActionEntity extends MifosTestCase {	
	
	public TestAccountActionEntity() throws SystemException, ApplicationException {
        super();
    }

    private Session session;
	private AccountActionEntity accountActionEntity;

	@Override
	protected void setUp() throws Exception {
		session = HibernateUtil.getSessionTL();
	}

	@Override
	protected void tearDown() throws Exception {
		HibernateUtil.closeSession();
		session=null;
	}

	public void testGetAccountAction(){
		Short id = 1;
		accountActionEntity = getAccountActionEntityObject(id);
		assertEquals("Loan Repayment", accountActionEntity.getName());
	}

	private AccountActionEntity getAccountActionEntityObject(Short id) {
		return (AccountActionEntity)session.get(AccountActionEntity.class,id);
	}
	
}
