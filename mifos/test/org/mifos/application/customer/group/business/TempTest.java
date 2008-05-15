package org.mifos.application.customer.group.business;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class TempTest extends MifosTestCase {
	private Transaction transaction;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Session sessionTL = HibernateUtil.getSessionTL();
		transaction = sessionTL.beginTransaction();
	}

	public void testCheckForOlderVersion() throws Exception {
		LoanOfferingBO loanOffering = new LoanPrdBusinessService().getLoanOffering((short)1);
		assertEquals((Integer)0, loanOffering.checkLoanAmountType());
	}
	
	@Override
	protected void tearDown() throws Exception {
		transaction.rollback();
	}
}
