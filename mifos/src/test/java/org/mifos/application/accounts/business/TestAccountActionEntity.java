package org.mifos.application.accounts.business;

import org.hibernate.Session;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

/**
 * Also see {@link AccountActionEntityTest}.
 */
public class TestAccountActionEntity extends MifosIntegrationTest {	
	
	public TestAccountActionEntity() throws SystemException, ApplicationException {
        super();
    }

    private Session session;
	private AccountActionEntity accountActionEntity;

	@Override
	protected void setUp() throws Exception {
		session = StaticHibernateUtil.getSessionTL();
	}

	@Override
	protected void tearDown() throws Exception {
		StaticHibernateUtil.closeSession();
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
