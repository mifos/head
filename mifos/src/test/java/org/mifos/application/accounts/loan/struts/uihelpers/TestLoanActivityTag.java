package org.mifos.application.accounts.loan.struts.uihelpers;

import java.util.Date;
import java.util.List;

import static junitx.framework.StringAssert.assertContains;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanActivityEntity;
import org.mifos.application.accounts.loan.business.LoanActivityView;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.service.LoanBusinessService;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestLoanActivityTag extends MifosTestCase {
	
	public TestLoanActivityTag() throws SystemException, ApplicationException {
        super();
    }
    CustomerBO center;
	CustomerBO group;
	CustomerBO client;
	AccountBO accountBO;
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

	public void testBuildLeftHeaderRows()throws Exception{
		Date startDate = new Date(System.currentTimeMillis());
		accountBO = getLoanAccount(
			AccountState.LOAN_APPROVED, startDate, 1);
		List<LoanActivityView>  activityViews=	new LoanBusinessService().getAllActivityView(accountBO.getGlobalAccountNum(),
				userContext.getLocaleId());
		assertContains("100",new LoanActivityTag().buildLeftHeaderRows(activityViews.get(0)).toString());
		
		
	}
	public void testBuildRightHeaderRows()throws Exception{
		Date startDate = new Date(System.currentTimeMillis());
		accountBO = getLoanAccount(
			AccountState.LOAN_APPROVED, startDate, 1);
		List<LoanActivityView>  activityViews=	new LoanBusinessService().getAllActivityView(accountBO.getGlobalAccountNum(),
				userContext.getLocaleId());
		assertContains("100",new LoanActivityTag().buildRightHeaderRows(activityViews.get(0)).toString());
		
		
	}
	private AccountBO getLoanAccount(AccountState state, Date startDate,
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
		((LoanBO) accountBO).addLoanActivity(loanActivity);
		TestObjectFactory.updateObject(accountBO);
		return accountBO;
	}
}
