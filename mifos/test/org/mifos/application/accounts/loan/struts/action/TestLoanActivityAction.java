package org.mifos.application.accounts.loan.struts.action;

import java.util.Date;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestLoanActivityAction extends MifosMockStrutsTestCase {

	private UserContext userContext;

	protected AccountBO accountBO = null;

	protected CustomerBO center = null;

	protected CustomerBO group = null;
	
	private String flowKey;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		userContext = TestObjectFactory.getContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
		flowKey = createFlow(request, LoanActivityAction.class);
	}

	@Override
	protected void tearDown() throws Exception {
		try {
			TestObjectFactory.cleanUp(accountBO);
			TestObjectFactory.cleanUp(group);
			TestObjectFactory.cleanUp(center);
		} catch (Exception e) {
			// TODO Whoops, cleanup didnt work, reset db
			TestDatabase.resetMySQLDatabase();
		}

		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testGetAllActivity() {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		Date startDate = new Date(System.currentTimeMillis());
		accountBO = getLoanAccount(
			AccountState.LOAN_APPROVED, startDate, 1);
		LoanBO loan = (LoanBO) accountBO;
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "getAllActivity");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("globalAccountNum", loan.getGlobalAccountNum());
		actionPerform();
		verifyForward("getAllActivity_success");
	}

	private AccountBO getLoanAccount(AccountState state, Date startDate,
			int disbursalType) {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				startDate, meeting);
		return TestObjectFactory.createLoanAccountWithDisbursement(
				"99999999999", group, state, startDate, loanOffering,
				disbursalType);

	}
}
