package org.mifos.application.accounts.loan.struts.uihelpers;

import static junitx.framework.StringAssert.assertContains;

import java.util.Date;

import org.mifos.application.accounts.loan.business.LoanActivityEntity;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanScheduleEntity;
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
		accountBO = getLoanAccount(Short.valueOf("3"), startDate, 1);
		HibernateUtil.closeandFlushSession();
		accountBO =(LoanBO) TestObjectFactory.getObject(LoanBO.class,accountBO.getAccountId());
		group = (CustomerBO)TestObjectFactory.getObject(CustomerBO.class,group.getCustomerId());
		center = (CustomerBO)TestObjectFactory.getObject(CustomerBO.class,center.getCustomerId());
		LoanRepaymentTag loanRepaymentTag = new LoanRepaymentTag();
		loanRepaymentTag.locale = userContext.getPereferedLocale();
		assertContains("100.0",loanRepaymentTag.createInstallmentRow( (LoanScheduleEntity)accountBO.getAccountActionDate(Short.valueOf("1")),false ));
	}
	
	public void testcreateRunningBalanceRow(){
		Date startDate = new Date(System.currentTimeMillis());
		accountBO = getLoanAccount(Short.valueOf("3"), startDate, 1);
		HibernateUtil.closeandFlushSession();
		accountBO =(LoanBO) TestObjectFactory.getObject(LoanBO.class,accountBO.getAccountId());
		group = (CustomerBO)TestObjectFactory.getObject(CustomerBO.class,group.getCustomerId());
		center = (CustomerBO)TestObjectFactory.getObject(CustomerBO.class,center.getCustomerId());
		LoanRepaymentTag loanRepaymentTag = new LoanRepaymentTag();
		loanRepaymentTag.locale = userContext.getPereferedLocale();
		assertContains("90.0",loanRepaymentTag.createRunningBalanceRow((LoanScheduleEntity)accountBO.getAccountActionDate(Short.valueOf("1")),new Money("50"),new Money("20"),new Money("20")));
	}

	private LoanBO getLoanAccount(Short accountSate, Date startDate,
			int disbursalType) {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		client = TestObjectFactory.createClient("Client",
				CustomerStatus.CLIENT_ACTIVE, "1.4.1.1", group, new Date(
						System.currentTimeMillis()));
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"), startDate, Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		accountBO = TestObjectFactory.createLoanAccountWithDisbursement(
				"99999999999", group, accountSate, startDate, loanOffering,
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
