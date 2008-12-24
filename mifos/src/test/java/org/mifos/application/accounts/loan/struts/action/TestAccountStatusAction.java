package org.mifos.application.accounts.loan.struts.action;

import java.util.Date;
import java.util.List;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestAccountStatusAction extends MifosMockStrutsTestCase {

	private UserContext userContext;

	protected AccountBO account;

	protected CenterBO center;

	protected GroupBO group;

	private ClientBO client;

	private MeetingBO meeting;

	private String flowKey;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		userContext = TestObjectFactory.getContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		request.getSession(false).setAttribute("ActivityContext",
				TestObjectFactory.getActivityContext());
		flowKey = createFlow(request, AccountStatusAction.class);
	}

	@Override
	protected void tearDown() throws Exception {
		try {
			TestObjectFactory.cleanUp(account);
			TestObjectFactory.cleanUp(client);
			TestObjectFactory.cleanUp(group);
			TestObjectFactory.cleanUp(center);
		} catch (Exception e) {
			// TODO Whoops, cleanup didnt work, reset db
			TestDatabase.resetMySQLDatabase();
		}
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testLoad() {
		setRequestPathInfo("/ChangeAccountStatus.do");
		addRequestParameter("method", Methods.load.toString());
		actionPerform();
		verifyForward(ActionForwards.changeAccountStatus_success.toString());
	}

	public void testSearchResults() throws Exception {
		createCustomers();
		account = getLoanAccount(group);
		setRequestPathInfo("/ChangeAccountStatus.do");
		addRequestParameter("method", Methods.searchResults.toString());
		addRequestParameter("officeId", account.getOffice().getOfficeId()
				.toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter("personnelId", account.getPersonnel()
				.getPersonnelId().toString());
		addRequestParameter("type", "loan");
		addRequestParameter("currentStatus", account.getAccountState().getId()
				.toString());
		performNoErrors();
		verifyForward(ActionForwards.changeAccountStatusSearch_success
				.toString());
		assertNotNull(SessionUtils.getAttribute(LoanConstants.SEARCH_RESULTS, request));
		assertEquals(1,((List<PersonnelView>)SessionUtils.getAttribute(LoanConstants.SEARCH_RESULTS, request)).size());
	}

	public void testSearchResults_noresults_forvalidate() throws Exception {
		createCustomers();
		account = getLoanAccount(group);
		setRequestPathInfo("/ChangeAccountStatus.do");
		addRequestParameter("method", Methods.searchResults.toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter("personnelId", account.getPersonnel()
				.getPersonnelId().toString());
		addRequestParameter("currentStatus",
				AccountState.LOAN_PARTIAL_APPLICATION.getValue().toString());
		actionPerform();
		verifyInputForward();
	}
	
	public void testSearchResults_noresults() throws Exception {
		createCustomers();
		account = getLoanAccount(group);
		setRequestPathInfo("/ChangeAccountStatus.do");
		addRequestParameter("method", Methods.searchResults.toString());
		addRequestParameter("officeId", account.getOffice().getOfficeId()
				.toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter("personnelId", account.getPersonnel()
				.getPersonnelId().toString());
		addRequestParameter("type", "loan");
		addRequestParameter("currentStatus",
				AccountState.LOAN_PARTIAL_APPLICATION.getValue().toString());
		actionPerform();
		verifyForward(ActionForwards.noresultfound.toString());
		assertNull(SessionUtils.getAttribute(LoanConstants.SEARCH_RESULTS, request));
	}

	public void testSearchResults_exception() {
		createCustomers();
		account = getLoanAccount(group);
		setRequestPathInfo("/ChangeAccountStatus.do");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter("method", Methods.searchResults.toString());
		actionPerform();
		verifyInputForward();
	}
	
	public void testGetLoanOfficers() throws Exception{
		createCustomers();
		account = getLoanAccount(group);
		setRequestPathInfo("/ChangeAccountStatus.do");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter("method", "getLoanOfficers");
		addRequestParameter("officeId",account.getOffice().getOfficeId().toString());
		actionPerform();
		verifyForward(ActionForwards.changeAccountStatus_success.toString());
		assertNotNull(SessionUtils.getAttribute(LoanConstants.LOAN_OFFICERS, request));
		assertEquals(1,((List<PersonnelView>)SessionUtils.getAttribute(LoanConstants.LOAN_OFFICERS, request)).size());
	}
	
	public void testUpdate() throws Exception{
		createCustomers();
		account = getLoanAccount(group);
		setRequestPathInfo("/ChangeAccountStatus.do");
		addRequestParameter("method", "update");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter("accountRecords[0]", account.getAccountId().toString());
		addRequestParameter("newStatus", "3");	
		addRequestParameter("comments", "comments");
		actionPerform();
		verifyForward(ActionForwards.changeAccountStatusConfirmation_success.toString());
	}
	
	public void testUpdate_validation() throws Exception{
		createCustomers();
		account = getLoanAccount(group);
		setRequestPathInfo("/ChangeAccountStatus.do");
		addRequestParameter("method", "update");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyInputForward();
	}
	
	private void createParentCustomer() {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center",
				meeting);

	}

	private void createCustomers() {
		createParentCustomer();
		group = TestObjectFactory.createGroupUnderCenter("group", CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("Client",
				CustomerStatus.CLIENT_ACTIVE, group);
		HibernateUtil.closeSession();
	}

	private LoanBO getLoanAccount(CustomerBO customerBO) {
		Date startDate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				startDate, meeting);
		return TestObjectFactory.createLoanAccount("42423142341", customerBO,
				AccountState.LOAN_PENDING_APPROVAL, startDate,
				loanOffering);

	}

}
