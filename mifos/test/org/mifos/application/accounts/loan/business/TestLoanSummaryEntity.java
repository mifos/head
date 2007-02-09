package org.mifos.application.accounts.loan.business;

import java.util.Date;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestLoanSummaryEntity extends MifosTestCase {

	protected AccountBO accountBO=null;
	protected CustomerBO center=null;
	protected CustomerBO group=null;
	
	public void testDecreaseBy(){
		accountBO=getLoanAccount();
		LoanSummaryEntity loanSummaryEntity=((LoanBO)accountBO).getLoanSummary();
		Money principal=TestObjectFactory.getMoneyForMFICurrency(100);
		Money interest=TestObjectFactory.getMoneyForMFICurrency(10);
		Money penalty=TestObjectFactory.getMoneyForMFICurrency(20);
		Money fees=TestObjectFactory.getMoneyForMFICurrency(30);
		Money originalPrincipal=loanSummaryEntity.getOriginalPrincipal();
		Money originalInterest=loanSummaryEntity.getOriginalInterest();
		Money originalFees=loanSummaryEntity.getOriginalFees();
		Money originalPenalty=loanSummaryEntity.getOriginalPenalty();
		loanSummaryEntity.decreaseBy(principal,interest,penalty,fees);
		assertEquals(loanSummaryEntity.getOriginalPrincipal().add(principal),originalPrincipal);
		assertEquals(loanSummaryEntity.getOriginalInterest().add(interest),originalInterest);
		assertEquals(loanSummaryEntity.getOriginalFees().add(fees),originalFees);
		assertEquals(loanSummaryEntity.getOriginalPenalty().add(penalty),originalPenalty);
	}
	
	public void testUpdatePaymentDetails(){
		accountBO=getLoanAccount();
		LoanSummaryEntity loanSummaryEntity=((LoanBO)accountBO).getLoanSummary();
		Money principal=TestObjectFactory.getMoneyForMFICurrency(100);
		Money interest=TestObjectFactory.getMoneyForMFICurrency(10);
		Money penalty=TestObjectFactory.getMoneyForMFICurrency(20);
		Money fees=TestObjectFactory.getMoneyForMFICurrency(30);
		loanSummaryEntity.updatePaymentDetails(principal,interest,penalty,fees);
		assertEquals(loanSummaryEntity.getPrincipalPaid(),principal);
		assertEquals(loanSummaryEntity.getInterestPaid(),interest);
		assertEquals(loanSummaryEntity.getFeesPaid(),fees);
		assertEquals(loanSummaryEntity.getPenaltyPaid(),penalty);
	}
	
	

	private AccountBO getLoanAccount() {
		Date startDate = new Date(System.currentTimeMillis());
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				startDate, meeting);
		return TestObjectFactory.createLoanAccount("42423142341", group, Short
				.valueOf("5"), startDate,
				loanOffering);
	}
	
	
	@Override
	protected void tearDown() throws Exception {
		accountBO=(AccountBO)HibernateUtil.getSessionTL().get(AccountBO.class,accountBO.getAccountId());
		group=(CustomerBO)HibernateUtil.getSessionTL().get(CustomerBO.class,group.getCustomerId());
		center=(CustomerBO)HibernateUtil.getSessionTL().get(CustomerBO.class,center.getCustomerId());
		TestObjectFactory.cleanUp(accountBO);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);

		HibernateUtil.closeSession();
		super.tearDown();
	}	

}
