package org.mifos.application.accounts.loan.business;

import java.util.Date;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.financial.util.helpers.FinancialInitializer;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanSummaryEntity;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.hibernate.HibernateStartUp;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.authorization.HierarchyManager;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

import junit.framework.TestCase;

public class TestLoanSummaryEntity extends TestCase {

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
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		return TestObjectFactory.createLoanAccount("42423142341", group, Short
				.valueOf("5"), new Date(System.currentTimeMillis()),
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
