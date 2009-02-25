package org.mifos.application.accounts.loan.struts.uihelpers;

import java.util.Date;

import static junitx.framework.StringAssert.assertContains;
import org.mifos.application.accounts.loan.business.LoanActivityEntity;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanScheduleEntity;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestLoanRepayTag extends MifosTestCase {
	CustomerBO center;
	CustomerBO group;
	CustomerBO client;
	LoanBO accountBO;
	UserContext userContext;
	
	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(accountBO);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}
	
	@Override
	protected void setUp() throws Exception {
		userContext = TestObjectFactory.getContext();
		super.setUp();
	}
	
	public void testCreateInstallmentRow(){
		
		Date startDate = new Date(System.currentTimeMillis());
		accountBO = getLoanAccount(
			AccountState.LOAN_APPROVED, startDate, 1);
		HibernateUtil.flushAndCloseSession();
		accountBO =TestObjectFactory.getObject(LoanBO.class,accountBO.getAccountId());
		group = TestObjectFactory.getCustomer(group.getCustomerId());
		center = TestObjectFactory.getCustomer(center.getCustomerId());
		LoanRepaymentTag loanRepaymentTag = new LoanRepaymentTag();
		loanRepaymentTag.locale = userContext.getPreferredLocale();
		assertContains("100.0",loanRepaymentTag.createInstallmentRow( (LoanScheduleEntity)accountBO.getAccountActionDate(Short.valueOf("1")),false ).toString());
	}
	
	public void testcreateRunningBalanceRow(){
		Date startDate = new Date(System.currentTimeMillis());
		accountBO = getLoanAccount(
			AccountState.LOAN_APPROVED, startDate, 1);
		HibernateUtil.flushAndCloseSession();
		accountBO =TestObjectFactory.getObject(LoanBO.class,accountBO.getAccountId());
		group = TestObjectFactory.getCustomer(group.getCustomerId());
		center = TestObjectFactory.getCustomer(center.getCustomerId());
		LoanRepaymentTag loanRepaymentTag = new LoanRepaymentTag();
		loanRepaymentTag.locale = userContext.getPreferredLocale();
		assertContains("90.0",loanRepaymentTag.createRunningBalanceRow((LoanScheduleEntity)accountBO.getAccountActionDate(Short.valueOf("1")),new Money("50"),new Money("20"),new Money("20")).toString());
	}

	private LoanBO getLoanAccount(AccountState state, Date startDate,
			int disbursalType) {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("Client",
				CustomerStatus.CLIENT_ACTIVE, group);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				startDate, meeting);
		accountBO = TestObjectFactory.createLoanAccountWithDisbursement(
				"99999999999", group, state, startDate, loanOffering,
				disbursalType);
		LoanActivityEntity loanActivity = new LoanActivityEntity(accountBO,
				TestObjectFactory.getPersonnel(userContext.getId()), "testing",
				new Money("100"), new Money("100"), new Money("100"),
				new Money("100"), new Money("100"), new Money("100"),
				new Money("100"), new Money("100"), startDate);
		accountBO.addLoanActivity(loanActivity);
		TestObjectFactory.updateObject(accountBO);
		return accountBO;
	}	
}
