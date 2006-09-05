package org.mifos.application.accounts.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mifos.framework.MifosTestCase;

import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanSummaryEntity;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.bulkentry.exceptions.BulkEntryAccountUpdateException;
import org.mifos.application.bulkentry.persistance.BulkEntryPersistance;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestLoanTrxnDetailEntity extends MifosTestCase {
	
private BulkEntryPersistance bulkEntryPersistance;


	private AccountPersistence accountPersistence;

	private CustomerBO center;
	private CustomerBO group;
	private CustomerBO client;
	private AccountBO account;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		bulkEntryPersistance = new BulkEntryPersistance();
		accountPersistence = new AccountPersistence();
	}
	
	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(account);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		super.tearDown();
		HibernateUtil.closeSession();
	}
	
	public void testSuccessSetRunningBalance() throws Exception{
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active", Short.valueOf("13"), "1.1", meeting,new Date(System.currentTimeMillis()));
		group=TestObjectFactory.createGroup("Group",Short.valueOf("9"),"1.1.1",center,new Date(System.currentTimeMillis()));
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering("Loan",Short.valueOf("2"),
				new Date(System.currentTimeMillis()),Short.valueOf("1"),300.0,1.2,Short.valueOf("3"),Short.valueOf("1"),
				Short.valueOf("1"),Short.valueOf("1"),Short.valueOf("1"),Short.valueOf("1"),meeting);
		account=TestObjectFactory.createLoanAccount("42423142341",group,Short.valueOf("5"),new Date(System.currentTimeMillis()),loanOffering);
		HibernateUtil.closeSession();
		account = (LoanBO) accountPersistence.getAccount(account.getAccountId());
		assertEquals(((LoanBO)account).getLoanOffering().getPrdOfferingName(), "Loan");
		
		List<AccountActionDateEntity> accountActionsToBeUpdated = new ArrayList<AccountActionDateEntity>();
		accountActionsToBeUpdated.add(account.getAccountActionDates().iterator().next());
		PaymentData paymentData = TestObjectFactory.getLoanAccountPaymentData(
				accountActionsToBeUpdated,new Money(TestObjectFactory.getMFICurrency(),"700.0"),null,account.getPersonnel(),
				"423423",Short.valueOf("1"),new Date(System
						.currentTimeMillis()),new Date(System
								.currentTimeMillis()));
		account.applyPayment(paymentData);
		HibernateUtil.commitTransaction();
		LoanTrxnDetailEntity loanTrxnDetailEntity=null;
		for(AccountPaymentEntity accountPaymentEntity :account.getAccountPayments()){
			for(AccountTrxnEntity accountTrxnEntity:accountPaymentEntity.getAccountTrxns()){
				loanTrxnDetailEntity=(LoanTrxnDetailEntity)accountTrxnEntity;
				break;
			}
		}
		LoanSummaryEntity loanSummaryEntity=((LoanBO)account).getLoanSummary();
		assertEquals(loanSummaryEntity.getOriginalPrincipal().subtract(loanSummaryEntity.getPrincipalPaid()),((LoanBO)account).getLoanActivityDetails().iterator().next().getPrincipalOutstanding());
	
	}
	
	

}
