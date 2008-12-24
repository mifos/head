package org.mifos.application.accounts.struts.action;

import java.sql.Date;

import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestNotesAction extends MifosMockStrutsTestCase {

	private SavingsBO savingsBO;

	private LoanBO loanBO;

	private UserContext userContext;

	private CustomerBO client;

	private CustomerBO group;

	private CustomerBO center;

	private MeetingBO meeting;

	private SavingsTestHelper helper = new SavingsTestHelper();

	private SavingsOfferingBO savingsOffering;

	private String flowKey;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		userContext = TestUtils.makeUser();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		ActivityContext ac = new ActivityContext((short) 0, userContext
				.getBranchId().shortValue(), userContext.getId().shortValue());
		request.getSession(false).setAttribute("ActivityContext", ac);
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);

		flowKey = createFlow(request, NotesAction.class);

		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
	}

	private void reloadMembers() {
		if (savingsBO != null) {
			savingsBO = (SavingsBO)HibernateUtil.getSessionTL().get(SavingsBO.class, savingsBO.getAccountId());
		}
		if (loanBO != null) {
			loanBO = (LoanBO)HibernateUtil.getSessionTL().get(LoanBO.class, loanBO.getAccountId());
		}
		if (group != null) {
			group = (GroupBO)HibernateUtil.getSessionTL().get(GroupBO.class, group.getCustomerId());
		}
		if (center != null) {
			center = (CenterBO)HibernateUtil.getSessionTL().get(CenterBO.class, center.getCustomerId());
		}
		if (client != null) {
			client = (CustomerBO)HibernateUtil.getSessionTL().get(CustomerBO.class, client.getCustomerId());
		}
		
	}
	
	@Override
	public void tearDown() throws Exception {		
		try {
			reloadMembers();
			TestObjectFactory.cleanUp(savingsBO);
			TestObjectFactory.cleanUp(loanBO);
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

	public void testLoad_Savings() throws Exception {
		savingsBO = getSavingsAccount("fsaf1", "ads1");
		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("accountId", savingsBO.getAccountId().toString());
		getRequest().getSession().setAttribute("security_param", "Savings");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward("load_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testPreview_Savings() throws Exception {
		savingsBO = getSavingsAccount("fsaf2", "ads2");
		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("comment", "Notes created");
		addRequestParameter("accountId", savingsBO.getAccountId().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward("preview_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testPrevious_Savings() throws Exception {
		savingsBO = getSavingsAccount("fsaf3", "ads3");
		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "previous");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward("previous_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testCancel_Savings() throws Exception {
		savingsBO = getSavingsAccount("fsaf4", "ads4");
		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "cancel");
		addRequestParameter("accountTypeId", savingsBO.getType()
				.getValue().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward("savings_details_page");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testCreate_Savings() throws Exception {
		savingsBO = getSavingsAccount("fsaf5", "ads5");

		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("accountId", savingsBO.getAccountId().toString());
		getRequest().getSession().setAttribute("security_param", "Savings");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();

		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("comment", "Notes created");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();

		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "create");
		addRequestParameter("comment", "Notes created");
		getRequest().getSession().setAttribute("security_param", "Savings");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward("savings_details_page");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testSearch_Savings() throws Exception {
		savingsBO = getSavingsAccount("fsaf6", "ads6");

		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("accountId", savingsBO.getAccountId().toString());
		getRequest().getSession().setAttribute("security_param", "Savings");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();

		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("comment", "Notes created");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();

		
		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "create");
		getRequest().getSession().setAttribute("security_param", "Savings");
		addRequestParameter("comment", "Notes created");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();

		
		HibernateUtil.closeSession();
		
		addRequestParameter("globalAccountNum", savingsBO.getGlobalAccountNum());
		setRequestPathInfo("/savingsAction.do");
		addRequestParameter("method", "get");
		actionPerform();
		
		
		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "search");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyForward("search_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertEquals("Size of the search result should be 2", 2, ((QueryResult)SessionUtils.getAttribute(Constants.SEARCH_RESULTS,request)).getSize());
		HibernateUtil.closeSession();
		savingsBO = TestObjectFactory.getObject(SavingsBO.class,savingsBO.getAccountId());
		getobjects();
	}
	private void getobjects(){
		
		client = TestObjectFactory.getObject(CustomerBO.class,client.getCustomerId());
		group = TestObjectFactory.getObject(CustomerBO.class,group.getCustomerId());
		center = TestObjectFactory.getObject(CustomerBO.class,center.getCustomerId());
	
	}
	public void testLoad_Loan() {
		loanBO = getLoanAccount();
		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("accountId", loanBO.getAccountId().toString());
		getRequest().getSession().setAttribute("security_param", "Loan");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward("load_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testPreview_Loan() {
		loanBO = getLoanAccount();
		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("comment", "Notes created");
		addRequestParameter("accountId", loanBO.getAccountId().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward("preview_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testPrevious_Loan() {
		loanBO = getLoanAccount();
		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "previous");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward("previous_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testCancel_Loan() {
		loanBO = getLoanAccount();
		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "cancel");
		addRequestParameter("accountTypeId", loanBO.getType()
				.getValue().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward("loan_detail_page");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testCreate_Loan() {
		loanBO = getLoanAccount();

		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("accountId", loanBO.getAccountId().toString());
		getRequest().getSession().setAttribute("security_param", "Loan");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();

		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("comment", "Notes created");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();

		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "create");
		addRequestParameter("comment", "Notes created");
		getRequest().getSession().setAttribute("security_param", "Loan");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyForward("loan_detail_page");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testSearch_Loan() throws Exception {
		loanBO = getLoanAccount();

		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("accountId", loanBO.getAccountId().toString());
		getRequest().getSession().setAttribute("security_param", "Loan");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();

		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("comment", "Notes created");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();

		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "create");
		getRequest().getSession().setAttribute("security_param", "Loan");
		addRequestParameter("comment", "Notes created");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();

		HibernateUtil.closeSession();
		addRequestParameter("globalAccountNum", loanBO.getGlobalAccountNum());
		setRequestPathInfo("/loanAccountAction.do");
		addRequestParameter("method", "get");
		actionPerform();
		

		
		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "search");
		addRequestParameter(Constants.CURRENTFLOWKEY,(String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyForward("search_success");
		verifyNoActionErrors();
		verifyNoActionMessages();

		assertEquals("Size of the search result should be 1", 1, ((QueryResult)SessionUtils.getAttribute(Constants.SEARCH_RESULTS,request)).getSize());
		HibernateUtil.closeSession();
		loanBO = TestObjectFactory.getObject(LoanBO.class,loanBO.getAccountId());

		getobjects();
		
	}

	private void createInitialObjects() {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("Client",
				CustomerStatus.CLIENT_ACTIVE, group);
	}

	private SavingsBO getSavingsAccount(String offeringName, String shortName)
			throws Exception {
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering(offeringName, shortName);
		return TestObjectFactory.createSavingsAccount("000100000000017",
				client, AccountStates.SAVINGS_ACC_APPROVED, new Date(System
						.currentTimeMillis()), savingsOffering);
	}

	private LoanBO getLoanAccount() {
		createInitialObjects();
		Date startDate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				startDate, meeting);
		return TestObjectFactory.createLoanAccount("42423142341", client, 
			AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, 
			startDate, loanOffering);
	}
}
